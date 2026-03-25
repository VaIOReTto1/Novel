package com.novel.rn.settings.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.rn.settings.SettingsUtils
import com.novel.core.logging.CoreLogger
import javax.inject.Inject

/**
 * 清理缓存UseCase
 * 
 * 功能：
 * - 清理所有应用缓存
 * - 计算缓存大小
 * - 缓存操作统计
 */
class ClearCacheUseCase @Inject constructor(
    private val settingsUtils: SettingsUtils
) : BaseUseCase<ClearCacheUseCase.CacheOperation, ClearCacheUseCase.CacheResult>() {

    companion object {
        private const val TAG = "ClearCacheUseCase"
    }

    sealed class CacheOperation {
        object ClearAll : CacheOperation()
        object CalculateSize : CacheOperation()
    }

    data class CacheResult(
        val message: String,
        val cacheSize: String? = null,
        val success: Boolean = true
    )

    override suspend fun execute(parameters: CacheOperation): CacheResult {
        CoreLogger.d(TAG, "开始缓存操作: ${parameters::class.simpleName}")
        
        try {
            when (parameters) {
                is CacheOperation.ClearAll -> {
                    val clearResult = settingsUtils.clearAllCache()
                    
                    val result = CacheResult(
                        message = clearResult,
                        success = true
                    )
                    
                    CoreLogger.d(TAG, "缓存清理完成: $clearResult")
                    return result
                }
                
                is CacheOperation.CalculateSize -> {
                    val size = settingsUtils.calculateCacheSize()
                    val formattedSize = settingsUtils.formatCacheSize(size)
                    
                    val result = CacheResult(
                        message = "缓存大小计算完成",
                        cacheSize = formattedSize,
                        success = true
                    )
                    
                    CoreLogger.d(TAG, "缓存大小计算完成: $formattedSize")
                    return result
                }
            }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "缓存操作失败: ${parameters::class.simpleName}", e)
            
            // 返回业务错误而不是异常
            return CacheResult(
                message = "缓存操作失败: ${e.message}",
                success = false
            )
        }
    }
} 

