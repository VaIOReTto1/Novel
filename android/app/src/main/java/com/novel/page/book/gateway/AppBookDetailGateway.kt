package com.novel.page.book.gateway

import com.novel.page.book.usecase.AddToBookshelfUseCase
import com.novel.page.book.usecase.FollowAuthorUseCase
import com.novel.page.book.usecase.GetBookDetailUseCase
import com.novel.page.book.usecase.GetLastChapterUseCase
import com.novel.page.book.usecase.RemoveFromBookshelfUseCase
import com.novel.page.book.viewmodel.BookDetailState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppBookDetailGateway @Inject constructor(
    private val getBookDetailUseCase: GetBookDetailUseCase,
    private val getLastChapterUseCase: GetLastChapterUseCase,
    private val addToBookshelfUseCase: AddToBookshelfUseCase,
    private val removeFromBookshelfUseCase: RemoveFromBookshelfUseCase,
    private val followAuthorUseCase: FollowAuthorUseCase,
) : BookDetailGateway {

    override suspend fun loadBookDetail(bookId: String, useCache: Boolean): BookDetailLoadResult {
        val result = getBookDetailUseCase(GetBookDetailUseCase.Params(bookId, useCache))
        return BookDetailLoadResult(
            bookInfo = result.bookInfo,
            reviews = result.reviews,
        )
    }

    override suspend fun loadLastChapter(bookId: String): BookDetailState.LastChapter? {
        return getLastChapterUseCase(GetLastChapterUseCase.Params(bookId)).lastChapter
    }

    override suspend fun addToBookshelf(bookId: String): BookDetailActionResult {
        val result = addToBookshelfUseCase(AddToBookshelfUseCase.Params(bookId))
        return BookDetailActionResult(success = result.success, message = result.message)
    }

    override suspend fun removeFromBookshelf(bookId: String): BookDetailActionResult {
        val result = removeFromBookshelfUseCase(RemoveFromBookshelfUseCase.Params(bookId))
        return BookDetailActionResult(success = result.success, message = result.message)
    }

    override suspend fun followAuthor(authorName: String): BookDetailActionResult {
        val result = followAuthorUseCase(FollowAuthorUseCase.Params(authorName))
        return BookDetailActionResult(success = result.success, message = result.message)
    }
}
