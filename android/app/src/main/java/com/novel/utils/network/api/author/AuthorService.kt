package com.novel.utils.network.api.author

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_AUTHOR
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
class AuthorService @Inject constructor() {
    
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
        @SerializedName("data") val data: Int?,
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

    // region 网络请求方法
    
    /**
     * 作家注册接口
     */
    private fun registerAuthor(
        request: AuthorRegisterRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 registerAuthor()，参数：$request")
        
        val requestBody = mapOf(
            "penName" to request.penName,
            "telPhone" to request.telPhone,
            "chatAccount" to request.chatAccount,
            "email" to request.email,
            "workDirection" to request.workDirection.toString()
        )
        
        ApiService.post(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "register",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 作家状态查询接口
     */
    private fun getAuthorStatus(
        callback: (AuthorStatusResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 getAuthorStatus()")
        
        ApiService.get(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "status",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, AuthorStatusResponse::class.java, callback)
        }
    }

    /**
     * 小说发布接口
     */
    private fun publishBook(
        request: BookAddRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 publishBook()，参数：$request")
        
        val requestBody = mapOf(
            "workDirection" to request.workDirection.toString(),
            "categoryId" to request.categoryId.toString(),
            "categoryName" to request.categoryName,
            "picUrl" to request.picUrl,
            "bookName" to request.bookName,
            "bookDesc" to request.bookDesc,
            "isVip" to request.isVip.toString()
        )
        
        ApiService.post(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 小说发布列表查询接口
     */
    private fun getAuthorBooks(
        pageNum: Int = 1,
        pageSize: Int = 10,
        callback: (BookListResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 getAuthorBooks()，参数：pageNum=$pageNum, pageSize=$pageSize")
        
        val params = mapOf(
            "pageNum" to pageNum.toString(),
            "pageSize" to pageSize.toString()
        )
        
        ApiService.get(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "books",
            params = params,
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookListResponse::class.java, callback)
        }
    }

    /**
     * 小说章节发布接口
     */
    private fun publishChapter(
        bookId: Long,
        request: ChapterAddRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 publishChapter()，bookId=$bookId，参数：$request")
        
        val requestBody = mapOf(
            "bookId" to request.bookId.toString(),
            "chapterName" to request.chapterName,
            "chapterContent" to request.chapterContent,
            "isVip" to request.isVip.toString()
        )
        
        ApiService.post(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book/chapter/$bookId",
            params = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 小说章节发布列表查询接口
     */
    private fun getBookChapters(
        bookId: Long,
        pageNum: Int = 1,
        pageSize: Int = 10,
        callback: (ChapterListResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 getBookChapters()，bookId=$bookId，pageNum=$pageNum, pageSize=$pageSize")
        
        val params = mapOf(
            "pageNum" to pageNum.toString(),
            "pageSize" to pageSize.toString()
        )
        
        ApiService.get(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book/chapters/$bookId",
            params = params,
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, ChapterListResponse::class.java, callback)
        }
    }

    /**
     * 小说章节查询接口
     */
    private fun getChapter(
        chapterId: Long,
        callback: (ChapterContentResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 getChapter()，chapterId=$chapterId")
        
        ApiService.get(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book/chapter/$chapterId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, ChapterContentResponse::class.java, callback)
        }
    }

    /**
     * 小说章节更新接口
     */
    private fun updateChapter(
        chapterId: Long,
        request: ChapterUpdateRequest,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 updateChapter()，chapterId=$chapterId，参数：$request")
        
        val json = Gson().toJson(request)
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        
        ApiService.put(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book/chapter/$chapterId",
            body = requestBody,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    /**
     * 小说章节删除接口
     */
    private fun deleteChapter(
        chapterId: Long,
        callback: (BaseResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("AuthorService", "开始 deleteChapter()，chapterId=$chapterId")
        
        ApiService.delete(
            baseUrl = BASE_URL_AUTHOR,
            endpoint = "book/chapter/$chapterId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BaseResponse::class.java, callback)
        }
    }

    // endregion

    // region 协程版本
    suspend fun registerAuthorBlocking(request: AuthorRegisterRequest): BaseResponse {
        return suspendCancellableCoroutine { cont ->
            registerAuthor(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getAuthorStatusBlocking(): AuthorStatusResponse {
        return suspendCancellableCoroutine { cont ->
            getAuthorStatus { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun publishBookBlocking(request: BookAddRequest): BaseResponse {
        return suspendCancellableCoroutine { cont ->
            publishBook(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getAuthorBooksBlocking(pageNum: Int = 1, pageSize: Int = 10): BookListResponse {
        return suspendCancellableCoroutine { cont ->
            getAuthorBooks(pageNum, pageSize) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun publishChapterBlocking(bookId: Long, request: ChapterAddRequest): BaseResponse {
        return suspendCancellableCoroutine { cont ->
            publishChapter(bookId, request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getChapterBlocking(chapterId: Long): ChapterContentResponse {
        return suspendCancellableCoroutine { cont ->
            getChapter(chapterId) { response, error ->
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
                    callback(Gson().fromJson(response, clazz), null)
                } catch (e: Exception) {
                    TimberLogger.e("AuthorService", "JSON解析失败", e)
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