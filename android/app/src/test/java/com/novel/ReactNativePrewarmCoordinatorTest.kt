package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactNativePrewarmCoordinatorTest {

    @Test
    fun shouldPrewarmAfterFirstFrame_onlyReturnsTrueOnce() {
        val coordinator = ReactNativePrewarmCoordinator()

        assertThat(coordinator.shouldPrewarmAfterFirstFrame()).isTrue()
        assertThat(coordinator.shouldPrewarmAfterFirstFrame()).isFalse()
    }
}
