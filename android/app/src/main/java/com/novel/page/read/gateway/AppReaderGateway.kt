package com.novel.page.read.gateway

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.novel.page.read.service.HistoryService
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.SettingsService
import com.novel.page.read.usecase.BuildVirtualPagesUseCase
import com.novel.page.read.usecase.FlipPageUseCase
import com.novel.page.read.usecase.InitReaderResult
import com.novel.page.read.usecase.InitReaderUseCase
import com.novel.page.read.usecase.LoadBookReviewsUseCase
import com.novel.page.read.usecase.ObservePaginationProgressUseCase
import com.novel.page.read.usecase.PreloadChaptersUseCase
import com.novel.page.read.usecase.SaveProgressUseCase
import com.novel.page.read.usecase.SeekProgressUseCase
import com.novel.page.read.usecase.SplitContentUseCase
import com.novel.page.read.usecase.SwitchChapterUseCase
import com.novel.page.read.usecase.UpdateSettingsUseCase
import com.novel.page.read.viewmodel.BookReview
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Singleton
class AppReaderPaginationGateway @Inject constructor(
    private val initReaderUseCase: InitReaderUseCase,
    private val flipPageUseCase: FlipPageUseCase,
    private val switchChapterUseCase: SwitchChapterUseCase,
    private val seekProgressUseCase: SeekProgressUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val preloadChaptersUseCase: PreloadChaptersUseCase,
    private val buildVirtualPagesUseCase: BuildVirtualPagesUseCase,
    private val splitContentUseCase: SplitContentUseCase,
    private val loadBookReviewsUseCase: LoadBookReviewsUseCase,
    private val paginationService: PaginationService,
    observePaginationProgressUseCase: ObservePaginationProgressUseCase,
) : ReaderPaginationGateway {

    private val paginationProgressFlow: Flow<com.novel.page.read.repository.ProgressiveCalculationState> =
        observePaginationProgressUseCase.execute()

    override fun observePaginationProgress(): Flow<com.novel.page.read.repository.ProgressiveCalculationState> {
        return paginationProgressFlow
    }

    override suspend fun getPageCountCache(
        bookId: String,
        fontSize: Int,
        containerSize: IntSize,
    ) = paginationService.getPageCountCache(bookId, fontSize, containerSize)

    override suspend fun initReader(
        bookId: String,
        chapterId: String?,
        initialState: ReaderState,
        scope: CoroutineScope,
    ): Result<ReaderInitResult> {
        return initReaderUseCase.execute(bookId, chapterId, initialState, scope)
            .map(InitReaderResult::toGatewayResult)
    }

    override suspend fun flipPage(
        state: ReaderState,
        direction: FlipDirection,
        scope: CoroutineScope,
    ): ReaderFlipResult {
        return flipPageUseCase.execute(state, direction, scope).toGatewayResult()
    }

    override fun executePreloadCheck(
        state: ReaderState,
        currentVirtualPage: VirtualPage.ContentPage,
        scope: CoroutineScope,
    ) {
        flipPageUseCase.executePreloadCheck(state, currentVirtualPage, scope)
    }

    override suspend fun switchChapter(
        state: ReaderState,
        newChapterId: String,
        scope: CoroutineScope,
        flipDirection: FlipDirection?,
    ): ReaderSwitchChapterResult {
        return switchChapterUseCase.execute(state, newChapterId, scope, flipDirection)
            .toGatewayResult()
    }

    override suspend fun seekToProgress(
        progress: Float,
        state: ReaderState,
    ): ReaderSeekProgressResult {
        return seekProgressUseCase.execute(progress, state).toGatewayResult()
    }

    override suspend fun saveProgress(state: ReaderState) {
        saveProgressUseCase.execute(state)
    }

    override fun preloadChapters(
        scope: CoroutineScope,
        chapterList: List<Chapter>,
        currentChapterId: String,
        state: ReaderState?,
    ) {
        preloadChaptersUseCase.execute(scope, chapterList, currentChapterId, state)
    }

    override fun performDynamicPreload(
        scope: CoroutineScope,
        state: ReaderState,
        currentChapterId: String,
        triggerExpansion: Boolean,
    ) {
        preloadChaptersUseCase.performDynamicPreload(scope, state, currentChapterId, triggerExpansion)
    }

    override suspend fun checkIfNewAdjacentChaptersLoaded(
        state: ReaderState,
        currentChapterIndex: Int,
    ): Boolean {
        return preloadChaptersUseCase.checkIfNewAdjacentChaptersLoaded(state, currentChapterIndex)
    }

    override suspend fun splitContent(
        state: ReaderState,
        restoreProgress: Float?,
        includeAdjacentChapters: Boolean,
    ): ReaderSplitContentResult {
        return splitContentUseCase.execute(state, restoreProgress, includeAdjacentChapters)
            .toGatewayResult()
    }

    override fun buildVirtualPages(
        state: ReaderState,
        preserveCurrentIndex: Boolean,
    ): ReaderBuildVirtualPagesResult {
        return buildVirtualPagesUseCase.execute(state, preserveCurrentIndex).toGatewayResult()
    }

    override fun fetchAllBookContentAndPaginateInBackground(
        bookId: String,
        chapterList: List<Chapter>,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density,
    ) {
        paginationService.fetchAllBookContentAndPaginateInBackground(
            bookId = bookId,
            chapterList = chapterList,
            readerSettings = readerSettings,
            containerSize = containerSize,
            density = density,
        )
    }

    override suspend fun loadBookReviews(bookId: String): ImmutableList<BookReview> {
        return loadBookReviewsUseCase.execute(bookId)
    }

    override fun cancelPreload() {
        preloadChaptersUseCase.cancelPreload()
    }
}

@Singleton
class AppReaderSettingsGateway @Inject constructor(
    private val settingsService: SettingsService,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
) : ReaderSettingsGateway {

    override fun loadSettings(): ReaderSettings = settingsService.loadSettings()

    override suspend fun updateSettings(
        newSettings: ReaderSettings,
        state: ReaderState,
    ): ReaderSettingsChangeResult {
        return when (val result = updateSettingsUseCase.execute(newSettings, state)) {
            is UpdateSettingsUseCase.UpdateResult.Success -> {
                ReaderSettingsChangeResult(
                    newPageData = result.newPageData,
                    newPageIndex = result.newPageIndex,
                )
            }
        }
    }
}

@Singleton
class AppReaderHistoryGateway @Inject constructor(
    private val historyService: HistoryService,
) : ReaderHistoryGateway {

    override suspend fun saveHistory(
        bookId: String,
        chapterId: String,
        bookTitle: String?,
        author: String?,
        coverUrl: String?,
        chapterTitle: String?,
    ) {
        historyService.saveHistory(
            bookId = bookId,
            chapterId = chapterId,
            bookTitle = bookTitle,
            author = author,
            coverUrl = coverUrl,
            chapterTitle = chapterTitle,
        )
    }
}

private fun InitReaderResult.toGatewayResult(): ReaderInitResult {
    return ReaderInitResult(
        settings = settings,
        chapterList = chapterList,
        initialChapter = initialChapter,
        initialChapterIndex = initialChapterIndex,
        initialPageData = initialPageData,
        initialPageIndex = initialPageIndex,
        pageCountCache = pageCountCache,
    )
}

private fun FlipPageUseCase.FlipResult.toGatewayResult(): ReaderFlipResult {
    return when (this) {
        is FlipPageUseCase.FlipResult.VirtualPageChanged -> {
            ReaderFlipResult.VirtualPageChanged(
                newVirtualPageIndex = newVirtualPageIndex,
                newVirtualPage = newVirtualPage,
                needsPreloadCheck = needsPreloadCheck,
            )
        }

        is FlipPageUseCase.FlipResult.ChapterChanged -> {
            ReaderFlipResult.ChapterChanged(
                switchResult = switchResult.toGatewayResult(),
            )
        }

        is FlipPageUseCase.FlipResult.Failure -> ReaderFlipResult.Failure(error)
        FlipPageUseCase.FlipResult.NoOp -> ReaderFlipResult.NoOp

        is FlipPageUseCase.FlipResult.NeedsVirtualPageRebuild -> {
            ReaderFlipResult.NeedsVirtualPageRebuild(
                newVirtualPageIndex = newVirtualPageIndex,
                newVirtualPage = newVirtualPage,
                needsPreloadCheck = needsPreloadCheck,
            )
        }
    }
}

private fun SwitchChapterUseCase.SwitchResult.toGatewayResult(): ReaderSwitchChapterResult {
    return when (this) {
        is SwitchChapterUseCase.SwitchResult.Success -> {
            ReaderSwitchChapterResult.Success(
                newChapterIndex = newChapterIndex,
                pageData = pageData,
                initialPageIndex = initialPageIndex,
            )
        }

        is SwitchChapterUseCase.SwitchResult.Failure -> ReaderSwitchChapterResult.Failure(error)
        SwitchChapterUseCase.SwitchResult.NoOp -> ReaderSwitchChapterResult.NoOp
    }
}

private fun SeekProgressUseCase.SeekResult.toGatewayResult(): ReaderSeekProgressResult {
    return when (this) {
        is SeekProgressUseCase.SeekResult.Success -> {
            ReaderSeekProgressResult.Success(
                newChapterIndex = newChapterIndex,
                newPageData = newPageData,
                newPageIndex = newPageIndex,
            )
        }

        is SeekProgressUseCase.SeekResult.Failure -> ReaderSeekProgressResult.Failure(error)
        SeekProgressUseCase.SeekResult.NoOp -> ReaderSeekProgressResult.NoOp
    }
}

private fun SplitContentUseCase.SplitResult.toGatewayResult(): ReaderSplitContentResult {
    return when (this) {
        is SplitContentUseCase.SplitResult.Success -> {
            ReaderSplitContentResult.Success(
                pageData = pageData,
                safePageIndex = safePageIndex,
            )
        }

        is SplitContentUseCase.SplitResult.Failure -> ReaderSplitContentResult.Failure(error)
    }
}

private fun BuildVirtualPagesUseCase.BuildResult.toGatewayResult(): ReaderBuildVirtualPagesResult {
    return when (this) {
        is BuildVirtualPagesUseCase.BuildResult.Success -> {
            ReaderBuildVirtualPagesResult.Success(
                virtualPages = virtualPages,
                newVirtualPageIndex = newVirtualPageIndex,
                loadedChapterData = loadedChapterData,
            )
        }

        is BuildVirtualPagesUseCase.BuildResult.Failure -> ReaderBuildVirtualPagesResult.Failure(error)
    }
}
