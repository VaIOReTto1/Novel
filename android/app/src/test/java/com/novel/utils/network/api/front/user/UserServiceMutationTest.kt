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

class UserServiceMutationTest {

    private val gson = GsonBuilder().create()

    @Test
    fun updateUserInfoBlocking_buildsExpectedPutRequestAndParsesResponse() = runBlocking {
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
        val service = UserService(gson, facade)

        val result = service.updateUserInfoBlocking(
            UserService.UserInfoUpdateRequest(
                userId = 7L,
                nickName = "reader",
                userPhoto = "https://example.com/photo.png",
                userSex = 1
            )
        )

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.PUT, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "userId" to "7",
                "nickName" to "reader",
                "userPhoto" to "https://example.com/photo.png",
                "userSex" to "1"
            ),
            facade.lastRequest?.bodyParams
        )
        assertEquals("application/json", facade.lastRequest?.headers?.get("Content-Type"))
        assertTrue(result.ok == true)
    }

    @Test
    fun postCommentBlocking_buildsExpectedPostRequestAndParsesResponse() = runBlocking {
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
        val service = UserService(gson, facade)

        val result = service.postCommentBlocking(
            UserService.CommentRequest(
                userId = 9L,
                bookId = 77L,
                commentContent = "Nice chapter"
            )
        )

        assertEquals(ApiService.BASE_URL_USER, facade.lastRequest?.baseUrl)
        assertEquals("comment", facade.lastRequest?.endpoint)
        assertEquals(NetworkRequestMethod.POST, facade.lastRequest?.method)
        assertEquals(
            mapOf(
                "bookId" to "77",
                "commentContent" to "Nice chapter"
            ),
            facade.lastRequest?.bodyParams
        )
        assertEquals("0", result.code)
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
