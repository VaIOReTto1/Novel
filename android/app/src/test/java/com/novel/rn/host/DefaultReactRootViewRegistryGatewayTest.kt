package com.novel.rn.host

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultReactRootViewRegistryGatewayTest {

    @Test
    fun `getOrCreateReactRootView delegates to registry action`() {
        val events = mutableListOf<String>()
        val failure = RuntimeException("expected")
        val gateway = DefaultReactRootViewRegistryGateway(
            getOrCreateReactRootViewAction = { componentName, initialProps ->
                events += "$componentName:${initialProps == null}"
                throw failure
            },
        )

        val thrown = runCatching {
            gateway.getOrCreateReactRootView(
                componentName = "SettingsPageComponent",
                initialProps = null,
            )
        }.exceptionOrNull()

        assertThat(thrown).isInstanceOf(RuntimeException::class.java)
        assertThat(thrown?.message).contains("expected")
        assertThat(events).containsExactly("SettingsPageComponent:true")
    }
}
