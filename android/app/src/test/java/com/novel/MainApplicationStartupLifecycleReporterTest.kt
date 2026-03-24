package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainApplicationStartupLifecycleReporterTest {

    @Test
    fun `markFirstActivityCreate forwards to monitor`() {
        val events = mutableListOf<String>()
        val reporter = MainApplicationStartupLifecycleReporter(
            onFirstActivityCreate = { events += "activity" },
            onFirstFrameDrawn = { events += "frame" },
            onAppFullyLoaded = { events += "loaded" },
            afterFirstFrame = { events += "deferred" },
        )

        reporter.markFirstActivityCreate()

        assertThat(events).containsExactly("activity")
    }

    @Test
    fun `markFirstFrameDrawn reports frame before deferred initialization`() {
        val events = mutableListOf<String>()
        val reporter = MainApplicationStartupLifecycleReporter(
            onFirstActivityCreate = { events += "activity" },
            onFirstFrameDrawn = { events += "frame" },
            onAppFullyLoaded = { events += "loaded" },
            afterFirstFrame = { events += "deferred" },
        )

        reporter.markFirstFrameDrawn()

        assertThat(events).containsExactly("frame", "deferred").inOrder()
    }

    @Test
    fun `markAppFullyLoaded forwards to monitor`() {
        val events = mutableListOf<String>()
        val reporter = MainApplicationStartupLifecycleReporter(
            onFirstActivityCreate = { events += "activity" },
            onFirstFrameDrawn = { events += "frame" },
            onAppFullyLoaded = { events += "loaded" },
            afterFirstFrame = { events += "deferred" },
        )

        reporter.markAppFullyLoaded()

        assertThat(events).containsExactly("loaded")
    }
}
