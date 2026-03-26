package com.novel.page.home.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.novel.core.adapter.StateAdapter
import com.novel.page.home.dao.HomeBookEntity
import com.novel.page.home.dao.HomeCategoryEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.StateFlow

class HomeStateAdapter(
    stateFlow: StateFlow<HomeState>,
) : StateAdapter<HomeState>(stateFlow) {

    @Composable
    fun isRefreshingState(): State<Boolean> = createStableState { it.isRefreshing }

    @Composable
    fun searchQueryState(): State<String> = createStableState { it.searchQuery }

    @Composable
    fun categoriesState(): State<ImmutableList<HomeCategoryEntity>> = createStableState { it.categories }

    @Composable
    fun selectedCategoryFilterState(): State<String> = createStableState { it.selectedCategoryFilter }

    @Composable
    fun categoryFiltersState(): State<ImmutableList<CategoryInfo>> = createStableState { it.categoryFilters }

    @Composable
    fun carouselBooksState(): State<ImmutableList<HomeBookEntity>> = createStableState { it.carouselBooks }

    @Composable
    fun hotBooksState(): State<ImmutableList<HomeBookEntity>> = createStableState { it.hotBooks }

    @Composable
    fun newBooksState(): State<ImmutableList<HomeBookEntity>> = createStableState { it.newBooks }

    @Composable
    fun vipBooksState(): State<ImmutableList<HomeBookEntity>> = createStableState { it.vipBooks }

    @Composable
    fun rankBooksState(): State<ImmutableList<HomeRankBook>> = createStableState { it.rankBooks }

    @Composable
    fun selectedRankTypeState(): State<String> = createStableState { it.selectedRankType }

    @Composable
    fun currentRecommendBooksState(): State<ImmutableList<RecommendItem>> =
        createStableState { it.currentRecommendBooks }

    @Composable
    fun isRecommendModeState(): State<Boolean> = createStableState { it.isRecommendMode }

    @Composable
    fun homeRecommendLoadingState(): State<Boolean> = createStableState { it.homeRecommendLoading }

    @Composable
    fun recommendLoadingState(): State<Boolean> = createStableState { it.recommendLoading }

    @Composable
    fun hasMoreRecommendState(): State<Boolean> = createStableState { it.hasMoreRecommend }

    @Composable
    fun hasMoreHomeRecommendState(): State<Boolean> = createStableState { it.hasMoreHomeRecommend }

    fun toHomeUiState(): HomeUiState = HomeStateProjector.toHomeUiState(getCurrentSnapshot())
}

data class HomeScreenState(
    val isLoading: Boolean,
    val error: String?,
    val isRefreshing: Boolean,
    val searchQuery: String,
    val categories: ImmutableList<HomeCategoryEntity>,
    val selectedCategoryFilter: String,
    val categoryFilters: ImmutableList<CategoryInfo>,
    val carouselBooks: ImmutableList<HomeBookEntity>,
    val hotBooks: ImmutableList<HomeBookEntity>,
    val newBooks: ImmutableList<HomeBookEntity>,
    val vipBooks: ImmutableList<HomeBookEntity>,
    val rankBooks: ImmutableList<HomeRankBook>,
    val selectedRankType: String,
    val currentRecommendBooks: ImmutableList<RecommendItem>,
    val canPerformSearch: Boolean,
    val searchHint: String,
    val canLoadMoreRecommend: Boolean,
    val loadMoreText: String,
    val homeStatusSummary: String,
    val shouldShowEmptyState: Boolean,
    val shouldShowLoadMoreButton: Boolean,
    val recommendModeText: String,
    val isRecommendMode: Boolean,
)

data class HomeUiState(
    val version: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRefreshing: Boolean = false,
    val categories: ImmutableList<HomeCategoryEntity> = persistentListOf(),
    val carouselBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val hotBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val newBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val vipBooks: ImmutableList<HomeBookEntity> = persistentListOf(),
    val searchQuery: String = "",
    val selectedCategoryFilter: String = HomeCategoryFilterSupport.HOME_FILTER_LABEL,
    val categoryFilters: ImmutableList<CategoryInfo> =
        persistentListOf(HomeCategoryFilterSupport.homeCategoryFilter()),
    val rankBooks: ImmutableList<HomeRankBook> = persistentListOf(),
    val selectedRankType: String = "点击榜",
    val currentRecommendBooks: ImmutableList<RecommendItem> = persistentListOf(),
    val homeRecommendLoading: Boolean = false,
    val recommendLoading: Boolean = false,
    val hasMoreRecommend: Boolean = true,
    val hasMoreHomeRecommend: Boolean = true,
    val recommendPage: Int = 1,
    val homeRecommendPage: Int = 1,
    val isRecommendMode: Boolean = true,
)
