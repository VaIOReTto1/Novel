package com.novel.page.search.viewmodel

import com.novel.page.search.repository.SearchParams

class SearchRetryPolicyCoordinator {

    fun shouldRetry(
        params: SearchParams,
        retryAttempts: Int,
        maxRetryAttempts: Int,
    ): Boolean {
        return params.triggerSource != SearchTriggerSource.LOAD_MORE &&
            retryAttempts < maxRetryAttempts
    }

    fun createAutomaticRetryParams(params: SearchParams): SearchParams {
        return params
    }

    fun createUserRetryParams(params: SearchParams): SearchParams {
        if (params.isLoadMore) {
            return params
        }

        return params.copy(triggerSource = SearchTriggerSource.USER_RETRY)
    }

    fun retryDelayMs(
        retryAttempts: Int,
        baseDelayMs: Long,
    ): Long {
        return retryAttempts * baseDelayMs
    }
}
