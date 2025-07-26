package com.novel.rn.bridge

import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.utils.TimberLogger
import com.novel.MainApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.os.Handler
import android.os.Looper
import com.novel.utils.NavViewModel
import kotlinx.coroutines.delay
import com.novel.page.login.dao.UserRepository
import com.novel.utils.network.TokenProvider
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import com.novel.rn.ReactNativeBridge
import androidx.compose.runtime.Stable

/**
 * 桥接ViewModel
 * 
 * 继承BaseMviViewModel，实现桥接相关的MVI模式：
 * - 导航操作管理
 * - RN组件缓存管理
 * - 跨端状态同步
 */
@HiltViewModel
class BridgeViewModel @Inject constructor(
    @Stable
    private val userRepository: UserRepository,
    @Stable
    private val tokenProvider: TokenProvider,
    @Stable
    private val userDefaults: NovelUserDefaults
) : BaseMviViewModel<BridgeIntent, BridgeState, BridgeEffect>() {
    
    companion object {
        private const val TAG = "BridgeViewModel"
    }
    
    private val bridgeReducer = BridgeReducer()
    
    /** 状态适配器，提供便利的状态访问方法 */
    val adapter = BridgeStateAdapter(state)
    
    init {
        TimberLogger.d(TAG, "BridgeViewModel初始化")
        // 初始化时检查桥接状态
        sendIntent(BridgeIntent.InitializeBridge)
    }
    
    override fun createInitialState(): BridgeState {
        return BridgeState()
    }
    
    override fun getReducer(): MviReducer<BridgeIntent, BridgeState> {
        // 返回一个适配器，将MviReducerWithEffect适配为MviReducer
        return object : MviReducer<BridgeIntent, BridgeState> {
            override fun reduce(currentState: BridgeState, intent: BridgeIntent): BridgeState {
                val result = bridgeReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }
    
    override fun onIntentProcessed(intent: BridgeIntent, newState: BridgeState) {
        super.onIntentProcessed(intent, newState)
        
        // 处理需要异步操作的Intent
        when (intent) {
            is BridgeIntent.InitializeBridge -> handleInitializeBridge()
            is BridgeIntent.NavigateToLogin -> handleNavigateToLogin()
            is BridgeIntent.NavigateToSettings -> handleNavigateToSettings()
            is BridgeIntent.NavigateBack -> handleNavigateBack(intent.componentName)
            is BridgeIntent.ClearComponentCache -> handleClearComponentCache(intent.componentName)
            is BridgeIntent.ClearAllComponentCache -> handleClearAllComponentCache()
            else -> {
                // 其他Intent不需要额外处理
            }
        }
    }
    
    private fun handleInitializeBridge() {
        // 异步初始化桥接
        viewModelScope.launch {
            try {
                // 模拟初始化过程
                delay(500)
                
                val result = BridgeAsyncResult.BridgeInitialized
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
                TimberLogger.d(TAG, "桥接初始化完成")
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "桥接初始化失败", e)
                handleAsyncError("桥接初始化失败: ${e.message}")
            }
        }
    }
    
    private fun handleNavigateToLogin() {
        // 执行实际的导航操作
        performNavigation {
            NavViewModel.navController.value?.navigate("login")
        }
    }
    
    private fun handleNavigateToSettings() {
        // 执行实际的导航操作
        performNavigation {
            NavViewModel.navController.value?.navigate("settings")
        }
    }
    
    private fun handleNavigateBack(componentName: String?) {
        // 如果提供了组件名，先清理缓存
        if (!componentName.isNullOrEmpty()) {
            clearComponentCacheInternal(componentName)
        }
        
        // 执行实际的返回操作
        performNavigation {
            NavViewModel.navController.value?.popBackStack()
        }
    }
    
    private fun handleClearComponentCache(componentName: String) {
        // 执行实际的缓存清理
        viewModelScope.launch {
            try {
                clearComponentCacheInternal(componentName)
                
                val result = BridgeAsyncResult.CacheOperationCompleted("已清理 $componentName 的缓存")
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理组件缓存失败", e)
                handleAsyncError("清理组件缓存失败: ${e.message}")
            }
        }
    }
    
    private fun handleClearAllComponentCache() {
        // 执行实际的缓存清理
        viewModelScope.launch {
            try {
                clearAllComponentCacheInternal()
                
                val result = BridgeAsyncResult.CacheOperationCompleted("已清理所有组件缓存")
                val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
                updateState(reduceResult.newState)
                reduceResult.effect?.let { sendEffect(it) }
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理所有组件缓存失败", e)
                handleAsyncError("清理所有组件缓存失败: ${e.message}")
            }
        }
    }
    
    /**
     * 注册组件（当ReactNativePage创建新组件时调用）
     */
    fun registerComponent(componentName: String) {
        viewModelScope.launch {
            val result = BridgeAsyncResult.ComponentRegistered(componentName)
            val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
            updateState(reduceResult.newState)
            reduceResult.effect?.let { sendEffect(it) }
            
            TimberLogger.d(TAG, "组件已注册: $componentName")
        }
    }
    
    /**
     * 路由变更通知
     */
    fun notifyRouteChanged(route: String) {
        viewModelScope.launch {
            val result = BridgeAsyncResult.RouteChanged(route)
            val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
            updateState(reduceResult.newState)
            reduceResult.effect?.let { sendEffect(it) }
            
            TimberLogger.d(TAG, "路由已变更: $route")
        }
    }
    
    /**
     * 发送用户数据到RN端
     * 当RN页面进入时调用，确保RN组件能够获取到最新的用户信息
     */
    fun sendUserDataToRN() {
        viewModelScope.launch {
            try {
                delay(2000) // 等待RN初始化完成
                
                // 获取真实的用户数据
                val currentUser = userRepository.getCurrentUser()
                val uid = userDefaults.get<Int>(NovelUserDefaultsKey.USER_ID)
                val token = tokenProvider.accessToken()
                TimberLogger.d(TAG, "获取用户数据: $currentUser, $uid, $token")

                if (currentUser != null && uid != null && !token.isNullOrEmpty()) {
                    // 发送真实用户数据
                    val sex = when (currentUser.userSex) {
                        1 -> "男"
                        2 -> "女"
                        else -> "未知"
                    }
                    ReactNativeBridge.sendUserDataToRN(
                        uid = uid.toString(),
                        token = token,
                        nickname = currentUser.nickName,
                        photo = currentUser.userPhoto,
                        sex = sex
                    )
                    TimberLogger.d(TAG, "发送真实用户数据完成: ${currentUser.nickName}")
                } else {
                    // 如果没有用户数据，发送测试数据作为后备
                    ReactNativeBridge.sendTestUserDataToRN()
                    TimberLogger.w(TAG, "用户数据不完整，使用测试数据")
                }
                
                delay(500)
                ReactNativeBridge.sendTestRecommendBooksToRN()
                TimberLogger.d(TAG, "发送RN数据完成")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "发送RN数据失败", e)
            }
        }
    }
    
    private fun performNavigation(action: () -> Unit) {
        // 确保在主线程执行导航
        Handler(Looper.getMainLooper()).post {
            try {
                action()
            } catch (e: Exception) {
                TimberLogger.e(TAG, "导航操作失败", e)
                handleAsyncError("导航操作失败: ${e.message}")
            }
        }
    }
    
    private fun clearComponentCacheInternal(componentName: String) {
        try {
            val mainApplication = getCurrentApplication()
            mainApplication?.clearReactRootViewCache(componentName)
            TimberLogger.d(TAG, "已清理 $componentName 的缓存")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理 $componentName 的缓存失败", e)
            throw e
        }
    }
    
    private fun clearAllComponentCacheInternal() {
        try {
            val mainApplication = getCurrentApplication()
            mainApplication?.clearAllReactRootViewCache()
            TimberLogger.d(TAG, "已清理所有组件缓存")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "清理所有组件缓存失败", e)
            throw e
        }
    }
    
    private fun getCurrentApplication(): MainApplication? {
        return MainApplication.getInstance()
    }
    
    private fun handleAsyncError(message: String) {
        val result = BridgeAsyncResult.Error(message)
        val reduceResult = bridgeReducer.handleAsyncResult(getCurrentState(), result)
        updateState(reduceResult.newState)
        reduceResult.effect?.let { sendEffect(it) }
    }
    
    // 添加公共方法供桥接模块使用
    fun getStateForBridge(): BridgeState = getCurrentState()
}