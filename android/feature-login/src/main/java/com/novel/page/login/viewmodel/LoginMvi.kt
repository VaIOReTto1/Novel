package com.novel.page.login.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.core.mvi.MviEffect

/**
 * Login模块MVI契约定义
 * 
 * 基于现有LoginAction、LoginUiState、LoginEvent重构为统一MVI框架
 * 保持所有原有功能和行为不变
 */

// ===== Intent定义 =====

/**
 * Login模块意图定义
 * 基于现有LoginAction重构，整合所有用户交互
 */
sealed class LoginIntent : MviIntent {
    /** 输入手机号 */
    data class InputPhone(val phone: String) : LoginIntent()
    
    /** 输入密码 */
    data class InputPassword(val password: String) : LoginIntent()
    
    /** 输入确认密码 */
    data class InputPasswordConfirm(val passwordConfirm: String) : LoginIntent()
    
    /** 输入验证码 */
    data class InputVerifyCode(val code: String) : LoginIntent()
    
    /** 切换协议同意状态 */
    data class ToggleAgreement(val accepted: Boolean) : LoginIntent()
    
    /** 切换登录/注册模式 */
    object SwitchToRegister : LoginIntent()
    object SwitchToLogin : LoginIntent()
    
    /** 执行登录 */
    object SubmitLogin : LoginIntent()
    
    /** 执行注册 */
    object SubmitRegister : LoginIntent()
    
    /** 刷新验证码 */
    object RefreshCaptcha : LoginIntent()
    
    /** 导航相关 */
    object NavigateToPrivacyPolicy : LoginIntent()
    object NavigateToTermsOfService : LoginIntent()
    object NavigateToTelService : LoginIntent()
    object NavigateBack : LoginIntent()
    
    /** 清除错误状态 */
    object ClearError : LoginIntent()
    
    /** 初始化页面数据 */
    object InitializePage : LoginIntent()
}

// ===== State定义 =====

/**
 * Login模块状态定义
 * 基于现有LoginUiState重构，继承MviState接口
 */
@Stable
data class LoginState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    
    // 模式控制
    val isLoginMode: Boolean = true,
    
    // 表单数据
    val loginForm: LoginForm = LoginForm(),
    val registerForm: RegisterForm = RegisterForm(),
    
    // 验证结果
    val validationResults: ValidationResults = ValidationResults(),
    
    // 验证码状态
    val captchaState: CaptchaState = CaptchaState(),
    
    // 页面状态
    val isSubmitting: Boolean = false,
    val submitError: String? = null,
    val isAgreementAccepted: Boolean = false,
    
    // 手机和运营商信息
    val phoneInfo: PhoneInfo = PhoneInfo()
) : MviState {
    
    override val isEmpty: Boolean
        get() = loginForm.phone.isBlank() && registerForm.phone.isBlank()
    
    override val isSuccess: Boolean
        get() = !isLoading && !hasError && !isEmpty && !isSubmitting
    
    /**
     * 当前激活的表单
     */
    val activeForm: FormData
        get() = if (isLoginMode) {
            FormData(
                phone = loginForm.phone,
                password = loginForm.password,
                passwordConfirm = "",
                verifyCode = ""
            )
        } else {
            FormData(
                phone = registerForm.phone,
                password = registerForm.password,
                passwordConfirm = registerForm.passwordConfirm,
                verifyCode = registerForm.verifyCode
            )
        }
    
    /**
     * 提交按钮是否可用
     */
    val isSubmitEnabled: Boolean
        get() = isAgreementAccepted && 
                !isSubmitting && 
                !isLoading &&
                if (isLoginMode) {
                    loginForm.isValid
                } else {
                    registerForm.isValid && captchaState.hasValidCaptcha
                }
    
    /**
     * 获取当前模式的按钮文字
     */
    val submitButtonText: String
        get() = if (isLoginMode) "登录" else "注册"
    
    /**
     * 获取模式切换按钮文字
     */
    val switchModeButtonText: String
        get() = if (isLoginMode) "暂无账号，去注册" else "返回登录"
}

/**
 * 登录表单数据
 */
@Stable
data class LoginForm(
    val phone: String = "",
    val password: String = ""
) {
    val isValid: Boolean
        get() = phone.isNotBlank() && password.isNotBlank()
}

/**
 * 注册表单数据
 */
@Stable
data class RegisterForm(
    val phone: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
    val verifyCode: String = ""
) {
    val isValid: Boolean
        get() = phone.isNotBlank() && 
                password.isNotBlank() && 
                passwordConfirm.isNotBlank() && 
                verifyCode.isNotBlank()
}

/**
 * 验证结果
 */
@Stable
data class ValidationResults(
    val phoneError: String? = null,
    val passwordError: String? = null,
    val passwordConfirmError: String? = null,
    val verifyCodeError: String? = null
) {
    /** 是否有错误 */
    val hasErrors: Boolean
        get() = phoneError != null || passwordError != null || passwordConfirmError != null || verifyCodeError != null
    
    /** 是否验证通过 */
    val isValid: Boolean
        get() = !hasErrors
}

/**
 * 验证码状态
 */
@Stable
data class CaptchaState(
    val imagePath: String = "",
    val sessionId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val hasValidCaptcha: Boolean
        get() = imagePath.isNotEmpty() && sessionId.isNotEmpty() && error == null
}

/**
 * 手机信息
 */
@Stable
data class PhoneInfo(
    val phoneNumber: String = "",
    val operatorName: String = ""
) {
    val maskedPhoneNumber: String
        get() = if (phoneNumber.length >= 11) {
            phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7)
        } else phoneNumber
}

/**
 * 通用表单数据
 */
@Stable
data class FormData(
    val phone: String,
    val password: String,
    val passwordConfirm: String,
    val verifyCode: String
)

// ===== Effect定义 =====

/**
 * Login模块副作用定义
 * 基于现有LoginEvent重构，处理一次性副作用
 */
sealed class LoginEffect : MviEffect {
    /** 导航到主页 */
    object NavigateToHome : LoginEffect()
    
    /** 导航到隐私政策页面 */
    object NavigateToPrivacyPolicy : LoginEffect()
    
    /** 导航到服务条款页面 */
    object NavigateToTermsOfService : LoginEffect()
    
    /** 返回上一页 */
    object NavigateBack : LoginEffect()
    
    /** 显示Toast消息 */
    data class ShowToast(val message: String) : LoginEffect()
    
    /** 触发触觉反馈 */
    object TriggerHapticFeedback : LoginEffect()
    
    /** 显示验证码发送成功对话框 */
    data class ShowSmsCodeSentDialog(val maskedPhone: String) : LoginEffect()
    
    /** 聚焦到验证码输入框 */
    object FocusSmsCodeInput : LoginEffect()
    
    /** 启动电话服务 */
    data class LaunchTelService(val phoneNumber: String) : LoginEffect()
} 
