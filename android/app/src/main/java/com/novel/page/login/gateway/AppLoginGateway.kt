package com.novel.page.login.gateway

import com.novel.page.login.usecase.CaptchaUseCase
import com.novel.page.login.usecase.InitializePageUseCase
import com.novel.page.login.usecase.LoginUseCase
import com.novel.page.login.usecase.RegisterUseCase
import com.novel.page.login.usecase.ValidateFormUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLoginGateway @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val validateFormUseCase: ValidateFormUseCase,
    private val captchaUseCase: CaptchaUseCase,
) : LoginGateway {

    override suspend fun login(username: String, password: String): LoginGatewayResult {
        return when (val result = loginUseCase(LoginUseCase.Params(username, password))) {
            is LoginUseCase.Result.Success -> LoginGatewayResult(success = true, message = result.message)
            is LoginUseCase.Result.Error -> LoginGatewayResult(success = false, message = result.message)
        }
    }

    override suspend fun register(
        username: String,
        password: String,
        sessionId: String,
        verifyCode: String,
    ): LoginGatewayResult {
        return when (
            val result = registerUseCase(
                RegisterUseCase.Params(
                    username = username,
                    password = password,
                    sessionId = sessionId,
                    verifyCode = verifyCode,
                ),
            )
        ) {
            is RegisterUseCase.Result.Success -> LoginGatewayResult(success = true, message = result.message)
            is RegisterUseCase.Result.Error -> LoginGatewayResult(success = false, message = result.message)
        }
    }

    override suspend fun validateLogin(phone: String, password: String) =
        validateFormUseCase.validateLogin(phone, password)

    override suspend fun validateRegister(
        phone: String,
        password: String,
        passwordConfirm: String,
        verifyCode: String,
    ) = validateFormUseCase.validateRegister(phone, password, passwordConfirm, verifyCode)

    override suspend fun refreshCaptcha(): CaptchaGatewayResult {
        return when (val result = captchaUseCase.refreshCaptcha()) {
            is CaptchaUseCase.Result.Success -> CaptchaGatewayResult(
                imagePath = result.imagePath,
                sessionId = result.sessionId,
            )
            is CaptchaUseCase.Result.Error -> CaptchaGatewayResult(error = result.message)
        }
    }

    override suspend fun clearCaptchaCache() {
        captchaUseCase.clearCache()
    }
}

@Singleton
class AppPhoneInfoProvider @Inject constructor(
    private val initializePageUseCase: InitializePageUseCase,
) : PhoneInfoProvider {

    override suspend fun loadPhoneInfo() = initializePageUseCase(Unit).phoneInfo
}
