package com.novel.page.search.viewmodel

import com.novel.page.search.repository.SearchParams

class SearchRetryPolicyCoordinator {

    fun shouldRetry(
        params: SearchParams,
        retryAttempts: Int,
        maxRetryAttempts: Int,
    ): Boolean {
        return !params.isLoadMore && retryAttempts <= maxRetryAttempts
    }

    fun retryDelayMs(
        retryAttempts: Int,
        baseDelayMs: Long,
    ): Long {
        return retryAttempts * baseDelayMs
    }
}
