package com.novel.page.read.repository

import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class ReaderPaginationModelsTest {

    @Test
    fun pageCountCacheData_keepsChapterRanges() {
        val cacheData = PageCountCacheData(
            bookId = "book-1",
            fontSize = 18,
            containerSize = IntSize(1080, 1920),
            totalPages = 32,
            chapterPageRanges = persistentListOf(
                PageCountCacheData.ChapterPageRange(
                    chapterId = "chapter-1",
                    startPage = 0,
                    endPage = 9,
                    pageCount = 10
                )
            ),
            cacheTime = 123L
        )

        assertThat(cacheData.chapterPageRanges).hasSize(1)
        assertThat(cacheData.chapterPageRanges.first().chapterId).isEqualTo("chapter-1")
        assertThat(cacheData.totalPages).isEqualTo(32)
    }

    @Test
    fun progressiveCalculationState_defaultsToIdle() {
        val state = ProgressiveCalculationState()

        assertThat(state.isCalculating).isFalse()
        assertThat(state.currentCalculatedPages).isEqualTo(0)
        assertThat(state.totalChapters).isEqualTo(0)
        assertThat(state.calculatedChapters).isEqualTo(0)
        assertThat(state.estimatedTotalPages).isEqualTo(0)
    }
}
