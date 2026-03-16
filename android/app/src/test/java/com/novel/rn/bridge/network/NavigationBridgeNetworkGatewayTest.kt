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
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL)

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
        val gateway = NavigationBridgeNetworkGateway(facade, FRONT_BASE_URL)

        val result = gateway.getHomeBooksHighPriority()

        assertEquals(false, result.ok)
        assertEquals(1, result.data.size)
        assertNull(result.data.first().bookName)
        assertNull(result.data.first().authorName)
        assertNull(result.data.first().picUrl)
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
