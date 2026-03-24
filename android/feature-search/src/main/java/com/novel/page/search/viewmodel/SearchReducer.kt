package com.novel.page.search.viewmodel

import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.MviReducer
import com.novel.core.mvi.ReduceResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import com.novel.page.search.component.SearchRankingItem

class SearchReducer : MviReducer<SearchIntent, SearchState> {

    companion object {
        private const val TAG = "SearchReducer"
    }

    override fun reduce(currentState: SearchState, intent: SearchIntent): SearchState {
        val result = reduceWithEffect(currentState, intent)
        return result.newState
    }

    fun reduceWithEffect(
        currentState: SearchState,
        intent: SearchIntent
    ): ReduceResult<SearchState, SearchEffect> {
        CoreLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")

        return when (intent) {
            is SearchIntent.LoadInitialData -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isLoading = true,
                        error = null
                    )
                )
            }

            is SearchIntent.UpdateSearchQuery -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        searchQuery = intent.query
                    )
                )
            }

            is SearchIntent.PerformSearch -> {
                if (intent.query.isBlank()) {
                    ReduceResult(currentState, SearchEffect.ShowToast("请输入搜索关键词"))
                } else {
                    ReduceResult(currentState, SearchEffect.NavigateToSearchResult(intent.query.trim()))
                }
            }

            is SearchIntent.ToggleHistoryExpansion -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        isHistoryExpanded = !currentState.isHistoryExpanded
                    )
                )
            }

            is SearchIntent.NavigateToBookDetail -> {
                ReduceResult(currentState, SearchEffect.NavigateToBookDetail(intent.bookId))
            }

            is SearchIntent.NavigateBack -> {
                ReduceResult(currentState, SearchEffect.NavigateBack)
            }

            is SearchIntent.ClearError -> {
                ReduceResult(
                    currentState.copy(
                        version = currentState.version + 1,
                        error = null
                    )
                )
            }
        }
    }

    fun handleLoadInitialDataSuccess(
        currentState: SearchState,
        searchHistory: ImmutableList<String>,
        novelRanking: ImmutableList<SearchRankingItem>,
        dramaRanking: ImmutableList<SearchRankingItem>,
        newBookRanking: ImmutableList<SearchRankingItem>
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = null,
            searchHistory = searchHistory,
            novelRanking = novelRanking,
            dramaRanking = dramaRanking,
            newBookRanking = newBookRanking,
            rankingLoading = false
        )
    }

    fun handleLoadInitialDataError(
        currentState: SearchState,
        errorMessage: String
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = errorMessage,
            rankingLoading = false
        )
    }

    fun handleSearchHistoryUpdated(
        currentState: SearchState,
        updatedHistory: List<String>
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            searchHistory = updatedHistory.toImmutableList()
        )
    }

    fun handleHistoryExpansionPersisted(
        currentState: SearchState,
        newExpansionState: Boolean
    ): SearchState {
        return currentState.copy(
            version = currentState.version + 1,
            isHistoryExpanded = newExpansionState
        )
    }
}
