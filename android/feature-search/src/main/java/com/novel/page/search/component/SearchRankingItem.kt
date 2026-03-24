package com.novel.page.search.component

import androidx.compose.runtime.Stable

@Stable
data class SearchRankingItem(
    val id: Long,
    val title: String,
    val author: String,
    val rank: Int
)
