package com.novel.utils.network.api.front

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.novel.core.StableThrowable
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import com.novel.utils.network.cache.IncrementalNetworkResponse
import kotlinx.collections.immutable.ImmutableList
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
    private val gson: Gson,
    private val networkFacade: NetworkFacade
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

    // region 协程版本
    suspend fun getBookByIdBlocking(bookId: Long): BookInfoResponse {
        return requestAndParse(
            endpoint = "book/$bookId",
            clazz = BookInfoResponse::class.java
        )
    }

    suspend fun getBookContentBlocking(chapterId: Long): BookContentResponse {
        return requestAndParse(
            endpoint = "book/content/$chapterId",
            clazz = BookContentResponse::class.java
        )
    }

    suspend fun getBookChaptersBlocking(bookId: Long): BookChapterResponse {
        return requestAndParse(
            endpoint = "book/chapter/list",
            queryParams = mapOf("bookId" to bookId.toString()),
            clazz = BookChapterResponse::class.java
        )
    }

    suspend fun getVisitRankBooksBlocking(): BookRankResponse {
        return requestAndParse(
            endpoint = "book/visit_rank",
            clazz = BookRankResponse::class.java
        )
    }

    suspend fun getUpdateRankBooksBlocking(): BookRankResponse {
        return requestAndParse(
            endpoint = "book/update_rank",
            clazz = BookRankResponse::class.java
        )
    }

    suspend fun getNewestRankBooksBlocking(): BookRankResponse {
        return requestAndParse(
            endpoint = "book/newest_rank",
            clazz = BookRankResponse::class.java
        )
    }

    suspend fun getBookCategoriesBlocking(workDirection: Int): BookCategoryResponse {
        return requestAndParse(
            endpoint = "book/category/list",
            queryParams = mapOf("workDirection" to workDirection.toString()),
            clazz = BookCategoryResponse::class.java
        )
    }

    suspend fun getLastChapterAboutBlocking(bookId: Long): BookChapterAboutResponse {
        return requestAndParse(
            endpoint = "book/last_chapter/about",
            queryParams = mapOf("bookId" to bookId.toString()),
            clazz = BookChapterAboutResponse::class.java
        )
    }

    /**
     * 获取最新评论的协程版本
     */
    suspend fun getNewestCommentsBlocking(bookId: Long): BookCommentResponse {
        return requestAndParse(
            endpoint = "book/comment/newest_list",
            queryParams = mapOf("bookId" to bookId.toString()),
            clazz = BookCommentResponse::class.java
        )
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
        val headers = buildConditionalHeaders(lastModified, eTag)
        TimberLogger.d("BookService", "条件请求获取章节内容: chapterId=$chapterId, lastModified=$lastModified, eTag=$eTag")

        return runIncrementalRequest(
            endpoint = "book/content/$chapterId",
            headers = headers,
            clazz = BookContentResponse::class.java
        ) { response, data ->
            IncrementalNetworkResponse.Modified(
                data = data,
                serverVersion = System.currentTimeMillis().toString(),
                lastModified = System.currentTimeMillis().toString(),
                eTag = "\"${response.hashCode()}\""
            )
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
        val headers = buildConditionalHeaders(lastModified, eTag)
        TimberLogger.d("BookService", "条件请求获取书籍信息: bookId=$bookId, lastModified=$lastModified, eTag=$eTag")

        return runIncrementalRequest(
            endpoint = "book/$bookId",
            headers = headers,
            clazz = BookInfoResponse::class.java
        ) { response, data ->
            val version = data.data?.updateTime ?: System.currentTimeMillis().toString()
            IncrementalNetworkResponse.Modified(
                data = data,
                serverVersion = version,
                lastModified = version,
                eTag = "\"${response.hashCode()}\""
            )
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
        val headers = buildConditionalHeaders(lastModified, eTag)
        TimberLogger.d("BookService", "条件请求获取章节列表: bookId=$bookId, lastModified=$lastModified, eTag=$eTag")

        return runIncrementalRequest(
            endpoint = "book/chapter/list",
            queryParams = mapOf("bookId" to bookId.toString()),
            headers = headers,
            clazz = BookChapterResponse::class.java
        ) { response, data ->
            val latestUpdateTime = data.data
                ?.maxByOrNull { it.chapterUpdateTime }
                ?.chapterUpdateTime
                ?: System.currentTimeMillis().toString()
            IncrementalNetworkResponse.Modified(
                data = data,
                serverVersion = latestUpdateTime,
                lastModified = latestUpdateTime,
                eTag = "\"${response.hashCode()}\""
            )
        }
    }
    // endregion

    // region 响应处理
    private suspend fun <T> requestAndParse(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap(),
        clazz: Class<T>
    ): T {
        val response = executeGetRequest(
            endpoint = endpoint,
            queryParams = queryParams
        )
        return gson.fromJson(response, clazz)
    }

    private suspend fun executeGetRequest(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = mapOf("Accept" to "*/*")
    ): String {
        return networkFacade.execute(
            NetworkRequest(
                baseUrl = BASE_URL_FRONT,
                endpoint = endpoint,
                method = NetworkRequestMethod.GET,
                queryParams = queryParams,
                headers = headers
            )
        )
    }

    private fun buildConditionalHeaders(
        lastModified: String?,
        eTag: String?
    ): Map<String, String> = buildMap {
        put("Accept", "*/*")
        lastModified?.let { put("If-Modified-Since", it) }
        eTag?.let { put("If-None-Match", it) }
    }

    private suspend fun <T> runIncrementalRequest(
        endpoint: String,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String>,
        clazz: Class<T>,
        onModified: (String, T) -> IncrementalNetworkResponse.Modified<T>
    ): IncrementalNetworkResponse<T> {
        return try {
            val response = executeGetRequest(
                endpoint = endpoint,
                queryParams = queryParams,
                headers = headers
            )
            val parsed = gson.fromJson(response, clazz)
            onModified(response, parsed)
        } catch (throwable: Throwable) {
            if (throwable.message?.contains("304") == true || throwable.message?.contains("Not Modified") == true) {
                IncrementalNetworkResponse.NotModified()
            } else {
                IncrementalNetworkResponse.Error(StableThrowable(throwable))
            }
        }
    }
    // endregion
}
