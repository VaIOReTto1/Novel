package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.page.search.component.SearchRankingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class SearchIntent : MviIntent {
    data object LoadInitialData : SearchIntent()
    data class UpdateSearchQuery(val query: String) : SearchIntent()
    data class PerformSearch(val query: String) : SearchIntent()
    data object ToggleHistoryExpansion : SearchIntent()
    data class NavigateToBookDetail(val bookId: Long) : SearchIntent()
    data object NavigateBack : SearchIntent()
    data object ClearError : SearchIntent()
}

@Stable
data class SearchState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val searchQuery: String = "",
    val searchHistory: ImmutableList<String> = persistentListOf(),
    val isHistoryExpanded: Boolean = false,
    val novelRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    val dramaRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    val newBookRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    val rankingLoading: Boolean = false
) : MviState {
    override val isEmpty: Boolean
        get() = searchHistory.isEmpty() &&
            novelRanking.isEmpty() &&
            dramaRanking.isEmpty() &&
            newBookRanking.isEmpty()
}

sealed class SearchEffect : MviEffect {
    data class NavigateToBookDetail(val bookId: Long) : SearchEffect()
    data class NavigateToSearchResult(val query: String) : SearchEffect()
    data object NavigateBack : SearchEffect()
    data class ShowToast(val message: String) : SearchEffect()
}
