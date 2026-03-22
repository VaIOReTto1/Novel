package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeRestoreCoordinatorTest {

    @Test
    fun coordinate_requestsNetworkReload_whenInitialLoadFinishedButHomeRecommendStillEmpty() {
        val outcome = HomeRestoreCoordinator().coordinate(
            HomeRestoreSnapshot(
                categoryFiltersCount = 1,
                categoryFiltersResolved = true,
                rankBooksCount = 0,
                rankResolved = false,
                isRecommendMode = true,
                homeRecommendCount = 0,
                homeRecommendResolved = true,
                homeRecommendNetworkRetryConsumed = false,
                initialLoadCompleted = true,
                isLoading = false,
                isRefreshing = false,
                hasError = false,
            ),
        )

        assertThat(outcome.shouldLoadCategoryFilters).isTrue()
        assertThat(outcome.shouldLoadRankBooks).isTrue()
        assertThat(outcome.shouldLoadHomeRecommend).isFalse()
        assertThat(outcome.shouldReloadHomeRecommendFromNetwork).isTrue()
    }

    @Test
    fun coordinate_keepsIdleState_whenAllCoreDataResolved() {
        val outcome = HomeRestoreCoordinator().coordinate(
            HomeRestoreSnapshot(
                categoryFiltersCount = 2,
                categoryFiltersResolved = true,
                rankBooksCount = 1,
                rankResolved = true,
                isRecommendMode = true,
                homeRecommendCount = 3,
                homeRecommendResolved = true,
                homeRecommendNetworkRetryConsumed = false,
                initialLoadCompleted = true,
                isLoading = false,
                isRefreshing = false,
                hasError = false,
            ),
        )

        assertThat(outcome.shouldLoadCategoryFilters).isFalse()
        assertThat(outcome.shouldLoadRankBooks).isFalse()
        assertThat(outcome.shouldLoadHomeRecommend).isFalse()
        assertThat(outcome.shouldReloadHomeRecommendFromNetwork).isFalse()
    }
}
