package com.novel.page.login.usecase

import com.novel.core.domain.BaseUseCase
import com.novel.page.login.utils.LoginForm
import com.novel.page.login.utils.RegisterForm
import com.novel.page.login.utils.ValidationService
import com.novel.page.login.viewmodel.ValidationResults
import com.novel.utils.TimberLogger
import javax.inject.Inject

/**
 * 表单验证UseCase
 * 
 * 封装表单验证业务逻辑，支持登录和注册表单验证
 * 基于现有ValidationService重构，保持验证逻辑不变
 */
class ValidateFormUseCase @Inject constructor(
    private val validationService: ValidationService
) {
    
    companion object {
        private const val TAG = "ValidateFormUseCase"
    }
    
    /**
     * 验证登录表单
     */
    suspend fun validateLogin(phone: String, password: String): ValidationResults {
        TimberLogger.d(TAG, "验证登录表单")
        
        val form = LoginForm(phone = phone, password = password)
        val result = validationService.validateLoginForm(form)
        
        return ValidationResults(
            phoneError = (result.phoneError as? com.novel.page.login.utils.ValidationResult.Error)?.message,
            passwordError = (result.passwordError as? com.novel.page.login.utils.ValidationResult.Error)?.message
        )
    }
    
    /**
     * 验证注册表单
     */
    suspend fun validateRegister(
        phone: String,
        password: String,
        passwordConfirm: String,
        verifyCode: String
    ): ValidationResults {
        TimberLogger.d(TAG, "验证注册表单")
        
        val form = RegisterForm(
            phone = phone,
            password = password,
            passwordConfirm = passwordConfirm,
            verifyCode = verifyCode
        )
        val result = validationService.validateRegisterForm(form)
        
        return ValidationResults(
            phoneError = (result.phoneError as? com.novel.page.login.utils.ValidationResult.Error)?.message,
            passwordError = (result.passwordError as? com.novel.page.login.utils.ValidationResult.Error)?.message,
            passwordConfirmError = (result.passwordConfirmError as? com.novel.page.login.utils.ValidationResult.Error)?.message,
            verifyCodeError = (result.verifyCodeError as? com.novel.page.login.utils.ValidationResult.Error)?.message
        )
    }
} 
