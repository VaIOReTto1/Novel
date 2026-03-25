package com.novel.page.search.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.search.gateway.SearchHistoryGateway
import com.novel.page.search.gateway.SearchRankingData
import com.novel.page.search.gateway.SearchRankingGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchHistoryGateway: SearchHistoryGateway,
    private val searchRankingGateway: SearchRankingGateway,
) : BaseMviViewModel<SearchIntent, SearchState, SearchEffect>() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MS = 300L
        private const val RANKING_CACHE_DURATION_MS = 5 * 60 * 1000L
    }

    private val reducer = SearchReducer()
    val adapter = SearchStateAdapter(state)

    @Stable
    private val searchQueryChannel = Channel<String>(Channel.UNLIMITED)
    @Stable
    private var searchJob: Job? = null

    private var cachedRankingData: SearchRankingData? = null
    private var rankingCacheTime: Long = 0L

    init {
        setupSearchDebounce()
    }

    override fun createInitialState(): SearchState = SearchState()

    override fun getReducer(): MviReducer<SearchIntent, SearchState> = reducer

    override fun onIntentProcessed(intent: SearchIntent, newState: SearchState) {
        when (intent) {
            SearchIntent.LoadInitialData -> handleLoadInitialData()
            is SearchIntent.UpdateSearchQuery -> searchQueryChannel.trySend(intent.query)
            is SearchIntent.PerformSearch -> handlePerformSearch(intent.query)
            SearchIntent.ToggleHistoryExpansion -> handleToggleHistoryExpansion()
            else -> Unit
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            searchQueryChannel.receiveAsFlow()
                .debounce(SEARCH_DEBOUNCE_DELAY_MS)
                .distinctUntilChanged()
                .collect { query ->
                    handleDebouncedSearchInput(query)
                }
        }
    }

    private fun handleDebouncedSearchInput(query: String) {
        if (query.length >= 2) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch { }
        }
    }

    private fun handleLoadInitialData() {
        viewModelScope.launch {
            runCatching {
                val history = searchHistoryGateway.getSearchHistory()
                val rankingData = if (isRankingCacheValid()) {
                    cachedRankingData!!
                } else {
                    searchRankingGateway.getRankingData().also {
                        cachedRankingData = it
                        rankingCacheTime = System.currentTimeMillis()
                    }
                }
                history to rankingData
            }.onSuccess { (history, rankingData) ->
                updateState(
                    reducer.handleLoadInitialDataSuccess(
                        currentState = getCurrentState(),
                        searchHistory = history.toImmutableList(),
                        novelRanking = rankingData.novelRanking,
                        dramaRanking = rankingData.dramaRanking,
                        newBookRanking = rankingData.newBookRanking,
                    ),
                )
            }.onFailure { error ->
                updateState(
                    reducer.handleLoadInitialDataError(
                        currentState = getCurrentState(),
                        errorMessage = "加载数据失败: ${error.message}",
                    ),
                )
            }
        }
    }

    private fun handlePerformSearch(query: String) {
        if (query.isBlank()) {
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            runCatching {
                searchHistoryGateway.addSearchHistory(query)
                searchHistoryGateway.getSearchHistory()
            }.onSuccess { updatedHistory ->
                updateState(
                    reducer.handleSearchHistoryUpdated(
                        currentState = getCurrentState(),
                        updatedHistory = updatedHistory,
                    ),
                )
            }.onFailure { error ->
                sendEffect(SearchEffect.ShowToast("搜索失败: ${error.message}"))
            }
        }
    }

    private fun handleToggleHistoryExpansion() {
        viewModelScope.launch {
            runCatching {
                searchHistoryGateway.toggleHistoryExpansion(getCurrentState().isHistoryExpanded)
            }.onSuccess { newExpansionState ->
                updateState(
                    reducer.handleHistoryExpansionPersisted(
                        currentState = getCurrentState(),
                        newExpansionState = newExpansionState,
                    ),
                )
            }.onFailure { error ->
                sendEffect(SearchEffect.ShowToast("操作失败: ${error.message}"))
            }
        }
    }

    private fun isRankingCacheValid(): Boolean {
        return cachedRankingData != null &&
            (System.currentTimeMillis() - rankingCacheTime) < RANKING_CACHE_DURATION_MS
    }
}
