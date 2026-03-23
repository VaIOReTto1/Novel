package com.novel.core.network.legacy

import com.google.gson.Gson
import com.novel.core.network.LegacyApiExecutor
import com.novel.utils.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

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
