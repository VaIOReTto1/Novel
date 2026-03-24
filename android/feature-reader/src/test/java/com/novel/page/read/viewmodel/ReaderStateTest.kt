package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import com.novel.page.read.repository.PageCountCacheData
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class ReaderStateTest {

    @Test
    fun computedReadingProgress_usesChapterIndexInVerticalMode() {
        val state = ReaderState(
            currentChapterIndex = 1,
            chapterList = persistentListOf(
                Chapter(id = "c1", chapterName = "第一章"),
                Chapter(id = "c2", chapterName = "第二章"),
                Chapter(id = "c3", chapterName = "第三章"),
            ),
            readerSettings = ReaderSettings.getDefault().copy(pageFlipEffect = PageFlipEffect.VERTICAL),
        )

        assertThat(state.computedReadingProgress).isWithin(0.0001f).of(2f / 3f)
    }

    @Test
    fun computedReadingProgress_usesPageCountCacheInPagedMode() {
        val state = ReaderState(
            currentChapter = Chapter(id = "c2", chapterName = "第二章"),
            currentPageIndex = 2,
            pageCountCache = PageCountCacheData(
                bookId = "book-1",
                fontSize = 16,
                containerSize = IntSize(1080, 1920),
                totalPages = 20,
                chapterPageRanges = persistentListOf(
                    PageCountCacheData.ChapterPageRange(
                        chapterId = "c2",
                        startPage = 5,
                        endPage = 9,
                        pageCount = 5,
                    ),
                ),
                cacheTime = 1L,
            ),
        )

        assertThat(state.computedReadingProgress).isWithin(0.0001f).of(8f / 20f)
    }
}
