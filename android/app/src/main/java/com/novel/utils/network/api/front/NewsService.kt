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

/**
 * 新闻服务API
 * 
 * 功能：
 * - 最新新闻列表获取
 * - 新闻详情内容查询
 * - 分类新闻数据管理
 * 
 * 特点：
 * - 支持协程和回调调用方式
 * - JSON数据自动序列化
 * - 统一错误处理机制
 */
@Singleton
@Stable
class NewsService @Inject constructor(
    @Stable
    private val gson: Gson
) {
    
    // region 数据结构
    data class NewsInfoResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: NewsInfo?,
        @SerializedName("ok") val ok: Boolean?
    )

    @Stable
    data class NewsListResponse(
        @SerializedName("code") val code: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("data") val data: ImmutableList<NewsInfo>?,
        @SerializedName("ok") val ok: Boolean?
    )

    data class NewsInfo(
        @SerializedName("id") val id: Long,
        @SerializedName("categoryId") val categoryId: Long,
        @SerializedName("categoryName") val categoryName: String,
        @SerializedName("sourceName") val sourceName: String,
        @SerializedName("title") val title: String,
        @SerializedName("updateTime") val updateTime: String,
        @SerializedName("content") val content: String?
    )
    // endregion

    // region 网络请求方法
    
    /**
     * 新闻信息查询接口
     */
    private fun getNewsById(
        newsId: Long,
        callback: (NewsInfoResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("NewsService", "开始 getNewsById()，参数：$newsId")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "news/$newsId",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, NewsInfoResponse::class.java, callback)
        }
    }

    /**
     * 最新新闻列表查询接口
     */
    private fun getLatestNews(
        callback: (NewsListResponse?, Throwable?) -> Unit
    ) {
        TimberLogger.d("NewsService", "开始 getLatestNews()")
        
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "news/latest_list",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            handleResponse(response, error, NewsListResponse::class.java, callback)
        }
    }

    // endregion

    // region 协程版本
    suspend fun getNewsByIdBlocking(newsId: Long): NewsInfoResponse {
        return suspendCancellableCoroutine { cont ->
            getNewsById(newsId) { response, error ->
                if (error != null) {
                    cont.resumeWith(Result.failure(error))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.failure(Exception("Response is null")))
                }
            }
        }
    }

    suspend fun getLatestNewsBlocking(): NewsListResponse {
        return suspendCancellableCoroutine { cont ->
            getLatestNews { response, error ->
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
                    callback(gson.fromJson(response, clazz), null)
                } catch (e: Exception) {
                    TimberLogger.e("NewsService", "JSON解析失败", e)
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
