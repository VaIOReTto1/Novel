package com.novel.page.read.repository

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import kotlinx.collections.immutable.ImmutableList

@Stable
data class PageCountCacheData(
    val bookId: String,
    val fontSize: Int,
    val containerSize: IntSize,
    val totalPages: Int,
    val chapterPageRanges: ImmutableList<ChapterPageRange>,
    val cacheTime: Long
) {
    @Stable
    data class ChapterPageRange(
        val chapterId: String,
        val startPage: Int,
        val endPage: Int,
        val pageCount: Int
    )
}

data class ProgressiveCalculationState(
    val isCalculating: Boolean = false,
    val currentCalculatedPages: Int = 0,
    val totalChapters: Int = 0,
    val calculatedChapters: Int = 0,
    val estimatedTotalPages: Int = 0
)
