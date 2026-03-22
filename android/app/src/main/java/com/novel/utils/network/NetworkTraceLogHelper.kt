package com.novel.utils.network

import com.novel.core.network.NetworkRequest
import com.novel.utils.network.interceptor.RequestIdInterceptor
import okhttp3.Request

object NetworkTraceLogHelper {

    fun formatLegacyDispatch(
        method: String,
        baseUrl: String,
        endpoint: String,
        headers: Map<String, String>,
    ): String {
        return buildString {
            append("channel=legacy-api")
            append(" method=$method")
            append(" baseUrl=$baseUrl")
            append(" endpoint=$endpoint")
            append(" requestId=${headers[RequestIdInterceptor.REQUEST_ID_HEADER]}")
            append(" traceId=${headers[RequestIdInterceptor.TRACE_ID_HEADER]}")
        }
    }

    fun formatOkHttpDispatch(request: Request): String {
        return buildString {
            append("channel=retrofit-client")
            append(" method=${request.method}")
            append(" url=${request.url}")
            append(" requestId=${request.header(RequestIdInterceptor.REQUEST_ID_HEADER)}")
            append(" traceId=${request.header(RequestIdInterceptor.TRACE_ID_HEADER)}")
        }
    }

    fun formatBridgeDispatch(request: NetworkRequest): String {
        return buildString {
            append("channel=bridge-gateway")
            append(" method=${request.method}")
            append(" baseUrl=${request.baseUrl}")
            append(" endpoint=${request.endpoint}")
            append(" requestId=${request.headers[RequestIdInterceptor.REQUEST_ID_HEADER]}")
            append(" traceId=${request.headers[RequestIdInterceptor.TRACE_ID_HEADER]}")
        }
    }
}
