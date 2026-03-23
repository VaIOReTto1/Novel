package com.novel.page.login.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class LoginStateAdapterTest {

    @Test
    fun canSubmit_returnsTrue_whenAgreementAcceptedAndFormValid() {
        val adapter = LoginStateAdapter(
            MutableStateFlow(
                LoginState(
                    isLoginMode = true,
                    isAgreementAccepted = true,
                    loginForm = LoginForm(
                        phone = "13800138000",
                        password = "123456"
                    ),
                ),
            ),
        )

        assertThat(adapter.canSubmit()).isTrue()
        assertThat(adapter.getSubmitButtonText()).isEqualTo("登录")
    }

    @Test
    fun getCaptchaHint_returnsRetryText_whenCaptchaHasError() {
        val adapter = LoginStateAdapter(
            MutableStateFlow(
                LoginState(
                    captchaState = CaptchaState(error = "load failed"),
                ),
            ),
        )

        assertThat(adapter.getCaptchaHint()).isEqualTo("验证码加载失败，点击重试")
    }
}
