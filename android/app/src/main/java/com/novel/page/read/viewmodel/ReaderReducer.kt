package com.novel.page.read.viewmodel

import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import com.novel.utils.TimberLogger
import kotlinx.collections.immutable.toImmutableList

/**
 * Reader模块的Reducer
 * 
 * 负责处理所有ReaderIntent并产生新的ReaderState
 * 支持副作用处理，如导航、Toast等
 */
class ReaderReducer : MviReducerWithEffect<ReaderIntent, ReaderState, ReaderEffect> {
    
    companion object {
        private const val TAG = "ReaderReducer"
    }
    
    override fun reduce(currentState: ReaderState, intent: ReaderIntent): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")
        
        return when (intent) {
            is ReaderIntent.InitReader -> handleInitReader(currentState, intent)
            is ReaderIntent.Retry -> handleRetry(currentState)
            is ReaderIntent.PageFlip -> handlePageFlip(currentState, intent)
            is ReaderIntent.PreviousChapter -> handlePreviousChapter(currentState)
            is ReaderIntent.NextChapter -> handleNextChapter(currentState)
            is ReaderIntent.SwitchToChapter -> handleSwitchToChapter(currentState, intent)
            is ReaderIntent.SeekToProgress -> handleSeekToProgress(currentState, intent)
            is ReaderIntent.UpdateSettings -> handleUpdateSettings(currentState, intent)
            is ReaderIntent.UpdateContainerSize -> handleUpdateContainerSize(currentState, intent)
            is ReaderIntent.ToggleMenu -> handleToggleMenu(currentState, intent)
            is ReaderIntent.ShowChapterList -> handleShowChapterList(currentState, intent)
            is ReaderIntent.ShowSettingsPanel -> handleShowSettingsPanel(currentState, intent)
            is ReaderIntent.SaveProgress -> handleSaveProgress(currentState, intent)
            is ReaderIntent.PreloadChapters -> handlePreloadChapters(currentState, intent)
            is ReaderIntent.SaveToHistory -> handleSaveToHistory(currentState, intent)
            is ReaderIntent.UpdateScrollPosition -> handleUpdateScrollPosition(currentState, intent)
            is ReaderIntent.UpdateSlideIndex -> handleUpdateSlideIndex(currentState, intent)
            is ReaderIntent.ShowProgressRestoredHint -> handleShowProgressRestoredHint(currentState, intent)
            is ReaderIntent.LoadBookReviews -> handleLoadBookReviews(currentState, intent)
            is ReaderIntent.BookReviewsLoadSuccess -> handleBookReviewsLoadSuccess(currentState, intent)
            is ReaderIntent.BookReviewsLoadFailure -> handleBookReviewsLoadFailure(currentState, intent)
        }
    }
    
    private fun handleInitReader(currentState: ReaderState, intent: ReaderIntent.InitReader): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "初始化阅读器: bookId=${intent.bookId}, chapterId=${intent.chapterId}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoading = true,
            error = null,
            bookId = intent.bookId
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleRetry(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "重试初始化")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoading = true,
            error = null
        )
        
        return ReduceResult(newState)
    }
    
    private fun handlePageFlip(currentState: ReaderState, intent: ReaderIntent.PageFlip): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "处理翻页: ${intent.direction}")
        
        // 翻页逻辑将在UseCase中处理，这里只更新基础状态
        val newState = currentState.copy(
            version = currentState.version + 1
        )
        
        // 添加触觉反馈
        val effect = ReaderEffect.TriggerHapticFeedback(HapticFeedbackType.LIGHT)
        
        return ReduceResult(newState, effect)
    }
    
    private fun handlePreviousChapter(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "上一章")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = true
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleNextChapter(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "下一章")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = true
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleSwitchToChapter(currentState: ReaderState, intent: ReaderIntent.SwitchToChapter): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "切换到章节: ${intent.chapterId}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = true,
            isChapterListVisible = false // 关闭章节列表
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleSeekToProgress(currentState: ReaderState, intent: ReaderIntent.SeekToProgress): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "跳转到进度: ${(intent.progress * 100).toInt()}%")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = true
        )
        
        val effect = ReaderEffect.ShowToast("跳转到 ${(intent.progress * 100).toInt()}%")
        
        return ReduceResult(newState, effect)
    }
    
    private fun handleUpdateSettings(currentState: ReaderState, intent: ReaderIntent.UpdateSettings): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "更新阅读器设置")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            readerSettings = intent.settings
        )
        
        // 如果亮度发生变化，发送设置亮度的副作用
        val effect = if (currentState.readerSettings.brightness != intent.settings.brightness) {
            ReaderEffect.SetBrightness(intent.settings.brightness)
        } else null
        
        return ReduceResult(newState, effect)
    }
    
    private fun handleUpdateContainerSize(currentState: ReaderState, intent: ReaderIntent.UpdateContainerSize): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "更新容器尺寸: ${intent.size}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            containerSize = intent.size,
            density = intent.density
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleToggleMenu(currentState: ReaderState, intent: ReaderIntent.ToggleMenu): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "切换菜单显示: ${intent.show}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isMenuVisible = intent.show,
            // 显示菜单时关闭其他面板
            isChapterListVisible = false,
            isSettingsPanelVisible = false
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleShowChapterList(currentState: ReaderState, intent: ReaderIntent.ShowChapterList): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "显示章节列表: ${intent.show}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isChapterListVisible = intent.show,
            isSettingsPanelVisible = if (intent.show) false else currentState.isSettingsPanelVisible
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleShowSettingsPanel(currentState: ReaderState, intent: ReaderIntent.ShowSettingsPanel): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "显示设置面板: ${intent.show}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isSettingsPanelVisible = intent.show,
            isChapterListVisible = if (intent.show) false else currentState.isChapterListVisible
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleSaveProgress(currentState: ReaderState, intent: ReaderIntent.SaveProgress): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "保存阅读进度: force=${intent.force}")
        
        val newState = currentState.copy(
            version = currentState.version + 1
        )
        
        return ReduceResult(newState)
    }
    
    private fun handlePreloadChapters(currentState: ReaderState, intent: ReaderIntent.PreloadChapters): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "预加载章节: ${intent.currentChapterId}")
        
        val newState = currentState.copy(
            version = currentState.version + 1
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleSaveToHistory(currentState: ReaderState, intent: ReaderIntent.SaveToHistory): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "保存到历史记录: bookId=${intent.bookId}, chapterId=${intent.chapterId}")
        
        // 历史记录保存不需要改变状态，只是触发副作用
        val newState = currentState.copy(
            version = currentState.version + 1
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleShowProgressRestoredHint(currentState: ReaderState, intent: ReaderIntent.ShowProgressRestoredHint): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "显示进度恢复提示: ${intent.show}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            showProgressRestoredHint = intent.show
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleLoadBookReviews(currentState: ReaderState, intent: ReaderIntent.LoadBookReviews): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "加载书籍评论: bookId=${intent.bookId}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoadingReviews = true,
            reviewsError = null
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleBookReviewsLoadSuccess(currentState: ReaderState, intent: ReaderIntent.BookReviewsLoadSuccess): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "评论加载成功: ${intent.reviews.size}条评论")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            bookReviews = intent.reviews,
            isLoadingReviews = false,
            reviewsError = null
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleBookReviewsLoadFailure(currentState: ReaderState, intent: ReaderIntent.BookReviewsLoadFailure): ReduceResult<ReaderState, ReaderEffect> {
        TimberLogger.d(TAG, "评论加载失败: ${intent.error}")
        
        val newState = currentState.copy(
            version = currentState.version + 1,
            isLoadingReviews = false,
            reviewsError = intent.error
        )
        
        return ReduceResult(newState)
    }
    
    private fun handleUpdateScrollPosition(currentState: ReaderState, intent: ReaderIntent.UpdateScrollPosition): ReduceResult<ReaderState, ReaderEffect> {
        if (intent.pageIndex != currentState.currentPageIndex) {
            TimberLogger.d(TAG, "滚动更新页面索引: ${currentState.currentPageIndex} -> ${intent.pageIndex}")
            val newState = currentState.copy(
                version = currentState.version + 1,
                currentPageIndex = intent.pageIndex
            )
            return ReduceResult(newState)
        }
        return ReduceResult(currentState)
    }

    private fun handleUpdateSlideIndex(currentState: ReaderState, intent: ReaderIntent.UpdateSlideIndex): ReduceResult<ReaderState, ReaderEffect> {
        val virtualPages = currentState.virtualPages
        if (intent.index !in virtualPages.indices || intent.index == currentState.virtualPageIndex) {
            TimberLogger.d(TAG, "滑动翻页索引无效或未变化: ${intent.index}")
            return ReduceResult(currentState)
        }

        TimberLogger.d(TAG, "更新滑动翻页索引: ${currentState.virtualPageIndex} -> ${intent.index}")
        val newVirtualPage = virtualPages[intent.index]

        // 基于新的虚拟页面推断同步的 currentPageIndex，使得 UI 能实时刷新页码
        val inferredPageIndex = when (newVirtualPage) {
            is VirtualPage.ContentPage -> if (newVirtualPage.chapterId == currentState.currentChapter?.id) {
                newVirtualPage.pageIndex
            } else currentState.currentPageIndex // 不同章节的翻页将在异步流程中处理
            is VirtualPage.BookDetailPage -> -1
            else -> currentState.currentPageIndex
        }

        var tmpState = currentState.copy(
            version = currentState.version + 1,
            virtualPageIndex = intent.index,
            currentPageIndex = inferredPageIndex
        )

        // 尝试即时刷新阅读进度，避免等待异步逻辑
        if (inferredPageIndex != currentState.currentPageIndex) {
            val pageCountCache = tmpState.pageCountCache
            val currentChapter = tmpState.currentChapter
            if (pageCountCache != null && currentChapter != null && pageCountCache.totalPages > 0) {
                val chapterRange = pageCountCache.chapterPageRanges.find { it.chapterId == currentChapter.id }
                if (chapterRange != null) {
                    val globalPage = if (inferredPageIndex == -1) chapterRange.startPage else chapterRange.startPage + inferredPageIndex
                    val newProgress = globalPage.toFloat() / pageCountCache.totalPages.toFloat()
                    tmpState = tmpState.copy(readingProgress = newProgress.coerceIn(0f, 1f))
                }
            }
        }

        return ReduceResult(tmpState)
    }
    
    /**
     * 处理初始化成功的状态更新
     */
    fun handleInitReaderSuccess(
        currentState: ReaderState,
        chapterList: List<Chapter>,
        initialChapter: Chapter,
        initialChapterIndex: Int,
        initialPageData: PageData,
        initialPageIndex: Int,
        settings: ReaderSettings,
        pageCountCache: com.novel.page.read.repository.PageCountCacheData?
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = null,
            chapterList = chapterList.toImmutableList(),
            currentChapter = initialChapter,
            currentChapterIndex = initialChapterIndex,
            currentPageData = initialPageData,
            currentPageIndex = initialPageIndex,
            bookContent = initialPageData.content,
            readerSettings = settings,
            pageCountCache = pageCountCache
        )
    }
    
    /**
     * 处理初始化失败的状态更新
     */
    fun handleInitReaderFailure(currentState: ReaderState, error: String): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = error
        )
    }
    
    /**
     * 处理章节切换成功的状态更新
     */
    fun handleChapterSwitchSuccess(
        currentState: ReaderState,
        newChapter: Chapter,
        newChapterIndex: Int,
        newPageData: PageData,
        newPageIndex: Int
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = false,
            currentChapter = newChapter,
            currentChapterIndex = newChapterIndex,
            currentPageData = newPageData,
            currentPageIndex = newPageIndex,
            bookContent = newPageData.content
        )
    }
    
    /**
     * 处理虚拟页面更新
     */
    fun handleVirtualPagesUpdate(
        currentState: ReaderState,
        virtualPages: List<VirtualPage>,
        virtualPageIndex: Int,
        loadedChapterData: Map<String, PageData>
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            virtualPages = virtualPages.toImmutableList(),
            virtualPageIndex = virtualPageIndex,
            loadedChapterData = loadedChapterData
        )
    }
    
    /**
     * 处理翻页状态更新
     */
    fun handlePageFlipUpdate(
        currentState: ReaderState,
        newPageIndex: Int,
        newVirtualPageIndex: Int
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            currentPageIndex = newPageIndex,
            virtualPageIndex = newVirtualPageIndex
        )
    }
    
    /**
     * 处理阅读进度更新
     */
    fun handleReadingProgressUpdate(
        currentState: ReaderState,
        newProgress: Float
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            readingProgress = newProgress
        )
    }
    
    /**
     * 处理评论加载成功
     */
    fun handleBookReviewsLoadSuccess(
        currentState: ReaderState,
        reviews: List<BookReview>
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            bookReviews = reviews.toImmutableList(),
            isLoadingReviews = false,
            reviewsError = null
        )
    }
    
    /**
     * 处理评论加载失败
     */
    fun handleBookReviewsLoadFailure(
        currentState: ReaderState,
        error: String
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoadingReviews = false,
            reviewsError = error
        )
    }
}
