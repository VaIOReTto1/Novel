package com.novel.utils.network.api.front.user

import com.google.gson.GsonBuilder
import com.novel.core.network.NetworkFacade
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.ApiService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UserServiceAuthTest {

    private val gson = GsonBuilder().create()

    @Test
    fun loginBlocking_buildsExpectedPostRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "uid":1,
                    "nickName":"novel-user",
                    "token":"token-123"
                  }
                }
            """.trimIndent()
        )
        val service = UserService(gson, facade)

        val result = service.loginBlocking(
            UserService.LoginRequest(
                username = "novel",
                password = "secret"
            )
        )

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("login", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "username" to "novel",
                "password" to "secret"
            ),
            facade.lastRequest?.bodyParams
        )
        assertEquals("application/json", facade.lastRequest?.headers?.get("Content-Type"))
        assertEquals("0", result.code)
        assertEquals("novel-user", result.data?.nickName)
    }

    @Test
    fun registerBlocking_buildsExpectedPostRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "uid":2,
                    "token":"register-token"
                  }
                }
            """.trimIndent()
        )
        val service = UserService(gson, facade)

        val result = service.registerBlocking(
            UserService.RegisterRequest(
                username = "novel",
                password = "secret",
                sessionId = "session-1",
                velCode = "1234"
            )
        )

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("register", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "username" to "novel",
                "password" to "secret",
                "sessionId" to "session-1",
                "velCode" to "1234"
            ),
            facade.lastRequest?.bodyParams
        )
        assertEquals("register-token", result.data?.token)
    }

    @Test
    fun getUserInfoBlocking_buildsExpectedGetRequestAndParsesResponse() = runBlocking {
        val facade = RecordingNetworkFacade(
            response = """
                {
                  "code":"0",
                  "message":"ok",
                  "ok":true,
                  "data":{
                    "nickName":"reader",
                    "userPhoto":"https://example.com/photo.png",
                    "userSex":1
                  }
                }
            """.trimIndent()
        )
        val service = UserService(gson, facade)

        val result = service.getUserInfoBlocking()

        assertEquals(ApiService.BASE_URL_FRONT, facade.lastRequest?.baseUrl)
        assertEquals("user", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.GET, facade.lastRequest?.method)
        assertEquals(mapOf("Accept" to "application/json"), facade.lastRequest?.headers)
        assertTrue(result?.ok == true)
        assertEquals("reader", result?.data?.nickName)
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
