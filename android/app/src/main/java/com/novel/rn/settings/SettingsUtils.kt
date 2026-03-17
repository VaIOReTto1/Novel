package com.novel.rn.settings

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.novel.ui.theme.ThemeManager
import com.novel.utils.TimberLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 设置工具类 - 优化版
 *
 * 功能模块：
 * - 缓存管理（计算、清理、格式化显示）
 * - 主题切换（浅色/深色/跟随系统）
 * - 定时切换夜间模式（使用WorkManager实现省电定时切换）
 * - 配置持久化（SharedPreferences封装）
 * - 全局主题同步管理
 *
 * 技术特点：
 * - Hilt单例依赖注入
 * - 协程异步IO操作
 * - 多级缓存目录处理
 * - 主题状态统一管理
 * - WorkManager后台定时任务，系统休眠时亦能触发，且省电
 */
@Stable
@Singleton
class SettingsUtils @Inject constructor(
    @Stable
    @ApplicationContext private val context: Context,
    @Stable
    private val settingsPreferenceStorage: SettingsPreferenceStorage
) {

    companion object {
        private const val TAG = "SettingsUtils"

        // 🎯 优化：WorkManager任务标识
        private const val NIGHT_MODE_WORK_TAG = "night_mode_timer_work"
        private const val WORK_CHECK_INTERVAL_MINUTES = 15L // 15分钟检查一次，平衡精度和省电
    }

    // 获取全局主题管理器
    private val themeManager by lazy { ThemeManager.getInstance(context) }

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
     * 优化：简化逻辑，去除双向往返
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
     * 优化：只通知原生，不再期待回读
     */
    fun setNightMode(mode: String) {
        TimberLogger.d(TAG, "🔧 开始设置主题模式: $mode")
        settingsPreferenceStorage.setNightMode(mode)
        TimberLogger.d(TAG, "📝 已保存主题模式到配置: $mode")

        // 🎯 优化：只通知ThemeManager，不期待返回值
        TimberLogger.d(TAG, "🎨 调用themeManager.setThemeMode: $mode")
        themeManager.setThemeMode(mode, notifyRN = false) // RN端会立即更新本地状态
        TimberLogger.d(TAG, "✅ themeManager.setThemeMode调用完成")

        when (mode) {
            "light" -> {
                settingsPreferenceStorage.setFollowSystemTheme(false)
            }
            "dark" -> {
                settingsPreferenceStorage.setFollowSystemTheme(false)
            }
            "auto" -> {
                settingsPreferenceStorage.setFollowSystemTheme(true)
            }
        }
    }

    /**
     * 获取当前夜间模式
     */
    internal fun getCurrentNightMode(): String {
        return settingsPreferenceStorage.getNightMode()
    }

    /**
     * 是否跟随系统主题
     */
    fun isFollowSystemTheme(): Boolean {
        return settingsPreferenceStorage.isFollowSystemTheme()
    }

    /**
     * 设置是否跟随系统主题
     */
    fun setFollowSystemTheme(follow: Boolean) {
        settingsPreferenceStorage.setFollowSystemTheme(follow)
        if (follow) {
            setNightMode("auto")
        }
    }

    /**
     * 设置自动切换夜间模式 - 优化版
     * 使用WorkManager替换Handler轮询
     */
    fun setAutoNightMode(enabled: Boolean) {
        TimberLogger.d(TAG, "🎯 设置自动切换夜间模式: $enabled")
        settingsPreferenceStorage.setAutoNightModeEnabled(enabled)

        if (enabled) {
            startTimeBasedThemeCheckWithWorkManager()
        } else {
            stopTimeBasedThemeCheckWithWorkManager()
        }
    }

    /**
     * 是否启用自动切换夜间模式
     */
    fun isAutoNightModeEnabled(): Boolean {
        return settingsPreferenceStorage.isAutoNightModeEnabled()
    }

    /**
     * 设置夜间模式时间段
     */
    fun setNightModeTime(startTime: String, endTime: String) {
        TimberLogger.d(TAG, "设置夜间模式时间: $startTime - $endTime")
        settingsPreferenceStorage.setNightModeTime(startTime, endTime)

        // 如果定时切换已启用，重新启动检查
        if (isAutoNightModeEnabled()) {
            startTimeBasedThemeCheckWithWorkManager()
        }
    }

    /**
     * 获取夜间模式开始时间
     */
    fun getNightModeStartTime(): String {
        return settingsPreferenceStorage.getNightModeStartTime()
    }

    /**
     * 获取夜间模式结束时间
     */
    fun getNightModeEndTime(): String {
        return settingsPreferenceStorage.getNightModeEndTime()
    }

    /**
     * 🎯 优化：使用WorkManager启动基于时间的主题检查
     */
    fun startTimeBasedThemeCheckWithWorkManager() {
        TimberLogger.d(TAG, "🎯 启动基于WorkManager的主题检查")

        // 如果已经在跟随系统主题，不启动定时切换
        if (isFollowSystemTheme()) {
            TimberLogger.d(TAG, "当前跟随系统主题，跳过定时切换")
            return
        }

        try {
            val workManager = WorkManager.getInstance(context)
            
            // 创建周期性工作请求
            val nightModeWorkRequest = PeriodicWorkRequestBuilder<NightModeWorker>(
                WORK_CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES
            )
                .addTag(NIGHT_MODE_WORK_TAG)
                .build()

            // 启动或更新工作，使用UPDATE策略确保只有一个实例
            workManager.enqueueUniquePeriodicWork(
                NIGHT_MODE_WORK_TAG,
                ExistingPeriodicWorkPolicy.UPDATE,
                nightModeWorkRequest
            )

            TimberLogger.d(TAG, "✅ WorkManager定时主题检查已启动，间隔: ${WORK_CHECK_INTERVAL_MINUTES}分钟")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "启动WorkManager定时主题检查失败", e)
        }
    }

    /**
     * 🎯 优化：停止基于WorkManager的主题检查
     */
    fun stopTimeBasedThemeCheckWithWorkManager() {
        TimberLogger.d(TAG, "🎯 停止基于WorkManager的主题检查")
        try {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(NIGHT_MODE_WORK_TAG)
            TimberLogger.d(TAG, "✅ WorkManager定时主题检查已停止")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "停止WorkManager定时主题检查失败", e)
        }
    }

    /**
     * 🎯 兼容性：保留旧方法名，内部调用新的WorkManager方法
     */
    fun startTimeBasedThemeCheck() {
        startTimeBasedThemeCheckWithWorkManager()
    }

    /**
     * 🎯 兼容性：保留旧方法名，内部调用新的WorkManager方法
     */
    fun stopTimeBasedThemeCheck() {
        stopTimeBasedThemeCheckWithWorkManager()
    }

    /**
     * 检查当前时间并根据设定切换主题
     * 现在由NightModeWorker调用
     */
    internal fun checkAndSwitchThemeBasedOnTime() {
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
            TimberLogger.d(TAG, "🎯 时间切换主题: $currentMode -> $expectedMode")
            setNightMode(expectedMode)
            TimberLogger.d(TAG, "✅ 定时主题切换完成: $expectedMode")
        }
    }

    /**
     * 将时间字符串转换为分钟数
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
            startTimeBasedThemeCheckWithWorkManager()
        }
    }

    /**
     * 清理资源（在应用退出时调用）
     */
    fun cleanup() {
        TimberLogger.d(TAG, "清理定时器资源")
        stopTimeBasedThemeCheckWithWorkManager()
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

/**
 * 🎯 新增：夜间模式WorkManager Worker
 * 替换Handler轮询，实现省电的后台定时切换
 */
class NightModeWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        private const val TAG = "NightModeWorker"
    }

    override fun doWork(): Result {
        return try {
            TimberLogger.d(TAG, "🎯 NightModeWorker执行定时主题检查")
            
            // 获取SettingsUtils实例并执行检查
            // 注意：由于Worker运行在后台，需要确保依赖注入可用
            // 这里使用简化的方式直接访问
            val prefs = applicationContext.getSharedPreferences("theme_preferences", Context.MODE_PRIVATE)
            val novelUserDefaults = com.novel.utils.Store.UserDefaults.SharedPrefsUserDefaults(prefs)
            val storageFacade = com.novel.core.storage.LegacyStorageFacade(novelUserDefaults)
            val preferenceStorage = SettingsPreferenceStorage(storageFacade)
            val settingsUtils = SettingsUtils(applicationContext, preferenceStorage)
            
            settingsUtils.checkAndSwitchThemeBasedOnTime()
            
            TimberLogger.d(TAG, "✅ NightModeWorker执行完成")
            Result.success()
        } catch (e: Exception) {
            TimberLogger.e(TAG, "❌ NightModeWorker执行失败", e)
            Result.failure()
        }
    }
}
