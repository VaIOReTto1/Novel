package com.novel.utils.network.api.author.ai

import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AiServiceTest {

    @Test
    fun polishTextBlocking_buildsExpectedPostQueryRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":"polished text"
                }
            """.trimIndent()
        )
        val service = AiService(facade)

        val result = service.polishTextBlocking("draft")

        assertEquals(ApiService.BASE_URL_AI, facade.lastRequest?.baseUrl)
        assertEquals("polish", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST_QUERY, facade.lastRequest?.method)
        assertEquals(mapOf("text" to "draft"), facade.lastRequest?.queryParams)
        assertEquals("polished text", result.data)
        assertTrue(result.ok == true)
    }

    @Test
    fun expandTextBlocking_buildsExpectedPostQueryRequest() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "ok":true,
                  "data":"expanded text"
                }
            """.trimIndent()
        )
        val service = AiService(facade)

        service.expandTextBlocking("draft", 150)

        assertEquals("expand", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST_QUERY, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "text" to "draft",
                "ratio" to "150"
            ),
            facade.lastRequest?.queryParams
        )
    }

    @Test
    fun polishTextResult_wrapsIoFailureAsNetworkError() = runBlocking {
        val service = AiService(FailingNetworkFacade(IOException("offline")))

        val result = service.polishTextResult("draft")

        assertTrue(result is com.novel.core.result.DataResult.Failure)
        assertTrue((result as com.novel.core.result.DataResult.Failure).error is com.novel.core.result.AppError.Network)
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

    private class FailingNetworkFacade(
        private val throwable: Throwable
    ) : NetworkFacade {
        override suspend fun execute(request: NetworkRequest): String {
            throw throwable
        }
    }
}
