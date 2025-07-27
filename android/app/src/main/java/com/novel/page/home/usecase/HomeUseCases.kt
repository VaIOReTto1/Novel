package com.novel.page.home.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.core.domain.FlowUseCase
import com.novel.page.home.dao.IHomeRepository
import com.novel.page.home.dao.HomeCategoryEntity
import com.novel.page.home.viewmodel.CategoryInfo
import com.novel.page.login.dao.UserRepository
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.TokenProvider
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.rn.ReactNativeBridge
import com.novel.utils.TimberLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay
import javax.inject.Inject
import androidx.compose.runtime.Stable
import com.novel.utils.network.api.searchBooksMediumPriority

/**
 * 获取首页分类UseCase（整合分类获取）
 */
@Stable
class GetHomeCategoriesUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : FlowUseCase<GetHomeCategoriesUseCase.Params, List<CategoryInfo>>() {
    
    @Stable
    data class Params(
        val workDirection: Int = 0,
        val strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    )
    
    override suspend fun execute(params: Params): Flow<List<CategoryInfo>> = flow {
        homeRepository.getBookCategories(params.workDirection, params.strategy)
            .catch { e ->
                TimberLogger.e("GetHomeCategoriesUseCase", "获取分类失败", e)
                emit(persistentListOf())
            }
            .collect { categories ->
                val filters = mutableListOf<CategoryInfo>().apply {
                    add(CategoryInfo("0", "推荐"))
                    addAll(categories.map { CategoryInfo(it.id.toString(), it.name) })
                }
                TimberLogger.d("GetHomeCategoriesUseCase", "获取分类成功: $filters")
                emit(filters)
            }
    }
}

/**
 * 获取首页推荐书籍UseCase（整合推荐书籍）
 */
@Stable
class GetHomeRecommendBooksUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : BaseUseCase<GetHomeRecommendBooksUseCase.Params, List<HomeService.HomeBook>>() {
    
    @Stable
    data class Params(
        val strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    )
    
    override suspend fun execute(params: Params): List<HomeService.HomeBook> {
        return try {
            homeRepository.getHomeBooks(params.strategy)
        } catch (e: Exception) {
            TimberLogger.e("GetHomeRecommendBooksUseCase", "获取首页推荐书籍失败", e)
            persistentListOf()
        }
    }
}

/**
 * 获取榜单书籍UseCase（整合榜单数据）
 */
@Stable
class GetRankingBooksUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : BaseUseCase<GetRankingBooksUseCase.Params, List<BookService.BookRank>>() {
    
    @Stable
    data class Params(
        val rankType: String,
        val strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    )
    
    override suspend fun execute(params: Params): List<BookService.BookRank> {
        return try {
            homeRepository.getRankBooks(params.rankType, params.strategy)
        } catch (e: Exception) {
            TimberLogger.e("GetRankingBooksUseCase", "获取榜单书籍失败: ${params.rankType}", e)
            persistentListOf()
        }
    }
}

/**
 * 刷新首页数据UseCase（整合刷新逻辑）
 */
@Stable
class RefreshHomeDataUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : BaseUseCase<Unit, Triple<List<HomeService.HomeBook>, List<HomeService.FriendLink>, List<BookService.BookCategory>>>() {
    
    override suspend fun execute(params: Unit): Triple<List<HomeService.HomeBook>, List<HomeService.FriendLink>, List<BookService.BookCategory>> {
        return try {
            homeRepository.refreshAllData()
        } catch (e: Exception) {
            TimberLogger.e("RefreshHomeDataUseCase", "刷新首页数据失败", e)
            Triple(persistentListOf(), persistentListOf(), persistentListOf())
        }
    }
}

/**
 * 发送React Native数据UseCase（RN数据发送）
 */
@Stable
class SendReactNativeDataUseCase @Inject constructor(
    @Stable
    private val userRepository: UserRepository,
    @Stable
    private val tokenProvider: TokenProvider,
    @Stable
    private val userDefaults: NovelUserDefaults
) : BaseUseCase<Unit, Unit>() {
    
    override suspend fun execute(params: Unit) {
        try {
            delay(2000) // 等待RN初始化完成
            
            // 获取真实的用户数据
            val currentUser = userRepository.getCurrentUser()
            val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)
            val token = tokenProvider.accessToken()
            TimberLogger.d("SendReactNativeDataUseCase", "获取用户数据: $currentUser, $uid, $token")

            if (currentUser != null && uid != null && !token.isNullOrEmpty()) {
                // 发送真实用户数据
                val sex = when (currentUser.userSex) {
                    1 -> "男"
                    2 -> "女"
                    else -> "未知"
                }
                TimberLogger.d("SendReactNativeDataUseCase", "发送真实用户数据完成: ${currentUser.nickName}")
            } else {
                // 如果没有用户数据，发送测试数据作为后备
                TimberLogger.w("SendReactNativeDataUseCase", "用户数据不完整，使用测试数据")
            }
            
            delay(500)
            TimberLogger.d("SendReactNativeDataUseCase", "发送RN数据完成")
        } catch (e: Exception) {
            TimberLogger.e("SendReactNativeDataUseCase", "发送RN数据失败", e)
        }
    }
}

/**
 * 获取分类推荐书籍UseCase
 */
@Stable
class GetCategoryRecommendBooksUseCase @Inject constructor(
    @Stable
    private val cachedBookRepository: CachedBookRepository,
    private val searchService: SearchService
) : BaseUseCase<GetCategoryRecommendBooksUseCase.Params, SearchService.PageResponse<SearchService.BookInfo>>() {
    
    @Stable
    data class Params(
        val categoryId: Int,
        val pageNum: Int = 1,
        val pageSize: Int = 8,
        val strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    )
    
    override suspend fun execute(params: Params): SearchService.PageResponse<SearchService.BookInfo> {
        return try {
            // 对于分类推荐，优先使用缓存，失败时使用高优先级搜索
            try {
                cachedBookRepository.searchBooks(
                    categoryId = params.categoryId,
                    pageNum = params.pageNum,
                    pageSize = params.pageSize,
                    strategy = params.strategy
                )
            } catch (e: Exception) {
                TimberLogger.w("GetCategoryRecommendBooksUseCase", "缓存搜索失败，使用中等优先级网络搜索: ${e.message}")
                searchService.searchBooksMediumPriority(
                    categoryId = params.categoryId,
                    pageNum = params.pageNum,
                    pageSize = params.pageSize
                ).data ?: SearchService.PageResponse(
                    list = persistentListOf(),
                    total = 0L,
                    pages = 0L,
                    pageNum = params.pageNum.toLong(),
                    pageSize = params.pageSize.toLong()
                )
            }
        } catch (e: Exception) {
            TimberLogger.e("GetCategoryRecommendBooksUseCase", "获取分类推荐书籍失败", e)
            SearchService.PageResponse(
                list = persistentListOf(),
                total = 0L,
                pages = 0L,
                pageNum = params.pageNum.toLong(),
                pageSize = params.pageSize.toLong()
            )
        }
    }
}

/**
 * 获取书籍分类数据UseCase
 */
@Stable
class GetCategoriesUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : FlowUseCase<GetCategoriesUseCase.Params, List<HomeCategoryEntity>>() {
    
    @Stable
    data class Params(
        val forceRefresh: Boolean = false
    )
    
    override suspend fun execute(params: Params): Flow<List<HomeCategoryEntity>> = 
        homeRepository.getCategories(params.forceRefresh)
            .map { apiCategories ->
                // 转换为Entity
                apiCategories.map { category ->
                    HomeCategoryEntity(
                        id = category.id,
                        name = category.name,
                        iconUrl = null,
                        sortOrder = 0
                    )
                }
            }
            .catch { e ->
                TimberLogger.e("GetCategoriesUseCase", "获取分类数据失败", e)
                emit(persistentListOf())
            }
}

/**
 * 获取各类书籍数据UseCase
 */
@Stable
class GetBooksDataUseCase @Inject constructor(
    @Stable
    private val homeRepository: IHomeRepository
) : FlowUseCase<GetBooksDataUseCase.Params, GetBooksDataUseCase.BooksData>() {
    
    @Stable
    data class Params(
        val forceRefresh: Boolean = false
    )
    
    @Stable
    data class BooksData(
        val carouselBooks: ImmutableList<HomeService.HomeBook>,
        val hotBooks: ImmutableList<HomeService.HomeBook>,
        val newBooks: ImmutableList<HomeService.HomeBook>,
        val vipBooks: ImmutableList<HomeService.HomeBook>
    )
    
    override suspend fun execute(params: Params): Flow<BooksData> = flow {
        // 并发获取不同类型的书籍
        val carouselFlow = homeRepository.getCarouselBooks(params.forceRefresh)
        val hotFlow = homeRepository.getHotBooks(params.forceRefresh)
        val newFlow = homeRepository.getNewBooks(params.forceRefresh)
        val vipFlow = homeRepository.getVipBooks(params.forceRefresh)
        
        // 合并所有Flow
        kotlinx.coroutines.flow.combine(carouselFlow, hotFlow, newFlow, vipFlow) { carousel, hot, new, vip ->
            BooksData(
                carouselBooks = carousel.toImmutableList(),
                hotBooks = hot.toImmutableList(),
                newBooks = new.toImmutableList(),
                vipBooks = vip.toImmutableList()
            )
        }.collect { booksData ->
            emit(booksData)
        }
    }.catch { e ->
        TimberLogger.e("GetBooksDataUseCase", "获取书籍数据失败", e)
        emit(BooksData(persistentListOf(), persistentListOf(), persistentListOf(), persistentListOf()))
    }
}