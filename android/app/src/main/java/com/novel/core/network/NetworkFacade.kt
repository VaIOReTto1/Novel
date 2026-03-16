package com.novel.core.network

enum class NetworkRequestMethod {
    GET,
    POST,
    POST_JSON,
    POST_QUERY,
    PUT,
    DELETE,
    PATCH
}

data class NetworkRequest(
    val baseUrl: String,
    val endpoint: String,
    val method: NetworkRequestMethod,
    val queryParams: Map<String, String> = emptyMap(),
    val bodyParams: Map<String, String> = emptyMap(),
    val jsonBodyParams: Map<String, Any?> = emptyMap(),
    val headers: Map<String, String> = emptyMap()
)

interface NetworkFacade {
    suspend fun execute(request: NetworkRequest): String
}
