package com.novel.page.login.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.page.login.utils.CaptchaService
import com.novel.utils.TimberLogger
import javax.inject.Inject

/**
 * 验证码管理UseCase
 * 
 * 封装验证码相关业务逻辑，包括加载、刷新、清理等
 * 基于现有CaptchaService重构，保持业务逻辑不变
 */
@Stable
class CaptchaUseCase @Inject constructor(
    private val captchaService: CaptchaService
) : BaseUseCase<Unit, CaptchaUseCase.Result>() {
    
    companion object {
        private const val TAG = "CaptchaUseCase"
    }
    
    sealed class Result {
        data class Success(
            val imagePath: String,
            val sessionId: String
        ) : Result()
        data class Error(val message: String) : Result()
    }
    
    override suspend fun execute(params: Unit): Result {
        TimberLogger.d(TAG, "加载验证码")
        
        return try {
            val success = captchaService.loadCaptcha()
            if (success) {
                val captchaInfo = captchaService.getCurrentCaptchaInfo()
                if (captchaInfo != null) {
                    TimberLogger.d(TAG, "验证码加载成功")
                    Result.Success(
                        imagePath = captchaInfo.imagePath,
                        sessionId = captchaInfo.sessionId
                    )
                } else {
                    TimberLogger.w(TAG, "验证码信息为空")
                    Result.Error("验证码加载失败")
                }
            } else {
                TimberLogger.w(TAG, "验证码加载失败")
                Result.Error("验证码加载失败")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "验证码加载异常", e)
            Result.Error("验证码加载失败：${e.localizedMessage}")
        }
    }
    
    /**
     * 刷新验证码
     */
    suspend fun refreshCaptcha(): Result {
        TimberLogger.d(TAG, "刷新验证码")
        
        return try {
            val success = captchaService.refreshCaptcha()
            if (success) {
                val captchaInfo = captchaService.getCurrentCaptchaInfo()
                if (captchaInfo != null) {
                    TimberLogger.d(TAG, "验证码刷新成功")
                    Result.Success(
                        imagePath = captchaInfo.imagePath,
                        sessionId = captchaInfo.sessionId
                    )
                } else {
                    TimberLogger.w(TAG, "刷新后验证码信息为空")
                    Result.Error("验证码刷新失败")
                }
            } else {
                TimberLogger.w(TAG, "验证码刷新失败")
                Result.Error("验证码刷新失败")
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "验证码刷新异常", e)
            Result.Error("验证码刷新失败：${e.localizedMessage}")
        }
    }
    
    /**
     * 清理验证码缓存
     */
    suspend fun clearCache() {
        TimberLogger.d(TAG, "清理验证码缓存")
        
        try {
            captchaService.clearAllCaptchaFiles()
            TimberLogger.d(TAG, "验证码缓存清理完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "验证码缓存清理失败", e)
        }
    }
} 
