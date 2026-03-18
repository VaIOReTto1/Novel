package com.novel.utils.network.api.front

import com.google.gson.GsonBuilder
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.ApiService
import com.novel.utils.network.ImmutableListTypeAdapterFactory
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

    private class RecordingNetworkFacade(
        private val response: String
    ) : NetworkFacade {
        var lastRequest: NetworkRequest? = null

        override suspend fun execute(request: NetworkRequest): String {
            lastRequest = request
            return response
        }
    }
}
