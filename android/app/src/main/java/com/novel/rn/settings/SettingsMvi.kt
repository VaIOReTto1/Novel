package com.novel.rn.settings

import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect

/**
 * 设置模块MVI契约
 * 
 * 功能域：
 * - 主题管理（夜间模式、跟随系统、自动切换）
 * - 缓存管理（计算大小、清理缓存）
 * - 时间设置（夜间模式时间段）
 */

// ==================== Intent ====================
sealed class SettingsIntent : MviIntent {
    // 主题相关
    object LoadCurrentTheme : SettingsIntent()
    object ToggleNightMode : SettingsIntent()
    data class SetNightMode(val mode: String) : SettingsIntent()
    data class SetFollowSystemTheme(val follow: Boolean) : SettingsIntent()
    data class SetAutoNightMode(val enabled: Boolean) : SettingsIntent()
    data class SetNightModeTime(val startTime: String, val endTime: String) : SettingsIntent()
    object CheckCurrentTimeTheme : SettingsIntent()
    
    // 缓存相关
    object CalculateCacheSize : SettingsIntent()
    object ClearAllCache : SettingsIntent()
    
    // 导航相关
    object NavigateToTimedSwitch : SettingsIntent()
    object NavigateToHelpSupport : SettingsIntent()
    object NavigateToPrivacyPolicy : SettingsIntent()
    
    // 用户账户相关
    object Logout : SettingsIntent()
    object ConfirmLogout : SettingsIntent()
}

// ==================== State ====================
data class SettingsState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // 主题设置
    val currentThemeMode: String = "auto",
    val actualTheme: String = "light",
    val isFollowSystemTheme: Boolean = true,
    val isAutoNightModeEnabled: Boolean = false,
    val nightModeStartTime: String = "22:00",
    val nightModeEndTime: String = "06:00",
    
    // 缓存信息
    val cacheSize: String = "计算中...",
    val isCacheCalculating: Boolean = false,
    val isCacheClearing: Boolean = false
) : MviState

// ==================== Effect ====================
sealed class SettingsEffect : MviEffect {
    // 主题变更通知
    data class NotifyThemeChanged(val theme: String) : SettingsEffect()
    
    // 用户反馈
    data class ShowToast(val message: String) : SettingsEffect()
    data class ShowError(val error: String) : SettingsEffect()
    
    // 导航
    object NavigateToTimedSwitch : SettingsEffect()
    object NavigateToHelpSupport : SettingsEffect()
    object NavigateToPrivacyPolicy : SettingsEffect()
    
    // 缓存操作结果
    data class CacheCalculated(val size: String) : SettingsEffect()
    data class CacheCleared(val message: String) : SettingsEffect()
    
    // 用户账户操作结果

    data class LogoutSuccess(val message: String) : SettingsEffect()
    data class LogoutError(val error: String) : SettingsEffect()
}