package com.novel.page.welfare.utils

import android.net.Uri
import com.novel.BuildConfig

object WelfareWebSecurityConfig {
    const val DEFAULT_TITLE = "福利页面 - Bing"
    const val DEFAULT_URL = BuildConfig.WELFARE_DEFAULT_URL

    private val allowedMainFrameHosts: Set<String> = buildSet {
        Uri.parse(DEFAULT_URL).host?.lowercase()?.let(::add)
        add("bing.com")
        add("www.bing.com")
        add("cn.bing.com")
    }

    fun isAllowedMainFrameUri(uri: Uri?): Boolean {
        if (uri == null) return false
        val scheme = uri.scheme?.lowercase()
        return when (scheme) {
            null, "", "about", "data", "javascript" -> true
            "http", "https" -> uri.host?.lowercase() in allowedMainFrameHosts
            else -> false
        }
    }

    fun shouldOpenExternally(uri: Uri?): Boolean {
        if (uri == null) return false
        val scheme = uri.scheme?.lowercase()
        return when (scheme) {
            null, "", "about", "data", "javascript" -> false
            "http", "https" -> !isAllowedMainFrameUri(uri)
            else -> true
        }
    }

    fun isAllowedMainFrameUrl(url: String): Boolean {
        return runCatching { isAllowedMainFrameUri(Uri.parse(url)) }.getOrDefault(false)
    }
}
