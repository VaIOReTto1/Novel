package com.novel.page.login.viewmodel

import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.core.logging.CoreLogger

/**
 * Login模块Reducer实现
 * 
 * 处理复杂的表单状态机转换，包括：
 * - 表单输入状态更新
 * - 模式切换（登录/注册）
 * - 验证状态管理
 * - 提交流程控制
 * - 副作用触发
 */
class LoginReducer : MviReducerWithEffect<LoginIntent, LoginState, LoginEffect> {
    
    companion object {
        private const val TAG = "LoginReducer"
    }
    
    override fun reduce(currentState: LoginState, intent: LoginIntent): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")
        
        return when (intent) {
            is LoginIntent.InputPhone -> handleInputPhone(currentState, intent)
            is LoginIntent.InputPassword -> handleInputPassword(currentState, intent)
            is LoginIntent.InputPasswordConfirm -> handleInputPasswordConfirm(currentState, intent)
            is LoginIntent.InputVerifyCode -> handleInputVerifyCode(currentState, intent)
            is LoginIntent.ToggleAgreement -> handleToggleAgreement(currentState, intent)
            is LoginIntent.SwitchToLogin -> handleSwitchToLogin(currentState)
            is LoginIntent.SwitchToRegister -> handleSwitchToRegister(currentState)
            is LoginIntent.SubmitLogin -> handleSubmitLogin(currentState)
            is LoginIntent.SubmitRegister -> handleSubmitRegister(currentState)
            is LoginIntent.RefreshCaptcha -> handleRefreshCaptcha(currentState)
            is LoginIntent.NavigateToTelService -> handleNavigateToTelService(currentState)
            is LoginIntent.NavigateToPrivacyPolicy -> handleNavigateToPrivacyPolicy(currentState)
            is LoginIntent.NavigateToTermsOfService -> handleNavigateToTermsOfService(currentState)
            is LoginIntent.InitializePage -> handleInitializePage(currentState)
            is LoginIntent.ClearError -> handleClearError(currentState)
            is LoginIntent.NavigateBack -> handleNavigateBack(currentState)
        }
    }
    
    /**
     * 处理手机号输入
     */
    private fun handleInputPhone(currentState: LoginState, intent: LoginIntent.InputPhone): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理手机号输入: ${intent.phone}")
        
        val newState = if (currentState.isLoginMode) {
            currentState.copy(
                version = currentState.version + 1,
                loginForm = currentState.loginForm.copy(phone = intent.phone),
                validationResults = currentState.validationResults.copy(phoneError = null)
            )
        } else {
            currentState.copy(
                version = currentState.version + 1,
                registerForm = currentState.registerForm.copy(phone = intent.phone),
                validationResults = currentState.validationResults.copy(phoneError = null)
            )
        }
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理密码输入
     */
    private fun handleInputPassword(currentState: LoginState, intent: LoginIntent.InputPassword): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理密码输入")
        
        val newState = if (currentState.isLoginMode) {
            currentState.copy(
                version = currentState.version + 1,
                loginForm = currentState.loginForm.copy(password = intent.password),
                validationResults = currentState.validationResults.copy(passwordError = null)
            )
        } else {
            currentState.copy(
                version = currentState.version + 1,
                registerForm = currentState.registerForm.copy(password = intent.password),
                validationResults = currentState.validationResults.copy(passwordError = null)
            )
        }
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理确认密码输入
     */
    private fun handleInputPasswordConfirm(currentState: LoginState, intent: LoginIntent.InputPasswordConfirm): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理确认密码输入")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            registerForm = currentState.registerForm.copy(passwordConfirm = intent.passwordConfirm),
            validationResults = currentState.validationResults.copy(passwordConfirmError = null)
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理验证码输入
     */
    private fun handleInputVerifyCode(currentState: LoginState, intent: LoginIntent.InputVerifyCode): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理验证码输入")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            registerForm = currentState.registerForm.copy(verifyCode = intent.code),
            validationResults = currentState.validationResults.copy(verifyCodeError = null)
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理协议同意状态切换
     */
    private fun handleToggleAgreement(currentState: LoginState, intent: LoginIntent.ToggleAgreement): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理协议同意状态切换: ${intent.accepted}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isAgreementAccepted = intent.accepted
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理切换到登录模式
     */
    private fun handleSwitchToLogin(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "切换到登录模式")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoginMode = true,
            validationResults = ValidationResults(), // 清空验证结果
            submitError = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理切换到注册模式
     */
    private fun handleSwitchToRegister(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "切换到注册模式")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoginMode = false,
            validationResults = ValidationResults(), // 清空验证结果
            submitError = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理登录提交
     */
    private fun handleSubmitLogin(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "开始登录提交")
        
        // 检查表单有效性
        if (!currentState.isSubmitEnabled) {
            CoreLogger.w(TAG, "登录表单无效，提交被拒绝")
            return ReduceResult(currentState, LoginEffect.ShowToast("请完善登录信息"))
        }
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSubmitting = true,
            submitError = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理注册提交
     */
    private fun handleSubmitRegister(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "开始注册提交")
        
        // 检查表单有效性
        if (!currentState.isSubmitEnabled) {
            CoreLogger.w(TAG, "注册表单无效，提交被拒绝")
            return ReduceResult(currentState, LoginEffect.ShowToast("请完善注册信息"))
        }
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSubmitting = true,
            submitError = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理验证码刷新
     */
    private fun handleRefreshCaptcha(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "刷新验证码")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            captchaState = currentState.captchaState.copy(isLoading = true, error = null)
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理导航到电话服务
     */
    private fun handleNavigateToTelService(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "导航到电话服务")
        
        val phoneNumber = getOperatorServiceNumber(currentState.phoneInfo.operatorName)
        val effect = LoginEffect.LaunchTelService(phoneNumber)
        return ReduceResult(currentState, effect)
    }
    
    /**
     * 处理导航到隐私政策
     */
    private fun handleNavigateToPrivacyPolicy(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "导航到隐私政策")
        
        val effect = LoginEffect.NavigateToPrivacyPolicy
        return ReduceResult(currentState, effect)
    }
    
    /**
     * 处理导航到服务条款
     */
    private fun handleNavigateToTermsOfService(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "导航到服务条款")
        
        val effect = LoginEffect.NavigateToTermsOfService
        return ReduceResult(currentState, effect)
    }
    
    /**
     * 处理页面初始化
     */
    private fun handleInitializePage(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "初始化页面")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoading = true,
            error = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理清除错误
     */
    private fun handleClearError(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "清除错误")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            error = null,
            submitError = null
        )
        
        return ReduceResult(newState)
    }
    
    /**
     * 处理返回导航
     */
    private fun handleNavigateBack(currentState: LoginState): ReduceResult<LoginState, LoginEffect> {
        CoreLogger.d(TAG, "处理返回导航")
        
        val effect = LoginEffect.NavigateBack
        return ReduceResult(currentState, effect)
    }
    
    /**
     * 获取运营商客服电话
     */
    private fun getOperatorServiceNumber(operatorName: String): String {
        return when (operatorName) {
            "移动" -> "10086"
            "联通" -> "10010"
            "电信" -> "10000"
            else -> "10000"
        }
    }
} 
