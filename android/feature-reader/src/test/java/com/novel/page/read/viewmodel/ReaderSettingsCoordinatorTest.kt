package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class ReaderSettingsCoordinatorTest {

    @Test
    fun createInitialSettingsIntent_returnsLoadedSettings() {
        val coordinator = ReaderSettingsCoordinator()
        val settings = ReaderSettings(fontSize = 20)

        val intent = coordinator.createInitialSettingsIntent(
            loadSettings = { settings },
            defaultSettings = { ReaderSettings.getDefault() },
        )

        assertThat(intent).isEqualTo(ReaderIntent.UpdateSettings(settings))
    }

    @Test
    fun createInitialSettingsIntent_fallsBackToDefaultWhenLoadingFails() {
        val coordinator = ReaderSettingsCoordinator()
        val defaultSettings = ReaderSettings(fontSize = 18)

        val intent = coordinator.createInitialSettingsIntent(
            loadSettings = { error("boom") },
            defaultSettings = { defaultSettings },
        )

        assertThat(intent).isEqualTo(ReaderIntent.UpdateSettings(defaultSettings))
    }

    @Test
    fun applyUpdateSuccess_updatesStateWhenNewPageDataExists() {
        val coordinator = ReaderSettingsCoordinator()
        val currentState = ReaderState(version = 3)
        val newPageData = PageData(
            chapterId = "chapter-1",
            chapterName = "Chapter 1",
            content = "content",
            pages = persistentListOf("page-1", "page-2"),
        )

        val outcome = coordinator.applyUpdateSuccess(
            currentState = currentState,
            newPageData = newPageData,
            newPageIndex = 1,
        )

        assertThat(outcome.updatedState.currentPageData).isEqualTo(newPageData)
        assertThat(outcome.updatedState.currentPageIndex).isEqualTo(1)
        assertThat(outcome.updatedState.version).isEqualTo(4)
        assertThat(outcome.shouldRebuildVirtualPages).isTrue()
    }

    @Test
    fun applyUpdateSuccess_keepsStateWhenNoPageDataReturned() {
        val coordinator = ReaderSettingsCoordinator()
        val currentState = ReaderState(version = 7)

        val outcome = coordinator.applyUpdateSuccess(
            currentState = currentState,
            newPageData = null,
            newPageIndex = 0,
        )

        assertThat(outcome.updatedState).isEqualTo(currentState)
        assertThat(outcome.shouldRebuildVirtualPages).isFalse()
    }
}
