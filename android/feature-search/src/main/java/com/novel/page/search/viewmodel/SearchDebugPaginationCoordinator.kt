package com.novel.page.search.viewmodel

class SearchDebugPaginationCoordinator {

    fun resolvePageSize(
        defaultPageSize: Int,
        overridePageSize: Int?,
    ): Int {
        val candidate = overridePageSize ?: return defaultPageSize
        if (candidate <= 0) {
            return defaultPageSize
        }
        return candidate.coerceAtMost(defaultPageSize)
    }
}
