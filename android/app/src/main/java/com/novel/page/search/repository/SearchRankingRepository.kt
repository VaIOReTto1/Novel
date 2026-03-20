package com.novel.page.search.repository

import com.novel.page.search.component.SearchRankingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal data class SearchRankingSource(
    val id: Long,
    val title: String,
    val author: String,
)

internal class SearchRankingRepository {

    suspend fun getNovelRanking(
        primary: suspend () -> List<SearchRankingSource>,
        fallback: suspend () -> List<SearchRankingSource>,
    ): ImmutableList<SearchRankingItem> {
        return loadRanking(
            primary = primary,
            fallback = fallback,
            fallbackTitle = "测试小说",
            fallbackAuthor = "测试作者",
        )
    }

    suspend fun getDramaRanking(
        primary: suspend () -> List<SearchRankingSource>,
        fallback: suspend () -> List<SearchRankingSource>,
    ): ImmutableList<SearchRankingItem> {
        return loadRanking(
            primary = primary,
            fallback = fallback,
            fallbackTitle = "热门短剧",
            fallbackAuthor = "短剧作者",
        )
    }

    suspend fun getNewBookRanking(
        primary: suspend () -> List<SearchRankingSource>,
        fallback: suspend () -> List<SearchRankingSource>,
    ): ImmutableList<SearchRankingItem> {
        return loadRanking(
            primary = primary,
            fallback = fallback,
            fallbackTitle = "新书推荐",
            fallbackAuthor = "新人作者",
        )
    }

    private suspend fun loadRanking(
        primary: suspend () -> List<SearchRankingSource>,
        fallback: suspend () -> List<SearchRankingSource>,
        fallbackTitle: String,
        fallbackAuthor: String,
    ): ImmutableList<SearchRankingItem> {
        val realData = try {
            try {
                primary()
            } catch (_: Exception) {
                fallback()
            }
        } catch (_: Exception) {
            emptyList()
        }.mapIndexed { index, item ->
            SearchRankingItem(
                id = item.id,
                title = item.title,
                author = item.author,
                rank = index + 1,
            )
        }

        if (realData.size >= 20) {
            return realData.toImmutableList()
        }

        val padded = realData.toMutableList()
        val startIndex = realData.size + 1
        for (rank in startIndex..20) {
            padded.add(
                SearchRankingItem(
                    id = 1_000L * rank,
                    title = "$fallbackTitle$rank",
                    author = "$fallbackAuthor$rank",
                    rank = rank,
                ),
            )
        }
        return padded.toImmutableList()
    }
}
