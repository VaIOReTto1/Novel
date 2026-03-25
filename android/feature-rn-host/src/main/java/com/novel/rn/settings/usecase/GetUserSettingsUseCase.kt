package com.novel.rn.settings.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.ui.theme.ThemeManager
import com.novel.core.logging.CoreLogger
import javax.inject.Inject

/**
 * 获取用户设置UseCase
 * 
 * 功能：
 * - 加载当前主题设置
 * - 获取缓存信息
 * - 读取自动切换配置
 * - 时间设置获取
 */
@Stable
class GetUserSettingsUseCase @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val themeManager: ThemeManager
) : BaseUseCase<Unit, GetUserSettingsUseCase.SettingsData>() {

    companion object {
        private const val TAG = "GetUserSettingsUseCase"
    }

    data class SettingsData(
        val currentThemeMode: String,
        val actualTheme: String,
        val isFollowSystemTheme: Boolean,
        val isAutoNightModeEnabled: Boolean,
        val nightModeStartTime: String,
        val nightModeEndTime: String,
        val cacheSize: String
    )

    override suspend fun execute(parameters: Unit): SettingsData {
        CoreLogger.d(TAG, "开始获取用户设置")
        
        try {
            // 获取主题相关设置
            val currentThemeMode = themeManager.getCurrentThemeMode()
            val actualTheme = themeManager.getCurrentActualThemeMode()
            val isFollowSystemTheme = settingsUtils.isFollowSystemTheme()
            
            // 获取自动切换相关设置
            val isAutoNightModeEnabled = settingsUtils.isAutoNightModeEnabled()
            val nightModeStartTime = settingsUtils.getNightModeStartTime()
            val nightModeEndTime = settingsUtils.getNightModeEndTime()
            
            // 计算缓存大小
            val cacheSize = try {
                val size = settingsUtils.calculateCacheSize()
                settingsUtils.formatCacheSize(size)
            } catch (e: Exception) {
                CoreLogger.e(TAG, "计算缓存大小失败", e)
                "计算失败"
            }
            
            val settingsData = SettingsData(
                currentThemeMode = currentThemeMode,
                actualTheme = actualTheme,
                isFollowSystemTheme = isFollowSystemTheme,
                isAutoNightModeEnabled = isAutoNightModeEnabled,
                nightModeStartTime = nightModeStartTime,
                nightModeEndTime = nightModeEndTime,
                cacheSize = cacheSize
            )
            
            CoreLogger.d(TAG, "用户设置获取成功: $settingsData")
            return settingsData
            
        } catch (e: Exception) {
            CoreLogger.e(TAG, "获取用户设置失败", e)
            throw e
        }
    }
} 

