package com.novel.utils.network.cache

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.api.front.BookService
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 预取任务状态
 */
@Stable
data class PrefetchTask(
    val chapterId: Long,
    val bookId: Long,
    val priority: PrefetchPriority,
    val status: PrefetchStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val error: String? = null
)

/**
 * 预取状态
 */
enum class PrefetchStatus {
    PENDING,    // 等待执行
    RUNNING,    // 正在执行
    COMPLETED,  // 完成
    FAILED,     // 失败
    CANCELLED   // 已取消
}

/**
 * 预取统计信息
 */
@Stable
data class PrefetchStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val failedTasks: Int,
    val cacheHitRate: Float,
    val networkSaved: Long, // 节省的网络流量（字节）
    val averageTaskTime: Long // 平均任务执行时间（毫秒）
)

/**
 * 智能预取器
 * 
 * 功能：
 * - 基于阅读行为智能预取章节内容
 * - 支持优先级队列和并发控制
 * - 监控预取效果和统计信息
 * - 自适应调整预取策略
 */
@Stable
@Singleton
class IntelligentPrefetcher @Inject constructor(
    private val bookService: BookService,
    private val cacheManager: NetworkCacheManager,
    private val behaviorAnalyzer: ReadingBehaviorAnalyzer
) {
    companion object {
        private const val TAG = "IntelligentPrefetcher"
        private const val MAX_CONCURRENT_TASKS = 3
        private const val TASK_TIMEOUT_MS = 30000L // 30秒超时
        private const val MAX_QUEUE_SIZE = 20
    }
    
    private val prefetchScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // 预取任务队列
    private val taskQueue = mutableListOf<PrefetchTask>()
    private val activeTasks = mutableMapOf<Long, Job>()
    
    // 状态管理
    private val _prefetchStats = MutableStateFlow(PrefetchStats(0, 0, 0, 0f, 0L, 0L))
    val prefetchStats: StateFlow<PrefetchStats> = _prefetchStats.asStateFlow()
    
    private val _activeTasks = MutableStateFlow<List<PrefetchTask>>(emptyList())
    val activeTasksFlow: StateFlow<List<PrefetchTask>> = _activeTasks.asStateFlow()
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    // 统计数据
    private var totalTasksCount = 0
    private var completedTasksCount = 0
    private var failedTasksCount = 0
    private var totalNetworkSaved = 0L
    private var totalTaskTime = 0L
    
    init {
        TimberLogger.d(TAG, "智能预取器初始化完成")
        startTaskProcessor()
    }
    
    /**
     * 开始智能预取
     */
    fun startIntelligentPrefetch(
        currentBookId: Long,
        currentChapterId: Long,
        availableChapters: List<Long>
    ) {
        prefetchScope.launch {
            try {
                val recommendation = behaviorAnalyzer.generatePrefetchRecommendation(
                    currentBookId, currentChapterId, availableChapters
                )
                
                TimberLogger.d(TAG, "收到预取建议: $recommendation")
                
                if (recommendation.shouldPrefetch) {
                    schedulePrefetchTasks(currentBookId, recommendation)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "智能预取启动失败", e)
            }
        }
    }
    
    /**
     * 调度预取任务
     */
    private suspend fun schedulePrefetchTasks(bookId: Long, recommendation: PrefetchRecommendation) {
        // 先检查缓存状态（在同步块外）
        val tasksToAdd = mutableListOf<PrefetchTask>()
        
        for (chapterId in recommendation.nextChapterIds) {
            val isAlreadyCached = cacheManager.isCacheExists(CacheKeys.bookContent(chapterId))
            
            if (!isAlreadyCached) {
                // 检查是否已经在队列中
                val isAlreadyQueued = synchronized(taskQueue) {
                    taskQueue.any { it.chapterId == chapterId && it.status == PrefetchStatus.PENDING }
                }
                
                if (!isAlreadyQueued) {
                    val task = PrefetchTask(
                        chapterId = chapterId,
                        bookId = bookId,
                        priority = recommendation.priority,
                        status = PrefetchStatus.PENDING
                    )
                    tasksToAdd.add(task)
                    TimberLogger.d(TAG, "准备添加预取任务: chapterId=$chapterId, priority=${recommendation.priority}")
                }
            }
        }
        
        // 在同步块内添加任务
        synchronized(taskQueue) {
            tasksToAdd.forEach { task ->
                insertTaskByPriority(task)
            }
            
            // 限制队列大小
            if (taskQueue.size > MAX_QUEUE_SIZE) {
                val removed = taskQueue.removeAt(taskQueue.size - 1)
                TimberLogger.d(TAG, "队列已满，移除最低优先级任务: chapterId=${removed.chapterId}")
            }
            
            updateActiveTasksFlow()
        }
    }
    
    /**
     * 按优先级插入任务
     */
    private fun insertTaskByPriority(task: PrefetchTask) {
        val insertIndex = taskQueue.indexOfFirst { it.priority.ordinal > task.priority.ordinal }
        if (insertIndex == -1) {
            taskQueue.add(task)
        } else {
            taskQueue.add(insertIndex, task)
        }
    }
    
    /**
     * 启动任务处理器
     */
    private fun startTaskProcessor() {
        prefetchScope.launch {
            _isRunning.value = true
            
            while (isActive) {
                try {
                    // 检查是否有可执行的任务
                    val availableTask = synchronized(taskQueue) {
                        taskQueue.firstOrNull { 
                            it.status == PrefetchStatus.PENDING && 
                            activeTasks.size < MAX_CONCURRENT_TASKS 
                        }
                    }
                    
                    if (availableTask != null) {
                        executeTask(availableTask)
                    } else {
                        // 没有任务时等待
                        delay(1000)
                    }
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "任务处理器异常", e)
                    delay(5000) // 异常时等待更长时间
                }
            }
            
            _isRunning.value = false
        }
    }
    
    /**
     * 执行预取任务
     */
    private suspend fun executeTask(task: PrefetchTask) {
        val startTime = System.currentTimeMillis()
        
        synchronized(taskQueue) {
            val index = taskQueue.indexOf(task)
            if (index != -1) {
                taskQueue[index] = task.copy(status = PrefetchStatus.RUNNING)
            }
        }
        
        val job = prefetchScope.launch {
            try {
                TimberLogger.d(TAG, "开始执行预取任务: chapterId=${task.chapterId}")
                
                // 执行预取
                val result = bookService.getBookContentWithIncrementalSync(
                    chapterId = task.chapterId,
                    cacheManager = cacheManager,
                    config = CacheConfigs.LONG_CACHE
                )
                
                val endTime = System.currentTimeMillis()
                val taskTime = endTime - startTime
                
                when (result) {
                    is IncrementalSyncResult.Updated -> {
                        markTaskCompleted(task, taskTime)
                        if (!result.hasChanged) {
                            // 内容未改变，计入网络节省
                            totalNetworkSaved += 100 * 1024L // 假设每章节约100KB
                        }
                        TimberLogger.d(TAG, "预取任务完成: chapterId=${task.chapterId}, hasChanged=${result.hasChanged}")
                    }
                    is IncrementalSyncResult.NoChange -> {
                        markTaskCompleted(task, taskTime)
                        totalNetworkSaved += 100 * 1024L // 缓存命中，节省网络流量
                        TimberLogger.d(TAG, "预取任务完成（缓存命中）: chapterId=${task.chapterId}")
                    }
                    is IncrementalSyncResult.Error -> {
                        markTaskFailed(task, result.error.message ?: "Unknown error")
                        TimberLogger.e(TAG, "预取任务失败: chapterId=${task.chapterId}", result.error)
                    }
                }
                
            } catch (e: Exception) {
                val taskTime = System.currentTimeMillis() - startTime
                markTaskFailed(task, e.message ?: "Execution failed")
                TimberLogger.e(TAG, "预取任务执行异常: chapterId=${task.chapterId}", e)
            }
        }
        
        // 添加超时控制
        val timeoutJob = prefetchScope.launch {
            delay(TASK_TIMEOUT_MS)
            if (job.isActive) {
                job.cancel()
                markTaskFailed(task, "Task timeout")
                TimberLogger.w(TAG, "预取任务超时: chapterId=${task.chapterId}")
            }
        }
        
        activeTasks[task.chapterId] = job
        updateActiveTasksFlow()
        
        try {
            job.join()
        } finally {
            timeoutJob.cancel()
            activeTasks.remove(task.chapterId)
            updateActiveTasksFlow()
        }
    }
    
    /**
     * 标记任务完成
     */
    private fun markTaskCompleted(task: PrefetchTask, taskTime: Long) {
        synchronized(taskQueue) {
            val index = taskQueue.indexOf(task)
            if (index != -1) {
                taskQueue[index] = task.copy(
                    status = PrefetchStatus.COMPLETED,
                    completedAt = System.currentTimeMillis()
                )
            }
        }
        
        completedTasksCount++
        totalTaskTime += taskTime
        updatePrefetchStats()
    }
    
    /**
     * 标记任务失败
     */
    private fun markTaskFailed(task: PrefetchTask, error: String) {
        synchronized(taskQueue) {
            val index = taskQueue.indexOf(task)
            if (index != -1) {
                taskQueue[index] = task.copy(
                    status = PrefetchStatus.FAILED,
                    error = error,
                    completedAt = System.currentTimeMillis()
                )
            }
        }
        
        failedTasksCount++
        updatePrefetchStats()
    }
    
    /**
     * 更新预取统计信息
     */
    private fun updatePrefetchStats() {
        val totalTasks = completedTasksCount + failedTasksCount
        val cacheHitRate = if (totalTasks > 0) {
            completedTasksCount.toFloat() / totalTasks
        } else 0f
        
        val averageTaskTime = if (completedTasksCount > 0) {
            totalTaskTime / completedTasksCount
        } else 0L
        
        _prefetchStats.value = PrefetchStats(
            totalTasks = totalTasks,
            completedTasks = completedTasksCount,
            failedTasks = failedTasksCount,
            cacheHitRate = cacheHitRate,
            networkSaved = totalNetworkSaved,
            averageTaskTime = averageTaskTime
        )
        
        TimberLogger.d(TAG, "更新预取统计: totalTasks=$totalTasks, cacheHitRate=$cacheHitRate, networkSaved=${totalNetworkSaved}B")
    }
    
    /**
     * 更新活动任务流
     */
    private fun updateActiveTasksFlow() {
        _activeTasks.value = taskQueue.filter { 
            it.status == PrefetchStatus.PENDING || it.status == PrefetchStatus.RUNNING 
        }
    }
    
    /**
     * 取消所有预取任务
     */
    fun cancelAllTasks() {
        prefetchScope.launch {
            TimberLogger.d(TAG, "取消所有预取任务")
            
            // 取消正在执行的任务
            activeTasks.values.forEach { it.cancel() }
            activeTasks.clear()
            
            // 标记待执行任务为已取消
            synchronized(taskQueue) {
                taskQueue.replaceAll { task ->
                    if (task.status == PrefetchStatus.PENDING || task.status == PrefetchStatus.RUNNING) {
                        task.copy(status = PrefetchStatus.CANCELLED)
                    } else task
                }
            }
            
            updateActiveTasksFlow()
        }
    }
    
    /**
     * 清理已完成的任务
     */
    fun cleanupCompletedTasks() {
        synchronized(taskQueue) {
            val beforeSize = taskQueue.size
            taskQueue.removeAll { task ->
                task.status == PrefetchStatus.COMPLETED || 
                task.status == PrefetchStatus.FAILED || 
                task.status == PrefetchStatus.CANCELLED
            }
            val afterSize = taskQueue.size
            
            if (beforeSize != afterSize) {
                TimberLogger.d(TAG, "清理已完成任务: 清理前=$beforeSize, 清理后=$afterSize")
                updateActiveTasksFlow()
            }
        }
    }
    
    /**
     * 获取任务队列状态
     */
    fun getQueueStatus(): String {
        synchronized(taskQueue) {
            val pending = taskQueue.count { it.status == PrefetchStatus.PENDING }
            val running = taskQueue.count { it.status == PrefetchStatus.RUNNING }
            val completed = taskQueue.count { it.status == PrefetchStatus.COMPLETED }
            val failed = taskQueue.count { it.status == PrefetchStatus.FAILED }
            
            return "队列状态 - 待执行: $pending, 执行中: $running, 已完成: $completed, 失败: $failed"
        }
    }
    
    /**
     * 销毁预取器
     */
    fun destroy() {
        TimberLogger.d(TAG, "销毁智能预取器")
        cancelAllTasks()
        prefetchScope.cancel()
    }
} 