package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.novel.page.component.PaperTexture
import com.novel.page.component.pagecurl.config.rememberPageCurlConfig
import com.novel.page.component.pagecurl.page.ExperimentalPageCurlApi
import com.novel.page.component.pagecurl.page.PageCurl
import com.novel.page.component.pagecurl.page.rememberPageCurlState
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.VirtualPage
import com.novel.utils.SwipeBackContainer
import androidx.compose.runtime.snapshotFlow
import com.novel.page.read.viewmodel.ReaderSettings
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import com.novel.page.read.viewmodel.ReaderState

/**
 * PageCurl仿真翻页容器
 *
 * 使用PageCurl库实现真实的书页卷曲翻页效果，并集成纸张纹理
 * 支持章节边界检测和自动章节切换，支持书籍详情页
 * 修复版本：解决边界翻页和章节切换问题，添加书籍详情页支持，增强预加载
 *
 * @param uiState 全局UI状态，包含虚拟页面列表和加载的数据
 * @param readerSettings 阅读器设置
 * @param onPageChange 页面变化回调
 * @param onSwipeBack iOS侧滑返回回调
 * @param onClick 点击回调
 */
@OptIn(ExperimentalPageCurlApi::class)
@Composable
fun PageCurlFlipContainer(
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    onPageChange: (FlipDirection) -> Unit,
    onSwipeBack: (() -> Unit)? = null,
    onLoadBookReviews: ((String) -> Unit)? = null,
    onClick: () -> Unit
) {
    val virtualPages = uiState.virtualPages
    val virtualPageIndex = uiState.virtualPageIndex
    val loadedChapters = uiState.loadedChapterData

    TimberLogger.d("PageCurlFlipContainer", "virtualPages: $virtualPages, virtualPageIndex: $virtualPageIndex")

    if (virtualPages.isEmpty()) {
        // 如果虚拟页面为空（例如，初始加载时），显示一个空白或加载指示器
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(readerSettings.backgroundColor)
        )
        return
    }


    val totalPages = virtualPages.size
    val currentVirtualPage = virtualPages.getOrNull(virtualPageIndex)
    val isOnBookDetailPage = currentVirtualPage is VirtualPage.BookDetailPage

    // PageCurl状态管理
    val pageCurlState = rememberPageCurlState(
        initialCurrent = virtualPageIndex
    )

    // 当在书籍详情页时，使用SwipeBackContainer包裹以支持侧滑返回
    if (isOnBookDetailPage) {
        SwipeBackContainer(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = readerSettings.backgroundColor,
            textColor = readerSettings.textColor,
            onSwipeComplete = onSwipeBack,
            onLeftSwipeToReader = {
                onPageChange(FlipDirection.NEXT)
            }
        ) {
            // 在SwipeBackContainer中只渲染详情页
            val bookInfo = (uiState.currentPageData?.bookInfo
                ?: loadedChapters[uiState.currentChapter?.id]?.bookInfo)

            PageContentDisplay(
                page = "",
                chapterName = uiState.currentChapter?.chapterName ?: "",
                isFirstPage = false,
                isBookDetailPage = true,
                bookInfo = bookInfo,
                readerSettings = readerSettings,
                showNavigationInfo = false,
                bookReviews = uiState.bookReviews,
                isLoadingReviews = uiState.isLoadingReviews,
                onLoadBookReviews = onLoadBookReviews,
                onClick = onClick
            )
        }
        return
    }

    // PageCurl配置 - 优化拖拽和点击区域
    val config = rememberPageCurlConfig(
        backPageColor = readerSettings.backgroundColor,
        backPageContentAlpha = 0.15f,
        shadowColor = if (readerSettings.backgroundColor.luminance() > 0.5f) Color.Black else Color.White,
        shadowAlpha = 0.25f,
        shadowRadius = 12.dp,
        dragForwardEnabled = true,
        dragBackwardEnabled = true,
        tapForwardEnabled = true,
        tapBackwardEnabled = true,
        // 扩大点击区域，改善边界操作体验
        tapInteraction = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction(
            forward = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction.Config(
                target = androidx.compose.ui.geometry.Rect(0.25f, 0.0f, 1.0f, 1.0f) // 右侧75%区域
            ),
            backward = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction.Config(
                target = androidx.compose.ui.geometry.Rect(0.0f, 0.0f, 0.75f, 1.0f) // 左侧75%区域
            )
        )
    )

    // 同步外部页面索引变化
    LaunchedEffect(virtualPageIndex) {
        if (virtualPageIndex != pageCurlState.current) {
            pageCurlState.snapTo(virtualPageIndex)
        }
    }

    // 监听PageCurl状态变化并通知ViewModel
    LaunchedEffect(pageCurlState) {
        snapshotFlow { pageCurlState.current }
            .distinctUntilChanged()
            .filter { it != virtualPageIndex } // 只在用户手动翻页时通知
            .collect { currentPage ->
                val direction = if (currentPage > virtualPageIndex) FlipDirection.NEXT else FlipDirection.PREVIOUS
                onPageChange(direction)
            }
    }

    // 添加背景颜色和翻页设置的详细日志
    LaunchedEffect(readerSettings) {
        TimberLogger.d("PageCurlFlipContainer", "PageCurlFlipContainer设置更新")
        TimberLogger.d("PageCurlFlipContainer", "背景颜色: ${String.format("#%08X", readerSettings.backgroundColor.value.toInt())}")
        TimberLogger.d("PageCurlFlipContainer", "文字颜色: ${String.format("#%08X", readerSettings.textColor.value.toInt())}")
        TimberLogger.d("PageCurlFlipContainer", "字体大小: ${readerSettings.fontSize}sp")
        TimberLogger.d("PageCurlFlipContainer", "翻页效果: ${readerSettings.pageFlipEffect}")
        TimberLogger.d("PageCurlFlipContainer", "背景亮度: ${readerSettings.backgroundColor.luminance()}")
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 主要内容区域 - 导航信息现在包含在PageContentDisplay中
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            PageCurl(
                count = totalPages,
                state = pageCurlState,
                config = config,
                modifier = Modifier.fillMaxSize()
            ) { pageIdx ->
                val virtualPage = virtualPages.getOrNull(pageIdx)

                if (virtualPage != null) {
                    // 渲染每一页内容，包含纸张纹理
                    PaperTexture(
                        modifier = Modifier.fillMaxSize(),
                        alpha = 0.04f,
                        density = 1.2f,
                        seed = pageIdx.toLong() * 42L
                    ) {
                        when (virtualPage) {
                            is VirtualPage.BookDetailPage -> {
                                val bookInfo = (uiState.currentPageData?.bookInfo
                                    ?: loadedChapters[uiState.currentChapter?.id]?.bookInfo)
                                // 显示书籍详情页
                                PageContentDisplay(
                                    page = "",
                                    chapterName = uiState.currentChapter?.chapterName ?: "",
                                    isFirstPage = false,
                                    isBookDetailPage = true,
                                    bookInfo = bookInfo,
                                    readerSettings = readerSettings,
                                    showNavigationInfo = false, // 书籍详情页不显示导航信息
                                    bookReviews = uiState.bookReviews,
                                    isLoadingReviews = uiState.isLoadingReviews,
                                    onLoadBookReviews = onLoadBookReviews,
                                    onClick = onClick
                                )
                            }
                            is VirtualPage.ContentPage -> {
                                val chapterData = loadedChapters[virtualPage.chapterId]
                                if (chapterData != null && virtualPage.pageIndex in chapterData.pages.indices) {
                                    PageContentDisplay(
                                        page = chapterData.pages[virtualPage.pageIndex],
                                        chapterName = chapterData.chapterName,
                                        isFirstPage = virtualPage.pageIndex == 0,
                                        readerSettings = readerSettings,
                                        onClick = onClick
                                    )
                                }
                            }
                            is VirtualPage.ChapterSection -> {
                                // Chapter section not supported in this flip mode
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(readerSettings.backgroundColor)
                                )
                            }
                        }
                    }
                } else {
                    // 如果虚拟页面不存在，显示空白页面
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(readerSettings.backgroundColor)
                    )
                }
            }
        }
    }
}