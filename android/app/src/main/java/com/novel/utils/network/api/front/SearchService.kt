package com.novel.utils.network.api.front

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class SearchService @Inject constructor(
    @Stable
    private val gson: Gson
) {
    
    // region 数据结构
    @Stable
    data class BookSearchResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: PageResponse<BookInfo>?,
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
    data class SearchRequest(
        val keyword: String? = null,
        val workDirection: Int? = null,
        val categoryId: Int? = null,
        val isVip: Int? = null,
        val bookStatus: Int? = null,
        val wordCountMin: Int? = null,
        val wordCountMax: Int? = null,
        val updateTimeMin: String? = null,
        val sort: String? = null,
        val pageNum: Int? = null,
        val pageSize: Int? = null
    )
    // endregion

    // region 网络请求方法
    
    /**
     * 小说搜索接口
     */
    private fun searchBooks(
        request: SearchRequest,
        callback: (BookSearchResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("SearchService", "开始 searchBooks()，参数：$request")
        
        val params = mutableMapOf<String, String>()
        request.keyword?.let { params["keyword"] = it }
        request.workDirection?.let { params["workDirection"] = it.toString() }
        request.categoryId?.let { params["categoryId"] = it.toString() }
        request.isVip?.let { params["isVip"] = it.toString() }
        request.bookStatus?.let { params["bookStatus"] = it.toString() }
        request.wordCountMin?.let { params["wordCountMin"] = it.toString() }
        request.wordCountMax?.let { params["wordCountMax"] = it.toString() }
        request.updateTimeMin?.let { params["updateTimeMin"] = it }
        request.sort?.let { params["sort"] = it }
        request.pageNum?.let { params["pageNum"] = it.toString() }
        request.pageSize?.let { params["pageSize"] = it.toString() }
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "search/books",
            params = params,
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, BookSearchResponse::class.java, callback)
        }
    }

    /**
     * 简化的搜索方法，只需要关键字
     */
    private fun searchBooksByKeyword(
        keyword: String,
        pageNum: Int = 1,
        pageSize: Int = 10,
        callback: (BookSearchResponse?, Throwable?) -> Unit
    ) {
        val request = SearchRequest(
            keyword = keyword,
            pageNum = pageNum,
            pageSize = pageSize
        )
        searchBooks(request, callback)
    }

    /**
     * 按分类搜索
     */
    private fun searchBooksByCategory(
        categoryId: Int,
        workDirection: Int,
        pageNum: Int = 1,
        pageSize: Int = 10,
        callback: (BookSearchResponse?, Throwable?) -> Unit
    ) {
        val request = SearchRequest(
            categoryId = categoryId,
            workDirection = workDirection,
            pageNum = pageNum,
            pageSize = pageSize
        )
        searchBooks(request, callback)
    }

    /**
     * 高级搜索
     */
    private fun advancedSearch(
        keyword: String? = null,
        workDirection: Int? = null,
        categoryId: Int? = null,
        isVip: Int? = null,
        bookStatus: Int? = null,
        wordCountMin: Int? = null,
        wordCountMax: Int? = null,
        sort: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 10,
        callback: (BookSearchResponse?, Throwable?) -> Unit
    ) {
        val request = SearchRequest(
            keyword = keyword,
            workDirection = workDirection,
            categoryId = categoryId,
            isVip = isVip,
            bookStatus = bookStatus,
            wordCountMin = wordCountMin,
            wordCountMax = wordCountMax,
            sort = sort,
            pageNum = pageNum,
            pageSize = pageSize
        )
        searchBooks(request, callback)
    }

    // endregion

    // region 协程版本
    suspend fun searchBooksBlocking(request: SearchRequest): BookSearchResponse {
        return suspendCancellableCoroutine { cont ->
            searchBooks(request) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun searchBooksByKeywordBlocking(
        keyword: String,
        pageNum: Int = 1,
        pageSize: Int = 10
    ): BookSearchResponse {
        val request = SearchRequest(
            keyword = keyword,
            pageNum = pageNum,
            pageSize = pageSize
        )
        return searchBooksBlocking(request)
    }

    /**
     * 完整搜索方法 - 支持所有筛选参数
     */
    suspend fun searchBooksBlocking(
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
    ): BookSearchResponse {
        val request = SearchRequest(
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
        return searchBooksBlocking(request)
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
                    TimberLogger.e("SearchService", "JSON解析失败", e)
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