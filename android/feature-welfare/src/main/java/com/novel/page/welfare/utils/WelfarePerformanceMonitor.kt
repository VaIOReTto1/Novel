package com.novel.page.welfare.utils

import androidx.compose.runtime.Stable
import com.novel.core.logging.CoreLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.system.measureTimeMillis

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

    @Stable
    data class PerformanceMetrics(
        val pageLoadTime: Long = 0L,
        val firstContentfulPaint: Long = 0L,
        val timeToInteractive: Long = 0L,
        val memoryUsage: Long = 0L,
        val networkLatency: Long = 0L,
        val errorCount: Int = 0,
        val retryCount: Int = 0
    )

    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    private val timestamps = ConcurrentHashMap<String, Long>()
    private val durations = ConcurrentHashMap<String, Long>()
    private val errorCounter = AtomicLong(0)
    private val retryCounter = AtomicLong(0)
    private val monitorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun startPageLoad(url: String) {
        val startTime = System.currentTimeMillis()
        timestamps["page_load_start"] = startTime
        timestamps["url"] = url.hashCode().toLong()

        CoreLogger.d(TAG, "开始监控页面加载: $url")
        startMemoryMonitoring()
    }

    fun recordFirstContentfulPaint() {
        val startTime = timestamps["page_load_start"] ?: return
        val fcp = System.currentTimeMillis() - startTime
        durations["fcp"] = fcp

        updateMetrics { copy(firstContentfulPaint = fcp) }
        CoreLogger.d(TAG, "首次内容绘制时间: ${fcp}ms")
    }

    fun recordPageLoadComplete() {
        val startTime = timestamps["page_load_start"] ?: return
        val loadTime = System.currentTimeMillis() - startTime
        durations["page_load"] = loadTime

        updateMetrics { copy(pageLoadTime = loadTime) }
        CoreLogger.i(TAG, "页面加载完成，耗时: ${loadTime}ms")
    }

    fun recordTimeToInteractive() {
        val startTime = timestamps["page_load_start"] ?: return
        val tti = System.currentTimeMillis() - startTime
        durations["tti"] = tti

        updateMetrics { copy(timeToInteractive = tti) }
        CoreLogger.d(TAG, "页面可交互时间: ${tti}ms")
    }

    fun recordNetworkLatency(latency: Long) {
        updateMetrics { copy(networkLatency = latency) }
        CoreLogger.d(TAG, "网络延迟: ${latency}ms")
    }

    fun recordError(errorType: String, errorMessage: String) {
        val errorCount = errorCounter.incrementAndGet()
        updateMetrics { copy(errorCount = errorCount.toInt()) }
        CoreLogger.w(TAG, "记录错误 [$errorType]: $errorMessage (总计: $errorCount)")
    }

    fun recordRetry(reason: String) {
        val retryCount = retryCounter.incrementAndGet()
        updateMetrics { copy(retryCount = retryCount.toInt()) }
        CoreLogger.d(TAG, "记录重试: $reason (总计: $retryCount)")
    }

    fun recordViewModelAction(action: String) {
        CoreLogger.d(TAG, "ViewModel操作: $action")
    }

    fun recordWebViewLoadStart() {
        timestamps["webview_load_start"] = System.currentTimeMillis()
        CoreLogger.d(TAG, "WebView开始加载")
    }

    fun recordWebViewLoadComplete() {
        val startTime = timestamps["webview_load_start"] ?: return
        val loadTime = System.currentTimeMillis() - startTime
        durations["webview_load"] = loadTime
        CoreLogger.d(TAG, "WebView加载完成，耗时: ${loadTime}ms")
    }

    fun measureOperation(operationName: String, operation: () -> Unit): Long {
        val duration = measureTimeMillis(operation)
        durations[operationName] = duration
        CoreLogger.d(TAG, "操作 [$operationName] 耗时: ${duration}ms")
        return duration
    }

    private fun startMemoryMonitoring() {
        monitorScope.launch {
            while (isActive) {
                try {
                    val runtime = Runtime.getRuntime()
                    val usedMemory = runtime.totalMemory() - runtime.freeMemory()
                    updateMetrics { copy(memoryUsage = usedMemory) }
                    delay(1000)
                } catch (e: Exception) {
                    CoreLogger.e(TAG, "内存监控出错", e)
                    break
                }
            }
        }
    }

    fun getPerformanceReport(): String {
        val metrics = performanceMetrics.value
        return buildString {
            appendLine("=== Welfare模块性能报告 ===")
            appendLine("页面加载时间: ${metrics.pageLoadTime}ms")
            appendLine("首次内容绘制: ${metrics.firstContentfulPaint}ms")
            appendLine("可交互时间: ${metrics.timeToInteractive}ms")
            appendLine("内存使用: ${metrics.memoryUsage} bytes")
            appendLine("网络延迟: ${metrics.networkLatency}ms")
            appendLine("错误次数: ${metrics.errorCount}")
            appendLine("重试次数: ${metrics.retryCount}")
        }
    }

    fun reset() {
        timestamps.clear()
        durations.clear()
        errorCounter.set(0)
        retryCounter.set(0)
        _performanceMetrics.value = PerformanceMetrics()
        CoreLogger.d(TAG, "性能监控数据已重置")
    }

    fun cleanup() {
        monitorScope.cancel()
        CoreLogger.d(TAG, "性能监控器已清理")
    }

    private inline fun updateMetrics(
        transform: PerformanceMetrics.() -> PerformanceMetrics
    ) {
        _performanceMetrics.value = _performanceMetrics.value.transform()
    }
}
