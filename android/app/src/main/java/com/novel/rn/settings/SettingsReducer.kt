package com.novel.rn.settings

import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.rn.settings.SettingsEffect.ShowToast
import com.novel.utils.TimberLogger

/**
 * 设置模块状态处理器
 * 
 * 负责处理所有设置相关的状态转换：
 * - 主题模式变更
 * - 缓存状态更新
 * - 加载状态管理
 * - 错误状态处理
 */
class SettingsReducer : MviReducerWithEffect<SettingsIntent, SettingsState, SettingsEffect> {
    
    companion object {
        private const val TAG = "SettingsReducer"
    }
    
    override fun reduce(
        currentState: SettingsState,
        intent: SettingsIntent
    ): ReduceResult<SettingsState, SettingsEffect> {
        
        TimberLogger.d(TAG, "处理设置意图: ${intent::class.simpleName}")
        
        return when (intent) {
            is SettingsIntent.LoadCurrentTheme -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }
            
            is SettingsIntent.ToggleNightMode -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }
            
            is SettingsIntent.SetNightMode -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        currentThemeMode = intent.mode,
                        isLoading = false
                    ),
                    effect = ShowToast("主题已切换到: ${intent.mode}")
                )
            }
            
            is SettingsIntent.SetFollowSystemTheme -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isFollowSystemTheme = intent.follow
                    ),
                    effect = ShowToast("跟随系统主题已设置为: ${intent.follow}")
                )
            }
            
            is SettingsIntent.SetAutoNightMode -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isAutoNightModeEnabled = intent.enabled
                    ),
                    effect = ShowToast("自动切换夜间模式已设置为: ${intent.enabled}")
                )
            }
            
            is SettingsIntent.SetNightModeTime -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        nightModeStartTime = intent.startTime,
                        nightModeEndTime = intent.endTime
                    ),
                    effect = ShowToast("夜间模式时间已设置为: ${intent.startTime} - ${intent.endTime}")
                )
            }
            
            is SettingsIntent.CalculateCacheSize -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheCalculating = true,
                        cacheSize = "计算中..."
                    )
                )
            }
            
            is SettingsIntent.ClearAllCache -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheClearing = true
                    )
                )
            }
            
            is SettingsIntent.CheckCurrentTimeTheme -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }
            
            is SettingsIntent.NavigateToTimedSwitch -> {
                ReduceResult(
                    newState = currentState,
                    effect = SettingsEffect.NavigateToTimedSwitch
                )
            }
            
            is SettingsIntent.NavigateToHelpSupport -> {
                ReduceResult(
                    newState = currentState,
                    effect = SettingsEffect.NavigateToHelpSupport
                )
            }
            
            is SettingsIntent.NavigateToPrivacyPolicy -> {
                ReduceResult(
                    newState = currentState,
                    effect = SettingsEffect.NavigateToPrivacyPolicy
                )
            }
            
            is SettingsIntent.Logout -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }

            SettingsIntent.ConfirmLogout -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true
                    )
                )
            }
        }
    }
    
    /**
     * 处理异步操作完成后的状态更新
     */
    fun handleAsyncResult(
        currentState: SettingsState,
        result: SettingsAsyncResult
    ): ReduceResult<SettingsState, SettingsEffect> {
        
        return when (result) {
            is SettingsAsyncResult.ThemeLoaded -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        currentThemeMode = result.mode,
                        actualTheme = result.actualTheme,
                        isFollowSystemTheme = result.followSystem,
                        isAutoNightModeEnabled = result.autoEnabled,
                        nightModeStartTime = result.startTime,
                        nightModeEndTime = result.endTime
                    )
                )
            }
            
            is SettingsAsyncResult.CacheSizeCalculated -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheCalculating = false,
                        cacheSize = result.size
                    ),
                    effect = SettingsEffect.CacheCalculated(result.size)
                )
            }
            
            is SettingsAsyncResult.CacheCleared -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isCacheClearing = false
                    ),
                    effect = SettingsEffect.CacheCleared(result.message)
                )
            }
            
            is SettingsAsyncResult.ThemeChanged -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        actualTheme = result.actualTheme
                    ),
                    effect = SettingsEffect.NotifyThemeChanged(result.actualTheme)
                )
            }
            
            is SettingsAsyncResult.Error -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        isCacheCalculating = false,
                        isCacheClearing = false,
                        error = result.message
                    ),
                    effect = SettingsEffect.ShowError(result.message)
                )
            }
            
            is SettingsAsyncResult.LogoutSuccess -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false
                    ),
                    effect = SettingsEffect.LogoutSuccess(result.message)
                )
            }
            
            is SettingsAsyncResult.LogoutError -> {
                ReduceResult(
                    newState = currentState.copy(
                        version = currentState.version + 1,
                        isLoading = false,
                        error = result.message
                    ),
                    effect = SettingsEffect.LogoutError(result.message)
                )
            }
        }
    }
}

/**
 * 异步操作结果封装
 */
sealed class SettingsAsyncResult {
    data class ThemeLoaded(
        val mode: String,
        val actualTheme: String,
        val followSystem: Boolean,
        val autoEnabled: Boolean,
        val startTime: String,
        val endTime: String
    ) : SettingsAsyncResult()
    
    data class CacheSizeCalculated(val size: String) : SettingsAsyncResult()
    data class CacheCleared(val message: String) : SettingsAsyncResult()
    data class ThemeChanged(val actualTheme: String) : SettingsAsyncResult()
    data class LogoutSuccess(val message: String) : SettingsAsyncResult()
    data class LogoutError(val message: String) : SettingsAsyncResult()
    data class Error(val message: String) : SettingsAsyncResult()
}
