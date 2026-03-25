package com.novel.page.search.gateway

import androidx.compose.runtime.Stable
import com.novel.page.search.component.SearchRankingItem
import com.novel.page.search.repository.SearchParams
import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.page.search.viewmodel.CategoryFilter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface SearchHistoryGateway {
    suspend fun getSearchHistory(): List<String>
    suspend fun addSearchHistory(keyword: String)
    suspend fun toggleHistoryExpansion(currentState: Boolean): Boolean
}

interface SearchRankingGateway {
    suspend fun getRankingData(): SearchRankingData
}

interface SearchQueryGateway {
    suspend fun searchBooks(params: SearchParams): SearchQueryResult?
}

interface CategoryFilterGateway {
    suspend fun getCategoryFilters(): Result<List<CategoryFilter>>
}

@Stable
data class SearchRankingData(
    val novelRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    val dramaRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
    val newBookRanking: ImmutableList<SearchRankingItem> = persistentListOf(),
)

@Stable
data class SearchQueryResult(
    val list: ImmutableList<BookInfoRespDto> = persistentListOf(),
    val total: Long? = null,
    val pages: Long? = null,
)
