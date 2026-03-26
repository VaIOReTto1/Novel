package com.novel.page.login.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.login.gateway.CaptchaGatewayResult
import com.novel.page.login.gateway.LoginGateway
import com.novel.page.login.gateway.LoginGatewayResult
import com.novel.page.login.gateway.PhoneInfoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialize page loads phone info and captcha`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            loginGateway = FakeLoginGateway(
                captchaResult = CaptchaGatewayResult(
                    imagePath = "/tmp/captcha.png",
                    sessionId = "session-1",
                ),
            ),
            phoneInfoProvider = FakePhoneInfoProvider(
                phoneInfo = PhoneInfo(phoneNumber = "13812345678", operatorName = "Carrier"),
            ),
        )

        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.phoneInfo.operatorName).isEqualTo("Carrier")
        assertThat(state.captchaState.sessionId).isEqualTo("session-1")
    }

    @Test
    fun `submit login succeeds through gateway`() = runTest(dispatcher) {
        val viewModel = LoginViewModel(
            loginGateway = FakeLoginGateway(
                loginResult = LoginGatewayResult(success = true, message = "登录成功"),
            ),
            phoneInfoProvider = FakePhoneInfoProvider(),
        )

        advanceUntilIdle()
        viewModel.sendIntent(LoginIntent.InputPhone("13812345678"))
        viewModel.sendIntent(LoginIntent.InputPassword("password"))
        viewModel.sendIntent(LoginIntent.ToggleAgreement(true))
        viewModel.sendIntent(LoginIntent.SubmitLogin)
        advanceUntilIdle()

        assertThat(viewModel.state.value.submitError).isNull()
        assertThat(viewModel.state.value.validationResults.isValid).isTrue()
    }

    private class FakeLoginGateway(
        private val loginResult: LoginGatewayResult = LoginGatewayResult(true, "ok"),
        private val registerResult: LoginGatewayResult = LoginGatewayResult(true, "ok"),
        private val captchaResult: CaptchaGatewayResult = CaptchaGatewayResult(),
    ) : LoginGateway {
        override suspend fun login(username: String, password: String): LoginGatewayResult = loginResult

        override suspend fun register(
            username: String,
            password: String,
            sessionId: String,
            verifyCode: String,
        ): LoginGatewayResult = registerResult

        override suspend fun validateLogin(phone: String, password: String): ValidationResults =
            ValidationResults()

        override suspend fun validateRegister(
            phone: String,
            password: String,
            passwordConfirm: String,
            verifyCode: String,
        ): ValidationResults = ValidationResults()

        override suspend fun refreshCaptcha(): CaptchaGatewayResult = captchaResult

        override suspend fun clearCaptchaCache() = Unit
    }

    private class FakePhoneInfoProvider(
        private val phoneInfo: PhoneInfo = PhoneInfo(),
    ) : PhoneInfoProvider {
        override suspend fun loadPhoneInfo(): PhoneInfo = phoneInfo
    }
}
