package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.read.repository.PageCountCacheData
import com.novel.page.read.repository.ProgressiveCalculationState
import androidx.compose.ui.unit.IntSize
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class ReaderMappingHelperTest {

    @Test
    fun toReaderInfo_mapsReaderStateFields() {
        val state = ReaderState(
            currentChapter = Chapter(id = "chapter-1", chapterName = "第一章"),
            currentPageIndex = 3,
        )

        val readerInfo = ReaderMappingHelper.toReaderInfo(state)

        assertThat(readerInfo.currentChapter?.id).isEqualTo("chapter-1")
        assertThat(readerInfo.perChapterPageIndex).isEqualTo(3)
        assertThat(readerInfo.paginationState).isEqualTo(state.paginationState)
    }

    @Test
    fun buildPageInfoText_usesPageCountCacheWhenAvailable() {
        val readerInfo = ReaderInfo(
            paginationState = ProgressiveCalculationState(),
            pageCountCache = PageCountCacheData(
                bookId = "book-1",
                fontSize = 16,
                containerSize = IntSize(1080, 1920),
                totalPages = 20,
                chapterPageRanges = persistentListOf(
                    PageCountCacheData.ChapterPageRange(
                        chapterId = "chapter-1",
                        startPage = 5,
                        endPage = 9,
                        pageCount = 5,
                    ),
                ),
                cacheTime = 1L,
            ),
            currentChapter = Chapter(id = "chapter-1", chapterName = "第一章"),
            perChapterPageIndex = 2,
        )

        val text = ReaderMappingHelper.buildPageInfoText(
            readerInfo = readerInfo,
            currentChapterIndex = 0,
            totalChapters = 10,
        )

        assertThat(text).isEqualTo("8 / 20")
    }

    @Test
    fun buildPageInfoText_fallsBackToEstimatedPagesWhenCacheMissing() {
        val readerInfo = ReaderInfo(
            paginationState = ProgressiveCalculationState(
                isCalculating = true,
                estimatedTotalPages = 50,
            ),
            pageCountCache = null,
            currentChapter = null,
            perChapterPageIndex = 1,
        )

        val text = ReaderMappingHelper.buildPageInfoText(
            readerInfo = readerInfo,
            currentChapterIndex = 2,
            totalChapters = 10,
        )

        assertThat(text).isEqualTo("12 / 50 (计算中...)")
    }
}
