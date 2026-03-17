package com.novel.utils.network.api.front

import androidx.compose.runtime.Stable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.core.result.AppError
import com.novel.core.result.DataResult
import com.novel.utils.TimberLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class HomeService @Inject constructor(
    @Stable
    private val gson: Gson,
    private val networkFacade: NetworkFacade
) {
    
    // region 数据结构
    @Stable
    data class HomeBooksResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<HomeBook>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class HomeBook(
        @SerializedName("type") val type: Int, // 0-轮播图 1-顶部栏 2-本周强推 3-热门推荐 4-精品推荐
        @SerializedName("bookId") val bookId: Long,
        @SerializedName("picUrl") val picUrl: String,
        @SerializedName("bookName") val bookName: String,
        @SerializedName("authorName") val authorName: String,
        @SerializedName("bookDesc") val bookDesc: String
    )

    @Stable
    data class FriendLinksResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<FriendLink>?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class FriendLink(
        @SerializedName("linkName") val linkName: String,
        @SerializedName("linkUrl") val linkUrl: String
    )
    // endregion

    // region 网络请求方法
    
    /**
     * 首页小说推荐查询接口
     */
    private fun getHomeBooks(
        callback: (HomeBooksResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("HomeService", "开始 getHomeBooks()")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse("home/books", HomeBooksResponse::class.java)
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * 首页友情链接列表查询接口
     */
    private fun getFriendLinks(
        callback: (FriendLinksResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("HomeService", "开始 getFriendLinks()")

        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                requestAndParse("home/friend_Link/list", FriendLinksResponse::class.java)
            }.onSuccess { response ->
                callback(response, null)
            }.onFailure { error ->
                callback(null, error)
            }
        }
    }

    /**
     * 获取特定类型的推荐书籍
     */
    private fun getBooksByType(
        type: Int,
        callback: (List<HomeBook>?, Throwable?) -> Unit
    ) {
        getHomeBooks { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val filteredBooks = response?.data?.filter { it.type == type }
                callback(filteredBooks, null)
            }
        }
    }

    /**
     * 获取轮播图书籍
     */
    private fun getCarouselBooks(callback: (List<HomeBook>?, Throwable?) -> Unit) {
        getBooksByType(0, callback)
    }

    /**
     * 获取顶部栏书籍
     */
    private fun getTopBarBooks(callback: (List<HomeBook>?, Throwable?) -> Unit) {
        getBooksByType(1, callback)
    }

    /**
     * 获取本周强推书籍
     */
    fun getWeeklyRecommendBooks(callback: (List<HomeBook>?, Throwable?) -> Unit) {
        getBooksByType(2, callback)
    }

    /**
     * 获取热门推荐书籍
     */
    fun getHotRecommendBooks(callback: (List<HomeBook>?, Throwable?) -> Unit) {
        getBooksByType(3, callback)
    }

    /**
     * 获取精品推荐书籍
     */
    fun getPremiumRecommendBooks(callback: (List<HomeBook>?, Throwable?) -> Unit) {
        getBooksByType(4, callback)
    }

    // endregion

    // region 协程版本
    suspend fun getHomeBooksBlocking(): HomeBooksResponse {
        return requestAndParse("home/books", HomeBooksResponse::class.java)
    }

    suspend fun getFriendLinksBlocking(): FriendLinksResponse {
        return requestAndParse("home/friend_Link/list", FriendLinksResponse::class.java)
    }

    suspend fun getHomeBooksResult(): DataResult<HomeBooksResponse> =
        runResulting { getHomeBooksBlocking() }

    suspend fun getFriendLinksResult(): DataResult<FriendLinksResponse> =
        runResulting { getFriendLinksBlocking() }

    suspend fun getCarouselBooksBlocking(): List<HomeBook> {
        return suspendCancellableCoroutine { cont ->
            getCarouselBooks { books, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    books?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Books is null")))
                }
            }
        }
    }
    // endregion

    private suspend fun <T> requestAndParse(
        endpoint: String,
        clazz: Class<T>
    ): T {
        val response = networkFacade.execute(
            NetworkRequest(
                baseUrl = com.novel.utils.network.ApiService.BASE_URL_FRONT,
                endpoint = endpoint,
                method = NetworkRequestMethod.GET,
                headers = mapOf("Accept" to "*/*")
            )
        )

        return gson.fromJson(response, clazz)
    }

    private suspend fun <T> runResulting(block: suspend () -> T): DataResult<T> =
        try {
            DataResult.Success(block())
        } catch (throwable: Throwable) {
            TimberLogger.e("HomeService", "DataResult request failed", throwable)
            DataResult.Failure(AppError.fromThrowable(throwable))
        }
    // endregion
}
