package com.novel.rn.settings

import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@Stable
class SettingsStateAdapter(
    stateFlow: StateFlow<SettingsState>,
) : StateAdapter<SettingsState>(stateFlow) {

    val currentThemeMode = mapState { it.currentThemeMode }
    val actualTheme = mapState { it.actualTheme }
    val isFollowSystemTheme = mapState { it.isFollowSystemTheme }
    val isAutoNightModeEnabled = mapState { it.isAutoNightModeEnabled }
    val nightModeStartTime = mapState { it.nightModeStartTime }
    val nightModeEndTime = mapState { it.nightModeEndTime }
    val cacheSize = mapState { it.cacheSize }
    val isCacheCalculating = mapState { it.isCacheCalculating }
    val isCacheClearing = mapState { it.isCacheClearing }

    fun isDarkTheme(): Boolean = getCurrentSnapshot().actualTheme == "dark"

    fun isLightTheme(): Boolean = getCurrentSnapshot().actualTheme == "light"

    fun canToggleTheme(): Boolean = !isCurrentlyLoading()

    fun canPerformCacheOperation(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isCacheCalculating && !state.isCacheClearing && !state.isLoading
    }

    fun isCacheOperationInProgress(): Boolean {
        val state = getCurrentSnapshot()
        return state.isCacheCalculating || state.isCacheClearing
    }

    fun getThemeDisplayName(): String {
        return when (getCurrentSnapshot().currentThemeMode) {
            "light" -> "浅色"
            "dark" -> "深色"
            "auto" -> "跟随系统"
            else -> "未知"
        }
    }

    fun getActualThemeDisplayName(): String = if (isDarkTheme()) "深色" else "浅色"

    fun getNightModeTimeRange(): String {
        val state = getCurrentSnapshot()
        return "${state.nightModeStartTime} - ${state.nightModeEndTime}"
    }

    fun getSettingsStatusSummary(): String {
        val state = getCurrentSnapshot()
        return buildString {
            append("主题: ${getThemeDisplayName()}")
            if (state.isFollowSystemTheme) append(" (跟随系统)")
            if (state.isAutoNightModeEnabled) append(" (自动切换)")
            if (state.isLoading) append(", 加载中")
            if (isCacheOperationInProgress()) append(", 缓存操作中")
        }
    }

    val themeModeChanges = createConditionFlow { it.currentThemeMode != "auto" }
    val actualThemeChanges = mapState { it.actualTheme }
    val followSystemThemeChanges = createConditionFlow { it.isFollowSystemTheme }
    val autoNightModeChanges = createConditionFlow { it.isAutoNightModeEnabled }
    val cacheOperationStatus = createConditionFlow { isCacheOperationInProgress() }
}

@Stable
data class SettingsScreenState(
    val isLoading: Boolean,
    val error: String?,
    val currentThemeMode: String,
    val actualTheme: String,
    val themeDisplayName: String,
    val actualThemeDisplayName: String,
    val isFollowSystemTheme: Boolean,
    val isAutoNightModeEnabled: Boolean,
    val nightModeTimeRange: String,
    val cacheSize: String,
    val canToggleTheme: Boolean,
    val canPerformCacheOperation: Boolean,
    val isCacheOperationInProgress: Boolean,
    val settingsStatusSummary: String,
)

class SettingsStateListener(private val adapter: SettingsStateAdapter) {

    fun onThemeChanged(action: (String) -> Unit): Flow<String> {
        return adapter.actualTheme.map {
            action(it)
            it
        }
    }

    fun onThemeModeChanged(action: (String) -> Unit): Flow<String> {
        return adapter.currentThemeMode.map {
            action(it)
            it
        }
    }

    fun onFollowSystemThemeChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isFollowSystemTheme.map {
            action(it)
            it
        }
    }

    fun onAutoNightModeChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isAutoNightModeEnabled.map {
            action(it)
            it
        }
    }

    fun onCacheOperationStatusChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.cacheOperationStatus.map {
            action(it)
            it
        }
    }
}

fun SettingsStateAdapter.toScreenState(): SettingsScreenState {
    return SettingsScreenState(
        isLoading = isCurrentlyLoading(),
        error = getCurrentError(),
        currentThemeMode = getCurrentSnapshot().currentThemeMode,
        actualTheme = getCurrentSnapshot().actualTheme,
        themeDisplayName = getThemeDisplayName(),
        actualThemeDisplayName = getActualThemeDisplayName(),
        isFollowSystemTheme = getCurrentSnapshot().isFollowSystemTheme,
        isAutoNightModeEnabled = getCurrentSnapshot().isAutoNightModeEnabled,
        nightModeTimeRange = getNightModeTimeRange(),
        cacheSize = getCurrentSnapshot().cacheSize,
        canToggleTheme = canToggleTheme(),
        canPerformCacheOperation = canPerformCacheOperation(),
        isCacheOperationInProgress = isCacheOperationInProgress(),
        settingsStatusSummary = getSettingsStatusSummary(),
    )
}

fun SettingsStateAdapter.createSettingsListener(): SettingsStateListener = SettingsStateListener(this)
