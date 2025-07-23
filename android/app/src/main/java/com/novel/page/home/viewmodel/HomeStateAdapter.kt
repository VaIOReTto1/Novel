package com.novel.page.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.StateFlow
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import com.novel.core.adapter.StateAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * Home状态适配器
 * 
 * 为Home模块提供状态适配功能，方便UI层访问MVI状态的特定部分
 * 继承基础StateAdapter，提供Home模块专用的状态适配功能
 * 
 * 特性：
 * - 继承基础StateAdapter的所有功能
 * - Home模块专用状态访问方法
 * - 细粒度状态订阅，减少不必要的重组
 * - 类型安全的强类型状态访问
 * - UI友好的便利方法
 * - 向后兼容HomeUiState格式
 * - 优化的@Composable状态访问方法，提升skippable比例
 */
@Stable
class HomeStateAdapter(
    stateFlow: StateFlow<HomeState>,
    @Stable
    private val scope: kotlinx.coroutines.CoroutineScope
) : StateAdapter<HomeState>(stateFlow) {
    
    // region Composable 状态访问方法 (用于提升 skippable 比例)
    
    /**
     * 书籍分类列表 - 优化版本
     * 替代 categories.collectAsState() 以提升性能
     */
    @Composable
    fun categoriesState(): State<ImmutableList<HomeCategoryEntity>> = 
        createStableState { it.categories }

    /**
     * 分类筛选器列表 - 优化版本
     * 替代 categoryFilters.collectAsState() 以提升性能
     */
    @Composable
    fun categoryFiltersState(): State<ImmutableList<CategoryInfo>> = 
        createStableState { it.categoryFilters }

    /**
     * 当前选中的分类筛选器 - 优化版本
     */
    @Composable
    fun selectedCategoryFilterState(): State<String> = 
        createStableState { it.selectedCategoryFilter }

    /**
     * 搜索关键词 - 优化版本
     */
    @Composable
    fun searchQueryState(): State<String> = 
        createStableState { it.searchQuery }

    /**
     * 轮播图书籍列表 - 优化版本
     */
    @Composable
    fun carouselBooksState(): State<ImmutableList<HomeBookEntity>> = 
        createStableState { it.carouselBooks }

    /**
     * 热门书籍列表 - 优化版本
     */
    @Composable
    fun hotBooksState(): State<ImmutableList<HomeBookEntity>> = 
        createStableState { it.hotBooks }

    /**
     * 最新书籍列表 - 优化版本
     */
    @Composable
    fun newBooksState(): State<ImmutableList<HomeBookEntity>> = 
        createStableState { it.newBooks }

    /**
     * VIP书籍列表 - 优化版本
     */
    @Composable
    fun vipBooksState(): State<ImmutableList<HomeBookEntity>> = 
        createStableState { it.vipBooks }

    /**
     * 当前选中的榜单类型 - 优化版本
     */
    @Composable
    fun selectedRankTypeState(): State<String> = 
        createStableState { it.selectedRankType }

    /**
     * 榜单书籍列表 - 优化版本
     */
    @Composable
    fun rankBooksState(): State<ImmutableList<BookService.BookRank>> = 
        createStableState { it.rankBooks }

    /**
     * 分类推荐书籍列表 - 优化版本
     */
    @Composable
    fun recommendBooksState(): State<ImmutableList<SearchService.BookInfo>> = 
        createStableState { it.recommendBooks }

    /**
     * 首页推荐书籍列表 - 优化版本
     */
    @Composable
    fun homeRecommendBooksState(): State<ImmutableList<HomeService.HomeBook>> = 
        createStableState { it.homeRecommendBooks }

    /**
     * 是否处于刷新状态 - 优化版本
     */
    @Composable
    fun isRefreshingState(): State<Boolean> = 
        createStableState { it.isRefreshing }

    /**
     * 是否处于推荐模式 - 优化版本
     */
    @Composable
    fun isRecommendModeState(): State<Boolean> = 
        createStableState { it.isRecommendMode }

    /**
     * 分类数据加载状态 - 优化版本
     */
    @Composable
    fun categoryLoadingState(): State<Boolean> = 
        createStableState { it.categoryLoading }

    /**
     * 书籍数据加载状态 - 优化版本
     */
    @Composable
    fun booksLoadingState(): State<Boolean> = 
        createStableState { it.booksLoading }

    /**
     * 榜单数据加载状态 - 优化版本
     */
    @Composable
    fun rankLoadingState(): State<Boolean> = 
        createStableState { it.rankLoading }

    /**
     * 推荐书籍加载状态 - 优化版本
     */
    @Composable
    fun recommendLoadingState(): State<Boolean> = 
        createStableState { it.recommendLoading }

    /**
     * 首页推荐书籍加载状态 - 优化版本
     */
    @Composable
    fun homeRecommendLoadingState(): State<Boolean> = 
        createStableState { it.homeRecommendLoading }

    /**
     * 是否有更多推荐数据 - 优化版本
     */
    @Composable
    fun hasMoreRecommendState(): State<Boolean> = 
        createStableState { it.hasMoreRecommend }

    /**
     * 是否有更多首页推荐数据 - 优化版本
     */
    @Composable
    fun hasMoreHomeRecommendState(): State<Boolean> = 
        createStableState { it.hasMoreHomeRecommend }

    /**
     * 当前推荐页码 - 优化版本
     */
    @Composable
    fun recommendPageState(): State<Int> = 
        createStableState { it.recommendPage }

    /**
     * 首页推荐页码 - 优化版本
     */
    @Composable
    fun homeRecommendPageState(): State<Int> = 
        createStableState { it.homeRecommendPage }

    /**
     * 当前推荐书籍列表（根据模式） - 优化版本
     */
    @Composable
    fun currentRecommendBooksState(): State<ImmutableList<RecommendItem>> = 
        createStableState { state ->
            if (state.isRecommendMode) {
                state.homeRecommendBooks.map { HomeRecommendItem(it) }.toImmutableList()
            } else {
                state.recommendBooks.map { CategoryRecommendItem(it) }.toImmutableList()
            }
        }

    // endregion
    
    // region Home模块专用便利方法
    
    /** 检查是否可以执行搜索 */
    fun canPerformSearch(): Boolean {
        val state = getCurrentSnapshot()
        return state.searchQuery.isNotBlank() && !state.isLoading
    }
    
    /** 获取搜索提示文本 */
    fun getSearchHint(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "加载中..."
            state.hasError -> "搜索失败，请重试"
            else -> "搜索您喜欢的小说"
        }
    }
    
    /** 检查是否可以加载更多推荐 */
    fun canLoadMoreRecommend(): Boolean {
        val state = getCurrentSnapshot()
        return when {
            state.isRecommendMode -> state.hasMoreHomeRecommend && !state.homeRecommendLoading
            else -> state.hasMoreRecommend && !state.recommendLoading
        }
    }
    
    /** 获取加载更多文本 */
    fun getLoadMoreText(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isRecommendMode && state.homeRecommendLoading -> "加载中..."
            !state.isRecommendMode && state.recommendLoading -> "加载中..."
            !canLoadMoreRecommend() -> "已加载全部"
            state.hasError -> "加载失败，点击重试"
            else -> "点击加载更多"
        }
    }

    /** 获取首页状态摘要 */
    fun getHomeStatusSummary(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "加载中"
            state.hasError -> "加载失败"
            state.isRefreshing -> "刷新中"
            state.isEmpty -> "暂无数据"
            else -> "加载完成"
        }
    }
    
    /** 检查是否显示空状态 */
    fun shouldShowEmptyState(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isLoading && !state.hasError && 
               state.categories.isEmpty() && 
               state.carouselBooks.isEmpty() && 
               state.hotBooks.isEmpty() && 
               state.newBooks.isEmpty() && 
               state.vipBooks.isEmpty()
    }
    
    /** 检查是否显示加载更多按钮 */
    fun shouldShowLoadMoreButton(): Boolean {
        val state = getCurrentSnapshot()
        return canLoadMoreRecommend() && (state.recommendBooks.isNotEmpty() || state.homeRecommendBooks.isNotEmpty())
    }
    
    /** 获取推荐模式文本 */
    fun getRecommendModeText(): String {
        val state = getCurrentSnapshot()
        return if (state.isRecommendMode) {
            "首页推荐"
        } else {
            "分类推荐 - ${state.selectedCategoryFilter}"
        }
    }
    
    // endregion
    
    // region 向后兼容方法
    
    /**
     * 将HomeState转换为HomeUiState
     * 保持与原有UI层的兼容性
     */
    fun toHomeUiState(): HomeUiState {
        val state = getCurrentSnapshot()
        return HomeUiState(
            // 基础状态
            version = state.version,
            isLoading = state.isLoading,
            error = state.error,
            isRefreshing = state.isRefreshing,
            
            // 分类数据相关
            categories = state.categories,
            categoryLoading = state.categoryLoading,
            
            // 书籍推荐数据相关
            carouselBooks = state.carouselBooks,
            hotBooks = state.hotBooks,
            newBooks = state.newBooks,
            vipBooks = state.vipBooks,
            booksLoading = state.booksLoading,
            
            // 搜索相关
            searchQuery = state.searchQuery,
            
            // 分类筛选器状态
            selectedCategoryFilter = state.selectedCategoryFilter,
            categoryFilters = state.categoryFilters,
            categoryFiltersLoading = state.categoryFiltersLoading,
            
            // 榜单状态
            selectedRankType = state.selectedRankType,
            rankBooks = state.rankBooks,
            rankLoading = state.rankLoading,
            
            // 推荐书籍状态
            recommendBooks = state.recommendBooks,
            homeRecommendBooks = state.homeRecommendBooks,
            recommendLoading = state.recommendLoading,
            hasMoreRecommend = state.hasMoreRecommend,
            recommendPage = state.recommendPage,
            totalRecommendPages = state.totalRecommendPages,
            
            // 首页推荐分页状态
            homeRecommendLoading = state.homeRecommendLoading,
            hasMoreHomeRecommend = state.hasMoreHomeRecommend,
            homeRecommendPage = state.homeRecommendPage,
            
            // 显示模式控制
            isRecommendMode = state.isRecommendMode
        )
    }
    
    // endregion
}

/**
 * 状态组合器
 * 将多个状态组合成UI需要的复合状态
 */
@Stable
data class HomeScreenState(
    val isLoading: Boolean,
    val error: String?,
    val isRefreshing: Boolean,
    val searchQuery: String,
    val categories: ImmutableList<HomeCategoryEntity>,
    val selectedCategoryFilter: String,
    val categoryFilters: ImmutableList<CategoryInfo>,
    val carouselBooks: ImmutableList<HomeBookEntity>,
    val hotBooks: ImmutableList<HomeBookEntity>,
    val newBooks: ImmutableList<HomeBookEntity>,
    val vipBooks: ImmutableList<HomeBookEntity>,
    val rankBooks: ImmutableList<BookService.BookRank>,
    val selectedRankType: String,
    val currentRecommendBooks: ImmutableList<RecommendItem>, // 根据模式显示不同类型的书籍
    val canPerformSearch: Boolean,
    val searchHint: String,
    val canLoadMoreRecommend: Boolean,
    val loadMoreText: String,
    val homeStatusSummary: String,
    val shouldShowEmptyState: Boolean,
    val shouldShowLoadMoreButton: Boolean,
    val recommendModeText: String,
    val isRecommendMode: Boolean
)

/**
 * 将HomeState转换为UI友好的组合状态
 */
@Stable
fun HomeStateAdapter.toScreenState(): HomeScreenState {
    val snapshot = getCurrentSnapshot()
    return HomeScreenState(
        isLoading = snapshot.isLoading,
        error = snapshot.error,
        isRefreshing = snapshot.isRefreshing,
        searchQuery = snapshot.searchQuery,
        categories = snapshot.categories,
        selectedCategoryFilter = snapshot.selectedCategoryFilter,
        categoryFilters = snapshot.categoryFilters,
        carouselBooks = snapshot.carouselBooks,
        hotBooks = snapshot.hotBooks,
        newBooks = snapshot.newBooks,
        vipBooks = snapshot.vipBooks,
        rankBooks = snapshot.rankBooks,
        selectedRankType = snapshot.selectedRankType,
        currentRecommendBooks = if (snapshot.isRecommendMode) {
            snapshot.homeRecommendBooks.map { HomeRecommendItem(it) }.toImmutableList()
        } else {
            snapshot.recommendBooks.map { CategoryRecommendItem(it) }.toImmutableList()
        },
        canPerformSearch = canPerformSearch(),
        searchHint = getSearchHint(),
        canLoadMoreRecommend = canLoadMoreRecommend(),
        loadMoreText = getLoadMoreText(),
        homeStatusSummary = getHomeStatusSummary(),
        shouldShowEmptyState = shouldShowEmptyState(),
        shouldShowLoadMoreButton = shouldShowLoadMoreButton(),
        recommendModeText = getRecommendModeText(),
        isRecommendMode = snapshot.isRecommendMode
    )
}

/**
 * 原有的HomeUiState数据类（保持兼容性）
 */
@Stable
data class HomeUiState(
    // 基础状态
    val version: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    
    // 分类数据相关
    val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),
    val categoryLoading: Boolean = false,
    
    // 书籍推荐数据相关
    val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val booksLoading: Boolean = false,
    
    // 搜索相关
    val searchQuery: String = "",
    
    // 分类筛选器状态
    val selectedCategoryFilter: String = "推荐",
    val categoryFilters: ImmutableList<CategoryInfo> = persistentListOf(CategoryInfo("0", "推荐")),
    val categoryFiltersLoading: Boolean = false,
    
    // 榜单状态
    val selectedRankType: String = "点击榜",
    val rankBooks: ImmutableList<BookService.BookRank> = persistentListOf(),
    val rankLoading: Boolean = false,
    
    // 推荐书籍状态
    val recommendBooks: ImmutableList<SearchService.BookInfo> = persistentListOf(),
    val homeRecommendBooks: ImmutableList<HomeService.HomeBook> = persistentListOf(),
    val recommendLoading: Boolean = false,
    val hasMoreRecommend: Boolean = true,
    val recommendPage: Int = 1,
    val totalRecommendPages: Int = 1,
    
    // 首页推荐分页状态
    val homeRecommendLoading: Boolean = false,
    val hasMoreHomeRecommend: Boolean = true,
    val homeRecommendPage: Int = 1,
    
    // 显示模式控制
    val isRecommendMode: Boolean = true
)