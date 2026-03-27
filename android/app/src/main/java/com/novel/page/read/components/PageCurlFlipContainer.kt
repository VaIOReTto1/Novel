package com.novel.page.read.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.novel.page.component.PaperTexture
import com.novel.page.component.pagecurl.config.rememberPageCurlConfig
import com.novel.page.component.pagecurl.page.ExperimentalPageCurlApi
import com.novel.page.component.pagecurl.page.PageCurl
import com.novel.page.component.pagecurl.page.PageCurlState
import com.novel.page.component.pagecurl.page.rememberPageCurlState
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import com.novel.utils.SwipeBackContainer
import com.novel.utils.TimberLogger
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalPageCurlApi::class)
@Composable
fun pageCurlFlipContainer(
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    callbacks: PageCurlFlipContainerCallbacks,
) {
    val virtualPages = uiState.virtualPages
    val virtualPageIndex = uiState.virtualPageIndex
    val loadedChapters = uiState.loadedChapterData

    TimberLogger.d(
        "PageCurlFlipContainer",
        "virtualPages: $virtualPages, virtualPageIndex: $virtualPageIndex",
    )

    if (virtualPages.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(readerSettings.backgroundColor),
        )
        return
    }

    val currentVirtualPage = virtualPages.getOrNull(virtualPageIndex)
    if (currentVirtualPage is VirtualPage.BookDetailPage) {
        BookDetailSwipeBackContent(
            uiState = uiState,
            loadedChapters = loadedChapters,
            readerSettings = readerSettings,
            callbacks = callbacks,
        )
        return
    }

    val pageCurlState = rememberPageCurlState(initialCurrent = virtualPageIndex)
    val config = rememberReaderPageCurlConfig(readerSettings)

    SyncPageCurlState(
        pageCurlState = pageCurlState,
        virtualPageIndex = virtualPageIndex,
        onPageChange = callbacks.onPageChange,
    )
    LogPageCurlSettings(readerSettings)
    PageCurlViewport(
        virtualPages = virtualPages,
        uiState = uiState,
        loadedChapters = loadedChapters,
        readerSettings = readerSettings,
        pageCurlState = pageCurlState,
        config = config,
        onLoadBookReviews = callbacks.onLoadBookReviews,
        onClick = callbacks.onClick,
    )
}

@OptIn(ExperimentalPageCurlApi::class)
@Composable
private fun rememberReaderPageCurlConfig(readerSettings: ReaderSettings) = rememberPageCurlConfig(
    backPageColor = readerSettings.backgroundColor,
    backPageContentAlpha = 0.15f,
    shadowColor = if (readerSettings.backgroundColor.luminance() > 0.5f) Color.Black else Color.White,
    shadowAlpha = 0.25f,
    shadowRadius = 12.dp,
    dragForwardEnabled = true,
    dragBackwardEnabled = true,
    tapForwardEnabled = true,
    tapBackwardEnabled = true,
    tapInteraction = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction(
        forward = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction.Config(
            target = androidx.compose.ui.geometry.Rect(0.25f, 0.0f, 1.0f, 1.0f),
        ),
        backward = com.novel.page.component.pagecurl.config.PageCurlConfig.TargetTapInteraction.Config(
            target = androidx.compose.ui.geometry.Rect(0.0f, 0.0f, 0.75f, 1.0f),
        ),
    ),
)

@OptIn(ExperimentalPageCurlApi::class)
@Composable
private fun SyncPageCurlState(
    pageCurlState: PageCurlState,
    virtualPageIndex: Int,
    onPageChange: (FlipDirection) -> Unit,
) {
    LaunchedEffect(virtualPageIndex) {
        if (virtualPageIndex != pageCurlState.current) {
            pageCurlState.snapTo(virtualPageIndex)
        }
    }

    LaunchedEffect(pageCurlState) {
        snapshotFlow { pageCurlState.current }
            .distinctUntilChanged()
            .filter { it != virtualPageIndex }
            .collect { currentPage ->
                val direction = if (currentPage > virtualPageIndex) {
                    FlipDirection.NEXT
                } else {
                    FlipDirection.PREVIOUS
                }
                onPageChange(direction)
            }
    }
}

@Composable
private fun LogPageCurlSettings(readerSettings: ReaderSettings) {
    LaunchedEffect(readerSettings) {
        TimberLogger.d("PageCurlFlipContainer", "PageCurlFlipContainer settings updated")
        TimberLogger.d(
            "PageCurlFlipContainer",
            "background color: ${String.format(Locale.US, "#%08X", readerSettings.backgroundColor.value.toInt())}",
        )
        TimberLogger.d(
            "PageCurlFlipContainer",
            "text color: ${String.format(Locale.US, "#%08X", readerSettings.textColor.value.toInt())}",
        )
        TimberLogger.d("PageCurlFlipContainer", "font size: ${readerSettings.fontSize}sp")
        TimberLogger.d("PageCurlFlipContainer", "page flip effect: ${readerSettings.pageFlipEffect}")
        TimberLogger.d(
            "PageCurlFlipContainer",
            "background luminance: ${readerSettings.backgroundColor.luminance()}",
        )
    }
}

@OptIn(ExperimentalPageCurlApi::class)
@Composable
private fun PageCurlViewport(
    virtualPages: List<VirtualPage>,
    uiState: ReaderState,
    loadedChapters: Map<String, PageData>,
    readerSettings: ReaderSettings,
    pageCurlState: PageCurlState,
    config: com.novel.page.component.pagecurl.config.PageCurlConfig,
    onLoadBookReviews: ((String) -> Unit)?,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            PageCurl(
                count = virtualPages.size,
                state = pageCurlState,
                config = config,
                modifier = Modifier.fillMaxSize(),
            ) { pageIdx ->
                PageCurlVirtualPageContent(
                    virtualPage = virtualPages.getOrNull(pageIdx),
                    uiState = uiState,
                    loadedChapters = loadedChapters,
                    readerSettings = readerSettings,
                    onLoadBookReviews = onLoadBookReviews,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
private fun BookDetailSwipeBackContent(
    uiState: ReaderState,
    loadedChapters: Map<String, PageData>,
    readerSettings: ReaderSettings,
    callbacks: PageCurlFlipContainerCallbacks,
) {
    SwipeBackContainer(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = readerSettings.backgroundColor,
        textColor = readerSettings.textColor,
        onSwipeComplete = callbacks.onSwipeBack,
        onLeftSwipeToReader = { callbacks.onPageChange(FlipDirection.NEXT) },
    ) {
        val bookInfo = uiState.currentPageData?.bookInfo
            ?: loadedChapters[uiState.currentChapter?.id]?.bookInfo

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
            onLoadBookReviews = callbacks.onLoadBookReviews,
            onClick = callbacks.onClick,
        )
    }
}

@Composable
private fun PageCurlVirtualPageContent(
    virtualPage: VirtualPage?,
    uiState: ReaderState,
    loadedChapters: Map<String, PageData>,
    readerSettings: ReaderSettings,
    onLoadBookReviews: ((String) -> Unit)?,
    onClick: () -> Unit,
) {
    if (virtualPage == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(readerSettings.backgroundColor),
        )
        return
    }

    PaperTexture(
        modifier = Modifier.fillMaxSize(),
        alpha = 0.04f,
        density = 1.2f,
        seed = virtualPage.hashCode().toLong() * 42L,
    ) {
        when (virtualPage) {
            is VirtualPage.BookDetailPage -> {
                val bookInfo = uiState.currentPageData?.bookInfo
                    ?: loadedChapters[uiState.currentChapter?.id]?.bookInfo

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
                    onClick = onClick,
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
                        onClick = onClick,
                    )
                }
            }

            is VirtualPage.ChapterSection -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(readerSettings.backgroundColor),
                )
            }
        }
    }
}

@Stable
data class PageCurlFlipContainerCallbacks(
    val onPageChange: (FlipDirection) -> Unit,
    val onSwipeBack: (() -> Unit)? = null,
    val onLoadBookReviews: ((String) -> Unit)? = null,
    val onClick: () -> Unit,
)
