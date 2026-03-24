package com.novel.page.search.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import com.novel.page.search.component.SearchRankingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.StateFlow

@Stable
class SearchStateAdapter(
    stateFlow: StateFlow<SearchState>
) : StateAdapter<SearchState>(stateFlow) {

    @Composable
    fun searchQueryState(): State<String> =
        createStableState { it.searchQuery }

    @Composable
    fun searchHistoryState(): State<ImmutableList<String>> =
        createStableState { it.searchHistory }

    @Composable
    fun isHistoryExpandedState(): State<Boolean> =
        createStableState { it.isHistoryExpanded }

    @Composable
    fun hasSearchHistoryState(): State<Boolean> =
        createStableState { it.searchHistory.isNotEmpty() }

    @Composable
    fun displayedSearchHistoryState(): State<ImmutableList<String>> =
        createStableState {
            if (it.isHistoryExpanded) it.searchHistory else it.searchHistory.take(3).toImmutableList()
        }

    @Composable
    fun novelRankingState(): State<ImmutableList<SearchRankingItem>> =
        createStableState { it.novelRanking }

    @Composable
    fun dramaRankingState(): State<ImmutableList<SearchRankingItem>> =
        createStableState { it.dramaRanking }

    @Composable
    fun newBookRankingState(): State<ImmutableList<SearchRankingItem>> =
        createStableState { it.newBookRanking }

    @Composable
    fun rankingLoadingState(): State<Boolean> =
        createStableState { it.rankingLoading }

    @Composable
    fun hasRankingDataState(): State<Boolean> =
        createStableState {
            it.novelRanking.isNotEmpty() || it.dramaRanking.isNotEmpty() || it.newBookRanking.isNotEmpty()
        }

    @Composable
    fun allRankingDataState(): State<PersistentList<RankingSection>> =
        createStableState {
            persistentListOf<RankingSection>().builder().apply {
                if (it.novelRanking.isNotEmpty()) add(RankingSection("小说榜", it.novelRanking))
                if (it.dramaRanking.isNotEmpty()) add(RankingSection("剧本榜", it.dramaRanking))
                if (it.newBookRanking.isNotEmpty()) add(RankingSection("新书榜", it.newBookRanking))
            }.build()
        }

    fun shouldShowMoreHistoryButton(): Boolean {
        val state = getCurrentSnapshot()
        return state.searchHistory.size > 3 && !state.isHistoryExpanded
    }

    fun shouldShowLessHistoryButton(): Boolean {
        val state = getCurrentSnapshot()
        return state.searchHistory.size > 3 && state.isHistoryExpanded
    }

    fun getHistoryToggleText(): String {
        val state = getCurrentSnapshot()
        return if (state.isHistoryExpanded) {
            "收起"
        } else {
            "查看更多 (${state.searchHistory.size - 3})"
        }
    }

    fun canPerformSearch(): Boolean {
        val state = getCurrentSnapshot()
        return state.searchQuery.isNotBlank() && !state.isLoading
    }

    fun getSearchHint(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "搜索中..."
            state.hasError -> "搜索失败，请重试"
            state.searchHistory.isNotEmpty() -> "搜索小说、作者"
            else -> "发现好看的小说"
        }
    }

    fun getHistoryCountText(): String {
        val count = getCurrentSnapshot().searchHistory.size
        return when {
            count == 0 -> "暂无搜索历史"
            count <= 3 -> "$count 条搜索历史"
            else -> "共 $count 条搜索历史"
        }
    }

    fun shouldShowRankingContent(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isLoading && !state.hasError && (
            state.novelRanking.isNotEmpty() ||
                state.dramaRanking.isNotEmpty() ||
                state.newBookRanking.isNotEmpty()
            )
    }

    fun getRankingStatusText(): String {
        val state = getCurrentSnapshot()
        val hasRanking = state.novelRanking.isNotEmpty() ||
            state.dramaRanking.isNotEmpty() ||
            state.newBookRanking.isNotEmpty()
        return when {
            state.rankingLoading -> "加载榜单中..."
            state.hasError -> "榜单加载失败"
            hasRanking -> "榜单加载完成"
            else -> "暂无榜单数据"
        }
    }
}

@Stable
data class RankingSection(
    val title: String,
    val items: ImmutableList<SearchRankingItem>
)

fun StateFlow<SearchState>.asSearchAdapter(): SearchStateAdapter {
    return SearchStateAdapter(this)
}

@Stable
data class SearchScreenState(
    val isLoading: Boolean,
    val error: String?,
    val searchQuery: String,
    val displayedHistory: PersistentList<String>,
    val rankingSections: PersistentList<RankingSection>,
    val canPerformSearch: Boolean,
    val searchHint: String,
    val shouldShowHistoryToggle: Boolean,
    val historyToggleText: String,
    val historyCountText: String,
    val shouldShowRanking: Boolean,
    val rankingStatusText: String
)

fun SearchStateAdapter.toScreenState(): SearchScreenState {
    val snapshot = getCurrentSnapshot()
    return SearchScreenState(
        isLoading = snapshot.isLoading,
        error = snapshot.error,
        searchQuery = snapshot.searchQuery,
        displayedHistory = if (snapshot.isHistoryExpanded) {
            snapshot.searchHistory.toPersistentList()
        } else {
            snapshot.searchHistory.take(3).toPersistentList()
        },
        rankingSections = buildList {
            if (snapshot.novelRanking.isNotEmpty()) add(RankingSection("小说榜", snapshot.novelRanking))
            if (snapshot.dramaRanking.isNotEmpty()) add(RankingSection("剧本榜", snapshot.dramaRanking))
            if (snapshot.newBookRanking.isNotEmpty()) add(RankingSection("新书榜", snapshot.newBookRanking))
        }.toPersistentList(),
        canPerformSearch = canPerformSearch(),
        searchHint = getSearchHint(),
        shouldShowHistoryToggle = shouldShowMoreHistoryButton() || shouldShowLessHistoryButton(),
        historyToggleText = getHistoryToggleText(),
        historyCountText = getHistoryCountText(),
        shouldShowRanking = shouldShowRankingContent(),
        rankingStatusText = getRankingStatusText()
    )
}
