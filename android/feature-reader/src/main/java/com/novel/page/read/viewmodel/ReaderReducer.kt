package com.novel.page.read.viewmodel

import com.novel.core.logging.CoreLogger
import com.novel.core.mvi.MviReducerWithEffect
import com.novel.core.mvi.ReduceResult
import kotlinx.collections.immutable.toImmutableList

class ReaderReducer : MviReducerWithEffect<ReaderIntent, ReaderState, ReaderEffect> {

    companion object {
        private const val TAG = "ReaderReducer"
    }

    override fun reduce(
        currentState: ReaderState,
        intent: ReaderIntent,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "处理Intent: ${intent::class.simpleName}")

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

    private fun handleInitReader(
        currentState: ReaderState,
        intent: ReaderIntent.InitReader,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "初始化阅读器: bookId=${intent.bookId}, chapterId=${intent.chapterId}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isLoading = true,
                error = null,
                bookId = intent.bookId,
            ),
        )
    }

    private fun handleRetry(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "重试初始化")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isLoading = true,
                error = null,
            ),
        )
    }

    private fun handlePageFlip(
        currentState: ReaderState,
        intent: ReaderIntent.PageFlip,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "处理翻页: ${intent.direction}")
        return ReduceResult(
            currentState.copy(version = currentState.version + 1),
            ReaderEffect.TriggerHapticFeedback(HapticFeedbackType.LIGHT),
        )
    }

    private fun handlePreviousChapter(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "上一章")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isSwitchingChapter = true,
            ),
        )
    }

    private fun handleNextChapter(currentState: ReaderState): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "下一章")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isSwitchingChapter = true,
            ),
        )
    }

    private fun handleSwitchToChapter(
        currentState: ReaderState,
        intent: ReaderIntent.SwitchToChapter,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "切换到章节: ${intent.chapterId}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isSwitchingChapter = true,
                isChapterListVisible = false,
            ),
        )
    }

    private fun handleSeekToProgress(
        currentState: ReaderState,
        intent: ReaderIntent.SeekToProgress,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "跳转到进度: ${(intent.progress * 100).toInt()}%")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isSwitchingChapter = true,
            ),
            ReaderEffect.ShowToast("跳转到${(intent.progress * 100).toInt()}%"),
        )
    }

    private fun handleUpdateSettings(
        currentState: ReaderState,
        intent: ReaderIntent.UpdateSettings,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "更新阅读器设置")
        val newState = currentState.copy(
            version = currentState.version + 1,
            readerSettings = intent.settings,
        )
        val effect = if (currentState.readerSettings.brightness != intent.settings.brightness) {
            ReaderEffect.SetBrightness(intent.settings.brightness)
        } else {
            null
        }
        return ReduceResult(newState, effect)
    }

    private fun handleUpdateContainerSize(
        currentState: ReaderState,
        intent: ReaderIntent.UpdateContainerSize,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "更新容器尺寸: ${intent.size}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                containerSize = intent.size,
                density = intent.density,
            ),
        )
    }

    private fun handleToggleMenu(
        currentState: ReaderState,
        intent: ReaderIntent.ToggleMenu,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "切换菜单显示: ${intent.show}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isMenuVisible = intent.show,
                isChapterListVisible = false,
                isSettingsPanelVisible = false,
            ),
        )
    }

    private fun handleShowChapterList(
        currentState: ReaderState,
        intent: ReaderIntent.ShowChapterList,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "显示章节列表: ${intent.show}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isChapterListVisible = intent.show,
                isSettingsPanelVisible = if (intent.show) false else currentState.isSettingsPanelVisible,
            ),
        )
    }

    private fun handleShowSettingsPanel(
        currentState: ReaderState,
        intent: ReaderIntent.ShowSettingsPanel,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "显示设置面板: ${intent.show}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isSettingsPanelVisible = intent.show,
                isChapterListVisible = if (intent.show) false else currentState.isChapterListVisible,
            ),
        )
    }

    private fun handleSaveProgress(
        currentState: ReaderState,
        intent: ReaderIntent.SaveProgress,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "保存阅读进度: force=${intent.force}")
        return ReduceResult(currentState.copy(version = currentState.version + 1))
    }

    private fun handlePreloadChapters(
        currentState: ReaderState,
        intent: ReaderIntent.PreloadChapters,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "预加载章节: ${intent.currentChapterId}")
        return ReduceResult(currentState.copy(version = currentState.version + 1))
    }

    private fun handleSaveToHistory(
        currentState: ReaderState,
        intent: ReaderIntent.SaveToHistory,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "保存到历史记录: bookId=${intent.bookId}, chapterId=${intent.chapterId}")
        return ReduceResult(currentState.copy(version = currentState.version + 1))
    }

    private fun handleShowProgressRestoredHint(
        currentState: ReaderState,
        intent: ReaderIntent.ShowProgressRestoredHint,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "显示进度恢复提示: ${intent.show}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                showProgressRestoredHint = intent.show,
            ),
        )
    }

    private fun handleLoadBookReviews(
        currentState: ReaderState,
        intent: ReaderIntent.LoadBookReviews,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "加载书籍评论: bookId=${intent.bookId}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isLoadingReviews = true,
                reviewsError = null,
            ),
        )
    }

    private fun handleBookReviewsLoadSuccess(
        currentState: ReaderState,
        intent: ReaderIntent.BookReviewsLoadSuccess,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "评论加载成功: ${intent.reviews.size}条评论")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                bookReviews = intent.reviews,
                isLoadingReviews = false,
                reviewsError = null,
            ),
        )
    }

    private fun handleBookReviewsLoadFailure(
        currentState: ReaderState,
        intent: ReaderIntent.BookReviewsLoadFailure,
    ): ReduceResult<ReaderState, ReaderEffect> {
        CoreLogger.d(TAG, "评论加载失败: ${intent.error}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                isLoadingReviews = false,
                reviewsError = intent.error,
            ),
        )
    }

    private fun handleUpdateScrollPosition(
        currentState: ReaderState,
        intent: ReaderIntent.UpdateScrollPosition,
    ): ReduceResult<ReaderState, ReaderEffect> {
        if (intent.pageIndex == currentState.currentPageIndex) {
            return ReduceResult(currentState)
        }
        CoreLogger.d(TAG, "滚动更新页面索引: ${currentState.currentPageIndex} -> ${intent.pageIndex}")
        return ReduceResult(
            currentState.copy(
                version = currentState.version + 1,
                currentPageIndex = intent.pageIndex,
            ),
        )
    }

    private fun handleUpdateSlideIndex(
        currentState: ReaderState,
        intent: ReaderIntent.UpdateSlideIndex,
    ): ReduceResult<ReaderState, ReaderEffect> {
        val virtualPages = currentState.virtualPages
        if (intent.index !in virtualPages.indices || intent.index == currentState.virtualPageIndex) {
            CoreLogger.d(TAG, "滑动翻页索引无效或未变化: ${intent.index}")
            return ReduceResult(currentState)
        }

        CoreLogger.d(TAG, "更新滑动翻页索引: ${currentState.virtualPageIndex} -> ${intent.index}")
        val newVirtualPage = virtualPages[intent.index]
        val inferredPageIndex = when (newVirtualPage) {
            is VirtualPage.ContentPage -> if (newVirtualPage.chapterId == currentState.currentChapter?.id) {
                newVirtualPage.pageIndex
            } else {
                currentState.currentPageIndex
            }

            is VirtualPage.BookDetailPage -> -1
            else -> currentState.currentPageIndex
        }

        var nextState = currentState.copy(
            version = currentState.version + 1,
            virtualPageIndex = intent.index,
            currentPageIndex = inferredPageIndex,
        )

        if (inferredPageIndex != currentState.currentPageIndex) {
            val pageCountCache = nextState.pageCountCache
            val currentChapter = nextState.currentChapter
            if (pageCountCache != null && currentChapter != null && pageCountCache.totalPages > 0) {
                val chapterRange = pageCountCache.chapterPageRanges.find { it.chapterId == currentChapter.id }
                if (chapterRange != null) {
                    val globalPage = if (inferredPageIndex == -1) {
                        chapterRange.startPage
                    } else {
                        chapterRange.startPage + inferredPageIndex
                    }
                    nextState = nextState.copy(
                        readingProgress = (globalPage.toFloat() / pageCountCache.totalPages.toFloat()).coerceIn(0f, 1f),
                    )
                }
            }
        }

        return ReduceResult(nextState)
    }

    fun handleInitReaderSuccess(
        currentState: ReaderState,
        chapterList: List<Chapter>,
        initialChapter: Chapter,
        initialChapterIndex: Int,
        initialPageData: PageData,
        initialPageIndex: Int,
        settings: ReaderSettings,
        pageCountCache: com.novel.page.read.repository.PageCountCacheData?,
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
            pageCountCache = pageCountCache,
        )
    }

    fun handleInitReaderFailure(currentState: ReaderState, error: String): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoading = false,
            error = error,
        )
    }

    fun handleChapterSwitchSuccess(
        currentState: ReaderState,
        newChapter: Chapter,
        newChapterIndex: Int,
        newPageData: PageData,
        newPageIndex: Int,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isSwitchingChapter = false,
            currentChapter = newChapter,
            currentChapterIndex = newChapterIndex,
            currentPageData = newPageData,
            currentPageIndex = newPageIndex,
            bookContent = newPageData.content,
        )
    }

    fun handleVirtualPagesUpdate(
        currentState: ReaderState,
        virtualPages: List<VirtualPage>,
        virtualPageIndex: Int,
        loadedChapterData: Map<String, PageData>,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            virtualPages = virtualPages.toImmutableList(),
            virtualPageIndex = virtualPageIndex,
            loadedChapterData = loadedChapterData,
        )
    }

    fun handlePageFlipUpdate(
        currentState: ReaderState,
        newPageIndex: Int,
        newVirtualPageIndex: Int,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            currentPageIndex = newPageIndex,
            virtualPageIndex = newVirtualPageIndex,
        )
    }

    fun handleReadingProgressUpdate(
        currentState: ReaderState,
        newProgress: Float,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            readingProgress = newProgress,
        )
    }

    fun handleBookReviewsLoadSuccess(
        currentState: ReaderState,
        reviews: List<BookReview>,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            bookReviews = reviews.toImmutableList(),
            isLoadingReviews = false,
            reviewsError = null,
        )
    }

    fun handleBookReviewsLoadFailure(
        currentState: ReaderState,
        error: String,
    ): ReaderState {
        return currentState.copy(
            version = currentState.version + 1,
            isLoadingReviews = false,
            reviewsError = error,
        )
    }
}
