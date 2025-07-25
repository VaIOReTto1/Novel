package com.novel.utils.network.interceptor

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.cache.RequestCoalescer
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 请求合并拦截器
 * 
 * 功能：
 * - 自动检测并合并相同的HTTP请求
 * - 集成到OkHttp拦截器链中
 * - 支持统计和监控
 */
@Stable
@Singleton
class RequestCoalescingInterceptor @Inject constructor(
    private val requestCoalescer: RequestCoalescer
) : Interceptor {
    
    companion object {
        private const val TAG = "RequestCoalescingInterceptor"
        
        // 不进行合并的请求类型
        private val NON_COALESCABLE_METHODS = setOf("POST", "PUT", "PATCH", "DELETE")
        private val STREAMING_CONTENT_TYPES = setOf(
            "application/octet-stream",
            "multipart/form-data",
            "application/x-www-form-urlencoded"
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // 检查请求是否适合合并
        if (!shouldCoalesce(request)) {
            TimberLogger.d(TAG, "请求不适合合并: ${request.method} ${request.url}")
            return chain.proceed(request)
        }
        
        // 使用协程阻塞等待合并结果
        return runBlocking {
            try {
                requestCoalescer.coalesce(request) {
                    // 在合并器中执行实际的网络请求
                    chain.proceed(request)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "请求合并处理异常: ${request.url}", e)
                throw e
            }
        }
    }
    
    /**
     * 判断请求是否应该进行合并
     */
    private fun shouldCoalesce(request: okhttp3.Request): Boolean {
        // 非GET请求通常不适合合并
        if (request.method in NON_COALESCABLE_METHODS) {
            return false
        }
        
        // 检查是否有请求体
        val requestBody = request.body
        if (requestBody != null) {
            val contentType = requestBody.contentType()?.toString()?.lowercase()
            
            // 流式或表单数据不适合合并
            if (contentType != null && STREAMING_CONTENT_TYPES.any { it in contentType }) {
                return false
            }
            
            // 大于1MB的请求体不进行合并
            val contentLength = requestBody.contentLength()
            if (contentLength > 1024 * 1024) {
                return false
            }
        }
        
        // 检查请求头是否包含不合并标记
        if (request.header("X-No-Coalesce") != null) {
            return false
        }
        
        // 检查是否为下载请求
        val acceptHeader = request.header("Accept")?.lowercase()
        if (acceptHeader != null) {
            val downloadContentTypes = setOf(
                "application/octet-stream",
                "image/",
                "video/",
                "audio/"
            )
            if (downloadContentTypes.any { acceptHeader.contains(it) }) {
                return false
            }
        }
        
        return true
    }
    
    /**
     * 获取合并统计信息
     */
    fun getStats() = requestCoalescer.getStats()
}

/**
 * 请求合并拦截器的扩展方法
 */
fun okhttp3.Request.withoutCoalescing(): okhttp3.Request {
    return this.newBuilder()
        .header("X-No-Coalesce", "true")
        .build()
} 