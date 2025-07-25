package com.novel.utils.network.interceptor

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.NetworkMonitor
import com.novel.utils.network.NetworkType
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min
import kotlin.math.pow

/**
 * 重试策略配置
 */
@Stable
data class RetryConfig(
    val maxRetries: Int,
    val baseDelayMs: Long,
    val maxDelayMs: Long,
    val backoffMultiplier: Double = 1.5,
    val jitterFactor: Double = 0.1
)

/**
 * 智能重试拦截器
 * 
 * 功能：
 * - 基于网络状态自适应重试策略
 * - 指数退避算法
 * - 条件化重试（只重试可恢复的错误）
 * - 支持抖动减少惊群效应
 */
@Stable
@Singleton
class SmartRetryInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {
    
    companion object {
        private const val TAG = "SmartRetryInterceptor"
        
        // 默认重试配置
        private val WIFI_RETRY_CONFIG = RetryConfig(
            maxRetries = 3,
            baseDelayMs = 500L,
            maxDelayMs = 5000L
        )
        
        private val CELLULAR_RETRY_CONFIG = RetryConfig(
            maxRetries = 4,
            baseDelayMs = 1000L,
            maxDelayMs = 10000L
        )
        
        private val POOR_NETWORK_RETRY_CONFIG = RetryConfig(
            maxRetries = 5,
            baseDelayMs = 2000L,
            maxDelayMs = 20000L,
            backoffMultiplier = 2.0
        )
        
        // 可重试的HTTP状态码
        private val RETRYABLE_STATUS_CODES = setOf(
            408, // Request Timeout
            429, // Too Many Requests
            500, // Internal Server Error
            502, // Bad Gateway
            503, // Service Unavailable
            504, // Gateway Timeout
            507, // Insufficient Storage
            511  // Network Authentication Required
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val retryConfig = getRetryConfig()
        
        var lastResponse: Response? = null
        var lastException: Exception? = null
        
        // 添加重试标记到请求头，避免上层拦截器重复处理
        val request = originalRequest.newBuilder()
            .header("X-Retry-Interceptor", "true")
            .build()
        
        for (attemptIndex in 0..retryConfig.maxRetries) {
            try {
                TimberLogger.d(TAG, "请求尝试 ${attemptIndex + 1}/${retryConfig.maxRetries + 1}: ${request.url}")
                
                val response = chain.proceed(request)
                
                // 检查响应是否需要重试
                if (shouldRetryResponse(response, attemptIndex, retryConfig.maxRetries)) {
                    response.close() // 关闭响应以释放连接
                    lastResponse = null
                    
                    if (attemptIndex < retryConfig.maxRetries) {
                        val delay = calculateDelay(attemptIndex, retryConfig)
                        TimberLogger.w(TAG, "HTTP ${response.code} - 将在 ${delay}ms 后重试")
                        Thread.sleep(delay)
                        continue
                    }
                } else {
                    // 成功或不可重试的错误
                    TimberLogger.d(TAG, "请求成功: ${response.code} ${request.url}")
                    return response
                }
                
                lastResponse = response
                
            } catch (e: Exception) {
                TimberLogger.w(TAG, "请求异常 (尝试 ${attemptIndex + 1}): ${e.message}")
                lastException = e
                
                // 检查异常是否可重试
                if (shouldRetryException(e, attemptIndex, retryConfig.maxRetries)) {
                    if (attemptIndex < retryConfig.maxRetries) {
                        val delay = calculateDelay(attemptIndex, retryConfig)
                        TimberLogger.w(TAG, "异常重试 - 将在 ${delay}ms 后重试: ${e.javaClass.simpleName}")
                        Thread.sleep(delay)
                        continue
                    }
                }
                
                // 不可重试的异常或重试次数用完
                throw e
            }
        }
        
        // 所有重试都失败了
        lastResponse?.let { response ->
            TimberLogger.e(TAG, "重试失败，返回最后响应: ${response.code}")
            return response
        }
        
        lastException?.let { exception ->
            TimberLogger.e(TAG, "重试失败，抛出异常: ${exception.message}")
            throw exception
        }
        
        // 理论上不应该到达这里
        throw IOException("All retry attempts failed")
    }
    
    /**
     * 获取适应当前网络状态的重试配置
     */
    private fun getRetryConfig(): RetryConfig {
        val networkState = networkMonitor.networkState.value
        val networkQuality = networkMonitor.getNetworkQuality()
        
        return when {
            !networkState.isConnected -> RetryConfig(0, 0L, 0L) // 无网络不重试
            networkState.type == NetworkType.WIFI -> WIFI_RETRY_CONFIG
            networkState.type == NetworkType.CELLULAR -> {
                if (networkQuality < 30) {
                    // 网络质量差，使用更保守的重试策略
                    POOR_NETWORK_RETRY_CONFIG
                } else {
                    CELLULAR_RETRY_CONFIG
                }
            }
            else -> CELLULAR_RETRY_CONFIG
        }
    }
    
    /**
     * 判断响应是否需要重试
     */
    private fun shouldRetryResponse(response: Response, attemptIndex: Int, maxRetries: Int): Boolean {
        // 已达到最大重试次数
        if (attemptIndex >= maxRetries) return false
        
        // 检查状态码是否可重试
        if (response.code in RETRYABLE_STATUS_CODES) {
            TimberLogger.d(TAG, "HTTP ${response.code} 可重试")
            return true
        }
        
        // 检查是否有 Retry-After 头
        val retryAfter = response.header("Retry-After")
        if (retryAfter != null && response.code == 429) {
            TimberLogger.d(TAG, "429 Too Many Requests with Retry-After: $retryAfter")
            return true
        }
        
        return false
    }
    
    /**
     * 判断异常是否需要重试
     */
    private fun shouldRetryException(exception: Exception, attemptIndex: Int, maxRetries: Int): Boolean {
        // 已达到最大重试次数
        if (attemptIndex >= maxRetries) return false
        
        return when (exception) {
            is SocketTimeoutException -> {
                TimberLogger.d(TAG, "SocketTimeoutException 可重试")
                true
            }
            is UnknownHostException -> {
                // DNS解析失败，在移动网络下可能是临时问题
                val networkType = networkMonitor.getCurrentNetworkType()
                val canRetry = networkType == NetworkType.CELLULAR
                TimberLogger.d(TAG, "UnknownHostException ${if (canRetry) "可重试" else "不可重试"} (网络类型: $networkType)")
                canRetry
            }
            is IOException -> {
                // 其他IO异常，根据消息判断
                val message = exception.message?.lowercase() ?: ""
                val canRetry = when {
                    "connection reset" in message -> true
                    "broken pipe" in message -> true
                    "connection refused" in message -> false // 服务器拒绝连接，重试无意义
                    "network is unreachable" in message -> false
                    else -> true // 默认可重试
                }
                TimberLogger.d(TAG, "IOException ${if (canRetry) "可重试" else "不可重试"}: $message")
                canRetry
            }
            else -> {
                TimberLogger.d(TAG, "${exception.javaClass.simpleName} 不可重试")
                false
            }
        }
    }
    
    /**
     * 计算重试延迟时间（指数退避 + 抖动）
     */
    private fun calculateDelay(attemptIndex: Int, config: RetryConfig): Long {
        // 指数退避
        val exponentialDelay = config.baseDelayMs * config.backoffMultiplier.pow(attemptIndex.toDouble()).toLong()
        
        // 限制最大延迟
        val clampedDelay = min(exponentialDelay, config.maxDelayMs)
        
        // 添加抖动减少惊群效应
        val jitter = clampedDelay * config.jitterFactor * (Math.random() - 0.5)
        val finalDelay = (clampedDelay + jitter).toLong()
        
        return maxOf(finalDelay, 0L)
    }
    
    /**
     * 检查请求是否已被此拦截器处理过
     */
    private fun isRetryRequest(request: Request): Boolean {
        return request.header("X-Retry-Interceptor") != null
    }
}

/**
 * 重试统计信息
 */
@Stable
data class RetryStats(
    val totalRequests: Long,
    val retriedRequests: Long,
    val retrySuccessCount: Long,
    val retryFailureCount: Long,
    val avgRetriesPerRequest: Double
) 