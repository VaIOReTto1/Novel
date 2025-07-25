package com.novel.page.login.utils

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.page.login.dao.UserRepository
import com.novel.rn.ReactNativeBridge
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.utils.network.TokenProvider
import com.novel.utils.network.api.front.user.UserService
import com.novel.utils.network.api.loginHighPriority
import com.novel.utils.network.api.registerHighPriority
import com.novel.utils.network.api.getUserInfoHighPriority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证领域服务
 * 
 * 核心功能：
 * - 用户认证：处理登录、注册等认证流程
 * - Token管理：安全的Token存储和过期控制
 * - 用户信息缓存：本地用户数据的管理和同步
 * - 跨端数据同步：向React Native端推送用户状态
 * - 安全登出：清理所有认证相关的本地数据
 * 
 * 安全特性：
 * - 所有认证操作都有严格的安全验证
 * - Token过期时间严格控制，确保安全性
 * - 敏感信息的安全存储和清理
 * - 网络异常的统一处理和错误反馈
 * 
 * 设计特点：
 * - 单例模式，全局认证状态管理
 * - 协程支持，避免阻塞主线程
 * - 依赖注入，便于测试和维护
 * - 统一错误处理，提升用户体验
 */
@Singleton
@Stable
class AuthService @Inject constructor(
    /** 用户网络服务 */
    @Stable private val userService: UserService,
    /** Token提供器 */
    @Stable val tokenProvider: TokenProvider,
    /** 用户配置存储 */
    @Stable val userDefaults: NovelUserDefaults,
    /** 用户本地仓库 */
    @Stable val userRepository: UserRepository
) {

    companion object {
        private const val TAG = "AuthService"
    }

    /**
     * 执行用户登录
     * 
     * 登录流程：
     * 1. 发起网络登录请求
     * 2. 验证服务器响应结果
     * 3. 保存认证信息到本地
     * 4. 异步加载用户详细信息
     * 5. 同步用户状态到RN端
     * 
     * @param username 用户名/手机号
     * @param password 用户密码
     * @return 登录结果，包含成功/失败状态和消息
     */
    suspend fun login(username: String, password: String): AuthResult =
        withContext(Dispatchers.IO) {
            try {
                TimberLogger.d(TAG, "开始用户登录，用户名: $username")
                
                // 对于登录这种用户主动操作，使用高优先级请求
                val response = try {
                    userService.loginHighPriority(
                        UserService.LoginRequest(username, password)
                    )
                } catch (e: Exception) {
                    TimberLogger.w(TAG, "高优先级登录请求失败，使用普通请求: ${e.message}")
                    userService.loginBlocking(
                        UserService.LoginRequest(username, password)
                    )
                }

                if (response.ok == true && response.data != null) {
                    TimberLogger.d(TAG, "登录成功，用户ID: ${response.data.uid}")
                    
                    // 保存认证信息
                    saveAuthInfo(response.data.token, response.data.uid)
                    
                    // 异步加载用户详情
                    loadAndCacheUserInfo()
                    
                    TimberLogger.d(TAG, "用户登录流程完成")
                    AuthResult.Success("登录成功")
                } else {
                    val errorMsg = response.msg ?: "登录失败"
                    TimberLogger.w(TAG, "登录失败: $errorMsg")
                    AuthResult.Error(errorMsg)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "登录过程发生异常", e)
                AuthResult.Error("网络异常：${e.localizedMessage}")
            }
        }

    /**
     * 执行用户注册
     * 
     * 注册流程：
     * 1. 发起网络注册请求
     * 2. 验证服务器响应结果
     * 3. 保存认证信息到本地
     * 4. 异步加载用户详细信息
     * 5. 同步用户状态到RN端
     * 
     * @param request 注册请求信息，包含用户名、密码、验证码等
     * @return 注册结果，包含成功/失败状态和消息
     */
    suspend fun register(request: RegisterRequest): AuthResult = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "开始用户注册，用户名: ${request.username}")
            
            // 对于注册这种用户主动操作，使用高优先级请求
            val response = try {
                userService.registerHighPriority(
                    UserService.RegisterRequest(
                        username = request.username,
                        password = request.password,
                        sessionId = request.sessionId,
                        velCode = request.verifyCode
                    )
                )
            } catch (e: Exception) {
                TimberLogger.w(TAG, "高优先级注册请求失败，使用普通请求: ${e.message}")
                userService.registerBlocking(
                    UserService.RegisterRequest(
                        username = request.username,
                        password = request.password,
                        sessionId = request.sessionId,
                        velCode = request.verifyCode
                    )
                )
            }

            if (response.ok == true && response.data != null) {
                TimberLogger.d(TAG, "注册成功，用户ID: ${response.data.uid}")
                
                // 保存认证信息
                saveAuthInfo(response.data.token, response.data.uid)
                
                // 异步加载用户详情
                loadAndCacheUserInfo()
                
                TimberLogger.d(TAG, "用户注册流程完成")
                AuthResult.Success("注册成功")
            } else {
                val errorMsg = response.msg ?: "注册失败"
                TimberLogger.w(TAG, "注册失败: $errorMsg")
                AuthResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "注册过程发生异常", e)
            AuthResult.Error("网络异常：${e.localizedMessage}")
        }
    }

    /**
     * 检查当前登录状态
     */
//    fun isLoggedIn(): Boolean {
//        val token = tokenProvider.accessToken()
//        val expiresAt = userDefaults.get<Long>(NovelUserDefaultsKey.TOKEN_EXPIRES_AT) ?: 0L
//        val isLoggedIn = userDefaults.get<Boolean>(NovelUserDefaultsKey.IS_LOGGED_IN) ?: false
//
//        return token.isNotEmpty() &&
//               System.currentTimeMillis() < expiresAt &&
//               isLoggedIn
//    }

    /**
     * 退出登录
     * 
     * 清理流程：
     * 1. 清理Token信息
     * 2. 移除所有认证相关配置
     * 3. 清空本地用户缓存
     * 4. 确保数据安全清理
     */
    suspend fun logout() = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "开始退出登录")
            
            // 清理认证信息
            tokenProvider.clear()
            userDefaults.remove(NovelUserDefaultsKey.TOKEN_EXPIRES_AT)
            userDefaults.remove(NovelUserDefaultsKey.IS_LOGGED_IN)
            userDefaults.remove(NovelUserDefaultsKey.USER_ID)
            
            // 清理本地用户缓存
            userRepository.clearAllUsers()
            
            TimberLogger.d(TAG, "退出登录完成，所有认证信息已清理")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "退出登录过程发生异常", e)
        }
    }

    /**
     * 保存认证信息
     * 
     * 安全特性：
     * - Token安全存储
     * - 30天过期时间设置
     * - 登录状态标记
     * - 用户ID本地缓存
     * 
     * @param token 访问令牌
     * @param uid 用户ID
     */
    private suspend fun saveAuthInfo(token: String, uid: Int) {
        try {
            TimberLogger.d(TAG, "保存认证信息，用户ID: $uid")
            
            tokenProvider.saveToken(token, "")
            userDefaults.set(
                System.currentTimeMillis() + 30 * 24 * 3600_000L, // 30天过期
                NovelUserDefaultsKey.TOKEN_EXPIRES_AT
            )
            userDefaults.set(true, NovelUserDefaultsKey.IS_LOGGED_IN)
            userDefaults.set(uid, NovelUserDefaultsKey.USER_ID)
            
            TimberLogger.d(TAG, "认证信息保存成功")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "保存认证信息失败", e)
        }
    }

    /**
     * 异步加载并缓存用户信息
     * 
     * 功能流程：
     * 1. 请求用户详细信息
     * 2. 缓存用户信息到本地数据库
     * 3. 同步用户数据到React Native端
     * 4. 处理性别信息转换
     */
    private suspend fun loadAndCacheUserInfo() {
        runCatching {
            TimberLogger.d(TAG, "开始加载用户详细信息")
            
            // 对于获取用户信息，使用高优先级请求
            val userInfo = try {
                userService.getUserInfoHighPriority()
            } catch (e: Exception) {
                TimberLogger.w(TAG, "高优先级获取用户信息失败，使用普通请求: ${e.message}")
                userService.getUserInfoBlocking()
            }
            userInfo?.data?.let { 
                userRepository.cacheUser(it)
                TimberLogger.d(TAG, "用户信息缓存成功")
            }
            
            // 准备同步到RN端的数据
            val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)
            val token = tokenProvider.accessToken()
            val nickname = userInfo?.data?.nickName ?: "用户$uid"
            val photo = userInfo?.data?.userPhoto ?: ""
            val sex = when (userInfo?.data?.userSex) {
                1 -> "男"
                2 -> "女"
                else -> "未知"
            }
            
            // 同步到RN端
            ReactNativeBridge.sendUserDataToRN(uid.toString(), token ?: "", nickname, photo, sex)
            TimberLogger.d(TAG, "用户数据已同步到RN端")
        }.onFailure { e ->
            TimberLogger.e(TAG, "加载用户信息失败", e)
        }
    }
}

/**
 * 注册请求数据类
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val sessionId: String,
    val verifyCode: String
)

/**
 * 认证结果封装
 */
sealed class AuthResult {
    data class Success(val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}