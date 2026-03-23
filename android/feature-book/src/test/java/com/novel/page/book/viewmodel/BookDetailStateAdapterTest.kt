package com.novel.page.book.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class BookDetailStateAdapterTest {

    @Test
    fun canStartReading_returnsTrue_whenBookInfoLoaded() {
        val adapter = BookDetailStateAdapter(
            MutableStateFlow(
                BookDetailState(
                    bookInfo = BookDetailState.BookInfo(
                        id = "1",
                        bookName = "Book",
                        authorName = "Author",
                        bookDesc = "Desc",
                        picUrl = "cover",
                        visitCount = 10L,
                        wordCount = 1000,
                        categoryName = "玄幻"
                    ),
                ),
            ),
        )

        assertThat(adapter.canStartReading()).isTrue()
        assertThat(adapter.getReadButtonText()).isEqualTo("暂无章节")
    }

    @Test
    fun getBookDetailStatusSummary_returnsFailure_whenErrorExists() {
        val adapter = BookDetailStateAdapter(
            MutableStateFlow(
                BookDetailState(error = "boom"),
            ),
        )

        assertThat(adapter.getBookDetailStatusSummary()).isEqualTo("加载失败")
        assertThat(adapter.shouldShowRetryButton()).isTrue()
    }
}
