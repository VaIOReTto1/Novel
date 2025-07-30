package com.novel.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.novel.page.MainPage
import com.novel.page.login.LoginPage
import com.novel.page.book.BookDetailPage
import com.novel.page.read.ReaderPage
import com.novel.page.search.SearchPage
import com.novel.page.search.SearchResultPage
import com.novel.rn.ReactNativePage
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.search.FullRankingPage
import com.novel.rn.MviModuleType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

/**
 * 导航设置 - 简化版本，翻书动画在HomePage内部处理
 */
@Composable
fun NavigationSetup() {
    TimberLogger.d("NavigationSetup", "NavigationSetup 重新组合")

    // 创建NavController
    val navController = rememberNavController()

    // 使用DisposableEffect确保在组件销毁时正确清理
    DisposableEffect(navController) {
        TimberLogger.d("NavigationSetup", "DisposableEffect: 设置 NavController")
        NavViewModel.navController.value = navController

        onDispose {
            TimberLogger.d("NavigationSetup", "DisposableEffect: 清理 NavController")
            // 确保在组件销毁时清理引用
            if (NavViewModel.navController.value == navController) {
                NavViewModel.navController.value = null
            }
        }
    }

    // 添加错误处理，防止在主题切换时出现NavController生命周期问题
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainPage()
        }
        composable("login") {
            LoginPage()
        }
        composable("search?query={query}") { backStackEntry ->
            backStackEntry.arguments?.getString("query") ?: ""
            SearchPage(
                onNavigateBack = {
                    NavViewModel.navigateBack()
                },
                onNavigateToBookDetail = { bookId ->
                    NavViewModel.navigateToReader(bookId.toString(), null)
                }
            )
        }
        composable("search_result?query={query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultPage(
                initialQuery = query
            )
        }
        composable("full_ranking/{rankingType}/{encodedData}") { backStackEntry ->
            val rankingType = backStackEntry.arguments?.getString("rankingType") ?: ""
            val encodedData = backStackEntry.arguments?.getString("encodedData") ?: ""

            // 解码榜单数据
            val rankingItems = try {
                NavViewModel.decodeRankingData(encodedData)
            } catch (e: Exception) {
                TimberLogger.e("NavigationUtil", "解码榜单数据失败: encodedData=$encodedData", e)
                emptyList()
            }

            FullRankingPage(
                rankingType = rankingType,
                rankingItems = rankingItems,
                onNavigateBack = {
                    NavViewModel.navigateBack()
                },
                onNavigateToBookDetail = { bookId ->
                    NavViewModel.navigateToReader(bookId = bookId.toString(), null)
                }
            )
        }
        composable("book_detail/{bookId}?fromRank={fromRank}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            backStackEntry.arguments?.getString("fromRank")?.toBoolean() ?: false
            BookDetailPage(
                bookId = bookId,
                onNavigateToReader = { bookId, chapterId ->
                    NavViewModel.navigateToReader(bookId, chapterId)
                }
            )
        }
        composable("reader/{bookId}?chapterId={chapterId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val chapterId = backStackEntry.arguments?.getString("chapterId")
            ReaderPage(
                bookId = bookId,
                chapterId = chapterId
            )
        }
        composable("profile") {
            // 个人中心页面 - 使用ReactNativePage
            ReactNativePage(
                componentName = "Novel",
                initialProps = mapOf("nativeMessage" to "ProfilePage"),
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        composable("settings") {
            // 设置页面 - 使用ReactNativePage加载SettingsPageComponent
            ReactNativePage(
                componentName = "SettingsPageComponent",
                initialProps = mapOf("source" to "android_settings"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BOTH
            )
        }
        composable("timed_switch") {
            // 定时切换页面 - 使用ReactNativePage加载TimedSwitchPageComponent  
            ReactNativePage(
                componentName = "TimedSwitchPageComponent",
                initialProps = mapOf("source" to "android_timed_switch"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BOTH
            )
        }
        composable("privacy_policy") {
            ReactNativePage(
                componentName = "PrivacyPolicyPageComponent",
                initialProps = mapOf("source" to "android_privacy_policy"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        composable("help_support") {
            ReactNativePage(
                componentName = "HelpSupportPageComponent",
                initialProps = mapOf("source" to "android_privacy_policy"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        composable("history") {
            ReactNativePage(
                componentName = "HistoryPageComponent",
                initialProps = mapOf("source" to "android_history"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        composable("message") {
            ReactNativePage(
                componentName = "MessagePageComponent",
                initialProps = mapOf("source" to "android_message"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        composable("becomewriter") {
            ReactNativePage(
                componentName = "BecomeWriterPageComponent",
                initialProps = mapOf("source" to "android_becomewriter"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        
        composable("recommendbook") {
            ReactNativePage(
                componentName = "RecommendBookPageComponent",
                initialProps = mapOf("source" to "android_recommendbook"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }

        composable("viewedusers") {
            ReactNativePage(
                componentName = "ViewedUsersPageComponent",
                initialProps = mapOf("source" to "android_viewedusers"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }

        composable("myreservation") {
            ReactNativePage(
                componentName = "MyReservationPageComponent",
                initialProps = mapOf("source" to "android_myreservation"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }

        composable("membercenter") {
            ReactNativePage(
                componentName = "MemberCenterPageComponent",
                initialProps = mapOf("source" to "android_membercenter"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }

        composable("feedbackhelp") {
            ReactNativePage(
                componentName = "FeedbackHelpMainPageComponent",
                initialProps = mapOf("source" to "android_feedbackhelp"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        
        composable("questionlist") {
            ReactNativePage(
                componentName = "QuestionListPageComponent",
                initialProps = mapOf("source" to "android_questionlist"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
        
        composable("questiondetail") {
            ReactNativePage(
                componentName = "QuestionDetailPageComponent",
                initialProps = mapOf("source" to "android_questiondetail"),
                destroyOnBack = true,
                mviModuleType = MviModuleType.BRIDGE
            )
        }
    }
}

/**
 * NavViewModel：
 * 1. 管理全局导航控制器 NavHostController
 * 2. 统一处理导航跳转逻辑，增加防抖功能，避免短时间内重复触发多次导航
 */
@Stable
object NavViewModel : ViewModel() {
    // LiveData 持有当前 NavHostController 实例，Compose 层通过观察此值调用实际导航
    val navController = MutableLiveData<NavHostController?>()

    // 防抖超时时间，单位毫秒
    private const val DEBOUNCE_TIMEOUT_MS = 1000L

    // 特殊后退命令标识
    private const val BACK_COMMAND = "__BACK__"

    // 使用 SharedFlow 缓冲导航命令
    private val _navCommands = MutableSharedFlow<String>(extraBufferCapacity = 16)

    init {
        // 在 ViewModel 的协程域中收集并处理导航命令
        viewModelScope.launch {
            _navCommands
                .debounce(DEBOUNCE_TIMEOUT_MS)    // 对命令流进行防抖
                .collect { command ->
                    navController.value?.let { controller ->
                        if (command == BACK_COMMAND) {
                            // 处理后退命令
                            controller.popBackStack()
                            TimberLogger.d("NavViewModel", "防抖后：popBackStack() 调用")
                        } else {
                            // 处理普通导航路由
                            controller.navigate(command)
                            TimberLogger.d("NavViewModel", "防抖后：navigate($command) 调用")
                        }
                    }
                }
        }
    }

    /**
     * 向 SharedFlow 发射导航命令，用于防抖处理
     * @param route 要导航的路由字符串
     */
    private fun navigateDebounced(route: String) {
        _navCommands.tryEmit(route)
    }

    // 当前书籍信息（ID, 是否来自排行榜），用于页面间动画或状态传递
    private var currentBookInfo: Pair<String, Boolean>? = null

    // 书籍翻页动画控制器
    private var flipBookController: FlipBookAnimationController? = null

    /**
     * 设置全局 FlipBook 动画控制器实例
     */
    fun setFlipBookController(controller: FlipBookAnimationController?) {
        flipBookController = controller
    }

    /**
     * 获取当前的 FlipBook 动画控制器（可能为 null）
     */
    fun currentFlipBookController(): FlipBookAnimationController? = flipBookController

    /**
     * 导航到搜索页面，可选携带查询参数
     * @param query 搜索关键词，默认为空
     */
    fun navigateToSearch(query: String = "") {
        TimberLogger.d("NavViewModel", "开始导航：搜索页面，query=$query")
        val route = if (query.isNotBlank()) "search?query=$query" else "search"
        navigateDebounced(route)
    }

    /**
     * 导航到搜索结果页面
     * @param query 搜索关键词
     */
    fun navigateToSearchResult(query: String) {
        TimberLogger.d("NavViewModel", "开始导航：搜索结果页面，query=$query")
        val route = "search_result?query=$query"
        navigateDebounced(route)
    }

    /**
     * 导航到完整排行榜页面
     * @param rankingType 榜单类型标识
     * @param rankingItems 榜单数据列表
     */
    fun navigateToFullRanking(
        rankingType: String,
        rankingItems: List<com.novel.page.search.component.SearchRankingItem>
    ) {
        TimberLogger.d("NavViewModel", "开始导航：完整排行榜，type=$rankingType, count=${rankingItems.size}")
        val encodedData = encodeRankingData(rankingItems)
        val route = "full_ranking/$rankingType/$encodedData"
        navigateDebounced(route)
    }

    /**
     * 导航到书籍详情页面
     * @param bookId 书籍 ID
     * @param fromRank 标识是否来自排行榜
     */
    fun navigateToBookDetail(bookId: String, fromRank: Boolean = false) {
        TimberLogger.d("NavViewModel", "开始导航：书籍详情，bookId=$bookId, fromRank=$fromRank")
        currentBookInfo = bookId to fromRank
        val route = "book_detail/$bookId?fromRank=$fromRank"
        navigateDebounced(route)
    }

    /**
     * 导航到阅读器页面
     * @param bookId 书籍 ID
     * @param chapterId 可选章节 ID
     */
    fun navigateToReader(bookId: String, chapterId: String? = null) {
        TimberLogger.d("NavViewModel", "开始导航：阅读页面，bookId=$bookId, chapterId=$chapterId")
        val route = if (chapterId != null) "reader/$bookId?chapterId=$chapterId" else "reader/$bookId"
        navigateDebounced(route)
    }

    /**
     * 导航到历史记录页面
     */
    fun navigateToHistory() {
        TimberLogger.d("NavViewModel", "开始导航：历史记录页面")
        navigateDebounced("history")
    }

    /**
     * 导航到消息页面
     */
    fun navigateToMessage() {
        TimberLogger.d("NavViewModel", "开始导航：消息页面")
        navigateDebounced("message")
    }

    /**
     * 导航到成为作家页面
     */
    fun navigateToBecomeWriter() {
        TimberLogger.d("NavViewModel", "开始导航：成为作家页面")
        navigateDebounced("becomewriter")
    }

    /**
     * 导航到推荐书籍页面
     */
    fun navigateToRecommendBook() {
        TimberLogger.d("NavViewModel", "开始导航：推荐书籍页面")
        navigateDebounced("recommendbook")
    }

    /**
     * 导航到看过的人页面
     */
    fun navigateToViewedUsers() {
        TimberLogger.d("NavViewModel", "开始导航：看过的人页面")
        navigateDebounced("viewedusers")
    }

    fun navigateToMyReservation() {
        TimberLogger.d("NavViewModel", "开始导航：我的预约页面")
        navigateDebounced("myreservation")
    }

    fun navigateToMemberCenter() {
        TimberLogger.d("NavViewModel", "开始导航：会员中心页面")
        navigateDebounced("membercenter")
    }

    /**
     * 导航到反馈与帮助页面
     */
    fun navigateToFeedbackHelp() {
        TimberLogger.d("NavViewModel", "开始导航：反馈与帮助页面")
        navigateDebounced("feedbackhelp")
    }

    /**
     * 导航到问题列表页面
     */
    fun navigateToQuestionList() {
        TimberLogger.d("NavViewModel", "开始导航：问题列表页面")
        navigateDebounced("questionlist")
    }

    /**
     * 导航到问题详情页面
     */
    fun navigateToQuestionDetail() {
        TimberLogger.d("NavViewModel", "开始导航：问题详情页面")
        navigateDebounced("questiondetail")
    }

    /**
     * 执行后退操作
     */
    fun navigateBack() {
        TimberLogger.d("NavViewModel", "执行后退导航")
        navigateDebounced(BACK_COMMAND)
    }

    /**
     * 将排行榜数据编码为 Base64 + URL 安全的字符串
     */
    private fun encodeRankingData(
        items: List<com.novel.page.search.component.SearchRankingItem>
    ): String {
        return try {
            val csv = items.joinToString("|") { "${it.id},${it.title},${it.author},${it.rank}" }
            val base64 = android.util.Base64.encodeToString(
                csv.toByteArray(),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
            java.net.URLEncoder.encode(base64, "UTF-8")
        } catch (e: Exception) {
            TimberLogger.e("NavViewModel", "编码排行榜数据失败", e)
            ""
        }
    }

    /**
     * 解码排行榜数据字符串，恢复为对象列表
     */
    fun decodeRankingData(
        encodedData: String
    ): List<com.novel.page.search.component.SearchRankingItem> {
        return try {
            val decodedUrl = java.net.URLDecoder.decode(encodedData, "UTF-8")
            val bytes = android.util.Base64.decode(
                decodedUrl,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
            val csv = String(bytes)
            csv.split("|").mapNotNull { entry ->
                val parts = entry.split(',')
                if (parts.size >= 4) {
                    com.novel.page.search.component.SearchRankingItem(
                        id = parts[0].toLongOrNull() ?: 0L,
                        title = parts[1],
                        author = parts[2],
                        rank = parts[3].toIntOrNull() ?: 0
                    )
                } else null
            }
        } catch (e: Exception) {
            TimberLogger.e("NavViewModel", "解码排行榜数据失败", e)
            emptyList()
        }
    }
}
