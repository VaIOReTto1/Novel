package com.novel.page.search.viewmodel

internal enum class SearchCategoryFilterLoadTrigger {
    SEARCH_STARTED,
    FILTER_SHEET_OPENED,
}

internal class SearchCategoryFilterLoadCoordinator {

    fun shouldLoadCategoryFilters(
        currentState: SearchResultState,
        isLoading: Boolean,
        trigger: SearchCategoryFilterLoadTrigger,
    ): Boolean {
        if (isLoading || currentState.categoryFilters.isNotEmpty()) {
            return false
        }

        return when (trigger) {
            SearchCategoryFilterLoadTrigger.SEARCH_STARTED,
            SearchCategoryFilterLoadTrigger.FILTER_SHEET_OPENED,
            -> true
        }
    }
}
