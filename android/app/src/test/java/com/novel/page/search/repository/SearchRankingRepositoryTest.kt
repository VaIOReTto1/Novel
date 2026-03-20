package com.novel.page.search.repository

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.component.SearchRankingItem
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchRankingRepositoryTest {

    @Test
    fun getNovelRanking_usesPrimaryDataWithoutPaddingMockRows() {
        runBlocking {
            val ranking = SearchRankingRepository().getNovelRanking(
                primary = {
                    listOf(
                        SearchRankingSource(1, "Novel-1", "Author-1"),
                        SearchRankingSource(2, "Novel-2", "Author-2"),
                    )
                },
                fallback = { error("fallback should not be called") },
            )

            assertThat(ranking).hasSize(2)
            assertThat(ranking.first()).isEqualTo(
                SearchRankingItem(id = 1, title = "Novel-1", author = "Author-1", rank = 1),
            )
        }
    }

    @Test
    fun getDramaRanking_usesFallbackWhenPrimaryFails() {
        runBlocking {
            val ranking = SearchRankingRepository().getDramaRanking(
                primary = { error("primary failed") },
                fallback = {
                    listOf(
                        SearchRankingSource(10, "Drama-1", "Writer-1"),
                    )
                },
            )

            assertThat(ranking.first()).isEqualTo(
                SearchRankingItem(id = 10, title = "Drama-1", author = "Writer-1", rank = 1),
            )
        }
    }

    @Test
    fun getNewBookRanking_returnsEmptyListWhenBothSourcesFail() {
        runBlocking {
            val ranking = SearchRankingRepository().getNewBookRanking(
                primary = { error("primary failed") },
                fallback = { error("fallback failed") },
            )

            assertThat(ranking).isEmpty()
        }
    }
}
