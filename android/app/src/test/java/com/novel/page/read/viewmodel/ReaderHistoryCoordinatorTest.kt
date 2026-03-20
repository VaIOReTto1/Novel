package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ReaderHistoryCoordinatorTest {

    @Test
    fun createSaveToHistoryIntent_returnsNullWhenStateIsNotReady() {
        val coordinator = ReaderHistoryCoordinator()

        val intent = coordinator.createSaveToHistoryIntent(
            state = ReaderState(),
            bookId = "book-1",
        )

        assertThat(intent).isNull()
    }

    @Test
    fun createSaveToHistoryIntent_mapsStateToIntent() {
        val coordinator = ReaderHistoryCoordinator()
        val state = ReaderState(
            currentChapter = Chapter(id = "chapter-1", chapterName = "第一章"),
            currentPageData = PageData(
                chapterId = "chapter-1",
                chapterName = "第一章",
                content = "content",
                pages = persistentListOf("page-1"),
                bookInfo = PageData.BookInfo(
                    bookId = "book-1",
                    bookName = "书名",
                    authorName = "作者",
                    bookDesc = "desc",
                    picUrl = "cover",
                    visitCount = 10,
                    wordCount = 1000,
                    categoryName = "分类",
                ),
            ),
        )

        val intent = coordinator.createSaveToHistoryIntent(
            state = state.copy(isLoading = false),
            bookId = "book-1",
        )

        assertThat(intent).isEqualTo(
            ReaderIntent.SaveToHistory(
                bookId = "book-1",
                chapterId = "chapter-1",
                bookTitle = "书名",
                author = "作者",
                coverUrl = "cover",
                chapterTitle = "第一章",
            ),
        )
    }

    @Test
    fun saveHistory_returnsSuccessWhenServiceCompletes() {
        runBlocking {
            val coordinator = ReaderHistoryCoordinator()
            var savedIntent: ReaderIntent.SaveToHistory? = null
            val intent = ReaderIntent.SaveToHistory(
                bookId = "book-1",
                chapterId = "chapter-1",
                bookTitle = "title",
                author = "author",
                coverUrl = "cover",
                chapterTitle = "第一章",
            )

            val outcome = coordinator.saveHistory(
                intent = intent,
                persist = { value ->
                    savedIntent = value
                },
            )

            assertThat(outcome.saved).isTrue()
            assertThat(savedIntent).isEqualTo(intent)
        }
    }

    @Test
    fun saveHistory_returnsFailureWhenServiceThrows() {
        runBlocking {
            val coordinator = ReaderHistoryCoordinator()

            val outcome = coordinator.saveHistory(
                intent = ReaderIntent.SaveToHistory(bookId = "book-1", chapterId = "chapter-1"),
                persist = { error("boom") },
            )

            assertThat(outcome.saved).isFalse()
        }
    }
}
