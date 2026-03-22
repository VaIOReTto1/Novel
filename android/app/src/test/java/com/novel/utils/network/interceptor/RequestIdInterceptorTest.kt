package com.novel.utils.network.interceptor

import com.google.common.truth.Truth.assertThat
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test

class RequestIdInterceptorTest {

    private val interceptor = RequestIdInterceptor()

    @Test
    fun `adds request id and trace id when missing`() {
        val request = Request.Builder()
            .url("https://example.com/books")
            .build()

        val response = interceptor.intercept(FakeChain(request))
        val interceptedRequest = response.request

        val requestId = interceptedRequest.header(RequestIdInterceptor.REQUEST_ID_HEADER)
        val traceId = interceptedRequest.header(RequestIdInterceptor.TRACE_ID_HEADER)

        assertThat(requestId).isNotNull()
        assertThat(requestId).isNotEmpty()
        assertThat(traceId).isEqualTo(requestId)
    }

    @Test
    fun `preserves existing request id and trace id`() {
        val request = Request.Builder()
            .url("https://example.com/books")
            .header(RequestIdInterceptor.REQUEST_ID_HEADER, "request-123")
            .header(RequestIdInterceptor.TRACE_ID_HEADER, "trace-456")
            .build()

        val response = interceptor.intercept(FakeChain(request))
        val interceptedRequest = response.request

        assertThat(interceptedRequest.header(RequestIdInterceptor.REQUEST_ID_HEADER)).isEqualTo("request-123")
        assertThat(interceptedRequest.header(RequestIdInterceptor.TRACE_ID_HEADER)).isEqualTo("trace-456")
    }

    @Test
    fun `reuses request id for trace id when trace id missing`() {
        val request = Request.Builder()
            .url("https://example.com/books")
            .header(RequestIdInterceptor.REQUEST_ID_HEADER, "request-123")
            .build()

        val response = interceptor.intercept(FakeChain(request))
        val interceptedRequest = response.request

        assertThat(interceptedRequest.header(RequestIdInterceptor.REQUEST_ID_HEADER)).isEqualTo("request-123")
        assertThat(interceptedRequest.header(RequestIdInterceptor.TRACE_ID_HEADER)).isEqualTo("request-123")
    }

    @Test
    fun `preserves existing trace id when request id missing`() {
        val request = Request.Builder()
            .url("https://example.com/books")
            .header(RequestIdInterceptor.TRACE_ID_HEADER, "trace-456")
            .build()

        val response = interceptor.intercept(FakeChain(request))
        val interceptedRequest = response.request

        assertThat(interceptedRequest.header(RequestIdInterceptor.TRACE_ID_HEADER)).isEqualTo("trace-456")
        assertThat(interceptedRequest.header(RequestIdInterceptor.REQUEST_ID_HEADER)).isNotNull()
    }

    @Test
    fun `ensureTraceHeaders adds trace headers to plain header map`() {
        val headers = RequestIdInterceptor.ensureTraceHeaders(mapOf("Accept" to "*/*"))

        assertThat(headers["Accept"]).isEqualTo("*/*")
        assertThat(headers[RequestIdInterceptor.REQUEST_ID_HEADER]).isNotNull()
        assertThat(headers[RequestIdInterceptor.TRACE_ID_HEADER])
            .isEqualTo(headers[RequestIdInterceptor.REQUEST_ID_HEADER])
    }

    private class FakeChain(
        private val request: Request
    ) : Interceptor.Chain {

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody("application/json".toMediaType()))
                .build()
        }

        override fun call(): Call {
            throw UnsupportedOperationException("Not needed for this test")
        }

        override fun connection(): Connection? = null

        override fun connectTimeoutMillis(): Int = 0

        override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 0

        override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 0

        override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
    }
}
