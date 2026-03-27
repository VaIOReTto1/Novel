package com.novel.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.novel.page.component.LaunchDialogType
import com.novel.page.component.ShortDramaToastData
import com.novel.page.component.rememberFlipBookAnimationController
import com.novel.ui.theme.NovelTheme
import com.novel.utils.DialogLaunchManager
import kotlinx.coroutines.launch

private val mainPageTabs = listOf(
    MainPageTab("首页", com.novel.R.drawable.home),
    MainPageTab("分类", com.novel.R.drawable.clarify),
    MainPageTab("福利", com.novel.R.drawable.welfare),
    MainPageTab("书架", com.novel.R.drawable.bookshelf),
    MainPageTab("我的", com.novel.R.drawable.my),
)

@Composable
fun MainPage() {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { mainPageTabs.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val globalFlipBookController = rememberFlipBookAnimationController()
    val startupUiCoordinator = remember { MainPageStartupUiCoordinator() }
    val startupUiState = remember { startupUiCoordinator.createInitialUiState() }

    var launchDialogType by remember { mutableStateOf<LaunchDialogType?>(null) }
    var showShortDramaToast by remember { mutableStateOf(startupUiState.showShortDramaToast) }
    var hasRevealedDeferredUi by remember { mutableStateOf(false) }
    val shortDramaData = remember {
        ShortDramaToastData(
            imageUrl = null,
            dramaName = "鍗佸叓宀佸お濂跺ザ椹惧埌,tongtont",
            watchedEpisodes = 1,
            remainingEpisodes = 99,
        )
    }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        val plan = startupUiCoordinator.createAfterFirstFramePlan(
            hasRevealedDeferredUi = hasRevealedDeferredUi,
        )
        if (plan.shouldRevealShortDramaToast) {
            showShortDramaToast = true
        }
        if (plan.shouldLoadLaunchDialog) {
            launchDialogType = DialogLaunchManager.getInstance(context).getDialogTypeToShow()
        }
        hasRevealedDeferredUi = true
    }

    val navigateToPage: (Int) -> Unit = { pageIndex ->
        scope.launch {
            pagerState.scrollToPage(pageIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MainPagePager(
                pagerState = pagerState,
                globalFlipBookController = globalFlipBookController,
                onNavigateToPage = navigateToPage,
            )
            MainPageBottomBar(
                tabs = mainPageTabs,
                selectedPage = pagerState.currentPage,
                onNavigateToPage = navigateToPage,
            )
        }

        MainPageOverlays(
            globalFlipBookController = globalFlipBookController,
            showShortDramaToast = showShortDramaToast,
            shortDramaData = shortDramaData,
            launchDialogType = launchDialogType,
            onContinueShortDrama = {
                navigateToPage(3)
                showShortDramaToast = false
            },
            onCloseShortDrama = {
                showShortDramaToast = false
            },
            onDismissLaunchDialog = {
                launchDialogType = null
            },
            onLaunchDialogPrimaryAction = {
                if (launchDialogType == LaunchDialogType.SIGNIN_BONUS) {
                    navigateToPage(2)
                }
            },
        )
    }
}

@Preview
@Composable
private fun MainPagePreview() {
    NovelTheme {
        MainPage()
    }
}
