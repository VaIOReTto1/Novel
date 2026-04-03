package com.novel.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.novel.page.MainPage
import com.novel.page.book.BookDetailPage
import com.novel.page.login.LoginPage
import com.novel.page.read.ReaderPage
import com.novel.page.search.FullRankingPage
import com.novel.page.search.SearchPage
import com.novel.page.search.SearchResultPage
import com.novel.rn.MviModuleType
import com.novel.rn.ReactNativePage
import com.novel.ui.showcase.NovelDesignShowcaseScreen

@Composable
fun NavigationSetup(debugRoute: String? = null) {
    TimberLogger.d("NavigationSetup", "NavigationSetup recomposed")

    val navController = rememberNavController()
    var debugRouteHandled by remember(debugRoute) { mutableStateOf(false) }

    DisposableEffect(navController) {
        TimberLogger.d("NavigationSetup", "Attach NavController to NavViewModel")
        NavViewModel.navController.value = navController

        onDispose {
            TimberLogger.d("NavigationSetup", "Detach NavController from NavViewModel")
            if (NavViewModel.navController.value == navController) {
                NavViewModel.navController.value = null
            }
        }
    }

    LaunchedEffect(navController, debugRoute, debugRouteHandled) {
        if (!debugRoute.isNullOrBlank() && !debugRouteHandled) {
            TimberLogger.d("NavigationSetup", "Navigate to debug route: $debugRoute")
            runCatching {
                navController.navigate(debugRoute)
            }.onFailure { error ->
                TimberLogger.e("NavigationSetup", "Failed to navigate debug route: $debugRoute", error)
            }
            debugRouteHandled = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "main",
    ) {
        registerHostRoutes()
    }
}

private fun NavGraphBuilder.registerHostRoutes() {
    composable("main") {
        MainPage()
    }
    composable("login") {
        LoginPage()
    }
    composable("search?query={query}") {
        SearchPage(
            onNavigateBack = NavViewModel::navigateBack,
            onNavigateToBookDetail = { bookId ->
                NavViewModel.navigateToReader(bookId.toString(), null)
            },
        )
    }
    composable("search_result?query={query}") { backStackEntry ->
        SearchResultPage(
            initialQuery = backStackEntry.arguments?.getString("query") ?: "",
        )
    }
    composable("full_ranking/{rankingType}/{encodedData}") { backStackEntry ->
        val rankingType = backStackEntry.arguments?.getString("rankingType") ?: ""
        val encodedData = backStackEntry.arguments?.getString("encodedData") ?: ""
        val rankingItems = runCatching {
            NavViewModel.decodeRankingData(encodedData)
        }.getOrElse { error ->
            TimberLogger.e("NavigationUtil", "Failed to decode ranking data: $encodedData", error)
            emptyList()
        }
        FullRankingPage(
            rankingType = rankingType,
            rankingItems = rankingItems,
            onNavigateBack = NavViewModel::navigateBack,
            onNavigateToBookDetail = { bookId ->
                NavViewModel.navigateToReader(bookId = bookId.toString(), null)
            },
        )
    }
    composable("book_detail/{bookId}?fromRank={fromRank}") { backStackEntry ->
        BookDetailPage(
            bookId = backStackEntry.arguments?.getString("bookId") ?: "",
            onNavigateToReader = { bookId, chapterId ->
                NavViewModel.navigateToReader(bookId, chapterId)
            },
        )
    }
    composable("reader/{bookId}?chapterId={chapterId}") { backStackEntry ->
        ReaderPage(
            bookId = backStackEntry.arguments?.getString("bookId") ?: "",
            chapterId = backStackEntry.arguments?.getString("chapterId"),
        )
    }
    composable("novel_design_showcase") {
        NovelDesignShowcaseScreen()
    }

    reactNativeRoute(
        route = "profile",
        componentName = "Novel",
        initialProps = mapOf("nativeMessage" to "ProfilePage"),
        destroyOnBack = false,
    )
    reactNativeRoute(
        route = "settings",
        componentName = "SettingsPageComponent",
        initialProps = mapOf("source" to "android_settings"),
        destroyOnBack = true,
        mviModuleType = MviModuleType.BOTH,
    )
    reactNativeRoute(
        route = "timed_switch",
        componentName = "TimedSwitchPageComponent",
        initialProps = mapOf("source" to "android_timed_switch"),
        destroyOnBack = true,
        mviModuleType = MviModuleType.BOTH,
    )
    reactNativeRoute(
        route = "privacy_policy",
        componentName = "PrivacyPolicyPageComponent",
        initialProps = mapOf("source" to "android_privacy_policy"),
    )
    reactNativeRoute(
        route = "help_support",
        componentName = "HelpSupportPageComponent",
        initialProps = mapOf("source" to "android_privacy_policy"),
    )
    reactNativeRoute(
        route = "history",
        componentName = "HistoryPageComponent",
        initialProps = mapOf("source" to "android_history"),
    )
    reactNativeRoute(
        route = "message",
        componentName = "MessagePageComponent",
        initialProps = mapOf("source" to "android_message"),
    )
    composable("becomewriter?isAuthor={isAuthor}") { backStackEntry ->
        reactNativeScreen(
            componentName = "BecomeWriterPageComponent",
            initialProps = mapOf(
                "source" to "android_becomewriter",
                "isAuthor" to (backStackEntry.arguments?.getString("isAuthor")?.toBooleanStrictOrNull() ?: false),
            ),
        )
    }
    reactNativeRoute(
        route = "writepage",
        componentName = "WritePageComponent",
        initialProps = mapOf("source" to "android_writepage"),
    )
    reactNativeRoute(
        route = "aipage",
        componentName = "AIWriteAssistantComponent",
        initialProps = mapOf("source" to "android_aipage"),
    )
    reactNativeRoute(
        route = "bookmanage",
        componentName = "BookManagePageComponent",
        initialProps = mapOf("source" to "android_bookmanage"),
    )
    reactNativeRoute(
        route = "recommendbook",
        componentName = "RecommendBookPageComponent",
        initialProps = mapOf("source" to "android_recommendbook"),
    )
    reactNativeRoute(
        route = "viewedusers",
        componentName = "ViewedUsersPageComponent",
        initialProps = mapOf("source" to "android_viewedusers"),
    )
    reactNativeRoute(
        route = "myreservation",
        componentName = "MyReservationPageComponent",
        initialProps = mapOf("source" to "android_myreservation"),
    )
    reactNativeRoute(
        route = "membercenter",
        componentName = "MemberCenterPageComponent",
        initialProps = mapOf("source" to "android_membercenter"),
    )
    reactNativeRoute(
        route = "feedbackhelp",
        componentName = "FeedbackHelpMainPageComponent",
        initialProps = mapOf("source" to "android_feedbackhelp"),
    )
    reactNativeRoute(
        route = "questionlist",
        componentName = "QuestionListPageComponent",
        initialProps = mapOf("source" to "android_questionlist"),
    )
    reactNativeRoute(
        route = "questiondetail",
        componentName = "QuestionDetailPageComponent",
        initialProps = mapOf("source" to "android_questiondetail"),
    )
    composable("comment/{bookData}") { backStackEntry ->
        reactNativeScreen(
            componentName = "CommentPageComponent",
            initialProps = mapOf(
                "bookData" to NavViewModel.decodeBookData(backStackEntry.arguments?.getString("bookData") ?: ""),
                "source" to "android_comment",
            ),
        )
    }
    composable("writereview/{bookId}?rating={rating}") { backStackEntry ->
        val initialProps = mutableMapOf<String, Any>(
            "bookId" to (backStackEntry.arguments?.getString("bookId") ?: ""),
            "source" to "android_writereview",
        )
        backStackEntry.arguments?.getString("rating")?.toIntOrNull()?.let { rating ->
            initialProps["initialRating"] = rating.toString()
        }
        reactNativeScreen(
            componentName = "WriteReviewPageComponent",
            initialProps = initialProps,
        )
    }
    reactNativeRoute(
        route = "writereview",
        componentName = "WriteReviewPageComponent",
        initialProps = mapOf("source" to "android_writereview"),
    )
    composable("reviewdetail/{commentData}") { backStackEntry ->
        reactNativeScreen(
            componentName = "ReviewDetailPageComponent",
            initialProps = mapOf(
                "commentData" to NavViewModel.decodeCommentData(backStackEntry.arguments?.getString("commentData") ?: ""),
                "source" to "android_reviewdetail",
            ),
        )
    }
}

private fun NavGraphBuilder.reactNativeRoute(
    route: String,
    componentName: String,
    initialProps: Map<String, Any>,
    destroyOnBack: Boolean = true,
    mviModuleType: MviModuleType = MviModuleType.BRIDGE,
) {
    composable(route) {
        reactNativeScreen(
            componentName = componentName,
            initialProps = initialProps,
            destroyOnBack = destroyOnBack,
            mviModuleType = mviModuleType,
        )
    }
}

@Composable
private fun reactNativeScreen(
    componentName: String,
    initialProps: Map<String, Any>,
    destroyOnBack: Boolean = true,
    mviModuleType: MviModuleType = MviModuleType.BRIDGE,
) {
    ReactNativePage(
        componentName = componentName,
        initialProps = initialProps,
        destroyOnBack = destroyOnBack,
        mviModuleType = mviModuleType,
    )
}
