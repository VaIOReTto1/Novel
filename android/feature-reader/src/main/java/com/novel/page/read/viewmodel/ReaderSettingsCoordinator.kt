package com.novel.page.read.viewmodel

data class ReaderSettingsUpdateOutcome(
    val updatedState: ReaderState,
    val shouldRebuildVirtualPages: Boolean,
)

class ReaderSettingsCoordinator {

    fun createInitialSettingsIntent(
        loadSettings: () -> ReaderSettings,
        defaultSettings: () -> ReaderSettings = ReaderSettings::getDefault,
    ): ReaderIntent.UpdateSettings {
        val settings = try {
            loadSettings()
        } catch (_: Exception) {
            defaultSettings()
        }
        return ReaderIntent.UpdateSettings(settings)
    }

    fun applyUpdateSuccess(
        currentState: ReaderState,
        newPageData: PageData?,
        newPageIndex: Int,
    ): ReaderSettingsUpdateOutcome {
        return if (newPageData != null) {
            ReaderSettingsUpdateOutcome(
                updatedState = currentState.copy(
                    version = currentState.version + 1,
                    currentPageData = newPageData,
                    currentPageIndex = newPageIndex,
                ),
                shouldRebuildVirtualPages = true,
            )
        } else {
            ReaderSettingsUpdateOutcome(
                updatedState = currentState,
                shouldRebuildVirtualPages = false,
            )
        }
    }
}
