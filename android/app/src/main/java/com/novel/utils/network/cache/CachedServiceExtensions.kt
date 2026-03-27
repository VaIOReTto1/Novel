package com.novel.utils.network.cache

import com.google.gson.reflect.TypeToken
import com.novel.core.StableThrowable
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.NewsService
import com.novel.utils.network.api.front.user.UserService
import java.util.concurrent.TimeUnit

/**
 * 缓存键生成器
 * 
 * 为不同类型的数据生成唯一的缓存键
 * 采用命名空间设计，避免键冲突
 */
object CacheKeys {
    /** 书籍信息缓存键 */
    fun bookInfo(bookId: Long) = "book_info_$bookId"
    /** 书籍章节列表缓存键 */
    fun bookChapters(bookId: Long) = "book_chapters_$bookId"
    /** 章节内容缓存键 */
    fun bookContent(chapterId: Long) = "book_content_$chapterId"
    /** 访问排行榜缓存键 */
    const val visitRankBooks = "visit_rank_books"
    /** 更新排行榜缓存键 */
    const val updateRankBooks = "update_rank_books"
    /** 新书排行榜缓存键 */
    const val newestRankBooks = "newest_rank_books"
    /** 书籍分类缓存键 */
    fun bookCategories(workDirection: Int) = "book_categories_$workDirection"
    /** 搜索结果缓存键 */
    fun searchBooks(params: String) = "search_books_$params"
    /** 首页推荐缓存键 */
    const val homeBooks = "home_books"
    /** 友情链接缓存键 */
    const val friendLinks = "friend_links"
    /** 最新资讯缓存键 */
    const val latestNews = "latest_news"
    /** 资讯详情缓存键 */  
    fun newsInfo(newsId: Long) = "news_info_$newsId"
    /** 用户信息缓存键 */
    const val userInfo = "user_info"
    /** 用户评论缓存键 */
    fun userComments(pageNum: Int, pageSize: Int) = "user_comments_${pageNum}_$pageSize"
}

/**
 * 常用缓存配置预设
 * 
 * 根据数据特点定义不同的缓存时长：
 * - SHORT_CACHE: 适用于实时性要求高的数据
 * - MEDIUM_CACHE: 适用于一般业务数据
 * - LONG_CACHE: 适用于相对稳定的数据
 * - EXTRA_LONG_CACHE: 适用于基础配置数据
 */
object CacheConfigs {
    /** 短期缓存：5分钟，适用于排行榜等实时数据，刷新阈值20% */
    val SHORT_CACHE = CacheConfig(
        maxAge = TimeUnit.MINUTES.toMillis(5),
        refreshThreshold = 0.2
    )
    /** 中期缓存：30分钟，适用于书籍信息等业务数据，刷新阈值30% */
    val MEDIUM_CACHE = CacheConfig(
        maxAge = TimeUnit.MINUTES.toMillis(30),
        refreshThreshold = 0.3
    )
    /** 长期缓存：2小时，适用于章节内容等稳定数据，刷新阈值50% */
    val LONG_CACHE = CacheConfig(
        maxAge = TimeUnit.HOURS.toMillis(2),
        refreshThreshold = 0.5
    )
    /** 超长期缓存：24小时，适用于分类等基础数据，刷新阈值70% */
    val EXTRA_LONG_CACHE = CacheConfig(
        maxAge = TimeUnit.HOURS.toMillis(24),
        refreshThreshold = 0.7
    )
}

/**
 * BookService 缓存扩展函数
 * 
 * 为BookService添加缓存功能，提升用户体验和应用性能
 * 所有扩展函数都支持不同的缓存策略和更新回调
 */

/**
 * 获取书籍详情（带缓存）
 * @param bookId 书籍ID
 * @param cacheManager 缓存管理器
 * @param strategy 缓存策略，默认缓存优先
 * @param onCacheUpdate 缓存更新回调
 */
suspend fun BookService.getBookByIdCached(
    bookId: Long,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookInfoResponse) -> Unit)? = null
): CacheResult<BookService.BookInfoResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.bookInfo(bookId),
        config = CacheConfigs.MEDIUM_CACHE,
        networkCall = { getBookByIdBlocking(bookId) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookInfoResponse>() {}
    )
}

suspend fun BookService.getBookChaptersCached(
    bookId: Long,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookChapterResponse) -> Unit)? = null
): CacheResult<BookService.BookChapterResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.bookChapters(bookId),
        config = CacheConfigs.MEDIUM_CACHE,
        networkCall = { getBookChaptersBlocking(bookId) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookChapterResponse>() {}
    )
}

suspend fun BookService.getBookContentCached(
    chapterId: Long,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookContentResponse) -> Unit)? = null
): CacheResult<BookService.BookContentResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.bookContent(chapterId),
        config = CacheConfigs.LONG_CACHE, // 章节内容缓存时间长一些
        networkCall = { getBookContentBlocking(chapterId) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookContentResponse>() {}
    )
}

suspend fun BookService.getVisitRankBooksCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookRankResponse) -> Unit)? = null
): CacheResult<BookService.BookRankResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.visitRankBooks,
        config = CacheConfigs.SHORT_CACHE, // 排行榜更新频繁，缓存时间短
        networkCall = { getVisitRankBooksBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookRankResponse>() {}
    )
}

suspend fun BookService.getUpdateRankBooksCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookRankResponse) -> Unit)? = null
): CacheResult<BookService.BookRankResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.updateRankBooks,
        config = CacheConfigs.SHORT_CACHE,
        networkCall = { getUpdateRankBooksBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookRankResponse>() {}
    )
}

suspend fun BookService.getNewestRankBooksCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookRankResponse) -> Unit)? = null
): CacheResult<BookService.BookRankResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.newestRankBooks,
        config = CacheConfigs.SHORT_CACHE,
        networkCall = { getNewestRankBooksBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookRankResponse>() {}
    )
}

suspend fun BookService.getBookCategoriesCached(
    workDirection: Int,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((BookService.BookCategoryResponse) -> Unit)? = null
): CacheResult<BookService.BookCategoryResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.bookCategories(workDirection),
        config = CacheConfigs.EXTRA_LONG_CACHE, // 分类信息基本不变，缓存时间长
        networkCall = { getBookCategoriesBlocking(workDirection) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<BookService.BookCategoryResponse>() {}
    )
}

/**
 * SearchService 缓存扩展
 */
suspend fun SearchService.searchBooksCached(
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
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((SearchService.BookSearchResponse) -> Unit)? = null
): CacheResult<SearchService.BookSearchResponse> {
    // 生成搜索参数的哈希作为缓存键的一部分
    val paramsHash = listOf(
        keyword, workDirection, categoryId, isVip, bookStatus,
        wordCountMin, wordCountMax, updateTimeMin, sort, pageNum, pageSize
    ).joinToString("_").hashCode().toString()
    
    return cacheManager.getCachedData(
        key = CacheKeys.searchBooks(paramsHash),
        config = CacheConfigs.SHORT_CACHE, // 搜索结果缓存时间短
        networkCall = {
            searchBooksBlocking(
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
                pageSize = pageSize
            )
        },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<SearchService.BookSearchResponse>() {}
    )
}

/**
 * HomeService 缓存扩展
 */
suspend fun HomeService.getHomeBookseCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((HomeService.HomeBooksResponse) -> Unit)? = null
): CacheResult<HomeService.HomeBooksResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.homeBooks,
        config = CacheConfigs.MEDIUM_CACHE,
        networkCall = { getHomeBooksBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<HomeService.HomeBooksResponse>() {}
    )
}

suspend fun HomeService.getFriendLinksCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((HomeService.FriendLinksResponse) -> Unit)? = null
): CacheResult<HomeService.FriendLinksResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.friendLinks,
        config = CacheConfigs.EXTRA_LONG_CACHE, // 友情链接变动较少
        networkCall = { getFriendLinksBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<HomeService.FriendLinksResponse>() {}
    )
}

/**
 * NewsService 缓存扩展
 */
suspend fun NewsService.getLatestNewsCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((NewsService.NewsListResponse) -> Unit)? = null
): CacheResult<NewsService.NewsListResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.latestNews,
        config = CacheConfigs.SHORT_CACHE, // 新闻更新频繁
        networkCall = { getLatestNewsBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<NewsService.NewsListResponse>() {}
    )
}

suspend fun NewsService.getNewsByIdCached(
    newsId: Long,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((NewsService.NewsInfoResponse) -> Unit)? = null
): CacheResult<NewsService.NewsInfoResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.newsInfo(newsId),
        config = CacheConfigs.LONG_CACHE,
        networkCall = { getNewsByIdBlocking(newsId) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<NewsService.NewsInfoResponse>() {}
    )
}

/**
 * UserService 缓存扩展
 */
suspend fun UserService.getUserInfoCached(
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((UserService.UserInfoResponse?) -> Unit)? = null
): CacheResult<UserService.UserInfoResponse?> {
    return cacheManager.getCachedData(
        key = CacheKeys.userInfo,
        config = CacheConfigs.MEDIUM_CACHE,
        networkCall = { getUserInfoBlocking() },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<UserService.UserInfoResponse?>() {}
    )
}

suspend fun UserService.getUserCommentsCached(
    pageRequest: UserService.PageRequest,
    cacheManager: NetworkCacheManager,
    strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
    onCacheUpdate: ((UserService.UserCommentsResponse) -> Unit)? = null
): CacheResult<UserService.UserCommentsResponse> {
    return cacheManager.getCachedData(
        key = CacheKeys.userComments(pageRequest.pageNum, pageRequest.pageSize),
        config = CacheConfigs.SHORT_CACHE,
        networkCall = { getUserCommentsBlocking(pageRequest) },
        strategy = strategy,
        onCacheUpdate = onCacheUpdate,
        typeToken = object : TypeToken<UserService.UserCommentsResponse>() {}
    )
}

/**
 * 缓存结果处理扩展
 */
inline fun <T> CacheResult<T>.onSuccess(action: (data: T, fromCache: Boolean) -> Unit): CacheResult<T> {
    if (this is CacheResult.Success) {
        action(data, fromCache)
    }
    return this
}

inline fun <T> CacheResult<T>.onError(action: (error: StableThrowable, cachedData: T?) -> Unit): CacheResult<T> {
    if (this is CacheResult.Error) {
        action(error, cachedData)
    }
    return this
}

inline fun <T> CacheResult<T>.fold(
    onSuccess: (data: T, fromCache: Boolean) -> Unit,
    onError: (error: StableThrowable, cachedData: T?) -> Unit
) {
    when (this) {
        is CacheResult.Success -> onSuccess(data, fromCache)
        is CacheResult.Error -> onError(error, cachedData)
    }
}

/**
 * BookService 增量同步缓存扩展函数
 * 
 * 支持增量同步的高级缓存功能，显著减少网络传输
 */

/**
 * 获取书籍详情（增量同步版本）
 */
suspend fun BookService.getBookByIdWithIncrementalSync(
    bookId: Long,
    cacheManager: NetworkCacheManager,
    config: CacheConfig = CacheConfigs.MEDIUM_CACHE,
    onCacheUpdate: ((BookService.BookInfoResponse) -> Unit)? = null
): IncrementalSyncResult<BookService.BookInfoResponse> {
    return cacheManager.getDataWithIncrementalSync(
        key = CacheKeys.bookInfo(bookId),
        config = config,
        networkCall = { lastModified, eTag ->
            getBookByIdWithCondition(bookId, lastModified, eTag)
        },
        typeToken = object : TypeToken<BookService.BookInfoResponse>() {}
    ).also { result ->
        // 处理缓存更新回调
        when (result) {
            is IncrementalSyncResult.Updated -> onCacheUpdate?.invoke(result.newData)
            is IncrementalSyncResult.NoChange -> onCacheUpdate?.invoke(result.cachedData)
            is IncrementalSyncResult.Error -> result.cachedData?.let { onCacheUpdate?.invoke(it) }
        }
    }
}

/**
 * 获取书籍章节列表（增量同步版本）
 */
suspend fun BookService.getBookChaptersWithIncrementalSync(
    bookId: Long,
    cacheManager: NetworkCacheManager,
    config: CacheConfig = CacheConfigs.MEDIUM_CACHE,
    onCacheUpdate: ((BookService.BookChapterResponse) -> Unit)? = null
): IncrementalSyncResult<BookService.BookChapterResponse> {
    return cacheManager.getDataWithIncrementalSync(
        key = CacheKeys.bookChapters(bookId),
        config = config,
        networkCall = { lastModified, eTag ->
            getBookChaptersWithCondition(bookId, lastModified, eTag)
        },
        typeToken = object : TypeToken<BookService.BookChapterResponse>() {}
    ).also { result ->
        when (result) {
            is IncrementalSyncResult.Updated -> onCacheUpdate?.invoke(result.newData)
            is IncrementalSyncResult.NoChange -> onCacheUpdate?.invoke(result.cachedData)
            is IncrementalSyncResult.Error -> result.cachedData?.let { onCacheUpdate?.invoke(it) }
        }
    }
}

/**
 * 获取书籍内容（增量同步版本）
 */
suspend fun BookService.getBookContentWithIncrementalSync(
    chapterId: Long,
    cacheManager: NetworkCacheManager,
    config: CacheConfig = CacheConfigs.LONG_CACHE,
    onCacheUpdate: ((BookService.BookContentResponse) -> Unit)? = null
): IncrementalSyncResult<BookService.BookContentResponse> {
    return cacheManager.getDataWithIncrementalSync(
        key = CacheKeys.bookContent(chapterId),
        config = config,
        networkCall = { lastModified, eTag ->
            getBookContentWithCondition(chapterId, lastModified, eTag)
        },
        typeToken = object : TypeToken<BookService.BookContentResponse>() {}
    ).also { result ->
        when (result) {
            is IncrementalSyncResult.Updated -> onCacheUpdate?.invoke(result.newData)
            is IncrementalSyncResult.NoChange -> onCacheUpdate?.invoke(result.cachedData)
            is IncrementalSyncResult.Error -> result.cachedData?.let { onCacheUpdate?.invoke(it) }
        }
    }
}
