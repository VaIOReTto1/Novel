package com.novel.page.login.usecase

import androidx.compose.runtime.Stable
import com.novel.core.domain.BaseUseCase
import com.novel.page.login.utils.AuthResult
import com.novel.page.login.utils.AuthService
import com.novel.utils.TimberLogger
import javax.inject.Inject

/**
 * 登录UseCase
 * 
 * 封装登录业务逻辑，提供统一的登录接口
 * 基于现有AuthService重构，保持业务逻辑不变
 */
@Stable
class LoginUseCase @Inject constructor(
    private val authService: AuthService
) : BaseUseCase<LoginUseCase.Params, LoginUseCase.Result>() {
    
    companion object {
        private const val TAG = "LoginUseCase"
    }
    
    data class Params(
        val username: String,
        val password: String
    )
    
    sealed class Result {
        data class Success(val message: String) : Result()
        data class Error(val message: String) : Result()
    }
    
    override suspend fun execute(params: Params): Result {
        TimberLogger.d(TAG, "执行登录，用户名: ${params.username}")
        
        return try {
            when (val authResult = authService.login(params.username, params.password)) {
                is AuthResult.Success -> {
                    TimberLogger.d(TAG, "登录成功: ${authResult.message}")
                    Result.Success(authResult.message)
                }
                is AuthResult.Error -> {
                    TimberLogger.w(TAG, "登录失败: ${authResult.message}")
                    Result.Error(authResult.message)
                }
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "登录异常", e)
            Result.Error("登录失败：${e.localizedMessage}")
        }
    }
} 
