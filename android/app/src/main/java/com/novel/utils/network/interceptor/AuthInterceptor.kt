package com.novel.utils.network.interceptor

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.TokenProvider
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证拦截器
 * 
 * 功能：
 * - 自动为需要认证的请求添加Authorization头
 * - 支持Token刷新机制
 * - 处理认证失败的情况
 */
@Stable
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {
    
    companion object {
        private const val TAG = "AuthInterceptor"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        
        // 不需要认证的路径
        private val NO_AUTH_PATHS = setOf(
            "/api/front/user/register",
            "/api/front/user/login",
            "/api/front/user/logout",
            "/api/front/home",
            "/api/front/book/category",
            "/api/front/book/rank"
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 检查是否需要认证
        if (!requiresAuth(originalRequest)) {
            TimberLogger.d(TAG, "请求无需认证: ${originalRequest.url.encodedPath}")
            return chain.proceed(originalRequest)
        }
        
        // 获取token
        val token = tokenProvider.getToken()
        
        if (token.isNullOrBlank()) {
            TimberLogger.d(TAG, "无可用token，直接发送请求: ${originalRequest.url.encodedPath}")
            return chain.proceed(originalRequest)
        }
        
        // 添加认证头
        val authenticatedRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "$BEARER_PREFIX$token")
            .build()
        
        TimberLogger.d(TAG, "添加认证头: ${authenticatedRequest.url.encodedPath}")
        
        val response = chain.proceed(authenticatedRequest)
        
        // 处理认证失败的情况
        if (response.code == 401) {
            TimberLogger.w(TAG, "认证失败 (401): ${authenticatedRequest.url}")
            // 这里可以触发token刷新逻辑
            // 目前只是记录日志
        }
        
        return response
    }
    
    /**
     * 判断请求是否需要认证
     */
    private fun requiresAuth(request: Request): Boolean {
        val path = request.url.encodedPath
        
        // 检查是否在无需认证的路径列表中
        if (NO_AUTH_PATHS.any { path.startsWith(it) }) {
            return false
        }
        
        // 检查请求头是否明确指定不需要认证
        if (request.header("X-No-Auth") != null) {
            return false
        }
        
        // 检查是否已经有Authorization头
        if (request.header(AUTHORIZATION_HEADER) != null) {
            return false
        }
        
        // 默认需要认证
        return true
    }
}

/**
 * Request扩展方法，用于标记不需要认证的请求
 */
fun Request.withoutAuth(): Request {
    return this.newBuilder()
        .header("X-No-Auth", "true")
        .build()
}
