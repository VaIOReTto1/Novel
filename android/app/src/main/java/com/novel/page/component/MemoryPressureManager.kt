package com.novel.page.component

import android.app.ActivityManager
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import com.novel.utils.TimberLogger
import androidx.compose.runtime.Stable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 内存压力回调接口
 * 定义内存压力事件的处理接口
 */
interface MemoryPressureCallback {
    /**
     * 处理内存压力事件
     * 
     * @param level 内存压力级别，参考ComponentCallbacks2的常量
     */
    fun onMemoryPressure(level: Int)
    
    /**
     * 处理低内存警告
     */
    fun onLowMemory()
    
    /**
     * 获取回调的优先级，数字越小优先级越高
     */
    fun getPriority(): Int = 100
}

/**
 * 内存状态信息
 */
@Stable
data class MemoryState(
    val availableMemoryMB: Long,
    val totalMemoryMB: Long,
    val usedMemoryMB: Long,
    val memoryUsageRatio: Float,
    val isLowMemory: Boolean,
    val pressureLevel: Int
) {
    val freeMemoryMB: Long = totalMemoryMB - usedMemoryMB
    
    fun getMemoryPressureDescription(): String = when (pressureLevel) {
        ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "严重内存压力"
        ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "中等内存压力"
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "后台内存压力"
        ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI隐藏状态"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "运行时严重内存不足"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "运行时内存不足"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "运行时中等内存压力"
        else -> "正常"
    }
}

/**
 * 内存压力监控管理器
 * 
 * 功能：
 * - 监听系统内存状态变化
 * - 在内存压力下自动通知相关组件进行清理
 * - 提供内存使用统计和预警
 * - 生命周期感知的自动注册/注销
 * 
 * 特性：
 * - 支持多个回调的优先级排序
 * - 弱引用防止内存泄漏
 * - 协程异步处理避免阻塞主线程
 * - 详细的内存状态监控和日志
 */
@Singleton
@Stable
class MemoryPressureManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ComponentCallbacks2, DefaultLifecycleObserver {
    
    companion object {
        private const val TAG = "MemoryPressureManager"
        private const val MEMORY_CHECK_INTERVAL = 30_000L // 30秒检查一次
        private const val LOW_MEMORY_THRESHOLD = 0.8f // 80%内存使用率视为低内存
    }
    
    // 弱引用回调列表，按优先级排序
    @Stable
    private val callbacks = mutableListOf<Pair<WeakReference<MemoryPressureCallback>, Int>>()
    
    // ActivityManager用于获取内存信息
    private val activityManager: ActivityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
    
    // 协程作用域
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // 当前内存状态
    @Stable
    @Volatile
    private var currentMemoryState: MemoryState? = null
    
    // 内存监控job
    @Volatile
    private var monitoringJob: Job? = null
    
    private val lock = Any()
    
    init {
        // 注册系统回调
        context.registerComponentCallbacks(this)
        
        TimberLogger.d(TAG, "内存压力管理器初始化完成")
    }
    
    /**
     * 注册内存压力回调
     * 
     * @param callback 回调实例
     */
    fun registerCallback(callback: MemoryPressureCallback) {
        synchronized(lock) {
            // 移除已经被GC的弱引用
            cleanupDeadReferences()
            
            val priority = callback.getPriority()
            val weakRef = WeakReference(callback)
            
            // 按优先级插入，优先级数字越小越靠前
            val insertIndex = callbacks.indexOfFirst { it.second > priority }
            if (insertIndex >= 0) {
                callbacks.add(insertIndex, weakRef to priority)
            } else {
                callbacks.add(weakRef to priority)
            }
            
            TimberLogger.d(TAG, "注册内存压力回调，优先级: $priority，当前回调数量: ${callbacks.size}")
        }
    }
    
    /**
     * 注销内存压力回调
     */
    fun unregisterCallback(callback: MemoryPressureCallback) {
        synchronized(lock) {
            callbacks.removeAll { pair ->
                val ref = pair.first.get()
                ref == null || ref == callback
            }
            TimberLogger.d(TAG, "注销内存压力回调，当前回调数量: ${callbacks.size}")
        }
    }
    
    /**
     * 获取当前内存状态
     */
    fun getCurrentMemoryState(): MemoryState {
        return currentMemoryState ?: updateMemoryState()
    }
    
    /**
     * 开始内存监控
     */
    fun startMonitoring() {
        if (monitoringJob?.isActive == true) {
            return
        }
        
        monitoringJob = scope.launch {
            TimberLogger.d(TAG, "开始内存监控")
            while (isActive) {
                try {
                    updateMemoryState()
                    delay(MEMORY_CHECK_INTERVAL)
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "内存监控异常", e)
                    delay(MEMORY_CHECK_INTERVAL)
                }
            }
        }
    }
    
    /**
     * 停止内存监控
     */
    fun stopMonitoring() {
        monitoringJob?.cancel()
        monitoringJob = null
        TimberLogger.d(TAG, "停止内存监控")
    }
    
    /**
     * 手动触发内存压力检查
     */
    fun checkMemoryPressure() {
        val state = updateMemoryState()
        if (state.isLowMemory) {
            notifyCallbacks(ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW)
        }
    }
    
    // ========== ComponentCallbacks2 实现 ==========
    
    override fun onTrimMemory(level: Int) {
        TimberLogger.w(TAG, "系统内存压力通知，级别: $level (${getMemoryPressureDescription(level)})")
        updateMemoryState()
        notifyCallbacks(level)
    }
    
    override fun onLowMemory() {
        TimberLogger.w(TAG, "系统低内存警告")
        updateMemoryState()
        notifyLowMemoryCallbacks()
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        // 配置变化时重新检查内存状态
        updateMemoryState()
    }
    
    // ========== DefaultLifecycleObserver 实现 ==========
    
    override fun onStart(owner: LifecycleOwner) {
        startMonitoring()
    }
    
    override fun onStop(owner: LifecycleOwner) {
        stopMonitoring()
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        scope.cancel()
        context.unregisterComponentCallbacks(this)
        synchronized(lock) {
            callbacks.clear()
        }
        TimberLogger.d(TAG, "内存压力管理器已销毁")
    }
    
    // ========== 私有方法 ==========
    
    private fun updateMemoryState(): MemoryState {
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val runtime = Runtime.getRuntime()
        val totalMemoryMB = memInfo.totalMem / (1024 * 1024)
        val availableMemoryMB = memInfo.availMem / (1024 * 1024)
        val usedMemoryMB = totalMemoryMB - availableMemoryMB
        val memoryUsageRatio = usedMemoryMB.toFloat() / totalMemoryMB
        val isLowMemory = memInfo.lowMemory || memoryUsageRatio > LOW_MEMORY_THRESHOLD
        
        val state = MemoryState(
            availableMemoryMB = availableMemoryMB,
            totalMemoryMB = totalMemoryMB,
            usedMemoryMB = usedMemoryMB,
            memoryUsageRatio = memoryUsageRatio,
            isLowMemory = isLowMemory,
            pressureLevel = if (isLowMemory) ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW else 0
        )
        
        currentMemoryState = state
        
        TimberLogger.v(TAG, "内存状态更新: 可用=${availableMemoryMB}MB, " +
                "总计=${totalMemoryMB}MB, 使用率=${(memoryUsageRatio * 100).toInt()}%, " +
                "低内存=$isLowMemory")
        
        return state
    }
    
    private fun notifyCallbacks(level: Int) {
        scope.launch {
            synchronized(lock) {
                cleanupDeadReferences()
                callbacks.forEach { (weakRef, _) ->
                    weakRef.get()?.let { callback ->
                        try {
                            callback.onMemoryPressure(level)
                        } catch (e: Exception) {
                            TimberLogger.e(TAG, "内存压力回调执行异常", e)
                        }
                    }
                }
            }
        }
    }
    
    private fun notifyLowMemoryCallbacks() {
        scope.launch {
            synchronized(lock) {
                cleanupDeadReferences()
                callbacks.forEach { (weakRef, _) ->
                    weakRef.get()?.let { callback ->
                        try {
                            callback.onLowMemory()
                        } catch (e: Exception) {
                            TimberLogger.e(TAG, "低内存回调执行异常", e)
                        }
                    }
                }
            }
        }
    }
    
    private fun cleanupDeadReferences() {
        callbacks.removeAll { it.first.get() == null }
    }
    
    private fun getMemoryPressureDescription(level: Int): String = when (level) {
        ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> "COMPLETE"
        ComponentCallbacks2.TRIM_MEMORY_MODERATE -> "MODERATE"
        ComponentCallbacks2.TRIM_MEMORY_BACKGROUND -> "BACKGROUND"
        ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> "UI_HIDDEN"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> "RUNNING_CRITICAL"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> "RUNNING_LOW"
        ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE -> "RUNNING_MODERATE"
        else -> "UNKNOWN($level)"
    }
} 
