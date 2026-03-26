package com.novel.page.search.viewmodel

import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.MviReducer
import com.novel.core.mvi.ReduceResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

class SearchResultReducer : MviReducer<SearchResultIntent, SearchResultState> {

    companion object {
        private const val TAG = "SearchResultReducer"
    }

    override fun reduce(currentState: SearchResultState, intent: SearchResultIntent): SearchResultState {
        return reduceWithEffect(currentState, intent).newState
    }

    fun reduceWithEffect(
        currentState: SearchResultState,
        intent: SearchResultIntent,
    ): ReduceResult<SearchResultState, SearchResultEffect> {
        CoreLogger.d(TAG, "Handling intent: ${intent::class.simpleName}")

        return when (intent) {
            is SearchResultIntent.UpdateQuery -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        query = intent.query,
                    ),
                )
            }
            is SearchResultIntent.PerformSearch -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null,
                        query = intent.query,
                        books = persistentListOf(),
                        totalResults = 0,
                        hasMore = false,
                        isLoadingMore = false,
                    ),
                )
            }
            is SearchResultIntent.SelectCategory -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        selectedCategoryId = intent.categoryId,
                    ),
                )
            }
            SearchResultIntent.OpenFilterSheet -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isFilterSheetOpen = true,
                    ),
                )
            }
            SearchResultIntent.CloseFilterSheet -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isFilterSheetOpen = false,
                    ),
                )
            }
            is SearchResultIntent.UpdateFilters -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        filters = intent.filters,
                    ),
                )
            }
            SearchResultIntent.ApplyFilters -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isFilterSheetOpen = false,
                        isLoading = true,
                        books = persistentListOf(),
                        totalResults = 0,
                        hasMore = false,
                    ),
                )
            }
            SearchResultIntent.ClearFilters -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        filters = FilterState(),
                    ),
                )
            }
            SearchResultIntent.LoadNextPage -> {
                if (currentState.isLoadingMore || !currentState.hasMore) {
                    ReduceResult(currentState)
                } else {
                    ReduceResult(
                        currentState.copy(
                            version = currentState.version + 1,
                            isLoadingMore = true,
                        ),
                    )
                }
            }
            is SearchResultIntent.NavigateToDetail -> {
                ReduceResult(currentState, SearchResultEffect.NavigateToDetail(intent.bookId))
            }
            SearchResultIntent.NavigateBack -> {
                ReduceResult(currentState, SearchResultEffect.NavigateBack)
            }
        }
    }

    fun handleSearchSuccess(
        currentState: SearchResultState,
        books: ImmutableList<BookInfoRespDto>,
        totalResults: Int,
        hasMore: Boolean,
        isLoadMore: Boolean = false,
    ): SearchResultState {
        return if (isLoadMore) {
            currentState.copy(
                version = currentState.version + 1,
                books = (currentState.books + books).toImmutableList(),
                hasMore = hasMore,
                isLoadingMore = false,
            )
        } else {
            currentState.copy(
                version = currentState.version + 1,
                isLoading = false,
                error = null,
                books = books,
                totalResults = totalResults,
                hasMore = hasMore,
                isLoadingMore = false,
            )
        }
    }

    fun handleSearchError(
        currentState: SearchResultState,
        errorMessage: String,
        isLoadMore: Boolean = false,
    ): SearchResultState {
        return if (isLoadMore) {
            currentState.copy(
                version = currentState.version + 1,
                isLoadingMore = false,
            )
        } else {
            currentState.copy(
                version = currentState.version + 1,
                isLoading = false,
                error = errorMessage,
                isLoadingMore = false,
            )
        }
    }

    fun handleCategoryFiltersLoaded(
        currentState: SearchResultState,
        categoryFilters: ImmutableList<CategoryFilter>,
    ): SearchResultState {
        return currentState.copy(
            version = currentState.version + 1,
            categoryFilters = categoryFilters,
        )
    }
}
