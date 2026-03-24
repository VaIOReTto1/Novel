package com.novel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainApplicationStartupOrchestratorTest {

    @Test
    fun `initializeCriticalComponents preserves startup order`() {
        val events = mutableListOf<String>()
        val orchestrator = MainApplicationStartupOrchestrator(
            timeProvider = { 100L + events.size },
        )

        orchestrator.initializeCriticalComponents(
            isNewArchitectureEnabled = true,
            initializeThemeManager = { events += "theme" },
            initializeSoLoader = { events += "soLoader" },
            initializeNewArchitecture = { events += "newArch" },
            onComponentInitialized = { componentName: String, _: Long, _: Long ->
                events += "record:$componentName"
            },
        )

        assertThat(events).containsExactly(
            "theme",
            "record:ThemeManager",
            "soLoader",
            "record:SoLoader",
            "newArch",
            "record:NewArchitecture",
        ).inOrder()
    }

    @Test
    fun `initializeCriticalComponents skips new architecture when disabled`() {
        val events = mutableListOf<String>()
        val orchestrator = MainApplicationStartupOrchestrator()

        orchestrator.initializeCriticalComponents(
            isNewArchitectureEnabled = false,
            initializeThemeManager = { events += "theme" },
            initializeSoLoader = { events += "soLoader" },
            initializeNewArchitecture = { events += "newArch" },
            onComponentInitialized = { componentName: String, _: Long, _: Long ->
                events += "record:$componentName"
            },
        )

        assertThat(events).containsExactly(
            "theme",
            "record:ThemeManager",
            "soLoader",
            "record:SoLoader",
        ).inOrder()
        assertThat(events).doesNotContain("newArch")
        assertThat(events).doesNotContain("record:NewArchitecture")
    }

    @Test
    fun `initializeNonCriticalComponentsAfterFirstFrame launches only planned tasks`() {
        val events = mutableListOf<String>()
        val orchestrator = MainApplicationStartupOrchestrator()

        orchestrator.initializeNonCriticalComponentsAfterFirstFrame(
            plan = StartupDeferredInitializationPlan(
                shouldInitializeNetwork = true,
                shouldInitializeSettings = false,
            ),
            launchNetworkInitialization = { events += "network" },
            launchSettingsInitialization = { events += "settings" },
        )

        assertThat(events).containsExactly("network")
    }
}
