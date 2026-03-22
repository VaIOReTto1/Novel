package com.novel.page.search.repository

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.api.front.SearchService
import com.novel.page.search.component.SearchRankingItem
import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.page.search.viewmodel.FilterState
import com.novel.page.search.viewmodel.SearchTriggerSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import javax.inject.Singleton
import com.novel.utils.network.cache.NetworkCacheManager
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.cache.searchBooksCached
import com.novel.utils.network.cache.onSuccess
import com.novel.utils.network.cache.onError
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.getNewestRankBooksHighPriority
import com.novel.utils.network.api.getUpdateRankBooksHighPriority
import com.novel.utils.network.api.getVisitRankBooksHighPriority

/**
 * 榜单数据结构
 * 
 * 包含三种类型榜单的完整数据
 */
@Stable
data class RankingData(
    /** 小说榜单列表 */
    val novelRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    /** 短剧榜单列表 */
    val dramaRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    /** 新书榜单列表 */
    val newBookRanking: ImmutableList<SearchRankingItem> = persistentListOf()
)

/**
 * 书籍信息分页响应DTO
 * 
 * 标准分页数据结构，包含分页信息和书籍列表
 */
@Stable
data class PageRespDtoBookInfoRespDto(
    /** 当前页码 */
    val pageNum: Long? = null,
    /** 每页大小 */
    val pageSize: Long? = null,
    /** 总记录数 */
    val total: Long? = null,
    /** 书籍信息列表 */
    val list: ImmutableList<BookInfoRespDto> = persistentListOf(),
    /** 总页数 */
    val pages: Long? = null
)

/**
 * 缓存的搜索结果
 */
@Stable
data class CachedSearchResult(
    val params: SearchParams,
    val result: PageRespDtoBookInfoRespDto,
    val cacheTime: Long
)

/**
 * 搜索数据仓库
 * 
 * 核心功能：
 * - 搜索历史管理：本地存储用户搜索记录，支持增删查改
 * - 榜单数据获取：集成缓存策略的多类型榜单数据
 * - 书籍搜索：支持多条件筛选的书籍搜索功能
 * - 状态管理：搜索历史展开状态的持久化
 * - 搜索结果缓存：智能缓存搜索结果，提升用户体验
 * 
 * 技术特点：
 * - 单例模式设计，全局数据一致性
 * - 集成缓存管理，提升用户体验
 * - JSON序列化存储，数据结构灵活
 * - 完善的异常处理和日志记录
 * - 依赖注入，便于测试和维护
 * - 搜索结果智能缓存与过期清理
 * 
 * 存储机制：
 * - 使用NovelUserDefaults进行本地配置存储
 * - 搜索历史限制为10条，自动清理旧记录
 * - 支持历史展开状态的保存和恢复
 * - 搜索结果缓存限制为20条，5分钟过期
 */
@Singleton
@Stable
class SearchRepository @Inject constructor(
    /** 搜索服务，提供网络搜索功能 */
    @Stable
    private val searchService: SearchService,
    /** 搜索历史存储 */
    @Stable
    private val searchHistoryStore: SearchHistoryStore,
    /** 网络缓存管理器 */
    @Stable
    private val cacheManager: NetworkCacheManager,
    /** 缓存书籍仓库，提供带缓存的数据访问 */
    @Stable
    private val cachedBookRepository: CachedBookRepository,
    /** 书籍服务，提供高优先级网络请求 */
    @Stable
    private val bookService: BookService
) {
    
    companion object {
        private const val TAG = "SearchRepository"
    }

    private val searchResultCacheStore = SearchResultCacheStore()
    private val searchRankingRepository = SearchRankingRepository()
    private val searchQueryRepository = SearchQueryRepository()
    
    // region 搜索结果缓存管理
    
    /**
     * 生成缓存键
     */
    private fun generateCacheKey(params: SearchParams): String {
        return "${params.query}-${params.categoryId}-${params.filters.hashCode()}-${params.page}"
    }
    
    /**
     * 检查缓存是否有效（5分钟内）
     */
    private fun isCacheValid(cached: CachedSearchResult): Boolean {
        return (System.currentTimeMillis() - cached.cacheTime) < (5 * 60 * 1000L)
    }
    
    /**
     * 获取缓存的搜索结果
     */
    fun getCachedSearchResult(params: SearchParams): CachedSearchResult? {
        val cached = searchResultCacheStore.getCachedSearchResult(params)
        cached?.let {
            TimberLogger.d(TAG, "返回缓存的搜索结果: ${generateCacheKey(params)}")
        }
        return cached
    }
    
    /**
     * 缓存搜索结果
     */
    fun cacheSearchResult(params: SearchParams, books: List<BookInfoRespDto>, totalResults: Int, hasMore: Boolean) {
        searchResultCacheStore.cacheSearchResult(params, books, totalResults, hasMore)
        TimberLogger.d(TAG, "缓存搜索结果: ${generateCacheKey(params)}")
    }
    
    /**
     * 清理过期缓存
     */
    private fun cleanExpiredCache() {
        return
    }
    
    /**
     * 清空搜索结果缓存
     */
    fun clearSearchResultCache() {
        searchResultCacheStore.clearSearchResultCache()
        TimberLogger.d(TAG, "搜索结果缓存已清空")
    }
    
    /**
     * 检查搜索结果缓存是否可用
     */
    fun isSearchResultCacheAvailable(params: SearchParams): Boolean {
        return searchResultCacheStore.isSearchResultCacheAvailable(params)
    }
    
    // endregion
    
    // region 搜索历史管理
    
    /**
     * 获取搜索历史
     */
    fun getSearchHistory(): List<String> = searchHistoryStore.getSearchHistory()
    
    /**
     * 添加搜索历史
     */
    fun addSearchHistory(keyword: String) = searchHistoryStore.addSearchHistory(keyword)
    
    /**
     * 清空搜索历史
     */
    fun clearSearchHistory() = searchHistoryStore.clearSearchHistory()
    
    /**
     * 获取历史展开状态
     */
    fun getHistoryExpansionState(): Boolean = searchHistoryStore.getHistoryExpansionState()
    
    /**
     * 保存历史展开状态
     */
    fun saveHistoryExpansionState(isExpanded: Boolean) =
        searchHistoryStore.saveHistoryExpansionState(isExpanded)
    
    // endregion
    
    // region 榜单数据获取
    
    /**
     * 获取推荐榜单数据 - 使用缓存优先策略
     */
    private suspend fun getNovelRanking(): ImmutableList<SearchRankingItem> {
        return searchRankingRepository.getNovelRanking(
            primary = {
                TimberLogger.d(TAG, "获取推荐榜单数据")
                cachedBookRepository.getVisitRankBooks(CacheStrategy.CACHE_FIRST).map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
            fallback = {
                bookService.getVisitRankBooksHighPriority().data.orEmpty().map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
        )
    }
    
    /**
     * 获取热搜短剧榜数据 - 使用缓存优先策略
     */
    private suspend fun getDramaRanking(): ImmutableList<SearchRankingItem> {
        return searchRankingRepository.getDramaRanking(
            primary = {
                TimberLogger.d(TAG, "获取热搜短剧榜数据")
                cachedBookRepository.getUpdateRankBooks(CacheStrategy.CACHE_FIRST).map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
            fallback = {
                bookService.getUpdateRankBooksHighPriority().data.orEmpty().map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
        )
    }
    
    /**
     * 获取新书榜单数据 - 使用缓存优先策略
     */
    private suspend fun getNewBookRanking(): ImmutableList<SearchRankingItem> {
        return searchRankingRepository.getNewBookRanking(
            primary = {
                TimberLogger.d(TAG, "获取新书榜单数据")
                cachedBookRepository.getNewestRankBooks(CacheStrategy.CACHE_FIRST).map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
            fallback = {
                bookService.getNewestRankBooksHighPriority().data.orEmpty().map { book ->
                    SearchRankingSource(book.id, book.bookName, book.authorName)
                }
            },
        )
    }
    
    /**
     * 获取所有榜单数据
     */
    suspend fun getAllRankingData(): RankingData {
        return try {
            // 并行获取所有榜单数据
            val novelRanking = getNovelRanking()
            val dramaRanking = getDramaRanking()
            val newBookRanking = getNewBookRanking()
            
            RankingData(
                novelRanking = novelRanking,
                dramaRanking = dramaRanking,
                newBookRanking = newBookRanking
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取所有榜单数据失败", e)
            RankingData()
        }
    }
    
    // endregion
    
    // region 搜索功能
    
    /**
     * 搜索书籍 - 带缓存管理的搜索实现
     */
    suspend fun searchBooksWithCache(
        params: SearchParams,
        strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    ): PageRespDtoBookInfoRespDto? {
        return searchQueryRepository.searchBooksWithCache(
            params = params,
            getCachedSearchResult = ::getCachedSearchResult,
            cacheSearchResult = ::cacheSearchResult,
            executeRemoteSearch = { request ->
                searchService.searchBooksCached(
                    keyword = request.keyword,
                    workDirection = request.workDirection,
                    categoryId = request.categoryId,
                    isVip = request.isVip,
                    bookStatus = request.bookStatus,
                    wordCountMin = request.wordCountMin,
                    wordCountMax = request.wordCountMax,
                    updateTimeMin = request.updateTimeMin,
                    sort = request.sort,
                    pageNum = request.pageNum,
                    pageSize = request.pageSize,
                    cacheManager = cacheManager,
                    strategy = request.strategy,
                    onCacheUpdate = {
                        TimberLogger.d(TAG, "搜索结果缓存已更新: keyword=${request.keyword}")
                    }
                ).onSuccess { _, fromCache ->
                    TimberLogger.d(TAG, "搜索成功，来源: ${if (fromCache) "缓存" else "网络"}，关键词: ${request.keyword}")
                }.onError { error, _ ->
                    TimberLogger.e(TAG, "搜索失败，关键词: ${request.keyword}", error)
                }.let { result ->
                    when (result) {
                        is com.novel.utils.network.cache.CacheResult.Success -> result.data
                        is com.novel.utils.network.cache.CacheResult.Error -> result.cachedData
                    }
                }
            },
        )
    }
    
    /**
     * 搜索书籍 - 缓存优先策略
     */
    suspend fun searchBooks(
        keyword: String? = null,
        workDirection: Int? = null,
        categoryId: Int? = null,
        isVip: Int? = null,
        bookStatus: Int? = null,
        wordCountMin: Int? = null,
        wordCountMax: Int? = null,
        updateTimeMin: String? = null,
        sort: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 20,
        strategy: CacheStrategy = CacheStrategy.CACHE_FIRST
    ): SearchService.BookSearchResponse? {
        return searchQueryRepository.searchBooks(
            keyword = keyword,
            workDirection = workDirection,
            categoryId = categoryId,
            isVip = isVip,
            bookStatus = bookStatus,
            wordCountMin = wordCountMin,
            wordCountMax = wordCountMax,
            updateTimeMin = updateTimeMin,
            sort = sort,
            pageNum = pageNum,
            pageSize = pageSize,
            strategy = strategy,
            executeRemoteSearch = { request ->
                searchService.searchBooksCached(
                    keyword = request.keyword,
                    workDirection = request.workDirection,
                    categoryId = request.categoryId,
                    isVip = request.isVip,
                    bookStatus = request.bookStatus,
                    wordCountMin = request.wordCountMin,
                    wordCountMax = request.wordCountMax,
                    updateTimeMin = request.updateTimeMin,
                    sort = request.sort,
                    pageNum = request.pageNum,
                    pageSize = request.pageSize,
                    cacheManager = cacheManager,
                    strategy = request.strategy,
                    onCacheUpdate = {
                        TimberLogger.d(TAG, "搜索结果缓存已更新: keyword=${request.keyword}")
                    }
                ).onSuccess { _, fromCache ->
                    TimberLogger.d(TAG, "搜索成功，来源: ${if (fromCache) "缓存" else "网络"}，关键词: ${request.keyword}")
                }.onError { error, _ ->
                    TimberLogger.e(TAG, "搜索失败，关键词: ${request.keyword}", error)
                }.let { result ->
                    when (result) {
                        is com.novel.utils.network.cache.CacheResult.Success -> result.data
                        is com.novel.utils.network.cache.CacheResult.Error -> result.cachedData
                    }
                }
            },
        )
    }
    
    /**
     * 清理搜索缓存
     */
    fun clearSearchCache() {
        try {
            clearSearchResultCache()
            TimberLogger.d(TAG, "搜索缓存已清理")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理搜索缓存失败", e)
        }
    }
    
    /**
     * 强制刷新搜索结果（绕过缓存）
     */
    suspend fun refreshSearchResults(
        keyword: String,
        workDirection: Int? = null,
        categoryId: Int? = null,
        isVip: Int? = null,
        bookStatus: Int? = null,
        wordCountMin: Int? = null,
        wordCountMax: Int? = null,
        updateTimeMin: String? = null,
        sort: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 20
    ): SearchService.BookSearchResponse? {
        return searchBooks(
            keyword = keyword,
            workDirection = workDirection,
            categoryId = categoryId,
            isVip = isVip,
            bookStatus = bookStatus,
            wordCountMin = wordCountMin,
            wordCountMax = wordCountMax,
            updateTimeMin = updateTimeMin,
            sort = sort,
            pageNum = pageNum,
            pageSize = pageSize,
            strategy = CacheStrategy.NETWORK_ONLY
        )
    }
    
    /**
     * 检查搜索缓存是否可用
     */
    suspend fun isSearchCacheAvailable(keyword: String): Boolean {
        return try {
            // 生成缓存键（简化版，实际应该与扩展函数中的逻辑一致）
            val paramsHash = keyword.hashCode().toString()
            cacheManager.isCacheExists("search_books_$paramsHash")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "检查搜索缓存状态失败", e)
            false
        }
    }
    
    // endregion
}
