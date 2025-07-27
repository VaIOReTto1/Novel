package com.novel.rn.settings

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Stable
import com.novel.ui.theme.ThemeManager
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.TimberLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置工具类
 *
 * 功能模块：
 * - 缓存管理（计算、清理、格式化显示）
 * - 主题切换（浅色/深色/跟随系统）
 * - 定时切换夜间模式（根据设定时间自动切换）
 * - 配置持久化（SharedPreferences封装）
 * - 全局主题同步管理
 *
 * 技术特点：
 * - Hilt单例依赖注入
 * - 协程异步IO操作
 * - 多级缓存目录处理
 * - 主题状态统一管理
 * - 定时器自动管理主题切换
 */
@Stable
@Singleton
class SettingsUtils @Inject constructor(
    @Stable
    @ApplicationContext private val context: Context,
    @Stable
    private val novelUserDefaults: NovelUserDefaults
) {

    companion object {
        private const val TAG = "SettingsUtils"
        private const val PREF_NIGHT_MODE = "night_mode"
        private const val PREF_AUTO_NIGHT_MODE = "auto_night_mode"
        private const val PREF_FOLLOW_SYSTEM = "follow_system_theme"
        private const val PREF_NIGHT_START_TIME = "night_start_time"
        private const val PREF_NIGHT_END_TIME = "night_end_time"

        // 动态检查时间间隔
        private const val CHECK_INTERVAL_MINUTE = 60 * 1000L      // 1分钟
        private const val CHECK_INTERVAL_QUARTER = 15 * 60 * 1000L  // 15分钟
        private const val CHECK_INTERVAL_HOUR = 60 * 60 * 1000L   // 1小时

        // 时间临近阈值（分钟）
        private const val THRESHOLD_URGENT = 5    // 5分钟内用1分钟间隔
        private const val THRESHOLD_NEAR = 30     // 30分钟内用15分钟间隔
        private const val THRESHOLD_FAR = 120     // 2小时内用1小时间隔
    }

    // 获取全局主题管理器
    private val themeManager by lazy { ThemeManager.Companion.getInstance(context) }

    // 定时器相关
    private val handler = Handler(Looper.getMainLooper())
    private var timeCheckRunnable: Runnable? = null
    private var isTimeCheckingStarted = false

    /**
     * 清除所有缓存
     * 包括内部缓存、外部缓存、图片缓存等
     * @return 清理结果信息
     */
    suspend fun clearAllCache(): String = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "开始清理缓存...")
            var totalSize = 0L

            // 计算缓存大小
            totalSize += calculateCacheSize()

            // 清除应用内部缓存目录
            clearInternalCache()

            // 清除图片缓存等其他缓存
            clearImageCache()

            val result = "已清理 ${formatCacheSize(totalSize)} 缓存"
            TimberLogger.d(TAG, "缓存清理完成: $result")
            result
        } catch (e: Exception) {
            val errorMsg = "清理缓存失败: ${e.message}"
            TimberLogger.e(TAG, errorMsg, e)
            errorMsg
        }
    }

    /**
     * 计算缓存大小
     * 遍历内部和外部缓存目录
     * @return 总缓存大小（字节）
     */
    suspend fun calculateCacheSize(): Long = withContext(Dispatchers.IO) {
        try {
            val cacheDir = context.cacheDir
            val externalCacheDir = context.externalCacheDir

            var totalSize = 0L
            totalSize += getDirSize(cacheDir)
            externalCacheDir?.let { totalSize += getDirSize(it) }

            TimberLogger.d(TAG, "缓存大小计算完成: ${formatCacheSize(totalSize)}")
            totalSize
        } catch (e: Exception) {
            TimberLogger.e(TAG, "计算缓存大小失败", e)
            0L
        }
    }

    /**
     * 格式化缓存大小显示
     * 支持B/KB/MB/GB单位转换
     */
    @SuppressLint("DefaultLocale")
    fun formatCacheSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.1fGB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.1fMB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.1fKB", bytes / 1024.0)
            else -> "${bytes}B"
        }
    }

    /**
     * 切换夜间模式
     * 如果当前是跟随系统主题模式，首先关闭跟随系统主题，然后切换到对应的固定主题
     * 否则支持两种模式循环切换：浅色 ↔ 深色
     * @return 切换结果提示
     */
    fun toggleNightMode(): String {
        return try {
            val currentMode = getCurrentNightMode()
            val isFollowingSystem = isFollowSystemTheme()
            
            TimberLogger.d(TAG, "🎯 toggleNightMode开始 - 当前主题模式: $currentMode, 跟随系统: $isFollowingSystem")
            
            val newMode = if (isFollowingSystem) {
                // 如果当前是跟随系统主题，根据当前实际主题切换到对应的固定主题
                val actualTheme = themeManager.getCurrentActualThemeMode()
                TimberLogger.d(TAG, "🔄 跟随系统主题模式，当前实际主题: $actualTheme")
                when (actualTheme) {
                    "light" -> "dark"  // 当前是浅色，切换到深色
                    "dark" -> "light"  // 当前是深色，切换到浅色
                    else -> "light"    // 默认切换到浅色
                }
            } else {
                // 如果不是跟随系统主题，在浅色和深色之间切换
                TimberLogger.d(TAG, "🔄 固定主题模式，当前模式: $currentMode")
                when (currentMode) {
                    "light" -> "dark"
                    "dark" -> "light"
                    else -> "light"  // 默认切换到浅色
                }
            }

            TimberLogger.d(TAG, "🔧 准备设置主题模式为: $newMode")
            setNightMode(newMode)
            TimberLogger.d(TAG, "✅ setNightMode调用完成")
            
            val result = if (isFollowingSystem) {
                "已关闭跟随系统主题，切换至${getNightModeDisplayName(newMode)}模式"
            } else {
                "已切换至${getNightModeDisplayName(newMode)}模式"
            }
            TimberLogger.d(TAG, "🎉 主题切换完成: $result")
            result
        } catch (e: Exception) {
            val errorMsg = "切换夜间模式失败: ${e.message}"
            TimberLogger.e(TAG, errorMsg, e)
            errorMsg
        }
    }

    /**
     * 设置夜间模式
     * 同步更新配置和全局主题管理器
     * @param mode 主题模式（light/dark/auto）
     */
    fun setNightMode(mode: String) {
        TimberLogger.d(TAG, "🔧 开始设置主题模式: $mode")
        novelUserDefaults.setString(PREF_NIGHT_MODE, mode)
        TimberLogger.d(TAG, "📝 已保存主题模式到配置: $mode")

        // 使用全局主题管理器统一管理
        TimberLogger.d(TAG, "🎨 调用themeManager.setThemeMode: $mode")
        themeManager.setThemeMode(mode)
        TimberLogger.d(TAG, "✅ themeManager.setThemeMode调用完成")

        when (mode) {
            "light" -> {
                novelUserDefaults.setString(PREF_FOLLOW_SYSTEM, "false")
            }
            "dark" -> {
                novelUserDefaults.setString(PREF_FOLLOW_SYSTEM, "false")
            }
            "auto" -> {
                novelUserDefaults.setString(PREF_FOLLOW_SYSTEM, "true")
            }
        }
    }

    /**
     * 获取当前夜间模式
     */
    internal fun getCurrentNightMode(): String {
        return novelUserDefaults.getString(PREF_NIGHT_MODE) ?: "auto"
    }

    /**
     * 是否跟随系统主题
     */
    fun isFollowSystemTheme(): Boolean {
        return novelUserDefaults.getString(PREF_FOLLOW_SYSTEM)?.toBoolean() ?: true
    }

    /**
     * 设置是否跟随系统主题
     */
    fun setFollowSystemTheme(follow: Boolean) {
        novelUserDefaults.setString(PREF_FOLLOW_SYSTEM, follow.toString())
        if (follow) {
            setNightMode("auto")
        }
    }

    /**
     * 设置自动切换夜间模式
     */
    fun setAutoNightMode(enabled: Boolean) {
        TimberLogger.d(TAG, "设置自动切换夜间模式: $enabled")
        novelUserDefaults.setString(PREF_AUTO_NIGHT_MODE, enabled.toString())

        if (enabled) {
            startTimeBasedThemeCheck()
        } else {
            stopTimeBasedThemeCheck()
        }
    }

    /**
     * 是否启用自动切换夜间模式
     */
    fun isAutoNightModeEnabled(): Boolean {
        return novelUserDefaults.getString(PREF_AUTO_NIGHT_MODE)?.toBoolean() ?: false
    }

    /**
     * 设置夜间模式时间段
     * @param startTime 开始时间 格式：HH:mm (如 "22:00")
     * @param endTime 结束时间 格式：HH:mm (如 "06:00")
     */
    fun setNightModeTime(startTime: String, endTime: String) {
        TimberLogger.d(TAG, "设置夜间模式时间: $startTime - $endTime")
        novelUserDefaults.setString(PREF_NIGHT_START_TIME, startTime)
        novelUserDefaults.setString(PREF_NIGHT_END_TIME, endTime)

        // 如果定时切换已启用，重新启动检查
        if (isAutoNightModeEnabled()) {
            startTimeBasedThemeCheck()
        }
    }

    /**
     * 获取夜间模式开始时间
     */
    fun getNightModeStartTime(): String {
        return novelUserDefaults.getString(PREF_NIGHT_START_TIME) ?: "22:00"
    }

    /**
     * 获取夜间模式结束时间
     */
    fun getNightModeEndTime(): String {
        return novelUserDefaults.getString(PREF_NIGHT_END_TIME) ?: "06:00"
    }

    /**
     * 启动基于时间的主题检查
     */
    fun startTimeBasedThemeCheck() {
        TimberLogger.d(TAG, "启动基于时间的主题检查")

        // 如果已经在跟随系统主题，不启动定时切换
        if (isFollowSystemTheme()) {
            TimberLogger.d(TAG, "当前跟随系统主题，跳过定时切换")
            return
        }

        stopTimeBasedThemeCheck() // 先停止之前的检查

        // 立即执行一次检查
        try {
            checkAndSwitchThemeBasedOnTime()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "立即检查主题失败", e)
        }

        timeCheckRunnable = object : Runnable {
            override fun run() {
                try {
                    checkAndSwitchThemeBasedOnTime()
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "检查时间切换主题失败", e)
                }

                // 继续下一次检查，使用智能间隔
                val nextInterval = calculateNextCheckInterval()
                handler.postDelayed(this, nextInterval)
                TimberLogger.v(TAG, "已安排下次检查，间隔: ${nextInterval}ms")
            }
        }

        // 安排第一次定时检查（在立即检查之后）
        val firstInterval = calculateNextCheckInterval()
        timeCheckRunnable?.let { handler.postDelayed(it, firstInterval) }
        isTimeCheckingStarted = true

        TimberLogger.d(TAG, "定时主题检查已启动，首次间隔: ${firstInterval}ms")
    }

    /**
     * 停止基于时间的主题检查
     */
    fun stopTimeBasedThemeCheck() {
        TimberLogger.d(TAG, "停止基于时间的主题检查")
        timeCheckRunnable?.let { handler.removeCallbacks(it) }
        timeCheckRunnable = null
        isTimeCheckingStarted = false
    }

    /**
     * 检查当前时间并根据设定切换主题
     */
    private fun checkAndSwitchThemeBasedOnTime() {
        if (!isAutoNightModeEnabled() || isFollowSystemTheme()) {
            TimberLogger.v(TAG, "自动切换未启用或正在跟随系统主题，跳过时间检查")
            return
        }

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val currentTimeInMinutes = currentHour * 60 + currentMinute

        val startTime = getNightModeStartTime()
        val endTime = getNightModeEndTime()

        val startTimeInMinutes = parseTimeToMinutes(startTime)
        val endTimeInMinutes = parseTimeToMinutes(endTime)

        val shouldBeNightMode = if (startTimeInMinutes <= endTimeInMinutes) {
            // 同一天内的时间段，如 08:00 - 18:00
            currentTimeInMinutes in startTimeInMinutes..endTimeInMinutes
        } else {
            // 跨天的时间段，如 22:00 - 06:00
            currentTimeInMinutes >= startTimeInMinutes || currentTimeInMinutes <= endTimeInMinutes
        }

        val currentMode = getCurrentNightMode()
        val expectedMode = if (shouldBeNightMode) "dark" else "light"

        TimberLogger.v(TAG, "时间检查: 当前时间=${String.format("%02d:%02d", currentHour, currentMinute)}, " +
                "夜间时段=${startTime}-${endTime}, 应为夜间模式=${shouldBeNightMode}, " +
                "当前模式=${currentMode}, 期望模式=${expectedMode}")

        if (currentMode != expectedMode) {
            TimberLogger.d(TAG, "时间切换主题: $currentMode -> $expectedMode")
            setNightMode(expectedMode)

            // 立即通知RN端主题已切换
            val actualTheme = themeManager.getCurrentActualThemeMode()
            themeManager.notifyThemeChangedToRN(actualTheme)
            TimberLogger.d(TAG, "✅ 主题切换完成并已通知RN端: $actualTheme")
        }
    }

    /**
     * 计算到下次切换时间的最短距离（分钟）
     */
    private fun calculateMinutesToNextSwitch(): Int {
        val currentTime = Calendar.getInstance()
        val currentTimeInMinutes = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(
            Calendar.MINUTE)

        val startTime = getNightModeStartTime()
        val endTime = getNightModeEndTime()

        val startTimeInMinutes = parseTimeToMinutes(startTime)
        val endTimeInMinutes = parseTimeToMinutes(endTime)

        // 计算到开始时间和结束时间的距离
        val minutesToStart = if (startTimeInMinutes > currentTimeInMinutes) {
            startTimeInMinutes - currentTimeInMinutes
        } else {
            (24 * 60) - currentTimeInMinutes + startTimeInMinutes // 跨天计算
        }

        val minutesToEnd = if (endTimeInMinutes > currentTimeInMinutes) {
            endTimeInMinutes - currentTimeInMinutes
        } else {
            (24 * 60) - currentTimeInMinutes + endTimeInMinutes // 跨天计算
        }

        // 返回最短距离
        return Math.min(minutesToStart, minutesToEnd)
    }

    /**
     * 根据距离下次切换的时间，智能计算检查间隔
     */
    private fun calculateNextCheckInterval(): Long {
        val minutesToNext = calculateMinutesToNextSwitch()

        return when {
            minutesToNext <= THRESHOLD_URGENT -> {
                TimberLogger.v(TAG, "距离切换时间${minutesToNext}分钟，使用1分钟检查间隔")
                CHECK_INTERVAL_MINUTE
            }
            minutesToNext <= THRESHOLD_NEAR -> {
                TimberLogger.v(TAG, "距离切换时间${minutesToNext}分钟，使用15分钟检查间隔")
                CHECK_INTERVAL_QUARTER
            }
            minutesToNext <= THRESHOLD_FAR -> {
                TimberLogger.v(TAG, "距离切换时间${minutesToNext}分钟，使用1小时检查间隔")
                CHECK_INTERVAL_HOUR
            }
            else -> {
                TimberLogger.v(TAG, "距离切换时间${minutesToNext}分钟，使用1小时检查间隔")
                CHECK_INTERVAL_HOUR
            }
        }
    }

    /**
     * 将时间字符串转换为分钟数
     * @param timeStr 时间字符串，格式："HH:mm"
     * @return 从00:00开始的分钟数
     */
    private fun parseTimeToMinutes(timeStr: String): Int {
        return try {
            val parts = timeStr.split(":")
            if (parts.size == 2) {
                val hour = parts[0].toInt()
                val minute = parts[1].toInt()
                hour * 60 + minute
            } else {
                TimberLogger.w(TAG, "时间格式错误: $timeStr，使用默认值")
                22 * 60 // 默认22:00
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "解析时间失败: $timeStr", e)
            22 * 60 // 默认22:00
        }
    }

    /**
     * 初始化定时切换（在应用启动时调用）
     */
    fun initializeAutoThemeSwitch() {
        TimberLogger.d(TAG, "初始化自动主题切换")
        if (isAutoNightModeEnabled() && !isFollowSystemTheme()) {
            startTimeBasedThemeCheck()
        }
    }

    /**
     * 清理资源（在应用退出时调用）
     */
    fun cleanup() {
        TimberLogger.d(TAG, "清理定时器资源")
        stopTimeBasedThemeCheck()
    }

    private fun getNightModeDisplayName(mode: String): String {
        return when (mode) {
            "light" -> "浅色"
            "dark" -> "深色"
            "auto" -> "跟随系统"
            else -> "未知"
        }
    }

    private fun clearInternalCache() {
        val cacheDir = context.cacheDir
        deleteDir(cacheDir)
    }

    private fun clearImageCache() {
        // 清除图片缓存（如果使用Glide、Coil等）
        val imageCacheDir = File(context.cacheDir, "image_cache")
        if (imageCacheDir.exists()) {
            deleteDir(imageCacheDir)
        }
    }

    private fun getDirSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L

        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                getDirSize(file)
            } else {
                file.length()
            }
        }
        return size
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir == null || !dir.exists()) return false

        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                deleteDir(file)
            }
        }
        return dir.delete()
    }
}