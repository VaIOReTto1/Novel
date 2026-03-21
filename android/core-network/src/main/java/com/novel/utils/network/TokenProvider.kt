package com.novel.utils.network

import androidx.compose.runtime.Stable

/**
 * Token提供器接口
 * 
 * 用于为网络请求提供认证令牌
 */
@Stable
interface TokenProvider {
    /**
     * 获取当前有效的认证令牌
     * @return 认证令牌，如果未登录则返回null
     */
    fun getToken(): String?
    
    /**
     * 获取访问令牌
     * @return 访问令牌
     */
    fun accessToken(): String?
    
    /**
     * 保存令牌
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    suspend fun saveToken(accessToken: String, refreshToken: String)
    
    /**
     * 清除所有令牌
     */
    suspend fun clear()
}
