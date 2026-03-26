package com.novel.page.login.gateway

import androidx.compose.runtime.Stable
import com.novel.page.login.viewmodel.ValidationResults

interface LoginGateway {
    suspend fun login(username: String, password: String): LoginGatewayResult

    suspend fun register(
        username: String,
        password: String,
        sessionId: String,
        verifyCode: String,
    ): LoginGatewayResult

    suspend fun validateLogin(phone: String, password: String): ValidationResults

    suspend fun validateRegister(
        phone: String,
        password: String,
        passwordConfirm: String,
        verifyCode: String,
    ): ValidationResults

    suspend fun refreshCaptcha(): CaptchaGatewayResult

    suspend fun clearCaptchaCache()
}

interface PhoneInfoProvider {
    suspend fun loadPhoneInfo(): com.novel.page.login.viewmodel.PhoneInfo
}

@Stable
data class LoginGatewayResult(
    val success: Boolean,
    val message: String,
)

@Stable
data class CaptchaGatewayResult(
    val imagePath: String = "",
    val sessionId: String = "",
    val error: String? = null,
) {
    val isSuccess: Boolean
        get() = error == null
}
