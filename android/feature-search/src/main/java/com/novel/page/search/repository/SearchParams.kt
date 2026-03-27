package com.novel.page.search.repository

import androidx.compose.runtime.Stable
import com.novel.page.search.viewmodel.FilterState
import com.novel.page.search.viewmodel.SearchTriggerSource

@Stable
data class SearchParams(
    val query: String,
    val page: Int,
    val pageSize: Int = 20,
    val categoryId: Int?,
    val filters: FilterState,
    val isLoadMore: Boolean,
    val triggerSource: SearchTriggerSource = if (isLoadMore) {
        SearchTriggerSource.LOAD_MORE
    } else {
        SearchTriggerSource.INITIAL_ENTRY
    },
)
