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
        plan.tasks.forEach { task ->
            when (task.id) {
                StartupDeferredInitializationTaskId.NETWORK -> launchNetworkInitialization()
                StartupDeferredInitializationTaskId.SETTINGS -> launchSettingsInitialization()
            }
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
