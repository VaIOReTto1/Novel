package com.novel.rn.bridge.network

import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NavigationBridgeNetworkGatewayTest {

    companion object {
        private const val FRONT_BASE_URL = "https://api-front.example/"
        private const val AUTHOR_BASE_URL = "https://api-author.example/"
    }

    @Test
    fun getHomeBooksHighPriority_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":[
                    {
                      "type":3,
                      "bookId":101,
                      "picUrl":"https://example.com/cover.png",
                      "bookName":"Smoke Home Book",
                      "authorName":"Novel Author",
                      "bookDesc":"Bridge gateway sample"
                    }
                  ]
                }
            """.trimIndent()
        )
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL, AUTHOR_BASE_URL)

        val result = gateway.getHomeBooksHighPriority()

        assertEquals(FRONT_BASE_URL, facade.lastRequest?.baseUrl)
        assertEquals("home/books", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(mapOf("Accept" to "*/*"), facade.lastRequest?.headers)
        assertEquals("0", result.code)
        assertEquals("ok", result.message)
        assertEquals(true, result.ok)
        assertEquals(1, result.data.size)
        assertEquals("101", result.data.first().bookId)
        assertEquals("Smoke Home Book", result.data.first().bookName)
    }

    @Test
    fun getHomeBooksHighPriority_keepsMissingOptionalFieldsAsNull() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":false,
                  "data":[
                    {
                      "type":0,
                      "bookId":202
                    }
                  ]
                }
            """.trimIndent()
        )
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL, AUTHOR_BASE_URL)

        val result = gateway.getHomeBooksHighPriority()

        assertEquals(false, result.ok)
        assertEquals(1, result.data.size)
        assertNull(result.data.first().bookName)
        assertNull(result.data.first().authorName)
        assertNull(result.data.first().picUrl)
    }

    @Test
    fun getBookCategories_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":[
                    {
                      "id":12,
                      "name":"玄幻"
                    }
                  ]
                }
            """.trimIndent()
        )
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL, AUTHOR_BASE_URL)

        val result = gateway.getBookCategories(workDirection = 1)

        assertEquals(FRONT_BASE_URL, facade.lastRequest?.baseUrl)
        assertEquals("book/category/list", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(
            mapOf("workDirection" to "1"),
            facade.lastRequest?.queryParams
        )
        assertEquals(true, result.ok)
        assertEquals(1, result.data.size)
        assertEquals("12", result.data.first().id)
        assertEquals("玄幻", result.data.first().name)
    }

    @Test
    fun searchBooks_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":{
                    "pageNum":2,
                    "pageSize":20,
                    "total":99,
                    "pages":5,
                    "list":[
                      {
                        "id":88,
                        "bookName":"Bridge Search Book",
                        "authorName":"Search Author",
                        "picUrl":"https://example.com/search.png",
                        "bookDesc":"Search bridge sample"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL, AUTHOR_BASE_URL)

        val result = gateway.searchBooks(
            workDirection = 0,
            categoryId = 7,
            pageNum = 2,
            pageSize = 20
        )

        assertEquals(FRONT_BASE_URL, facade.lastRequest?.baseUrl)
        assertEquals("search/books", facade.lastRequest?.endpoint)
        assertEquals(
            mapOf(
                "pageNum" to "2",
                "pageSize" to "20",
                "workDirection" to "0",
                "categoryId" to "7"
            ),
            facade.lastRequest?.queryParams
        )
        assertEquals(true, result.ok)
        assertEquals(2L, result.pageNum)
        assertEquals(20L, result.pageSize)
        assertEquals(99L, result.total)
        assertEquals(5L, result.pages)
        assertEquals(1, result.list.size)
        assertEquals("88", result.list.first().id)
        assertEquals("Bridge Search Book", result.list.first().bookName)
    }

    @Test
    fun getAuthorBooks_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "list":[
                      {
                        "id":901,
                        "bookName":"Author Bridge Book",
                        "authorName":"Bridge Writer",
                        "picUrl":"https://example.com/author.png",
                        "wordCount":123456,
                        "bookDesc":"Author bridge sample",
                        "categoryId":5,
                        "categoryName":"都市"
                      }
                    ]
                  }
                }
            """.trimIndent()
        )
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL, AUTHOR_BASE_URL)

        val result = gateway.getAuthorBooks(pageNum = 1, pageSize = 10)

        assertEquals(AUTHOR_BASE_URL, facade.lastRequest?.baseUrl)
        assertEquals("books", facade.lastRequest?.endpoint)
        assertEquals(
            mapOf(
                "pageNum" to "1",
                "pageSize" to "10"
            ),
            facade.lastRequest?.queryParams
        )
        assertEquals("0", result.code)
        assertEquals("ok", result.message)
        assertEquals(true, result.ok)
        assertEquals(1, result.list.size)
        assertEquals("901", result.list.first().id)
        assertEquals("Author Bridge Book", result.list.first().bookName)
        assertEquals(123456.0, result.list.first().wordCount, 0.0)
        assertEquals("5", result.list.first().categoryId)
        assertEquals("都市", result.list.first().categoryName)
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
