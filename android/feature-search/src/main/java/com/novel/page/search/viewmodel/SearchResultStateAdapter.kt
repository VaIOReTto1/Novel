package com.novel.page.search.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

@Stable
class SearchResultStateAdapter(
    stateFlow: StateFlow<SearchResultState>
) : StateAdapter<SearchResultState>(stateFlow) {

    @Composable
    fun queryState(): State<String> =
        createStableState { it.query }

    @Composable
    fun booksState(): State<ImmutableList<BookInfoRespDto>> =
        createStableState { it.books }

    @Composable
    fun totalResultsState(): State<Int> =
        createStableState { it.totalResults }

    @Composable
    fun hasMoreState(): State<Boolean> =
        createStableState { it.hasMore }

    @Composable
    fun selectedCategoryIdState(): State<Int?> =
        createStableState { it.selectedCategoryId }

    @Composable
    fun categoryFiltersState(): State<ImmutableList<CategoryFilter>> =
        createStableState { it.categoryFilters }

    @Composable
    fun filtersState(): State<FilterState> =
        createStableState { it.filters }

    @Composable
    fun isFilterSheetOpenState(): State<Boolean> =
        createStableState { it.isFilterSheetOpen }

    @Composable
    fun isLoadingMoreState(): State<Boolean> =
        createStableState { it.isLoadingMore }

    fun canLoadMore(): Boolean {
        val state = getCurrentSnapshot()
        return state.hasMore && !state.isLoadingMore && !state.isLoading
    }

    fun hasResults(): Boolean = getCurrentSnapshot().books.isNotEmpty()

    fun shouldShowEmptyState(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isLoading && state.books.isEmpty() && !state.hasError
    }

    fun shouldShowLoadingMoreIndicator(): Boolean = getCurrentSnapshot().isLoadingMore

    fun shouldShowFilterSheet(): Boolean = getCurrentSnapshot().isFilterSheetOpen

    fun getResultSummary(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "搜索中..."
            state.hasError -> "搜索失败"
            state.books.isEmpty() -> "0个结果"
            else -> "${state.totalResults}个结果"
        }
    }

    fun getSelectedCategoryName(): String {
        val state = getCurrentSnapshot()
        return state.categoryFilters.find { it.id == state.selectedCategoryId }?.name ?: "全部"
    }
}
