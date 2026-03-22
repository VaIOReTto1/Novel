package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class SearchCategoryFilterLoadCoordinatorTest {

    private val coordinator = SearchCategoryFilterLoadCoordinator()

    @Test
    fun shouldLoadCategoryFilters_returnsTrue_whenSearchStartsAndFiltersMissing() {
        val shouldLoad = coordinator.shouldLoadCategoryFilters(
            currentState = SearchResultState(),
            isLoading = false,
            trigger = SearchCategoryFilterLoadTrigger.SEARCH_STARTED,
        )

        assertThat(shouldLoad).isEqualTo(true)
    }

    @Test
    fun shouldLoadCategoryFilters_returnsTrue_whenFilterSheetOpensAndFiltersMissing() {
        val shouldLoad = coordinator.shouldLoadCategoryFilters(
            currentState = SearchResultState(),
            isLoading = false,
            trigger = SearchCategoryFilterLoadTrigger.FILTER_SHEET_OPENED,
        )

        assertThat(shouldLoad).isEqualTo(true)
    }

    @Test
    fun shouldLoadCategoryFilters_returnsFalse_whenFiltersAlreadyLoaded() {
        val shouldLoad = coordinator.shouldLoadCategoryFilters(
            currentState = SearchResultState(
                categoryFilters = persistentListOf(CategoryFilter(id = -1, name = "全部")),
            ),
            isLoading = false,
            trigger = SearchCategoryFilterLoadTrigger.SEARCH_STARTED,
        )

        assertThat(shouldLoad).isEqualTo(false)
    }

    @Test
    fun shouldLoadCategoryFilters_returnsFalse_whenLoadAlreadyInFlight() {
        val shouldLoad = coordinator.shouldLoadCategoryFilters(
            currentState = SearchResultState(),
            isLoading = true,
            trigger = SearchCategoryFilterLoadTrigger.FILTER_SHEET_OPENED,
        )

        assertThat(shouldLoad).isEqualTo(false)
    }
}
