package com.novel.utils.network.api.front.user

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

class UserServiceQueryTest {

    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
        .create()

    @Test
    fun getUserCommentsBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "pageNum":3,
                    "pageSize":15,
                    "total":30,
                    "pages":2,
                    "list":[
                      {
                        "commentContent":"Great book",
                        "commentBookPic":"https://example.com/cover.png",
                        "commentBook":"Novel",
                        "commentTime":"2026-03-17 08:00:00"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        val service = UserService(gson, facade)

        val result = service.getUserCommentsBlocking(
            UserService.PageRequest(
                pageNum = 3,
                pageSize = 15,
                fetchAll = false
            )
        )

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("comments", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "pageNum" to "3",
                "pageSize" to "15",
                "fetchAll" to "false"
            ),
            facade.lastRequest?.queryParams
        )
        assertTrue(result.ok == true)
        assertEquals(1, result.data?.list?.size)
        assertEquals("Great book", result.data?.list?.first()?.commentContent)
    }

    @Test
    fun getBookshelfStatusBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":1
                }
            """.trimIndent()
        )
        val service = UserService(gson, facade)

        val result = service.getBookshelfStatusBlocking("book-1")

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("bookshelf_status", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(mapOf("bookId" to "book-1"), facade.lastRequest?.queryParams)
        assertEquals(1, result.data)
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
