package com.novel.page.read.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

class ReaderStateAdapter(stateFlow: StateFlow<ReaderState>) : StateAdapter<ReaderState>(stateFlow) {

    @Composable
    fun isInitializingState(): State<Boolean> =
        createStableState { it.isLoading && it.bookId.isNotEmpty() && it.chapterList.isEmpty() }

    @Composable
    fun isInitializedState(): State<Boolean> =
        createStableState { !it.isLoading && it.chapterList.isNotEmpty() && it.currentChapter != null }

    @Composable
    fun isSwitchingChapterState(): State<Boolean> = createStableState { it.isSwitchingChapter }

    @Composable
    fun errorMessageState(): State<String> = createStableState { it.error ?: "" }

    @Composable
    fun currentChapterNameState(): State<String> =
        createStableState { it.currentChapter?.chapterName ?: "" }

    @Composable
    fun currentChapterNumberState(): State<Int> = createStableState { it.currentChapterIndex + 1 }

    @Composable
    fun totalChaptersState(): State<Int> = createStableState { it.chapterList.size }

    @Composable
    fun chapterProgressTextState(): State<String> =
        createStableState { "${it.currentChapterIndex + 1}/${it.chapterList.size}" }

    @Composable
    fun currentPageNumberState(): State<Int> =
        createStableState {
            when {
                it.currentPageIndex == -1 -> 0
                else -> it.currentPageIndex + 1
            }
        }

    @Composable
    fun currentChapterTotalPagesState(): State<Int> =
        createStableState { it.currentPageData?.pages?.size ?: 0 }

    @Composable
    fun pageProgressTextState(): State<String> =
        createStableState {
            when {
                it.currentPageIndex == -1 -> "详情页"
                (it.currentPageData?.pages?.size ?: 0) > 0 -> "${it.currentPageIndex + 1}/${it.currentPageData!!.pages.size}"
                else -> "0/0"
            }
        }

    @Composable
    fun readingProgressPercentState(): State<Int> =
        createStableState { (it.computedReadingProgress * 100).toInt() }

    @Composable
    fun readingProgressTextState(): State<String> =
        createStableState { "${(it.computedReadingProgress * 100).toInt()}%" }

    @Composable
    fun canFlipToPreviousPageState(): State<Boolean> =
        createStableState { it.virtualPages.isNotEmpty() && it.virtualPageIndex > 0 }

    @Composable
    fun canFlipToNextPageState(): State<Boolean> =
        createStableState { it.virtualPages.isNotEmpty() && it.virtualPageIndex < it.virtualPages.size - 1 }

    @Composable
    fun canFlipToPreviousChapterState(): State<Boolean> = createStableState { it.currentChapterIndex > 0 }

    @Composable
    fun canFlipToNextChapterState(): State<Boolean> =
        createStableState { it.currentChapterIndex < it.chapterList.size - 1 }

    @Composable
    fun isFirstPageState(): State<Boolean> =
        createStableState { it.currentPageIndex == 0 || it.currentPageIndex == -1 }

    @Composable
    fun isLastPageState(): State<Boolean> =
        createStableState {
            val pageData = it.currentPageData
            pageData != null && it.currentPageIndex >= pageData.pages.size - 1
        }

    @Composable
    fun isOnBookDetailPageState(): State<Boolean> = createStableState { it.currentPageIndex == -1 }

    @Composable
    fun pageFlipEffectState(): State<PageFlipEffect> =
        createStableState { it.readerSettings.pageFlipEffect }

    @Composable
    fun isVerticalScrollModeState(): State<Boolean> =
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.VERTICAL }

    @Composable
    fun isSlideFlipModeState(): State<Boolean> =
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.SLIDE }

    @Composable
    fun isCoverFlipModeState(): State<Boolean> =
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.COVER }

    @Composable
    fun isPageCurlModeState(): State<Boolean> =
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.PAGECURL }

    @Composable
    fun isNoAnimationModeState(): State<Boolean> =
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.NONE }

    @Composable
    fun isMenuVisibleState(): State<Boolean> = createStableState { it.isMenuVisible }

    @Composable
    fun isChapterListVisibleState(): State<Boolean> = createStableState { it.isChapterListVisible }

    @Composable
    fun isSettingsPanelVisibleState(): State<Boolean> = createStableState { it.isSettingsPanelVisible }

    @Composable
    fun hasAnyPanelVisibleState(): State<Boolean> =
        createStableState { it.isMenuVisible || it.isChapterListVisible || it.isSettingsPanelVisible }

    @Composable
    fun fontSizeState(): State<Int> = createStableState { it.readerSettings.fontSize }

    @Composable
    fun brightnessState(): State<Float> = createStableState { it.readerSettings.brightness }

    @Composable
    fun brightnessPercentState(): State<Int> =
        createStableState { (it.readerSettings.brightness * 100).toInt() }

    @Composable
    fun backgroundColorState(): State<androidx.compose.ui.graphics.Color> =
        createStableState { it.readerSettings.backgroundColor }

    @Composable
    fun textColorState(): State<androidx.compose.ui.graphics.Color> =
        createStableState { it.readerSettings.textColor }

    @Composable
    fun hasPageCountCacheState(): State<Boolean> = createStableState { it.pageCountCache != null }

    @Composable
    fun totalBookPagesState(): State<Int> = createStableState { it.pageCountCache?.totalPages ?: 0 }

    @Composable
    fun isCalculatingPaginationState(): State<Boolean> =
        createStableState { it.paginationState.isCalculating }

    @Composable
    fun paginationProgressState(): State<Float> =
        createStableState {
            if (it.paginationState.totalChapters > 0) {
                it.paginationState.calculatedChapters.toFloat() / it.paginationState.totalChapters.toFloat()
            } else {
                0f
            }
        }

    @Composable
    fun paginationProgressPercentState(): State<Int> =
        createStableState {
            val progress = if (it.paginationState.totalChapters > 0) {
                it.paginationState.calculatedChapters.toFloat() / it.paginationState.totalChapters.toFloat()
            } else {
                0f
            }
            (progress * 100).toInt()
        }

    @Composable
    fun loadedChapterCountState(): State<Int> = createStableState { it.loadedChapterData.size }

    @Composable
    fun hasPreloadedNextChapterState(): State<Boolean> = createStableState { it.nextChapterData != null }

    @Composable
    fun hasPreloadedPreviousChapterState(): State<Boolean> =
        createStableState { it.previousChapterData != null }

    @Composable
    fun showProgressRestoredHintState(): State<Boolean> =
        createStableState { it.showProgressRestoredHint }

    fun getChapterAt(index: Int): Chapter? = getCurrentSnapshot().chapterList.getOrNull(index)

    fun findChapterById(chapterId: String): Chapter? =
        getCurrentSnapshot().chapterList.find { it.id == chapterId }

    fun getChapterIndex(chapterId: String): Int =
        getCurrentSnapshot().chapterList.indexOfFirst { it.id == chapterId }

    fun getPreviousChapter(): Chapter? {
        val state = getCurrentSnapshot()
        return if (state.currentChapterIndex > 0) {
            state.chapterList.getOrNull(state.currentChapterIndex - 1)
        } else {
            null
        }
    }

    fun getNextChapter(): Chapter? {
        val state = getCurrentSnapshot()
        return if (state.currentChapterIndex < state.chapterList.size - 1) {
            state.chapterList.getOrNull(state.currentChapterIndex + 1)
        } else {
            null
        }
    }

    fun isCurrentChapter(chapterId: String): Boolean =
        getCurrentSnapshot().currentChapter?.id == chapterId

    fun isCurrentPage(pageIndex: Int): Boolean =
        getCurrentSnapshot().currentPageIndex == pageIndex

    fun getCurrentVirtualPage(): VirtualPage? {
        val state = getCurrentSnapshot()
        return state.virtualPages.getOrNull(state.virtualPageIndex)
    }

    fun isContainerInitialized(): Boolean {
        val state = getCurrentSnapshot()
        return state.containerSize.width > 0 && state.containerSize.height > 0 && state.density != null
    }

    fun formatReadingTime(durationMs: Long): String {
        val minutes = durationMs / 60000
        val hours = minutes / 60
        return when {
            hours > 0 -> "${hours}小时${minutes % 60}分钟"
            minutes > 0 -> "${minutes}分钟"
            else -> "不到1分钟"
        }
    }

    fun estimateRemainingReadingTime(avgReadingSpeedWpm: Int = 200): String {
        val state = getCurrentSnapshot()
        val remainingProgress = 1f - state.computedReadingProgress
        val pageCountCache = state.pageCountCache
        if (remainingProgress <= 0f || pageCountCache == null) return "未知"

        val totalWords = pageCountCache.totalPages * 300
        val remainingWords = (totalWords * remainingProgress).toInt()
        val remainingMinutes = remainingWords / avgReadingSpeedWpm
        return formatReadingTime(remainingMinutes * 60000L)
    }

    fun getDebugInfo(): String {
        val state = getCurrentSnapshot()
        return buildString {
            appendLine("=== Reader State Debug Info ===")
            appendLine("Version: ${state.version}")
            appendLine("Loading: ${state.isLoading}")
            appendLine("Error: ${state.error}")
            appendLine("BookId: ${state.bookId}")
            appendLine("Current Chapter: ${state.currentChapter?.chapterName} (${state.currentChapterIndex + 1}/${state.chapterList.size})")
            appendLine("Current Page: ${if (state.currentPageIndex == -1) "详情页" else "${state.currentPageIndex + 1}/${state.currentPageData?.pages?.size ?: 0}"}")
            appendLine("Reading Progress: ${(state.computedReadingProgress * 100).toInt()}%")
            appendLine("Flip Effect: ${state.readerSettings.pageFlipEffect}")
            appendLine("Virtual Pages: ${state.virtualPages.size}")
            appendLine("Virtual Page Index: ${state.virtualPageIndex}")
            appendLine("Loaded Chapters: ${state.loadedChapterData.size}")
            appendLine("Container Size: ${state.containerSize}")
            appendLine("Has Cache: ${state.pageCountCache != null}")
            val pageCountCache = state.pageCountCache
            if (pageCountCache != null) {
                appendLine("Total Book Pages: ${pageCountCache.totalPages}")
            }
            appendLine("UI States: Menu=${state.isMenuVisible}, ChapterList=${state.isChapterListVisible}, Settings=${state.isSettingsPanelVisible}")
        }
    }
}

fun StateFlow<ReaderState>.asReaderAdapter(): ReaderStateAdapter = ReaderStateAdapter(this)
