package com.novel.page.search.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.repository.SearchParams
import org.junit.Test

class SearchRetryPolicyCoordinatorTest {

    private val coordinator = SearchRetryPolicyCoordinator()

    @Test
    fun shouldRetry_returnsTrue_forNonLoadMoreSearchWithinLimit() {
        val shouldRetry = coordinator.shouldRetry(
            params = createParams(
                triggerSource = SearchTriggerSource.INITIAL_ENTRY,
                isLoadMore = false,
            ),
            retryAttempts = 1,
            maxRetryAttempts = 3,
        )

        assertThat(shouldRetry).isTrue()
    }

    @Test
    fun shouldRetry_returnsFalse_forLoadMoreEvenWithinLimit() {
        val shouldRetry = coordinator.shouldRetry(
            params = createParams(
                triggerSource = SearchTriggerSource.LOAD_MORE,
                isLoadMore = true,
            ),
            retryAttempts = 1,
            maxRetryAttempts = 3,
        )

        assertThat(shouldRetry).isFalse()
    }

    @Test
    fun shouldRetry_returnsTrue_forUserRetryWithinLimit() {
        val shouldRetry = coordinator.shouldRetry(
            params = createParams(
                triggerSource = SearchTriggerSource.USER_RETRY,
                isLoadMore = false,
            ),
            retryAttempts = 2,
            maxRetryAttempts = 3,
        )

        assertThat(shouldRetry).isTrue()
    }

    @Test
    fun shouldRetry_returnsFalse_forFilterApplyBeyondLimit() {
        val shouldRetry = coordinator.shouldRetry(
            params = createParams(
                triggerSource = SearchTriggerSource.FILTER_APPLY,
                isLoadMore = false,
            ),
            retryAttempts = 4,
            maxRetryAttempts = 3,
        )

        assertThat(shouldRetry).isFalse()
    }

    @Test
    fun createRetryParams_marksManualRetryTrigger_forNonLoadMoreSearch() {
        val retryParams = coordinator.createRetryParams(
            createParams(
                triggerSource = SearchTriggerSource.INITIAL_ENTRY,
                isLoadMore = false,
            ),
        )

        assertThat(retryParams.triggerSource).isEqualTo(SearchTriggerSource.USER_RETRY)
        assertThat(retryParams.page).isEqualTo(1)
    }

    @Test
    fun retryDelayMs_scalesLinearlyWithAttemptCount() {
        assertThat(
            coordinator.retryDelayMs(
                retryAttempts = 2,
                baseDelayMs = 1000L,
            ),
        ).isEqualTo(2000L)
    }

    private fun createParams(
        triggerSource: SearchTriggerSource,
        isLoadMore: Boolean,
    ): SearchParams {
        return SearchParams(
            query = "keyword",
            page = if (isLoadMore) 2 else 1,
            categoryId = null,
            filters = FilterState(),
            isLoadMore = isLoadMore,
            triggerSource = triggerSource,
        )
    }
}
