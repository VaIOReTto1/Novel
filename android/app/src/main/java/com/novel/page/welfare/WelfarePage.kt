package com.novel.page.welfare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.novel.page.component.WelfareRedPacketDialog
import com.novel.ui.theme.NovelDesignTokens
import com.novel.page.welfare.viewmodel.WelfareViewModel
import com.novel.utils.DialogLaunchManager

@Composable
fun WelfarePage(
    onNavigateBack: () -> Unit = {},
    viewModel: WelfareViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    val isPageVisible = lifecycleState.isAtLeast(Lifecycle.State.STARTED)

    var showWelfareDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isPageVisible) {
        if (isPageVisible) {
            showWelfareDialog = DialogLaunchManager
                .getInstance(context)
                .shouldShowWelfareDialog(0.7f)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NovelDesignTokens.color("color.bg.canvas"))
    ) {
        WelfarePageContent(
            onNavigateBack = onNavigateBack,
            viewModel = viewModel,
        )

        if (showWelfareDialog) {
            WelfareRedPacketDialog(
                onDismiss = { showWelfareDialog = false },
                onOpen = { showWelfareDialog = false },
            )
        }
    }
}
