package com.novel.rn.bridge.delegate

import com.novel.page.read.service.HistoryItem
import com.novel.rn.bridge.network.AuthorBookBridgeItem
import com.novel.rn.bridge.network.AuthorBooksBridgeResponse
import com.novel.rn.bridge.network.BookCategoryBridgeItem
import com.novel.rn.bridge.network.BookCategoryBridgeResponse
import com.novel.rn.bridge.network.HomeBooksBridgeItem
import com.novel.rn.bridge.network.HomeBooksBridgeResponse
import com.novel.rn.bridge.network.SearchBooksBridgeItem
import com.novel.rn.bridge.network.SearchBooksBridgeResponse
import com.novel.utils.network.api.author.AuthorService

data class ReadingHistoryPayloadItem(
    val id: String,
    val bookId: String,
    val chapterId: String,
    val title: String,
    val chapterTitle: String,
    val totalChapters: Int,
    val currentChapter: Int,
    val author: String,
    val coverUrl: String,
    val lastReadTime: Double,
    val readProgress: Double,
    val description: String,
    val type: String,
    val categoryId: String,
    val readCount: Int,
    val rating: Double
)

data class ReadingHistoryPayload(
    val historyItems: List<ReadingHistoryPayloadItem>,
    val success: Boolean
)

data class HomeBooksPayload(
    val code: String?,
    val message: String?,
    val ok: Boolean,
    val data: List<HomeBooksBridgeItem>
)

data class AuthorStatusPayload(
    val code: String?,
    val message: String?,
    val ok: Boolean,
    val isAuthor: Boolean
)

data class AuthorBooksPayload(
    val code: String?,
    val message: String?,
    val ok: Boolean,
    val list: List<AuthorBookBridgeItem>
)

data class BookCategoriesPayload(
    val ok: Boolean,
    val list: List<BookCategoryBridgeItem>
)

data class SearchBooksPayload(
    val ok: Boolean,
    val pageNum: Long?,
    val pageSize: Long?,
    val total: Long?,
    val pages: Long?,
    val list: List<SearchBooksBridgeItem>
)

class NavigationContentQueryDelegate {

    fun buildReadingHistoryPayload(historyItems: List<HistoryItem>): ReadingHistoryPayload {
        return ReadingHistoryPayload(
            historyItems = historyItems.map { item ->
                ReadingHistoryPayloadItem(
                    id = item.id,
                    bookId = item.bookId,
                    chapterId = item.chapterId,
                    title = item.title,
                    chapterTitle = item.chapterTitle,
                    totalChapters = item.totalChapters,
                    currentChapter = item.currentChapter,
                    author = item.author,
                    coverUrl = item.coverUrl,
                    lastReadTime = item.lastReadTime.toDouble(),
                    readProgress = item.readProgress.toDouble(),
                    description = "暂无描述",
                    type = "book",
                    categoryId = "1",
                    readCount = 1,
                    rating = 4.5
                )
            },
            success = true
        )
    }

    fun buildHomeBooksPayload(response: HomeBooksBridgeResponse): HomeBooksPayload {
        return HomeBooksPayload(
            code = response.code,
            message = response.message,
            ok = response.ok,
            data = response.data
        )
    }

    fun buildAuthorStatusPayload(response: AuthorService.AuthorStatusResponse): AuthorStatusPayload {
        return AuthorStatusPayload(
            code = response.code,
            message = response.message,
            ok = response.ok ?: false,
            isAuthor = response.data == "0"
        )
    }

    fun buildAuthorBooksPayload(response: AuthorBooksBridgeResponse): AuthorBooksPayload {
        return AuthorBooksPayload(
            code = response.code,
            message = response.message,
            ok = response.ok,
            list = response.list
        )
    }

    fun buildBookCategoriesPayload(response: BookCategoryBridgeResponse): BookCategoriesPayload {
        return BookCategoriesPayload(
            ok = response.ok,
            list = response.data
        )
    }

    fun buildSearchBooksPayload(response: SearchBooksBridgeResponse): SearchBooksPayload {
        return SearchBooksPayload(
            ok = response.ok,
            pageNum = response.pageNum,
            pageSize = response.pageSize,
            total = response.total,
            pages = response.pages,
            list = response.list
        )
    }
}
