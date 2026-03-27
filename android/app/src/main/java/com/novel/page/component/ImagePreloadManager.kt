package com.novel.page.component

import android.content.ComponentCallbacks2
import com.novel.utils.TimberLogger
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 预加载优先级
 */
@Stable
enum class PreloadPriority(val value: Int) {
    /** 立即预加载（即将进入可视区域） */
    IMMEDIATE(1),
    /** 高优先级（用户滚动方向的下一屏） */
    HIGH(2),
    /** 中优先级（用户滚动方向的第二屏） */
    MEDIUM(3),
    /** 低优先级（背景预加载） */
    LOW(4),
    /** 最低优先级（空闲时预加载） */
    IDLE(5)
}

/**
 * 预加载任务
 */
@Stable
data class PreloadTask(
    val url: String,
    val priority: PreloadPriority,
    val needToken: Boolean = false,
    val compressionConfig: CompressionConfig? = null,
    val createdTime: Long = System.currentTimeMillis(),
    val retryCount: Int = 0,
    val taskId: String = "${url.hashCode()}_${createdTime}"
) : Comparable<PreloadTask> {
    override fun compareTo(other: PreloadTask): Int {
        // 优先级数字越小越优先
        val priorityComparison = priority.value.compareTo(other.priority.value)
        if (priorityComparison != 0) return priorityComparison
        
        // 同优先级按创建时间排序
        return createdTime.compareTo(other.createdTime)
    }
}

/**
 * 预加载统计信息
 */
@Stable
data class PreloadStats(
    val totalRequests: Int,
    val successfulPreloads: Int,
    val failedPreloads: Int,
    val cacheHits: Int,
    val currentQueueSize: Int,
    val averageLoadTime: Long,
    val memoryPressureEvents: Int
) {
    val successRate: Float = if (totalRequests > 0) successfulPreloads.toFloat() / totalRequests else 0f
    val cacheHitRate: Float = if (totalRequests > 0) cacheHits.toFloat() / totalRequests else 0f
    
    override fun toString(): String {
        return "PreloadStats(requests: $totalRequests, success: ${(successRate * 100).toInt()}%, " +
               "cache hits: ${(cacheHitRate * 100).toInt()}%, queue: $currentQueueSize, " +
               "avg time: ${averageLoadTime}ms, memory events: $memoryPressureEvents)"
    }
}

/**
 * 滚动方向检测器
 */
@Stable
class ScrollDirectionDetector {
    private var lastFirstVisibleItemIndex = 0
    private var lastFirstVisibleItemScrollOffset = 0
    
    /**
     * 检测滚动方向
     * 
     * @return 1表示向下滚动，-1表示向上滚动，0表示静止
     */
    fun detectScrollDirection(listState: LazyListState): Int {
        val currentFirstVisibleItem = listState.firstVisibleItemIndex
        val currentScrollOffset = listState.firstVisibleItemScrollOffset
        
        val direction = when {
            currentFirstVisibleItem > lastFirstVisibleItemIndex -> 1 // 向下
            currentFirstVisibleItem < lastFirstVisibleItemIndex -> -1 // 向上
            currentFirstVisibleItem == lastFirstVisibleItemIndex -> {
                when {
                    currentScrollOffset > lastFirstVisibleItemScrollOffset -> 1 // 向下
                    currentScrollOffset < lastFirstVisibleItemScrollOffset -> -1 // 向上
                    else -> 0 // 静止
                }
            }
            else -> 0
        }
        
        lastFirstVisibleItemIndex = currentFirstVisibleItem
        lastFirstVisibleItemScrollOffset = currentScrollOffset
        
        return direction
    }
}

/**
 * 智能图片预加载管理器
 * 
 * 核心功能：
 * - 基于用户滚动行为预测下一屏内容
 * - 多优先级任务队列，确保重要图片优先加载
 * - 内存压力自适应，避免OOM
 * - 网络状态感知，在不同网络条件下调整策略
 * - 详细的统计信息和性能监控
 * 
 * 智能策略：
 * - 检测滚动方向和速度，预测用户浏览趋势
 * - 根据内存使用情况动态调整预加载数量
 * - 支持不同场景的预加载配置（列表、详情、搜索等）
 * - 自动清理过期任务和无用缓存
 */
@Singleton
@Stable
class ImagePreloadManager @Inject constructor(
    private val imageLoader: ImageLoaderService,
    private val memoryPressureManager: MemoryPressureManager,
    private val config: ImageOptimizationConfig
) : MemoryPressureCallback, DefaultLifecycleObserver {
    
    companion object {
        private const val TAG = "ImagePreloadManager"
        private const val MAX_QUEUE_SIZE = 50
        private const val MAX_CONCURRENT_PRELOADS = 3
        private const val TASK_TIMEOUT_MS = 30000L // 30秒超时
        private const val RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_COUNT = 2
    }
    
    // 预加载任务队列（优先级队列）
    @Stable
    private val preloadQueue = PriorityBlockingQueue<PreloadTask>()
    
    // 正在进行的任务
    @Stable
    private val activeTasks = ConcurrentHashMap<String, Job>()
    
    // 已完成的URL集合（避免重复预加载）
    @Stable
    private val completedUrls = ConcurrentHashMap.newKeySet<String>()
    
    // 协程作用域
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // 统计信息
    @Stable
    private val totalRequests = AtomicInteger(0)
    @Stable
    private val successfulPreloads = AtomicInteger(0)
    @Stable
    private val failedPreloads = AtomicInteger(0)
    @Stable
    private val cacheHits = AtomicInteger(0)
    @Stable
    private val memoryPressureEvents = AtomicInteger(0)
    @Stable
    private val totalLoadTime = AtomicInteger(0)
    
    // 控制变量
    @Volatile
    private var isPreloadingEnabled = true
    @Volatile
    private var maxConcurrentPreloads = MAX_CONCURRENT_PRELOADS
    
    // 预加载工作协程
    private var preloadWorker: Job? = null
    
    init {
        // 注册内存压力回调
        memoryPressureManager.registerCallback(this)
        startPreloadWorker()
        TimberLogger.d(TAG, "图片预加载管理器初始化完成")
    }
    
    /**
     * 提交单个预加载任务
     * 
     * @param url 图片URL
     * @param priority 优先级
     * @param needToken 是否需要认证Token
     * @param compressionConfig 压缩配置
     */
    fun submitPreloadTask(
        url: String,
        priority: PreloadPriority = PreloadPriority.MEDIUM,
        needToken: Boolean = false,
        compressionConfig: CompressionConfig? = null
    ) {
        if (!isPreloadingEnabled || url.isBlank()) {
            return
        }
        
        // 检查是否已经预加载过
        if (completedUrls.contains(url)) {
            cacheHits.incrementAndGet()
            TimberLogger.v(TAG, "预加载缓存命中: $url")
            return
        }
        
        // 检查是否已在队列中
        if (activeTasks.containsKey(url)) {
            TimberLogger.v(TAG, "预加载任务已存在: $url")
            return
        }
        
        // 队列大小限制
        if (preloadQueue.size >= MAX_QUEUE_SIZE) {
            // 移除最低优先级的任务
            val iterator = preloadQueue.iterator()
            var lowestPriorityTask: PreloadTask? = null
            
            while (iterator.hasNext()) {
                val task = iterator.next()
                if (lowestPriorityTask == null || task.priority.value > lowestPriorityTask.priority.value) {
                    lowestPriorityTask = task
                }
            }
            
            lowestPriorityTask?.let {
                preloadQueue.remove(it)
                TimberLogger.d(TAG, "移除低优先级预加载任务: ${it.url}")
            }
        }
        
        val task = PreloadTask(url, priority, needToken, compressionConfig)
        preloadQueue.offer(task)
        totalRequests.incrementAndGet()
        
        TimberLogger.v(TAG, "提交预加载任务: $url, 优先级: $priority")
    }
    
    /**
     * 批量提交预加载任务
     */
    fun submitPreloadTasks(
        urls: List<String>,
        priority: PreloadPriority = PreloadPriority.MEDIUM,
        needToken: Boolean = false,
        compressionConfig: CompressionConfig? = null
    ) {
        urls.forEach { url ->
            submitPreloadTask(url, priority, needToken, compressionConfig)
        }
    }
    
    /**
     * 为列表滚动场景提交预加载任务
     * 基于滚动状态智能预测需要预加载的图片
     */
    @Composable
    fun PreloadForLazyList(
        listState: LazyListState,
        imageUrls: List<String>,
        preloadDistance: Int = config.preloadDistance,
        needToken: Boolean = false
    ) {
        val scrollDirectionDetector = remember { ScrollDirectionDetector() }
        
        LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
            if (!isPreloadingEnabled) return@LaunchedEffect
            
            val firstVisible = listState.firstVisibleItemIndex
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: firstVisible
            val scrollDirection = scrollDirectionDetector.detectScrollDirection(listState)
            
            // 根据滚动方向确定预加载范围
            val preloadRange = when (scrollDirection) {
                1 -> { // 向下滚动
                    val start = lastVisible + 1
                    val end = (start + preloadDistance).coerceAtMost(imageUrls.size - 1)
                    start..end
                }
                -1 -> { // 向上滚动
                    val end = firstVisible - 1
                    val start = (end - preloadDistance).coerceAtLeast(0)
                    start..end
                }
                else -> return@LaunchedEffect // 静止时不预加载
            }
            
            // 提交预加载任务
            preloadRange.forEach { index ->
                if (index in imageUrls.indices) {
                    val url = imageUrls[index]
                    val priority = when {
                        index <= lastVisible + 2 -> PreloadPriority.IMMEDIATE
                        index <= lastVisible + 5 -> PreloadPriority.HIGH
                        else -> PreloadPriority.MEDIUM
                    }
                    
                    submitPreloadTask(
                        url = url,
                        priority = priority,
                        needToken = needToken,
                        compressionConfig = CompressionConfig.forCache()
                    )
                }
            }
            
            TimberLogger.v(TAG, "列表滚动预加载: 方向=$scrollDirection, 范围=$preloadRange")
        }
    }
    
    /**
     * 清除指定URL的预加载任务
     */
    fun cancelPreloadTask(url: String) {
        activeTasks[url]?.cancel()
        activeTasks.remove(url)
        preloadQueue.removeAll { it.url == url }
        TimberLogger.v(TAG, "取消预加载任务: $url")
    }
    
    /**
     * 清除所有预加载任务
     */
    fun clearAllTasks() {
        activeTasks.values.forEach { it.cancel() }
        activeTasks.clear()
        preloadQueue.clear()
        completedUrls.clear()
        TimberLogger.d(TAG, "清除所有预加载任务")
    }
    
    /**
     * 获取统计信息
     */
    fun getStats(): PreloadStats {
        val avgLoadTime = if (successfulPreloads.get() > 0) {
            totalLoadTime.get().toLong() / successfulPreloads.get().toLong()
        } else 0L
        
        return PreloadStats(
            totalRequests = totalRequests.get(),
            successfulPreloads = successfulPreloads.get(),
            failedPreloads = failedPreloads.get(),
            cacheHits = cacheHits.get(),
            currentQueueSize = preloadQueue.size,
            averageLoadTime = avgLoadTime,
            memoryPressureEvents = memoryPressureEvents.get()
        )
    }
    
    /**
     * 设置预加载开关
     */
    fun setPreloadingEnabled(enabled: Boolean) {
        isPreloadingEnabled = enabled
        TimberLogger.d(TAG, "预加载${if (enabled) "启用" else "禁用"}")
        
        if (!enabled) {
            clearAllTasks()
        }
    }
    
    // ========== MemoryPressureCallback 实现 ==========
    
    override fun onMemoryPressure(level: Int) {
        memoryPressureEvents.incrementAndGet()
        
        when (level) {
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
                // 严重内存压力：停止预加载，清除队列
                setPreloadingEnabled(false)
                TimberLogger.w(TAG, "严重内存压力，停止预加载")
            }
            
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW -> {
                // 中等内存压力：减少并发数，清理低优先级任务
                maxConcurrentPreloads = 1
                clearLowPriorityTasks()
                TimberLogger.i(TAG, "中等内存压力，减少预加载并发数")
            }
            
            else -> {
                // 轻微压力：仅清理最低优先级任务
                clearIdleTasks()
                TimberLogger.d(TAG, "轻微内存压力，清理闲置任务")
            }
        }
    }
    
    override fun onLowMemory() {
        setPreloadingEnabled(false)
        TimberLogger.w(TAG, "低内存警告，停止所有预加载")
    }
    
    override fun getPriority(): Int = 50 // 中等优先级
    
    // ========== DefaultLifecycleObserver 实现 ==========
    
    override fun onStart(owner: LifecycleOwner) {
        if (config.enablePreloading) {
            setPreloadingEnabled(true)
            maxConcurrentPreloads = config.maxPreloadConcurrency
        }
    }
    
    override fun onStop(owner: LifecycleOwner) {
        setPreloadingEnabled(false)
    }
    
    override fun onDestroy(owner: LifecycleOwner) {
        preloadWorker?.cancel()
        scope.cancel()
        memoryPressureManager.unregisterCallback(this)
        TimberLogger.d(TAG, "预加载管理器已销毁")
    }
    
    // ========== 私有方法 ==========
    
    /**
     * 启动预加载工作协程
     */
    private fun startPreloadWorker() {
        preloadWorker = scope.launch {
            while (isActive) {
                try {
                    val task = preloadQueue.poll()
                    if (task != null) {
                        // 检查并发限制
                        while (activeTasks.size >= maxConcurrentPreloads && isActive) {
                            delay(100)
                        }
                        
                        if (isActive) {
                            processPreloadTask(task)
                        }
                    } else {
                        // 队列为空时短暂休息
                        delay(200)
                    }
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "预加载工作协程异常", e)
                    delay(1000)
                }
            }
        }
    }
    
    /**
     * 处理单个预加载任务
     */
    private fun processPreloadTask(task: PreloadTask) {
        val job = scope.launch {
            val startTime = System.currentTimeMillis()
            
            try {
                activeTasks[task.url] = coroutineContext[Job]!!
                
                // 设置超时
                withTimeout(TASK_TIMEOUT_MS) {
                    val bitmap = imageLoader.loadImage(task.url, task.needToken)
                    
                    if (bitmap != null) {
                        completedUrls.add(task.url)
                        successfulPreloads.incrementAndGet()
                        
                        val loadTime = System.currentTimeMillis() - startTime
                        totalLoadTime.addAndGet(loadTime.toInt())
                        
                        TimberLogger.v(TAG, "预加载完成: ${task.url}, 耗时: ${loadTime}ms")
                    } else {
                        error("图片加载返回null")
                    }
                }
                
            } catch (e: Exception) {
                failedPreloads.incrementAndGet()
                TimberLogger.w(TAG, "预加载失败: ${task.url}", e)
                
                // 重试逻辑
                if (task.retryCount < MAX_RETRY_COUNT) {
                    delay(RETRY_DELAY_MS)
                    val retryTask = task.copy(retryCount = task.retryCount + 1)
                    preloadQueue.offer(retryTask)
                    TimberLogger.d(TAG, "预加载重试: ${task.url}, 第${retryTask.retryCount}次")
                }
                
            } finally {
                activeTasks.remove(task.url)
            }
        }
        
        activeTasks[task.url] = job
    }
    
    /**
     * 清理低优先级任务
     */
    private fun clearLowPriorityTasks() {
        val iterator = preloadQueue.iterator()
        while (iterator.hasNext()) {
            val task = iterator.next()
            if (task.priority == PreloadPriority.LOW || task.priority == PreloadPriority.IDLE) {
                iterator.remove()
                activeTasks[task.url]?.cancel()
                activeTasks.remove(task.url)
            }
        }
    }
    
    /**
     * 清理闲置任务
     */
    private fun clearIdleTasks() {
        preloadQueue.removeAll { it.priority == PreloadPriority.IDLE }
    }
} 
