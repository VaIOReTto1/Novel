package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * SearchResult模块MVI契约定义
 * 
 * 根据优化方案阶段2第13天任务要求，将SearchResult模块重构为统一MVI架构
 * 保持UI和业务逻辑完全不变，所有功能完整实现无遗漏
 */

// region SearchResult Intent 定义

/**
 * 搜索结果页面用户意图
 * 基于核心MVI接口，整合现有SearchResultAction
 */
sealed class SearchResultIntent : MviIntent {
    /** 更新搜索查询内容 */
    data class UpdateQuery(val query: String) : SearchResultIntent()
    
    /** 执行搜索操作 */
    data class PerformSearch(val query: String) : SearchResultIntent()
    
    /** 选择搜索分类 */
    data class SelectCategory(val categoryId: Int?) : SearchResultIntent()
    
    /** 打开筛选面板 */
    data object OpenFilterSheet : SearchResultIntent()
    
    /** 关闭筛选面板 */
    data object CloseFilterSheet : SearchResultIntent()
    
    /** 更新筛选条件 */
    data class UpdateFilters(val filters: FilterState) : SearchResultIntent()
    
    /** 应用筛选条件 */
    data object ApplyFilters : SearchResultIntent()
    
    /** 清除筛选条件 */
    data object ClearFilters : SearchResultIntent()
    
    /** 加载下一页数据 */
    data object LoadNextPage : SearchResultIntent()
    
    /** 导航到书籍详情 */
    data class NavigateToDetail(val bookId: String) : SearchResultIntent()
    
    /** 返回上级页面 */
    data object NavigateBack : SearchResultIntent()
}

// endregion

// region SearchResult State 定义

/**
 * 搜索结果页面状态
 * 基于现有SearchResultUiState重构，符合核心MVI State接口
 */
@Stable
data class SearchResultState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    /** 搜索查询内容 */
    val query: String = "",
    /** 搜索结果列表 */
    val books: ImmutableList<BookInfoRespDto> = persistentListOf(),
    /** 总结果数量 */
    val totalResults: Int = 0,
    /** 是否还有更多数据 */
    val hasMore: Boolean = false,
    /** 当前选中的分类ID */
    val selectedCategoryId: Int? = null,
    /** 分类筛选器列表 */
    val categoryFilters: ImmutableList<CategoryFilter> = persistentListOf(),
    /** 当前筛选条件 */
    val filters: FilterState = FilterState(),
    /** 筛选面板是否打开 */
    val isFilterSheetOpen: Boolean = false,
    /** 分页加载状态 */
    val isLoadingMore: Boolean = false
) : MviState {
    
    override val isEmpty: Boolean
        get() = books.isEmpty() && !isLoading
}

// endregion

// region SearchResult Effect 定义

/**
 * 搜索结果页面副作用
 * 替换现有SearchResultEvent，处理一次性事件如导航
 */
sealed class SearchResultEffect : MviEffect {
    /** 导航到书籍详情 */
    data class NavigateToDetail(val bookId: String) : SearchResultEffect()
    
    /** 返回上级页面 */
    data object NavigateBack : SearchResultEffect()
    
    /** 显示Toast提示 */
    data class ShowToast(val message: String) : SearchResultEffect()
}

// endregion

/**
 * 筛选状态
 */
data class FilterState(
    val updateStatus: UpdateStatus = UpdateStatus.ALL,
    val isVip: VipStatus = VipStatus.ALL,
    val wordCountRange: WordCountRange = WordCountRange.ALL,
    val sortBy: SortBy = SortBy.NULL
)

/**
 * 更新状态
 */
enum class UpdateStatus(val value: Int?, val displayName: String) {
    ALL(null, "全部"),
    FINISHED(1, "已完结"),
    ONGOING(0, "连载中"),
    HALF_YEAR_FINISHED(-1, "半年内完结"),
    THREE_DAYS_UPDATED(-2, "3日内更新"),
    SEVEN_DAYS_UPDATED(-3, "7日内更新"),
    ONE_MONTH_UPDATED(-4, "1月内更新")
}

/**
 * VIP状态
 */
enum class VipStatus(val value: Int?, val displayName: String) {
    ALL(null, "全部"),
    FREE(0, "免费"),
    PAID(1, "付费")
}

/**
 * 字数范围
 */
enum class WordCountRange(val min: Int?, val max: Int?, val displayName: String) {
    ALL(null, null, "全部"),
    UNDER_10W(null, 100000, "10万字以内"),
    W_10_30(100000, 300000, "10-30万"),
    W_30_50(300000, 500000, "30-50万"),
    W_50_100(500000, 1000000, "50-100万"),
    W_100_200(1000000, 2000000, "100-200万"),
    W_200_300(2000000, 3000000, "200-300万"),
    OVER_300W(3000000, null, "300万以上")
}

/**
 * 排序方式
 */
enum class SortBy(val value: String, val displayName: String) {
    NULL("null","默认排序"),
    NEW_UPDATE("last_chapter_update_time desc", "最近更新"),
    HIGH_CLICK("visit_count desc", "点击量"),
    WORD_COUNT("word_count desc", "总字数")
}

/**
 * 分类筛选
 */
data class CategoryFilter(
    val id: Int,
    val name: String?
)

/**
 * 书籍信息响应DTO（简化版本，与API对应）
 */
data class BookInfoRespDto(
    val id: Long,
    val categoryId: Long?,
    val categoryName: String?,
    val picUrl: String?,
    val bookName: String?,
    val authorId: Long?,
    val authorName: String?,
    val bookDesc: String?,
    val bookStatus: Int,
    val visitCount: Long,
    val wordCount: Int,
    val commentCount: Int,
    val firstChapterId: Long?,
    val lastChapterId: Long?,
    val lastChapterName: String?,
    val updateTime: String?
)

