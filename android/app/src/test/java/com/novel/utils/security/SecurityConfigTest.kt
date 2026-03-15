package com.novel.utils.security

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SecurityConfigTest {

    private val securityConfig = SecurityConfig()

    @Test
    fun `shouldRefreshToken returns true when expiry is within threshold`() {
        val expiryTimestamp = System.currentTimeMillis() + SecurityConfig.TOKEN_REFRESH_THRESHOLD_MS - 1

        assertThat(securityConfig.shouldRefreshToken(expiryTimestamp)).isTrue()
    }

    @Test
    fun `shouldRefreshToken returns false when expiry is beyond threshold`() {
        val expiryTimestamp = System.currentTimeMillis() + SecurityConfig.TOKEN_REFRESH_THRESHOLD_MS + 60_000L

        assertThat(securityConfig.shouldRefreshToken(expiryTimestamp)).isFalse()
    }

    @Test
    fun `isFileExtensionAllowed accepts configured image extensions`() {
        assertThat(securityConfig.isFileExtensionAllowed("cover.webp")).isTrue()
        assertThat(securityConfig.isFileExtensionAllowed("avatar.PNG")).isTrue()
    }

    @Test
    fun `isFileExtensionAllowed rejects unsupported extensions`() {
        assertThat(securityConfig.isFileExtensionAllowed("payload.exe")).isFalse()
        assertThat(securityConfig.isFileExtensionAllowed("archive.zip")).isFalse()
    }

    @Test
    fun `getOperatorServiceNumber falls back to default when operator is unknown`() {
        assertThat(securityConfig.getOperatorServiceNumber("未知运营商")).isEqualTo("10000")
    }

    @Test
    fun `sanitizeForLog masks middle section of long strings`() {
        assertThat(securityConfig.sanitizeForLog("abcdefgh")).isEqualTo("ab****gh")
        assertThat(securityConfig.sanitizeForLog("1234")).isEqualTo("****")
    }
}
