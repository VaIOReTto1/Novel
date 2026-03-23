package com.novel.page.login.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

@Stable
class LoginStateAdapter(
    stateFlow: StateFlow<LoginState>
) : StateAdapter<LoginState>(stateFlow) {

    @Composable
    fun isLoginModeState(): State<Boolean> =
        createStableState { it.isLoginMode }

    @Composable
    fun isRegisterModeState(): State<Boolean> =
        createStableState { !it.isLoginMode }

    @Composable
    fun isSubmittingState(): State<Boolean> =
        createStableState { it.isSubmitting }

    @Composable
    fun submitErrorState(): State<String?> =
        createStableState { it.submitError }

    @Composable
    fun isAgreementAcceptedState(): State<Boolean> =
        createStableState { it.isAgreementAccepted }

    @Composable
    fun activeFormState(): State<FormData> =
        createStableState { it.activeForm }

    @Composable
    fun loginFormState(): State<LoginForm> =
        createStableState { it.loginForm }

    @Composable
    fun registerFormState(): State<RegisterForm> =
        createStableState { it.registerForm }

    @Composable
    fun validationResultsState(): State<ValidationResults> =
        createStableState { it.validationResults }

    @Composable
    fun captchaStateState(): State<CaptchaState> =
        createStableState { it.captchaState }

    @Composable
    fun captchaImagePathState(): State<String> =
        createStableState { it.captchaState.imagePath }

    @Composable
    fun captchaSessionIdState(): State<String> =
        createStableState { it.captchaState.sessionId }

    @Composable
    fun isCaptchaLoadingState(): State<Boolean> =
        createStableState { it.captchaState.isLoading }

    @Composable
    fun captchaErrorState(): State<String?> =
        createStableState { it.captchaState.error }

    @Composable
    fun hasValidCaptchaState(): State<Boolean> =
        createStableState { it.captchaState.hasValidCaptcha }

    @Composable
    fun phoneInfoState(): State<PhoneInfo> =
        createStableState { it.phoneInfo }

    @Composable
    fun phoneNumberState(): State<String> =
        createStableState { it.phoneInfo.phoneNumber }

    @Composable
    fun operatorNameState(): State<String> =
        createStableState { it.phoneInfo.operatorName }

    @Composable
    fun maskedPhoneNumberState(): State<String> =
        createStableState { it.phoneInfo.maskedPhoneNumber }

    @Composable
    fun isSubmitEnabledState(): State<Boolean> =
        createStableState { it.isSubmitEnabled }

    @Composable
    fun submitButtonTextState(): State<String> =
        createStableState { it.submitButtonText }

    @Composable
    fun switchModeButtonTextState(): State<String> =
        createStableState { it.switchModeButtonText }

    @Composable
    fun hasValidationErrorsState(): State<Boolean> =
        createStableState { it.validationResults.hasErrors }

    fun canSubmit(): Boolean = getCurrentSnapshot().isSubmitEnabled

    fun getSubmitButtonText(): String = getCurrentSnapshot().submitButtonText

    fun getSwitchModeButtonText(): String = getCurrentSnapshot().switchModeButtonText

    fun hasValidationErrors(): Boolean = getCurrentSnapshot().validationResults.hasErrors

    fun getCurrentPhone(): String {
        val state = getCurrentSnapshot()
        return if (state.isLoginMode) {
            state.loginForm.phone
        } else {
            state.registerForm.phone
        }
    }

    fun getCurrentPassword(): String {
        val state = getCurrentSnapshot()
        return if (state.isLoginMode) {
            state.loginForm.password
        } else {
            state.registerForm.password
        }
    }

    fun canSwitchToRegister(): Boolean = getCurrentSnapshot().isLoginMode

    fun canSwitchToLogin(): Boolean = !getCurrentSnapshot().isLoginMode

    fun getCaptchaHint(): String {
        val state = getCurrentSnapshot()
        return when {
            state.captchaState.isLoading -> "验证码加载中..."
            state.captchaState.error != null -> "验证码加载失败，点击重试"
            state.captchaState.hasValidCaptcha -> "点击刷新验证码"
            else -> "获取验证码"
        }
    }

    fun getOperatorServiceNumber(): String {
        val operatorName = getCurrentSnapshot().phoneInfo.operatorName
        return when (operatorName) {
            "移动" -> "10086"
            "联通" -> "10010"
            "电信" -> "10000"
            else -> "10000"
        }
    }

    fun getLoginStatusSummary(): String {
        val state = getCurrentSnapshot()
        return buildString {
            append("模式: ${if (state.isLoginMode) "登录" else "注册"}")
            if (state.isSubmitting) append(", 提交中")
            if (state.hasError) append(", 有错误")
            if (state.validationResults.hasErrors) append(", 验证失败")
        }
    }
}

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
