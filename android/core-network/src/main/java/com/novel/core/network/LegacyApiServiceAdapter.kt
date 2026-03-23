package com.novel.core.network

import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

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

class LegacyApiServiceAdapter(
    private val executor: LegacyApiExecutor
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
