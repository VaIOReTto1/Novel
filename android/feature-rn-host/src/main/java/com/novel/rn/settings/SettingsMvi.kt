package com.novel.rn.settings

import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState

sealed class SettingsIntent : MviIntent {
    data object LoadCurrentTheme : SettingsIntent()
    data object ToggleNightMode : SettingsIntent()
    data class SetNightMode(val mode: String) : SettingsIntent()
    data class SetFollowSystemTheme(val follow: Boolean) : SettingsIntent()
    data class SetAutoNightMode(val enabled: Boolean) : SettingsIntent()
    data class SetNightModeTime(val startTime: String, val endTime: String) : SettingsIntent()
    data object CheckCurrentTimeTheme : SettingsIntent()
    data object CalculateCacheSize : SettingsIntent()
    data object ClearAllCache : SettingsIntent()
    data object NavigateToTimedSwitch : SettingsIntent()
    data object NavigateToHelpSupport : SettingsIntent()
    data object NavigateToPrivacyPolicy : SettingsIntent()
    data object Logout : SettingsIntent()
    data object ConfirmLogout : SettingsIntent()
}

data class SettingsState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val currentThemeMode: String = "auto",
    val actualTheme: String = "light",
    val isFollowSystemTheme: Boolean = true,
    val isAutoNightModeEnabled: Boolean = false,
    val nightModeStartTime: String = "22:00",
    val nightModeEndTime: String = "06:00",
    val cacheSize: String = "计算中...",
    val isCacheCalculating: Boolean = false,
    val isCacheClearing: Boolean = false,
) : MviState

sealed class SettingsEffect : MviEffect {
    data class NotifyThemeChanged(val theme: String) : SettingsEffect()
    data class ShowToast(val message: String) : SettingsEffect()
    data class ShowError(val error: String) : SettingsEffect()
    data object NavigateToTimedSwitch : SettingsEffect()
    data object NavigateToHelpSupport : SettingsEffect()
    data object NavigateToPrivacyPolicy : SettingsEffect()
    data class CacheCalculated(val size: String) : SettingsEffect()
    data class CacheCleared(val message: String) : SettingsEffect()
    data class LogoutSuccess(val message: String) : SettingsEffect()
    data class LogoutError(val error: String) : SettingsEffect()
}
