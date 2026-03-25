package com.novel.utils.Store.NovelKeyChain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class KeyChainTokenProviderTest {

    @Test
    fun `getToken reads token from keychain`() {
        val keyChain = object : NovelKeyChain {
            override fun saveToken(accessToken: String?, refreshToken: String?) = Unit
            override fun save(key: NovelKeyChainType, value: String) = Unit
            override fun read(key: NovelKeyChainType): String? {
                return if (key == NovelKeyChainType.TOKEN) "token-123" else null
            }

            override fun delete(key: NovelKeyChainType) = Unit
        }

        val provider = KeyChainTokenProvider(keyChain)

        assertThat(provider.getToken() == "token-123").isTrue()
    }
}
