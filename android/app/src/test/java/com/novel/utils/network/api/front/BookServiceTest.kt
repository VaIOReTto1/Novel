package com.novel.utils.network.api.front

import com.google.gson.GsonBuilder
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.ApiService
import com.novel.utils.network.ImmutableListTypeAdapterFactory
import com.novel.utils.network.cache.IncrementalNetworkResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookServiceTest {

    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
        .create()

    @Test
    fun getVisitRankBooksBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":[
                    {
                      "id":1,
                      "categoryId":7,
                      "categoryName":"都市",
                      "picUrl":"https://example.com/cover.png",
                      "bookName":"Visit Rank Book",
                      "authorName":"Author",
                      "bookDesc":"desc",
                      "wordCount":1000,
                      "lastChapterName":"最新章",
                      "lastChapterUpdateTime":"2026-03-18 12:00:00"
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getVisitRankBooksBlocking()

        assertEquals(ApiService.BASE_URL_FRONT, facade.lastRequest?.baseUrl)
        assertEquals("book/visit_rank", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals("Visit Rank Book", result.data?.first()?.bookName)
    }

    @Test
    fun getUpdateRankBooksBlocking_buildsExpectedGetRequest() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        service.getUpdateRankBooksBlocking()

        assertEquals("book/update_rank", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
    }

    @Test
    fun getNewestRankBooksBlocking_buildsExpectedGetRequest() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        service.getNewestRankBooksBlocking()

        assertEquals("book/newest_rank", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
    }

    @Test
    fun getBookCategoriesBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[
                    {
                      "id":11,
                      "name":"玄幻"
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookCategoriesBlocking(1)

        assertEquals("book/category/list", facade.lastRequest?.endpoint)
        assertEquals(mapOf("workDirection" to "1"), facade.lastRequest?.queryParams)
        assertEquals("玄幻", result.data?.first()?.name)
        assertTrue(result.ok == true)
    }

    @Test
    fun getBookByIdBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "id":5,
                    "categoryId":7,
                    "categoryName":"都市",
                    "picUrl":"https://example.com/cover.png",
                    "bookName":"Detail Book",
                    "authorId":9,
                    "authorName":"Author",
                    "bookDesc":"desc",
                    "bookStatus":1,
                    "visitCount":99,
                    "wordCount":12345,
                    "commentCount":5,
                    "firstChapterId":11,
                    "lastChapterId":12,
                    "lastChapterName":"最新章",
                    "updateTime":"2026-03-18 10:00:00"
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookByIdBlocking(5L)

        assertEquals("book/5", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals("Detail Book", result.data?.bookName)
    }

    @Test
    fun getBookChaptersBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[
                    {
                      "id":11,
                      "bookId":5,
                      "chapterNum":1,
                      "chapterName":"第一章",
                      "chapterWordCount":1000,
                      "chapterUpdateTime":"2026-03-18 10:00:00",
                      "isVip":0
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookChaptersBlocking(5L)

        assertEquals("book/chapter/list", facade.lastRequest?.endpoint)
        assertEquals(mapOf("bookId" to "5"), facade.lastRequest?.queryParams)
        assertEquals("第一章", result.data?.first()?.chapterName)
    }

    @Test
    fun getBookContentBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "bookInfo":{
                      "id":5,
                      "categoryId":7,
                      "categoryName":"都市",
                      "picUrl":"https://example.com/cover.png",
                      "bookName":"Detail Book",
                      "authorId":9,
                      "authorName":"Author",
                      "bookDesc":"desc",
                      "bookStatus":1,
                      "visitCount":99,
                      "wordCount":12345,
                      "commentCount":5,
                      "firstChapterId":11,
                      "lastChapterId":12,
                      "lastChapterName":"最新章",
                      "updateTime":"2026-03-18 10:00:00"
                    },
                    "chapterInfo":{
                      "id":11,
                      "bookId":5,
                      "chapterNum":1,
                      "chapterName":"第一章",
                      "chapterWordCount":1000,
                      "chapterUpdateTime":"2026-03-18 10:00:00",
                      "isVip":0
                    },
                    "bookContent":"content"
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookContentBlocking(11L)

        assertEquals("book/content/11", facade.lastRequest?.endpoint)
        assertEquals("content", result.data?.bookContent)
    }

    @Test
    fun getNewestCommentsBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "commentTotal":1,
                    "comments":[
                      {
                        "id":1,
                        "commentContent":"nice",
                        "commentUser":"reader",
                        "commentUserId":10,
                        "commentUserPhoto":"https://example.com/photo.png",
                        "commentTime":"2026-03-18 10:00:00"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getNewestCommentsBlocking(5L)

        assertEquals("book/comment/newest_list", facade.lastRequest?.endpoint)
        assertEquals(mapOf("bookId" to "5"), facade.lastRequest?.queryParams)
        assertEquals("nice", result.data?.comments?.first()?.commentContent)
    }

    @Test
    fun getLastChapterAboutBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "chapterInfo":{
                      "id":12,
                      "bookId":5,
                      "chapterNum":2,
                      "chapterName":"第二章",
                      "chapterWordCount":1200,
                      "chapterUpdateTime":"2026-03-18 11:00:00",
                      "isVip":0
                    },
                    "chapterTotal":20,
                    "contentSummary":"summary"
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getLastChapterAboutBlocking(5L)

        assertEquals("book/last_chapter/about", facade.lastRequest?.endpoint)
        assertEquals(mapOf("bookId" to "5"), facade.lastRequest?.queryParams)
        assertEquals("summary", result.data?.contentSummary)
    }

    @Test
    fun getBookByIdWithCondition_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "id":5,
                    "categoryId":7,
                    "categoryName":"都市",
                    "picUrl":"https://example.com/cover.png",
                    "bookName":"Detail Book",
                    "authorId":9,
                    "authorName":"Author",
                    "bookDesc":"desc",
                    "bookStatus":1,
                    "visitCount":99,
                    "wordCount":12345,
                    "commentCount":5,
                    "firstChapterId":11,
                    "lastChapterId":12,
                    "lastChapterName":"最新章",
                    "updateTime":"2026-03-18 10:00:00"
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookByIdWithCondition(
            bookId = 5L,
            lastModified = "Mon, 18 Mar 2026 10:00:00 GMT",
            eTag = "\"book-5\""
        )

        assertEquals("book/5", facade.lastRequest?.endpoint)
        assertEquals("Mon, 18 Mar 2026 10:00:00 GMT", facade.lastRequest?.headers?.get("If-Modified-Since"))
        assertEquals("\"book-5\"", facade.lastRequest?.headers?.get("If-None-Match"))
        assertTrue(result is IncrementalNetworkResponse.Modified)
        assertEquals(
            "Detail Book",
            (result as IncrementalNetworkResponse.Modified).data.data?.bookName
        )
    }

    @Test
    fun getBookChaptersWithCondition_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[
                    {
                      "id":11,
                      "bookId":5,
                      "chapterNum":1,
                      "chapterName":"第一章",
                      "chapterWordCount":1000,
                      "chapterUpdateTime":"2026-03-18 10:00:00",
                      "isVip":0
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookChaptersWithCondition(
            bookId = 5L,
            lastModified = "Mon, 18 Mar 2026 10:00:00 GMT",
            eTag = "\"chapter-list-5\""
        )

        assertEquals("book/chapter/list", facade.lastRequest?.endpoint)
        assertEquals(mapOf("bookId" to "5"), facade.lastRequest?.queryParams)
        assertEquals("Mon, 18 Mar 2026 10:00:00 GMT", facade.lastRequest?.headers?.get("If-Modified-Since"))
        assertEquals("\"chapter-list-5\"", facade.lastRequest?.headers?.get("If-None-Match"))
        assertTrue(result is IncrementalNetworkResponse.Modified)
        assertEquals(
            "第一章",
            (result as IncrementalNetworkResponse.Modified).data.data?.first()?.chapterName
        )
    }

    @Test
    fun getBookChaptersWithCondition_handlesNullChapterUpdateTimeWithoutThrowing() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[
                    {
                      "id":11,
                      "bookId":5,
                      "chapterNum":1,
                      "chapterName":"第一章",
                      "chapterWordCount":1000,
                      "chapterUpdateTime":null,
                      "isVip":0
                    },
                    {
                      "id":12,
                      "bookId":5,
                      "chapterNum":2,
                      "chapterName":"第二章",
                      "chapterWordCount":1200,
                      "chapterUpdateTime":null,
                      "isVip":0
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookChaptersWithCondition(bookId = 5L)

        assertTrue(result is IncrementalNetworkResponse.Modified)
        assertEquals(
            2,
            (result as IncrementalNetworkResponse.Modified).data.data?.size
        )
    }

    @Test
    fun getBookContentWithCondition_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "bookInfo":{
                      "id":5,
                      "categoryId":7,
                      "categoryName":"都市",
                      "picUrl":"https://example.com/cover.png",
                      "bookName":"Detail Book",
                      "authorId":9,
                      "authorName":"Author",
                      "bookDesc":"desc",
                      "bookStatus":1,
                      "visitCount":99,
                      "wordCount":12345,
                      "commentCount":5,
                      "firstChapterId":11,
                      "lastChapterId":12,
                      "lastChapterName":"最新章",
                      "updateTime":"2026-03-18 10:00:00"
                    },
                    "chapterInfo":{
                      "id":11,
                      "bookId":5,
                      "chapterNum":1,
                      "chapterName":"第一章",
                      "chapterWordCount":1000,
                      "chapterUpdateTime":"2026-03-18 10:00:00",
                      "isVip":0
                    },
                    "bookContent":"content"
                  }
                }
            """.trimIndent()
        )
        val service = BookService(gson, facade)

        val result = service.getBookContentWithCondition(
            chapterId = 11L,
            lastModified = "Mon, 18 Mar 2026 10:00:00 GMT",
            eTag = "\"content-11\""
        )

        assertEquals("book/content/11", facade.lastRequest?.endpoint)
        assertEquals("Mon, 18 Mar 2026 10:00:00 GMT", facade.lastRequest?.headers?.get("If-Modified-Since"))
        assertEquals("\"content-11\"", facade.lastRequest?.headers?.get("If-None-Match"))
        assertTrue(result is IncrementalNetworkResponse.Modified)
        assertEquals(
            "content",
            (result as IncrementalNetworkResponse.Modified).data.data?.bookContent
        )
    }

    @Test
    fun getBookContentWithCondition_returnsNotModifiedWhenFacadeReports304() = runBlocking {
        val service = BookService(gson, ThrowingNetworkFacade(Exception("304 Not Modified")))

        val result = service.getBookContentWithCondition(chapterId = 11L)

        assertTrue(result is IncrementalNetworkResponse.NotModified)
    }

    private class RecordingNetworkFacade(
        private val response: String
    ) : NetworkFacade {
        var lastRequest: NetworkRequest? = null

        override suspend fun execute(request: NetworkRequest): String {
            lastRequest = request
            return response
        }
    }

    private class ThrowingNetworkFacade(
        private val throwable: Throwable
    ) : NetworkFacade {
        override suspend fun execute(request: NetworkRequest): String {
            throw throwable
        }
    }
}
