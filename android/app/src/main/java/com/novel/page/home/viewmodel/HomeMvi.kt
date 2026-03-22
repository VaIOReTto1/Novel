package com.novel.page.home.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Immutable
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.dao.HomeBookEntity
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.api.front.HomeService
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

/**
 * 分类信息数据类
 */
@Stable
data class CategoryInfo(
    val id: String,
    val name: String
)

/**
 * React Native 数据传输类型
 */
@Stable
sealed interface ReactNativeData

@Stable
data class UserData(
    val uid: String,
    val token: String,
    val nickname: String,
    val photo: String,
    val sex: String? = null
) : ReactNativeData

@Stable
data class BookListData(
    val books: ImmutableList<HomeService.HomeBook>
) : ReactNativeData

/**
 * 推荐书籍项接口 - 替代 ImmutableList<Any> 提供类型安全
 */
@Stable
sealed interface RecommendItem {
    val id: Long
    val title: String
    val author: String
    val coverUrl: String
}

/**
 * 分类推荐书籍项
 */
@Stable
data class CategoryRecommendItem(
    val data: SearchService.BookInfo
) : RecommendItem {
    override val id: Long get() = data.id
    override val title: String get() = data.bookName
    override val author: String get() = data.authorName
    override val coverUrl: String get() = data.picUrl
}

/**
 * 首页推荐书籍项
 */
@Stable
data class HomeRecommendItem(
    val data: HomeService.HomeBook
) : RecommendItem {
    override val id: Long get() = data.bookId
    override val title: String get() = data.bookName
    override val author: String get() = data.authorName
    override val coverUrl: String get() = data.picUrl
}

/**
 * 屏幕尺寸数据类
 */
@Stable
data class ScreenSize(val w: Float, val h: Float)

/**
 * Home模块的Intent定义
 * 整合现有HomeAction，基于核心MVI接口
 */
@Stable
sealed class HomeIntent : MviIntent {
    /** 加载初始数据 - 页面首次加载时触发 */
    data object LoadInitialData : HomeIntent()

    /** 刷新数据 - 用户下拉刷新时触发 */
    data object RefreshData : HomeIntent()

    /** 更新搜索关键词 - 用户输入搜索内容时触发 */
    data class UpdateSearchQuery(val query: String) : HomeIntent()

    /** 选择分类筛选器 - 用户切换分类标签时触发 */
    data class SelectCategoryFilter(val categoryName: String) : HomeIntent()

    /** 选择榜单类型 - 用户切换榜单标签时触发 */
    data class SelectRankType(val rankType: String) : HomeIntent()

    /** 加载更多推荐内容 - 用户滚动到底部时触发 */
    data object LoadMoreRecommend : HomeIntent()

    /** 加载更多首页推荐内容 - 首页推荐模式下的加载更多 */
    data object LoadMoreHomeRecommend : HomeIntent()

    /** 导航到搜索页面 - 用户点击搜索时触发 */
    data class NavigateToSearch(val query: String) : HomeIntent()

    /** 导航到书籍详情页 - 用户点击书籍时触发 */
    data class NavigateToBookDetail(val bookId: Long) : HomeIntent()

    /** 导航到分类页面 - 用户点击分类时触发 */
    data class NavigateToCategory(val categoryId: Long) : HomeIntent()

    /** 导航到完整榜单页面 - 用户点击查看更多榜单时触发 */
    data class NavigateToFullRanking(val rankType: String) : HomeIntent()

    /** 恢复数据 - 页面重新获得焦点时触发数据检查和恢复 */
    data object RestoreData : HomeIntent()

    /** 清除错误状态 - 用户关闭错误提示时触发 */
    data object ClearError : HomeIntent()

    /** 刷新完成 - 标记刷新操作完成 */
    data object RefreshComplete : HomeIntent()

    // === 数据加载成功的Intent ===
    /** 分类筛选器加载成功 */
    data class CategoryFiltersLoadSuccess(val filters: ImmutableList<CategoryInfo>) : HomeIntent()

    /** 分类筛选器加载失败 */
    data class CategoryFiltersLoadFailure(val error: String) : HomeIntent()

    /** 首页推荐书籍加载成功 */
    data class HomeRecommendBooksLoadSuccess(
        val books: ImmutableList<HomeService.HomeBook>,
        val isRefresh: Boolean = false,
        val hasMore: Boolean = true
    ) : HomeIntent()

    /** 首页推荐书籍加载失败 */
    data class HomeRecommendBooksLoadFailure(val error: String) : HomeIntent()

    /** 分类推荐书籍加载成功 */
    data class CategoryRecommendBooksLoadSuccess(
        val books: ImmutableList<SearchService.BookInfo>,
        val isLoadMore: Boolean = false,
        val hasMore: Boolean = true,
        val totalPages: Int = 1
    ) : HomeIntent()

    /** 分类推荐书籍加载失败 */
    data class CategoryRecommendBooksLoadFailure(val error: String) : HomeIntent()

    /** 榜单书籍加载成功 */
    data class RankBooksLoadSuccess(val books: ImmutableList<BookService.BookRank>) : HomeIntent()

    /** 榜单书籍加载失败 */
    data class RankBooksLoadFailure(val error: String) : HomeIntent()

    /** 分类数据加载成功 */
    data class CategoriesLoadSuccess(val categories: ImmutableList<HomeCategoryEntity>) :
        HomeIntent()

    /** 分类数据加载失败 */
    data class CategoriesLoadFailure(val error: String) : HomeIntent()

    /** 书籍数据加载成功 */
    data class BooksLoadSuccess(
        val carouselBooks: ImmutableList<HomeBookEntity>,
        val hotBooks: ImmutableList<HomeBookEntity>,
        val newBooks: ImmutableList<HomeBookEntity>,
        val vipBooks: ImmutableList<HomeBookEntity>
    ) : HomeIntent()

    /** 书籍数据加载失败 */
    data class BooksLoadFailure(val error: String) : HomeIntent()
}

/**
 * Home模块的State定义
 * 基于现有HomeUiState重构，继承MviState
 */
@Stable
data class HomeState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,

    /** 下拉刷新状态 - 用于下拉刷新的加载指示器 */
    val isRefreshing: Boolean = false,

    // === 分类数据相关 ===
    /** 书籍分类列表 - 从数据库或网络获取的分类信息 */
    val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),

    /** 分类数据加载状态 */
    val categoryLoading: Boolean = false,

    // === 书籍推荐数据相关 ===
    /** 轮播图书籍列表 - 首页顶部轮播展示的精选书籍 */
    val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),

    /** 热门书籍列表 - 按热度排序的推荐书籍 */
    val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),

    /** 最新书籍列表 - 按发布时间排序的新书 */
    val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),

    /** VIP书籍列表 - 付费或会员专享书籍 */
    val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),

    /** 书籍数据加载状态 */
    val booksLoading: Boolean = false,

    // === 搜索相关 ===
    /** 当前搜索关键词 */
    val searchQuery: String = "",

    // === 分类筛选器状态 ===
    /** 当前选中的分类筛选器名称 */
    val selectedCategoryFilter: String = HomeCategoryFilterSupport.HOME_FILTER_LABEL,

    /** 可用的分类筛选器列表 - 包含"推荐"和所有书籍分类 */
    val categoryFilters: ImmutableList<CategoryInfo> = persistentListOf(
        HomeCategoryFilterSupport.homeCategoryFilter()
    ),

    /** 分类筛选器数据加载状态 */
    val categoryFiltersLoading: Boolean = false,

    // === 榜单状态 ===
    /** 当前选中的榜单类型（点击榜/更新榜/新书榜） */
    val selectedRankType: String = "点击榜", // HomeRepository.RANK_TYPE_VISIT

    /** 当前榜单的书籍列表 */
    val rankBooks: ImmutableList<BookService.BookRank> = persistentListOf(),

    /** 榜单数据加载状态 */
    val rankLoading: Boolean = false,

    // === 推荐书籍状态 - 支持双数据源 ===
    /** 分类搜索结果书籍列表 - 来自搜索服务的分类书籍 */
    val recommendBooks: ImmutableList<SearchService.BookInfo> = persistentListOf(),

    /** 首页推荐书籍列表 - 来自首页服务的推荐书籍 */
    val homeRecommendBooks: ImmutableList<HomeService.HomeBook> = persistentListOf(),

    /** 推荐书籍加载状态 */
    val recommendLoading: Boolean = false,

    /** 是否还有更多分类推荐数据可加载 */
    val hasMoreRecommend: Boolean = true,

    /** 当前分类推荐数据的页码 */
    val recommendPage: Int = 1,

    /** 分类推荐数据的总页数 */
    val totalRecommendPages: Int = 1,

    // === 首页推荐分页状态 ===
    /** 首页推荐数据加载状态 */
    val homeRecommendLoading: Boolean = false,

    /** 是否还有更多首页推荐数据可加载 */
    val hasMoreHomeRecommend: Boolean = true,

    /** 当前首页推荐数据的页码 */
    val homeRecommendPage: Int = 1,

    // === 显示模式控制 ===
    /** 当前显示模式 - true=推荐模式（显示首页推荐），false=分类模式（显示分类搜索结果） */
    val isRecommendMode: Boolean = true
) : MviState

/**
 * Home模块的Effect定义
 * 替换现有HomeEvent，处理一次性副作用
 */
@Stable
sealed class HomeEffect : MviEffect {
    /** 导航到书籍页面 - 触发页面跳转到书籍阅读 */
    data class NavigateToBook(val bookId: Long) : HomeEffect()

    /** 导航到分类页面 - 触发页面跳转到指定分类 */
    data class NavigateToCategory(val categoryId: Long) : HomeEffect()

    /** 导航到搜索页面 - 触发页面跳转到搜索功能 */
    data class NavigateToSearch(val query: String = "") : HomeEffect()

    /** 导航到分类总览页面 - 触发页面跳转到分类列表 */
    data object NavigateToCategoryPage : HomeEffect()

    /** 显示Toast提示 - 向用户显示简短的提示信息 */
    data class ShowToast(val message: String) : HomeEffect()

    /** 导航到书籍详情页 - 触发页面跳转到书籍详细信息 */
    data class NavigateToBookDetail(val bookId: Long) : HomeEffect()

    /** 导航到完整榜单页面 - 触发页面跳转到榜单详情 */
    data class NavigateToFullRanking(val rankType: String) : HomeEffect()

    /** 发送数据到React Native */
    @Stable
    data class SendToReactNative(@Stable val data: ReactNativeData) : HomeEffect()
}

/**
 * Home模块的Reducer定义
 * 实现MviReducerWithEffect接口，处理所有Intent的状态转换逻辑
 */
class HomeReducer : MviReducerWithEffect<HomeIntent, HomeState, HomeEffect> {

    override fun reduce(
        currentState: HomeState,
        intent: HomeIntent
    ): ReduceResult<HomeState, HomeEffect> {
        return when (intent) {
            is HomeIntent.LoadInitialData -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null
                    )
                )
            }

            is HomeIntent.RefreshData -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isRefreshing = true,
                        error = null
                    )
                )
            }

            is HomeIntent.UpdateSearchQuery -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        searchQuery = intent.query
                    )
                )
            }

            is HomeIntent.SelectCategoryFilter -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        selectedCategoryFilter = HomeCategoryFilterSupport.normalizeSelectedFilter(intent.categoryName),
                        isRecommendMode = HomeCategoryFilterSupport.isHomeFilter(intent.categoryName),
                        recommendBooks = if (HomeCategoryFilterSupport.isHomeFilter(intent.categoryName)) persistentListOf() else currentState.recommendBooks,
                        recommendPage = 1
                    )
                )
            }

            is HomeIntent.SelectRankType -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        selectedRankType = intent.rankType,
                        rankLoading = true
                    )
                )
            }

            is HomeIntent.LoadMoreRecommend -> {
                if (currentState.isRecommendMode) {
                    ReduceResult(
                        newState = currentState.copy(
                            version = currentState.version + 1,
                            homeRecommendLoading = true
                        )
                    )
                } else {
                    ReduceResult(
                        newState = currentState.copy(
                            version = currentState.version + 1,
                            recommendLoading = true
                        )
                    )
                }
            }

            is HomeIntent.LoadMoreHomeRecommend -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        homeRecommendLoading = true,
                        homeRecommendPage = currentState.homeRecommendPage + 1
                    )
                )
            }

            is HomeIntent.NavigateToSearch -> {
                ReduceResult(
                    newState = currentState,
                    effect = HomeEffect.NavigateToSearch(intent.query)
                )
            }

            is HomeIntent.NavigateToBookDetail -> {
                ReduceResult(
                    newState = currentState,
                    effect = HomeEffect.NavigateToBookDetail(intent.bookId)
                )
            }

            is HomeIntent.NavigateToCategory -> {
                ReduceResult(
                    newState = currentState,
                    effect = HomeEffect.NavigateToCategory(intent.categoryId)
                )
            }

            is HomeIntent.NavigateToFullRanking -> {
                ReduceResult(
                    newState = currentState,
                    effect = HomeEffect.NavigateToFullRanking(intent.rankType)
                )
            }

            is HomeIntent.RestoreData -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1
                    )
                )
            }

            is HomeIntent.ClearError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        error = null
                    )
                )
            }

            is HomeIntent.RefreshComplete -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isRefreshing = false,
                        isLoading = false
                    )
                )
            }

            // === 数据加载成功/失败的Intent处理 ===
            is HomeIntent.CategoryFiltersLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        categoryFilters = HomeCategoryFilterSupport.normalizeFilters(intent.filters),
                        categoryFiltersLoading = false
                    )
                )
            }

            is HomeIntent.CategoryFiltersLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        categoryFiltersLoading = false,
                        isLoading = false,
                        isRefreshing = false,
                        error = intent.error
                    )
                )
            }

            is HomeIntent.HomeRecommendBooksLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        homeRecommendBooks = if (intent.isRefresh) {
                            intent.books
                        } else {
                            (currentState.homeRecommendBooks + intent.books).toImmutableList()
                        },
                        homeRecommendLoading = false,
                        hasMoreHomeRecommend = intent.hasMore,
                        homeRecommendPage = if (intent.isRefresh) {
                            1
                        } else {
                            currentState.homeRecommendPage + 1
                        },
                        isRefreshing = false,
                        isLoading = false
                    )
                )
            }

            is HomeIntent.HomeRecommendBooksLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        homeRecommendLoading = false,
                        isRefreshing = false,
                        isLoading = false,
                        error = intent.error
                    )
                )
            }

            is HomeIntent.CategoryRecommendBooksLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        recommendBooks = if (intent.isLoadMore) {
                            (currentState.recommendBooks + intent.books).toImmutableList()
                        } else {
                            intent.books
                        },
                        recommendLoading = false,
                        hasMoreRecommend = intent.hasMore,
                        totalRecommendPages = intent.totalPages,
                        recommendPage = if (intent.isLoadMore) {
                            currentState.recommendPage + 1
                        } else {
                            1
                        },
                        isRefreshing = false,
                        isLoading = false
                    )
                )
            }

            is HomeIntent.CategoryRecommendBooksLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        recommendLoading = false,
                        isRefreshing = false,
                        isLoading = false,
                        error = intent.error
                    )
                )
            }

            is HomeIntent.RankBooksLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        rankBooks = intent.books,
                        rankLoading = false
                    )
                )
            }

            is HomeIntent.RankBooksLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        rankLoading = false,
                        isLoading = false,
                        isRefreshing = false,
                        error = intent.error
                    )
                )
            }

            is HomeIntent.CategoriesLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        categories = intent.categories,
                        categoryLoading = false
                    )
                )
            }

            is HomeIntent.CategoriesLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        categoryLoading = false,
                        isLoading = false,
                        isRefreshing = false,
                        error = intent.error
                    )
                )
            }

            is HomeIntent.BooksLoadSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        carouselBooks = intent.carouselBooks,
                        hotBooks = intent.hotBooks,
                        newBooks = intent.newBooks,
                        vipBooks = intent.vipBooks,
                        booksLoading = false,
                        isLoading = false
                    )
                )
            }

            is HomeIntent.BooksLoadFailure -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        booksLoading = false,
                        isLoading = false,
                        error = intent.error
                    )
                )
            }
        }
    }
}
