package com.novel.utils.network.api

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.priority.RequestPriority
import com.novel.utils.network.priority.withPriority
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.network.api.front.NewsService
import com.novel.utils.network.api.front.user.UserService
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

/**
 * 请求优先级上下文键
 */
private val PRIORITY_CONTEXT_KEY = ThreadLocal<RequestPriority>()

/**
 * 优化服务扩展
 * 
 * 为现有的Service添加优先级和优化支持，保持向后兼容性
 * 所有扩展方法都是可选的，不影响现有API调用
 */
object OptimizedServiceExtensions {
    private const val TAG = "OptimizedServiceExtensions"
    
    /**
     * 设置当前线程的请求优先级上下文
     */
    fun setRequestPriority(priority: RequestPriority) {
        PRIORITY_CONTEXT_KEY.set(priority)
        TimberLogger.d(TAG, "设置请求优先级: $priority")
    }
    
    /**
     * 获取当前线程的请求优先级
     */
    fun getCurrentRequestPriority(): RequestPriority? {
        return PRIORITY_CONTEXT_KEY.get()
    }
    
    /**
     * 清除当前线程的请求优先级上下文
     */
    fun clearRequestPriority() {
        PRIORITY_CONTEXT_KEY.remove()
    }
    
    /**
     * 在指定优先级上下文中执行操作
     */
    suspend fun <T> withPriority(priority: RequestPriority, block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            val oldPriority = getCurrentRequestPriority()
            try {
                setRequestPriority(priority)
                block()
            } finally {
                if (oldPriority != null) {
                    setRequestPriority(oldPriority)
                } else {
                    clearRequestPriority()
                }
            }
        }
    }
}

/**
 * BookService优化扩展
 */

/**
 * 高优先级获取书籍信息（用户主动点击）
 */
suspend fun BookService.getBookByIdHighPriority(bookId: Long): BookService.BookInfoResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取书籍信息: bookId=$bookId")
        getBookByIdBlocking(bookId)
    }
}

/**
 * 低优先级预取书籍信息（后台预取）
 */
suspend fun BookService.prefetchBookById(bookId: Long): BookService.BookInfoResponse? {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.LOW) {
            TimberLogger.d("BookService", "后台预取书籍信息: bookId=$bookId")
            getBookByIdBlocking(bookId)
        }
    } catch (e: Exception) {
        TimberLogger.w("BookService", "预取书籍信息失败: bookId=$bookId", e)
        null
    }
}

/**
 * 高优先级获取章节列表（用户导航）
 */
suspend fun BookService.getBookChaptersHighPriority(bookId: Long): BookService.BookChapterResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取章节列表: bookId=$bookId")
        getBookChaptersBlocking(bookId)
    }
}

/**
 * 高优先级获取章节内容（用户阅读）
 */
suspend fun BookService.getBookContentHighPriority(chapterId: Long): BookService.BookContentResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取章节内容: chapterId=$chapterId")
        getBookContentBlocking(chapterId)
    }
}

/**
 * 低优先级预取章节内容（预读）
 */
suspend fun BookService.prefetchBookContent(chapterId: Long): BookService.BookContentResponse? {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.LOW) {
            TimberLogger.d("BookService", "预取章节内容: chapterId=$chapterId")
            getBookContentBlocking(chapterId)
        }
    } catch (e: Exception) {
        TimberLogger.w("BookService", "预取章节内容失败: chapterId=$chapterId", e)
        null
    }
}

/**
 * 高优先级获取排行榜（用户主动查看）
 */
suspend fun BookService.getVisitRankBooksHighPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取访问排行榜")
        getVisitRankBooksBlocking()
    }
}

suspend fun BookService.getUpdateRankBooksHighPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取更新排行榜")
        getUpdateRankBooksBlocking()
    }
}

suspend fun BookService.getNewestRankBooksHighPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取最新排行榜")
        getNewestRankBooksBlocking()
    }
}

/**
 * 中等优先级获取排行榜（首页显示）
 */
suspend fun BookService.getVisitRankBooksMediumPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.MEDIUM) {
        TimberLogger.d("BookService", "中等优先级获取访问排行榜")
        getVisitRankBooksBlocking()
    }
}

suspend fun BookService.getUpdateRankBooksMediumPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.MEDIUM) {
        TimberLogger.d("BookService", "中等优先级获取更新排行榜")
        getUpdateRankBooksBlocking()
    }
}

suspend fun BookService.getNewestRankBooksMediumPriority(): BookService.BookRankResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.MEDIUM) {
        TimberLogger.d("BookService", "中等优先级获取最新排行榜")
        getNewestRankBooksBlocking()
    }
}

/**
 * 高优先级获取分类（用户主动查看）
 */
suspend fun BookService.getBookCategoriesHighPriority(workDirection: Int): BookService.BookCategoryResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("BookService", "高优先级获取书籍分类: workDirection=$workDirection")
        getBookCategoriesBlocking(workDirection)
    }
}

/**
 * 低优先级获取分类（背景加载）
 */
suspend fun BookService.getBookCategoriesLowPriority(workDirection: Int): BookService.BookCategoryResponse? {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.LOW) {
            TimberLogger.d("BookService", "低优先级获取书籍分类: workDirection=$workDirection")
            getBookCategoriesBlocking(workDirection)
        }
    } catch (e: Exception) {
        TimberLogger.w("BookService", "获取书籍分类失败: workDirection=$workDirection", e)
        null
    }
}

/**
 * SearchService优化扩展
 */

/**
 * 高优先级搜索（用户主动搜索）
 */
suspend fun SearchService.searchBooksHighPriority(
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
    pageSize: Int = 20
): SearchService.BookSearchResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("SearchService", "高优先级搜索书籍: keyword=$keyword")
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
    }
}

/**
 * 低优先级搜索建议（自动补全）
 */
suspend fun SearchService.searchSuggestionsLowPriority(
    keyword: String,
    limit: Int = 10
): SearchService.BookSearchResponse? {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.LOW) {
            TimberLogger.d("SearchService", "低优先级搜索建议: keyword=$keyword")
            searchBooksBlocking(
                keyword = keyword,
                pageSize = limit
            )
        }
    } catch (e: Exception) {
        TimberLogger.w("SearchService", "搜索建议失败: keyword=$keyword", e)
        null
    }
}

/**
 * HomeService优化扩展
 */

/**
 * 高优先级获取首页数据（用户进入首页）
 */
suspend fun HomeService.getHomeBooksHighPriority(): HomeService.HomeBooksResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("HomeService", "高优先级获取首页书籍")
        getHomeBooksBlocking()
    }
}

/**
 * 低优先级获取友情链接（后台加载）
 */
suspend fun HomeService.getFriendLinksLowPriority(): HomeService.FriendLinksResponse? {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.LOW) {
            TimberLogger.d("HomeService", "低优先级获取友情链接")
            getFriendLinksBlocking()
        }
    } catch (e: Exception) {
        TimberLogger.w("HomeService", "获取友情链接失败", e)
        null
    }
}

/**
 * NewsService优化扩展
 */

/**
 * 中等优先级获取最新资讯（内容页显示）
 */
suspend fun NewsService.getLatestNewsMediumPriority(): NewsService.NewsListResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.MEDIUM) {
        TimberLogger.d("NewsService", "中等优先级获取最新资讯")
        getLatestNewsBlocking()
    }
}

/**
 * 高优先级获取资讯详情（用户点击查看）
 */
suspend fun NewsService.getNewsByIdHighPriority(newsId: Long): NewsService.NewsInfoResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("NewsService", "高优先级获取资讯详情: newsId=$newsId")
        getNewsByIdBlocking(newsId)
    }
}

/**
 * UserService优化扩展
 */

/**
 * 高优先级用户登录（用户主动操作）
 */
suspend fun UserService.loginHighPriority(request: UserService.LoginRequest): UserService.LoginResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("UserService", "高优先级用户登录: username=${request.username}")
        loginBlocking(request)
    }
}

/**
 * 高优先级用户注册（用户主动操作）
 */
suspend fun UserService.registerHighPriority(request: UserService.RegisterRequest): UserService.RegisterResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("UserService", "高优先级用户注册: username=${request.username}")
        registerBlocking(request)
    }
}

/**
 * 高优先级获取用户信息（登录后）
 */
suspend fun UserService.getUserInfoHighPriority(): UserService.UserInfoResponse? {
    return OptimizedServiceExtensions.withPriority(RequestPriority.HIGH) {
        TimberLogger.d("UserService", "高优先级获取用户信息")
        getUserInfoBlocking()
    }
}

/**
 * 中等优先级获取用户评论（个人中心显示）
 */
suspend fun UserService.getUserCommentsMediumPriority(
    pageRequest: UserService.PageRequest
): UserService.UserCommentsResponse {
    return OptimizedServiceExtensions.withPriority(RequestPriority.MEDIUM) {
        TimberLogger.d("UserService", "中等优先级获取用户评论: page=${pageRequest.pageNum}")
        getUserCommentsBlocking(pageRequest)
    }
}

/**
 * 低优先级上报用户行为（后台统计）
 */
suspend fun UserService.reportUserBehaviorLowPriority(
    behavior: Map<String, Any>
): Boolean {
    return try {
        OptimizedServiceExtensions.withPriority(RequestPriority.BACKGROUND) {
            TimberLogger.d("UserService", "低优先级上报用户行为")
            // 这里可以添加具体的行为上报逻辑
            true
        }
    } catch (e: Exception) {
        TimberLogger.w("UserService", "上报用户行为失败", e)
        false
    }
}

/**
 * 网络优化策略建议
 */
@Stable
object NetworkOptimizationStrategy {
    
    /**
     * 根据场景推荐优先级
     */
    fun recommendPriority(scenario: String): RequestPriority {
        return when (scenario.lowercase()) {
            "user_click", "page_navigation", "login", "reading" -> RequestPriority.HIGH
            "list_loading", "content_display", "search" -> RequestPriority.MEDIUM
            "prefetch", "background_sync", "statistics" -> RequestPriority.LOW
            "cleanup", "cache_warm" -> RequestPriority.BACKGROUND
            else -> RequestPriority.MEDIUM
        }
    }
    
    /**
     * 获取场景描述
     */
    fun getScenarioDescription(priority: RequestPriority): String {
        return when (priority) {
            RequestPriority.HIGH -> "用户关键操作（点击、导航、阅读）"
            RequestPriority.MEDIUM -> "内容展示（列表、搜索、详情）"
            RequestPriority.LOW -> "后台任务（预取、同步、统计）"
            RequestPriority.BACKGROUND -> "维护任务（清理、缓存预热）"
        }
    }
}