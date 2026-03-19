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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationContentQueryDelegateTest {

    private val delegate = NavigationContentQueryDelegate()

    @Test
    fun buildReadingHistoryPayload_keepsCurrentFields() {
        val payload = delegate.buildReadingHistoryPayload(
            listOf(
                HistoryItem(
                    id = "1",
                    bookId = "2",
                    chapterId = "3",
                    title = "Book",
                    author = "Author",
                    coverUrl = "cover",
                    chapterTitle = "Chapter",
                    readProgress = 0.5f,
                    lastReadTime = 123L,
                    totalChapters = 10,
                    currentChapter = 2
                )
            )
        )

        assertTrue(payload.success)
        assertEquals("Book", payload.historyItems.first().title)
        assertEquals("暂无描述", payload.historyItems.first().description)
    }

    @Test
    fun buildHomeBooksPayload_mapsGatewayResponse() {
        val payload = delegate.buildHomeBooksPayload(
            HomeBooksBridgeResponse(
                code = "0",
                message = "ok",
                ok = true,
                data = listOf(
                    HomeBooksBridgeItem(
                        type = 1,
                        bookId = "101",
                        picUrl = "cover",
                        bookName = "Book",
                        authorName = "Author",
                        bookDesc = "Desc"
                    )
                )
            )
        )

        assertEquals("0", payload.code)
        assertTrue(payload.ok)
        assertEquals("101", payload.data.first().bookId)
    }

    @Test
    fun buildAuthorStatusPayload_mapsResponseToIsAuthor() {
        val payload = delegate.buildAuthorStatusPayload(
            AuthorService.AuthorStatusResponse(
                code = "0",
                message = "ok",
                data = "0",
                ok = true
            )
        )

        assertTrue(payload.isAuthor)
        assertEquals("0", payload.code)
    }

    @Test
    fun buildAuthorBooksPayload_mapsGatewayResponse() {
        val payload = delegate.buildAuthorBooksPayload(
            AuthorBooksBridgeResponse(
                code = "0",
                message = "ok",
                ok = true,
                list = listOf(
                    AuthorBookBridgeItem(
                        id = "9",
                        bookName = "Author Book",
                        authorName = "Writer",
                        picUrl = "cover",
                        wordCount = 12.0,
                        bookDesc = "Desc",
                        categoryId = "5",
                        categoryName = "都市"
                    )
                )
            )
        )

        assertEquals("Author Book", payload.list.first().bookName)
        assertEquals("5", payload.list.first().categoryId)
    }

    @Test
    fun buildBookCategoriesPayload_mapsGatewayResponse() {
        val payload = delegate.buildBookCategoriesPayload(
            BookCategoryBridgeResponse(
                ok = true,
                data = listOf(BookCategoryBridgeItem(id = "7", name = "玄幻"))
            )
        )

        assertTrue(payload.ok)
        assertEquals("玄幻", payload.list.first().name)
    }

    @Test
    fun buildSearchBooksPayload_mapsGatewayResponse() {
        val payload = delegate.buildSearchBooksPayload(
            SearchBooksBridgeResponse(
                ok = true,
                pageNum = 1,
                pageSize = 10,
                total = 20,
                pages = 2,
                list = listOf(
                    SearchBooksBridgeItem(
                        id = "8",
                        bookName = "Search Book",
                        authorName = "Author",
                        picUrl = "cover",
                        bookDesc = "Desc"
                    )
                )
            )
        )

        assertEquals(1L, payload.pageNum)
        assertEquals("Search Book", payload.list.first().bookName)
    }
}
