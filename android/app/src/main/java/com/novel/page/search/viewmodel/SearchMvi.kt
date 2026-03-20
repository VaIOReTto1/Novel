package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect
import com.novel.page.search.component.SearchRankingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * Search模块MVI契约定义
 * 
 * 根据优化方案阶段2第13天任务要求，将Search模块重构为统一MVI架构
 * 保持UI和业务逻辑完全不变，所有功能完整实现无遗漏
 */

// region Search Intent 定义

/**
 * 搜索页面用户意图
 * 基于核心MVI接口，整合现有SearchAction
 */
sealed class SearchIntent : MviIntent {
    /** 加载初始数据 */
    data object LoadInitialData : SearchIntent()
    
    /** 更新搜索查询内容 */
    data class UpdateSearchQuery(val query: String) : SearchIntent()
    
    /** 执行搜索操作 */
    data class PerformSearch(val query: String) : SearchIntent()
    
    /** 切换历史记录展开状态 */
    data object ToggleHistoryExpansion : SearchIntent()
    
    /** 导航到书籍详情页 */
    data class NavigateToBookDetail(val bookId: Long) : SearchIntent()
    
    /** 返回上级页面 */
    data object NavigateBack : SearchIntent()
    
    /** 清除错误状态 */
    data object ClearError : SearchIntent()
}

// endregion

// region Search State 定义

/**
 * 搜索页面状态
 * 基于现有SearchUiState重构，符合核心MVI State接口
 */
@Stable
data class SearchState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // 搜索相关
    /** 当前搜索查询内容 */
    val searchQuery: String = "",
    /** 搜索历史记录 */
    val searchHistory: ImmutableList<String> = persistentListOf(),
    /** 历史记录是否展开显示 */
    val isHistoryExpanded: Boolean = false,
    
    // 推荐榜单
    /** 小说榜单数据 */
    val novelRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    /** 剧本榜单数据 */
    val dramaRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    /** 新书榜单数据 */
    val newBookRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    
    /** 榜单数据加载状态 */
    val rankingLoading: Boolean = false
) : MviState {
    
    override val isEmpty: Boolean
        get() = searchHistory.isEmpty() && 
                novelRanking.isEmpty() && 
                dramaRanking.isEmpty() && 
                newBookRanking.isEmpty()
}

// endregion

// region Search Effect 定义

/**
 * 搜索页面副作用
 * 替换现有SearchEvent，处理一次性事件如导航和提示
 */
sealed class SearchEffect : MviEffect {
    /** 导航到书籍详情页 */
    data class NavigateToBookDetail(val bookId: Long) : SearchEffect()
    
    /** 导航到搜索结果页 */
    data class NavigateToSearchResult(val query: String) : SearchEffect()
    
    /** 返回上级页面 */
    data object NavigateBack : SearchEffect()
    
    /** 显示Toast提示 */
    data class ShowToast(val message: String) : SearchEffect()
}

// endregion
