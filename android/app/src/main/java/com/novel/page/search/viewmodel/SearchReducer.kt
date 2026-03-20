package com.novel.page.search.viewmodel

import com.novel.utils.TimberLogger
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.core.mvi.MviReducer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 搜索页面状态转换器
 * 
 * 实现纯函数式状态转换逻辑，替换现有ViewModel手写状态管理
 * 根据当前状态和Intent计算新状态，支持副作用处理
 */
class SearchReducer : MviReducer<SearchIntent, SearchState> {
    
    companion object {
        private const val TAG = "SearchReducer"
    }
    
    override fun reduce(currentState: SearchState, intent: SearchIntent): SearchState {
        val result = reduceWithEffect(currentState, intent)
        return result.newState
    }
    
    fun reduceWithEffect(
        currentState: SearchState, 
        intent: SearchIntent
    ): ReduceResult<SearchState, SearchEffect> {
        TimberLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")
        
        return when (intent) {
            is SearchIntent.LoadInitialData -> {
                // 开始加载状态，清除错误
                val newState = currentState.copy(
                    version = currentState.version + 1,
                    isLoading = true,
                    error = null
                )
                ReduceResult(newState)
            }
            
            is SearchIntent.UpdateSearchQuery -> {
                // 更新搜索查询内容
                val newState = currentState.copy(
                    version = currentState.version + 1,
                    searchQuery = intent.query
                )
                ReduceResult(newState)
            }
            
            is SearchIntent.PerformSearch -> {
                // 执行搜索，触发导航副作用
                if (intent.query.isBlank()) {
                    val effect = SearchEffect.ShowToast("请输入搜索关键词")
                    ReduceResult(currentState, effect)
                } else {
                    val effect = SearchEffect.NavigateToSearchResult(intent.query.trim())
                    ReduceResult(currentState, effect)
                }
            }
            
            is SearchIntent.ToggleHistoryExpansion -> {
                // 切换历史记录展开状态
                val newState = currentState.copy(
                    version = currentState.version + 1,
                    isHistoryExpanded = !currentState.isHistoryExpanded
                )
                ReduceResult(newState)
            }
            
            is SearchIntent.NavigateToBookDetail -> {
                // 导航到书籍详情页
                val effect = SearchEffect.NavigateToBookDetail(intent.bookId)
                ReduceResult(currentState, effect)
            }
            
            is SearchIntent.NavigateBack -> {
                // 返回上级页面
                val effect = SearchEffect.NavigateBack
                ReduceResult(currentState, effect)
            }
            
            is SearchIntent.ClearError -> {
                // 清除错误状态
                val newState = currentState.copy(
                    version = currentState.version + 1,
                    error = null
                )
                ReduceResult(newState)
            }
        }
    }
    
    /**
     * 处理数据加载成功的状态更新
     * 提供给ViewModel在异步操作完成后调用
     */
    fun handleLoadInitialDataSuccess(
        currentState: SearchState,
        searchHistory: ImmutableList<String>,
        novelRanking: ImmutableList<com.novel.page.search.component.SearchRankingItem>,
        dramaRanking: ImmutableList<com.novel.page.search.component.SearchRankingItem>,
        newBookRanking: ImmutableList<com.novel.page.search.component.SearchRankingItem>
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = null,
            searchHistory = searchHistory,
            novelRanking = novelRanking,
            dramaRanking = dramaRanking,
            newBookRanking = newBookRanking,
            rankingLoading = false
        )
    }
    
    /**
     * 处理数据加载失败的状态更新
     * 提供给ViewModel在异步操作失败后调用
     */
    fun handleLoadInitialDataError(
        currentState: SearchState,
        errorMessage: String
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = errorMessage,
            rankingLoading = false
        )
    }
    
    /**
     * 处理搜索历史更新
     * 在添加搜索历史后更新状态
     */
    fun handleSearchHistoryUpdated(
        currentState: SearchState,
        updatedHistory: List<String>
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            searchHistory = updatedHistory.toImmutableList()
        )
    }
    
    /**
     * 处理历史展开状态持久化后的更新
     * 在切换展开状态并持久化后更新
     */
    fun handleHistoryExpansionPersisted(
        currentState: SearchState,
        newExpansionState: Boolean
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isHistoryExpanded = newExpansionState
        )
    }
}
