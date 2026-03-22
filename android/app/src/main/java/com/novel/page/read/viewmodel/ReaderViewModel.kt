package com.novel.page.read.viewmodel

import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewModelScope
import com.novel.core.mvi.BaseMviViewModel
import com.novel.core.mvi.MviReducer
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.SettingsService
import com.novel.page.read.usecase.BuildVirtualPagesUseCase
import com.novel.page.read.usecase.FlipPageUseCase
import com.novel.page.read.usecase.InitReaderUseCase
import com.novel.page.read.usecase.LoadBookReviewsUseCase
import com.novel.page.read.usecase.ObservePaginationProgressUseCase
import com.novel.page.read.usecase.PreloadChaptersUseCase
import com.novel.page.read.usecase.SaveProgressUseCase
import com.novel.page.read.usecase.SeekProgressUseCase
import com.novel.page.read.usecase.SplitContentUseCase
import com.novel.page.read.usecase.SwitchChapterUseCase
import com.novel.page.read.usecase.UpdateSettingsUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.utils.TimberLogger
import com.novel.page.read.service.HistoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 基于统一MVI框架的ReaderViewModel
 *
 * 继承BaseMviViewModel，使用ReaderIntent、ReaderState、ReaderEffect
 * 保持所有原有功能，但使用统一的MVI架构
 */
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val initReaderUseCase: InitReaderUseCase,
    private val flipPageUseCase: FlipPageUseCase,
    private val switchChapterUseCase: SwitchChapterUseCase,
    private val seekProgressUseCase: SeekProgressUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val preloadChaptersUseCase: PreloadChaptersUseCase,
    private val buildVirtualPagesUseCase: BuildVirtualPagesUseCase,
    private val splitContentUseCase: SplitContentUseCase,
    private val loadBookReviewsUseCase: LoadBookReviewsUseCase,
    private val paginationService: PaginationService,
    private val settingsService: SettingsService,
    private val historyService: HistoryService,
    observePaginationProgressUseCase: ObservePaginationProgressUseCase,
) : BaseMviViewModel<ReaderIntent, ReaderState, ReaderEffect>() {

    companion object {
        private const val TAG = ReaderLogTags.READER_VIEW_MODEL
        private const val FLIP_COOLDOWN_MS = 300L // 翻页防抖时间
    }

    // 翻页防抖控制
    private var lastFlipTime = 0L

    val readerReducer = ReaderReducer()
    private val readerSettingsCoordinator = ReaderSettingsCoordinator()
    private val readerHistoryCoordinator = ReaderHistoryCoordinator()
    private val readerPerformanceTraceCoordinator = ReaderPerformanceTraceCoordinator()

    /** 新的StateAdapter实例 */
    val adapter = state.asReaderAdapter()

    init {
        TimberLogger.d(TAG, "ReaderViewModel初始化")

        // 优先加载设置以确保UI（如背景色）能立即响应
        viewModelScope.launch {
            TimberLogger.d(TAG, "加载初始设置")
            sendIntent(
                readerSettingsCoordinator.createInitialSettingsIntent(
                    loadSettings = settingsService::loadSettings,
                    defaultSettings = ReaderSettings::getDefault,
                )
            )
        }

        // 观察分页进度
        observePaginationProgressUseCase.execute()
            .onEach { paginationState ->
                TimberLogger.d(
                    TAG,
                    "分页进度更新: 章节 ${paginationState.calculatedChapters}/${paginationState.totalChapters}, 估算总页=${paginationState.estimatedTotalPages}"
                )

                val currentState = getCurrentState()
                val newState = currentState.copy(
                    version = currentState.version + 1,
                    paginationState = paginationState
                )
                updateState(newState)

                // 当渐进计算完成且尚未写入页数缓存时，主动查询并更新
                if (!paginationState.isCalculating && paginationState.estimatedTotalPages > 0) {
                    val current = getCurrentState()
                    if (current.pageCountCache == null &&
                        current.containerSize != IntSize.Zero
                    ) {
                        viewModelScope.launch {
                            var cacheFound = false
                            for (attempt in 0 until 3) {
                                val cache = paginationService.getPageCountCache(
                                    current.bookId,
                                    current.readerSettings.fontSize,
                                    current.containerSize
                                )
                                if (cache != null) {
                                    val updatedState = getCurrentState().copy(
                                        version = getCurrentState().version + 1,
                                        pageCountCache = cache
                                    )
                                    updateState(updatedState)
                                    TimberLogger.d(
                                        TAG,
                                        "分页完成后写入页数缓存: 总页数=${cache.totalPages}"
                                    )
                                    cacheFound = true
                                    break
                                } else {
                                    TimberLogger.d(
                                        TAG,
                                        "页数缓存暂未生成，等待重试(${attempt + 1}/3)"
                                    )
                                    delay(500)
                                }
                            }
                            if (!cacheFound) {
                                TimberLogger.w(TAG, "多次尝试仍未获取到页数缓存，放弃更新")
                            }
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    override fun createInitialState(): ReaderState {
        return ReaderState()
    }

    override fun getReducer(): MviReducer<ReaderIntent, ReaderState> {
        val effectReducer = ReaderReducer()
        return object : MviReducer<ReaderIntent, ReaderState> {
            override fun reduce(currentState: ReaderState, intent: ReaderIntent): ReaderState {
                val result = effectReducer.reduce(currentState, intent)
                // 在这里处理副作用
                result.effect?.let { effect ->
                    sendEffect(effect)
                }
                return result.newState
            }
        }
    }

    override fun onIntentProcessed(intent: ReaderIntent, newState: ReaderState) {
        super.onIntentProcessed(intent, newState)

        // 处理需要异步操作的Intent
        when (intent) {
            is ReaderIntent.InitReader -> handleInitReaderAsync(intent)
            is ReaderIntent.PageFlip -> handlePageFlipAsync(intent)
            is ReaderIntent.PreviousChapter -> handleChapterChangeAsync(FlipDirection.PREVIOUS)
            is ReaderIntent.NextChapter -> handleChapterChangeAsync(FlipDirection.NEXT)
            is ReaderIntent.SwitchToChapter -> handleSwitchToChapterAsync(intent)
            is ReaderIntent.SeekToProgress -> handleSeekToProgressAsync(intent)
            is ReaderIntent.UpdateSettings -> handleUpdateSettingsAsync(intent)
            is ReaderIntent.UpdateContainerSize -> handleUpdateContainerSizeAsync(intent)
            is ReaderIntent.SaveProgress -> handleSaveProgressAsync(intent)
            is ReaderIntent.PreloadChapters -> handlePreloadChaptersAsync(intent)
            is ReaderIntent.SaveToHistory -> handleSaveToHistoryAsync(intent)
            is ReaderIntent.Retry -> handleRetryAsync()
            is ReaderIntent.UpdateScrollPosition -> handleUpdateScrollPositionAsync(intent)
            is ReaderIntent.UpdateSlideIndex -> handleUpdateSlideIndexAsync(intent)
            is ReaderIntent.LoadBookReviews -> handleLoadBookReviewsAsync(intent)
            else -> {
                // 其他Intent不需要异步处理
            }
        }
    }

    // ======================= 异步处理方法 =======================

    private fun handleInitReaderAsync(intent: ReaderIntent.InitReader) {
        viewModelScope.launch {
            val trace = startPerformanceTrace(
                action = "init",
                metadata = mapOf(
                    "bookId" to intent.bookId,
                    "entry" to (intent.chapterId ?: "restored"),
                ),
            )
            TimberLogger.d(
                TAG,
                "异步初始化阅读器: bookId=${intent.bookId}, chapterId=${intent.chapterId}"
            )

            initReaderUseCase.execute(
                bookId = intent.bookId,
                chapterId = intent.chapterId,
                initialState = getCurrentState(),
                scope = viewModelScope
            ).onSuccess { result ->
                TimberLogger.d(
                    TAG,
                    "阅读器初始化成功: 章节=${result.initialChapter.chapterName}, 页面=${result.initialPageIndex}"
                )

                val newState = readerReducer.handleInitReaderSuccess(
                    getCurrentState(),
                    result.chapterList,
                    result.initialChapter,
                    result.initialChapterIndex,
                    result.initialPageData,
                    result.initialPageIndex,
                    result.settings,
                    result.pageCountCache
                )
                updateState(newState)

                // 初始化后构建虚拟页面
                buildVirtualPages(preserveCurrentIndex = false)

                // 如果由于容器信息缺失导致初始分页退化为单页，且此时容器尺寸 & density 已就绪，则立即重新分页
                val currentData = getCurrentState().currentPageData
                if (currentData != null && currentData.pages.size == 1 &&
                    getCurrentState().containerSize != IntSize.Zero &&
                    getCurrentState().density != null
                ) {
                    TimberLogger.d(TAG, "检测到初始分页为单页，容器信息已就绪，触发重新分页")
                    splitContentAndBuildVirtualPages(preserveVirtualIndex = false)
                }
                finishPerformanceTrace(
                    trace = trace,
                    status = "success",
                    metadata = mapOf(
                        "chapterId" to result.initialChapter.id,
                        "pageCount" to result.initialPageData.pages.size.toString(),
                    ),
                )
            }.onFailure { error ->
                TimberLogger.e(TAG, "阅读器初始化失败", error)
                val newState = readerReducer.handleInitReaderFailure(
                    getCurrentState(),
                    error.message ?: "初始化失败"
                )
                updateState(newState)
                finishPerformanceTrace(
                    trace = trace,
                    status = "failure",
                    metadata = mapOf(
                        "error" to (error.message ?: "unknown"),
                    ),
                )
                sendEffect(ReaderEffect.ShowErrorDialog("初始化失败", error.message ?: "未知错误"))
            }
        }
    }

    private fun handlePageFlipAsync(intent: ReaderIntent.PageFlip) {
        viewModelScope.launch {
            // 防抖处理
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastFlipTime < FLIP_COOLDOWN_MS) {
                TimberLogger.d(TAG, "翻页防抖: 忽略重复操作")
                return@launch
            }
            lastFlipTime = currentTime
            val trace = startPerformanceTrace(
                action = "flip",
                metadata = mapOf(
                    "source" to "page_flip_intent",
                    "direction" to intent.direction.name,
                    "mode" to getCurrentState().readerSettings.pageFlipEffect.name,
                ),
            )

            TimberLogger.d(TAG, "异步处理翻页操作: ${intent.direction}")

            val result = flipPageUseCase.execute(
                getCurrentState(),
                intent.direction,
                viewModelScope
            )

            when (result) {
                is FlipPageUseCase.FlipResult.VirtualPageChanged -> {
                    TimberLogger.d(
                        TAG,
                        "虚拟页面翻页: 索引=${result.newVirtualPageIndex}, 页面类型=${result.newVirtualPage::class.simpleName}"
                    )

                    // 检查是否为平移模式
                    val isSlideMode =
                        getCurrentState().readerSettings.pageFlipEffect == PageFlipEffect.SLIDE

                    if (isSlideMode) {
                        updateSlideFlipState(result.newVirtualPage, result.newVirtualPageIndex)
                    } else {
                        val updatedState = readerReducer.handlePageFlipUpdate(
                            getCurrentState(),
                            if (result.newVirtualPage is VirtualPage.ContentPage) result.newVirtualPage.pageIndex else getCurrentState().currentPageIndex,
                            result.newVirtualPageIndex
                        )
                        updateState(updatedState)
                        updatePageFlipState(result.newVirtualPage)
                    }

                    // 如果需要预加载检查，执行预加载检查
                    if (result.needsPreloadCheck && result.newVirtualPage is VirtualPage.ContentPage) {
                        flipPageUseCase.executePreloadCheck(
                            getCurrentState(),
                            result.newVirtualPage,
                            viewModelScope
                        )
                    }
                    finishPerformanceTrace(
                        trace = trace,
                        status = "success",
                        metadata = mapOf(
                            "outcome" to "virtual_page_changed",
                            "virtualIndex" to result.newVirtualPageIndex.toString(),
                        ),
                    )
                }

                is FlipPageUseCase.FlipResult.ChapterChanged -> {
                    TimberLogger.d(TAG, "翻页触发章节切换")
                    handleChapterSwitchResult(result.switchResult)
                    finishPerformanceTrace(
                        trace = trace,
                        status = switchResultStatus(result.switchResult),
                        metadata = mapOf("outcome" to "chapter_changed"),
                    )
                }

                is FlipPageUseCase.FlipResult.NeedsVirtualPageRebuild -> {
                    TimberLogger.d(TAG, "检测到新的相邻章节已加载，处理翻页并重建虚拟页面")

                    val isSlideMode =
                        getCurrentState().readerSettings.pageFlipEffect == PageFlipEffect.SLIDE
                    if (isSlideMode) {
                        updateSlideFlipState(result.newVirtualPage, result.newVirtualPageIndex)
                    } else {
                        val updatedState = readerReducer.handlePageFlipUpdate(
                            getCurrentState(),
                            if (result.newVirtualPage is VirtualPage.ContentPage) result.newVirtualPage.pageIndex else getCurrentState().currentPageIndex,
                            result.newVirtualPageIndex
                        )
                        updateState(updatedState)
                        updatePageFlipState(result.newVirtualPage)
                    }

                    buildVirtualPages(preserveCurrentIndex = true)

                    if (result.needsPreloadCheck && result.newVirtualPage is VirtualPage.ContentPage) {
                        flipPageUseCase.executePreloadCheck(
                            getCurrentState(),
                            result.newVirtualPage,
                            viewModelScope
                        )
                    }
                    finishPerformanceTrace(
                        trace = trace,
                        status = "success",
                        metadata = mapOf(
                            "outcome" to "virtual_page_rebuild",
                            "virtualIndex" to result.newVirtualPageIndex.toString(),
                        ),
                    )
                }

                is FlipPageUseCase.FlipResult.Failure -> {
                    TimberLogger.e(TAG, "翻页失败", result.error)
                    finishPerformanceTrace(
                        trace = trace,
                        status = "failure",
                        metadata = mapOf(
                            "outcome" to "failure",
                            "error" to (result.error.message ?: "unknown"),
                        ),
                    )
                    sendEffect(ReaderEffect.ShowToast("翻页失败: ${result.error.message}"))
                }

                is FlipPageUseCase.FlipResult.NoOp -> {
                    TimberLogger.d(TAG, "翻页无操作")
                    finishPerformanceTrace(
                        trace = trace,
                        status = "noop",
                        metadata = mapOf("outcome" to "noop"),
                    )
                }
            }
        }
    }

    private fun handleChapterChangeAsync(direction: FlipDirection) {
        viewModelScope.launch {
            sendIntent(ReaderIntent.PageFlip(direction))
        }
    }

    private fun handleSwitchToChapterAsync(intent: ReaderIntent.SwitchToChapter) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步切换到指定章节: ${intent.chapterId}")

            val result = switchChapterUseCase.execute(
                getCurrentState(),
                intent.chapterId,
                viewModelScope
            )
            handleChapterSwitchResult(result)
        }
    }

    private fun handleSeekToProgressAsync(intent: ReaderIntent.SeekToProgress) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步跳转到进度: ${(intent.progress * 100).toInt()}%")

            val result = seekProgressUseCase.execute(intent.progress, getCurrentState())
            when (result) {
                is SeekProgressUseCase.SeekResult.Success -> {
                    TimberLogger.d(TAG, "进度跳转成功")
                    val newState = readerReducer.handleChapterSwitchSuccess(
                        getCurrentState(),
                        getCurrentState().chapterList[result.newChapterIndex],
                        result.newChapterIndex,
                        result.newPageData,
                        result.newPageIndex
                    )
                    updateState(newState)
                    buildVirtualPages(preserveCurrentIndex = false)

                    // 保存新的阅读进度
                    saveProgressUseCase.execute(getCurrentState())

                    // 触发动态预加载（顺序执行，保证完成后再重建虚拟页）
                    checkAndPreload(getCurrentState().virtualPageIndex)

                    // 预加载结束后检查并重建虚拟页面，保证Slide模式可以进入相邻章节
                    checkAndRebuildVirtualPagesIfNeeded(result.newChapterIndex)

                    // 通过Intent将预加载操作告知Reducer，保持事件流一致
                    sendIntent(ReaderIntent.PreloadChapters(result.newPageData.chapterId))
                }

                is SeekProgressUseCase.SeekResult.Failure -> {
                    TimberLogger.e(TAG, "进度跳转失败", result.error)
                    sendEffect(ReaderEffect.ShowToast("跳转失败: ${result.error.message}"))
                }

                is SeekProgressUseCase.SeekResult.NoOp -> {
                    TimberLogger.d(TAG, "进度跳转无操作")
                }
            }
        }
    }

    private fun handleUpdateSettingsAsync(intent: ReaderIntent.UpdateSettings) {
        viewModelScope.launch {
            val trace = startPerformanceTrace(
                action = "settings_update",
                metadata = mapOf(
                    "mode" to intent.settings.pageFlipEffect.name,
                    "fontSize" to intent.settings.fontSize.toString(),
                ),
            )
            TimberLogger.d(TAG, "异步更新阅读器设置")

            val result = updateSettingsUseCase.execute(intent.settings, getCurrentState())
            when (result) {
                is UpdateSettingsUseCase.UpdateResult.Success -> {
                    TimberLogger.d(TAG, "设置更新成功")

                    val outcome = readerSettingsCoordinator.applyUpdateResult(
                        currentState = getCurrentState(),
                        result = result,
                    )
                    if (result.newPageData != null) {
                        TimberLogger.d(
                            TAG,
                            "应用新的分页数据: 页数=${result.newPageData.pages.size}, 新页面索引=${result.newPageIndex}"
                        )
                    }

                    updateState(outcome.updatedState)

                    if (outcome.shouldRebuildVirtualPages) {
                        TimberLogger.d(TAG, "重新分页后重建虚拟页面")
                        buildVirtualPages(preserveCurrentIndex = true)
                    }
                    finishPerformanceTrace(
                        trace = trace,
                        status = "success",
                        metadata = mapOf(
                            "rebuildVirtualPages" to outcome.shouldRebuildVirtualPages.toString(),
                            "pageCount" to (result.newPageData?.pages?.size?.toString() ?: "unchanged"),
                        ),
                    )
                }
            }
        }
    }

    private fun handleUpdateContainerSizeAsync(intent: ReaderIntent.UpdateContainerSize) {
        viewModelScope.launch {
            val oldState = getCurrentState()
            if (intent.size.width == 0 || intent.size.height == 0 ||
                (oldState.containerSize == intent.size && oldState.density == intent.density)
            ) {
                TimberLogger.d(TAG, "容器尺寸无变化，跳过更新")
                return@launch
            }

            TimberLogger.d(TAG, "异步更新容器尺寸: ${oldState.containerSize} -> ${intent.size}")

            if (adapter.isSuccess.first()) {
                // 重新分割内容
                splitContentAndBuildVirtualPages(preserveVirtualIndex = true)

                // 启动后台全书分页计算
                val state = getCurrentState()
                if (state.readerSettings.pageFlipEffect != PageFlipEffect.VERTICAL) {
                    paginationService.fetchAllBookContentAndPaginateInBackground(
                        bookId = state.bookId,
                        chapterList = state.chapterList,
                        readerSettings = state.readerSettings,
                        containerSize = intent.size,
                        density = intent.density
                    )

                    // 获取页数缓存并更新状态
                    val pageCountCache = paginationService.getPageCountCache(
                        state.bookId,
                        state.readerSettings.fontSize,
                        intent.size
                    )
                    if (pageCountCache != null) {
                        val updatedState = getCurrentState().copy(
                            version = getCurrentState().version + 1,
                            pageCountCache = pageCountCache
                        )
                        updateState(updatedState)
                        TimberLogger.d(TAG, "页数缓存更新: 总页数=${pageCountCache.totalPages}")
                    }
                }
            }
        }
    }

    private fun handleSaveProgressAsync(intent: ReaderIntent.SaveProgress) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步保存阅读进度: force=${intent.force}")

            try {
                saveProgressUseCase.execute(getCurrentState())
                sendEffect(ReaderEffect.SaveProgressCompleted(true))
            } catch (e: Exception) {
                TimberLogger.e(TAG, "保存进度失败", e)
                sendEffect(ReaderEffect.SaveProgressCompleted(false))
            }
        }
    }

    private fun handlePreloadChaptersAsync(intent: ReaderIntent.PreloadChapters) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步预加载章节: ${intent.currentChapterId}")

            val state = getCurrentState()
            val currentChapterIndex =
                state.chapterList.indexOfFirst { it.id == intent.currentChapterId }

            if (currentChapterIndex >= 0) {
                preloadChaptersUseCase.execute(
                    viewModelScope,
                    state.chapterList,
                    intent.currentChapterId
                )

                // 预加载完成后，检查是否有新的相邻章节需要重建虚拟页面
                delay(500)
                checkAndRebuildVirtualPagesIfNeeded(currentChapterIndex)
            }
        }
    }
    
    private fun handleSaveToHistoryAsync(intent: ReaderIntent.SaveToHistory) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步保存历史记录: bookId=${intent.bookId}, chapterId=${intent.chapterId}")

            val outcome = readerHistoryCoordinator.saveHistory(
                intent = intent,
                persist = { value ->
                    historyService.saveHistory(
                        bookId = value.bookId,
                        chapterId = value.chapterId,
                        bookTitle = value.bookTitle,
                        author = value.author,
                        coverUrl = value.coverUrl,
                        chapterTitle = value.chapterTitle,
                    )
                },
            )

            if (outcome.saved) {
                TimberLogger.d(TAG, "历史记录保存成功")
            } else {
                TimberLogger.e(TAG, "保存历史记录失败")
            }
        }
    }

    private fun handleRetryAsync() {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步重试初始化")
            val currentState = getCurrentState()
            val bookId = currentState.bookId
            val chapterId = currentState.currentChapter?.id

            // 重置状态并重新初始化
            updateState(ReaderState())
            sendIntent(ReaderIntent.InitReader(bookId, chapterId))
        }
    }

    private fun handleUpdateScrollPositionAsync(intent: ReaderIntent.UpdateScrollPosition) {
        val currentState = getCurrentState()
        if (intent.pageIndex != currentState.currentPageIndex) {
            val trace = startPerformanceTrace(
                action = "flip",
                metadata = mapOf(
                    "source" to "vertical_scroll",
                    "fromIndex" to currentState.currentPageIndex.toString(),
                    "toIndex" to intent.pageIndex.toString(),
                ),
            )
            TimberLogger.d(
                TAG,
                "滚动更新页面索引: ${currentState.currentPageIndex} -> ${intent.pageIndex}"
            )

            val updatedState = currentState.copy(
                version = currentState.version + 1,
                currentPageIndex = intent.pageIndex
            )
            updateState(updateReadingProgressForState(updatedState))

            // 页面变化后保存进度
            sendIntent(ReaderIntent.SaveProgress())
            finishPerformanceTrace(
                trace = trace,
                status = "success",
                metadata = mapOf("mode" to PageFlipEffect.VERTICAL.name),
            )
        }
    }

    private fun handleUpdateSlideIndexAsync(intent: ReaderIntent.UpdateSlideIndex) {
        val state = getCurrentState()
        val virtualPages = state.virtualPages

        if (intent.index !in virtualPages.indices) {
            TimberLogger.w(
                TAG,
                "滑动翻页索引越界: index=${intent.index}, size=${virtualPages.size}"
            )
            return
        }

        // 索引无变化或越界的早期检查已移除，完全依赖 updateSlideFlipState 处理
        TimberLogger.d(
            TAG,
            "异步处理滑动索引更新: from=${state.virtualPageIndex}, to=${intent.index}"
        )
        val trace = startPerformanceTrace(
            action = "flip",
            metadata = mapOf(
                "source" to "slide_index",
                "fromIndex" to state.virtualPageIndex.toString(),
                "toIndex" to intent.index.toString(),
                "mode" to PageFlipEffect.SLIDE.name,
            ),
        )

        val newVirtualPage = virtualPages[intent.index]
        updateSlideFlipState(newVirtualPage, intent.index)

        // 页面切换后保存进度
        sendIntent(ReaderIntent.SaveProgress())

        // 检查预加载
        if (newVirtualPage is VirtualPage.ContentPage) {
            sendIntent(ReaderIntent.PreloadChapters(newVirtualPage.chapterId))
        }
        finishPerformanceTrace(
            trace = trace,
            status = "success",
            metadata = mapOf(
                "targetType" to newVirtualPage::class.simpleName.orEmpty(),
            ),
        )
    }

    private fun handleLoadBookReviewsAsync(intent: ReaderIntent.LoadBookReviews) {
        viewModelScope.launch {
            TimberLogger.d(TAG, "异步加载书籍评论: bookId=${intent.bookId}")
            
            try {
                val reviews = loadBookReviewsUseCase.execute(intent.bookId)
                TimberLogger.d(TAG, "书籍评论加载成功: 评论数=${reviews.size}")
                
                sendIntent(ReaderIntent.BookReviewsLoadSuccess(reviews))
            } catch (error: Exception) {
                TimberLogger.e(TAG, "书籍评论加载失败", error)
                sendIntent(ReaderIntent.BookReviewsLoadFailure(error.message ?: "加载评论失败"))
            }
        }
    }

    // ======================= 辅助方法 =======================

    /**
     * 处理章节切换结果
     */
    private fun handleChapterSwitchResult(switchResult: SwitchChapterUseCase.SwitchResult) {
        when (switchResult) {
            is SwitchChapterUseCase.SwitchResult.Success -> {
                TimberLogger.d(TAG, "章节切换成功")
                val newState = readerReducer.handleChapterSwitchSuccess(
                    getCurrentState(),
                    getCurrentState().chapterList[switchResult.newChapterIndex],
                    switchResult.newChapterIndex,
                    switchResult.pageData,
                    switchResult.initialPageIndex
                )
                updateState(newState)
                buildVirtualPages(preserveCurrentIndex = false)
            }

            is SwitchChapterUseCase.SwitchResult.Failure -> {
                TimberLogger.e(TAG, "章节切换失败", switchResult.error)
                val errorState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    isSwitchingChapter = false,
                    error = switchResult.error.message ?: "章节切换失败"
                )
                updateState(errorState)
                sendEffect(
                    ReaderEffect.ShowErrorDialog(
                        "章节切换失败",
                        switchResult.error.message ?: "未知错误"
                    )
                )
            }

            is SwitchChapterUseCase.SwitchResult.NoOp -> {
                TimberLogger.d(TAG, "章节切换无操作")
                val noOpState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    isSwitchingChapter = false
                )
                updateState(noOpState)
            }
        }
    }

    /**
     * 分割内容并构建虚拟页面
     */
    private suspend fun splitContentAndBuildVirtualPages(
        restoreProgress: Float? = null,
        preserveVirtualIndex: Boolean = false
    ) {
        TimberLogger.d(
            TAG,
            "开始分割内容并构建虚拟页面: restoreProgress=$restoreProgress, preserve=$preserveVirtualIndex"
        )

        val state = getCurrentState()

        // 首先分割内容
        val splitResult = splitContentUseCase.execute(
            state = state,
            restoreProgress = restoreProgress,
            includeAdjacentChapters = true
        )

        when (splitResult) {
            is SplitContentUseCase.SplitResult.Success -> {
                TimberLogger.d(
                    TAG,
                    "内容分割成功: 页数=${splitResult.pageData.pages.size}, 安全页面索引=${splitResult.safePageIndex}"
                )
                val updatedState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    currentPageData = splitResult.pageData,
                    currentPageIndex = splitResult.safePageIndex,
                    isSwitchingChapter = false
                )
                updateState(updatedState)

                // 构建虚拟页面
                buildVirtualPages(preserveVirtualIndex)

                // 若仍未开始全书分页，启动后台任务
                val st = getCurrentState()
                if (st.pageCountCache == null && st.containerSize != IntSize.Zero && st.density != null) {
                    TimberLogger.d(TAG, "未检测到页数缓存，启动后台全书分页计算")
                    paginationService.fetchAllBookContentAndPaginateInBackground(
                        bookId = st.bookId,
                        chapterList = st.chapterList,
                        readerSettings = st.readerSettings,
                        containerSize = st.containerSize,
                        density = st.density
                    )
                }
            }

            is SplitContentUseCase.SplitResult.Failure -> {
                TimberLogger.e(TAG, "内容分割失败", splitResult.error)
                val errorState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    error = splitResult.error.message ?: "内容分割失败",
                    isSwitchingChapter = false
                )
                updateState(errorState)
                sendEffect(
                    ReaderEffect.ShowErrorDialog(
                        "内容加载失败",
                        splitResult.error.message ?: "未知错误"
                    )
                )
            }
        }
    }

    /**
     * 使用BuildVirtualPagesUseCase构建虚拟页面
     */
    private fun buildVirtualPages(preserveCurrentIndex: Boolean = true) {
        TimberLogger.d(TAG, "构建虚拟页面: preserve=$preserveCurrentIndex")

        val buildResult = buildVirtualPagesUseCase.execute(
            state = getCurrentState(),
            preserveCurrentIndex = preserveCurrentIndex
        )

        when (buildResult) {
            is BuildVirtualPagesUseCase.BuildResult.Success -> {
                TimberLogger.d(
                    TAG,
                    "虚拟页面构建成功: 总页数=${buildResult.virtualPages.size}, 当前索引=${buildResult.newVirtualPageIndex}"
                )
                val newState = readerReducer.handleVirtualPagesUpdate(
                    getCurrentState(),
                    buildResult.virtualPages,
                    buildResult.newVirtualPageIndex,
                    buildResult.loadedChapterData
                )
                updateState(newState)
            }

            is BuildVirtualPagesUseCase.BuildResult.Failure -> {
                TimberLogger.e(TAG, "虚拟页面构建失败", buildResult.error)
                // 不中断整个操作，只记录错误
            }
        }
    }

    /**
     * 处理平移模式的状态更新（避免循环更新）
     */
    private fun updateSlideFlipState(newVirtualPage: VirtualPage, newVirtualIndex: Int) {
        TimberLogger.d(
            TAG,
            "更新平移模式状态: 索引=$newVirtualIndex, 页面=${newVirtualPage::class.simpleName}"
        )

        when (newVirtualPage) {
            is VirtualPage.ContentPage -> {
                val state = getCurrentState()
                if (newVirtualPage.chapterId != state.currentChapter?.id) {
                    val newChapterIndex =
                        state.chapterList.indexOfFirst { it.id == newVirtualPage.chapterId }
                    val newChapter = state.chapterList.getOrNull(newChapterIndex)

                    if (newChapter != null) {
                        TimberLogger.d(
                            TAG,
                            "平移模式跨章节翻页: ${state.currentChapter?.chapterName} -> ${newChapter.chapterName}"
                        )
                        val newChapterData =
                            state.loadedChapterData[newVirtualPage.chapterId] // May be null

                        val tempState = state.copy(
                            version = state.version + 1,
                            currentChapter = newChapter,
                            currentChapterIndex = newChapterIndex,
                            currentPageIndex = newVirtualPage.pageIndex,
                            currentPageData = newChapterData, // This can be null
                            virtualPageIndex = newVirtualIndex,
                            bookContent = newChapterData?.content.orEmpty()
                        )
                        updateState(updateReadingProgressForState(tempState))

                        if (newChapterData == null) {
                            TimberLogger.d(
                                TAG,
                                "目标章节内容未缓存，发送异步加载意图: ${newChapter.id}"
                            )
                            sendIntent(ReaderIntent.SwitchToChapter(newChapter.id))
                        }
                    } else {
                        // 未预加载到章节数据，主动触发章节加载
                        val newChapterIndex =
                            state.chapterList.indexOfFirst { it.id == newVirtualPage.chapterId }
                        val newChapter = state.chapterList.getOrNull(newChapterIndex)
                        if (newChapter == null) {
                            TimberLogger.e(
                                TAG,
                                "目标章节未在章节列表中: ${newVirtualPage.chapterId}"
                            )
                        } else {
                            TimberLogger.d(
                                TAG,
                                "章节数据未预加载，主动加载章节: ${newChapter.chapterName}"
                            )
                            viewModelScope.launch {
                                val switchResult = switchChapterUseCase.execute(
                                    state,
                                    newVirtualPage.chapterId,
                                    this,
                                    null
                                )
                                when (switchResult) {
                                    is SwitchChapterUseCase.SwitchResult.Success -> {
                                        var loadedState = state.copy(
                                            currentChapter = newChapter,
                                            currentChapterIndex = newChapterIndex,
                                            currentPageData = switchResult.pageData,
                                            currentPageIndex = switchResult.initialPageIndex,
                                            virtualPageIndex = newVirtualIndex,
                                            bookContent = switchResult.pageData.content
                                        )
                                        updateReadingProgressForState(loadedState)
                                        // 重新构建虚拟页面，确保后续滑动正确
                                        buildVirtualPages(preserveCurrentIndex = true)
                                    }

                                    is SwitchChapterUseCase.SwitchResult.Failure -> TimberLogger.e(
                                        TAG,
                                        "主动切换章节失败",
                                        switchResult.error
                                    )

                                    else -> {}
                                }
                            }
                        }
                    }
                    return // Exit here to avoid falling into the same-chapter logic
                }

                // Same-chapter flip logic
                TimberLogger.d(
                    TAG,
                    "平移模式同章节翻页: 页面 ${state.currentPageIndex} -> ${newVirtualPage.pageIndex}"
                )
                val updatedState = state.copy(
                    version = state.version + 1,
                    currentPageIndex = newVirtualPage.pageIndex,
                    virtualPageIndex = newVirtualIndex
                )
                updateState(updateReadingProgressForState(updatedState))

                viewModelScope.launch {
                    saveProgressUseCase.execute(getCurrentState())
                    checkAndPreload(getCurrentState().virtualPageIndex)
                }
            }

            is VirtualPage.BookDetailPage -> {
                TimberLogger.d(TAG, "平移模式切换到书籍详情页")
                val updatedState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    currentPageIndex = -1,
                    virtualPageIndex = newVirtualIndex
                )
                updateState(updateReadingProgressForState(updatedState))
            }

            is VirtualPage.ChapterSection -> {
                TimberLogger.d(TAG, "平移模式切换到章节区域")
                val updatedState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    virtualPageIndex = newVirtualIndex
                )
                updateState(updatedState)
            }
        }
    }

    /**
     * 处理其他翻页模式的状态更新
     */
    private fun updatePageFlipState(newVirtualPage: VirtualPage) {
        TimberLogger.d(TAG, "更新翻页状态: 页面=${newVirtualPage::class.simpleName}")

        when (newVirtualPage) {
            is VirtualPage.ContentPage -> {
                val state = getCurrentState()
                if (newVirtualPage.chapterId != state.currentChapter?.id) {
                    // 切换章节
                    val newChapterData = state.loadedChapterData[newVirtualPage.chapterId]
                    if (newChapterData != null) {
                        val newChapterIndex =
                            state.chapterList.indexOfFirst { it.id == newVirtualPage.chapterId }
                        val newChapter = state.chapterList.getOrNull(newChapterIndex)

                        if (newChapter != null) {
                            TimberLogger.d(
                                TAG,
                                "翻页模式章节切换: ${state.currentChapter?.chapterName} -> ${newChapter.chapterName}"
                            )
                            val updatedState = state.copy(
                                version = state.version + 1,
                                currentChapter = newChapter,
                                currentChapterIndex = newChapterIndex,
                                currentPageIndex = newVirtualPage.pageIndex,
                                currentPageData = newChapterData,
                                bookContent = newChapterData.content
                            )
                            updateState(updateReadingProgressForState(updatedState))

                            // 保存进度并触发动态预加载
                            viewModelScope.launch {
                                saveProgressUseCase.execute(getCurrentState())
                                preloadChaptersUseCase.performDynamicPreload(
                                    viewModelScope,
                                    getCurrentState(),
                                    newVirtualPage.chapterId,
                                    triggerExpansion = false
                                )
                            }
                        }
                    }
                } else {
                    // 同章节内翻页
                    TimberLogger.d(
                        TAG,
                        "翻页模式同章节翻页: 页面${state.currentPageIndex} -> ${newVirtualPage.pageIndex}"
                    )
                    val updatedState = state.copy(
                        version = state.version + 1,
                        currentPageIndex = newVirtualPage.pageIndex
                    )
                    updateState(updateReadingProgressForState(updatedState))
                }
            }

            is VirtualPage.BookDetailPage -> {
                TimberLogger.d(TAG, "翻页模式切换到书籍详情页")
                val updatedState = getCurrentState().copy(
                    version = getCurrentState().version + 1,
                    currentPageIndex = -1
                )
                updateState(updateReadingProgressForState(updatedState))

                // 保存进度
                viewModelScope.launch {
                    saveProgressUseCase.execute(getCurrentState())
                }
            }

            is VirtualPage.ChapterSection -> {
                TimberLogger.d(TAG, "翻页模式章节区域（暂不支持）")
                // 章节模式暂不支持
            }
        }
    }

    /**
     * 更新阅读进度 - 基于当前状态计算
     */
    private fun updateReadingProgressForState(state: ReaderState): ReaderState {
        val pageCountCache = state.pageCountCache
        val currentChapter = state.currentChapter
        val currentPageIndex = state.currentPageIndex

        if (pageCountCache != null && currentChapter != null && pageCountCache.totalPages > 0) {
            val chapterRange =
                pageCountCache.chapterPageRanges.find { it.chapterId == currentChapter.id }
            if (chapterRange != null) {
                val globalPage = when {
                    currentPageIndex == -1 -> chapterRange.startPage // 书籍详情页
                    else -> chapterRange.startPage + currentPageIndex
                }
                val newProgress = globalPage.toFloat() / pageCountCache.totalPages.toFloat()
                TimberLogger.d(
                    TAG,
                    "更新状态阅读进度: 全局页面=$globalPage/${pageCountCache.totalPages}, 进度=${(newProgress * 100).toInt()}%"
                )
                return state.copy(
                    version = state.version + 1,
                    readingProgress = newProgress.coerceIn(0f, 1f)
                )
            }
        }

        return state
    }

    /**
     * 检查并重建虚拟页面（如果需要）
     */
    private suspend fun checkAndRebuildVirtualPagesIfNeeded(currentChapterIndex: Int) {
        TimberLogger.d(TAG, "检查虚拟页面重建需求")
        val hasNewAdjacent = preloadChaptersUseCase.checkIfNewAdjacentChaptersLoaded(
            getCurrentState(),
            currentChapterIndex
        )
        if (hasNewAdjacent) {
            TimberLogger.d(TAG, "检测到新的相邻章节已加载，重建虚拟页面")
            buildVirtualPages(preserveCurrentIndex = true)
        } else {
            TimberLogger.d(TAG, "未检测到新的相邻章节，无需重建")
        }
    }

    // ReaderViewModel.kt
    private suspend fun checkAndPreload(virtualIndex: Int) {
        val state = getCurrentState()
        val page = state.virtualPages.getOrNull(virtualIndex) as? VirtualPage.ContentPage ?: return
        val chapterIdx = state.chapterList.indexOfFirst { it.id == page.chapterId }
        if (chapterIdx < 0) return      // 容错

        preloadChaptersUseCase.performDynamicPreload(
            scope = viewModelScope,
            state = state,
            currentChapterId = page.chapterId,
            triggerExpansion = true        // ← 一定要扩展窗口
        )                                  // :contentReference[oaicite:4]{index=4}

        delay(500)                         // 给 IO 线程一个缓冲
        buildVirtualPages(preserveCurrentIndex = true)
    }

    private fun startPerformanceTrace(
        action: String,
        metadata: Map<String, String> = emptyMap(),
    ): ReaderPerformanceTrace {
        val trace = readerPerformanceTraceCoordinator.start(
            action = action,
            metadata = metadata,
        )
        TimberLogger.d(
            ReaderLogTags.READER_PERFORMANCE_PROBE,
            readerPerformanceTraceCoordinator.formatStartMessage(trace),
        )
        return trace
    }

    private fun finishPerformanceTrace(
        trace: ReaderPerformanceTrace,
        status: String,
        metadata: Map<String, String> = emptyMap(),
    ) {
        TimberLogger.d(
            ReaderLogTags.READER_PERFORMANCE_PROBE,
            readerPerformanceTraceCoordinator.formatFinishMessage(
                trace = trace,
                status = status,
                metadata = metadata,
            ),
        )
    }

    private fun switchResultStatus(result: SwitchChapterUseCase.SwitchResult): String {
        return when (result) {
            is SwitchChapterUseCase.SwitchResult.Success -> "success"
            is SwitchChapterUseCase.SwitchResult.Failure -> "failure"
            is SwitchChapterUseCase.SwitchResult.NoOp -> "noop"
        }
    }


    override fun onCleared() {
        super.onCleared()
        TimberLogger.d(TAG, "ReaderViewModel销毁，保存进度并清理资源")
        viewModelScope.launch {
            saveProgressUseCase.execute(getCurrentState())
        }
        preloadChaptersUseCase.cancelPreload()
    }
}
