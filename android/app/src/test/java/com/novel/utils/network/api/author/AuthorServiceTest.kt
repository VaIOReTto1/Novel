package com.novel.utils.network.api.author

import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.core.result.AppError
import com.novel.core.result.DataResult
import com.novel.utils.network.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class AuthorServiceTest {

    @Test
    fun getAuthorStatusBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":"0"
                }
            """.trimIndent()
        )
        val service = AuthorService(facade)

        val result = service.getAuthorStatusBlocking()

        assertEquals(ApiService.BASE_URL_AUTHOR, facade.lastRequest?.baseUrl)
        assertEquals("status", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals("0", result.data)
        assertTrue(result.ok == true)
    }

    @Test
    fun registerAuthorBlocking_buildsExpectedPostRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":null
                }
            """.trimIndent()
        )
        val service = AuthorService(facade)
        val request = AuthorService.AuthorRegisterRequest(
            penName = "writer",
            telPhone = "writer",
            chatAccount = "writer",
            email = "writer@example.com",
            workDirection = 1
        )

        val result = service.registerAuthorBlocking(request)

        assertEquals(ApiService.BASE_URL_AUTHOR, facade.lastRequest?.baseUrl)
        assertEquals("register", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "penName" to "writer",
                "telPhone" to "writer",
                "chatAccount" to "writer",
                "email" to "writer@example.com",
                "workDirection" to "1"
            ),
            facade.lastRequest?.bodyParams
        )
        assertEquals("0", result.code)
        assertTrue(result.ok == true)
    }

    @Test
    fun getAuthorStatusResult_wrapsIoFailureAsNetworkError() = runBlocking {
        val service = AuthorService(FailingNetworkFacade(IOException("offline")))

        val result = service.getAuthorStatusResult()

        assertTrue(result is DataResult.Failure)
        assertTrue((result as DataResult.Failure).error is AppError.Network)
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
