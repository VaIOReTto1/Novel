package com.novel.page.login.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.page.login.utils.AuthResult
import com.novel.page.login.utils.AuthService
import com.novel.page.login.utils.RegisterRequest
import com.novel.utils.TimberLogger
import javax.inject.Inject

/**
 * 注册UseCase
 * 
 * 封装注册业务逻辑，提供统一的注册接口
 * 基于现有AuthService重构，保持业务逻辑不变
 */
@Stable
class RegisterUseCase @Inject constructor(
    private val authService: AuthService
) : BaseUseCase<RegisterUseCase.Params, RegisterUseCase.Result>() {
    
    companion object {
        private const val TAG = "RegisterUseCase"
    }
    
    data class Params(
        val username: String,
        val password: String,
        val sessionId: String,
        val verifyCode: String
    )
    
    sealed class Result {
        data class Success(val message: String) : Result()
        data class Error(val message: String) : Result()
    }
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "执行注册，用户名: ${params.username}")
        
        return try {
            val request = RegisterRequest(
                username = params.username,
                password = params.password,
                sessionId = params.sessionId,
                verifyCode = params.verifyCode
            )
            
            when (val authResult = authService.register(request)) {
                is AuthResult.Success -> {
                    TimberLogger.d(TAG, "注册成功: ${authResult.message}")
                    Result.Success(authResult.message)
                }
                is AuthResult.Error -> {
                    TimberLogger.w(TAG, "注册失败: ${authResult.message}")
                    Result.Error(authResult.message)
                }
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "注册异常", e)
            Result.Error("注册失败：${e.localizedMessage}")
        }
    }
} 
