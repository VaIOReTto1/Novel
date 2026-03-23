package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class SearchResultStateAdapterTest {

    @Test
    fun canLoadMore_returnsTrue_whenHasMoreAndNotLoadingMore() {
        val adapter = SearchResultStateAdapter(
            MutableStateFlow(
                SearchResultState(
                    hasMore = true,
                    isLoadingMore = false,
                ),
            ),
        )

        assertThat(adapter.canLoadMore()).isTrue()
        assertThat(adapter.getResultSummary()).contains("0个结果")
    }

    @Test
    fun shouldShowEmptyState_returnsTrue_whenIdleAndNoBooks() {
        val adapter = SearchResultStateAdapter(
            MutableStateFlow(
                SearchResultState(
                    isLoading = false,
                    books = kotlinx.collections.immutable.persistentListOf(),
                ),
            ),
        )

        assertThat(adapter.shouldShowEmptyState()).isTrue()
    }
}
