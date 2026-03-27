package com.novel.page.read

import com.novel.core.StableThrowable
import com.novel.debug.RuntimeDebugScenarioStore
import com.novel.utils.TimberLogger
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.BackHandler
import com.novel.page.component.FlipBookAnimationController
import com.novel.page.component.LoadingStateComponent
import kotlinx.coroutines.launch
import com.novel.page.component.NovelText
import com.novel.page.component.SolidCircleSlider
import com.novel.page.component.ViewState
import com.novel.page.read.components.ChapterListPanel
import com.novel.page.read.components.CoverFlipContainer
import com.novel.page.read.components.NoAnimationContainer
import com.novel.page.read.components.PageCurlFlipContainer
import com.novel.page.read.components.ReaderSettingsPanel
import com.novel.page.read.components.SlideFlipContainer
import com.novel.page.read.components.VerticalScrollContainer
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.ReaderHistoryCoordinator
import com.novel.page.read.viewmodel.ReaderIntent
import com.novel.page.read.viewmodel.ReaderViewModel
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.ReaderDebugScenarioCoordinator
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.wdp
import com.novel.utils.ssp
import com.novel.utils.NavViewModel
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.toArgb
import com.novel.page.read.viewmodel.PageFlipEffect
import com.novel.page.read.viewmodel.ReaderInfo
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderMappingHelper
import com.novel.page.read.viewmodel.ReaderRestoreHintCoordinator
import com.novel.page.read.viewmodel.ReaderSettingsRefreshCoordinator
import com.novel.page.read.viewmodel.ReaderStartupCoordinator

val LocalReaderInfo = staticCompositionLocalOf<ReaderInfo> {
    error("No ReaderInfo provided")
}

/**
 * 小说阅读器页面
 * @param bookId 书籍ID
 * @param chapterId 章节ID（可选，默认从第一章开始）
 * @param viewModel 阅读器ViewModel
 */
@Composable
fun ReaderPage(
    flipBookController: FlipBookAnimationController? = null,
    bookId: String,
    chapterId: String? = null
) {
    val viewModel: ReaderViewModel = hiltViewModel()
    val activeController = flipBookController ?: NavViewModel.currentFlipBookController()
    val readerHistoryCoordinator = remember { ReaderHistoryCoordinator() }
    val readerRestoreHintCoordinator = remember { ReaderRestoreHintCoordinator() }
    val readerDebugScenarioCoordinator = remember { ReaderDebugScenarioCoordinator() }
    val readerSettingsRefreshCoordinator = remember { ReaderSettingsRefreshCoordinator() }
    val readerStartupCoordinator = remember { ReaderStartupCoordinator() }

    // 性能优化：使用 StateAdapter 的稳定状态访问方法，减少重组
    val adapter = viewModel.adapter
    
    // 迁移到新的优化状态访问方法
    val state by adapter.currentState.collectAsState()
    val isInitialized by adapter.isInitializedState()
    val showProgressRestoredHint by adapter.showProgressRestoredHintState()
    
    val density = LocalDensity.current

    val coroutineScope = rememberCoroutineScope()
    var hasShownRestoreHint by rememberSaveable(bookId, chapterId) { mutableStateOf(false) }
    var shouldShowRestoreHintForEntry by rememberSaveable(bookId, chapterId) { mutableStateOf(false) }
    var restoreHintManagedByCoordinator by rememberSaveable(bookId, chapterId) { mutableStateOf(false) }
    var hasPendingPostInitRefresh by rememberSaveable(bookId, chapterId) { mutableStateOf(true) }
    var previousReaderSettings by remember { mutableStateOf<ReaderSettings?>(null) }

    // —— 只做反向动画 + 清理 controller，不做导航 ——
    val reverseOnly = {
            coroutineScope.launch {
                activeController?.triggerReverseAnimation()
            }
            NavViewModel.setFlipBookController(null)
    }

    // —— 真正的"后退"：先做动画，再清理 controller，最后导航 ——
    val performBack = {
            reverseOnly()
            NavViewModel.navigateBack()
    }

    // 系统返回键处理
    BackHandler {
        performBack()
    }

    // 性能优化：缓存初始化阅读器回调
    val handleInitReader = remember(viewModel) { { bookId: String, chapterId: String? ->
        TimberLogger.d("ReaderPage", "开始加载书籍和章节内容")
        viewModel.sendIntent(ReaderIntent.InitReader(bookId, chapterId))
    } }
    
    LaunchedEffect(bookId, chapterId) {
        TimberLogger.d("ReaderPage", "ReaderPage参数变化: bookId=$bookId, chapterId=$chapterId")
        val launchPlan = readerStartupCoordinator.createInitialLoadPlan(
            bookId = bookId,
            chapterId = chapterId,
        )
        if (launchPlan.shouldInitReader) {
            shouldShowRestoreHintForEntry = launchPlan.shouldShowProgressRestoredHint
            handleInitReader(bookId, chapterId)
        } else {
            TimberLogger.w("ReaderPage", "书籍ID或章节ID为空，跳过加载")
        }
    }
    
    LaunchedEffect(chapterId, state.isSuccess, state.currentPageData?.chapterId) {
        if (readerRestoreHintCoordinator.shouldShowHint(
                shouldRestoreFromProgress = shouldShowRestoreHintForEntry,
                isInitSuccess = state.isSuccess,
                hasPageData = state.currentPageData != null,
                hasShownHint = hasShownRestoreHint,
            )
        ) {
            hasShownRestoreHint = true
            shouldShowRestoreHintForEntry = false
            restoreHintManagedByCoordinator = true
            TimberLogger.d(
                "ReaderPage",
                "trace action=restore_hint_show source=restored_progress budgetMs=${readerRestoreHintCoordinator.hintVisibleDurationMs()}",
            )
            viewModel.sendIntent(ReaderIntent.ShowProgressRestoredHint(true))
        }
    }

    LaunchedEffect(showProgressRestoredHint, restoreHintManagedByCoordinator) {
        if (
            readerRestoreHintCoordinator.shouldAutoDismissHint(
                isHintVisible = showProgressRestoredHint,
                wasShownForRestore = restoreHintManagedByCoordinator,
            )
        ) {
            delay(readerRestoreHintCoordinator.hintVisibleDurationMs())
            TimberLogger.d(
                "ReaderPage",
                "trace action=restore_hint_hide source=restored_progress budgetMs=${readerRestoreHintCoordinator.hintVisibleDurationMs()}",
            )
            viewModel.sendIntent(ReaderIntent.ShowProgressRestoredHint(false))
            restoreHintManagedByCoordinator = false
        }
    }

    // 当状态初始化完成且有书籍信息时保存历史记录
    LaunchedEffect(state.isSuccess, state.currentChapter, state.currentPageData?.bookInfo) {
        val historyIntent = readerHistoryCoordinator.createSaveToHistoryIntent(
            state = state,
            bookId = bookId,
        )
        if (historyIntent != null) {
            TimberLogger.d("ReaderPage", "保存历史记录: bookInfo=${state.currentPageData?.bookInfo}, chapter=${state.currentChapter?.chapterName}")
            viewModel.sendIntent(historyIntent)
        }

    }

    LaunchedEffect(isInitialized, state.containerSize, hasPendingPostInitRefresh) {
        if (readerStartupCoordinator.shouldRefreshContainerAfterInit(
                isInitialized = isInitialized,
                containerSize = state.containerSize,
                hasPendingPostInitRefresh = hasPendingPostInitRefresh,
            )
        ) {
            viewModel.sendIntent(ReaderIntent.UpdateContainerSize(state.containerSize, density))
            hasPendingPostInitRefresh = false
            TimberLogger.d("ReaderPage", "初始化后补发容器尺寸更新: ${state.containerSize}")
        }
    }

    LaunchedEffect(isInitialized, state.currentPageData?.chapterId) {
        val autoFlipPlan = readerDebugScenarioCoordinator.createAutoFlipPlan(
            isInitialized = isInitialized,
            hasCurrentPage = state.currentPageData != null,
            direction = readerDebugScenarioCoordinator.resolveAutoFlipDirection(
                RuntimeDebugScenarioStore.currentReaderAutoFlipDirection(),
            ),
        )
        val autoFlipDirection = autoFlipPlan.direction
        if (autoFlipPlan.shouldTriggerAutoFlip && autoFlipDirection != null) {
            TimberLogger.d("ReaderPage", "触发debug自动翻页: $autoFlipDirection")
            viewModel.sendIntent(ReaderIntent.PageFlip(autoFlipDirection))
        }
    }

    LaunchedEffect(state.readerSettings) {
        TimberLogger.d("ReaderPage", "设置状态变化监听触发")
        TimberLogger.d(
            "ReaderPage",
            "当前背景颜色: ${
                String.format(
                    "#%08X",
                    state.readerSettings.backgroundColor.toArgb()
                )
            }"
        )
        TimberLogger.d(
            "ReaderPage",
            "当前文字颜色: ${String.format("#%08X", state.readerSettings.textColor.toArgb())}"
        )
        TimberLogger.d("ReaderPage", "当前字体大小: ${state.readerSettings.fontSize}sp")
        TimberLogger.d("ReaderPage", "当前翻页效果: ${state.readerSettings.pageFlipEffect}")

        // 强制触发更新容器尺寸，确保页数正确刷新
        if (readerSettingsRefreshCoordinator.shouldRefreshPagination(
                previousSettings = previousReaderSettings,
                currentSettings = state.readerSettings,
                containerSize = state.containerSize,
            )
        ) {
            viewModel.sendIntent(ReaderIntent.UpdateContainerSize(state.containerSize, density))
            TimberLogger.d("ReaderPage", "强制触发容器尺寸更新以刷新页数: ${state.containerSize}")
        }
    }

    // 清理controller的DisposableEffect
    SideEffect {
        previousReaderSettings = state.readerSettings
    }

    DisposableEffect(Unit) {
        onDispose { NavViewModel.setFlipBookController(null) }
    }

    // 将需要传递给子组件的状态打包
    val readerInfo = ReaderMappingHelper.toReaderInfo(state)

    // 性能优化：缓存重试回调
    val handleRetry = remember(viewModel) { {
        viewModel.sendIntent(ReaderIntent.Retry)
    } }
    
    // 优化：使用 remember 避免重复创建适配器对象，并稳定依赖项
    val loadingStateComponent = remember(
        state.hasError,
        state.isEmpty,
        state.error,
        state.isLoading,
        bookId,
        handleRetry
    ) {
        object : LoadingStateComponent {
            override val loading: Boolean get() = state.isLoading
            override val containsCancelable: Boolean get() = false
            override val viewState: ViewState
                get() = when {
                    state.hasError -> ViewState.Error(StableThrowable(Exception(state.error)))
                    state.isEmpty -> ViewState.Empty
                    else -> ViewState.Idle
                }

            override fun showLoading(show: Boolean) {
                // 状态由ViewModel管理，无需实现
            }

            override fun cancelLoading() {
                // 无需实现
            }

            override fun showViewState(viewState: ViewState) {
                // 状态由ViewModel管理，无需实现
            }

            override fun retry() {
                handleRetry()
            }
        }
    }

    // 左滑进入阅读器的回调函数
    val handleLeftSwipeToReader: () -> Unit = {
        TimberLogger.d("BookDetailPage", "左滑进入阅读器: bookId=$bookId")
        // 把当前的 FlipBookAnimationController 暂存到全局
        NavViewModel.setFlipBookController(activeController)
    }
    CompositionLocalProvider(LocalReaderInfo provides readerInfo) {
        LoadingStateComponent(
            component = loadingStateComponent,
            modifier = Modifier.fillMaxSize(),
            backgroundColor = NovelColors.NovelBookBackground,
            flipBookController = activeController,
            onLeftSwipeToReader = handleLeftSwipeToReader
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(state.readerSettings.backgroundColor)
                    .semantics { contentDescription = "阅读页面" }
                    .onSizeChanged { size ->
                        if (size != IntSize.Zero) {
                            viewModel.sendIntent(ReaderIntent.UpdateContainerSize(size, density))
                        }
                    }
            ) {
                when {
                    state.isLoading -> {
                        LoadingIndicator()
                    }

                    state.hasError -> {
                        // 性能优化：缓存错误重试回调
                        val onErrorRetry = remember(viewModel) { {
                            viewModel.sendIntent(ReaderIntent.Retry)
                        } }
                        
                        ErrorContent(
                            error = state.error?:"",
                            onRetry = onErrorRetry
                        )
                    }

                    isInitialized -> {
                        // 主阅读内容 - 使用整合的翻页容器以支持新的优化
                        if (state.currentPageData != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                IntegratedPageFlipContainer(
                                    state = state,
                                    onIntent = viewModel::sendIntent,
                                    onSwipeBack = {
                                        // iOS侧滑返回
                                        reverseOnly()
                                    },
                                    onClick = {
                                        viewModel.sendIntent(ReaderIntent.ToggleMenu(!state.isMenuVisible))
                                    }
                                )
                            }
                        } else {
                            // 兼容旧的显示方式，当分页数据还未准备好时
                            // 同时更新容器尺寸
                            ReaderContent(
                                state = state,
                                onClick = {
                                     viewModel.sendIntent(ReaderIntent.ToggleMenu(!state.isMenuVisible))
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .onSizeChanged { size ->
                                        // 确保容器尺寸信息及时更新到ViewModel
                                        if (size.width > 0 && size.height > 0) {
                                            viewModel.sendIntent(
                                                ReaderIntent.UpdateContainerSize(
                                                    size,
                                                    density
                                                )
                                            )
                                        }
                                    }
                            )
                        }

                        AnimatedVisibility(
                            visible = state.isChapterListVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 60.wdp)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))  // 让面板从剩余区域向上滑
                                
                                // 性能优化：缓存章节列表回调函数  
                                val onChapterSelected = remember(viewModel) { { chapter: com.novel.page.read.viewmodel.Chapter ->
                                    // 保存当前进度
                                    viewModel.sendIntent(ReaderIntent.SaveProgress())
                                    // 切换章节
                                    viewModel.sendIntent(ReaderIntent.SwitchToChapter(chapter.id))
                                    viewModel.sendIntent(ReaderIntent.ShowChapterList(false))
                                } }
                                
                                val onChapterListDismiss = remember(viewModel) { {
                                    viewModel.sendIntent(ReaderIntent.ShowChapterList(false))
                                } }
                                
                                ChapterListPanel(
                                    chapters = state.chapterList,
                                    currentChapterId = state.currentChapter?.id ?: "",
                                    backgroundColor = state.readerSettings.backgroundColor,
                                    onChapterSelected = onChapterSelected,
                                    onDismiss = onChapterListDismiss,
                                    modifier = Modifier.clickable(enabled = false) { } // 阻止穿透
                                )
                            }
                        }

                        // —— 再绘制"设置"面板（如果 showSettings == true）——
                        AnimatedVisibility(
                            visible = state.isSettingsPanelVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 60.wdp)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                
                                // 性能优化：缓存设置变更回调
                                val onSettingsChange = remember(viewModel) { { settings: ReaderSettings ->
                                    viewModel.sendIntent(ReaderIntent.UpdateSettings(settings))
                                } }
                                
                                ReaderSettingsPanel(
                                    settings = state.readerSettings,
                                    onSettingsChange = onSettingsChange,
                                    modifier = Modifier.clickable(enabled = false) { }
                                )
                            }
                        }

                        // —— 最后再绘制"控制面板" ——
                        // 阅读进度恢复提示
                        AnimatedVisibility(
                            visible = showProgressRestoredHint,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it })
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.wdp),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = NovelColors.NovelMain.copy(alpha = 0.9f)
                                    ),
                                    shape = RoundedCornerShape(8.wdp)
                                ) {
                                    Text(
                                        text = "已恢复上次阅读位置",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 16.wdp,
                                            vertical = 8.wdp
                                        )
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = state.isMenuVisible,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            ReaderControls(
                                state = state,
                                hideProgress = state.isSettingsPanelVisible || state.isChapterListVisible,
                                onBack = performBack,
                                onIntent = viewModel::sendIntent
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 策略模式分发容器
 */
@Composable
private fun IntegratedPageFlipContainer(
    state: ReaderState,
    onIntent: (ReaderIntent) -> Unit,
    onSwipeBack: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    val onPageChange: (FlipDirection) -> Unit = { direction ->
        if (state.isSettingsPanelVisible || state.isChapterListVisible) {
            onIntent(ReaderIntent.ShowSettingsPanel(false))
            onIntent(ReaderIntent.ShowChapterList(false))
        } else {
            onIntent(ReaderIntent.PageFlip(direction))
        }
    }
    
    val onChapterChange: (FlipDirection) -> Unit = { direction ->
        if (state.isSettingsPanelVisible || state.isChapterListVisible) {
            onIntent(ReaderIntent.ShowSettingsPanel(false))
            onIntent(ReaderIntent.ShowChapterList(false))
        } else {
            when (direction) {
                FlipDirection.NEXT -> onIntent(ReaderIntent.NextChapter)
                FlipDirection.PREVIOUS -> onIntent(ReaderIntent.PreviousChapter)
            }
        }
    }
    
    val onNavigateToReader : () -> Unit = {
        onIntent(ReaderIntent.PageFlip(FlipDirection.NEXT))
    }

    when (state.readerSettings.pageFlipEffect) {
        PageFlipEffect.NONE -> NoAnimationContainer(
            uiState = state,
            readerSettings = state.readerSettings,
            onPageChange = onPageChange,
            onSwipeBack = onSwipeBack,
            onLoadBookReviews = { bookId -> onIntent(ReaderIntent.LoadBookReviews(bookId)) },
            onClick = onClick
        )

        PageFlipEffect.COVER -> CoverFlipContainer(
            uiState = state,
            readerSettings = state.readerSettings,
            onPageChange = onPageChange,
            onNavigateToReader = { _, _ -> onNavigateToReader() },
            onSwipeBack = onSwipeBack,
            onLoadBookReviews = { bookId -> onIntent(ReaderIntent.LoadBookReviews(bookId)) },
            onClick = onClick
        )

        PageFlipEffect.SLIDE -> SlideFlipContainer(
            uiState = state,
            readerSettings = state.readerSettings,
            onPageChange = onPageChange,
            onSwipeBack = onSwipeBack,
            onLoadBookReviews = { bookId -> onIntent(ReaderIntent.LoadBookReviews(bookId)) },
            onClick = onClick,
            onSlideIndexChange = { newIndex ->
                // 使用专用的更新方法避免循环更新
                onIntent(ReaderIntent.UpdateSlideIndex(newIndex))
            }
        )

        PageFlipEffect.PAGECURL -> PageCurlFlipContainer(
            uiState = state,
            readerSettings = state.readerSettings,
            onPageChange = onPageChange,
            onSwipeBack = onSwipeBack,
            onLoadBookReviews = { bookId -> onIntent(ReaderIntent.LoadBookReviews(bookId)) },
            onClick = onClick
        )

        PageFlipEffect.VERTICAL -> VerticalScrollContainer(
            uiState = state,
            readerSettings = state.readerSettings,
            onChapterChange = onChapterChange,
            onVerticalScrollPageChange = { pageIndex ->
                 onIntent(ReaderIntent.UpdateScrollPosition(pageIndex))
            },
            onClick = onClick
        )
    }
}

/**
 * 阅读内容组件（兼容旧版本）
 */
@Composable
private fun ReaderContent(
    state: ReaderState,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.wdp, vertical = 20.wdp),
        verticalArrangement = Arrangement.spacedBy(8.wdp)
    ) {
        // 章节标题
        item(key = "chapter_title") {
            Text(
                text = state.currentChapter?.chapterName ?: "",
                fontSize = (state.readerSettings.fontSize + 4).sp,
                fontWeight = FontWeight.Bold,
                color = state.readerSettings.textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.wdp)
            )
        }

        // 章节内容
        item(key = "chapter_content") {
            Text(
                text = state.bookContent.replace("<br/><br/>", "\n\n").replace("<br/>", "\n"),
                fontSize = state.readerSettings.fontSize.sp,
                color = state.readerSettings.textColor,
                lineHeight = (state.readerSettings.fontSize * 1.5).sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 阅读器控制面板
 */
@Composable
private fun ReaderControls(
    state: ReaderState,
    onBack: () -> Unit,
    hideProgress: Boolean = false,
    onIntent: (ReaderIntent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部控制栏
        TopBar(
            onBack = onBack,
            readerSettings = state.readerSettings
        )

        Spacer(modifier = Modifier.weight(1f))

        // 底部控制栏
        BottomControls(
            state = state,
            hideProgress = hideProgress,
            onIntent = onIntent
        )
    }
}

/**
 * 顶部控制栏
 */
@Composable
private fun TopBar(
    onBack: () -> Unit,
    readerSettings: ReaderSettings
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(readerSettings.backgroundColor.copy(alpha = 0.95f))
            .padding(4.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = readerSettings.textColor
            )
        }
    }
}

/**
 * 底部控制栏
 */
@Composable
private fun BottomControls(
    state: ReaderState,
    hideProgress: Boolean = false,
    onIntent: (ReaderIntent) -> Unit
) {
    val readerInfo = LocalReaderInfo.current
    val paginationState = readerInfo.paginationState
    val totalPages = readerInfo.pageCountCache?.totalPages
        ?: paginationState.estimatedTotalPages.takeIf { it > 0 }
        ?: 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(state.readerSettings.backgroundColor.copy(alpha = 1f))
    ) {
        if (!hideProgress) {
            // Pagination progress indicator
            if (paginationState.isCalculating) {
                Text(
                    text = "正在计算总页数...",
                    fontSize = 10.ssp,
                    color = state.readerSettings.textColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }

            // 第一行：上一章、进度条、下一章
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.wdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.wdp)
            ) {
                // 性能优化：缓存章节导航回调  
                val onPreviousChapter = remember(onIntent) { {
                    onIntent(ReaderIntent.PreviousChapter)
                } }
                
                val onNextChapter = remember(onIntent) { {
                    onIntent(ReaderIntent.NextChapter)
                } }
                
                val onProgressSeek = remember(onIntent, totalPages) { { rawValue: Float ->
                    // 仿照亮度控制，添加步进量化
                    val stepSize = 20f / (totalPages.takeIf { it > 0 } ?: 1)
                    val stepped = (rawValue / stepSize).roundToInt() * stepSize
                    val clamped = stepped.coerceIn(0f, 1f)
                    onIntent(ReaderIntent.SeekToProgress(clamped))
                } }

                NovelText(
                    "上一章",
                    fontSize = 14.ssp,
                    fontWeight = FontWeight.Bold,
                    color = state.readerSettings.textColor,
                    modifier = Modifier.debounceClickable(onClick = onPreviousChapter)
                )

                // 进度条 - 改为SolidCircleSlider
                SolidCircleSlider(
                    progress = state.computedReadingProgress,
                    onValueChange = onProgressSeek,
                    modifier = Modifier.weight(1f),
                    trackColor = Color.Gray.copy(alpha = 0.1f),
                    progressColor = Color.Gray.copy(alpha = 0.5f),
                    thumbColor = state.readerSettings.backgroundColor,
                    trackHeightDp = 24.dp,
                    thumbRadiusDp = 16.dp
                )

                NovelText(
                    "下一章",
                    fontSize = 14.ssp,
                    fontWeight = FontWeight.Bold,
                    color = state.readerSettings.textColor,
                    modifier = Modifier.debounceClickable(onClick = onNextChapter)
                )
            }
        } else {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.02.wdp),
                color = Color.Gray.copy(alpha = 0.1f),
            )
        }

        // 第二行：功能按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.wdp)
                .height(60.wdp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 性能优化：缓存控制按钮回调
            val onShowChapterList = remember(onIntent) { {
                onIntent(ReaderIntent.ShowChapterList(true))
            } }
            
            val onToggleNightMode = remember { {
                /* 暂不实现 */
            } }
            
            val onShowSettings = remember(onIntent) { {
                onIntent(ReaderIntent.ShowSettingsPanel(true))
            } }

            ControlButton(
                icon = Icons.AutoMirrored.Filled.List,
                text = "目录",
                onClick = onShowChapterList,
                tintColor = state.readerSettings.textColor
            )

            ControlButton(
                icon = Icons.Default.Star,
                text = "夜间",
                onClick = onToggleNightMode,
                tintColor = state.readerSettings.textColor
            )

            ControlButton(
                icon = Icons.Default.Settings,
                text = "设置",
                onClick = onShowSettings,
                tintColor = state.readerSettings.textColor
            )
        }
    }
}

/**
 * 控制按钮
 */
@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    tintColor: Color = Color(0xFF2E2E2E) // 使用安全的默认深灰色，而不是主题色
) {
    // 性能优化：缓存点击回调
    val onClickCached = remember(onClick) { onClick }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClickCached() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tintColor,
            modifier = Modifier.size(24.wdp)
        )
        Text(
            text = text,
            color = tintColor,
            fontSize = 12.sp
        )
    }
}

/**
 * 加载指示器
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = NovelColors.NovelMain)
    }
}

/**
 * 错误内容
 */
@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            color = NovelColors.NovelError,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.wdp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = NovelColors.NovelMain)
        ) {
            Text("重试")
        }
    }
}
