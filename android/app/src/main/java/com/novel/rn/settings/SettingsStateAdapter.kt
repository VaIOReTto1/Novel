package com.novel.rn.settings

import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/**
 * 设置模块状态适配器
 * 
 * 为Settings模块提供统一的状态适配功能：
 * - 细粒度状态订阅
 * - UI友好的便利方法
 * - 类型安全的状态访问
 */
@Stable
class SettingsStateAdapter(
    stateFlow: StateFlow<SettingsState>
) : StateAdapter<SettingsState>(stateFlow) {
    
    // region 基础状态适配
    
    /** 当前主题模式 */
    val currentThemeMode = mapState { it.currentThemeMode }
    
    /** 实际主题 */
    val actualTheme = mapState { it.actualTheme }
    
    /** 是否跟随系统主题 */
    val isFollowSystemTheme = mapState { it.isFollowSystemTheme }
    
    /** 是否启用自动夜间模式 */
    val isAutoNightModeEnabled = mapState { it.isAutoNightModeEnabled }
    
    /** 夜间模式开始时间 */
    val nightModeStartTime = mapState { it.nightModeStartTime }
    
    /** 夜间模式结束时间 */
    val nightModeEndTime = mapState { it.nightModeEndTime }
    
    /** 缓存大小 */
    val cacheSize = mapState { it.cacheSize }
    
    /** 是否正在计算缓存 */
    val isCacheCalculating = mapState { it.isCacheCalculating }
    
    /** 是否正在清理缓存 */
    val isCacheClearing = mapState { it.isCacheClearing }
    
    // endregion
    
    // region 状态查询方法
    
    /** 检查是否为深色主题 */
    fun isDarkTheme(): Boolean {
        return getCurrentSnapshot().actualTheme == "dark"
    }
    
    /** 检查是否为浅色主题 */
    fun isLightTheme(): Boolean {
        return getCurrentSnapshot().actualTheme == "light"
    }
    
    /** 检查是否可以切换主题 */
    fun canToggleTheme(): Boolean {
        return !isCurrentlyLoading()
    }
    
    /** 检查是否可以执行缓存操作 */
    fun canPerformCacheOperation(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isCacheCalculating && !state.isCacheClearing && !state.isLoading
    }
    
    /** 检查缓存操作是否进行中 */
    fun isCacheOperationInProgress(): Boolean {
        val state = getCurrentSnapshot()
        return state.isCacheCalculating || state.isCacheClearing
    }
    
    /** 获取主题显示名称 */
    fun getThemeDisplayName(): String {
        val state = getCurrentSnapshot()
        return when (state.currentThemeMode) {
            "light" -> "浅色"
            "dark" -> "深色"
            "auto" -> "跟随系统"
            else -> "未知"
        }
    }
    
    /** 获取实际主题显示名称 */
    fun getActualThemeDisplayName(): String {
        return if (isDarkTheme()) "深色" else "浅色"
    }
    
    /** 获取夜间模式时间段显示 */
    fun getNightModeTimeRange(): String {
        val state = getCurrentSnapshot()
        return "${state.nightModeStartTime} - ${state.nightModeEndTime}"
    }
    
    /** 获取设置状态摘要 */
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
    
    // endregion
    
    // region 条件流创建
    
    /** 监听主题模式变化 */
    val themeModeChanges = createConditionFlow { it.currentThemeMode != "auto" }
    
    /** 监听实际主题变化 */
    val actualThemeChanges = mapState { it.actualTheme }
    
    /** 监听跟随系统主题状态变化 */
    val followSystemThemeChanges = createConditionFlow { it.isFollowSystemTheme }
    
    /** 监听自动夜间模式状态变化 */
    val autoNightModeChanges = createConditionFlow { it.isAutoNightModeEnabled }
    
    /** 监听缓存操作状态变化 */
    val cacheOperationStatus = createConditionFlow { isCacheOperationInProgress() }
    
    // endregion
}

/**
 * 状态组合器 - 设置页面专用
 */
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
    val settingsStatusSummary: String
)

/**
 * 状态监听器 - 设置页面专用
 */
class SettingsStateListener(private val adapter: SettingsStateAdapter) {
    
    /** 监听主题变化 */
    fun onThemeChanged(action: (String) -> Unit): Flow<String> {
        return adapter.actualTheme.map { theme ->
            action(theme)
            theme
        }
    }
    
    /** 监听主题模式变化 */
    fun onThemeModeChanged(action: (String) -> Unit): Flow<String> {
        return adapter.currentThemeMode.map { mode ->
            action(mode)
            mode
        }
    }
    
    /** 监听跟随系统主题状态变化 */
    fun onFollowSystemThemeChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isFollowSystemTheme.map { follow ->
            action(follow)
            follow
        }
    }
    
    /** 监听自动夜间模式状态变化 */
    fun onAutoNightModeChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.isAutoNightModeEnabled.map { enabled ->
            action(enabled)
            enabled
        }
    }
    
    /** 监听缓存操作状态变化 */
    fun onCacheOperationStatusChanged(action: (Boolean) -> Unit): Flow<Boolean> {
        return adapter.cacheOperationStatus.map { inProgress ->
            action(inProgress)
            inProgress
        }
    }
}

/**
 * 扩展函数：为SettingsStateAdapter创建状态组合器
 */
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
        settingsStatusSummary = getSettingsStatusSummary()
    )
}

/**
 * 扩展函数：为SettingsStateAdapter创建状态监听器
 */
fun SettingsStateAdapter.createSettingsListener(): SettingsStateListener {
    return SettingsStateListener(this)
} 
