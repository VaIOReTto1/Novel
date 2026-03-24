package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class SearchStateAdapterTest {

    @Test
    fun canPerformSearch_returnsTrue_whenQueryPresentAndIdle() {
        val adapter = SearchStateAdapter(
            MutableStateFlow(
                SearchState(
                    searchQuery = "斗破苍穹",
                    isLoading = false,
                ),
            ),
        )

        assertThat(adapter.canPerformSearch()).isTrue()
        assertThat(adapter.getSearchHint()).isEqualTo("发现好看的小说")
    }

    @Test
    fun shouldShowHistoryToggle_returnsTrue_whenHistoryExceedsCollapsedLimit() {
        val adapter = SearchStateAdapter(
            MutableStateFlow(
                SearchState(
                    searchHistory = persistentListOf("a", "b", "c", "d"),
                    isHistoryExpanded = false,
                ),
            ),
        )

        assertThat(adapter.shouldShowMoreHistoryButton()).isTrue()
        assertThat(adapter.getHistoryToggleText()).contains("查看更多")
    }
}
