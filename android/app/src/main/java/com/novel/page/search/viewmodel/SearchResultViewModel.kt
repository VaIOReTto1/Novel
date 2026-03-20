package com.novel.page.search.viewmodel

import com.novel.utils.TimberLogger
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.search.usecase.*
import com.novel.page.search.repository.SearchRepository
import com.novel.page.search.repository.SearchParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.Channel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import androidx.compose.runtime.Stable

/**
 * 搜索结果页面ViewModel - MVI架构重构版
 * 
 * 根据优化方案阶段2第13天任务要求，重构为统一MVI架构：
 * - 继承BaseMviViewModel<SearchResultIntent, SearchResultState, SearchResultEffect>
 * - 移除原有的兼容性代码和ViewModel内缓存实现
 * - 缓存逻辑转移到SearchRepository中统一管理
 * - 保持UI和业务逻辑完全不变，所有功能完整实现无遗漏
 * - 搜索防抖、重试机制优化
 * 
 * 主要职责：
 * - 管理搜索结果的获取和展示
 * - 处理分页加载逻辑
 * - 管理筛选条件和分类选择
 * - 协调搜索相关的用户交互
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchResultViewModel @Inject constructor(
    /** 搜索数据仓库 */
    private val searchRepository: SearchRepository,
    /** 获取分类筛选器用例 */
    private val getCategoryFiltersUseCase: GetCategoryFiltersUseCase
) : BaseMviViewModel<SearchResultIntent, SearchResultState, SearchResultEffect>() {
    
    companion object {
        private const val TAG = "SearchResultViewModel"
        private const val SEARCH_DEBOUNCE_DELAY_MS = 500L // 搜索防抖延迟
        private const val MAX_RETRY_ATTEMPTS = 3 // 最大重试次数
        private const val RETRY_DELAY_MS = 1000L // 重试延迟
    }
    
    /** Reducer实例，处理状态转换逻辑 */
    private val reducer = SearchResultReducer()
    
    /** StateAdapter实例，提供UI友好的状态访问 */
    val adapter = SearchResultStateAdapter(state)
    
    /** 当前页码（从1开始） */
    private var currentPage = 1
    /** 分页加载状态标识 */
    private var isLoadingMore = false
    
    /** 搜索防抖处理 */
    @Stable
    private val searchQueryChannel = Channel<SearchParams>(Channel.UNLIMITED)
    @Stable
    private var searchJob: Job? = null
    
    /** 当前搜索参数，用于重试 */
    private var currentSearchParams: SearchParams? = null
    private var retryAttempts = 0

    init {
        TimberLogger.d(TAG, "SearchResultViewModel MVI重构版初始化")
        setupSearchDebounce()
        // 立即加载分类筛选器
        loadCategoryFilters()
    }
    
    /**
     * 设置搜索防抖机制
     */
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            searchQueryChannel.receiveAsFlow()
                .debounce(SEARCH_DEBOUNCE_DELAY_MS)
                .collect { params ->
                    executeSearch(params)
                }
        }
    }
    
    /**
     * 执行搜索（防抖后的实际搜索逻辑）
     */
    private fun executeSearch(params: SearchParams) {
        TimberLogger.d(TAG, "执行搜索: ${params.query}, 页码: ${params.page}")
        
        currentSearchParams = params
        retryAttempts = 0
        
        performSearchWithRetry(params)
    }
    
    /**
     * 带重试机制的搜索执行
     */
    private fun performSearchWithRetry(params: SearchParams) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val response = searchRepository.searchBooksWithCache(params)
                
                if (response != null) {
                    TimberLogger.d(TAG, "搜索成功: 返回${response.list.size}条结果")
                    
                    val books = response.list
                    val hasMore = (response.pages ?: 0) > params.page
                    val totalResults = response.total?.toInt() ?: 0
                    
                    // 更新状态
                    val newState = reducer.handleSearchSuccess(
                        currentState = getCurrentState(),
                        books = books,
                        totalResults = totalResults,
                        hasMore = hasMore,
                        isLoadMore = params.isLoadMore
                    )
                    updateState(newState)
                    
                    if (params.isLoadMore) {
                        isLoadingMore = false
                    }
                    
                    // 重置重试计数
                    retryAttempts = 0
                } else {
                    handleSearchFailure(Exception("搜索返回为空"), params)
                }
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "搜索异常", e)
                handleSearchFailure(e, params)
            }
        }
    }
    
    /**
     * 处理搜索失败和重试逻辑
     */
    private fun handleSearchFailure(exception: Throwable, params: SearchParams) {
        retryAttempts++
        
        if (retryAttempts <= MAX_RETRY_ATTEMPTS) {
            TimberLogger.d(TAG, "搜索失败，准备重试 ($retryAttempts/$MAX_RETRY_ATTEMPTS)")
            
            viewModelScope.launch {
                delay(RETRY_DELAY_MS * retryAttempts) // 递增延迟
                performSearchWithRetry(params)
            }
        } else {
            TimberLogger.e(TAG, "搜索失败，超出重试次数")
            
            val newState = reducer.handleSearchError(
                currentState = getCurrentState(),
                errorMessage = "搜索失败: ${exception.message}",
                isLoadMore = params.isLoadMore
            )
            updateState(newState)
            
            if (params.isLoadMore) {
                currentPage-- // 恢复页码
                isLoadingMore = false
            }
            
            sendEffect(SearchResultEffect.ShowToast("搜索失败: ${exception.message}"))
        }
    }
    
    /**
     * 创建初始状态
     * 基础MVI框架要求实现此方法
     */
    override fun createInitialState(): SearchResultState {
        return SearchResultState()
    }
    
    /**
     * 获取Reducer实例
     * 基础MVI框架要求实现此方法
     */
    override fun getReducer(): MviReducer<SearchResultIntent, SearchResultState> {
        return reducer
    }
    
    /**
     * Intent处理完成后的回调
     * 处理需要异步操作的Intent
     */
    override fun onIntentProcessed(intent: SearchResultIntent, newState: SearchResultState) {
        // 获取副作用并处理
        val result = reducer.reduceWithEffect(getCurrentState(), intent)
        result.effect?.let { effect ->
            sendEffect(effect)
        }
        
        when (intent) {
            is SearchResultIntent.PerformSearch -> {
                handlePerformSearch(intent.query)
            }
            is SearchResultIntent.SelectCategory -> {
                handleCategorySelection()
            }
            is SearchResultIntent.ApplyFilters -> {
                handleApplyFilters()
            }
            is SearchResultIntent.LoadNextPage -> {
                handleLoadNextPage()
            }
            else -> {
                // 其他Intent在Reducer中已经完全处理，无需额外操作
            }
        }
    }
    

    
    // region 私有业务逻辑处理方法
    
    /**
     * 处理搜索操作
     * 重置分页状态并使用防抖机制执行新搜索
     */
    private fun handlePerformSearch(query: String) {
        TimberLogger.d(TAG, "准备搜索: $query")
        
        if (query.isBlank()) {
            sendEffect(SearchResultEffect.ShowToast("请输入搜索关键词"))
            return
        }
        
        // 重置分页状态
        currentPage = 1
        isLoadingMore = false
        
        val currentState = getCurrentState()
        val params = SearchParams(
            query = query,
            page = currentPage,
            categoryId = currentState.selectedCategoryId,
            filters = currentState.filters,
            isLoadMore = false
        )
        
        // 使用防抖机制
        searchQueryChannel.trySend(params)
    }
    
    /**
     * 处理分类选择
     * 选择分类后重新搜索
     */
    private fun handleCategorySelection() {
        val currentState = getCurrentState()
        if (currentState.query.isNotBlank()) {
            // 重置分页状态
            currentPage = 1
            isLoadingMore = false
            
            val params = SearchParams(
                query = currentState.query,
                page = currentPage,
                categoryId = currentState.selectedCategoryId,
                filters = currentState.filters,
                isLoadMore = false
            )
            
            // 分类选择立即搜索，不使用防抖
            executeSearch(params)
        }
    }
    
    /**
     * 处理筛选条件应用
     * 应用筛选条件后重新搜索
     */
    private fun handleApplyFilters() {
        val currentState = getCurrentState()
        if (currentState.query.isNotBlank()) {
            // 重置分页状态
            currentPage = 1
            isLoadingMore = false
            
            val params = SearchParams(
                query = currentState.query,
                page = currentPage,
                categoryId = currentState.selectedCategoryId,
                filters = currentState.filters,
                isLoadMore = false
            )
            
            // 筛选应用立即搜索，不使用防抖
            executeSearch(params)
        }
    }
    
    /**
     * 处理分页加载
     * 加载下一页搜索结果
     */
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
        
        // 设置分页加载状态
        isLoadingMore = true
        currentPage++
        
        val params = SearchParams(
            query = currentState.query,
            page = currentPage,
            categoryId = currentState.selectedCategoryId,
            filters = currentState.filters,
            isLoadMore = true
        )
        
        // 分页加载立即执行，不使用防抖
        executeSearch(params)
    }
    
    /**
     * 清理资源
     */
    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchQueryChannel.close()
        TimberLogger.d(TAG, "SearchResultViewModel资源清理完成")
    }
    
    /**
     * 加载分类筛选器
     * 在ViewModel初始化时调用
     */
    private fun loadCategoryFilters() {
        viewModelScope.launch {
            try {
                val result = getCategoryFiltersUseCase.execute()
                result.fold(
                    onSuccess = { filters ->
                        val newState = reducer.handleCategoryFiltersLoaded(
                            currentState = getCurrentState(),
                            categoryFilters = filters.toImmutableList()
                        )
                        updateState(newState)
                    },
                    onFailure = {
                        TimberLogger.w(TAG, "分类筛选器加载失败，保留现有状态")
                    }
                )
            } catch (e: Exception) {
                // 忽略分类加载错误，使用默认值
                TimberLogger.e(TAG, "加载分类筛选器失败", e)
            }
        }
    }
    
    // endregion
}
