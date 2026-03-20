package com.novel.page.welfare.utils

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

/**
 * Welfare模块性能监控器
 * 
 * 功能特点：
 * - WebView加载性能监控
 * - 页面渲染时间统计
 * - 内存使用情况跟踪
 * - 网络请求性能分析
 * - 用户交互响应时间
 * 
 * 性能指标：
 * - 首次内容绘制时间 (FCP)
 * - 页面完全加载时间 (PLT)
 * - 交互响应时间 (TTI)
 * - 内存峰值使用量
 */
@Stable
class WelfarePerformanceMonitor private constructor() {
    
    companion object {
        private const val TAG = "WelfarePerformanceMonitor"
        
        @Volatile
        private var INSTANCE: WelfarePerformanceMonitor? = null
        
        fun getInstance(): WelfarePerformanceMonitor {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: WelfarePerformanceMonitor().also { INSTANCE = it }
            }
        }
    }
    
    // 性能指标数据类
    @Stable
    data class PerformanceMetrics(
        val pageLoadTime: Long = 0L,           // 页面加载时间
        val firstContentfulPaint: Long = 0L,   // 首次内容绘制
        val timeToInteractive: Long = 0L,      // 可交互时间
        val memoryUsage: Long = 0L,            // 内存使用量
        val networkLatency: Long = 0L,         // 网络延迟
        val errorCount: Int = 0,               // 错误次数
        val retryCount: Int = 0                // 重试次数
    )
    
    // 性能数据流
    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()
    
    // 时间戳记录
    private val timestamps = ConcurrentHashMap<String, Long>()
    private val durations = ConcurrentHashMap<String, Long>()
    
    // 计数器
    private val errorCounter = AtomicLong(0)
    private val retryCounter = AtomicLong(0)
    
    // 协程作用域
    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * 开始监控页面加载
     */
    fun startPageLoad(url: String) {
        val startTime = System.currentTimeMillis()
        timestamps["page_load_start"] = startTime
        timestamps["url"] = url.hashCode().toLong()
        
        TimberLogger.d(TAG, "开始监控页面加载: $url")
        
        // 开始内存监控
        startMemoryMonitoring()
    }
    
    /**
     * 记录首次内容绘制
     */
    fun recordFirstContentfulPaint() {
        val currentTime = System.currentTimeMillis()
        val startTime = timestamps["page_load_start"] ?: currentTime
        val fcp = currentTime - startTime
        
        durations["first_contentful_paint"] = fcp
        updateMetrics { it.copy(firstContentfulPaint = fcp) }
        
        TimberLogger.d(TAG, "首次内容绘制时间: ${fcp}ms")
    }
    
    /**
     * 记录页面加载完成
     */
    fun recordPageLoadComplete() {
        val currentTime = System.currentTimeMillis()
        val startTime = timestamps["page_load_start"] ?: currentTime
        val loadTime = currentTime - startTime
        
        durations["page_load_time"] = loadTime
        updateMetrics { it.copy(pageLoadTime = loadTime) }
        
        TimberLogger.i(TAG, "页面加载完成，耗时: ${loadTime}ms")
        
        // 停止内存监控
        stopMemoryMonitoring()
    }
    
    /**
     * 记录可交互时间
     */
    fun recordTimeToInteractive() {
        val currentTime = System.currentTimeMillis()
        val startTime = timestamps["page_load_start"] ?: currentTime
        val tti = currentTime - startTime
        
        durations["time_to_interactive"] = tti
        updateMetrics { it.copy(timeToInteractive = tti) }
        
        TimberLogger.d(TAG, "页面可交互时间: ${tti}ms")
    }
    
    /**
     * 记录网络请求性能
     */
    fun recordNetworkLatency(latency: Long) {
        durations["network_latency"] = latency
        updateMetrics { it.copy(networkLatency = latency) }
        
        TimberLogger.d(TAG, "网络延迟: ${latency}ms")
    }
    
    /**
     * 记录错误
     */
    fun recordError(errorType: String, errorMessage: String) {
        val errorCount = errorCounter.incrementAndGet()
        updateMetrics { it.copy(errorCount = errorCount.toInt()) }
        
        TimberLogger.w(TAG, "记录错误 [$errorType]: $errorMessage (总计: $errorCount)")
    }
    
    /**
     * 记录重试
     */
    fun recordRetry(reason: String) {
        val retryCount = retryCounter.incrementAndGet()
        updateMetrics { it.copy(retryCount = retryCount.toInt()) }
        
        TimberLogger.d(TAG, "记录重试: $reason (总计: $retryCount)")
    }
    
    /**
     * 记录ViewModel操作
     */
    fun recordViewModelAction(action: String) {
        val currentTime = System.currentTimeMillis()
        timestamps["viewmodel_$action"] = currentTime
        
        TimberLogger.d(TAG, "ViewModel操作: $action")
    }
    
    /**
     * 记录WebView加载开始
     */
    fun recordWebViewLoadStart() {
        val currentTime = System.currentTimeMillis()
        timestamps["webview_load_start"] = currentTime
        
        TimberLogger.d(TAG, "WebView开始加载")
    }
    
    /**
     * 记录WebView加载完成
     */
    fun recordWebViewLoadComplete() {
        val currentTime = System.currentTimeMillis()
        val startTime = timestamps["webview_load_start"] ?: currentTime
        val loadTime = currentTime - startTime
        
        durations["webview_load_time"] = loadTime
        TimberLogger.d(TAG, "WebView加载完成，耗时: ${loadTime}ms")
    }
    
    /**
     * 测量操作执行时间
     */
    suspend fun <T> measureOperation(
        operationName: String,
        operation: suspend () -> T
    ): T {
        var result: T
        val duration = measureTimeMillis {
            result = operation()
        }
        
        durations[operationName] = duration
        TimberLogger.d(TAG, "操作 [$operationName] 耗时: ${duration}ms")
        
        return result
    }
    
    /**
     * 开始内存监控
     */
    private fun startMemoryMonitoring() {
        monitorScope.launch {
            while (isActive) {
                try {
                    val runtime = Runtime.getRuntime()
                    val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                    
                    updateMetrics { it.copy(memoryUsage = usedMemory) }
                    
                    delay(1000) // 每秒检查一次
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "内存监控出错", e)
                    break
                }
            }
        }
    }
    
    /**
     * 停止内存监控
     */
    private fun stopMemoryMonitoring() {
        // 内存监控会在协程作用域取消时自动停止
    }
    
    /**
     * 更新性能指标
     */
    private fun updateMetrics(update: (PerformanceMetrics) -> PerformanceMetrics) {
        _performanceMetrics.value = update(_performanceMetrics.value)
    }
    
    /**
     * 获取性能报告
     */
    fun getPerformanceReport(): String {
        val metrics = _performanceMetrics.value
        return buildString {
            appendLine("=== Welfare模块性能报告 ===")
            appendLine("页面加载时间: ${metrics.pageLoadTime}ms")
            appendLine("首次内容绘制: ${metrics.firstContentfulPaint}ms")
            appendLine("可交互时间: ${metrics.timeToInteractive}ms")
            appendLine("内存使用量: ${formatBytes(metrics.memoryUsage)}")
            appendLine("网络延迟: ${metrics.networkLatency}ms")
            appendLine("错误次数: ${metrics.errorCount}")
            appendLine("重试次数: ${metrics.retryCount}")
            appendLine("==============================")
            
            // 详细操作时间
            if (durations.isNotEmpty()) {
                appendLine("\n详细操作时间:")
                durations.forEach { (operation, duration) ->
                    appendLine("  $operation: ${duration}ms")
                }
            }
        }
    }
    
    /**
     * 重置性能数据
     */
    fun reset() {
        timestamps.clear()
        durations.clear()
        errorCounter.set(0)
        retryCounter.set(0)
        _performanceMetrics.value = PerformanceMetrics()
        
        TimberLogger.d(TAG, "性能监控数据已重置")
    }
    
    /**
     * 清理资源
     */
    fun cleanup() {
        monitorScope.cancel()
        reset()
        TimberLogger.d(TAG, "性能监控器已清理")
    }
    
    /**
     * 格式化字节数
     */
    private fun formatBytes(bytes: Long): String {
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        
        return when {
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$bytes B"
        }
    }
    
    /**
     * 获取性能等级评估
     */
    fun getPerformanceGrade(): String {
        val metrics = _performanceMetrics.value
        
        return when {
            metrics.pageLoadTime <= 1000 && metrics.errorCount == 0 -> "优秀"
            metrics.pageLoadTime <= 2000 && metrics.errorCount <= 1 -> "良好"
            metrics.pageLoadTime <= 3000 && metrics.errorCount <= 3 -> "一般"
            else -> "需要优化"
        }
    }
    
    /**
     * 导出性能数据
     */
    fun exportMetrics(): Map<String, Any> {
        val metrics = _performanceMetrics.value
        return mapOf(
            "pageLoadTime" to metrics.pageLoadTime,
            "firstContentfulPaint" to metrics.firstContentfulPaint,
            "timeToInteractive" to metrics.timeToInteractive,
            "memoryUsage" to metrics.memoryUsage,
            "networkLatency" to metrics.networkLatency,
            "errorCount" to metrics.errorCount,
            "retryCount" to metrics.retryCount,
            "performanceGrade" to getPerformanceGrade(),
            "timestamp" to System.currentTimeMillis(),
            "detailedDurations" to durations.toMap()
        )
    }
}
