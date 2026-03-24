package com.novel.rn.host

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DefaultReactContextWarmupGatewayTest {

    @Test
    fun `warmUpIfNeeded skips when context already exists`() {
        val events = mutableListOf<String>()
        val gateway = DefaultReactContextWarmupGateway(
            hasReactContextAction = { true },
            createReactContextAction = { events += "warmup" },
        )

        gateway.warmUpIfNeeded()

        assertThat(events).isEmpty()
    }

    @Test
    fun `warmUpIfNeeded creates context when missing`() {
        val events = mutableListOf<String>()
        val gateway = DefaultReactContextWarmupGateway(
            hasReactContextAction = { false },
            createReactContextAction = { events += "warmup" },
        )

        gateway.warmUpIfNeeded()

        assertThat(events).containsExactly("warmup")
    }
}
