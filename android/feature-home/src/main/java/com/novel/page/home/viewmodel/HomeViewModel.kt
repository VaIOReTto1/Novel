package com.novel.page.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.home.gateway.HomeFeedGateway
import com.novel.page.home.gateway.HomeRnSyncGateway
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeFeedGateway: HomeFeedGateway,
    private val homeRnSyncGateway: HomeRnSyncGateway,
) : BaseMviViewModel<HomeIntent, HomeState, HomeEffect>() {

    companion object {
    private const val RECOMMEND_PAGE_SIZE = 8
    }

    private val reducer = HomeReducer()
    val adapter = HomeStateAdapter(state)

    private var cachedHomeRecommendBooks: ImmutableList<HomeRecommendItem> = persistentListOf()

    init {
        sendIntent(HomeIntent.LoadInitialData)
        viewModelScope.launch {
            runCatching { homeRnSyncGateway.sync() }
        }
    }

    override fun createInitialState(): HomeState = HomeState()

    override fun getReducer(): MviReducer<HomeIntent, HomeState> {
        return object : MviReducer<HomeIntent, HomeState> {
            override fun reduce(currentState: HomeState, intent: HomeIntent): HomeState {
                return reducer.reduce(currentState, intent).newState
            }
        }
    }

    override fun onIntentProcessed(intent: HomeIntent, newState: HomeState) {
        when (intent) {
            HomeIntent.LoadInitialData -> loadInitialData()
            HomeIntent.RefreshData -> refreshData()
            is HomeIntent.SelectCategoryFilter -> selectCategoryFilter(intent.categoryName)
            is HomeIntent.SelectRankType -> selectRankType(intent.rankType)
            HomeIntent.LoadMoreRecommend -> loadMoreCategoryRecommend()
            HomeIntent.LoadMoreHomeRecommend -> loadMoreHomeRecommend()
            HomeIntent.RestoreData -> restoreDataIfNeeded()
            else -> Unit
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            runCatching {
                homeFeedGateway.loadInitialData()
            }.onSuccess { snapshot ->
                cachedHomeRecommendBooks = snapshot.allHomeRecommendBooks
                val firstPage = cachedHomeRecommendBooks.take(RECOMMEND_PAGE_SIZE).toImmutableList()
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        isLoading = false,
                        error = null,
                        categories = snapshot.categories,
                        categoryFilters = HomeCategoryFilterSupport.normalizeFilters(snapshot.categoryFilters),
                        carouselBooks = snapshot.carouselBooks,
                        hotBooks = snapshot.hotBooks,
                        newBooks = snapshot.newBooks,
                        vipBooks = snapshot.vipBooks,
                        rankBooks = snapshot.rankBooks,
                        currentRecommendBooks = firstPage,
                        isRecommendMode = true,
                        hasMoreHomeRecommend = cachedHomeRecommendBooks.size > firstPage.size,
                        homeRecommendPage = 1,
                        homeRecommendLoading = false,
                    ),
                )
            }.onFailure { error ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        isLoading = false,
                        error = error.message ?: "加载失败",
                    ),
                )
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            runCatching {
                homeFeedGateway.refreshData()
            }.onSuccess { snapshot ->
                cachedHomeRecommendBooks = snapshot.allHomeRecommendBooks
                val firstPage = cachedHomeRecommendBooks.take(RECOMMEND_PAGE_SIZE).toImmutableList()
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        isRefreshing = false,
                        isLoading = false,
                        error = null,
                        categories = snapshot.categories,
                        categoryFilters = HomeCategoryFilterSupport.normalizeFilters(snapshot.categoryFilters),
                        carouselBooks = snapshot.carouselBooks,
                        hotBooks = snapshot.hotBooks,
                        newBooks = snapshot.newBooks,
                        vipBooks = snapshot.vipBooks,
                        rankBooks = snapshot.rankBooks,
                        currentRecommendBooks = firstPage,
                        isRecommendMode = true,
                        hasMoreHomeRecommend = cachedHomeRecommendBooks.size > firstPage.size,
                        homeRecommendPage = 1,
                        homeRecommendLoading = false,
                    ),
                )
                sendEffect(HomeEffect.ShowToast("刷新成功"))
            }.onFailure { error ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        isRefreshing = false,
                        isLoading = false,
                        error = error.message ?: "刷新失败",
                    ),
                )
                sendEffect(HomeEffect.ShowToast("刷新失败"))
            }
        }
    }

    private fun selectCategoryFilter(categoryName: String) {
        val normalized = HomeCategoryFilterSupport.normalizeSelectedFilter(categoryName)
        if (HomeCategoryFilterSupport.isHomeFilter(normalized)) {
            val firstPage = cachedHomeRecommendBooks.take(RECOMMEND_PAGE_SIZE).toImmutableList()
            updateState(
                getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    selectedCategoryFilter = normalized,
                    isRecommendMode = true,
                    currentRecommendBooks = firstPage,
                    hasMoreHomeRecommend = cachedHomeRecommendBooks.size > firstPage.size,
                    homeRecommendPage = 1,
                    recommendPage = 1,
                ),
            )
            return
        }

        viewModelScope.launch {
            runCatching {
                homeFeedGateway.loadCategoryRecommendBooks(
                    categoryName = normalized,
                    categoryFilters = getCurrentState().categoryFilters,
                    pageNum = 1,
                    pageSize = RECOMMEND_PAGE_SIZE,
                )
            }.onSuccess { page ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        selectedCategoryFilter = normalized,
                        isRecommendMode = false,
                        currentRecommendBooks = page.items,
                        hasMoreRecommend = page.hasMore,
                        recommendPage = 1,
                        recommendLoading = false,
                    ),
                )
            }.onFailure { error ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        error = error.message ?: "加载分类数据失败",
                        recommendLoading = false,
                    ),
                )
            }
        }
    }

    private fun selectRankType(rankType: String) {
        viewModelScope.launch {
            runCatching {
                homeFeedGateway.loadRankBooks(rankType)
            }.onSuccess { books ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        selectedRankType = rankType,
                        rankBooks = books,
                    ),
                )
            }.onFailure { error ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        error = error.message ?: "加载榜单失败",
                    ),
                )
            }
        }
    }

    private fun loadMoreCategoryRecommend() {
        val state = getCurrentState()
        if (state.isRecommendMode || !state.hasMoreRecommend) {
            return
        }
        viewModelScope.launch {
            runCatching {
                homeFeedGateway.loadCategoryRecommendBooks(
                    categoryName = state.selectedCategoryFilter,
                    categoryFilters = state.categoryFilters,
                    pageNum = state.recommendPage + 1,
                    pageSize = RECOMMEND_PAGE_SIZE,
                )
            }.onSuccess { page ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        currentRecommendBooks = (getCurrentState().currentRecommendBooks + page.items).toImmutableList(),
                        hasMoreRecommend = page.hasMore,
                        recommendPage = getCurrentState().recommendPage + 1,
                        recommendLoading = false,
                    ),
                )
            }.onFailure { error ->
                updateState(
                    getCurrentState().copy(
                        version = getCurrentState().version + 1,
                        recommendLoading = false,
                        error = error.message ?: "加载更多失败",
                    ),
                )
            }
        }
    }

    private fun loadMoreHomeRecommend() {
        val state = getCurrentState()
        if (!state.isRecommendMode || !state.hasMoreHomeRecommend) {
            return
        }

        viewModelScope.launch {
            val resolvedCache = if (cachedHomeRecommendBooks.isNotEmpty()) {
                cachedHomeRecommendBooks
            } else {
                homeFeedGateway.loadAllHomeRecommendBooks(refresh = false)
            }
            cachedHomeRecommendBooks = resolvedCache

            val nextPage = state.homeRecommendPage + 1
            val endIndex = nextPage * RECOMMEND_PAGE_SIZE
            val items = resolvedCache.take(endIndex).toImmutableList()
            updateState(
                state.copy(
                    version = state.version + 1,
                    currentRecommendBooks = items,
                    homeRecommendPage = nextPage,
                    hasMoreHomeRecommend = resolvedCache.size > items.size,
                    homeRecommendLoading = false,
                ),
            )
        }
    }

    private fun restoreDataIfNeeded() {
        val state = getCurrentState()
        if (!state.isLoading && state.currentRecommendBooks.isEmpty() && state.rankBooks.isEmpty()) {
            loadInitialData()
        }
    }
}
