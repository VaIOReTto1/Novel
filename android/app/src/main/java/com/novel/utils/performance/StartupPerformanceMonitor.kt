package com.novel.utils.performance

import android.app.Application
import android.content.Context
import android.os.Process
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 启动性能监控器
 * 
 * 功能：
 * - 冷启动时间监控
 * - 组件初始化耗时分析
 * - 内存使用监控
 * - 首帧渲染时间统计
 * - 性能报告生成
 */
@Stable
@Singleton
class StartupPerformanceMonitor @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "StartupPerformanceMonitor"
        private const val REPORT_DELAY_MS = 5000L // 5秒后生成报告
    }

    private val isMonitoring = AtomicBoolean(false)
    private val startupMetrics = ConcurrentHashMap<String, StartupMetric>()
    private val componentInitTimes = ConcurrentHashMap<String, Long>()
    
    // 核心时间点
    private val processStartTime = AtomicLong(0)
    private val applicationCreateTime = AtomicLong(0)
    private val firstActivityCreateTime = AtomicLong(0)
    private val firstFrameDrawnTime = AtomicLong(0)
    private val appFullyLoadedTime = AtomicLong(0)

    // 性能状态
    private val _performanceState = MutableStateFlow(PerformanceState.IDLE)
    val performanceState: StateFlow<PerformanceState> = _performanceState.asStateFlow()

    // 监控作用域
    private val monitoringScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * 启动性能监控状态
     */
    enum class PerformanceState {
        IDLE,           // 空闲状态
        MONITORING,     // 监控中
        ANALYZING,      // 分析中
        COMPLETED       // 完成
    }

    /**
     * 启动指标数据类
     */
    @Stable
    data class StartupMetric(
        val name: String,
        val startTime: Long,
        val endTime: Long = 0,
        val duration: Long = 0,
        val description: String = ""
    ) {
        val isCompleted: Boolean get() = endTime > 0
        val actualDuration: Long get() = if (isCompleted) endTime - startTime else System.currentTimeMillis() - startTime
    }

    /**
     * 性能报告数据类
     */
    @Stable
    data class PerformanceReport(
        val totalStartupTime: Long,
        val applicationInitTime: Long,
        val firstActivityTime: Long,
        val firstFrameTime: Long,
        val fullyLoadedTime: Long,
        val componentMetrics: Map<String, Long>,
        val memoryUsage: MemoryMetrics,
        val recommendations: List<String>
    )

    /**
     * 内存指标数据类
     */
    @Stable
    data class MemoryMetrics(
        val totalMemory: Long,
        val usedMemory: Long,
        val availableMemory: Long,
        val maxHeapSize: Long,
        val usedHeapSize: Long
    )

    /**
     * 开始监控进程启动
     */
    fun startProcessMonitoring() {
        if (isMonitoring.compareAndSet(false, true)) {
            processStartTime.set(System.currentTimeMillis())
            _performanceState.value = PerformanceState.MONITORING
            
            TimberLogger.i(TAG, "🚀 开始启动性能监控 - 进程启动时间: ${processStartTime.get()}")
            
            // 记录进程启动指标
            startupMetrics["process_start"] = StartupMetric(
                name = "process_start",
                startTime = processStartTime.get(),
                description = "进程启动到虚拟机初始化完成"
            )
        }
    }

    /**
     * 记录Application onCreate开始
     */
    fun onApplicationCreateStart() {
        val currentTime = System.currentTimeMillis()
        applicationCreateTime.set(currentTime)
        
        TimberLogger.d(TAG, "📱 Application onCreate 开始: $currentTime")
        
        startupMetrics["application_create"] = StartupMetric(
            name = "application_create",
            startTime = currentTime,
            description = "Application.onCreate方法执行"
        )
    }

    /**
     * 记录Application onCreate完成
     */
    fun onApplicationCreateEnd() {
        val currentTime = System.currentTimeMillis()
        
        startupMetrics["application_create"]?.let { metric ->
            startupMetrics["application_create"] = metric.copy(
                endTime = currentTime,
                duration = currentTime - metric.startTime
            )
        }
        
        val duration = currentTime - applicationCreateTime.get()
        TimberLogger.i(TAG, "✅ Application onCreate 完成，耗时: ${duration}ms")
    }

    /**
     * 记录首个Activity创建
     */
    fun onFirstActivityCreate() {
        val currentTime = System.currentTimeMillis()
        firstActivityCreateTime.set(currentTime)
        
        TimberLogger.d(TAG, "🎬 首个Activity创建: $currentTime")
        
        startupMetrics["first_activity"] = StartupMetric(
            name = "first_activity",
            startTime = currentTime,
            description = "首个Activity创建到完成"
        )
    }

    /**
     * 记录首帧绘制完成
     */
    fun onFirstFrameDrawn() {
        val currentTime = System.currentTimeMillis()
        firstFrameDrawnTime.set(currentTime)
        
        // 完成首个Activity指标
        startupMetrics["first_activity"]?.let { metric ->
            startupMetrics["first_activity"] = metric.copy(
                endTime = currentTime,
                duration = currentTime - metric.startTime
            )
        }
        
        // 记录首帧绘制指标
        startupMetrics["first_frame"] = StartupMetric(
            name = "first_frame",
            startTime = firstActivityCreateTime.get(),
            endTime = currentTime,
            duration = currentTime - firstActivityCreateTime.get(),
            description = "Activity创建到首帧渲染完成"
        )
        
        val totalTime = currentTime - processStartTime.get()
        TimberLogger.i(TAG, "🎨 首帧绘制完成，总启动时间: ${totalTime}ms")
    }

    /**
     * 记录应用完全加载完成
     */
    fun onAppFullyLoaded() {
        val currentTime = System.currentTimeMillis()
        appFullyLoadedTime.set(currentTime)
        
        startupMetrics["fully_loaded"] = StartupMetric(
            name = "fully_loaded",
            startTime = processStartTime.get(),
            endTime = currentTime,
            duration = currentTime - processStartTime.get(),
            description = "应用完全加载并可交互"
        )
        
        val totalTime = currentTime - processStartTime.get()
        TimberLogger.i(TAG, "🎯 应用完全加载完成，总耗时: ${totalTime}ms")
        
        // 延迟生成性能报告
        schedulePerformanceReport()
    }

    /**
     * 记录组件初始化时间
     */
    fun recordComponentInitTime(componentName: String, duration: Long) {
        componentInitTimes[componentName] = duration
        TimberLogger.d(TAG, "⚙️ 组件 $componentName 初始化耗时: ${duration}ms")
    }

    /**
     * 获取当前内存使用情况
     */
    private fun getCurrentMemoryMetrics(): MemoryMetrics {
        val runtime = Runtime.getRuntime()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        return MemoryMetrics(
            totalMemory = memInfo.totalMem,
            usedMemory = memInfo.totalMem - memInfo.availMem,
            availableMemory = memInfo.availMem,
            maxHeapSize = runtime.maxMemory(),
            usedHeapSize = runtime.totalMemory() - runtime.freeMemory()
        )
    }

    /**
     * 安排性能报告生成
     */
    private fun schedulePerformanceReport() {
        monitoringScope.launch {
            delay(REPORT_DELAY_MS)
            generatePerformanceReport()
        }
    }

    /**
     * 生成性能报告
     */
    private suspend fun generatePerformanceReport() {
        withContext(Dispatchers.IO) {
            _performanceState.value = PerformanceState.ANALYZING
            
            TimberLogger.i(TAG, "📊 开始生成启动性能报告...")
            
            val report = PerformanceReport(
                totalStartupTime = appFullyLoadedTime.get() - processStartTime.get(),
                applicationInitTime = startupMetrics["application_create"]?.duration ?: 0,
                firstActivityTime = startupMetrics["first_activity"]?.duration ?: 0,
                firstFrameTime = startupMetrics["first_frame"]?.duration ?: 0,
                fullyLoadedTime = startupMetrics["fully_loaded"]?.duration ?: 0,
                componentMetrics = componentInitTimes.toMap(),
                memoryUsage = getCurrentMemoryMetrics(),
                recommendations = generateRecommendations()
            )
            
            logPerformanceReport(report)
            _performanceState.value = PerformanceState.COMPLETED
        }
    }

    /**
     * 生成性能优化建议
     */
    private fun generateRecommendations(): List<String> {
        val recommendations = mutableListOf<String>()
        
        val totalStartupTime = appFullyLoadedTime.get() - processStartTime.get()
        val applicationInitTime = startupMetrics["application_create"]?.duration ?: 0
        val firstFrameTime = startupMetrics["first_frame"]?.duration ?: 0
        
        // 启动时间分析
        when {
            totalStartupTime > 3000 -> {
                recommendations.add("总启动时间(${totalStartupTime}ms)较长，建议进一步优化初始化流程")
            }
            totalStartupTime > 2000 -> {
                recommendations.add("总启动时间(${totalStartupTime}ms)中等，可考虑延迟非关键组件初始化")
            }
            else -> {
                recommendations.add("总启动时间(${totalStartupTime}ms)良好")
            }
        }

        // Application初始化分析
        if (applicationInitTime > 500) {
            recommendations.add("Application初始化时间(${applicationInitTime}ms)较长，建议将非关键初始化移至后台")
        }

        // 首帧渲染分析
        if (firstFrameTime > 500) {
            recommendations.add("首帧渲染时间(${firstFrameTime}ms)较长，建议优化布局复杂度和减少主线程工作")
        }

        // 组件初始化分析
        componentInitTimes.forEach { (component, time) ->
            if (time > 200) {
                recommendations.add("组件 $component 初始化时间(${time}ms)较长，建议优化或延迟加载")
            }
        }

        // 内存使用分析
        val memoryMetrics = getCurrentMemoryMetrics()
        val heapUsagePercent = (memoryMetrics.usedHeapSize * 100 / memoryMetrics.maxHeapSize)
        if (heapUsagePercent > 80) {
            recommendations.add("堆内存使用率(${heapUsagePercent}%)较高，建议检查内存泄漏")
        }

        return recommendations
    }

    /**
     * 输出性能报告日志
     */
    private fun logPerformanceReport(report: PerformanceReport) {
        TimberLogger.i(TAG, "")
        TimberLogger.i(TAG, "==================== 启动性能报告 ====================")
        TimberLogger.i(TAG, "📊 总启动时间: ${report.totalStartupTime}ms")
        TimberLogger.i(TAG, "📱 Application初始化: ${report.applicationInitTime}ms")
        TimberLogger.i(TAG, "🎬 首个Activity创建: ${report.firstActivityTime}ms")
        TimberLogger.i(TAG, "🎨 首帧绘制: ${report.firstFrameTime}ms")
        TimberLogger.i(TAG, "🎯 完全加载: ${report.fullyLoadedTime}ms")
        TimberLogger.i(TAG, "")
        TimberLogger.i(TAG, "📈 组件初始化详情:")
        report.componentMetrics.forEach { (component, time) ->
            TimberLogger.i(TAG, "   $component: ${time}ms")
        }
        TimberLogger.i(TAG, "")
        TimberLogger.i(TAG, "💾 内存使用:")
        TimberLogger.i(TAG, "   堆内存: ${report.memoryUsage.usedHeapSize / 1024 / 1024}MB / ${report.memoryUsage.maxHeapSize / 1024 / 1024}MB")
        TimberLogger.i(TAG, "   系统内存: ${report.memoryUsage.usedMemory / 1024 / 1024}MB / ${report.memoryUsage.totalMemory / 1024 / 1024}MB")
        TimberLogger.i(TAG, "")
        TimberLogger.i(TAG, "💡 优化建议:")
        report.recommendations.forEach { recommendation ->
            TimberLogger.i(TAG, "   • $recommendation")
        }
        TimberLogger.i(TAG, "====================================================")
        TimberLogger.i(TAG, "")
    }

    /**
     * 停止监控并清理资源
     */
    fun stopMonitoring() {
        isMonitoring.set(false)
        monitoringScope.cancel()
        _performanceState.value = PerformanceState.IDLE
        TimberLogger.d(TAG, "🛑 启动性能监控已停止")
    }

    /**
     * 获取当前启动指标
     */
    fun getCurrentMetrics(): Map<String, StartupMetric> {
        return startupMetrics.toMap()
    }
} 
