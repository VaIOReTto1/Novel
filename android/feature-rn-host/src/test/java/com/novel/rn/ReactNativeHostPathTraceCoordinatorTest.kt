package com.novel.rn

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactNativeHostPathTraceCoordinatorTest {

    private val coordinator = ReactNativeHostPathTraceCoordinator()

    @Test
    fun formatContextTrace_marksExistingContextPath() {
        assertThat(
            coordinator.formatContextTrace(
                trigger = "prewarm_after_first_frame",
                hasReactContext = true,
            ),
        ).isEqualTo("trigger=prewarm_after_first_frame reactContextPath=ALREADY_READY")
    }

    @Test
    fun formatContextTrace_marksFirstCreatePath() {
        assertThat(
            coordinator.formatContextTrace(
                trigger = "prewarm_after_first_frame",
                hasReactContext = false,
            ),
        ).isEqualTo("trigger=prewarm_after_first_frame reactContextPath=FIRST_CREATE")
    }

    @Test
    fun formatRootViewTrace_marksReuseAndContextState() {
        assertThat(
            coordinator.formatRootViewTrace(
                componentName = "ProfilePage",
                reused = true,
                hasReactContext = true,
            ),
        ).isEqualTo("component=ProfilePage reactRootViewPath=REUSED reactContextPath=ALREADY_READY")
    }

    @Test
    fun formatRootViewTrace_marksFirstCreateAndColdContext() {
        assertThat(
            coordinator.formatRootViewTrace(
                componentName = "ProfilePage",
                reused = false,
                hasReactContext = false,
            ),
        ).isEqualTo("component=ProfilePage reactRootViewPath=FIRST_CREATE reactContextPath=FIRST_CREATE")
    }

    @Test
    fun formatRootViewTrace_marksWarmOpenSeparatelyFromColdCreate() {
        assertThat(
            coordinator.formatRootViewTrace(
                componentName = "ProfilePage",
                reused = false,
                hasReactContext = true,
            ),
        ).isEqualTo("component=ProfilePage reactRootViewPath=OPEN reactContextPath=ALREADY_READY")
    }
}
