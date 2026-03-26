package com.novel.rn.settings.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.ui.theme.ThemeManager
import com.novel.core.logging.CoreLogger
import javax.inject.Inject

/**
 * 更新设置UseCase
 * 
 * 功能：
 * - 主题模式设置更新
 * - 自动切换配置修改
 * - 时间设置更新
 * - 跟随系统主题设置
 */
class UpdateSettingsUseCase @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val themeManager: ThemeManager
) : BaseUseCase<UpdateSettingsUseCase.UpdateParams, UpdateSettingsUseCase.UpdateResult>() {

    companion object {
        private const val TAG = "UpdateSettingsUseCase"
    }

    sealed class UpdateParams {
        data class ThemeMode(val mode: String) : UpdateParams()
        data class FollowSystemTheme(val follow: Boolean) : UpdateParams()
        data class AutoNightMode(val enabled: Boolean) : UpdateParams()
        data class NightModeTime(val startTime: String, val endTime: String) : UpdateParams()
        object ToggleTheme : UpdateParams()
    }

    data class UpdateResult(
        val message: String,
        val newThemeMode: String? = null,
        val newActualTheme: String? = null
    )

    override suspend fun execute(parameters: UpdateParams): UpdateResult {
        CoreLogger.d(TAG, "开始更新设置: ${parameters::class.simpleName}")
        
        try {
            when (parameters) {
                is UpdateParams.ThemeMode -> {
                    settingsUtils.setNightMode(parameters.mode)
                    // 不发送RN事件，由SettingsViewModel统一发送
                    themeManager.setThemeMode(parameters.mode, notifyRN = false)
                    
                    val actualTheme = themeManager.getCurrentActualThemeMode()
                    val result = UpdateResult(
                        message = "主题已切换到: ${getThemeDisplayName(parameters.mode)}",
                        newThemeMode = parameters.mode,
                        newActualTheme = actualTheme
                    )
                    
                    CoreLogger.d(TAG, "主题模式更新成功: ${parameters.mode} -> $actualTheme")
                    return result
                }
                
                is UpdateParams.FollowSystemTheme -> {
                    settingsUtils.setFollowSystemTheme(parameters.follow)
                    
                    // 同步更新ThemeManager的状态
                    val newMode = if (parameters.follow) {
                        "auto"
                    } else {
                        // 如果关闭跟随系统，需要设置为当前实际主题
                        val currentActual = themeManager.getCurrentActualThemeMode()
                        currentActual
                    }
                    
                    // 重要：同步ThemeManager的状态，不发送RN事件，由SettingsViewModel统一发送
                    themeManager.setThemeMode(newMode, notifyRN = false)
                    
                    val actualTheme = themeManager.getCurrentActualThemeMode()
                    
                    // 始终返回完整的状态信息，确保RN端状态同步
                    val result = UpdateResult(
                        message = "跟随系统主题已${if (parameters.follow) "开启" else "关闭"}",
                        newThemeMode = newMode,
                        newActualTheme = actualTheme
                    )
                    
                    CoreLogger.d(TAG, "跟随系统主题更新成功: ${parameters.follow}, newMode: $newMode, actualTheme: $actualTheme")
                    return result
                }
                
                is UpdateParams.AutoNightMode -> {
                    settingsUtils.setAutoNightMode(parameters.enabled)
                    
                    val result = UpdateResult(
                        message = "自动切换夜间模式已${if (parameters.enabled) "开启" else "关闭"}"
                    )
                    
                    CoreLogger.d(TAG, "自动夜间模式更新成功: ${parameters.enabled}")
                    return result
                }
                
                is UpdateParams.NightModeTime -> {
                    settingsUtils.setNightModeTime(parameters.startTime, parameters.endTime)
                    
                    val result = UpdateResult(
                        message = "夜间模式时间已设置为: ${parameters.startTime} - ${parameters.endTime}"
                    )
                    
                    CoreLogger.d(TAG, "夜间模式时间更新成功: ${parameters.startTime} - ${parameters.endTime}")
                    return result
                }
                
                is UpdateParams.ToggleTheme -> {
                    val resultMessage = settingsUtils.toggleNightMode()
                    
                    // 确保ThemeManager状态已同步，获取最新的状态
                    val newThemeMode = themeManager.getCurrentThemeMode()
                    val newActualTheme = themeManager.getCurrentActualThemeMode()
                    
                    val result = UpdateResult(
                        message = resultMessage,
                        newThemeMode = newThemeMode,
                        newActualTheme = newActualTheme
                    )
                    
                    CoreLogger.d(TAG, "主题切换成功: $newThemeMode -> $newActualTheme (followSystem: ${themeManager.followSystemTheme.value})")
                    return result
                }
            }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "更新设置失败: ${parameters::class.simpleName}", e)
            throw e
        }
    }
    
    private fun getThemeDisplayName(mode: String): String {
        return when (mode) {
            "light" -> "浅色"
            "dark" -> "深色"
            "auto" -> "跟随系统"
            else -> "未知"
        }
    }
}

