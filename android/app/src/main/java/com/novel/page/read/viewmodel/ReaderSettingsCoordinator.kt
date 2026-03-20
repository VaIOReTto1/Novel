package com.novel.page.read.viewmodel

import com.novel.page.read.usecase.UpdateSettingsUseCase

internal data class ReaderSettingsUpdateOutcome(
    val updatedState: ReaderState,
    val shouldRebuildVirtualPages: Boolean,
)

internal class ReaderSettingsCoordinator {

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

    fun applyUpdateResult(
        currentState: ReaderState,
        result: UpdateSettingsUseCase.UpdateResult,
    ): ReaderSettingsUpdateOutcome {
        return when (result) {
            is UpdateSettingsUseCase.UpdateResult.Success -> {
                if (result.newPageData != null) {
                    ReaderSettingsUpdateOutcome(
                        updatedState = currentState.copy(
                            version = currentState.version + 1,
                            currentPageData = result.newPageData,
                            currentPageIndex = result.newPageIndex,
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
    }
}
