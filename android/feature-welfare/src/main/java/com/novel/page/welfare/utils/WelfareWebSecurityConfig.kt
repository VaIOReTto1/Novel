package com.novel.page.welfare.utils

import android.net.Uri
import com.novel.feature.welfare.BuildConfig
import java.net.URI

object WelfareWebSecurityConfig {
    const val DEFAULT_TITLE = "福利页面 - Bing"
    const val DEFAULT_URL = BuildConfig.WELFARE_DEFAULT_URL

    private val allowedMainFrameHosts: Set<String> = buildSet {
        parseHost(DEFAULT_URL)?.let(::add)
        add("bing.com")
        add("www.bing.com")
        add("cn.bing.com")
    }

    private fun parseHost(url: String): String? {
        return runCatching { URI(url).host?.lowercase() }.getOrNull()
    }

    private fun isAllowedUri(scheme: String?, host: String?): Boolean {
        return when (scheme?.lowercase()) {
            null, "", "about", "data", "javascript" -> true
            "http", "https" -> host?.lowercase() in allowedMainFrameHosts
            else -> false
        }
    }

    fun isAllowedMainFrameUri(uri: Uri?): Boolean {
        if (uri == null) return false
        return isAllowedUri(uri.scheme, uri.host)
    }

    fun shouldOpenExternally(uri: Uri?): Boolean {
        if (uri == null) return false
        return when (uri.scheme?.lowercase()) {
            null, "", "about", "data", "javascript" -> false
            "http", "https" -> !isAllowedMainFrameUri(uri)
            else -> true
        }
    }

    fun isAllowedMainFrameUrl(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        return isAllowedUri(uri.scheme, uri.host)
    }

    fun shouldOpenExternallyUrl(url: String): Boolean {
        val uri = runCatching { URI(url) }.getOrNull() ?: return false
        return when (uri.scheme?.lowercase()) {
            null, "", "about", "data", "javascript" -> false
            "http", "https" -> !isAllowedUri(uri.scheme, uri.host)
            else -> true
        }
    }
}
