package com.novel.rn.bridge

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.facebook.react.bridge.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.novel.ComposeMainActivity
import com.novel.MainApplication
import com.novel.utils.NavViewModel
import com.novel.rn.settings.SettingsViewModel
import com.novel.utils.network.api.author.ai.AiService
import com.novel.utils.network.ApiService
import com.novel.utils.network.ApiService.BASE_URL_FRONT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.atomic.AtomicBoolean
import com.novel.rn.settings.SettingsEffect
import com.novel.rn.settings.SettingsIntent
import org.json.JSONObject

/**
 * 导航桥接模块
 * 
 * 专门处理导航相关的RN调用，通过BridgeViewModel管理状态：
 * - 页面导航操作
 * - 组件缓存管理
 * - 返回操作处理
 */
@Stable
class NavigationBridgeModule(
    @Stable
    private val reactContext: ReactApplicationContext
) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val TAG = "NavigationBridgeModule"
    }

    override fun getName(): String = "NavigationBridge"

    private val bridgeViewModel: BridgeViewModel?
        get() = try {
            val activity = currentActivity as? ComposeMainActivity
            activity?.let { 
                ViewModelProvider(it as ViewModelStoreOwner)[BridgeViewModel::class.java]
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "无法获取BridgeViewModel", e)
            null
        }

    private val settingsViewModel: SettingsViewModel?
        get() = try {
            val activity = currentActivity as? ComposeMainActivity
            activity?.let {
                val vm = ViewModelProvider(it as ViewModelStoreOwner)[SettingsViewModel::class.java]
                vm.initReactContext(reactContext)
                vm
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "无法获取SettingsViewModel", e)
            null
        }

    /**
     * 导航到登录页面
     */
    @ReactMethod
    fun goToLogin() {
        TimberLogger.d(TAG, "导航到登录页面")
        
        bridgeViewModel?.sendIntent(BridgeIntent.NavigateToLogin) ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback到直接导航
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.navigate("login")
            }
        }
    }

    /**
     * 导航到设置页面
     */
    @ReactMethod
    fun navigateToSettings() {
        TimberLogger.d(TAG, "导航到设置页面")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.NavigateToSettings)
        } ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback到直接导航
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.navigate("settings")
            }
        }
    }

    /**
     * 返回上一页
     */
    @ReactMethod
    fun navigateBack(componentName: String?) {
        TimberLogger.d(TAG, "返回上一页, 组件: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.NavigateBack(componentName))
        } ?: run {
            TimberLogger.w(TAG, "BridgeViewModel未初始化，使用fallback导航")
            // Fallback处理
            if (!componentName.isNullOrEmpty()) {
                try {
                    MainApplication.getInstance()?.clearReactRootViewCache(componentName)
                    TimberLogger.d(TAG, "已清理 $componentName 的缓存")
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "清理 $componentName 的缓存失败", e)
                }
            }
            
            Handler(Looper.getMainLooper()).post {
                NavViewModel.navController.value?.popBackStack()
            }
        }
    }

    /**
     * 导航到定时切换页面
     */
    @ReactMethod
    fun navigateToTimedSwitch() {
        TimberLogger.d(TAG, "导航到定时切换页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("timed_switch")
        }
    }

    /**
     * 导航到帮助与支持页面
     */
    @ReactMethod
    fun navigateToHelpSupport() {
        TimberLogger.d(TAG, "导航到帮助与支持页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("help_support")
        }
    }

    /**
     * 导航到隐私政策页面
     */
    @ReactMethod
    fun navigateToPrivacyPolicy() {
        TimberLogger.d(TAG, "导航到隐私政策页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navController.value?.navigate("privacy_policy")
        }
    }

    /**
     * 导航到历史页面
     */
    @ReactMethod
    fun navigateToHistory() {
        TimberLogger.d(TAG, "导航到历史页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToHistory()
        }
    }

    /**
     * 导航到消息页面
     */
    @ReactMethod
    fun navigateToMessage() {
        TimberLogger.d(TAG, "导航到消息页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMessage()
        }
    }

    /**
     * 导航到成为作家页面
     */
    @ReactMethod
    fun navigateToBecomeWriter() {
        TimberLogger.d(TAG, "导航到成为作家页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToBecomeWriter()
        }
    }

    /**
     * 跳转成为作家页前，查询作家状态，并将结果传入页面
     */
    @ReactMethod
    fun navigateToBecomeWriterWithStatus() {
        TimberLogger.d(TAG, "查询作家状态后导航到成为作家页面")
        CoroutineScope(Dispatchers.IO).launch {
            var isAuthor = false
            try {
                val service = com.novel.utils.network.api.author.AuthorService()
                val resp = service.getAuthorStatusBlocking()
                val dataVal = resp.data
                isAuthor = (dataVal == "0")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取作家状态失败，默认非作家", e)
            }
            Handler(Looper.getMainLooper()).post {
                val route = if (isAuthor) "becomewriter?isAuthor=true" else "becomewriter?isAuthor=false"
                TimberLogger.d(TAG, "导航到becomewriter页面，$route")
                NavViewModel.navController.value?.navigate(route)
            }
        }
    }

    /**
     * 导航到写作页面
     */
    @ReactMethod
    fun navigateToWritePage() {
        TimberLogger.d(TAG, "导航到写作页面")
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToWritePage()
        }
    }

    /**
     * 导航到AI写作助手页面
     */
    @ReactMethod
    fun navigateToAIPage() {
        TimberLogger.d(TAG, "导航到AI写作助手页面")
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToAIPage()
        }
    }

    /**
     * 导航到推书中心页面
     */
    @ReactMethod
    fun navigateToRecommendBook() {
        TimberLogger.d(TAG, "导航到推书中心页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToRecommendBook()
        }
    }

    /**
     * 导航到我的预约页面
     */
    @ReactMethod
    fun navigateToMyReservation() {
        TimberLogger.d(TAG, "导航到我的预约页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMyReservation()
        }
    }

    /**
     * 导航到会员中心页面
     */
    @ReactMethod
    fun navigateToMemberCenter() {
        TimberLogger.d(TAG, "导航到会员中心页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToMemberCenter()
        }
    }

    /**
     * 导航到看过的人页面
     */
    @ReactMethod
    fun navigateToViewedUsers() {
        TimberLogger.d(TAG, "导航到看过的人页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToViewedUsers()
        }
    }

    /**
     * 导航到反馈与帮助页面
     */
    @ReactMethod
    fun navigateToFeedbackHelp() {
        TimberLogger.d(TAG, "导航到反馈与帮助页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToFeedbackHelp()
        }
    }

    /**
     * 导航到问题列表页面
     */
    @ReactMethod
    fun navigateToQuestionList() {
        TimberLogger.d(TAG, "导航到问题列表页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToQuestionList()
        }
    }

    /**
     * 导航到问题详情页面
     */
    @ReactMethod
    fun navigateToQuestionDetail() {
        TimberLogger.d(TAG, "导航到问题详情页面")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToQuestionDetail()
        }
    }

    /**
     * 获取首页推荐书籍（高优先级）
     */
    @ReactMethod
    fun getHomeBooksHighPriority(promise: Promise) {
        TimberLogger.d(TAG, "获取首页推荐书籍（高优先级，Bridge直连API以避免ImmutableList解析问题）")
        // 不改动原生 HomeService 的实现，这里直接发起请求并将结果转换为 RN 可识别的结构
        ApiService.get(
            baseUrl = BASE_URL_FRONT,
            endpoint = "home/books",
            headers = mapOf("Accept" to "*/*")
        ) { response, error ->
            if (error != null) {
                TimberLogger.e(TAG, "获取首页推荐书籍失败", error)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("HOME_BOOKS_ERROR", error) }
                return@get
            }

            if (response == null) {
                CoroutineScope(Dispatchers.Main).launch { promise.reject("HOME_BOOKS_ERROR", "Empty response") }
                return@get
            }

            try {
                val json = JSONObject(response)
                val code = if (!json.isNull("code")) json.optString("code") else null
                val message = if (!json.isNull("message")) json.optString("message") else null
                val ok = json.optBoolean("ok", false)

                val dataArray = Arguments.createArray()
                val dataJsonArr = json.optJSONArray("data")
                if (dataJsonArr != null) {
                    for (i in 0 until dataJsonArr.length()) {
                        val item = dataJsonArr.optJSONObject(i) ?: continue
                        val b = Arguments.createMap().apply {
                            putInt("type", item.optInt("type"))
                            putDouble("bookId", item.optLong("bookId").toDouble())
                            putString("picUrl", item.optString("picUrl", null))
                            putString("bookName", item.optString("bookName", null))
                            putString("authorName", item.optString("authorName", null))
                            putString("bookDesc", item.optString("bookDesc", null))
                        }
                        dataArray.pushMap(b)
                    }
                }

                val map = Arguments.createMap().apply {
                    if (code != null) putString("code", code)
                    if (message != null) putString("message", message)
                    putArray("data", dataArray)
                    putBoolean("ok", ok)
                }

                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "解析首页推荐书籍失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("HOME_BOOKS_ERROR", e) }
            }
        }
    }

    /**
     * 导航到作品管理（章节管理）页面
     */
    @ReactMethod
    fun navigateToBookManage() {
        TimberLogger.d(TAG, "导航到作品管理页面")
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToBookManage()
        }
    }

    /**
     * 导航到发表评论页面
     */
    @ReactMethod
    fun navigateToWriteReview(bookId: String?, rating: Double?) {
        TimberLogger.d(TAG, "导航到发表评论页面，bookId=$bookId, rating=$rating")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToWriteReview(bookId, rating?.toInt())
        }
    }

    /**
     * 导航到评论详情页面
     */
    @ReactMethod
    fun navigateToReviewDetail(commentData: String) {
        TimberLogger.d(TAG, "导航到评论详情页面，评论数据: $commentData")
        
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToReviewDetail(commentData)
        }
    }

    /**
     * 清理指定组件缓存
     */
    @ReactMethod
    fun clearComponentCache(componentName: String, callback: Callback) {
        TimberLogger.d(TAG, "清理组件缓存: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.ClearComponentCache(componentName))
            // 由于RN桥接是同步的，我们暂时直接返回成功
            callback.invoke(null, "已清理 $componentName 的缓存")
        } ?: run {
            try {
                MainApplication.getInstance()?.clearReactRootViewCache(componentName)
                callback.invoke(null, "已清理 $componentName 的缓存")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理组件缓存失败", e)
                callback.invoke(e.message, null)
            }
        }
    }

    /**
     * 清理所有组件缓存
     */
    @ReactMethod
    fun clearAllComponentCache(callback: Callback) {
        TimberLogger.d(TAG, "清理所有组件缓存")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.sendIntent(BridgeIntent.ClearAllComponentCache)
            // 由于RN桥接是同步的，我们暂时直接返回成功
            callback.invoke(null, "已清理所有组件缓存")
        } ?: run {
            try {
                MainApplication.getInstance()?.clearAllReactRootViewCache()
                callback.invoke(null, "已清理所有组件缓存")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "清理所有组件缓存失败", e)
                callback.invoke(e.message, null)
            }
        }
    }

    /**
     * 注册组件到桥接系统
     */
    @ReactMethod
    fun registerComponent(componentName: String) {
        TimberLogger.d(TAG, "注册组件: $componentName")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.registerComponent(componentName)
        }
    }

    /**
     * 通知路由变更
     */
    @ReactMethod
    fun notifyRouteChanged(route: String) {
        TimberLogger.d(TAG, "路由变更: $route")
        
        bridgeViewModel?.let { viewModel ->
            viewModel.notifyRouteChanged(route)
        }
    }

    /**
     * 获取桥接状态
     */
    @ReactMethod
    fun getBridgeStatus(callback: Callback) {
        TimberLogger.d(TAG, "获取桥接状态")
        
        bridgeViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            val status = mapOf(
                "isInitialized" to currentState.isBridgeInitialized,
                "currentRoute" to currentState.currentRoute,
                "cachedComponentsCount" to currentState.cachedComponents.size,
                "isLoading" to currentState.isLoading
            )
            
            val bundle = Arguments.createMap().apply {
                putBoolean("isInitialized", currentState.isBridgeInitialized)
                putString("currentRoute", currentState.currentRoute)
                putInt("cachedComponentsCount", currentState.cachedComponents.size)
                putBoolean("isLoading", currentState.isLoading)
            }
            
            callback.invoke(null, bundle)
        } ?: run {
            val bundle = Arguments.createMap().apply {
                putBoolean("isInitialized", false)
                putString("currentRoute", null)
                putInt("cachedComponentsCount", 0)
                putBoolean("isLoading", false)
            }
            callback.invoke(null, bundle)
        }
    }

    /**
     * 获取当前实际主题
     */
    @ReactMethod
    fun getCurrentActualTheme(callback: Callback) {
        TimberLogger.d(TAG, "获取当前实际主题")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.actualTheme)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    /**
     * 获取当前夜间模式
     */
    @ReactMethod
    fun getCurrentNightMode(callback: Callback) {
        TimberLogger.d(TAG, "获取当前夜间模式")
        
        settingsViewModel?.let { viewModel ->
            val currentState = viewModel.getStateForBridge()
            callback.invoke(null, currentState.currentThemeMode)
        } ?: run {
            callback.invoke("ViewModel未初始化", null)
        }
    }

    // =================== AI Service Bridge ===================
    @ReactMethod
    fun aiPolish(text: String, promise: Promise) {
        TimberLogger.d(TAG, "AI 润色，请求文本长度=${text.length}")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = AiService().polishTextBlocking(text)
                CoroutineScope(Dispatchers.Main).launch {
                    if (resp.ok == true) {
                        promise.resolve(resp.data ?: "")
                    } else {
                        promise.reject("AI_POLISH_ERROR", resp.message ?: "AI 返回失败")
                    }
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "AI 润色失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AI_POLISH_ERROR", e) }
            }
        }
    }

    @ReactMethod
    fun aiExpand(text: String, ratio: Int, promise: Promise) {
        TimberLogger.d(TAG, "AI 扩写，请求文本长度=${text.length}，ratio=$ratio")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = AiService().expandTextBlocking(text, ratio)
                CoroutineScope(Dispatchers.Main).launch {
                    if (resp.ok == true) {
                        promise.resolve(resp.data ?: "")
                    } else {
                        promise.reject("AI_EXPAND_ERROR", resp.message ?: "AI 返回失败")
                    }
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "AI 扩写失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AI_EXPAND_ERROR", e) }
            }
        }
    }

    @ReactMethod
    fun aiCondense(text: String, ratio: Int, promise: Promise) {
        TimberLogger.d(TAG, "AI 缩写，请求文本长度=${text.length}，ratio=$ratio")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = AiService().condenseTextBlocking(text, ratio)
                CoroutineScope(Dispatchers.Main).launch {
                    if (resp.ok == true) {
                        promise.resolve(resp.data ?: "")
                    } else {
                        promise.reject("AI_CONDENSE_ERROR", resp.message ?: "AI 返回失败")
                    }
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "AI 缩写失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AI_CONDENSE_ERROR", e) }
            }
        }
    }

    @ReactMethod
    fun aiContinue(text: String, length: Int, promise: Promise) {
        TimberLogger.d(TAG, "AI 续写，请求文本长度=${text.length}，length=$length")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = AiService().continueTextBlocking(text, length)
                CoroutineScope(Dispatchers.Main).launch {
                    if (resp.ok == true) {
                        promise.resolve(resp.data ?: "")
                    } else {
                        promise.reject("AI_CONTINUE_ERROR", resp.message ?: "AI 返回失败")
                    }
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "AI 续写失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AI_CONTINUE_ERROR", e) }
            }
        }
    }

    /**
     * RN 触发原生注册作者
     * penName 昵称；telPhone/chatAccount 使用昵称；email 为 `${nickname}@163.com`
     * sex: 0/1，默认 1
     */
    @ReactMethod
    fun registerAuthor(penName: String, sex: Int, promise: Promise) {
        TimberLogger.d(TAG, "触发作者注册 penName=$penName sex=$sex")
        // 执行网络请求
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val authorService = com.novel.utils.network.api.author.AuthorService()
                val req = com.novel.utils.network.api.author.AuthorService.AuthorRegisterRequest(
                    penName = penName,
                    telPhone = penName,
                    chatAccount = penName,
                    email = "$penName@163.com",
                    workDirection = sex
                )
                val resp = authorService.registerAuthorBlocking(req)
                CoroutineScope(Dispatchers.Main).launch {
                    promise.resolve(Arguments.createMap().apply {
                        putString("code", resp.code)
                        putString("message", resp.message)
                        putBoolean("ok", resp.ok ?: false)
                    })
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "作者注册失败", e)
                CoroutineScope(Dispatchers.Main).launch {
                    promise.reject("REGISTER_ERROR", e)
                }
            }
        }
    }

    /**
     * 统一主题切换接口
     */
    @ReactMethod
    fun changeTheme(theme: String, promise: Promise) {
        TimberLogger.d(TAG, "统一主题切换: $theme")
        
        settingsViewModel?.let { viewModel ->
            observeEffectForPromise(viewModel, promise) { effect, promiseResolved, p ->
                when (effect) {
                    is SettingsEffect.ShowToast -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.resolve(effect.message)
                        }
                        true // 停止监听
                    }
                    is SettingsEffect.ShowError -> {
                        if (promiseResolved.compareAndSet(false, true)) {
                            p.reject("THEME_CHANGE_ERROR", effect.error)
                        }
                        true // 停止监听
                    }
                    else -> false // 继续监听
                }
            }
            
            viewModel.sendIntent(SettingsIntent.SetNightMode(theme))
        } ?: run {
            promise.reject("VIEWMODEL_ERROR", "ViewModel未初始化")
        }
    }

    /**
     * 观察Effect并执行Promise的辅助方法
     */
    private fun observeEffectForPromise(
        viewModel: SettingsViewModel,
        promise: Promise,
        effectHandler: (SettingsEffect, AtomicBoolean, Promise) -> Boolean
    ) {
        TimberLogger.d(TAG, "设置Effect观察器(Promise)")
        
        // 使用原子布尔值确保promise只被调用一次
        val promiseResolved = AtomicBoolean(false)
        
        // 在主线程上启动协程来监听Effect
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // 设置超时时间，避免无限等待
                withTimeout(10000) {
                    viewModel.effect.collect { effect ->
                        TimberLogger.d(TAG, "收到Effect: $effect")
                        val shouldStop = effectHandler(effect, promiseResolved, promise)
                        if (shouldStop) {
                            // 只有当effectHandler返回true时才停止监听
                            return@collect
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                TimberLogger.w(TAG, "Effect监听超时")
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.reject("TIMEOUT_ERROR", "操作超时")
                }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Effect监听异常", e)
                if (promiseResolved.compareAndSet(false, true)) {
                    promise.reject("EFFECT_ERROR", "操作失败: ${e.message}", e)
                }
            }
        }
    }
}