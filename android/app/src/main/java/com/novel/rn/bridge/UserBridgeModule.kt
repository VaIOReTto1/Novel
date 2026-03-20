package com.novel.rn.bridge

import androidx.compose.runtime.Stable
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.novel.core.config.RefactorFeatureFlags
import com.novel.utils.TimberLogger
import com.novel.page.login.dao.UserRepository
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.utils.network.TokenProvider
import kotlinx.coroutines.launch
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.novel.rn.bridge.rejectMapped

/**
 * 用户数据桥接模块
 * 提供用户信息的同步获取和异步推送功能
 */
@Stable
class UserBridgeModule(
    @Stable
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val TAG = "UserBridgeModule"
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface UserBridgeEntryPoint {
        fun userRepository(): UserRepository
        fun novelUserDefaults(): NovelUserDefaults
        fun tokenProvider(): TokenProvider
        fun bridgeCoroutineScopes(): BridgeCoroutineScopes
        fun refactorFeatureFlags(): RefactorFeatureFlags
    }

    private val entryPoint: UserBridgeEntryPoint by lazy {
        EntryPointAccessors.fromApplication(
            reactContext.applicationContext,
            UserBridgeEntryPoint::class.java
        )
    }
    
    private val userRepository: UserRepository by lazy {
        entryPoint.userRepository()
    }
    
    private val userDefaults: NovelUserDefaults by lazy {
        entryPoint.novelUserDefaults()
    }
    
    private val tokenProvider: TokenProvider by lazy {
        entryPoint.tokenProvider()
    }

    private val bridgeCoroutineScopes: BridgeCoroutineScopes by lazy {
        entryPoint.bridgeCoroutineScopes()
    }

    private val refactorFeatureFlags: RefactorFeatureFlags by lazy {
        entryPoint.refactorFeatureFlags()
    }

    override fun getName(): String = "UserBridge"

    /**
     * 获取当前用户数据（同步方法，返回Promise）
     */
    @ReactMethod
    fun getCurrentUserData(promise: Promise) {
        bridgeCoroutineScopes.io.launch {
            try {
                TimberLogger.d(TAG, "开始获取当前用户数据")
                
                // 获取用户基本信息
                val currentUser = userRepository.getCurrentUser()
                val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)
                val token = tokenProvider.accessToken()
                
                if (currentUser != null && uid != null && !token.isNullOrEmpty()) {
                    // 构建用户数据
                    val sex = when (currentUser.userSex) {
                        1 -> "男"
                        2 -> "女"
                        else -> "未知"
                    }
                    
                    val userData = Arguments.createMap().apply {
                        putString("uid", uid.toString())
                        putString("token", token)
                        putString("nickname", currentUser.nickName)
                        putString("photo", currentUser.userPhoto)
                        putString("sex", sex)
                    }
                    
                    TimberLogger.d(TAG, "✅ 获取用户数据成功: ${currentUser.nickName}")
                    promise.resolve(userData)
                } else {
                    TimberLogger.w(TAG, "⚠️ 用户数据不完整，返回null")
                    promise.resolve(null)
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "❌ 获取用户数据失败", e)
                promise.rejectMapped(
                    throwable = e,
                    defaultCode = "USER_DATA_ERROR",
                    defaultMessagePrefix = "获取用户数据失败",
                    enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                )
            }
        }
    }

    /**
     * 检查用户登录状态
     */
    @ReactMethod
    fun isUserLoggedIn(promise: Promise) {
        bridgeCoroutineScopes.io.launch {
            try {
                val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)
                val token = tokenProvider.accessToken()
                val isLoggedIn = uid != null && !token.isNullOrEmpty()
                
                TimberLogger.d(TAG, "用户登录状态: $isLoggedIn")
                promise.resolve(isLoggedIn)
            } catch (e: Exception) {
                TimberLogger.e(TAG, "检查登录状态失败", e)
                promise.rejectMapped(
                    throwable = e,
                    defaultCode = "LOGIN_STATUS_ERROR",
                    defaultMessagePrefix = "检查登录状态失败",
                    enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                )
            }
        }
    }

    /**
     * 获取用户余额和金币信息
     */
    @ReactMethod
    fun getUserBalance(promise: Promise) {
        bridgeCoroutineScopes.io.launch {
            try {
                // 这里可以添加获取余额和金币的逻辑
                // 目前返回模拟数据
                val balanceData = Arguments.createMap().apply {
                    putDouble("balance", 0.0)
                    putInt("coins", 0)
                }
                
                TimberLogger.d(TAG, "获取用户余额信息")
                promise.resolve(balanceData)
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取余额信息失败", e)
                promise.rejectMapped(
                    throwable = e,
                    defaultCode = "BALANCE_ERROR",
                    defaultMessagePrefix = "获取余额信息失败",
                    enabled = refactorFeatureFlags.enableBridgeErrorMapper()
                )
            }
        }
    }
}
