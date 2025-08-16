package com.novel.page.read.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import com.novel.utils.SwipeBackContainer
import com.novel.utils.wdp
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 覆盖翻页容器 - 优化版本，修复边界处理和virtualPageIndex实时更新
 */
@Composable
fun CoverFlipContainer(
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    onPageChange: (FlipDirection) -> Unit,
    onNavigateToReader: ((String, String?) -> Unit)? = null,
    onSwipeBack: (() -> Unit)? = null, // 新增：iOS侧滑返回回调
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
                onClick = onClick
            )
        }
        return
    }

    var dragOffset by remember { mutableFloatStateOf(0f) }
    var flipDirection by remember { mutableStateOf(FlipDirection.NEXT) }
    var isDragging by remember { mutableStateOf(false) }

    // 主要内容区域 - 导航信息现在包含在PageContentDisplay中
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(virtualPageIndex) { // 添加virtualPageIndex作为key，确保手势重置
                detectDragGestures(
                    onDragStart = { 
                        dragOffset = 0f 
                        isDragging = true
                        // 每次手势开始时重置方向状态
                        flipDirection = FlipDirection.NEXT
                    },
                    onDragEnd = {
                        val threshold = size.width * 0.2f // 触发翻页的阈值
                        val shouldFlip = abs(dragOffset) >= threshold

                        if (shouldFlip) {
                            // 立即触发翻页，ViewModel会更新virtualPageIndex
                            onPageChange(flipDirection)
                        }
                        
                        // 重置拖拽状态
                        dragOffset = 0f
                        isDragging = false
                    },
                    onDragCancel = { 
                        dragOffset = 0f 
                        isDragging = false
                    },
                    onDrag = { _, dragAmount ->
                        // 每次拖拽都重新计算方向，不依赖之前的状态
                        val newDirection = if (dragAmount.x > 0) FlipDirection.PREVIOUS else FlipDirection.NEXT
                        val newOffset = dragOffset + dragAmount.x
                        
                        // 检查是否可以朝该方向翻页
                        val canFlip = when(newDirection) {
                            FlipDirection.NEXT -> virtualPageIndex < virtualPages.size - 1
                            FlipDirection.PREVIOUS -> virtualPageIndex > 0
                        }

                        if (canFlip) {
                            dragOffset = newOffset
                            flipDirection = newDirection // 更新当前拖拽方向
                        } else if (newDirection == FlipDirection.PREVIOUS && onSwipeBack != null) {
                            // 如果不能向前翻页，则可能是iOS侧滑返回
                             onSwipeBack()
                        }
                    }
                )
            }
    ) {
        // 计算目标页面信息
        val targetVirtualIndex = when {
            dragOffset > 0 -> virtualPageIndex - 1 // Previous
            dragOffset < 0 -> virtualPageIndex + 1 // Next
            else -> -1
        }

        // 显示背景页（目标页面）
        if (targetVirtualIndex in virtualPages.indices) {
            val targetVirtualPage = virtualPages[targetVirtualIndex]
            PageRenderer(
                virtualPage = targetVirtualPage,
                uiState = uiState,
                readerSettings = readerSettings,
                onLoadBookReviews = onLoadBookReviews,
                onClick = onClick
            )
        }


        // 当前页（覆盖效果）- 移除所有动画和阴影抖动
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(dragOffset.roundToInt(), 0) }
                .shadow(
                    elevation = if (dragOffset != 0f) 8.wdp else 0.wdp,
                    shape = RectangleShape
                )
        ) {
            val currentVirtualPage = virtualPages.getOrNull(virtualPageIndex)
            if (currentVirtualPage != null) {
                PageRenderer(
                    virtualPage = currentVirtualPage,
                    uiState = uiState,
                    readerSettings = readerSettings,
                    onLoadBookReviews = onLoadBookReviews,
                    onClick = onClick
                )
            }
        }
    }
}

/**
 * 辅助函数，用于渲染单个虚拟页面
 */
@Composable
private fun PageRenderer(
    virtualPage: VirtualPage,
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    onLoadBookReviews: ((String) -> Unit)?,
    onClick: () -> Unit
) {
    val loadedChapters = uiState.loadedChapterData

    when (virtualPage) {
        is VirtualPage.BookDetailPage -> {
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
                onClick = onClick
            )
        }
        is VirtualPage.ContentPage -> {
            val chapterData = loadedChapters[virtualPage.chapterId]
            if (chapterData != null) {
                val pageContent = chapterData.pages.getOrNull(virtualPage.pageIndex) ?: ""
                PageContentDisplay(
                    page = pageContent,
                    chapterName = chapterData.chapterName,
                    isFirstPage = virtualPage.pageIndex == 0,
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
    }
}
