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

class HomeServiceTest {

    private val gson = GsonBuilder()
        .registerTypeAdapterFactory(ImmutableListTypeAdapterFactory())
        .create()

    @Test
    fun getHomeBooksBlocking_buildsExpectedRequestAndParsesResponse() = runBlocking {
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
                      "picUrl":"https://example.com/home.png",
                      "bookName":"Home NetworkFacade Book",
                      "authorName":"Home Author",
                      "bookDesc":"Home service sample"
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = HomeService(gson, facade)

        val result = service.getHomeBooksBlocking()

        assertEquals(ApiService.BASE_URL_FRONT, facade.lastRequest?.baseUrl)
        assertEquals("home/books", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(mapOf("Accept" to "*/*"), facade.lastRequest?.headers)
        assertEquals("0", result.code)
        assertEquals("ok", result.message)
        assertTrue(result.ok == true)
        assertEquals(1, result.data?.size)
        assertEquals("Home NetworkFacade Book", result.data?.first()?.bookName)
    }

    @Test
    fun getFriendLinksBlocking_buildsExpectedRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":[
                    {
                      "linkName":"OpenAI",
                      "linkUrl":"https://openai.com/"
                    }
                  ]
                }
            """.trimIndent()
        )
        val service = HomeService(gson, facade)

        val result = service.getFriendLinksBlocking()

        assertEquals(ApiService.BASE_URL_FRONT, facade.lastRequest?.baseUrl)
        assertEquals("home/friend_Link/list", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals("OpenAI", result.data?.first()?.linkName)
        assertEquals("https://openai.com/", result.data?.first()?.linkUrl)
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
