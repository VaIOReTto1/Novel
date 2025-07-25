package com.novel.utils.network.interceptor

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.utils.network.api.OptimizedServiceExtensions
import com.novel.utils.network.priority.RequestPriority
import com.novel.utils.network.priority.withPriority
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 优先级拦截器
 * 
 * 功能：
 * - 从ThreadLocal中读取请求优先级
 * - 将优先级信息附加到请求中
 * - 配合PriorityRequestDispatcher工作
 */
@Stable
@Singleton
class PriorityInterceptor @Inject constructor() : Interceptor {
    
    companion object {
        private const val TAG = "PriorityInterceptor"
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 从ThreadLocal获取当前优先级
        val priority = OptimizedServiceExtensions.getCurrentRequestPriority() ?: RequestPriority.MEDIUM
        
        // 将优先级信息附加到请求中
        val requestWithPriority = originalRequest.withPriority(priority)
        
        TimberLogger.d(TAG, "应用请求优先级: $priority 到 ${originalRequest.url}")
        
        return chain.proceed(requestWithPriority)
    }
} 