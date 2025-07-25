package com.novel.utils.network.cache

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import kotlinx.coroutines.*
import okhttp3.Request
import okhttp3.Response
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.sorted

/**
 * 请求合并器 - 将相同请求合并，减少重复网络调用
 * 
 * 功能：
 * - 基于请求特征生成唯一键
 * - 相同请求只发送一次，其他等待结果
 * - 支持超时自动清理
 * - 提供统计信息用于监控
 */
@Stable
@Singleton
class RequestCoalescer @Inject constructor() {
    companion object {
        private const val TAG = "RequestCoalescer"
        private const val CLEANUP_TIMEOUT_MS = 30_000L // 30秒后自动清理
        private const val MAX_POOL_SIZE = 100 // 最大池大小
    }
    
    // 待处理请求池
    private val requestPool = ConcurrentHashMap<String, CompletableDeferred<CoalesceResult>>()
    // 统计信息
    private var hitCount = 0
    private var missCount = 0
    private var maxPoolSize = 0
    
    /**
     * 合并请求结果
     */
    @Stable
    sealed class CoalesceResult {
        @Stable
        data class Success(val response: Response) : CoalesceResult()
        @Stable
        data class Error(val exception: Throwable) : CoalesceResult()
    }
    
    /**
     * 执行请求合并
     * @param request 原始请求
     * @param producer 网络请求执行器
     * @return 响应结果
     */
    suspend fun coalesce(
        request: Request,
        producer: suspend () -> Response
    ): Response = withContext(Dispatchers.IO) {
        val key = generateRequestKey(request)
        
        // 检查是否已有相同请求在执行
        val existingDeferred = requestPool[key]
        if (existingDeferred != null) {
            hitCount++
            TimberLogger.d(TAG, "请求合并命中: key=$key")
            return@withContext handleCoalesceResult(existingDeferred.await())
        }
        
        // 创建新的延迟对象
        val deferred = CompletableDeferred<CoalesceResult>()
        requestPool[key] = deferred
        missCount++
        
        // 更新池大小统计
        maxPoolSize = maxOf(maxPoolSize, requestPool.size)
        
        // 设置超时清理
        val cleanupJob = launch {
            delay(CLEANUP_TIMEOUT_MS)
            requestPool.remove(key)
            if (!deferred.isCompleted) {
                deferred.complete(CoalesceResult.Error(Exception("Request timeout during coalescing")))
            }
        }
        
        try {
            TimberLogger.d(TAG, "发起新请求: key=$key")
            val response = producer()
            deferred.complete(CoalesceResult.Success(response))
            return@withContext response
        } catch (e: Exception) {
            TimberLogger.e(TAG, "请求执行失败: key=$key", e)
            deferred.complete(CoalesceResult.Error(e))
            throw e
        } finally {
            requestPool.remove(key)
            cleanupJob.cancel()
        }
    }
    
    /**
     * 生成请求唯一键
     */
    private fun generateRequestKey(request: Request): String {
        val sb = StringBuilder()
        
        // 方法
        sb.append(request.method)
        sb.append("_")
        
        // URL（包含查询参数）
        val url = request.url
        sb.append(url.scheme)
        sb.append("://")
        sb.append(url.host)
        sb.append(url.port)
        sb.append(url.encodedPath)
        
        // 查询参数排序后拼接
        val sortedQuery = url.queryParameterNames.sorted().joinToString("&") { name ->
            val values = mutableListOf<String>()
            for (value in url.queryParameterValues(name)) {
                if (value != null) {
                    values.add(value)
                }
            }
            "$name=${values.sorted().joinToString(",")}"
        }
        if (sortedQuery.isNotEmpty()) {
            sb.append("?")
            sb.append(sortedQuery)
        }
        
        // 请求体哈希（如果存在）
        request.body?.let { body ->
            val buffer = okio.Buffer()
            body.writeTo(buffer)
            val bodyBytes = buffer.readByteArray()
            if (bodyBytes.isNotEmpty()) {
                sb.append("_body:")
                sb.append(hashBytes(bodyBytes))
            }
        }
        
        return sb.toString()
    }
    
    /**
     * 计算字节数组的MD5哈希
     */
    private fun hashBytes(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("MD5")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * 处理合并结果
     */
    private fun handleCoalesceResult(result: CoalesceResult): Response {
        return when (result) {
            is CoalesceResult.Success -> result.response
            is CoalesceResult.Error -> throw result.exception
        }
    }
    
    /**
     * 获取统计信息
     */
    fun getStats(): CoalesceStats {
        val total = hitCount + missCount
        val hitRate = if (total > 0) hitCount.toFloat() / total else 0f
        
        return CoalesceStats(
            hitCount = hitCount,
            missCount = missCount,
            hitRate = hitRate,
            currentPoolSize = requestPool.size,
            maxPoolSize = maxPoolSize
        )
    }
    
    /**
     * 清理所有等待中的请求
     */
    fun clearAll() {
        requestPool.values.forEach { deferred ->
            if (!deferred.isCompleted) {
                deferred.complete(CoalesceResult.Error(Exception("RequestCoalescer cleared")))
            }
        }
        requestPool.clear()
        TimberLogger.d(TAG, "已清理所有待处理请求")
    }
    
    /**
     * 手动清理超时请求
     */
    fun cleanup() {
        val currentTime = System.currentTimeMillis()
        val toRemove = mutableListOf<String>()
        
        // 这里简化实现，实际应该记录请求创建时间
        if (requestPool.size > MAX_POOL_SIZE) {
            TimberLogger.w(TAG, "请求池大小超限，清理最老的请求")
            val keys = requestPool.keys.take(requestPool.size - MAX_POOL_SIZE)
            keys.forEach { key ->
                requestPool.remove(key)?.let { deferred ->
                    if (!deferred.isCompleted) {
                        deferred.complete(CoalesceResult.Error(Exception("Pool size exceeded")))
                    }
                }
            }
        }
    }
}

/**
 * 合并统计信息
 */
@Stable
data class CoalesceStats(
    val hitCount: Int,
    val missCount: Int,
    val hitRate: Float,
    val currentPoolSize: Int,
    val maxPoolSize: Int
) 