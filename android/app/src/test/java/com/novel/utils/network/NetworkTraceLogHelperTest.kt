package com.novel.utils.network

import com.google.common.truth.Truth.assertThat
import com.novel.core.network.NetworkRequest
import com.novel.core.network.NetworkRequestMethod
import com.novel.utils.network.interceptor.RequestIdInterceptor
import okhttp3.Request
import org.junit.Test

class NetworkTraceLogHelperTest {

    @Test
    fun formatLegacyDispatch_includesRequestAndTraceIds() {
        val headers = RequestIdInterceptor.ensureTraceHeaders(mapOf("Accept" to "*/*"))

        val message = NetworkTraceLogHelper.formatLegacyDispatch(
            method = "GET",
            baseUrl = "https://example.com/",
            endpoint = "books",
            headers = headers,
        )

        assertThat(message).contains("requestId=${headers[RequestIdInterceptor.REQUEST_ID_HEADER]}")
        assertThat(message).contains("traceId=${headers[RequestIdInterceptor.TRACE_ID_HEADER]}")
        assertThat(message).contains("method=GET")
        assertThat(message).contains("endpoint=books")
    }

    @Test
    fun formatOkHttpDispatch_readsHeadersFromRequestObject() {
        val headers = RequestIdInterceptor.ensureTraceHeaders(emptyMap())
        val request = Request.Builder()
            .url("https://example.com/books")
            .header(RequestIdInterceptor.REQUEST_ID_HEADER, headers.getValue(RequestIdInterceptor.REQUEST_ID_HEADER))
            .header(RequestIdInterceptor.TRACE_ID_HEADER, headers.getValue(RequestIdInterceptor.TRACE_ID_HEADER))
            .build()

        val message = NetworkTraceLogHelper.formatOkHttpDispatch(request)

        assertThat(message).contains("requestId=${headers[RequestIdInterceptor.REQUEST_ID_HEADER]}")
        assertThat(message).contains("traceId=${headers[RequestIdInterceptor.TRACE_ID_HEADER]}")
        assertThat(message).contains("url=https://example.com/books")
    }

    @Test
    fun formatBridgeDispatch_readsHeadersFromNetworkRequest() {
        val headers = RequestIdInterceptor.ensureTraceHeaders(mapOf("Accept" to "*/*"))
        val request = NetworkRequest(
            baseUrl = "https://front.example/",
            endpoint = "home/books",
            method = NetworkRequestMethod.GET,
            headers = headers,
        )

        val message = NetworkTraceLogHelper.formatBridgeDispatch(request)

        assertThat(message).contains("requestId=${headers[RequestIdInterceptor.REQUEST_ID_HEADER]}")
        assertThat(message).contains("traceId=${headers[RequestIdInterceptor.TRACE_ID_HEADER]}")
        assertThat(message).contains("endpoint=home/books")
    }
}
