package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.utils.SwipeBackContainer
import kotlinx.coroutines.flow.distinctUntilChanged
import com.novel.page.read.viewmodel.VirtualPage

/**
 * 平移翻页容器 - ViewPager风格，支持章节切换
 * 修复版本：解决翻页回跳问题和手势冲突，确保流畅的翻页体验
 */
@Composable
fun SlideFlipContainer(
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    onPageChange: (FlipDirection) -> Unit,
    onSwipeBack: (() -> Unit)? = null,
    onLoadBookReviews: ((String) -> Unit)? = null,
    onClick: () -> Unit,
    onSlideIndexChange: ((Int) -> Unit)? = null // 新增：平移模式专用的索引更新回调){}
) {
    val virtualPages = uiState.virtualPages
    val virtualPageIndex = uiState.virtualPageIndex
    val loadedChapters = uiState.loadedChapterData

    if (virtualPages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().clickable { onClick() })
        return
    }

    val totalPages = virtualPages.size
    val currentVirtualPage = virtualPages.getOrNull(virtualPageIndex)

    val pagerState = rememberPagerState(
        initialPage = virtualPageIndex,
        pageCount = { totalPages }
    )
    
    val isOnBookDetailPage = currentVirtualPage is VirtualPage.BookDetailPage

    // 状态管理 - 修复循环更新问题
    var lastKnownVirtualPageIndex by remember { mutableIntStateOf(virtualPageIndex) }
    var isUserScrolling by remember { mutableStateOf(false) }
    var shouldSyncFromViewModel by remember { mutableStateOf(false) }

    // 调试日志
    TimberLogger.d("SlideFlipContainer", "State - virtualPageIndex: $virtualPageIndex, pagerPage: ${pagerState.currentPage}, lastKnown: $lastKnownVirtualPageIndex")

    // 检测 ViewModel 中的 virtualPageIndex 变化（非用户手势触发）
    LaunchedEffect(virtualPageIndex) {
        if (virtualPageIndex != lastKnownVirtualPageIndex && !isUserScrolling) {
            // ViewModel 状态发生了变化，且不是用户滚动触发的
            TimberLogger.d("SlideFlipContainer", "Syncing from ViewModel: $lastKnownVirtualPageIndex -> $virtualPageIndex")
            shouldSyncFromViewModel = true
            pagerState.scrollToPage(virtualPageIndex)
            lastKnownVirtualPageIndex = virtualPageIndex
            shouldSyncFromViewModel = false
        }
    }

    // 监听用户滚动行为
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling && !shouldSyncFromViewModel) {
                    // 用户开始滚动，且不是 ViewModel 同步触发的
                    if (!isUserScrolling) {
                        TimberLogger.d("SlideFlipContainer", "User started scrolling")
                        isUserScrolling = true
                    }
                } else if (!isScrolling && isUserScrolling) {
                    // 用户滚动结束
                    TimberLogger.d("SlideFlipContainer", "User finished scrolling")
                    isUserScrolling = false
                }
            }
    }

    // 监听页面变化并通知 ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { currentPage ->
                if (currentPage != lastKnownVirtualPageIndex && isUserScrolling && !shouldSyncFromViewModel) {
                    TimberLogger.d("SlideFlipContainer", "User flipped page: $lastKnownVirtualPageIndex -> $currentPage")
                    
                    val previousPage = lastKnownVirtualPageIndex
                    lastKnownVirtualPageIndex = currentPage
                    
                    // 优先使用专用的索引更新回调，避免循环更新
                    if (onSlideIndexChange != null) {
                        onSlideIndexChange(currentPage)
                    } else {
                        // 兼容性：使用方向方式
                        val direction = if (currentPage > previousPage) {
                            FlipDirection.NEXT
                        } else {
                            FlipDirection.PREVIOUS
                        }
                        onPageChange(direction)
                    }
                }
            }
    }

    // 当在书籍详情页时，单独渲染以支持侧滑返回
    // 注意：在平移模式中，书籍详情页需要特殊处理以避免与 HorizontalPager 的手势冲突
    if (isOnBookDetailPage) {
        SwipeBackContainer(
            modifier = Modifier.fillMaxSize(),
            onSwipeComplete = onSwipeBack,
            onLeftSwipeToReader = {
                // 左滑进入阅读器：使用专用回调更新索引，避免循环
                val nextIndex = virtualPageIndex + 1
                TimberLogger.d("SlideFlipContainer", "Left swipe to reader: $virtualPageIndex -> $nextIndex")
                if (nextIndex < virtualPages.size) {
                    onSlideIndexChange?.invoke(nextIndex) ?: onPageChange(FlipDirection.NEXT)
                }
            }
        ) {
            val bookInfo = loadedChapters[uiState.currentChapter?.id]?.bookInfo
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

    // 正常的翻页模式
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->
        val virtualPage = virtualPages.getOrNull(pageIndex)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        ) {
            when (virtualPage) {
                is VirtualPage.BookDetailPage -> {
                    // 这种情况理论上不会出现，因为书籍详情页已在上面单独处理
                    val bookInfo = loadedChapters[uiState.currentChapter?.id]?.bookInfo
                    PageContentDisplay(
                        page = "",
                        chapterName = uiState.currentChapter?.chapterName ?: "",
                        isFirstPage = false,
                        isBookDetailPage = true,
                        bookInfo = bookInfo,
                        readerSettings = readerSettings,
                        showNavigationInfo = false,
                        onClick = onClick
                    )
                }
                is VirtualPage.ContentPage -> {
                    val chapterData = loadedChapters[virtualPage.chapterId]
                    if (chapterData != null) {
                        PageContentDisplay(
                            page = chapterData.pages.getOrNull(virtualPage.pageIndex) ?: "",
                            chapterName = chapterData.chapterName,
                            isFirstPage = virtualPage.pageIndex == 0,
                            readerSettings = readerSettings,
                            onClick = onClick
                        )
                    }
                }
                is VirtualPage.ChapterSection -> {
                    // Chapter section not supported in this flip mode
                    Box(modifier = Modifier.fillMaxSize())
                }
                null -> {
                    // Placeholder for loading or error
                    Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}