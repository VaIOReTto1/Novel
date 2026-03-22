package com.novel.page.home.viewmodel

data class HomeRestoreSnapshot(
    val categoryFiltersCount: Int,
    val categoryFiltersResolved: Boolean,
    val rankBooksCount: Int,
    val rankResolved: Boolean,
    val isRecommendMode: Boolean,
    val homeRecommendCount: Int,
    val homeRecommendResolved: Boolean,
    val homeRecommendNetworkRetryConsumed: Boolean,
    val initialLoadCompleted: Boolean,
    val isLoading: Boolean,
    val isRefreshing: Boolean,
    val hasError: Boolean,
)

data class HomeRestoreOutcome(
    val shouldLoadCategoryFilters: Boolean,
    val shouldLoadRankBooks: Boolean,
    val shouldLoadHomeRecommend: Boolean,
    val shouldReloadHomeRecommendFromNetwork: Boolean,
)

class HomeRestoreCoordinator {

    fun coordinate(snapshot: HomeRestoreSnapshot): HomeRestoreOutcome {
        if (snapshot.isLoading || snapshot.isRefreshing || snapshot.hasError) {
            return HomeRestoreOutcome(
                shouldLoadCategoryFilters = false,
                shouldLoadRankBooks = false,
                shouldLoadHomeRecommend = false,
                shouldReloadHomeRecommendFromNetwork = false,
            )
        }

        val shouldLoadCategoryFilters =
            snapshot.categoryFiltersCount <= 1

        val shouldLoadRankBooks =
            !snapshot.rankResolved || snapshot.rankBooksCount == 0

        val shouldLoadHomeRecommend =
            snapshot.isRecommendMode &&
                !snapshot.homeRecommendResolved

        val shouldReloadHomeRecommendFromNetwork =
            snapshot.isRecommendMode &&
                snapshot.initialLoadCompleted &&
                snapshot.homeRecommendResolved &&
                snapshot.homeRecommendCount == 0 &&
                !snapshot.homeRecommendNetworkRetryConsumed

        return HomeRestoreOutcome(
            shouldLoadCategoryFilters = shouldLoadCategoryFilters,
            shouldLoadRankBooks = shouldLoadRankBooks,
            shouldLoadHomeRecommend = shouldLoadHomeRecommend,
            shouldReloadHomeRecommendFromNetwork = shouldReloadHomeRecommendFromNetwork,
        )
    }
}
