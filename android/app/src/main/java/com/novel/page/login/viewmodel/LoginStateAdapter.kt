package com.novel.page.login.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

/**
 * Login模块状态适配器
 * 
 * 为Login模块提供统一的状态适配功能：
 * - 细粒度状态订阅
 * - UI友好的便利方法
 * - 类型安全的状态访问
 * - 优化的@Composable状态访问方法，提升skippable比例
 */
@Stable
class LoginStateAdapter(
    stateFlow: StateFlow<LoginState>
) : StateAdapter<LoginState>(stateFlow) {
    
    // region Composable 状态访问方法 (用于提升 skippable 比例)
    
    /**
     * 是否为登录模式 - 优化版本
     * 替代 isLoginMode.collectAsState() 以提升性能
     */
    @Composable
    fun isLoginModeState(): State<Boolean> = 
        createStableState { it.isLoginMode }

    /**
     * 是否为注册模式 - 优化版本
     */
    @Composable
    fun isRegisterModeState(): State<Boolean> = 
        createStableState { !it.isLoginMode }

    /**
     * 是否正在提交 - 优化版本
     */
    @Composable
    fun isSubmittingState(): State<Boolean> = 
        createStableState { it.isSubmitting }

    /**
     * 提交错误信息 - 优化版本
     */
    @Composable
    fun submitErrorState(): State<String?> = 
        createStableState { it.submitError }

    /**
     * 协议是否已同意 - 优化版本
     */
    @Composable
    fun isAgreementAcceptedState(): State<Boolean> = 
        createStableState { it.isAgreementAccepted }

    /**
     * 当前激活的表单数据 - 优化版本
     */
    @Composable
    fun activeFormState(): State<FormData> = 
        createStableState { it.activeForm }

    /**
     * 登录表单 - 优化版本
     */
    @Composable
    fun loginFormState(): State<LoginForm> = 
        createStableState { it.loginForm }

    /**
     * 注册表单 - 优化版本
     */
    @Composable
    fun registerFormState(): State<RegisterForm> = 
        createStableState { it.registerForm }

    /**
     * 验证结果 - 优化版本
     */
    @Composable
    fun validationResultsState(): State<ValidationResults> = 
        createStableState { it.validationResults }

    /**
     * 验证码状态 - 优化版本
     */
    @Composable
    fun captchaStateState(): State<CaptchaState> = 
        createStableState { it.captchaState }

    /**
     * 验证码图片路径 - 优化版本
     */
    @Composable
    fun captchaImagePathState(): State<String> = 
        createStableState { it.captchaState.imagePath }

    /**
     * 验证码会话ID - 优化版本
     */
    @Composable
    fun captchaSessionIdState(): State<String> = 
        createStableState { it.captchaState.sessionId }

    /**
     * 验证码是否正在加载 - 优化版本
     */
    @Composable
    fun isCaptchaLoadingState(): State<Boolean> = 
        createStableState { it.captchaState.isLoading }

    /**
     * 验证码错误信息 - 优化版本
     */
    @Composable
    fun captchaErrorState(): State<String?> = 
        createStableState { it.captchaState.error }

    /**
     * 是否有有效验证码 - 优化版本
     */
    @Composable
    fun hasValidCaptchaState(): State<Boolean> = 
        createStableState { it.captchaState.hasValidCaptcha }

    /**
     * 手机信息 - 优化版本
     */
    @Composable
    fun phoneInfoState(): State<PhoneInfo> = 
        createStableState { it.phoneInfo }

    /**
     * 手机号码 - 优化版本
     */
    @Composable
    fun phoneNumberState(): State<String> = 
        createStableState { it.phoneInfo.phoneNumber }

    /**
     * 运营商名称 - 优化版本
     */
    @Composable
    fun operatorNameState(): State<String> = 
        createStableState { it.phoneInfo.operatorName }

    /**
     * 脱敏手机号 - 优化版本
     */
    @Composable
    fun maskedPhoneNumberState(): State<String> = 
        createStableState { it.phoneInfo.maskedPhoneNumber }

    /**
     * 提交按钮是否可用 - 优化版本
     */
    @Composable
    fun isSubmitEnabledState(): State<Boolean> = 
        createStableState { it.isSubmitEnabled }

    /**
     * 获取提交按钮文字 - 优化版本
     */
    @Composable
    fun submitButtonTextState(): State<String> = 
        createStableState { it.submitButtonText }

    /**
     * 获取模式切换按钮文字 - 优化版本
     */
    @Composable
    fun switchModeButtonTextState(): State<String> = 
        createStableState { it.switchModeButtonText }

    /**
     * 是否有验证错误 - 优化版本
     */
    @Composable
    fun hasValidationErrorsState(): State<Boolean> = 
        createStableState { it.validationResults.hasErrors }

    // endregion
    
    // region 便利方法
    
    /** 提交按钮是否可用 */
    fun canSubmit(): Boolean = getCurrentSnapshot().isSubmitEnabled
    
    /** 获取提交按钮文字 */
    fun getSubmitButtonText(): String = getCurrentSnapshot().submitButtonText
    
    /** 获取模式切换按钮文字 */
    fun getSwitchModeButtonText(): String = getCurrentSnapshot().switchModeButtonText
    
    /** 是否有验证错误 */
    fun hasValidationErrors(): Boolean = getCurrentSnapshot().validationResults.hasErrors
    
    /** 获取当前手机号（根据模式） */
    fun getCurrentPhone(): String {
        val state = getCurrentSnapshot()
        return if (state.isLoginMode) {
            state.loginForm.phone
        } else {
            state.registerForm.phone
        }
    }
    
    /** 获取当前密码（根据模式） */
    fun getCurrentPassword(): String {
        val state = getCurrentSnapshot()
        return if (state.isLoginMode) {
            state.loginForm.password
        } else {
            state.registerForm.password
        }
    }
    
    /** 检查是否可以切换到注册模式 */
    fun canSwitchToRegister(): Boolean = getCurrentSnapshot().isLoginMode
    
    /** 检查是否可以切换到登录模式 */
    fun canSwitchToLogin(): Boolean = !getCurrentSnapshot().isLoginMode
    
    /** 获取验证码提示文字 */
    fun getCaptchaHint(): String {
        val state = getCurrentSnapshot()
        return when {
            state.captchaState.isLoading -> "验证码加载中..."
            state.captchaState.error != null -> "验证码加载失败，点击重试"
            state.captchaState.hasValidCaptcha -> "点击刷新验证码"
            else -> "获取验证码"
        }
    }
    
    /** 获取运营商客服电话 */
    fun getOperatorServiceNumber(): String {
        val operatorName = getCurrentSnapshot().phoneInfo.operatorName
        return when (operatorName) {
            "移动" -> "10086"
            "联通" -> "10010"
            "电信" -> "10000"
            else -> "10000"
        }
    }
    
    /** 获取登录状态摘要 */
    fun getLoginStatusSummary(): String {
        val state = getCurrentSnapshot()
        return buildString {
            append("模式: ${if (state.isLoginMode) "登录" else "注册"}")
            if (state.isSubmitting) append(", 提交中")
            if (state.hasError) append(", 有错误")
            if (state.validationResults.hasErrors) append(", 验证失败")
        }
    }
    
    // endregion
}

/**
 * 状态组合器 - 登录页面专用
 */
@Stable
data class LoginScreenState(
    val isLoading: Boolean,
    val error: String?,
    val isLoginMode: Boolean,
    val canSubmit: Boolean,
    val submitButtonText: String,
    val switchModeButtonText: String,
    val currentPhone: String,
    val currentPassword: String,
    val hasValidationErrors: Boolean,
    val hasValidCaptcha: Boolean,
    val captchaHint: String,
    val operatorServiceNumber: String,
    val loginStatusSummary: String
)



/**
 * 扩展函数：为LoginStateAdapter创建状态组合器
 */
fun LoginStateAdapter.toScreenState(): LoginScreenState {
    return LoginScreenState(
        isLoading = isCurrentlyLoading(),
        error = getCurrentError(),
        isLoginMode = getCurrentSnapshot().isLoginMode,
        canSubmit = canSubmit(),
        submitButtonText = getSubmitButtonText(),
        switchModeButtonText = getSwitchModeButtonText(),
        currentPhone = getCurrentPhone(),
        currentPassword = getCurrentPassword(),
        hasValidationErrors = hasValidationErrors(),
        hasValidCaptcha = getCurrentSnapshot().captchaState.hasValidCaptcha,
        captchaHint = getCaptchaHint(),
        operatorServiceNumber = getOperatorServiceNumber(),
        loginStatusSummary = getLoginStatusSummary()
    )
}

