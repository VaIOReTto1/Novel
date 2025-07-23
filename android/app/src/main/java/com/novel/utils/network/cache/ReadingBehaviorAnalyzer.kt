package com.novel.utils.network.cache

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 阅读行为数据
 */
@Stable
data class ReadingBehavior(
    val bookId: Long,
    val chapterId: Long,
    val readingStartTime: Long,
    val readingEndTime: Long,
    val readingDuration: Long, // 毫秒
    val deviceState: DeviceState
)

/**
 * 设备状态信息
 */
@Stable
data class DeviceState(
    val networkType: NetworkType,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val availableStorage: Long // 字节
)

/**
 * 网络类型
 */
enum class NetworkType {
    WIFI, CELLULAR, NONE
}

/**
 * 阅读统计信息
 */
@Stable
data class ReadingStats(
    val averageReadingSpeed: Long, // 每章平均阅读时间（毫秒）
    val totalReadingTime: Long,    // 总阅读时间
    val chaptersRead: Int,         // 已读章节数
    val consecutiveReadingSessions: Int, // 连续阅读次数
    val preferredReadingTime: Int, // 偏好阅读时段（小时）
    val lastReadingSession: Long   // 最后阅读时间
)

/**
 * 预取建议
 */
@Stable
data class PrefetchRecommendation(
    val shouldPrefetch: Boolean,
    val prefetchCount: Int,
    val nextChapterIds: List<Long>,
    val priority: PrefetchPriority,
    val estimatedNetworkUsage: Long, // 字节
    val reason: String
)

/**
 * 预取优先级
 */
enum class PrefetchPriority {
    HIGH, MEDIUM, LOW, NONE
}

/**
 * 阅读行为分析器
 * 
 * 功能：
 * - 收集和分析用户阅读行为
 * - 预测用户阅读模式
 * - 生成智能预取建议
 * - 监控设备状态
 */
@Stable
@Singleton
class ReadingBehaviorAnalyzer @Inject constructor(
    private val context: Context,
    private val userDefaults: NovelUserDefaults
) {
    companion object {
        private const val TAG = "ReadingBehaviorAnalyzer"
        private const val MAX_BEHAVIOR_RECORDS = 100
        
        // 预取策略配置
        private const val DEFAULT_PREFETCH_COUNT = 3
        private const val WIFI_PREFETCH_COUNT = 5
        private const val CELLULAR_PREFETCH_COUNT = 2
        private const val LOW_BATTERY_THRESHOLD = 20
        private const val MIN_STORAGE_THRESHOLD = 100 * 1024 * 1024L // 100MB
    }
    
    private val behaviorHistory = mutableListOf<ReadingBehavior>()
    
    private val _currentReadingStats = MutableStateFlow(getInitialStats())
    val currentReadingStats: StateFlow<ReadingStats> = _currentReadingStats.asStateFlow()
    
    init {
        loadBehaviorHistory()
        TimberLogger.d(TAG, "阅读行为分析器初始化完成")
    }
    
    /**
     * 记录阅读行为
     */
    fun recordReadingBehavior(behavior: ReadingBehavior) {
        TimberLogger.d(TAG, "记录阅读行为: bookId=${behavior.bookId}, chapterId=${behavior.chapterId}, duration=${behavior.readingDuration}ms")
        
        behaviorHistory.add(behavior)
        
        // 保持历史记录数量限制
        if (behaviorHistory.size > MAX_BEHAVIOR_RECORDS) {
            behaviorHistory.removeAt(0)
        }
        
        // 更新统计信息
        updateReadingStats()
        
        // 持久化行为历史
        saveBehaviorHistory()
    }
    
    /**
     * 开始阅读会话
     */
    fun startReadingSession(bookId: Long, chapterId: Long): Long {
        val sessionId = System.currentTimeMillis()
        TimberLogger.d(TAG, "开始阅读会话: bookId=$bookId, chapterId=$chapterId, sessionId=$sessionId")
        return sessionId
    }
    
    /**
     * 结束阅读会话
     */
    fun endReadingSession(
        sessionId: Long,
        bookId: Long,
        chapterId: Long
    ) {
        val endTime = System.currentTimeMillis()
        val duration = endTime - sessionId
        
        if (duration > 0) {
            val deviceState = getCurrentDeviceState()
            val behavior = ReadingBehavior(
                bookId = bookId,
                chapterId = chapterId,
                readingStartTime = sessionId,
                readingEndTime = endTime,
                readingDuration = duration,
                deviceState = deviceState
            )
            
            recordReadingBehavior(behavior)
        }
    }
    
    /**
     * 生成预取建议
     */
    fun generatePrefetchRecommendation(
        currentBookId: Long,
        currentChapterId: Long,
        availableChapters: List<Long>
    ): PrefetchRecommendation {
        val deviceState = getCurrentDeviceState()
        val stats = _currentReadingStats.value
        
        // 基于设备状态判断是否应该预取
        val shouldPrefetch = shouldPrefetchBasedOnDeviceState(deviceState)
        
        if (!shouldPrefetch) {
            return PrefetchRecommendation(
                shouldPrefetch = false,
                prefetchCount = 0,
                nextChapterIds = emptyList(),
                priority = PrefetchPriority.NONE,
                estimatedNetworkUsage = 0,
                reason = "设备状态不适合预取（电量低/存储不足/无网络）"
            )
        }
        
        // 确定预取数量
        val prefetchCount = determinePrefetchCount(deviceState, stats)
        
        // 预测下一个需要的章节
        val nextChapterIds = predictNextChapters(currentChapterId, availableChapters, prefetchCount)
        
        // 计算优先级
        val priority = calculatePrefetchPriority(deviceState, stats)
        
        // 估算网络使用量（假设每章平均100KB）
        val estimatedUsage = nextChapterIds.size * 100 * 1024L
        
        TimberLogger.d(TAG, "生成预取建议: count=$prefetchCount, priority=$priority, chapters=$nextChapterIds")
        
        return PrefetchRecommendation(
            shouldPrefetch = nextChapterIds.isNotEmpty(),
            prefetchCount = prefetchCount,
            nextChapterIds = nextChapterIds,
            priority = priority,
            estimatedNetworkUsage = estimatedUsage,
            reason = "基于阅读习惯的智能预取建议"
        )
    }
    
    /**
     * 获取当前设备状态
     */
    private fun getCurrentDeviceState(): DeviceState {
        val networkType = getNetworkType()
        val batteryInfo = getBatteryInfo()
        val storageInfo = getAvailableStorage()
        
        return DeviceState(
            networkType = networkType,
            batteryLevel = batteryInfo.first,
            isCharging = batteryInfo.second,
            availableStorage = storageInfo
        )
    }
    
    /**
     * 获取网络类型
     */
    private fun getNetworkType(): NetworkType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }
    
    /**
     * 获取电池信息
     */
    private fun getBatteryInfo(): Pair<Int, Boolean> {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = batteryManager.isCharging
        
        return Pair(batteryLevel, isCharging)
    }
    
    /**
     * 获取可用存储空间
     */
    private fun getAvailableStorage(): Long {
        return try {
            context.cacheDir.usableSpace
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取存储空间失败", e)
            0L
        }
    }
    
    /**
     * 基于设备状态判断是否应该预取
     */
    private fun shouldPrefetchBasedOnDeviceState(deviceState: DeviceState): Boolean {
        return when {
            deviceState.networkType == NetworkType.NONE -> false
            deviceState.batteryLevel < LOW_BATTERY_THRESHOLD && !deviceState.isCharging -> false
            deviceState.availableStorage < MIN_STORAGE_THRESHOLD -> false
            else -> true
        }
    }
    
    /**
     * 确定预取数量
     */
    private fun determinePrefetchCount(deviceState: DeviceState, stats: ReadingStats): Int {
        val baseCount = when (deviceState.networkType) {
            NetworkType.WIFI -> WIFI_PREFETCH_COUNT
            NetworkType.CELLULAR -> CELLULAR_PREFETCH_COUNT
            NetworkType.NONE -> 0
        }
        
        // 根据阅读速度调整
        val speedMultiplier = when {
            stats.averageReadingSpeed < 5 * 60 * 1000 -> 1.5f // 快速阅读，增加预取
            stats.averageReadingSpeed > 20 * 60 * 1000 -> 0.7f // 慢速阅读，减少预取
            else -> 1.0f
        }
        
        return (baseCount * speedMultiplier).toInt().coerceAtMost(10)
    }
    
    /**
     * 预测下一个需要的章节
     */
    private fun predictNextChapters(
        currentChapterId: Long,
        availableChapters: List<Long>,
        prefetchCount: Int
    ): List<Long> {
        val currentIndex = availableChapters.indexOf(currentChapterId)
        if (currentIndex == -1) return emptyList()
        
        val nextChapters = mutableListOf<Long>()
        for (i in 1..prefetchCount) {
            val nextIndex = currentIndex + i
            if (nextIndex < availableChapters.size) {
                nextChapters.add(availableChapters[nextIndex])
            }
        }
        
        return nextChapters
    }
    
    /**
     * 计算预取优先级
     */
    private fun calculatePrefetchPriority(deviceState: DeviceState, stats: ReadingStats): PrefetchPriority {
        return when {
            deviceState.networkType == NetworkType.WIFI && deviceState.isCharging -> PrefetchPriority.HIGH
            deviceState.networkType == NetworkType.WIFI -> PrefetchPriority.MEDIUM
            deviceState.networkType == NetworkType.CELLULAR && stats.consecutiveReadingSessions > 3 -> PrefetchPriority.MEDIUM
            deviceState.networkType == NetworkType.CELLULAR -> PrefetchPriority.LOW
            else -> PrefetchPriority.NONE
        }
    }
    
    /**
     * 更新阅读统计信息
     */
    private fun updateReadingStats() {
        if (behaviorHistory.isEmpty()) return
        
        val totalDuration = behaviorHistory.sumOf { it.readingDuration }
        val averageSpeed = totalDuration / behaviorHistory.size
        val chaptersRead = behaviorHistory.distinctBy { "${it.bookId}_${it.chapterId}" }.size
        
        // 计算连续阅读次数
        val consecutiveSessions = calculateConsecutiveSessions()
        
        // 计算偏好阅读时段
        val preferredHour = calculatePreferredReadingTime()
        
        val lastSession = behaviorHistory.maxOfOrNull { it.readingEndTime } ?: System.currentTimeMillis()
        
        val newStats = ReadingStats(
            averageReadingSpeed = averageSpeed,
            totalReadingTime = totalDuration,
            chaptersRead = chaptersRead,
            consecutiveReadingSessions = consecutiveSessions,
            preferredReadingTime = preferredHour,
            lastReadingSession = lastSession
        )
        
        _currentReadingStats.value = newStats
        saveReadingStats(newStats)
        
        TimberLogger.d(TAG, "更新阅读统计: averageSpeed=${averageSpeed}ms, chaptersRead=$chaptersRead")
    }
    
    /**
     * 计算连续阅读次数
     */
    private fun calculateConsecutiveSessions(): Int {
        if (behaviorHistory.isEmpty()) return 0
        
        val sortedHistory = behaviorHistory.sortedBy { it.readingStartTime }
        var consecutiveCount = 1
        var currentStreak = 1
        
        for (i in 1 until sortedHistory.size) {
            val timeDiff = sortedHistory[i].readingStartTime - sortedHistory[i - 1].readingEndTime
            if (timeDiff < 60 * 60 * 1000) { // 1小时内算连续
                currentStreak++
            } else {
                consecutiveCount = maxOf(consecutiveCount, currentStreak)
                currentStreak = 1
            }
        }
        
        return maxOf(consecutiveCount, currentStreak)
    }
    
    /**
     * 计算偏好阅读时段
     */
    private fun calculatePreferredReadingTime(): Int {
        if (behaviorHistory.isEmpty()) return 20 // 默认晚上8点
        
        val hourCounts = mutableMapOf<Int, Int>()
        behaviorHistory.forEach { behavior ->
            val hour = java.util.Calendar.getInstance().apply {
                timeInMillis = behavior.readingStartTime
            }.get(java.util.Calendar.HOUR_OF_DAY)
            
            hourCounts[hour] = hourCounts.getOrDefault(hour, 0) + 1
        }
        
        return hourCounts.maxByOrNull { it.value }?.key ?: 20
    }
    
    /**
     * 获取初始统计信息
     */
    private fun getInitialStats(): ReadingStats {
        return try {
            val averageSpeed = userDefaults.getString("average_reading_speed")?.toLongOrNull() ?: 10 * 60 * 1000L
            val totalTime = userDefaults.getString("total_reading_time")?.toLongOrNull() ?: 0L
            val chaptersRead = userDefaults.getString("chapters_read")?.toIntOrNull() ?: 0
            val consecutiveSessions = userDefaults.getString("consecutive_sessions")?.toIntOrNull() ?: 0
            val preferredTime = userDefaults.getString("preferred_reading_time")?.toIntOrNull() ?: 20
            val lastSession = userDefaults.getString("last_reading_session")?.toLongOrNull() ?: System.currentTimeMillis()
            
            ReadingStats(
                averageReadingSpeed = averageSpeed,
                totalReadingTime = totalTime,
                chaptersRead = chaptersRead,
                consecutiveReadingSessions = consecutiveSessions,
                preferredReadingTime = preferredTime,
                lastReadingSession = lastSession
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载初始统计信息失败", e)
            ReadingStats(10 * 60 * 1000L, 0L, 0, 0, 20, System.currentTimeMillis())
        }
    }
    
    /**
     * 保存阅读统计信息
     */
    private fun saveReadingStats(stats: ReadingStats) {
        try {
            userDefaults.setString("average_reading_speed", stats.averageReadingSpeed.toString())
            userDefaults.setString("total_reading_time", stats.totalReadingTime.toString())
            userDefaults.setString("chapters_read", stats.chaptersRead.toString())
            userDefaults.setString("consecutive_sessions", stats.consecutiveReadingSessions.toString())
            userDefaults.setString("preferred_reading_time", stats.preferredReadingTime.toString())
            userDefaults.setString("last_reading_session", stats.lastReadingSession.toString())
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存阅读统计信息失败", e)
        }
    }
    
    /**
     * 加载行为历史
     */
    private fun loadBehaviorHistory() {
        try {
            // 这里可以从UserDefaults或数据库加载历史数据
            // 为简化实现，暂时跳过持久化历史行为的加载
            TimberLogger.d(TAG, "行为历史加载完成，记录数: ${behaviorHistory.size}")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "加载行为历史失败", e)
        }
    }
    
    /**
     * 保存行为历史
     */
    private fun saveBehaviorHistory() {
        try {
            // 这里可以持久化行为历史到UserDefaults或数据库
            // 为简化实现，暂时跳过持久化
            TimberLogger.d(TAG, "行为历史保存完成，记录数: ${behaviorHistory.size}")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存行为历史失败", e)
        }
    }
} 