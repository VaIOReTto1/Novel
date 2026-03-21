package com.novel.core.concurrency

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnDemandInitializerTest {

    @Test
    fun initializeIfNeeded_runsInitializerOnlyOnce() {
        var calls = 0
        val initializer = OnDemandInitializer { calls++ }

        val firstRun = initializer.initializeIfNeeded()
        val secondRun = initializer.initializeIfNeeded()

        assertTrue(firstRun)
        assertFalse(secondRun)
        assertEquals(1, calls)
    }

    @Test
    fun isInitialized_reflectsExecutionState() {
        val initializer = OnDemandInitializer {}

        assertFalse(initializer.isInitialized())

        initializer.initializeIfNeeded()

        assertTrue(initializer.isInitialized())
    }
}
