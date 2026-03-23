package com.novel.utils.network.api.author

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.novel.core.network.LegacyApiServiceAdapter
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.core.network.legacy.DefaultLegacyApiExecutor
import com.novel.core.result.AppError
import com.novel.core.result.DataResult
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService.BASE_URL_AUTHOR
import com.novel.utils.network.ImmutableListTypeAdapterFactory
import kotlinx.collections.immutable.ImmutableList
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 作者服务类
 * 
 * 功能：
 * - 作者书籍管理
 * - 章节CRUD操作
 * - 章节内容管理
 * - 作者数据统计
 */
@Singleton
class AuthorService @Inject constructor(
    private val networkFacade: NetworkFacade
) {

    constructor() : this(LegacyApiServiceAdapter(DefaultLegacyApiExecutor))

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
        .create()
    
    // region 数据结构
    @Stable
    data class BaseResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: Any?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class AuthorStatusResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: String?,
        @SerializedName("ok") val ok: Boolean?
    )

     data class AuthorRegisterRequest(
         @SerializedName("penName") val penName: String,
         @SerializedName("telPhone") val telPhone: String,
         @SerializedName("chatAccount") val chatAccount: String,
         @SerializedName("email") val email: String,
         @SerializedName("workDirection") val workDirection: Int // 0-男频 1-女频
     )

    data class BookAddRequest(
        @SerializedName("workDirection") val workDirection: Int,
        @SerializedName("categoryId") val categoryId: Long,
        @SerializedName("categoryName") val categoryName: String,
        @SerializedName("picUrl") val picUrl: String,
        @SerializedName("bookName") val bookName: String,
        @SerializedName("bookDesc") val bookDesc: String,
        @SerializedName("isVip") val isVip: Int // 1-收费 0-免费
    )

    data class ChapterAddRequest(
        @SerializedName("bookId") val bookId: Long,
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("chapterContent") val chapterContent: String,
        @SerializedName("isVip") val isVip: Int // 1-收费 0-免费
    )

    data class ChapterUpdateRequest(
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("chapterContent") val chapterContent: String,
        @SerializedName("isVip") val isVip: Int // 1-收费 0-免费
    )

    data class BookListResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: PageResponse<BookInfo>?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class ChapterListResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: PageResponse<ChapterInfo>?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class ChapterContentResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ChapterContent?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class PageResponse<T>(
        @SerializedName("pageNum") val pageNum: Long,
        @SerializedName("pageSize") val pageSize: Long,
        @SerializedName("total") val total: Long,
        @SerializedName("list") val list: ImmutableList<T>,
        @SerializedName("pages") val pages: Long
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
    data class ChapterInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("bookId") val bookId: Long,
        @SerializedName("chapterNum") val chapterNum: Int,
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("chapterWordCount") val chapterWordCount: Int,
        @SerializedName("chapterUpdateTime") val chapterUpdateTime: String,
        @SerializedName("isVip") val isVip: Int
    )

    @Stable
    data class ChapterContent(
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("chapterContent") val chapterContent: String,
        @SerializedName("isVip") val isVip: Int
    )
    // endregion

    // region 协程版本
    suspend fun registerAuthorBlocking(request: AuthorRegisterRequest): BaseResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "register",
                method = NetworkRequestMethod.POST,
                bodyParams = buildAuthorRegisterParams(request),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = BaseResponse::class.java
        )
    }

    suspend fun getAuthorStatusBlocking(): AuthorStatusResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "status",
                method = NetworkRequestMethod.GET,
                headers = mapOf("Accept" to "*/*")
            ),
            clazz = AuthorStatusResponse::class.java
        )
    }

    suspend fun getAuthorStatusResult(): DataResult<AuthorStatusResponse> =
        runResulting { getAuthorStatusBlocking() }

    suspend fun publishBookBlocking(request: BookAddRequest): BaseResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "book",
                method = NetworkRequestMethod.POST,
                bodyParams = mapOf(
                    "workDirection" to request.workDirection.toString(),
                    "categoryId" to request.categoryId.toString(),
                    "categoryName" to request.categoryName,
                    "picUrl" to request.picUrl,
                    "bookName" to request.bookName,
                    "bookDesc" to request.bookDesc,
                    "isVip" to request.isVip.toString()
                ),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = BaseResponse::class.java
        )
    }

    suspend fun getAuthorBooksBlocking(pageNum: Int = 1, pageSize: Int = 10): BookListResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "books",
                method = NetworkRequestMethod.GET,
                queryParams = mapOf(
                    "pageNum" to pageNum.toString(),
                    "pageSize" to pageSize.toString()
                ),
                headers = mapOf("Accept" to "*/*")
            ),
            clazz = BookListResponse::class.java
        )
    }

    suspend fun publishChapterBlocking(bookId: Long, request: ChapterAddRequest): BaseResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "book/chapter/$bookId",
                method = NetworkRequestMethod.POST,
                bodyParams = mapOf(
                    "bookId" to request.bookId.toString(),
                    "chapterName" to request.chapterName,
                    "chapterContent" to request.chapterContent,
                    "isVip" to request.isVip.toString()
                ),
                headers = mapOf(
                    "Content-Type" to "application/json",
                    "Accept" to "*/*"
                )
            ),
            clazz = BaseResponse::class.java
        )
    }

    suspend fun getChapterBlocking(chapterId: Long): ChapterContentResponse {
        return requestAndParse(
            request = NetworkRequest(
                baseUrl = BASE_URL_AUTHOR,
                endpoint = "book/chapter/$chapterId",
                method = NetworkRequestMethod.GET,
                headers = mapOf("Accept" to "*/*")
            ),
            clazz = ChapterContentResponse::class.java
        )
    }
    // endregion

    private suspend fun <T> requestAndParse(
        request: NetworkRequest,
        clazz: Class<T>
    ): T {
        val response = networkFacade.execute(request)
        return gson.fromJson(response, clazz)
    }

    private suspend fun <T> runResulting(block: suspend () -> T): DataResult<T> =
        try {
            DataResult.Success(block())
        } catch (throwable: Throwable) {
            TimberLogger.e("AuthorService", "DataResult request failed", throwable)
            DataResult.Failure(AppError.fromThrowable(throwable))
        }

    private fun <T> handleResponse(
        response: String?,
        error: Throwable?,
        clazz: Class<T>,
        callback: (T?, Throwable?) -> Unit
    ) {
        when {
            error != null -> callback(null, error)
            response != null -> {
                try {
                    callback(gson.fromJson(response, clazz), null)
                } catch (e: Exception) {
                    TimberLogger.e("AuthorService", "JSON解析失败", e)
                    callback(null, e)
                }
            }
            else -> callback(null, Exception("Response is null"))
        }
    }

    private fun buildAuthorRegisterParams(request: AuthorRegisterRequest): Map<String, String> =
        mapOf(
            "penName" to request.penName,
            "telPhone" to request.telPhone,
            "chatAccount" to request.chatAccount,
            "email" to request.email,
            "workDirection" to request.workDirection.toString()
        )
    // endregion
}
