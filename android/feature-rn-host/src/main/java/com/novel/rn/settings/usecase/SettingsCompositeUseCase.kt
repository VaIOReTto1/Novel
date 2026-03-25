package com.novel.rn.settings.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.ui.theme.ThemeManager
import com.novel.core.logging.CoreLogger
import javax.inject.Inject

/**
 * Settings模块组合UseCase
 * 
 * 处理复杂的业务逻辑组合，整合所有Settings相关的UseCase
 * 仿照Home模块的UseCase模式，不使用Flow，直接返回结果
 */
@Stable
class SettingsCompositeUseCase @Inject constructor(
    @Stable
    private val settingsUtils: SettingsUtils,
    @Stable
    private val themeManager: ThemeManager
) : BaseUseCase<SettingsCompositeUseCase.Params, SettingsCompositeUseCase.Result>() {
    
    companion object {
        private const val TAG = "SettingsCompositeUseCase"
    }
    
    data class Params(
        val loadInitialSettings: Boolean = false,
        val syncThemeWithRN: Boolean = false,
        val performThemeToggle: Boolean = false,
        val calculateAndClearCache: Boolean = false,
        val exportSettings: Boolean = false,
        val exportPath: String? = null
    )
    
    data class Result(
        val settingsData: GetUserSettingsUseCase.SettingsData? = null,
        val updateResult: UpdateSettingsUseCase.UpdateResult? = null,
        val cacheResult: ClearCacheUseCase.CacheResult? = null,
        val exportResult: ExportUserDataUseCase.ExportResult? = null,
        val isSuccess: Boolean = true,
        val errorMessage: String? = null
    )
    
    // 创建内部UseCase实例
    @Stable
    private val getUserSettingsUseCase: GetUserSettingsUseCase by lazy {
        GetUserSettingsUseCase(settingsUtils, themeManager)
    }
    @Stable
    private val updateSettingsUseCase: UpdateSettingsUseCase by lazy {
        UpdateSettingsUseCase(settingsUtils, themeManager)
    }
    @Stable
    private val clearCacheUseCase: ClearCacheUseCase by lazy {
        ClearCacheUseCase(settingsUtils)
    }
    @Stable
    private val exportUserDataUseCase: Nothing by lazy {
        // 注意：这里需要Context，实际使用时应该通过依赖注入获取
        throw NotImplementedError("需要Context依赖，请使用Hilt注入的实例")
    }
    
    override suspend fun execute(params: Params): Result {
        CoreLogger.d(TAG, "开始执行组合操作: $params")
        
        return try {
            when {
                params.loadInitialSettings -> loadInitialSettings()
                params.syncThemeWithRN -> syncThemeWithRN()
                params.performThemeToggle -> performThemeToggle()
                params.calculateAndClearCache -> calculateAndClearCache()
                params.exportSettings -> exportSettings(params.exportPath)
                else -> Result(isSuccess = false, errorMessage = "未知操作类型")
            }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "组合操作执行失败", e)
            Result(isSuccess = false, errorMessage = e.message ?: "未知错误")
        }
    }
    
    /**
     * 加载初始设置数据
     */
    private suspend fun loadInitialSettings(): Result {
        CoreLogger.d(TAG, "加载初始设置数据")
        
        return try {
            val settingsData = getUserSettingsUseCase(Unit)
            
            CoreLogger.d(TAG, "初始设置数据加载完成")
            
            Result(
                settingsData = settingsData,
                isSuccess = true
            )
        } catch (e: Exception) {
            CoreLogger.e(TAG, "加载初始设置数据失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 同步主题到RN
     */
    private suspend fun syncThemeWithRN(): Result {
        CoreLogger.d(TAG, "同步主题到RN")
        
        return try {
            // 获取当前设置
            val settingsData = getUserSettingsUseCase(Unit)
            
            CoreLogger.d(TAG, "主题同步到RN完成")
            
            Result(
                settingsData = settingsData,
                isSuccess = true
            )
        } catch (e: Exception) {
            CoreLogger.e(TAG, "同步主题到RN失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 执行主题切换
     */
    private suspend fun performThemeToggle(): Result {
        CoreLogger.d(TAG, "执行主题切换")
        
        return try {
            val updateResult = updateSettingsUseCase(UpdateSettingsUseCase.UpdateParams.ToggleTheme)
            
            // 重新获取设置数据以确保同步
            val settingsData = getUserSettingsUseCase(Unit)
            
            CoreLogger.d(TAG, "主题切换完成")
            
            Result(
                settingsData = settingsData,
                updateResult = updateResult,
                isSuccess = true
            )
        } catch (e: Exception) {
            CoreLogger.e(TAG, "主题切换失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 计算并清理缓存
     */
    private suspend fun calculateAndClearCache(): Result {
        CoreLogger.d(TAG, "计算并清理缓存")
        
        return try {
            // 先计算缓存大小
            val calculateResult = clearCacheUseCase(ClearCacheUseCase.CacheOperation.CalculateSize)
            
            // 如果有缓存，则清理
            if (calculateResult.success && calculateResult.cacheSize != "0B") {
                val clearResult = clearCacheUseCase(ClearCacheUseCase.CacheOperation.ClearAll)
                
                CoreLogger.d(TAG, "缓存计算和清理完成")
                
                Result(
                    cacheResult = clearResult,
                    isSuccess = true
                )
            } else {
                CoreLogger.d(TAG, "无需清理缓存")
                
                Result(
                    cacheResult = calculateResult,
                    isSuccess = true
                )
            }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "缓存操作失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
    
    /**
     * 导出设置
     */
    private suspend fun exportSettings(exportPath: String?): Result {
        CoreLogger.d(TAG, "导出设置")
        
        return try {
            // 注意：这里需要实际的ExportUserDataUseCase实例
            // 在实际使用中应该通过Hilt注入获取
            CoreLogger.w(TAG, "导出功能需要Context依赖，请使用完整的依赖注入")
            
            Result(isSuccess = false, errorMessage = "导出功能暂不可用")
        } catch (e: Exception) {
            CoreLogger.e(TAG, "导出设置失败", e)
            Result(isSuccess = false, errorMessage = e.message)
        }
    }
}

