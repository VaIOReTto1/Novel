package com.novel.page.welfare.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WelfareWebSecurityConfigTest {

    @Test
    fun allowsBingHostsAndInternalSchemes() {
        assertThat(WelfareWebSecurityConfig.isAllowedMainFrameUrl("https://www.bing.com/")).isTrue()
        assertThat(WelfareWebSecurityConfig.isAllowedMainFrameUrl("about:blank")).isTrue()
    }

    @Test
    fun opensUnknownHostsExternally() {
        assertThat(WelfareWebSecurityConfig.shouldOpenExternallyUrl("https://example.com/")).isTrue()
        assertThat(WelfareWebSecurityConfig.shouldOpenExternallyUrl("mailto:test@example.com")).isTrue()
    }
}
