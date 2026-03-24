package com.novel

internal class MainApplicationStartupOrchestrator(
    private val timeProvider: () -> Long = System::currentTimeMillis,
) {

    fun initializeCriticalComponents(
        isNewArchitectureEnabled: Boolean,
        initializeThemeManager: () -> Unit,
        initializeSoLoader: () -> Unit,
        initializeNewArchitecture: () -> Unit,
        onComponentInitialized: (componentName: String, startTime: Long, duration: Long) -> Unit,
    ) {
        runAndRecord("ThemeManager", onComponentInitialized, initializeThemeManager)
        runAndRecord("SoLoader", onComponentInitialized, initializeSoLoader)
        if (isNewArchitectureEnabled) {
            runAndRecord("NewArchitecture", onComponentInitialized, initializeNewArchitecture)
        }
    }

    fun initializeNonCriticalComponentsAfterFirstFrame(
        plan: StartupDeferredInitializationPlan,
        launchNetworkInitialization: () -> Unit,
        launchSettingsInitialization: () -> Unit,
    ) {
        if (plan.shouldInitializeNetwork) {
            launchNetworkInitialization()
        }
        if (plan.shouldInitializeSettings) {
            launchSettingsInitialization()
        }
    }

    private fun runAndRecord(
        componentName: String,
        onComponentInitialized: (componentName: String, startTime: Long, duration: Long) -> Unit,
        action: () -> Unit,
    ) {
        val startTime = timeProvider()
        action()
        onComponentInitialized(componentName, startTime, timeProvider() - startTime)
    }
}
