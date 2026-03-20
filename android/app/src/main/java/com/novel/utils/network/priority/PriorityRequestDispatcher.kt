package com.novel.utils.network.priority

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.NetworkMonitor
import com.novel.utils.network.NetworkType
import okhttp3.Call
import okhttp3.Dispatcher
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 请求优先级枚举
 */
enum class RequestPriority(val value: Int, val weight: Int) {
    HIGH(3, 5),      // 用户关键操作（点击、页面跳转）
    MEDIUM(2, 3),    // 一般数据获取（列表、详情）
    LOW(1, 1),       // 后台任务（预取、统计上报）
    BACKGROUND(0, 1) // 最低优先级（清理、同步）
}

/**
 * 带优先级的任务包装器
 */
@Stable
data class PriorityTask(
    val runnable: Runnable,
    val priority: RequestPriority,
    val createdAt: Long = System.currentTimeMillis(),
    val taskId: String = generateTaskId()
) : Comparable<PriorityTask>, Runnable {
    
    companion object {
        private val taskCounter = AtomicInteger(0)
        
        private fun generateTaskId(): String {
            return "task_${System.currentTimeMillis()}_${taskCounter.incrementAndGet()}"
        }
    }
    
    override fun run() {
        runnable.run()
    }
    
    override fun compareTo(other: PriorityTask): Int {
        // 首先按优先级排序（高优先级在前）
        val priorityCompare = other.priority.value.compareTo(this.priority.value)
        if (priorityCompare != 0) return priorityCompare
        
        // 相同优先级按创建时间排序（先创建的在前）
        return this.createdAt.compareTo(other.createdAt)
    }
}

/**
 * 优先级队列线程池执行器
 */
class PriorityThreadPoolExecutor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit,
    private val networkMonitor: NetworkMonitor
) : ThreadPoolExecutor(
    corePoolSize,
    maximumPoolSize,
    keepAliveTime,
    unit,
    PriorityBlockingQueue<Runnable>()
) {
    
    companion object {
        private const val TAG = "PriorityThreadPoolExecutor"
        private const val STARVATION_THRESHOLD_MS = 5000L // 5秒饿死阈值
    }
    
    // 统计信息
    private val taskStats = ConcurrentHashMap<RequestPriority, AtomicInteger>()
    private val waitingTasks = ConcurrentHashMap<String, Long>()
    
    init {
        RequestPriority.values().forEach { priority ->
            taskStats[priority] = AtomicInteger(0)
        }
    }
    
    override fun execute(command: Runnable) {
        val priority = extractPriority(command)
        val priorityTask = if (command is PriorityTask) {
            command
        } else {
            PriorityTask(command, priority)
        }
        
        // 记录任务统计
        taskStats[priority]?.incrementAndGet()
        waitingTasks[priorityTask.taskId] = System.currentTimeMillis()
        
        TimberLogger.d(TAG, "提交任务: ${priorityTask.taskId}, 优先级: $priority")
        
        super.execute(priorityTask)
    }
    
    override fun beforeExecute(t: Thread?, r: Runnable?) {
        super.beforeExecute(t, r)
        
        if (r is PriorityTask) {
            val waitTime = System.currentTimeMillis() - r.createdAt
            waitingTasks.remove(r.taskId)
            
            TimberLogger.d(TAG, "开始执行任务: ${r.taskId}, 等待时间: ${waitTime}ms")
            
            // 检查是否存在饿死情况
            checkStarvation()
        }
    }
    
    override fun afterExecute(r: Runnable?, t: Throwable?) {
        super.afterExecute(r, t)
        
        if (r is PriorityTask) {
            val executionTime = System.currentTimeMillis() - r.createdAt
            TimberLogger.d(TAG, "任务执行完成: ${r.taskId}, 总时间: ${executionTime}ms")
            
            if (t != null) {
                TimberLogger.e(TAG, "任务执行异常: ${r.taskId}", t)
            }
        }
    }
    
    /**
     * 从任务中提取优先级
     */
    private fun extractPriority(command: Runnable): RequestPriority {
        return when (command) {
            is PriorityTask -> command.priority
            else -> {
                // 默认优先级，可以根据线程名或其他信息推断
                val threadName = Thread.currentThread().name.lowercase()
                when {
                    "ui" in threadName || "main" in threadName -> RequestPriority.HIGH
                    "background" in threadName || "worker" in threadName -> RequestPriority.LOW
                    else -> RequestPriority.MEDIUM
                }
            }
        }
    }
    
    /**
     * 检查低优先级任务是否被饿死
     */
    private fun checkStarvation() {
        val currentTime = System.currentTimeMillis()
        val starvedTasks = waitingTasks.entries.filter { (_, createTime) ->
            currentTime - createTime > STARVATION_THRESHOLD_MS
        }
        
        if (starvedTasks.isNotEmpty()) {
            TimberLogger.w(TAG, "检测到${starvedTasks.size}个任务可能被饿死")
            // 可以在这里实现饿死保护逻辑，比如临时提升优先级
        }
    }
    
    /**
     * 获取队列统计信息
     */
    fun getQueueStats(): Map<RequestPriority, Int> {
        return taskStats.mapValues { it.value.get() }
    }
    
    /**
     * 获取当前等待任务数
     */
    fun getWaitingTaskCount(): Int {
        return waitingTasks.size
    }
}

/**
 * 优先级请求分发器
 */
@Stable
@Singleton
class PriorityRequestDispatcher @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    
    companion object {
        private const val TAG = "PriorityRequestDispatcher"
    }
    
    private var priorityExecutor: PriorityThreadPoolExecutor
    
    init {
        priorityExecutor = createExecutor()
        TimberLogger.d(TAG, "优先级请求分发器初始化完成")
    }
    
    /**
     * 创建线程池执行器
     */
    private fun createExecutor(): PriorityThreadPoolExecutor {
        val concurrency = networkMonitor.getRecommendedConcurrency()
        
        return PriorityThreadPoolExecutor(
            corePoolSize = maxOf(2, concurrency / 2),
            maximumPoolSize = maxOf(4, concurrency),
            keepAliveTime = 60L,
            unit = TimeUnit.SECONDS,
            networkMonitor = networkMonitor
        )
    }
    
    /**
     * 创建适配网络状态的OkHttp Dispatcher
     */
    fun createOkHttpDispatcher(): Dispatcher {
        val dispatcher = Dispatcher(priorityExecutor)
        
        // 根据网络状态调整并发数
        val concurrency = networkMonitor.getRecommendedConcurrency()
        dispatcher.maxRequests = maxOf(8, concurrency * 2)
        dispatcher.maxRequestsPerHost = maxOf(4, concurrency)
        
        TimberLogger.d(TAG, "创建OkHttp分发器: maxRequests=${dispatcher.maxRequests}, maxPerHost=${dispatcher.maxRequestsPerHost}")
        
        return dispatcher
    }
    
    /**
     * 更新执行器配置（当网络状态改变时调用）
     */
    fun updateConfiguration() {
        val newConcurrency = networkMonitor.getRecommendedConcurrency()
        val currentCore = priorityExecutor.corePoolSize
        val newCore = maxOf(2, newConcurrency / 2)
        val newMax = maxOf(4, newConcurrency)
        
        if (newCore != currentCore) {
            priorityExecutor.corePoolSize = newCore
            priorityExecutor.maximumPoolSize = newMax
            
            TimberLogger.d(TAG, "更新线程池配置: core=$newCore, max=$newMax")
        }
    }
    
    /**
     * 获取统计信息
     */
    fun getStats(): PriorityStats {
        return PriorityStats(
            queueStats = priorityExecutor.getQueueStats(),
            activeCount = priorityExecutor.activeCount,
            completedTaskCount = priorityExecutor.completedTaskCount,
            waitingTaskCount = priorityExecutor.getWaitingTaskCount(),
            poolSize = priorityExecutor.poolSize
        )
    }
}

/**
 * Request扩展方法，用于设置优先级
 */
fun Request.withPriority(priority: RequestPriority): Request {
    return this.newBuilder()
        .tag(RequestPriority::class.java, priority)
        .build()
}

/**
 * Call扩展方法，用于设置优先级
 */
fun Call.withPriority(priority: RequestPriority): Call {
    // 这里需要修改Request，但Call本身不支持修改
    // 实际使用时应该在创建Request时就设置优先级
    return this
}

/**
 * 从Request中提取优先级
 */
fun Request.getPriority(): RequestPriority {
    return this.tag(RequestPriority::class.java) ?: RequestPriority.MEDIUM
}

/**
 * 优先级统计信息
 */
@Stable
data class PriorityStats(
    val queueStats: Map<RequestPriority, Int>,
    val activeCount: Int,
    val completedTaskCount: Long,
    val waitingTaskCount: Int,
    val poolSize: Int
) 
