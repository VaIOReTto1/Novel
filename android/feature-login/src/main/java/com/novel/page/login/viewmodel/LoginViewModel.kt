package com.novel.page.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.login.gateway.LoginGateway
import com.novel.page.login.gateway.PhoneInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginGateway: LoginGateway,
    private val phoneInfoProvider: PhoneInfoProvider,
) : BaseMviViewModel<LoginIntent, LoginState, LoginEffect>() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    val adapter = LoginStateAdapter(state)

    init {
        sendIntent(LoginIntent.InitializePage)
    }

    override fun createInitialState(): LoginState = LoginState()

    override fun getReducer(): MviReducer<LoginIntent, LoginState> = LoginReducerAdapter()

    override fun onIntentProcessed(intent: LoginIntent, newState: LoginState) {
        super.onIntentProcessed(intent, newState)

        viewModelScope.launch {
            when (intent) {
                LoginIntent.InitializePage -> handleInitializePage()
                LoginIntent.SubmitLogin -> handleSubmitLogin(newState)
                LoginIntent.SubmitRegister -> handleSubmitRegister(newState)
                LoginIntent.RefreshCaptcha -> handleRefreshCaptcha()
                else -> Unit
            }
        }
    }

    private suspend fun handleInitializePage() {
        runCatching {
            phoneInfoProvider.loadPhoneInfo()
        }.onSuccess { phoneInfo ->
            updateState(LoginStateUpdater.updatePhoneInfo(getCurrentState(), phoneInfo))
            handleRefreshCaptcha()
        }.onFailure { error ->
            updateState(
                getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    isLoading = false,
                    error = "页面初始化失败：${error.localizedMessage}",
                ),
            )
        }
    }

    private suspend fun handleSubmitLogin(currentState: LoginState) {
        val validationResult = loginGateway.validateLogin(
            phone = currentState.loginForm.phone,
            password = currentState.loginForm.password,
        )

        if (!validationResult.isValid) {
            val newState = LoginStateUpdater.updateValidationResults(currentState, validationResult)
            updateState(newState)
            delay(500)
            val result = LoginStateUpdater.updateLoginFailure(
                newState,
                "登录失败: 验证未通过",
            )
            updateState(result.newState)
            result.effect?.let(::sendEffect)
            return
        }

        val loginResult = loginGateway.login(
            username = currentState.loginForm.phone,
            password = currentState.loginForm.password,
        )

        val result = if (loginResult.success) {
            LoginStateUpdater.updateLoginSuccess(currentState, loginResult.message)
        } else {
            LoginStateUpdater.updateLoginFailure(currentState, loginResult.message)
        }
        updateState(result.newState)
        result.effect?.let(::sendEffect)
    }

    private suspend fun handleSubmitRegister(currentState: LoginState) {
        val validationResult = loginGateway.validateRegister(
            phone = currentState.registerForm.phone,
            password = currentState.registerForm.password,
            passwordConfirm = currentState.registerForm.passwordConfirm,
            verifyCode = currentState.registerForm.verifyCode,
        )

        if (!validationResult.isValid) {
            val newState = LoginStateUpdater.updateValidationResults(currentState, validationResult)
            updateState(newState)
            delay(500)
            val result = LoginStateUpdater.updateRegisterFailure(
                newState,
                "注册失败: 验证未通过",
            )
            updateState(result.newState)
            result.effect?.let(::sendEffect)
            return
        }

        val registerResult = loginGateway.register(
            username = currentState.registerForm.phone,
            password = currentState.registerForm.password,
            sessionId = currentState.captchaState.sessionId,
            verifyCode = currentState.registerForm.verifyCode,
        )

        val result = if (registerResult.success) {
            LoginStateUpdater.updateRegisterSuccess(currentState, registerResult.message)
        } else {
            LoginStateUpdater.updateRegisterFailure(currentState, registerResult.message)
        }
        updateState(result.newState)
        result.effect?.let(::sendEffect)
    }

    private suspend fun handleRefreshCaptcha() {
        val captchaResult = loginGateway.refreshCaptcha()
        val newCaptchaState = CaptchaState(
            imagePath = captchaResult.imagePath,
            sessionId = captchaResult.sessionId,
            isLoading = false,
            error = captchaResult.error,
        )
        updateState(LoginStateUpdater.updateCaptchaState(getCurrentState(), newCaptchaState))

        if (!captchaResult.isSuccess) {
            sendEffect(LoginEffect.ShowToast("验证码加载失败"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            loginGateway.clearCaptchaCache()
        }
    }

    private class LoginReducerAdapter : MviReducer<LoginIntent, LoginState> {
        private val effectReducer = LoginReducer()

        override fun reduce(currentState: LoginState, intent: LoginIntent): LoginState {
            return effectReducer.reduce(currentState, intent).newState
        }
    }
}
