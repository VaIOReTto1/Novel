package com.novel.page.read.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.novel.core.adapter.StateAdapter
import kotlinx.coroutines.flow.StateFlow

/**
 * Reader状态适配器
 *
 * 提供ReaderState的便利方法和计算属性
 * 简化UI层对状态的访问和判断
 * 优化的@Composable状态访问方法，提升skippable比例
 */
class ReaderStateAdapter(stateFlow: StateFlow<ReaderState>) : StateAdapter<ReaderState>(stateFlow) {

    // region Composable 状态访问方法 (用于提升 skippable 比例)

    /**
     * 是否正在初始化 - 优化版本
     * 替代 isInitializing.collectAsState() 以提升性能
     */
    @Composable
    fun isInitializingState(): State<Boolean> = 
        createStableState { 
            it.isLoading && it.bookId.isNotEmpty() && it.chapterList.isEmpty()
        }

    /**
     * 是否初始化完成 - 优化版本
     */
    @Composable
    fun isInitializedState(): State<Boolean> = 
        createStableState {
            !it.isLoading && it.chapterList.isNotEmpty() && it.currentChapter != null
        }

    /**
     * 是否正在切换章节 - 优化版本
     */
    @Composable
    fun isSwitchingChapterState(): State<Boolean> = 
        createStableState { it.isSwitchingChapter }

    /**
     * 错误信息 - 优化版本
     */
    @Composable
    fun errorMessageState(): State<String> = 
        createStableState { it.error ?: "" }

    /**
     * 当前章节名称 - 优化版本
     */
    @Composable
    fun currentChapterNameState(): State<String> = 
        createStableState { it.currentChapter?.chapterName ?: "" }

    /**
     * 当前章节索引（从1开始） - 优化版本
     */
    @Composable
    fun currentChapterNumberState(): State<Int> = 
        createStableState { it.currentChapterIndex + 1 }

    /**
     * 总章节数 - 优化版本
     */
    @Composable
    fun totalChaptersState(): State<Int> = 
        createStableState { it.chapterList.size }

    /**
     * 章节进度文本 - 优化版本
     */
    @Composable
    fun chapterProgressTextState(): State<String> = 
        createStableState { 
            "${it.currentChapterIndex + 1}/${it.chapterList.size}"
        }

    /**
     * 当前页码（从1开始） - 优化版本
     */
    @Composable
    fun currentPageNumberState(): State<Int> = 
        createStableState {
            when {
                it.currentPageIndex == -1 -> 0 // 书籍详情页
                else -> it.currentPageIndex + 1
            }
        }

    /**
     * 当前章节总页数 - 优化版本
     */
    @Composable
    fun currentChapterTotalPagesState(): State<Int> = 
        createStableState { it.currentPageData?.pages?.size ?: 0 }

    /**
     * 页面进度文本 - 优化版本
     */
    @Composable
    fun pageProgressTextState(): State<String> = 
        createStableState { 
            when {
                it.currentPageIndex == -1 -> "详情页"
                (it.currentPageData?.pages?.size ?: 0) > 0 -> "${it.currentPageIndex + 1}/${it.currentPageData!!.pages.size}"
                else -> "0/0"
            }
        }

    /**
     * 阅读进度百分比（0-100） - 优化版本
     */
    @Composable
    fun readingProgressPercentState(): State<Int> = 
        createStableState { (it.computedReadingProgress * 100).toInt() }

    /**
     * 阅读进度文本 - 优化版本
     */
    @Composable
    fun readingProgressTextState(): State<String> = 
        createStableState { "${(it.computedReadingProgress * 100).toInt()}%" }

    /**
     * 是否可以翻到上一页 - 优化版本
     */
    @Composable
    fun canFlipToPreviousPageState(): State<Boolean> = 
        createStableState {
            it.virtualPages.isNotEmpty() && it.virtualPageIndex > 0
        }

    /**
     * 是否可以翻到下一页 - 优化版本
     */
    @Composable
    fun canFlipToNextPageState(): State<Boolean> = 
        createStableState {
            it.virtualPages.isNotEmpty() && it.virtualPageIndex < it.virtualPages.size - 1
        }

    /**
     * 是否可以翻到上一章 - 优化版本
     */
    @Composable
    fun canFlipToPreviousChapterState(): State<Boolean> = 
        createStableState { it.currentChapterIndex > 0 }

    /**
     * 是否可以翻到下一章 - 优化版本
     */
    @Composable
    fun canFlipToNextChapterState(): State<Boolean> = 
        createStableState { 
            it.currentChapterIndex < it.chapterList.size - 1
        }

    /**
     * 是否在第一页 - 优化版本
     */
    @Composable
    fun isFirstPageState(): State<Boolean> = 
        createStableState { 
            it.currentPageIndex == 0 || it.currentPageIndex == -1
        }

    /**
     * 是否在最后一页 - 优化版本
     */
    @Composable
    fun isLastPageState(): State<Boolean> = 
        createStableState {
            val pageData = it.currentPageData
            pageData != null && it.currentPageIndex >= pageData.pages.size - 1
        }

    /**
     * 是否在书籍详情页 - 优化版本
     */
    @Composable
    fun isOnBookDetailPageState(): State<Boolean> = 
        createStableState { it.currentPageIndex == -1 }

    /**
     * 当前翻页效果 - 优化版本
     */
    @Composable
    fun pageFlipEffectState(): State<PageFlipEffect> = 
        createStableState { it.readerSettings.pageFlipEffect }

    /**
     * 是否为纵向滚动模式 - 优化版本
     */
    @Composable
    fun isVerticalScrollModeState(): State<Boolean> = 
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.VERTICAL }

    /**
     * 是否为平移翻页模式 - 优化版本
     */
    @Composable
    fun isSlideFlipModeState(): State<Boolean> = 
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.SLIDE }

    /**
     * 是否为覆盖翻页模式 - 优化版本
     */
    @Composable
    fun isCoverFlipModeState(): State<Boolean> = 
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.COVER }

    /**
     * 是否为卷曲翻页模式 - 优化版本
     */
    @Composable
    fun isPageCurlModeState(): State<Boolean> = 
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.PAGECURL }

    /**
     * 是否为无动画翻页模式 - 优化版本
     */
    @Composable
    fun isNoAnimationModeState(): State<Boolean> = 
        createStableState { it.readerSettings.pageFlipEffect == PageFlipEffect.NONE }

    /**
     * 是否显示菜单 - 优化版本
     */
    @Composable
    fun isMenuVisibleState(): State<Boolean> = 
        createStableState { it.isMenuVisible }

    /**
     * 是否显示章节列表 - 优化版本
     */
    @Composable
    fun isChapterListVisibleState(): State<Boolean> = 
        createStableState { it.isChapterListVisible }

    /**
     * 是否显示设置面板 - 优化版本
     */
    @Composable
    fun isSettingsPanelVisibleState(): State<Boolean> = 
        createStableState { it.isSettingsPanelVisible }

    /**
     * 是否有任何面板显示 - 优化版本
     */
    @Composable
    fun hasAnyPanelVisibleState(): State<Boolean> = 
        createStableState {
            it.isMenuVisible || it.isChapterListVisible || it.isSettingsPanelVisible
        }

    /**
     * 字体大小 - 优化版本
     */
    @Composable
    fun fontSizeState(): State<Int> = 
        createStableState { it.readerSettings.fontSize }

    /**
     * 亮度值 - 优化版本
     */
    @Composable
    fun brightnessState(): State<Float> = 
        createStableState { it.readerSettings.brightness }

    /**
     * 亮度百分比 - 优化版本
     */
    @Composable
    fun brightnessPercentState(): State<Int> = 
        createStableState { (it.readerSettings.brightness * 100).toInt() }

    /**
     * 背景颜色 - 优化版本
     */
    @Composable
    fun backgroundColorState(): State<androidx.compose.ui.graphics.Color> = 
        createStableState { it.readerSettings.backgroundColor }

    /**
     * 文字颜色 - 优化版本
     */
    @Composable
    fun textColorState(): State<androidx.compose.ui.graphics.Color> = 
        createStableState { it.readerSettings.textColor }

    /**
     * 是否有页数缓存 - 优化版本
     */
    @Composable
    fun hasPageCountCacheState(): State<Boolean> = 
        createStableState { it.pageCountCache != null }

    /**
     * 全书总页数 - 优化版本
     */
    @Composable
    fun totalBookPagesState(): State<Int> = 
        createStableState { it.pageCountCache?.totalPages ?: 0 }

    /**
     * 是否正在计算分页 - 优化版本
     */
    @Composable
    fun isCalculatingPaginationState(): State<Boolean> = 
        createStableState { it.paginationState.isCalculating }

    /**
     * 分页计算进度 - 优化版本
     */
    @Composable
    fun paginationProgressState(): State<Float> = 
        createStableState {
            if (it.paginationState.totalChapters > 0) {
                it.paginationState.calculatedChapters.toFloat() / it.paginationState.totalChapters.toFloat()
            } else 0f
        }

    /**
     * 分页计算进度百分比 - 优化版本
     */
    @Composable
    fun paginationProgressPercentState(): State<Int> = 
        createStableState { 
            val progress = if (it.paginationState.totalChapters > 0) {
                it.paginationState.calculatedChapters.toFloat() / it.paginationState.totalChapters.toFloat()
            } else 0f
            (progress * 100).toInt()
        }

    /**
     * 已加载的章节数量 - 优化版本
     */
    @Composable
    fun loadedChapterCountState(): State<Int> = 
        createStableState { it.loadedChapterData.size }

    /**
     * 是否有预加载的下一章 - 优化版本
     */
    @Composable
    fun hasPreloadedNextChapterState(): State<Boolean> = 
        createStableState { it.nextChapterData != null }

    /**
     * 是否有预加载的上一章 - 优化版本
     */
    @Composable
    fun hasPreloadedPreviousChapterState(): State<Boolean> = 
        createStableState { it.previousChapterData != null }

    /**
     * 显示进度恢复提示 - 优化版本
     */
    @Composable
    fun showProgressRestoredHintState(): State<Boolean> = 
        createStableState { it.showProgressRestoredHint }

    // endregion

    // ======================= 便利方法 =======================

    /**
     * 获取指定索引的章节
     */
    fun getChapterAt(index: Int): Chapter? {
        return getCurrentSnapshot().chapterList.getOrNull(index)
    }

    /**
     * 根据ID查找章节
     */
    fun findChapterById(chapterId: String): Chapter? {
        return getCurrentSnapshot().chapterList.find { it.id == chapterId }
    }

    /**
     * 获取章节在列表中的索引
     */
    fun getChapterIndex(chapterId: String): Int {
        return getCurrentSnapshot().chapterList.indexOfFirst { it.id == chapterId }
    }

    /**
     * 获取上一章
     */
    fun getPreviousChapter(): Chapter? {
        val state = getCurrentSnapshot()
        return if (state.currentChapterIndex > 0) {
            state.chapterList.getOrNull(state.currentChapterIndex - 1)
        } else null
    }

    /**
     * 获取下一章
     */
    fun getNextChapter(): Chapter? {
        val state = getCurrentSnapshot()
        return if (state.currentChapterIndex < state.chapterList.size - 1) {
            state.chapterList.getOrNull(state.currentChapterIndex + 1)
        } else null
    }

    /**
     * 检查是否为指定章节
     */
    fun isCurrentChapter(chapterId: String): Boolean {
        return getCurrentSnapshot().currentChapter?.id == chapterId
    }

    /**
     * 检查是否为指定页面
     */
    fun isCurrentPage(pageIndex: Int): Boolean {
        return getCurrentSnapshot().currentPageIndex == pageIndex
    }

    /**
     * 获取当前虚拟页面
     */
    fun getCurrentVirtualPage(): VirtualPage? {
        val state = getCurrentSnapshot()
        return state.virtualPages.getOrNull(state.virtualPageIndex)
    }

    /**
     * 检查容器是否已初始化
     */
    fun isContainerInitialized(): Boolean {
        val state = getCurrentSnapshot()
        return state.containerSize.width > 0 && state.containerSize.height > 0 && state.density != null
    }

    /**
     * 格式化阅读时长
     */
    fun formatReadingTime(durationMs: Long): String {
        val minutes = durationMs / 60000
        val hours = minutes / 60
        return when {
            hours > 0 -> "${hours}小时${minutes % 60}分钟"
            minutes > 0 -> "${minutes}分钟"
            else -> "不到1分钟"
        }
    }

    /**
     * 计算预计剩余阅读时间
     */
    fun estimateRemainingReadingTime(avgReadingSpeedWpm: Int = 200): String {
        val state = getCurrentSnapshot()
        val remainingProgress = 1f - state.computedReadingProgress
        if (remainingProgress <= 0f || state.pageCountCache == null) return "未知"

        val totalWords = (state.pageCountCache?.totalPages ?: 0) * 300 // 假设每页300字
        val remainingWords = (totalWords * remainingProgress).toInt()
        val remainingMinutes = remainingWords / avgReadingSpeedWpm

        return formatReadingTime(remainingMinutes * 60000L)
    }

    // ======================= 调试信息 =======================

    /**
     * 获取状态调试信息
     */
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
            if (state.pageCountCache != null) {
                appendLine("Total Book Pages: ${state.pageCountCache.totalPages}")
            }
            appendLine("UI States: Menu=${getCurrentSnapshot().isMenuVisible}, ChapterList=${getCurrentSnapshot().isChapterListVisible}, Settings=${getCurrentSnapshot().isSettingsPanelVisible}")
        }
    }
}

/**
 * StateAdapter工厂方法
 * 为StateFlow提供便捷的适配器创建
 */
fun StateFlow<ReaderState>.asReaderAdapter(): ReaderStateAdapter {
    return ReaderStateAdapter(this)
}