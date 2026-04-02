package com.novel.page

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.novel.page.component.AppLaunchDialog
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.component.GlobalFlipBookOverlay
import com.novel.page.component.LaunchDialogType
import com.novel.page.component.NovelText
import com.novel.page.component.ShortDramaToastData
import com.novel.page.component.ShortSentenceToast
import com.novel.page.home.HomePage
import com.novel.page.welfare.WelfarePage
import com.novel.rn.MviModuleType
import com.novel.rn.ReactNativePage
import com.novel.ui.theme.NovelDesignTokens
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

internal data class MainPageTab(
    val label: String,
    @DrawableRes val iconResId: Int,
)

@Composable
internal fun ColumnScope.MainPagePager(
    pagerState: PagerState,
    globalFlipBookController: FlipBookAnimationController,
    onNavigateToPage: (Int) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .weight(1f)
            .background(color = NovelDesignTokens.color("color.bg.canvas")),
        userScrollEnabled = false,
    ) { pageIndex ->
        when (pageIndex) {
            0 -> HomePage(
                onNavigateToCategory = { onNavigateToPage(1) },
                globalFlipBookController = globalFlipBookController,
            )
            1 -> ReactNativePage(
                componentName = "CategoryPageComponent",
                initialProps = mapOf("source" to "android_category"),
                mviModuleType = MviModuleType.BRIDGE,
            )
            2 -> WelfarePage(
                onNavigateBack = { onNavigateToPage(0) },
            )
            3 -> ReactNativePage(
                componentName = "BookshelfPageComponent",
                initialProps = mapOf("source" to "android_bookshelf"),
                mviModuleType = MviModuleType.BRIDGE,
            )
            4 -> ReactNativePage(
                mviModuleType = MviModuleType.BRIDGE,
            )
            else -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NovelDesignTokens.color("color.bg.surface")),
                contentAlignment = Alignment.Center,
            ) {
                NovelText("Page Not Found", color = NovelDesignTokens.color("color.text.primary"))
            }
        }
    }
}

@Composable
internal fun MainPageBottomBar(
    tabs: List<MainPageTab>,
    selectedPage: Int,
    onNavigateToPage: (Int) -> Unit,
) {
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.wdp),
        containerColor = NovelDesignTokens.color("color.bg.surface"),
        contentPadding = PaddingValues(0.wdp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.wdp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                NavButton(
                    onClick = { onNavigateToPage(index) },
                    isSelected = selectedPage == index,
                    text = tab.label,
                    iconResId = tab.iconResId,
                )
            }
        }
    }
}

@Composable
internal fun BoxScope.MainPageOverlays(
    globalFlipBookController: FlipBookAnimationController,
    showShortDramaToast: Boolean,
    shortDramaData: ShortDramaToastData,
    launchDialogType: LaunchDialogType?,
    onContinueShortDrama: () -> Unit,
    onCloseShortDrama: () -> Unit,
    onDismissLaunchDialog: () -> Unit,
    onLaunchDialogPrimaryAction: () -> Unit,
) {
    GlobalFlipBookOverlay(controller = globalFlipBookController)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 64.wdp)
            .align(Alignment.BottomCenter),
    ) {
        ShortSentenceToast(
            data = shortDramaData,
            visible = showShortDramaToast,
            onContinue = onContinueShortDrama,
            onClose = onCloseShortDrama,
        )
    }

    if (launchDialogType != null) {
        AppLaunchDialog(
            type = launchDialogType,
            onDismiss = onDismissLaunchDialog,
            onPrimaryAction = onLaunchDialogPrimaryAction,
        )
    }
}

@Composable
internal fun NavButton(
    onClick: () -> Unit,
    isSelected: Boolean,
    text: String,
    @DrawableRes iconResId: Int,
) {
    val color = if (isSelected) {
        NovelDesignTokens.color("color.text.primary")
    } else {
        NovelDesignTokens.color("color.text.secondary")
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxHeight()
            .debounceClickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(20.wdp, 20.wdp),
            colorFilter = ColorFilter.tint(color),
        )
        NovelText(
            text = text,
            fontSize = 10.ssp,
            lineHeight = 14.ssp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.wdp),
            style = MaterialTheme.typography.bodyMedium,
            color = color,
        )
    }
}
