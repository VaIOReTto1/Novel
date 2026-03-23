package com.novel.core.network

import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LegacyApiServiceAdapterTest {

    @Test
    fun execute_dispatchesGetRequestsToLegacyExecutor() = runBlocking {
        val executor = RecordingLegacyApiExecutor(
            response = "get-ok"
        )
        val adapter = LegacyApiServiceAdapter(executor)

        val result = adapter.execute(
            NetworkRequest(
                baseUrl = "https://example.com/",
                endpoint = "books/home",
                method = NetworkRequestMethod.GET,
                queryParams = mapOf("page" to "1"),
                headers = mapOf("X-Test" to "true")
            )
        )

        assertEquals("get-ok", result)
        assertEquals("GET", executor.lastMethod)
        assertEquals("https://example.com/", executor.lastBaseUrl)
        assertEquals("books/home", executor.lastEndpoint)
        assertEquals(mapOf("page" to "1"), executor.lastParams)
        assertEquals(mapOf("X-Test" to "true"), executor.lastHeaders)
    }

    @Test
    fun execute_dispatchesPostJsonRequestsToLegacyExecutor() = runBlocking {
        val executor = RecordingLegacyApiExecutor(
            response = "post-json-ok"
        )
        val adapter = LegacyApiServiceAdapter(executor)

        val result = adapter.execute(
            NetworkRequest(
                baseUrl = "https://example.com/",
                endpoint = "user/profile",
                method = NetworkRequestMethod.POST_JSON,
                jsonBodyParams = mapOf("nickname" to "novel"),
                headers = mapOf("Authorization" to "Bearer token")
            )
        )

        assertEquals("post-json-ok", result)
        assertEquals("POST_JSON", executor.lastMethod)
        assertEquals(mapOf("nickname" to "novel"), executor.lastJsonBody)
        assertEquals(mapOf("Authorization" to "Bearer token"), executor.lastHeaders)
    }

    @Test
    fun execute_throwsOriginalErrorWhenLegacyExecutorFails() {
        val failure = IllegalStateException("network failed")
        val executor = RecordingLegacyApiExecutor(
            error = failure
        )
        val adapter = LegacyApiServiceAdapter(executor)

        val result = runCatching {
            runBlocking {
                adapter.execute(
                    NetworkRequest(
                        baseUrl = "https://example.com/",
                        endpoint = "books/detail",
                        method = NetworkRequestMethod.GET
                    )
                )
            }
        }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals("network failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun execute_throwsIoExceptionWhenLegacyExecutorReturnsNothing() {
        val executor = RecordingLegacyApiExecutor()
        val adapter = LegacyApiServiceAdapter(executor)

        val result = runCatching {
            runBlocking {
                adapter.execute(
                    NetworkRequest(
                        baseUrl = "https://example.com/",
                        endpoint = "books/detail",
                        method = NetworkRequestMethod.GET
                    )
                )
            }
        }

        assertTrue(result.exceptionOrNull() is IOException)
    }

    private class RecordingLegacyApiExecutor(
        private val response: String? = null,
        private val error: Throwable? = null
    ) : LegacyApiExecutor {
        var lastMethod: String? = null
        var lastBaseUrl: String? = null
        var lastEndpoint: String? = null
        var lastParams: Map<String, String> = emptyMap()
        var lastJsonBody: Map<String, Any?> = emptyMap()
        var lastHeaders: Map<String, String> = emptyMap()

        override fun get(
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("GET", baseUrl, endpoint, params, emptyMap(), headers)
            callback(response, error)
        }

        override fun post(
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("POST", baseUrl, endpoint, params, emptyMap(), headers)
            callback(response, error)
        }

        override fun postJson(
            baseUrl: String,
            endpoint: String,
            params: Map<String, Any?>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("POST_JSON", baseUrl, endpoint, emptyMap(), params, headers)
            callback(response, error)
        }

        override fun postQuery(
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("POST_QUERY", baseUrl, endpoint, params, emptyMap(), headers)
            callback(response, error)
        }

        override fun put(
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("PUT", baseUrl, endpoint, params, emptyMap(), headers)
            callback(response, error)
        }

        override fun delete(
            baseUrl: String,
            endpoint: String,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("DELETE", baseUrl, endpoint, emptyMap(), emptyMap(), headers)
            callback(response, error)
        }

        override fun patch(
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            headers: Map<String, String>,
            callback: (String?, Throwable?) -> Unit
        ) {
            record("PATCH", baseUrl, endpoint, params, emptyMap(), headers)
            callback(response, error)
        }

        private fun record(
            method: String,
            baseUrl: String,
            endpoint: String,
            params: Map<String, String>,
            jsonBody: Map<String, Any?>,
            headers: Map<String, String>
        ) {
            lastMethod = method
            lastBaseUrl = baseUrl
            lastEndpoint = endpoint
            lastParams = params
            lastJsonBody = jsonBody
            lastHeaders = headers
        }
    }
}
