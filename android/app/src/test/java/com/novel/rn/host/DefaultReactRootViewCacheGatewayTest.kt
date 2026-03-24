package com.novel.rn.host

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultReactRootViewCacheGatewayTest {

    @Test
    fun `clearComponentCache delegates to clear action`() {
        val events = mutableListOf<String>()
        val gateway = DefaultReactRootViewCacheGateway(
            clearComponentCacheAction = { componentName -> events += "clear:$componentName" },
            clearAllComponentCacheAction = { events += "clearAll" },
        )

        gateway.clearComponentCache("profile")

        assertThat(events).containsExactly("clear:profile")
    }

    @Test
    fun `clearAllComponentCache delegates to clear all action`() {
        val events = mutableListOf<String>()
        val gateway = DefaultReactRootViewCacheGateway(
            clearComponentCacheAction = { componentName -> events += "clear:$componentName" },
            clearAllComponentCacheAction = { events += "clearAll" },
        )

        gateway.clearAllComponentCache()

        assertThat(events).containsExactly("clearAll")
    }
}
