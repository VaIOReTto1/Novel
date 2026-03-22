package com.novel.page.login.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginReducerTest {

    @Test
    fun reduce_switchToRegister_enablesRegisterModeAndClearsSubmitError() {
        val reducer = LoginReducer()

        val result = reducer.reduce(
            currentState = LoginState(
                isLoginMode = true,
                submitError = "boom",
                validationResults = ValidationResults(phoneError = "bad"),
            ),
            intent = LoginIntent.SwitchToRegister,
        )

        assertThat(result.newState.isLoginMode).isFalse()
        assertThat(result.newState.submitError).isNull()
        assertThat(result.newState.validationResults.hasErrors).isFalse()
    }
}
