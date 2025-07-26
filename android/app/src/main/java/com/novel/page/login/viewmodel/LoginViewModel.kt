package com.novel.page.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.login.usecase.*
import com.novel.utils.TimberLogger
import com.novel.utils.PhoneInfoUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Login模块ViewModel - MVI重构版本
 *
 * 基于统一MVI框架重构，职责：
 * - 继承BaseMviViewModel，使用统一状态管理
 * - 协调各种UseCase处理业务逻辑
 * - 处理Intent到UseCase的调用转换
 * - 管理页面生命周期和资源清理
 *
 * 重构要点：
 * - 移除原有的MutableStateFlow和Channel
 * - 使用BaseMviViewModel提供的状态管理
 * - 业务逻辑完全委托给UseCase
 * - 保持所有原有功能和行为不变
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    /** 登录UseCase */
    private val loginUseCase: LoginUseCase,
    /** 注册UseCase */
    private val registerUseCase: RegisterUseCase,
    /** 表单验证UseCase */
    private val validateFormUseCase: ValidateFormUseCase,
    /** 验证码管理UseCase */
    private val captchaUseCase: CaptchaUseCase,
    /** 初始化页面UseCase */
    private val initializePageUseCase: InitializePageUseCase,
    /** 手机信息工具 */
    private val phoneInfoUtil: PhoneInfoUtil
) : BaseMviViewModel<LoginIntent, LoginState, LoginEffect>() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    /** 状态适配器，提供便利的状态访问方法 */
    val adapter = LoginStateAdapter(state)

    init {
        TimberLogger.d(TAG, "LoginViewModel初始化 - MVI版本")
        // 自动初始化页面数据
        sendIntent(LoginIntent.InitializePage)
    }

    override fun createInitialState(): LoginState {
        return LoginState()
    }

    override fun getReducer(): MviReducer<LoginIntent, LoginState> {
        return LoginReducerAdapter()
    }

    override fun onIntentProcessed(intent: LoginIntent, newState: LoginState) {
        super.onIntentProcessed(intent, newState)

        // 根据Intent类型触发相应的UseCase
        viewModelScope.launch {
            when (intent) {
                is LoginIntent.InitializePage -> handleInitializePage()
                is LoginIntent.SubmitLogin -> handleSubmitLogin(newState)
                is LoginIntent.SubmitRegister -> handleSubmitRegister(newState)
                is LoginIntent.RefreshCaptcha -> handleRefreshCaptcha()
                else -> {
                    // 其他Intent由Reducer直接处理，无需额外UseCase调用
                }
            }
        }
    }

    /**
     * 处理页面初始化
     */
    private suspend fun handleInitializePage() {
        try {
            TimberLogger.d(TAG, "开始初始化页面数据")

            val result = initializePageUseCase(Unit)

            // 更新手机信息
            val newState = LoginStateUpdater.updatePhoneInfo(getCurrentState(), result.phoneInfo)
            updateState(newState)

            // 加载验证码
            handleRefreshCaptcha()

            TimberLogger.d(TAG, "页面初始化完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "页面初始化失败", e)
            val newState = getCurrentState().copy(
                version = getCurrentState().version + 1,
                isLoading = false,
                error = "页面初始化失败：${e.localizedMessage}"
            )
            updateState(newState)
        }
    }

    /**
     * 处理登录提交
     */
    private suspend fun handleSubmitLogin(currentState: LoginState) {
        try {
            TimberLogger.d(TAG, "开始处理登录")

            // 验证表单
            val validationResult = validateFormUseCase.validateLogin(
                phone = currentState.loginForm.phone,
                password = currentState.loginForm.password
            )

            if (!validationResult.isValid) {
                val newState =
                    LoginStateUpdater.updateValidationResults(currentState, validationResult)
                updateState(newState)
                delay(500)
                val result = LoginStateUpdater.updateLoginFailure(
                    newState,
                    "登录失败: 验证未通过"
                )
                updateState(result.newState)
                result.effect?.let { sendEffect(it) }
                return
            }

            // 执行登录
            val loginResult = loginUseCase(
                LoginUseCase.Params(
                    username = currentState.loginForm.phone,
                    password = currentState.loginForm.password
                )
            )

            when (loginResult) {
                is LoginUseCase.Result.Success -> {
                    val result =
                        LoginStateUpdater.updateLoginSuccess(currentState, loginResult.message)
                    updateState(result.newState)
                    result.effect?.let { sendEffect(it) }
                }

                is LoginUseCase.Result.Error -> {
                    val result =
                        LoginStateUpdater.updateLoginFailure(currentState, loginResult.message)
                    updateState(result.newState)
                    result.effect?.let { sendEffect(it) }
                }
            }

        } catch (e: Exception) {
            TimberLogger.e(TAG, "登录处理异常", e)
            val result =
                LoginStateUpdater.updateLoginFailure(currentState, "登录失败：${e.localizedMessage}")
            updateState(result.newState)
            result.effect?.let { sendEffect(it) }
        }
    }

    /**
     * 处理注册提交
     */
    private suspend fun handleSubmitRegister(currentState: LoginState) {
        try {
            TimberLogger.d(TAG, "开始处理注册")

            // 验证表单
            val validationResult = validateFormUseCase.validateRegister(
                phone = currentState.registerForm.phone,
                password = currentState.registerForm.password,
                passwordConfirm = currentState.registerForm.passwordConfirm,
                verifyCode = currentState.registerForm.verifyCode
            )

            if (!validationResult.isValid) {
                val newState =
                    LoginStateUpdater.updateValidationResults(currentState, validationResult)
                updateState(newState)
                delay(500)
                val result = LoginStateUpdater.updateRegisterFailure(
                    newState,
                    "注册失败: 验证未通过"
                )
                updateState(result.newState)
                result.effect?.let { sendEffect(it) }
                return
            }

            // 执行注册
            val registerResult = registerUseCase(
                RegisterUseCase.Params(
                    username = currentState.registerForm.phone,
                    password = currentState.registerForm.password,
                    sessionId = currentState.captchaState.sessionId,
                    verifyCode = currentState.registerForm.verifyCode
                )
            )

            when (registerResult) {
                is RegisterUseCase.Result.Success -> {
                    val result = LoginStateUpdater.updateRegisterSuccess(
                        currentState,
                        registerResult.message
                    )
                    updateState(result.newState)
                    result.effect?.let { sendEffect(it) }
                }

                is RegisterUseCase.Result.Error -> {
                    val result = LoginStateUpdater.updateRegisterFailure(
                        currentState,
                        registerResult.message
                    )
                    updateState(result.newState)
                    result.effect?.let { sendEffect(it) }
                }
            }

        } catch (e: Exception) {
            TimberLogger.e(TAG, "注册处理异常", e)
            val result = LoginStateUpdater.updateRegisterFailure(
                currentState,
                "注册失败：${e.localizedMessage}"
            )
            updateState(result.newState)
            result.effect?.let { sendEffect(it) }
        }
    }

    /**
     * 处理验证码刷新
     */
    private suspend fun handleRefreshCaptcha() {
        try {
            TimberLogger.d(TAG, "开始刷新验证码")

            val captchaResult = captchaUseCase.refreshCaptcha()

            when (captchaResult) {
                is CaptchaUseCase.Result.Success -> {
                    val newCaptchaState = CaptchaState(
                        imagePath = captchaResult.imagePath,
                        sessionId = captchaResult.sessionId,
                        isLoading = false,
                        error = null
                    )
                    val newState =
                        LoginStateUpdater.updateCaptchaState(getCurrentState(), newCaptchaState)
                    updateState(newState)
                }

                is CaptchaUseCase.Result.Error -> {
                    val newCaptchaState = CaptchaState(
                        imagePath = "",
                        sessionId = "",
                        isLoading = false,
                        error = captchaResult.message
                    )
                    val newState =
                        LoginStateUpdater.updateCaptchaState(getCurrentState(), newCaptchaState)
                    updateState(newState)
                    sendEffect(LoginEffect.ShowToast("验证码加载失败"))
                }
            }
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "验证码刷新异常", e)
            val newCaptchaState = CaptchaState(
                imagePath = "",
                sessionId = "",
                isLoading = false,
                error = e.localizedMessage
            )
            val newState = LoginStateUpdater.updateCaptchaState(getCurrentState(), newCaptchaState)
            updateState(newState)
        }
    }

    override fun onCleared() {
        super.onCleared()
        TimberLogger.d(TAG, "LoginViewModel清理资源")
        // 清理验证码缓存
        viewModelScope.launch {
            try {
                captchaUseCase.clearCache()
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理验证码缓存失败", e)
            }
        }
    }

    /**
     * LoginReducer适配器
     * 将MviReducerWithEffect适配为MviReducer接口
     */
    private class LoginReducerAdapter : MviReducer<LoginIntent, LoginState> {
        private val effectReducer = LoginReducer()
        
        override fun reduce(currentState: LoginState, intent: LoginIntent): LoginState {
            val result = effectReducer.reduce(currentState, intent)
            return result.newState
        }
    }
}
