package com.novel.page.book.gateway

import androidx.compose.runtime.Stable
import com.novel.page.book.viewmodel.BookDetailState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

interface BookDetailGateway {
    suspend fun loadBookDetail(bookId: String, useCache: Boolean): BookDetailLoadResult

    suspend fun loadLastChapter(bookId: String): BookDetailState.LastChapter?

    suspend fun addToBookshelf(bookId: String): BookDetailActionResult

    suspend fun removeFromBookshelf(bookId: String): BookDetailActionResult

    suspend fun followAuthor(authorName: String): BookDetailActionResult
}

@Stable
data class BookDetailLoadResult(
    val bookInfo: BookDetailState.BookInfo? = null,
    val reviews: ImmutableList<BookDetailState.BookReview> = persistentListOf(),
)

@Stable
data class BookDetailActionResult(
    val success: Boolean,
    val message: String = "",
)
