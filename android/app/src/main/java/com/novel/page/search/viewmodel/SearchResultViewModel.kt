package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.search.repository.SearchParams
import com.novel.page.search.repository.SearchRepository
import com.novel.page.search.usecase.GetCategoryFiltersUseCase
import com.novel.utils.TimberLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val getCategoryFiltersUseCase: GetCategoryFiltersUseCase,
) : BaseMviViewModel<SearchResultIntent, SearchResultState, SearchResultEffect>() {

    companion object {
        private const val TAG = "SearchResultViewModel"
        private const val SEARCH_DEBOUNCE_DELAY_MS = 500L
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 1000L
    }

    private val reducer = SearchResultReducer()
    val adapter = SearchResultStateAdapter(state)
    private val retryPolicyCoordinator = SearchRetryPolicyCoordinator()
    private val performanceTraceCoordinator = SearchPerformanceTraceCoordinator()

    private var currentPage = 1
    private var isLoadingMore = false

    @Stable
    private val searchQueryChannel = Channel<SearchParams>(Channel.UNLIMITED)

    @Stable
    private var searchJob: Job? = null

    private var currentSearchParams: SearchParams? = null
    private var retryAttempts = 0
    private val categoryFilterLoadCoordinator = SearchCategoryFilterLoadCoordinator()
    private var isCategoryFiltersLoading = false

    init {
        TimberLogger.d(TAG, "SearchResultViewModel MVI重构版初始化")
        setupSearchDebounce()
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            searchQueryChannel.receiveAsFlow()
                .debounce(SEARCH_DEBOUNCE_DELAY_MS)
                .collect { params ->
                    executeSearch(params)
                }
        }
    }

    private fun executeSearch(params: SearchParams) {
        TimberLogger.d(TAG, "执行搜索: ${params.query}, 页码: ${params.page}")
        currentSearchParams = params
        retryAttempts = 0
        performSearchWithRetry(
            params = params,
            trace = startSearchTrace(params),
        )
    }

    private fun performSearchWithRetry(
        params: SearchParams,
        trace: SearchPerformanceTrace,
    ) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val response = searchRepository.searchBooksWithCache(params)

                if (response != null) {
                    TimberLogger.d(TAG, "搜索成功: 返回${response.list.size}条结果")

                    val books = response.list
                    val hasMore = (response.pages ?: 0) > params.page
                    val totalResults = response.total?.toInt() ?: 0

                    val newState = reducer.handleSearchSuccess(
                        currentState = getCurrentState(),
                        books = books,
                        totalResults = totalResults,
                        hasMore = hasMore,
                        isLoadMore = params.isLoadMore,
                    )
                    updateState(newState)

                    if (params.isLoadMore) {
                        isLoadingMore = false
                    }

                    retryAttempts = 0
                    finishSearchTrace(
                        trace = trace,
                        status = "success",
                        metadata = mapOf(
                            "trigger" to params.triggerSource.name,
                            "resultCount" to books.size.toString(),
                            "hasMore" to hasMore.toString(),
                            "page" to params.page.toString(),
                        ),
                    )
                } else {
                    handleSearchFailure(
                        exception = Exception("搜索返回为空"),
                        params = params,
                        trace = trace,
                    )
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "搜索异常", e)
                handleSearchFailure(
                    exception = e,
                    params = params,
                    trace = trace,
                )
            }
        }
    }

    private fun handleSearchFailure(
        exception: Throwable,
        params: SearchParams,
        trace: SearchPerformanceTrace,
    ) {
        retryAttempts++

        if (retryPolicyCoordinator.shouldRetry(
                params = params,
                retryAttempts = retryAttempts,
                maxRetryAttempts = MAX_RETRY_ATTEMPTS,
            )
        ) {
            TimberLogger.d(TAG, "搜索失败，准备重试($retryAttempts/$MAX_RETRY_ATTEMPTS)")

            viewModelScope.launch {
                delay(
                    retryPolicyCoordinator.retryDelayMs(
                        retryAttempts = retryAttempts,
                        baseDelayMs = RETRY_DELAY_MS,
                    ),
                )
                performSearchWithRetry(
                    params = params,
                    trace = trace,
                )
            }
        } else {
            TimberLogger.e(TAG, "搜索失败，超出重试次数")

            val newState = reducer.handleSearchError(
                currentState = getCurrentState(),
                errorMessage = "搜索失败: ${exception.message}",
                isLoadMore = params.isLoadMore,
            )
            updateState(newState)

            if (params.isLoadMore) {
                currentPage--
                isLoadingMore = false
            }

            finishSearchTrace(
                trace = trace,
                status = "failure",
                metadata = mapOf(
                    "trigger" to params.triggerSource.name,
                    "page" to params.page.toString(),
                    "retryAttempts" to retryAttempts.toString(),
                    "error" to (exception.message ?: "unknown"),
                ),
            )
            sendEffect(SearchResultEffect.ShowToast("搜索失败: ${exception.message}"))
        }
    }

    override fun createInitialState(): SearchResultState {
        return SearchResultState()
    }

    override fun getReducer(): MviReducer<SearchResultIntent, SearchResultState> {
        return reducer
    }

    override fun onIntentProcessed(intent: SearchResultIntent, newState: SearchResultState) {
        val result = reducer.reduceWithEffect(getCurrentState(), intent)
        result.effect?.let { effect ->
            sendEffect(effect)
        }

        when (intent) {
            is SearchResultIntent.PerformSearch -> handlePerformSearch(intent.query)
            is SearchResultIntent.SelectCategory -> handleCategorySelection()
            is SearchResultIntent.OpenFilterSheet -> handleOpenFilterSheet()
            is SearchResultIntent.ApplyFilters -> handleApplyFilters()
            is SearchResultIntent.LoadNextPage -> handleLoadNextPage()
            else -> Unit
        }
    }

    private fun handlePerformSearch(query: String) {
        TimberLogger.d(TAG, "准备搜索: $query")

        if (query.isBlank()) {
            sendEffect(SearchResultEffect.ShowToast("请输入搜索关键词"))
            return
        }

        ensureCategoryFiltersLoaded(SearchCategoryFilterLoadTrigger.SEARCH_STARTED)

        currentPage = 1
        isLoadingMore = false

        val currentState = getCurrentState()
        val params = SearchParams(
            query = query,
            page = currentPage,
            categoryId = currentState.selectedCategoryId,
            filters = currentState.filters,
            isLoadMore = false,
            triggerSource = SearchTriggerSource.INITIAL_ENTRY,
        )

        searchQueryChannel.trySend(params)
    }

    private fun handleCategorySelection() {
        val currentState = getCurrentState()
        if (currentState.query.isNotBlank()) {
            currentPage = 1
            isLoadingMore = false

            val params = SearchParams(
                query = currentState.query,
                page = currentPage,
                categoryId = currentState.selectedCategoryId,
                filters = currentState.filters,
                isLoadMore = false,
                triggerSource = SearchTriggerSource.CATEGORY_SWITCH,
            )

            executeSearch(params)
        }
    }

    private fun handleOpenFilterSheet() {
        ensureCategoryFiltersLoaded(SearchCategoryFilterLoadTrigger.FILTER_SHEET_OPENED)
    }

    private fun handleApplyFilters() {
        val currentState = getCurrentState()
        if (currentState.query.isNotBlank()) {
            currentPage = 1
            isLoadingMore = false

            val params = SearchParams(
                query = currentState.query,
                page = currentPage,
                categoryId = currentState.selectedCategoryId,
                filters = currentState.filters,
                isLoadMore = false,
                triggerSource = SearchTriggerSource.FILTER_APPLY,
            )

            executeSearch(params)
        }
    }

    private fun handleLoadNextPage() {
        if (isLoadingMore) {
            TimberLogger.d(TAG, "正在加载中，跳过重复请求")
            return
        }

        val currentState = getCurrentState()
        if (!currentState.hasMore) {
            TimberLogger.d(TAG, "没有更多数据，跳过加载")
            return
        }

        if (currentState.query.isBlank()) {
            TimberLogger.d(TAG, "查询为空，跳过分页加载")
            return
        }

        isLoadingMore = true
        currentPage++

        val params = SearchParams(
            query = currentState.query,
            page = currentPage,
            categoryId = currentState.selectedCategoryId,
            filters = currentState.filters,
            isLoadMore = true,
            triggerSource = SearchTriggerSource.LOAD_MORE,
        )

        executeSearch(params)
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchQueryChannel.close()
        TimberLogger.d(TAG, "SearchResultViewModel资源清理完成")
    }

    private fun ensureCategoryFiltersLoaded(trigger: SearchCategoryFilterLoadTrigger) {
        if (categoryFilterLoadCoordinator.shouldLoadCategoryFilters(
                currentState = getCurrentState(),
                isLoading = isCategoryFiltersLoading,
                trigger = trigger,
            )
        ) {
            loadCategoryFilters()
        }
    }

    private fun loadCategoryFilters() {
        if (isCategoryFiltersLoading) {
            return
        }
        isCategoryFiltersLoading = true

        viewModelScope.launch {
            try {
                val result = getCategoryFiltersUseCase.execute()
                result.fold(
                    onSuccess = { filters ->
                        val newState = reducer.handleCategoryFiltersLoaded(
                            currentState = getCurrentState(),
                            categoryFilters = filters.toImmutableList(),
                        )
                        updateState(newState)
                    },
                    onFailure = {
                        TimberLogger.w(TAG, "分类筛选器加载失败，保留现有状态")
                    },
                )
            } catch (e: Exception) {
                TimberLogger.e(TAG, "加载分类筛选器失败", e)
            } finally {
                isCategoryFiltersLoading = false
            }
        }
    }

    private fun startSearchTrace(params: SearchParams): SearchPerformanceTrace {
        val trace = performanceTraceCoordinator.start(
            action = "search",
            metadata = mapOf(
                "trigger" to params.triggerSource.name,
                "query" to params.query,
                "page" to params.page.toString(),
            ),
        )
        TimberLogger.d(TAG, performanceTraceCoordinator.formatStartMessage(trace))
        return trace
    }

    private fun finishSearchTrace(
        trace: SearchPerformanceTrace,
        status: String,
        metadata: Map<String, String> = emptyMap(),
    ) {
        TimberLogger.d(
            TAG,
            performanceTraceCoordinator.formatFinishMessage(
                trace = trace,
                status = status,
                metadata = metadata,
            ),
        )
    }
}
