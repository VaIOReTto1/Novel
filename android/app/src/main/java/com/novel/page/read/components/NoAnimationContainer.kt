package com.novel.page.read.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.utils.SwipeBackContainer
import kotlin.math.abs
import com.novel.page.read.viewmodel.VirtualPage

/**
 * 无动画翻页容器 - 优化版本，修复章节切换问题
 */
@Composable
fun NoAnimationContainer(
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

    if (virtualPages.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClick))
        return
    }

    val currentVirtualPage = virtualPages.getOrNull(virtualPageIndex)
    val isOnBookDetailPage = currentVirtualPage is VirtualPage.BookDetailPage

    // 当在书籍详情页时，使用SwipeBackContainer包裹以支持侧滑返回和提示
    if (isOnBookDetailPage) {
        SwipeBackContainer(
            modifier = Modifier.fillMaxSize(),
            onSwipeComplete = onSwipeBack,
            onLeftSwipeToReader = {
                onPageChange(FlipDirection.NEXT)
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

    var swipeDirection by remember { mutableStateOf<FlipDirection?>(null) }
    var swipeAmount by remember { mutableFloatStateOf(0f) }

    // 主要内容区域
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        swipeDirection = null
                        swipeAmount = 0f
                    },
                    onDragEnd = {
                        swipeDirection?.let { direction ->
                            // 判断是否达到翻页阈值
                            val threshold = size.width * 0.2f
                            if (abs(swipeAmount) >= threshold) {
                                onPageChange(direction)
                            }
                        }
                        swipeDirection = null
                        swipeAmount = 0f
                    },
                    onDragCancel = { 
                        swipeDirection = null
                        swipeAmount = 0f
                    },
                    onDrag = { _, dragAmount ->
                        val deltaX = dragAmount.x
                        swipeAmount += deltaX
                        
                        // 仅在 swipeDirection 未设置时确定方向，避免拖拽过程中方向改变
                        if (swipeDirection == null && abs(deltaX) > 20) {
                             val newDirection = if (deltaX > 0) FlipDirection.PREVIOUS else FlipDirection.NEXT
                            
                             val canFlip = when(newDirection) {
                                 FlipDirection.NEXT -> virtualPageIndex < virtualPages.size - 1
                                 FlipDirection.PREVIOUS -> virtualPageIndex > 0
                             }

                            if (canFlip) {
                                swipeDirection = newDirection
                            } else if (newDirection == FlipDirection.PREVIOUS && onSwipeBack != null) {
                                onSwipeBack()
                            }
                        }
                    }
                )
            }
    ) {
        when (currentVirtualPage) {
            is VirtualPage.ContentPage -> {
                val chapterData = loadedChapters[currentVirtualPage.chapterId]
                if (chapterData != null) {
                    val pageContent = chapterData.pages.getOrNull(currentVirtualPage.pageIndex) ?: ""
                    PageContentDisplay(
                        page = pageContent,
                        chapterName = chapterData.chapterName,
                        isFirstPage = currentVirtualPage.pageIndex == 0,
                        isBookDetailPage = false,
                        bookInfo = null,
                        readerSettings = readerSettings,
                        showNavigationInfo = true,
                        onClick = onClick
                    )
                }
            }
            is VirtualPage.ChapterSection -> {
                // Chapter section not supported in this flip mode
                Box(modifier = Modifier.fillMaxSize().background(readerSettings.backgroundColor))
            }
            null -> {
                 // Placeholder
                 Box(modifier = Modifier.fillMaxSize())
            }

            VirtualPage.BookDetailPage -> TODO()
        }
    }
}
