package com.novel.page.home.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.core.domain.ComposeUseCase
import com.novel.page.home.dao.IHomeRepository
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.toEntity
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.home.viewmodel.HomeCategoryFilterSupport
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.TimberLogger
import com.novel.page.login.dao.UserRepository
import com.novel.utils.network.TokenProvider
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import androidx.compose.runtime.Stable

/**
 * Home模块组合UseCase
 * 
 * 处理复杂的业务逻辑组合，整合所有Home相关的UseCase
 */
@Stable
open class HomeCompositeUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository,
    @Stable
    private val cachedBookRepository: CachedBookRepository,
    @Stable
    private val searchService: SearchService,
    @Stable
    private val userRepository: UserRepository,
    @Stable
    private val tokenProvider: TokenProvider,
    @Stable
    private val userDefaults: NovelUserDefaults
) : BaseUseCase<HomeCompositeUseCase.Params, HomeCompositeUseCase.Result>() {
    
    companion object {
        private const val TAG = "HomeCompositeUseCase"
        private const val RECOMMEND_PAGE_SIZE = 8
    }
    
    @Stable
    data class Params(
        val loadInitialData: Boolean = false,
        val refreshData: Boolean = false,
        val categoryFilter: String? = null,
        val rankType: String? = null,
        val loadMoreRecommend: Boolean = false,
        val categoryFilters: ImmutableList<CategoryInfo> = persistentListOf(),
        val currentPage: Int = 1
    )
    
    @Stable
    data class Result(
        val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),
        val categoryFilters: ImmutableList<CategoryInfo> = persistentListOf(),
        val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
        val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
        val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
        val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
        val rankBooks: ImmutableList<BookService.BookRank> = persistentListOf(),
        val recommendBooks: ImmutableList<SearchService.BookInfo> = persistentListOf(),
        val homeRecommendBooks: ImmutableList<HomeService.HomeBook> = persistentListOf(),
        val homeRecommendUsedNetworkFallback: Boolean = false,
        val hasMoreRecommend: Boolean = true,
        val totalPages: Int = 1,
        val isSuccess: Boolean = true,
        val errorMessage: String? = null
    )
    
    // 预初始化内部UseCase实例，使用稳定接口避免Compose重组问题
    @Stable
    private val getHomeCategoriesUseCase: GetHomeCategoriesUseCase = 
        GetHomeCategoriesUseCase(homeRepository)
    
    @Stable
    private val getHomeRecommendBooksUseCase: GetHomeRecommendBooksUseCase = 
        GetHomeRecommendBooksUseCase(homeRepository)
    
    @Stable
    private val getRankingBooksUseCase: GetRankingBooksUseCase = 
        GetRankingBooksUseCase(homeRepository)
    
    @Stable
    private val refreshHomeDataUseCase: RefreshHomeDataUseCase = 
        RefreshHomeDataUseCase(homeRepository)
    
    @Stable
    private val sendReactNativeDataUseCase: SendReactNativeDataUseCase = 
        SendReactNativeDataUseCase(userRepository, tokenProvider, userDefaults)
    
    @Stable
    private val getCategoryRecommendBooksUseCase: GetCategoryRecommendBooksUseCase = 
        GetCategoryRecommendBooksUseCase(cachedBookRepository, searchService)
    
    @Stable
    private val getCategoriesUseCase: GetCategoriesUseCase = 
        GetCategoriesUseCase(homeRepository)
    
    @Stable
    private val getBooksDataUseCase: GetBooksDataUseCase = 
        GetBooksDataUseCase(homeRepository)
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "开始执行组合操作: $params")
        
        return try {
            when {
                params.loadInitialData -> loadInitialData()
                params.refreshData -> refreshAllData()
                params.categoryFilter != null -> loadCategoryData(params.categoryFilter, params.currentPage, params.categoryFilters)
                params.rankType != null -> loadRankingData(params.rankType)
                params.loadMoreRecommend -> loadMoreRecommendData(params.currentPage)
                else -> Result(isSuccess = false, errorMessage = "未知操作类型")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "组合操作执行失败", e)
            Result(isSuccess = false, errorMessage = e.message ?: "未知错误")
        }
    }
    
    /**
     * 加载初始数据
     */
    private suspend fun loadInitialData(): Result {
        TimberLogger.d(TAG, "加载初始数据")
        
        return try {
            // 使用并发方式获取数据，但对Flow类型的数据使用first()确保获取到实际数据
            val categoryFilters = try {
                getHomeCategoriesUseCase(
                    GetHomeCategoriesUseCase.Params()
                ).first() // 使用first()确保获取到数据
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取分类筛选器失败", e)
                persistentListOf()
            }
            
            val categories = try {
                getCategoriesUseCase(
                    GetCategoriesUseCase.Params(forceRefresh = false)
                ).first() // 使用first()确保获取到数据
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取分类数据失败", e)
                persistentListOf()
            }

            val resolvedCategoryFilters = HomeCategoryFilterSupport.normalizeFilters(
                categoryFilters
                    .takeUnless { isMockOnlyCategoryFilters(it) }
                    ?: categories.map { CategoryInfo(it.id.toString(), it.name) }
            )

            val booksData = try {
                getBooksDataUseCase(
                    GetBooksDataUseCase.Params(forceRefresh = false)
                ).first() // 使用first()确保获取到数据
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取书籍数据失败", e)
                GetBooksDataUseCase.BooksData(persistentListOf(), persistentListOf(), persistentListOf(), persistentListOf())
            }
            
            val rankBooksResult = getRankingBooksUseCase(
                GetRankingBooksUseCase.Params("点击榜") // 使用字符串常量，避免依赖具体实现类
            )
            
            val cacheFirstHomeRecommendResult = getHomeRecommendBooksUseCase(
                GetHomeRecommendBooksUseCase.Params()
            )
            val homeRecommendUsedNetworkFallback = cacheFirstHomeRecommendResult.isEmpty()
            val homeRecommendResult = if (homeRecommendUsedNetworkFallback) {
                getHomeRecommendBooksUseCase(
                    GetHomeRecommendBooksUseCase.Params(strategy = CacheStrategy.NETWORK_ONLY)
                )
            } else {
                cacheFirstHomeRecommendResult
            }
            
            // 转换书籍数据为Entity
            val carouselBooks = booksData.carouselBooks.map { it.toEntity("carousel") }.toImmutableList()
            val hotBooks = booksData.hotBooks.map { it.toEntity("hot") }.toImmutableList()
            val newBooks = booksData.newBooks.map { it.toEntity("new") }.toImmutableList()
            val vipBooks = booksData.vipBooks.map { it.toEntity("vip") }.toImmutableList()
            
            TimberLogger.d(TAG, "初始数据加载完成 - 分类筛选器数量: ${categoryFilters.size}, 分类数量: ${categories.size}")
            
            Result(
                categories = categories.toImmutableList(),
                categoryFilters = resolvedCategoryFilters,
                carouselBooks = carouselBooks,
                hotBooks = hotBooks,
                newBooks = newBooks,
                vipBooks = vipBooks,
                rankBooks = rankBooksResult.toImmutableList(),
                homeRecommendBooks = homeRecommendResult.toImmutableList(),
                homeRecommendUsedNetworkFallback = homeRecommendUsedNetworkFallback,
                isSuccess = true
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载初始数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 刷新所有数据
     */
    private fun isMockOnlyCategoryFilters(categoryFilters: List<CategoryInfo>): Boolean {
        return categoryFilters.size == 1 &&
            categoryFilters.first().id == HomeCategoryFilterSupport.HOME_FILTER_ID &&
            HomeCategoryFilterSupport.isHomeFilter(categoryFilters.first().name)
    }

    private suspend fun refreshAllData(): Result {
        TimberLogger.d(TAG, "刷新所有数据")
        
        return try {
            // 强制刷新
            refreshHomeDataUseCase(Unit)
            
            // 重新加载数据
            loadInitialData()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "刷新数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 加载分类数据
     */
    private suspend fun loadCategoryData(categoryName: String, page: Int, categoryFilters: ImmutableList<CategoryInfo>): Result {
        TimberLogger.d(TAG, "加载分类数据: categoryName=$categoryName, page=$page")
        
        return try {
            if (HomeCategoryFilterSupport.isHomeFilter(categoryName)) {
                // 加载首页推荐
                val homeRecommendBooks = getHomeRecommendBooksUseCase(
                    GetHomeRecommendBooksUseCase.Params()
                )
                
                Result(
                    homeRecommendBooks = homeRecommendBooks.toImmutableList(),
                    hasMoreRecommend = homeRecommendBooks.size >= RECOMMEND_PAGE_SIZE,
                    isSuccess = true
                )
            } else {
                // 加载分类推荐 - 修复分类ID映射
                val categoryId = getCurrentCategoryId(categoryName, categoryFilters)
                TimberLogger.d(TAG, "分类名称映射: $categoryName -> $categoryId")
                
                val booksData = getCategoryRecommendBooksUseCase(
                    GetCategoryRecommendBooksUseCase.Params(
                        categoryId = categoryId,
                        pageNum = page,
                        pageSize = RECOMMEND_PAGE_SIZE
                    )
                )
                
                Result(
                    recommendBooks = booksData.list.toImmutableList(),
                    hasMoreRecommend = booksData.list.size >= RECOMMEND_PAGE_SIZE,
                    totalPages = booksData.pages.toInt(),
                    isSuccess = true
                )
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载分类数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 根据分类名称获取分类ID
     */
    /**
     * 获取当前分类ID
     */
    private fun getCurrentCategoryId(categoryName: String, categoryFilters: ImmutableList<CategoryInfo>): Int {

        // 打印调试信息
        TimberLogger.d(TAG, "获取分类ID - 分类名称: $categoryName")
        TimberLogger.d(TAG, "可用分类筛选器: ${categoryFilters.map { "${it.name}(${it.id})" }}")

        // 特殊处理推荐模式
        if (HomeCategoryFilterSupport.isHomeFilter(categoryName)) {
            TimberLogger.d(TAG, "推荐模式，返回categoryId: 0")
            return 0
        }

        // 查找对应的分类ID
        val normalizedCategoryName = HomeCategoryFilterSupport.normalizeSelectedFilter(categoryName)
        val categoryInfo = categoryFilters.find {
            HomeCategoryFilterSupport.normalizeSelectedFilter(it.name) == normalizedCategoryName
        }
        val categoryId = categoryInfo?.id?.toIntOrNull() ?: run {
            // 如果找不到，尝试用分类名称创建一个映射
            val mappedId = when (categoryName) {
                "玄幻奇幻" -> 1
                "武侠仙侠" -> 2
                "都市言情" -> 3
                "历史军情" -> 4
                "科幻灵异" -> 5
                "网游竞技" -> 6
                else -> 1 // 默认返回1
            }
            TimberLogger.w(TAG, "未找到分类 '$categoryName' 的ID，使用映射值: $mappedId")
            mappedId
        }

        TimberLogger.d(TAG, "分类 '$categoryName' 对应ID: $categoryId")
        return categoryId
    }
    
    /**
     * 加载榜单数据
     */
    private suspend fun loadRankingData(rankType: String): Result {
        TimberLogger.d(TAG, "加载榜单数据: rankType=$rankType")
        
        return try {
            val rankBooks = getRankingBooksUseCase(
                GetRankingBooksUseCase.Params(rankType)
            )
            
            Result(
                rankBooks = rankBooks.toImmutableList(),
                isSuccess = true
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载榜单数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 加载更多推荐数据
     */
    private suspend fun loadMoreRecommendData(page: Int): Result {
        TimberLogger.d(TAG, "加载更多推荐数据: page=$page")
        
        return try {
            // 获取新的首页推荐数据（强制从网络获取以确保数据更新）
            val homeRecommendBooks = getHomeRecommendBooksUseCase(
                GetHomeRecommendBooksUseCase.Params(strategy = CacheStrategy.NETWORK_ONLY)
            )
            
            // 计算分页数据
            val startIndex = (page - 1) * RECOMMEND_PAGE_SIZE
            val endIndex = startIndex + RECOMMEND_PAGE_SIZE
            val pagedBooks = homeRecommendBooks.drop(startIndex).take(RECOMMEND_PAGE_SIZE)
            
            TimberLogger.d(TAG, "加载更多推荐数据完成 - 页码: $page, 获取数量: ${pagedBooks.size}, 总数据量: ${homeRecommendBooks.size}")
            
            Result(
                homeRecommendBooks = pagedBooks.toImmutableList(),
                hasMoreRecommend = endIndex < homeRecommendBooks.size,
                isSuccess = true
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载更多推荐数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 加载更多分类推荐数据
     */
    private suspend fun loadMoreCategoryData(categoryId: Int, page: Int): Result {
        TimberLogger.d(TAG, "加载更多分类推荐数据: categoryId=$categoryId, page=$page")
        
        return try {
            val booksData = getCategoryRecommendBooksUseCase(
                GetCategoryRecommendBooksUseCase.Params(
                    categoryId = categoryId,
                    pageNum = page,
                    pageSize = RECOMMEND_PAGE_SIZE,
                    strategy = CacheStrategy.CACHE_FIRST
                )
            )
            
            TimberLogger.d(TAG, "加载更多分类推荐数据完成 - 分类ID: $categoryId, 页码: $page, 获取数量: ${booksData.list.size}")
            
            Result(
                recommendBooks = booksData.list.toImmutableList(),
                hasMoreRecommend = booksData.list.size >= RECOMMEND_PAGE_SIZE,
                totalPages = booksData.pages.toInt(),
                isSuccess = true
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载更多分类推荐数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
}

/**
 * Home模块状态检查UseCase
 * 
 * 检查Home模块的各种状态
 */
class HomeStatusCheckUseCase @Inject constructor(
    private val homeRepository: IHomeRepository
) : BaseUseCase<HomeStatusCheckUseCase.Params, HomeStatusCheckUseCase.Result>() {
    
    companion object {
        private const val TAG = "HomeStatusCheckUseCase"
    }
    
    data class Params(
        val checkCategories: Boolean = true,
        val checkRecommend: Boolean = true,
        val checkRanking: Boolean = true
    )
    
    data class Result(
        val hasCategoriesData: Boolean,
        val hasRecommendData: Boolean,
        val hasRankingData: Boolean
    )
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "检查Home模块状态")
        
        return try {
            var hasCategoriesData = true
            var hasRecommendData = true
            var hasRankingData = true
            
            if (params.checkCategories) {
                // TODO: 检查分类数据状态
            }
            
            if (params.checkRecommend) {
                // TODO: 检查推荐数据状态
            }
            
            if (params.checkRanking) {
                // TODO: 检查榜单数据状态
            }
            
            Result(
                hasCategoriesData = hasCategoriesData,
                hasRecommendData = hasRecommendData,
                hasRankingData = hasRankingData
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "状态检查失败", e)
            Result(
                hasCategoriesData = false,
                hasRecommendData = false,
                hasRankingData = false
            )
        }
    }
}
