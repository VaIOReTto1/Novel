package com.novel.utils.network.interceptor

import androidx.compose.runtime.Stable
import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Stable
@Singleton
class RequestIdInterceptor @Inject constructor() : Interceptor {

    companion object {
        const val REQUEST_ID_HEADER = "X-Request-Id"
        const val TRACE_ID_HEADER = "X-Trace-Id"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val existingRequestId = originalRequest.header(REQUEST_ID_HEADER)
        val existingTraceId = originalRequest.header(TRACE_ID_HEADER)

        val requestId = existingRequestId ?: generateId()
        val traceId = existingTraceId ?: requestId

        val updatedRequest = originalRequest.newBuilder().apply {
            if (existingRequestId == null) {
                header(REQUEST_ID_HEADER, requestId)
            }
            if (existingTraceId == null) {
                header(TRACE_ID_HEADER, traceId)
            }
        }.build()

        return chain.proceed(updatedRequest)
    }

    private fun generateId(): String = UUID.randomUUID().toString()
}
