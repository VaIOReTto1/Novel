package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.component.SearchRankingItem
import com.novel.page.search.gateway.SearchHistoryGateway
import com.novel.page.search.gateway.SearchRankingData
import com.novel.page.search.gateway.SearchRankingGateway
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load initial data hydrates history and rankings`() = runTest(dispatcher) {
        val viewModel = SearchViewModel(
            searchHistoryGateway = FakeSearchHistoryGateway(listOf("keyword")),
            searchRankingGateway = FakeSearchRankingGateway(),
        )

        viewModel.sendIntent(SearchIntent.LoadInitialData)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.searchHistory).containsExactly("keyword")
        assertThat(state.novelRanking).hasSize(1)
    }

    private class FakeSearchHistoryGateway(
        private val history: List<String>,
    ) : SearchHistoryGateway {
        override suspend fun getSearchHistory(): List<String> = history
        override suspend fun addSearchHistory(keyword: String) = Unit
        override suspend fun toggleHistoryExpansion(currentState: Boolean): Boolean = !currentState
    }

    private class FakeSearchRankingGateway : SearchRankingGateway {
        override suspend fun getRankingData(): SearchRankingData {
            return SearchRankingData(
                novelRanking = persistentListOf(SearchRankingItem(1L, "Novel", "Author", 1)),
            )
        }
    }
}
