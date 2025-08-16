package com.novel.utils.network.api.front

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.novel.core.StableThrowable
import com.novel.utils.network.cache.IncrementalNetworkResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 书籍服务API
 * 
 * 功能：
 * - 书籍信息查询（详情、榜单、分类）
 * - 章节内容获取
 * - 评论数据管理
 * - 阅读统计更新
 * 
 * 特点：
 * - 支持协程和回调两种调用方式
 * - 统一的响应处理机制
 * - 完整的数据模型定义
 */
@Singleton
@Stable
class BookService @Inject constructor(
    @Stable
    private val gson: Gson
) {
    
    // region 数据结构
    @Stable
    data class BookInfoResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: BookInfo?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("categoryId") val categoryId: Long,
        @SerializedName("categoryName") val categoryName: String,
        @SerializedName("picUrl") val picUrl: String,
        @SerializedName("bookName") val bookName: String,
        @SerializedName("authorId") val authorId: Long,
        @SerializedName("authorName") val authorName: String,
        @SerializedName("bookDesc") val bookDesc: String,
        @SerializedName("bookStatus") val bookStatus: Int,
        @SerializedName("visitCount") val visitCount: Long,
        @SerializedName("wordCount") val wordCount: Int,
        @SerializedName("commentCount") val commentCount: Int,
        @SerializedName("firstChapterId") val firstChapterId: Long,
        @SerializedName("lastChapterId") val lastChapterId: Long,
        @SerializedName("lastChapterName") val lastChapterName: String,
        @SerializedName("updateTime") val updateTime: String
    )

    @Stable
    data class BookListResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<BookInfo>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookRankResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<BookRank>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookRank(
        @SerializedName("id") val id: Long,
        @SerializedName("categoryId") val categoryId: Long,
        @SerializedName("categoryName") val categoryName: String,
        @SerializedName("picUrl") val picUrl: String,
        @SerializedName("bookName") val bookName: String,
        @SerializedName("authorName") val authorName: String,
        @SerializedName("bookDesc") val bookDesc: String,
        @SerializedName("wordCount") val wordCount: Int,
        @SerializedName("lastChapterName") val lastChapterName: String,
        @SerializedName("lastChapterUpdateTime") val lastChapterUpdateTime: String
    )

    @Stable
    data class BookChapterResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<BookChapter>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookChapter(
        @SerializedName("id") val id: Long,
        @SerializedName("bookId") val bookId: Long,
        @SerializedName("chapterNum") val chapterNum: Int,
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("chapterWordCount") val chapterWordCount: Int,
        @SerializedName("chapterUpdateTime") val chapterUpdateTime: String,
        @SerializedName("isVip") val isVip: Int
    )

    @Stable
    data class BookContentResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: BookContentAbout?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookContentAbout(
        @SerializedName("bookInfo") val bookInfo: BookInfo,
        @SerializedName("chapterInfo") val chapterInfo: BookChapter,
        @SerializedName("bookContent") val bookContent: String
    )

    @Stable
    data class BookCommentResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: BookComment?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookComment(
        @SerializedName("commentTotal") val commentTotal: Long,
        @SerializedName("comments") val comments: ImmutableList<CommentInfo>
    )

    @Stable
    data class CommentInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("commentContent") val commentContent: String,
        @SerializedName("commentUser") val commentUser: String,
        @SerializedName("commentUserId") val commentUserId: Long,
        @SerializedName("commentUserPhoto") val commentUserPhoto: String,
        @SerializedName("commentTime") val commentTime: String
    )

    @Stable
    data class BookCategoryResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<BookCategory>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookCategory(
        @SerializedName("id") val id: Long,
        @SerializedName("name") val name: String
    )

    @Stable
    data class ChapterIdResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: Long?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookChapterAboutResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: BookChapterAbout?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class BookChapterAbout(
        @SerializedName("chapterInfo") val chapterInfo: BookChapter,
        @SerializedName("chapterTotal") val chapterTotal: Long,
        @SerializedName("contentSummary") val contentSummary: String
    )

    @Stable
    data class BaseResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: Any?,
        @SerializedName("ok") val ok: Boolean?
    )
    // endregion

    // region 网络请求方法
    
    /**
     * 小说信息查询接口
     */
    private fun getBookById(
        bookId: Long,
        callback: (BookInfoResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getBookById()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/$bookId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookInfoResponse::class.java, callback)
        }
    }

    /**
     * 小说点击榜查询接口
     */
    private fun getVisitRankBooks(
        callback: (BookRankResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getVisitRankBooks()")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/visit_rank",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookRankResponse::class.java, callback)
        }
    }

    /**
     * 小说更新榜查询接口
     */
    private fun getUpdateRankBooks(
        callback: (BookRankResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getUpdateRankBooks()")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/update_rank",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookRankResponse::class.java, callback)
        }
    }

    /**
     * 小说新书榜查询接口
     */
    private fun getNewestRankBooks(
        callback: (BookRankResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getNewestRankBooks()")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/newest_rank",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookRankResponse::class.java, callback)
        }
    }

    /**
     * 小说推荐列表查询接口
     */
    private fun getRecommendBooks(
        bookId: Long,
        callback: (BookListResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getRecommendBooks()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/rec_list",
            params = mapOf("bookId" to bookId.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookListResponse::class.java, callback)
        }
    }

    /**
     * 小说章节列表查询接口
     */
    private fun getBookChapters(
        bookId: Long,
        callback: (BookChapterResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getBookChapters()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/chapter/list",
            params = mapOf("bookId" to bookId.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookChapterResponse::class.java, callback)
        }
    }

    /**
     * 小说内容相关信息查询接口
     */
    private fun getBookContent(
        chapterId: Long,
        callback: (BookContentResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getBookContent()，参数：$chapterId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/content/$chapterId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookContentResponse::class.java, callback)
        }
    }

    /**
     * 小说最新评论查询接口
     */
    private fun getNewestComments(
        bookId: Long,
        callback: (BookCommentResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getNewestComments()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/comment/newest_list",
            params = mapOf("bookId" to bookId.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookCommentResponse::class.java, callback)
        }
    }

    /**
     * 小说分类列表查询接口
     */
    private fun getBookCategories(
        workDirection: Int,
        callback: (BookCategoryResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getBookCategories()，参数：$workDirection")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/category/list",
            params = mapOf("workDirection" to workDirection.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookCategoryResponse::class.java, callback)
        }
    }

    /**
     * 获取上一章节ID接口
     */
    private fun getPreChapterId(
        chapterId: Long,
        callback: (ChapterIdResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getPreChapterId()，参数：$chapterId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/pre_chapter_id/$chapterId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, ChapterIdResponse::class.java, callback)
        }
    }

    /**
     * 获取下一章节ID接口
     */
    private fun getNextChapterId(
        chapterId: Long,
        callback: (ChapterIdResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getNextChapterId()，参数：$chapterId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/next_chapter_id/$chapterId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, ChapterIdResponse::class.java, callback)
        }
    }

    /**
     * 小说最新章节相关信息查询接口
     */
    private fun getLastChapterAbout(
        bookId: Long,
        callback: (BookChapterAboutResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 getLastChapterAbout()，参数：$bookId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/last_chapter/about",
            params = mapOf("bookId" to bookId.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookChapterAboutResponse::class.java, callback)
        }
    }

    /**
     * 增加小说点击量接口
     */
    private fun addVisitCount(
        bookId: Long,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("BookService", "开始 addVisitCount()，参数：$bookId")
        
        ApiService.post(
            baseUrl = BASE_URL_FRONT,
            endpoint = "book/visit",
            params = mapOf("bookId" to bookId.toString()),
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    // endregion

    // region 协程版本
    suspend fun getBookByIdBlocking(bookId: Long): BookInfoResponse {
        return suspendCancellableCoroutine { cont ->
            getBookById(bookId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getBookContentBlocking(chapterId: Long): BookContentResponse {
        return suspendCancellableCoroutine { cont ->
            getBookContent(chapterId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getBookChaptersBlocking(bookId: Long): BookChapterResponse {
        return suspendCancellableCoroutine { cont ->
            getBookChapters(bookId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getVisitRankBooksBlocking(): BookRankResponse {
        return suspendCancellableCoroutine { cont ->
            getVisitRankBooks { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getUpdateRankBooksBlocking(): BookRankResponse {
        return suspendCancellableCoroutine { cont ->
            getUpdateRankBooks { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getNewestRankBooksBlocking(): BookRankResponse {
        return suspendCancellableCoroutine { cont ->
            getNewestRankBooks { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getBookCategoriesBlocking(workDirection: Int): BookCategoryResponse {
        return suspendCancellableCoroutine { cont ->
            getBookCategories(workDirection) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getLastChapterAboutBlocking(bookId: Long): BookChapterAboutResponse {
        return suspendCancellableCoroutine { cont ->
            getLastChapterAbout(bookId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    /**
     * 获取最新评论的协程版本
     */
    suspend fun getNewestCommentsBlocking(bookId: Long): BookCommentResponse {
        return suspendCancellableCoroutine { cont ->
            getNewestComments(bookId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }
    // endregion

    // region 增量同步支持的协程版本

    /**
     * 支持条件请求的获取章节内容（增量同步）
     */
    suspend fun getBookContentWithCondition(
        chapterId: Long,
        lastModified: String? = null,
        eTag: String? = null
    ): IncrementalNetworkResponse<BookContentResponse> {
        return suspendCancellableCoroutine { cont ->
            val headers = mutableMapOf<String, String>()
            headers["Accept"] = "*/*"
            
            // 添加条件请求头
            lastModified?.let { headers["If-Modified-Since"] = it }
            eTag?.let { headers["If-None-Match"] = it }
            
            TimberLogger.d("BookService", "条件请求获取章节内容: chapterId=$chapterId, lastModified=$lastModified, eTag=$eTag")
            
            ApiService.get(
                baseUrl = BASE_URL_FRONT,
                endpoint = "book/content/$chapterId",
                headers = headers
            ) { response, error ->
                when {
                    error != null -> {
                        if (error.message?.contains("304") == true || error.message?.contains("Not Modified") == true) {
                            // 304 Not Modified
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.NotModified()))
                        } else {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(error))))
                        }
                    }
                    response != null -> {
                        try {
                            val bookContentResponse = gson.fromJson(response, BookContentResponse::class.java)
                            // 模拟服务器返回的版本信息（实际项目中应从响应头获取）
                            val serverVersion = System.currentTimeMillis().toString()
                            val responseLastModified = System.currentTimeMillis().toString()
                            val responseETag = "\"${response.hashCode()}\""
                            
                            cont.resumeWith(Result.success(
                                IncrementalNetworkResponse.Modified(
                                    data = bookContentResponse,
                                    serverVersion = serverVersion,
                                    lastModified = responseLastModified,
                                    eTag = responseETag
                                )
                            ))
                        } catch (e: Exception) {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(e))))
                        }
                    }
                    else -> {
                        cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(Exception("Response is null")))))
                    }
                }
            }
        }
    }

    /**
     * 支持条件请求的获取书籍信息（增量同步）
     */
    suspend fun getBookByIdWithCondition(
        bookId: Long,
        lastModified: String? = null,
        eTag: String? = null
    ): IncrementalNetworkResponse<BookInfoResponse> {
        return suspendCancellableCoroutine { cont ->
            val headers = mutableMapOf<String, String>()
            headers["Accept"] = "*/*"
            
            // 添加条件请求头
            lastModified?.let { headers["If-Modified-Since"] = it }
            eTag?.let { headers["If-None-Match"] = it }
            
            TimberLogger.d("BookService", "条件请求获取书籍信息: bookId=$bookId, lastModified=$lastModified, eTag=$eTag")
            
            ApiService.get(
                baseUrl = BASE_URL_FRONT,
                endpoint = "book/$bookId",
                headers = headers
            ) { response, error ->
                when {
                    error != null -> {
                        if (error.message?.contains("304") == true || error.message?.contains("Not Modified") == true) {
                            // 304 Not Modified
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.NotModified()))
                        } else {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(error))))
                        }
                    }
                    response != null -> {
                        try {
                            val bookInfoResponse = gson.fromJson(response, BookInfoResponse::class.java)
                            // 模拟服务器返回的版本信息（实际项目中应从响应头获取）
                            val serverVersion = bookInfoResponse.data?.updateTime ?: System.currentTimeMillis().toString()
                            val responseLastModified = bookInfoResponse.data?.updateTime ?: System.currentTimeMillis().toString()
                            val responseETag = "\"${response.hashCode()}\""
                            
                            cont.resumeWith(Result.success(
                                IncrementalNetworkResponse.Modified(
                                    data = bookInfoResponse,
                                    serverVersion = serverVersion,
                                    lastModified = responseLastModified,
                                    eTag = responseETag
                                )
                            ))
                        } catch (e: Exception) {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(e))))
                        }
                    }
                    else -> {
                        cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(Exception("Response is null")))))
                    }
                }
            }
        }
    }

    /**
     * 支持条件请求的获取章节列表（增量同步）
     */
    suspend fun getBookChaptersWithCondition(
        bookId: Long,
        lastModified: String? = null,
        eTag: String? = null
    ): IncrementalNetworkResponse<BookChapterResponse> {
        return suspendCancellableCoroutine { cont ->
            val headers = mutableMapOf<String, String>()
            headers["Accept"] = "*/*"
            
            // 添加条件请求头
            lastModified?.let { headers["If-Modified-Since"] = it }
            eTag?.let { headers["If-None-Match"] = it }
            
            TimberLogger.d("BookService", "条件请求获取章节列表: bookId=$bookId, lastModified=$lastModified, eTag=$eTag")
            
            ApiService.get(
                baseUrl = BASE_URL_FRONT,
                endpoint = "book/chapter/list",
                params = mapOf("bookId" to bookId.toString()),
                headers = headers
            ) { response, error ->
                when {
                    error != null -> {
                        if (error.message?.contains("304") == true || error.message?.contains("Not Modified") == true) {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.NotModified()))
                        } else {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(error))))
                        }
                    }
                    response != null -> {
                        try {
                            val bookChapterResponse = gson.fromJson(response, BookChapterResponse::class.java)
                            // 使用最新章节的更新时间作为版本信息，安全处理null值
                            val latestUpdateTime = bookChapterResponse.data
                                ?.filter { it.chapterUpdateTime != null }
                                ?.maxByOrNull { it.chapterUpdateTime ?: "" }
                                ?.chapterUpdateTime
                            val serverVersion = latestUpdateTime ?: System.currentTimeMillis().toString()
                            val responseLastModified = latestUpdateTime ?: System.currentTimeMillis().toString()
                            val responseETag = "\"${response.hashCode()}\""
                            
                            cont.resumeWith(Result.success(
                                IncrementalNetworkResponse.Modified(
                                    data = bookChapterResponse,
                                    serverVersion = serverVersion,
                                    lastModified = responseLastModified,
                                    eTag = responseETag
                                )
                            ))
                        } catch (e: Exception) {
                            cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(e))))
                        }
                    }
                    else -> {
                        cont.resumeWith(Result.success(IncrementalNetworkResponse.Error(StableThrowable(Exception("Response is null")))))
                    }
                }
            }
        }
    }
    // endregion

    // region 响应处理
    private fun <T> handleResponse(
        response: String?,
        error: Throwable?,
        clazz: Class<T>,
        callback: (T?, Throwable?) -> Unit
    ) {
        when {
            error != null -> {
                callback(null, error)
            }
            response != null -> {
                try {
                    callback(gson.fromJson(response, clazz), null)
                } catch (e: Exception) {
                    callback(null, e)
                }
            }
            else -> {
                callback(null, Exception("Response is null"))
            }
        }
    }
    // endregion
}