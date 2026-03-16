package com.novel.core.network

import com.novel.utils.network.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LegacyApiExecutor {
    fun get(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun post(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun postJson(
        baseUrl: String,
        endpoint: String,
        params: Map<String, Any?>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun postQuery(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun put(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun delete(
        baseUrl: String,
        endpoint: String,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )

    fun patch(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    )
}

object DefaultLegacyApiExecutor : LegacyApiExecutor {
    private val gson = Gson()

    override fun get(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.get(baseUrl, endpoint, params, headers, callback)
    }

    override fun post(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.post(baseUrl, endpoint, params, headers, callback)
    }

    override fun postJson(
        baseUrl: String,
        endpoint: String,
        params: Map<String, Any?>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.postJson(baseUrl, endpoint, params, headers, callback)
    }

    override fun postQuery(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.postQuery(baseUrl, endpoint, params, headers, callback)
    }

    override fun put(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.put(baseUrl, endpoint, createJsonBody(params), headers, callback)
    }

    override fun delete(
        baseUrl: String,
        endpoint: String,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.delete(baseUrl, endpoint, headers, callback)
    }

    override fun patch(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>,
        headers: Map<String, String>,
        callback: (String?, Throwable?) -> Unit
    ) {
        ApiService.patch(baseUrl, endpoint, params, headers, callback)
    }

    private fun createJsonBody(params: Map<String, String>): RequestBody {
        return gson.toJson(params)
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }
}

class LegacyApiServiceAdapter(
    private val executor: LegacyApiExecutor = DefaultLegacyApiExecutor
) : NetworkFacade {

    override suspend fun execute(request: NetworkRequest): String =
        suspendCancellableCoroutine { continuation ->
            val callback = callback@{ body: String?, throwable: Throwable? ->
                if (!continuation.isActive) {
                    return@callback
                }

                when {
                    throwable != null -> continuation.resumeWithException(throwable)
                    body != null -> continuation.resume(body)
                    else -> continuation.resumeWithException(
                        IOException("LegacyApiServiceAdapter returned null body and null error")
                    )
                }
            }

            when (request.method) {
                NetworkRequestMethod.GET -> executor.get(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.queryParams,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.POST -> executor.post(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.bodyParams,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.POST_JSON -> executor.postJson(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.jsonBodyParams,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.POST_QUERY -> executor.postQuery(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.queryParams,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.PUT -> executor.put(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.bodyParams,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.DELETE -> executor.delete(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    headers = request.headers,
                    callback = callback
                )

                NetworkRequestMethod.PATCH -> executor.patch(
                    baseUrl = request.baseUrl,
                    endpoint = request.endpoint,
                    params = request.bodyParams,
                    headers = request.headers,
                    callback = callback
                )
            }
        }
}
