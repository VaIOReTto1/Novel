package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.search.gateway.CategoryFilterGateway
import com.novel.page.search.gateway.SearchQueryGateway
import com.novel.page.search.repository.SearchParams
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchResultViewModel @Inject constructor(
    private val searchQueryGateway: SearchQueryGateway,
    private val categoryFilterGateway: CategoryFilterGateway,
) : BaseMviViewModel<SearchResultIntent, SearchResultState, SearchResultEffect>() {

    companion object {
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
        setupSearchDebounce()
    }

    override fun createInitialState(): SearchResultState = SearchResultState()

    override fun getReducer(): MviReducer<SearchResultIntent, SearchResultState> = reducer

    override fun onIntentProcessed(intent: SearchResultIntent, newState: SearchResultState) {
        when (intent) {
            is SearchResultIntent.PerformSearch -> handleInitialSearch(intent.query)
            is SearchResultIntent.SelectCategory -> handleCategorySelection()
            SearchResultIntent.OpenFilterSheet -> handleOpenFilterSheet()
            SearchResultIntent.ApplyFilters -> handleApplyFilters()
            SearchResultIntent.LoadNextPage -> handleLoadNextPage()
            else -> Unit
        }
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
        currentSearchParams = params
        retryAttempts = 0
        performSearchWithRetry(params, startSearchTrace(params))
    }

    private fun performSearchWithRetry(params: SearchParams, trace: SearchPerformanceTrace) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            runCatching {
                searchQueryGateway.searchBooks(params)
            }.onSuccess { response ->
                if (response != null) {
                    val hasMore = (response.pages ?: 0) > params.page
                    updateState(
                        reducer.handleSearchSuccess(
                            currentState = getCurrentState(),
                            books = response.list,
                            totalResults = response.total?.toInt() ?: 0,
                            hasMore = hasMore,
                            isLoadMore = params.isLoadMore,
                        ),
                    )
                    if (params.isLoadMore) {
                        isLoadingMore = false
                    }
                    retryAttempts = 0
                    finishSearchTrace(
                        trace,
                        status = "success",
                        metadata = mapOf(
                            "trigger" to params.triggerSource.name,
                            "resultCount" to response.list.size.toString(),
                            "hasMore" to hasMore.toString(),
                            "page" to params.page.toString(),
                        ),
                    )
                } else {
                    handleSearchFailure(params, trace, IllegalStateException("empty result"))
                }
            }.onFailure { error ->
                handleSearchFailure(params, trace, error)
            }
        }
    }

    private suspend fun handleSearchFailure(
        params: SearchParams,
        trace: SearchPerformanceTrace,
        exception: Throwable,
    ) {
        if (retryPolicyCoordinator.shouldRetry(params, retryAttempts, MAX_RETRY_ATTEMPTS)) {
            retryAttempts++
            delay(
                retryPolicyCoordinator.retryDelayMs(
                    retryAttempts = retryAttempts,
                    baseDelayMs = RETRY_DELAY_MS,
                ),
            )
            performSearchWithRetry(
                retryPolicyCoordinator.createAutomaticRetryParams(params),
                trace,
            )
            return
        }

        updateState(
            reducer.handleSearchError(
                currentState = getCurrentState(),
                errorMessage = "搜索失败: ${exception.message}",
                isLoadMore = params.isLoadMore,
            ),
        )

        if (params.isLoadMore) {
            currentPage--
            isLoadingMore = false
        }

        finishSearchTrace(
            trace,
            status = "failure",
            metadata = mapOf(
                "trigger" to trace.metadata["trigger"].orEmpty().ifBlank { params.triggerSource.name },
                "page" to params.page.toString(),
            ),
        )
    }

    private fun handleInitialSearch(query: String) {
        ensureCategoryFiltersLoaded(SearchCategoryFilterLoadTrigger.SEARCH_STARTED)
        currentPage = 1
        isLoadingMore = false
        searchQueryChannel.trySend(
            SearchParams(
                query = query,
                page = currentPage,
                categoryId = getCurrentState().selectedCategoryId,
                filters = getCurrentState().filters,
                isLoadMore = false,
                triggerSource = SearchTriggerSource.INITIAL_ENTRY,
            ),
        )
    }

    private fun handleCategorySelection() {
        val currentState = getCurrentState()
        if (currentState.query.isNotBlank()) {
            currentPage = 1
            executeSearch(
                SearchParams(
                    query = currentState.query,
                    page = currentPage,
                    categoryId = currentState.selectedCategoryId,
                    filters = currentState.filters,
                    isLoadMore = false,
                    triggerSource = SearchTriggerSource.CATEGORY_SWITCH,
                ),
            )
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
            executeSearch(
                SearchParams(
                    query = currentState.query,
                    page = currentPage,
                    categoryId = currentState.selectedCategoryId,
                    filters = currentState.filters,
                    isLoadMore = false,
                    triggerSource = SearchTriggerSource.FILTER_APPLY,
                ),
            )
        }
    }

    private fun handleLoadNextPage() {
        if (isLoadingMore) {
            return
        }

        val currentState = getCurrentState()
        if (!currentState.hasMore || currentState.query.isBlank()) {
            return
        }

        isLoadingMore = true
        currentPage += 1
        executeSearch(
            SearchParams(
                query = currentState.query,
                page = currentPage,
                categoryId = currentState.selectedCategoryId,
                filters = currentState.filters,
                isLoadMore = true,
                triggerSource = SearchTriggerSource.LOAD_MORE,
            ),
        )
    }

    private fun ensureCategoryFiltersLoaded(trigger: SearchCategoryFilterLoadTrigger) {
        if (
            categoryFilterLoadCoordinator.shouldLoadCategoryFilters(
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
                categoryFilterGateway.getCategoryFilters().onSuccess { filters ->
                    updateState(
                        reducer.handleCategoryFiltersLoaded(
                            currentState = getCurrentState(),
                            categoryFilters = filters.toImmutableList(),
                        ),
                    )
                }
            } finally {
                isCategoryFiltersLoading = false
            }
        }
    }

    private fun startSearchTrace(params: SearchParams): SearchPerformanceTrace {
        return performanceTraceCoordinator.start(
            action = "search",
            metadata = mapOf(
                "trigger" to params.triggerSource.name,
                "query" to params.query,
            ),
        )
    }

    private fun finishSearchTrace(
        trace: SearchPerformanceTrace,
        status: String,
        metadata: Map<String, String>,
    ) {
        CoreLogger.d(
            "SearchResultViewModel",
            performanceTraceCoordinator.formatFinishMessage(trace, status, metadata),
        )
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchQueryChannel.close()
    }
}
