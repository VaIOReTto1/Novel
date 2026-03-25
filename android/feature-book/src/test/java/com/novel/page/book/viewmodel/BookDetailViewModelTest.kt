package com.novel.page.book.viewmodel

import com.google.common.truth.Truth.assertThat
import com.novel.page.book.gateway.BookDetailActionResult
import com.novel.page.book.gateway.BookDetailGateway
import com.novel.page.book.gateway.BookDetailLoadResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadBookDetail updates book info and last chapter from gateway`() = runTest(dispatcher) {
        val gateway = FakeBookDetailGateway(
            loadResult = BookDetailLoadResult(
                bookInfo = BookDetailState.BookInfo(
                    id = "1",
                    bookName = "Book",
                    authorName = "Author",
                    bookDesc = "Desc",
                    picUrl = "cover",
                    visitCount = 10L,
                    wordCount = 2000,
                    categoryName = "Fantasy",
                ),
                reviews = persistentListOf(
                    BookDetailState.BookReview(
                        id = "r1",
                        content = "Great",
                        rating = 5,
                        readTime = "now",
                        userName = "user",
                    ),
                ),
            ),
            lastChapter = BookDetailState.LastChapter(
                chapterName = "Chapter 1",
                chapterUpdateTime = "today",
            ),
        )
        val viewModel = BookDetailViewModel(gateway)

        viewModel.loadBookDetail("1")
        advanceUntilIdle()

        val state = viewModel.state.value
        assertThat(state.bookInfo?.bookName).isEqualTo("Book")
        assertThat(state.lastChapter?.chapterName).isEqualTo("Chapter 1")
        assertThat(state.reviews).hasSize(1)
    }

    private class FakeBookDetailGateway(
        private val loadResult: BookDetailLoadResult = BookDetailLoadResult(),
        private val lastChapter: BookDetailState.LastChapter? = null,
    ) : BookDetailGateway {
        override suspend fun loadBookDetail(bookId: String, useCache: Boolean): BookDetailLoadResult =
            loadResult

        override suspend fun loadLastChapter(bookId: String): BookDetailState.LastChapter? =
            lastChapter

        override suspend fun addToBookshelf(bookId: String): BookDetailActionResult =
            BookDetailActionResult(success = true)

        override suspend fun removeFromBookshelf(bookId: String): BookDetailActionResult =
            BookDetailActionResult(success = true)

        override suspend fun followAuthor(authorName: String): BookDetailActionResult =
            BookDetailActionResult(success = true)
    }
}
