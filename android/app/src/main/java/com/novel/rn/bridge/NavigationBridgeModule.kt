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
import com.novel.rn.bridge.network.NavigationBridgeNetworkGateway
import com.novel.page.read.service.HistoryService
import com.novel.page.read.service.HistoryServiceImpl
import com.novel.page.read.service.HistoryItem
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import java.util.concurrent.atomic.AtomicBoolean
import com.novel.rn.settings.SettingsEffect
import com.novel.rn.settings.SettingsIntent
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

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

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NavigationBridgeEntryPoint {
        fun novelUserDefaults(): NovelUserDefaults
        fun navigationBridgeNetworkGateway(): NavigationBridgeNetworkGateway
    }

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

    private val historyService: HistoryService by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            reactContext.applicationContext,
            NavigationBridgeEntryPoint::class.java
        )
        val userDefaults = entryPoint.novelUserDefaults()
        HistoryServiceImpl(userDefaults)
    }

    private val navigationBridgeNetworkGateway: NavigationBridgeNetworkGateway by lazy {
        val entryPoint = EntryPointAccessors.fromApplication(
            reactContext.applicationContext,
            NavigationBridgeEntryPoint::class.java
        )
        entryPoint.navigationBridgeNetworkGateway()
    }

    // =================== Selection Menu (Android) ===================
    private val MENU_ID_POLISH = 0xA11001
    private val MENU_ID_EXPAND = 0xA11002
    private val MENU_ID_CONDENSE = 0xA11003
    private val MENU_ID_CONTINUE = 0xA11004

    /**
     * 为指定的 TextInput 视图安装自定义选择菜单（仅本页调用）
     */
    @ReactMethod
    fun attachSelectionMenu(@Suppress("UNUSED_PARAMETER") viewTag: Int) {
        try {
            Handler(Looper.getMainLooper()).post {
                val activity = currentActivity ?: return@post
                var attempts = 0
                fun tryAttach() {
                    val v = activity.currentFocus
                    if (v is EditText) {
                        v.customSelectionActionModeCallback = object : ActionMode.Callback {
                            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                                try {
                                    menu.add(0, MENU_ID_POLISH, 0, "润色")
                                    menu.add(0, MENU_ID_EXPAND, 1, "扩写")
                                    menu.add(0, MENU_ID_CONDENSE, 2, "缩写")
                                    menu.add(0, MENU_ID_CONTINUE, 3, "续写")
                                } catch (_: Exception) { }
                                return true
                            }

                            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

                            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                                val action = when (item.itemId) {
                                    MENU_ID_POLISH -> "polish"
                                    MENU_ID_EXPAND -> "expand"
                                    MENU_ID_CONDENSE -> "condense"
                                    MENU_ID_CONTINUE -> "continue"
                                    else -> null
                                } ?: return false

                                val start = try { v.selectionStart } catch (_: Exception) { -1 }
                                val end = try { v.selectionEnd } catch (_: Exception) { -1 }
                                val selected = try {
                                    if (start >= 0 && end > start) v.text.substring(start, end) else ""
                                } catch (_: Exception) { "" }

                                val map = Arguments.createMap().apply {
                                    putString("action", action)
                                    if (selected.isNotEmpty()) putString("selectedText", selected)
                                    putInt("start", start)
                                    putInt("end", end)
                                }
                                try {
                                    reactContext
                                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                                        .emit("WritePageSelectionMenuAction", map)
                                } catch (_: Exception) { }

                                mode.finish()
                                return true
                            }

                            override fun onDestroyActionMode(mode: ActionMode) { /* no-op */ }
                        }
                    } else if (attempts < 6) {
                        attempts += 1
                        Handler(Looper.getMainLooper()).postDelayed({ tryAttach() }, 100)
                    }
                }
                tryAttach()
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "attachSelectionMenu 失败", e)
        }
    }

    @ReactMethod
    fun detachSelectionMenu(@Suppress("UNUSED_PARAMETER") viewTag: Int) {
        try {
            Handler(Looper.getMainLooper()).post {
                val activity = currentActivity ?: return@post
                val v = activity.currentFocus
                if (v is EditText) {
                    v.customSelectionActionModeCallback = null
                }
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "detachSelectionMenu 失败", e)
        }
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
     * 直接依据前端传入的标志导航到成为作家页面（避免原生重复查询）
     */
    @ReactMethod
    fun navigateToBecomeWriterWithFlag(isAuthor: Boolean) {
        TimberLogger.d(TAG, "根据传入标志导航到成为作家页面 isAuthor=$isAuthor")
        Handler(Looper.getMainLooper()).post {
            val route = if (isAuthor) "becomewriter?isAuthor=true" else "becomewriter?isAuthor=false"
            NavViewModel.navController.value?.navigate(route)
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
     * 导航到阅读器页面
     */
    @ReactMethod
    fun navigateToReader(bookId: String, chapterId: String?) {
        TimberLogger.d(TAG, "导航到阅读器页面，bookId=$bookId, chapterId=$chapterId")
        Handler(Looper.getMainLooper()).post {
            NavViewModel.navigateToReader(bookId)
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
        TimberLogger.d(TAG, "获取首页推荐书籍（高优先级，Bridge 经 NetworkFacade 兼容主通路请求）")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = navigationBridgeNetworkGateway.getHomeBooksHighPriority()
                val dataArray = Arguments.createArray()

                response.data.forEach { item ->
                    val bookMap = Arguments.createMap().apply {
                        putInt("type", item.type)
                        putString("bookId", item.bookId)
                        putString("picUrl", item.picUrl)
                        putString("bookName", item.bookName)
                        putString("authorName", item.authorName)
                        putString("bookDesc", item.bookDesc)
                    }
                    dataArray.pushMap(bookMap)
                }

                val map = Arguments.createMap().apply {
                    response.code?.let { putString("code", it) }
                    response.message?.let { putString("message", it) }
                    putArray("data", dataArray)
                    putBoolean("ok", response.ok)
                }

                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取首页推荐书籍失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("HOME_BOOKS_ERROR", e) }
            }
        }
    }

    /**
     * 获取阅读历史记录
     */
    @ReactMethod
    fun getReadingHistory(promise: Promise) {
        TimberLogger.d(TAG, "获取阅读历史记录")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val historyItems = historyService.getAllHistory()
                val historyArray = Arguments.createArray()
                
                historyItems.forEach { item ->
                    val historyMap = Arguments.createMap()
                    historyMap.putString("id", item.id)
                    historyMap.putString("bookId", item.bookId)
                    historyMap.putString("chapterId", item.chapterId)
                    historyMap.putString("title", item.title)
                    historyMap.putString("chapterTitle", item.chapterTitle)
                    historyMap.putInt("totalChapters", item.totalChapters)
                    historyMap.putInt("currentChapter", item.currentChapter)
                    historyMap.putString("author", item.author)
                    historyMap.putString("coverUrl", item.coverUrl)
                    historyMap.putDouble("lastReadTime", item.lastReadTime.toDouble())
                    historyMap.putDouble("readProgress", item.readProgress.toDouble())
                    // Mock data for fields not available in HistoryItem
                    historyMap.putString("description", "暂无描述")
                    historyMap.putString("type", "book")
                    historyMap.putString("categoryId", "1")
                    historyMap.putInt("readCount", 1)
                    historyMap.putDouble("rating", 4.5)
                    historyArray.pushMap(historyMap)
                }
                
                val result = Arguments.createMap()
                result.putArray("historyItems", historyArray)
                result.putBoolean("success", true)
                
                TimberLogger.d(TAG, "获取阅读历史记录成功，共${historyItems.size}条记录")
                promise.resolve(result)
                
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取阅读历史记录异常", e)
                promise.reject("ERROR", e.message ?: "获取历史记录失败")
            }
        }
    }

    /**
     * 获取作家状态（布尔 isAuthor），由RN端自行缓存与决定跳转
     */
    @ReactMethod
    fun getAuthorStatus(promise: Promise) {
        TimberLogger.d(TAG, "RN调用获取作家状态")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = com.novel.utils.network.api.author.AuthorService().getAuthorStatusBlocking()
                val dataVal = resp.data
                val isAuthor = (dataVal == "0")
                val map = Arguments.createMap().apply {
                    resp.code?.let { putString("code", it) }
                    resp.message?.let { putString("message", it) }
                    putBoolean("ok", resp.ok ?: false)
                    putBoolean("isAuthor", isAuthor)
                }
                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取作家状态失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AUTHOR_STATUS_ERROR", e) }
            }
        }
    }

    /**
     * 获取作家发布作品列表
     */
    @ReactMethod
    fun getAuthorBooks(pageNum: Int, pageSize: Int, promise: Promise) {
        TimberLogger.d(TAG, "RN调用获取作家作品列表 pageNum=$pageNum pageSize=$pageSize")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val listArray = Arguments.createArray()
                val response = navigationBridgeNetworkGateway.getAuthorBooks(
                    pageNum = pageNum,
                    pageSize = pageSize
                )

                response.list.forEach { item ->
                    val bookMap = Arguments.createMap().apply {
                        putString("id", item.id)
                        putString("bookName", item.bookName)
                        putString("authorName", item.authorName)
                        putString("picUrl", item.picUrl)
                        putDouble("wordCount", item.wordCount)
                        putString("bookDesc", item.bookDesc)
                        putString("categoryId", item.categoryId)
                        putString("categoryName", item.categoryName)
                    }
                    listArray.pushMap(bookMap)
                }

                val map = Arguments.createMap().apply {
                    response.code?.let { putString("code", it) }
                    response.message?.let { putString("message", it) }
                    putBoolean("ok", response.ok)
                    putArray("list", listArray)
                }
                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取作家作品列表失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("AUTHOR_BOOKS_ERROR", e) }
            }
        }
    }

    // =================== Category & Search Bridge ===================
    /**
     * 获取书籍分类（按工作方向：男生=0，女生=1）
     */
    @ReactMethod
    fun getBookCategories(workDirection: Int, promise: Promise) {
        TimberLogger.d(TAG, "RN调用获取书籍分类 workDirection=$workDirection")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val arr = Arguments.createArray()
                val response = navigationBridgeNetworkGateway.getBookCategories(workDirection)

                response.data.forEach { item ->
                    val categoryMap = Arguments.createMap().apply {
                        putString("id", item.id)
                        putString("name", item.name)
                    }
                    arr.pushMap(categoryMap)
                }

                val map = Arguments.createMap().apply {
                    putBoolean("ok", response.ok)
                    putArray("list", arr)
                }
                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "获取书籍分类失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("CATEGORY_LIST_ERROR", e) }
            }
        }
    }

    /**
     * 分类/方向搜索书籍
     * @param workDirection 0/1
     * @param categoryId 分类ID，<=0 表示不指定
     */
    @ReactMethod
    fun searchBooks(workDirection: Int, categoryId: Int, pageNum: Int, pageSize: Int, promise: Promise) {
        TimberLogger.d(TAG, "RN调用搜索书籍 workDirection=$workDirection categoryId=$categoryId pageNum=$pageNum pageSize=$pageSize")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val listArr = Arguments.createArray()
                val response = navigationBridgeNetworkGateway.searchBooks(
                    workDirection = workDirection,
                    categoryId = categoryId,
                    pageNum = pageNum,
                    pageSize = pageSize
                )

                response.list.forEach { item ->
                    val bookMap = Arguments.createMap().apply {
                        putString("id", item.id)
                        putString("bookName", item.bookName)
                        putString("authorName", item.authorName)
                        putString("picUrl", item.picUrl)
                        putString("bookDesc", item.bookDesc)
                    }
                    listArr.pushMap(bookMap)
                }

                val map = Arguments.createMap().apply {
                    putBoolean("ok", response.ok)
                    putArray("list", listArr)
                    response.pageNum?.let { putDouble("pageNum", it.toDouble()) }
                    response.pageSize?.let { putDouble("pageSize", it.toDouble()) }
                    response.total?.let { putDouble("total", it.toDouble()) }
                    response.pages?.let { putDouble("pages", it.toDouble()) }
                }
                CoroutineScope(Dispatchers.Main).launch { promise.resolve(map) }
            } catch (e: Exception) {
                TimberLogger.e(TAG, "搜索书籍失败", e)
                CoroutineScope(Dispatchers.Main).launch { promise.reject("SEARCH_BOOKS_ERROR", e) }
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
