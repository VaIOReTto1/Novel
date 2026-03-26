package com.novel.page.home.viewmodel

object HomeStateProjector {

    fun canPerformSearch(state: HomeState): Boolean = state.searchQuery.isNotBlank() && !state.isLoading

    fun getSearchHint(state: HomeState): String = when {
        state.isLoading -> "加载中..."
        state.hasError -> "搜索失败，请重试"
        else -> "搜索您喜欢的小说"
    }

    fun canLoadMoreRecommend(state: HomeState): Boolean = when {
        state.isRecommendMode -> state.hasMoreHomeRecommend && !state.homeRecommendLoading
        else -> state.hasMoreRecommend && !state.recommendLoading
    }

    fun getLoadMoreText(state: HomeState): String = when {
        state.isRecommendMode && state.homeRecommendLoading -> "加载中..."
        !state.isRecommendMode && state.recommendLoading -> "加载中..."
        !canLoadMoreRecommend(state) -> "已加载全部"
        state.hasError -> "加载失败，点击重试"
        else -> "点击加载更多"
    }

    fun getHomeStatusSummary(state: HomeState): String = when {
        state.isLoading -> "加载中"
        state.hasError -> "加载失败"
        state.isRefreshing -> "刷新中"
        state.isEmpty -> "暂无数据"
        else -> "加载完成"
    }

    fun shouldShowEmptyState(state: HomeState): Boolean =
        !state.isLoading && !state.hasError && state.currentRecommendBooks.isEmpty() && state.rankBooks.isEmpty()

    fun shouldShowLoadMoreButton(state: HomeState): Boolean =
        canLoadMoreRecommend(state) && state.currentRecommendBooks.isNotEmpty()

    fun getRecommendModeText(state: HomeState): String =
        if (state.isRecommendMode) "首页推荐" else "分类推荐 - ${state.selectedCategoryFilter}"

    fun toHomeUiState(state: HomeState): HomeUiState {
        return HomeUiState(
            version = state.version,
            isLoading = state.isLoading,
            error = state.error,
            isRefreshing = state.isRefreshing,
            categories = state.categories,
            carouselBooks = state.carouselBooks,
            hotBooks = state.hotBooks,
            newBooks = state.newBooks,
            vipBooks = state.vipBooks,
            searchQuery = state.searchQuery,
            selectedCategoryFilter = state.selectedCategoryFilter,
            categoryFilters = state.categoryFilters,
            rankBooks = state.rankBooks,
            selectedRankType = state.selectedRankType,
            currentRecommendBooks = state.currentRecommendBooks,
            homeRecommendLoading = state.homeRecommendLoading,
            recommendLoading = state.recommendLoading,
            hasMoreRecommend = state.hasMoreRecommend,
            hasMoreHomeRecommend = state.hasMoreHomeRecommend,
            recommendPage = state.recommendPage,
            homeRecommendPage = state.homeRecommendPage,
            isRecommendMode = state.isRecommendMode,
        )
    }

    fun toScreenState(state: HomeState): HomeScreenState {
        return HomeScreenState(
            isLoading = state.isLoading,
            error = state.error,
            isRefreshing = state.isRefreshing,
            searchQuery = state.searchQuery,
            categories = state.categories,
            selectedCategoryFilter = state.selectedCategoryFilter,
            categoryFilters = state.categoryFilters,
            carouselBooks = state.carouselBooks,
            hotBooks = state.hotBooks,
            newBooks = state.newBooks,
            vipBooks = state.vipBooks,
            rankBooks = state.rankBooks,
            selectedRankType = state.selectedRankType,
            currentRecommendBooks = state.currentRecommendBooks,
            canPerformSearch = canPerformSearch(state),
            searchHint = getSearchHint(state),
            canLoadMoreRecommend = canLoadMoreRecommend(state),
            loadMoreText = getLoadMoreText(state),
            homeStatusSummary = getHomeStatusSummary(state),
            shouldShowEmptyState = shouldShowEmptyState(state),
            shouldShowLoadMoreButton = shouldShowLoadMoreButton(state),
            recommendModeText = getRecommendModeText(state),
            isRecommendMode = state.isRecommendMode,
        )
    }
}
