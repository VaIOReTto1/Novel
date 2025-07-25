package com.novel.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    }
}

@Stable
object NavViewModel : ViewModel() {
    val navController = MutableLiveData<NavHostController>()

    // 当前书籍信息（用于返回动画）
    private var currentBookInfo: Pair<String, Boolean>? = null

    /** 供 BookDetailPage 与 ReaderPage 共享的临时动画控制器 */
    private var flipBookController: FlipBookAnimationController? = null

    fun setFlipBookController(controller: FlipBookAnimationController?) {
        flipBookController = controller
    }

    /** 获取当前 FlipBookAnimationController（可能为 null） */
    fun currentFlipBookController(): FlipBookAnimationController? = flipBookController

    /**
     * 导航到搜索页面
     * @param query 搜索关键词（可选）
     */
    fun navigateToSearch(query: String = "") {
        TimberLogger.d("NavViewModel", "===== 导航到搜索页面 =====")
        TimberLogger.d("NavViewModel", "query: $query")

        val route = if (query.isNotBlank()) {
            "search?query=$query"
        } else {
            "search"
        }

        navController.value?.navigate(route)
        TimberLogger.d("NavViewModel", "✅ 导航到搜索页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 导航到搜索结果页面
     * @param query 搜索关键词
     */
    fun navigateToSearchResult(query: String) {
        TimberLogger.d("NavViewModel", "===== 导航到搜索结果页面 =====")
        TimberLogger.d("NavViewModel", "query: $query")

        val route = "search_result?query=$query"
        navController.value?.navigate(route)

        TimberLogger.d("NavViewModel", "✅ 导航到搜索结果页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 导航到完整榜单页面
     * @param rankingType 榜单类型
     * @param rankingItems 榜单数据
     */
    fun navigateToFullRanking(
        rankingType: String,
        rankingItems: List<com.novel.page.search.component.SearchRankingItem>
    ) {
        TimberLogger.d("NavViewModel", "===== 导航到完整榜单页面 =====")
        TimberLogger.d("NavViewModel", "rankingType: $rankingType")
        TimberLogger.d("NavViewModel", "rankingItems count: ${rankingItems.size}")

        val encodedData = encodeRankingData(rankingItems)
        val route = "full_ranking/$rankingType/$encodedData"
        navController.value?.navigate(route)

        TimberLogger.d("NavViewModel", "✅ 导航到完整榜单页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 编码榜单数据为URL安全的字符串
     */
    private fun encodeRankingData(items: List<com.novel.page.search.component.SearchRankingItem>): String {
        return try {
            val json = android.util.Base64.encodeToString(
                items.joinToString("|") { "${it.id},${it.title},${it.author},${it.rank}" }
                    .toByteArray(),
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
            java.net.URLEncoder.encode(json, "UTF-8")
        } catch (e: Exception) {
            TimberLogger.e("NavViewModel", "编码榜单数据失败", e)
            ""
        }
    }

    /**
     * 解码榜单数据
     */
    fun decodeRankingData(encodedData: String): List<com.novel.page.search.component.SearchRankingItem> {
        return try {
            val decodedJson = java.net.URLDecoder.decode(encodedData, "UTF-8")
            val decodedBytes = android.util.Base64.decode(
                decodedJson,
                android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP
            )
            val dataString = String(decodedBytes)

            dataString.split("|").mapNotNull { itemString ->
                val parts = itemString.split(",")
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
            TimberLogger.e("NavViewModel", "解码榜单数据失败", e)
            emptyList()
        }
    }

    /**
     * 导航到书籍详情页
     * @param bookId 书籍ID
     * @param fromRank 是否来自榜单（用于识别但不影响动画，动画在HomePage处理）
     */
    fun navigateToBookDetail(bookId: String, fromRank: Boolean = false) {
        TimberLogger.d("NavViewModel", "===== 导航到书籍详情页 =====")
        TimberLogger.d("NavViewModel", "bookId: $bookId")
        TimberLogger.d("NavViewModel", "fromRank: $fromRank")

        // 记录当前书籍信息
        currentBookInfo = bookId to fromRank
        TimberLogger.d("NavViewModel", "保存书籍信息: $currentBookInfo")

        navController.value?.navigate("book_detail/$bookId?fromRank=$fromRank")
        TimberLogger.d("NavViewModel", "✅ 导航命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 导航到阅读器页面
     * @param bookId 书籍ID
     * @param chapterId 章节ID（可选）
     */
    fun navigateToReader(bookId: String, chapterId: String? = null) {
        TimberLogger.d("NavViewModel", "===== 导航到阅读器页面 =====")
        TimberLogger.d("NavViewModel", "bookId: $bookId")
        TimberLogger.d("NavViewModel", "chapterId: $chapterId")

        val route = if (chapterId != null) {
            "reader/$bookId?chapterId=$chapterId"
        } else {
            "reader/$bookId"
        }

        navController.value?.navigate(route)
        TimberLogger.d("NavViewModel", "✅ 导航到阅读器命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 导航到历史页面
     */
    fun navigateToHistory() {
        TimberLogger.d("NavViewModel", "===== 导航到历史页面 =====")
        
        navController.value?.navigate("history")
        TimberLogger.d("NavViewModel", "✅ 导航到历史页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 导航到消息页面
     */
    fun navigateToMessage() {
        TimberLogger.d("NavViewModel", "===== 导航到消息页面 =====")
        
        navController.value?.navigate("message")
        TimberLogger.d("NavViewModel", "✅ 导航到消息页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    fun navigateToBecomeWriter() {
        TimberLogger.d("NavViewModel", "===== 导航到成为作家页面 =====")
        
        navController.value?.navigate("becomewriter")
        TimberLogger.d("NavViewModel", "✅ 导航到成为作家页面命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

        fun navigateToRecommendBook() {
        TimberLogger.d("NavViewModel", "===== 导航到推荐书籍页面 =====")
        
        navController.value?.navigate("recommendbook")
        TimberLogger.d("NavViewModel", "✅ 导航命令已发送")
        TimberLogger.d("NavViewModel", "==============================")
    }

    /**
     * 返回
     */
    fun navigateBack() {
        navController.value?.popBackStack()
    }
}