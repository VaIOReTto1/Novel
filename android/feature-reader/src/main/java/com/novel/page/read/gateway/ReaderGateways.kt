package com.novel.page.read.gateway

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.novel.core.StableThrowable
import com.novel.page.read.repository.PageCountCacheData
import com.novel.page.read.repository.ProgressiveCalculationState
import com.novel.page.read.viewmodel.BookReview
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface ReaderPaginationGateway {
    fun observePaginationProgress(): Flow<ProgressiveCalculationState>

    suspend fun getPageCountCache(
        bookId: String,
        fontSize: Int,
        containerSize: IntSize,
    ): PageCountCacheData?

    suspend fun initReader(
        bookId: String,
        chapterId: String?,
        initialState: ReaderState,
        scope: CoroutineScope,
    ): Result<ReaderInitResult>

    suspend fun flipPage(
        state: ReaderState,
        direction: FlipDirection,
        scope: CoroutineScope,
    ): ReaderFlipResult

    fun executePreloadCheck(
        state: ReaderState,
        currentVirtualPage: VirtualPage.ContentPage,
        scope: CoroutineScope,
    )

    suspend fun switchChapter(
        state: ReaderState,
        newChapterId: String,
        scope: CoroutineScope,
        flipDirection: FlipDirection? = null,
    ): ReaderSwitchChapterResult

    suspend fun seekToProgress(
        progress: Float,
        state: ReaderState,
    ): ReaderSeekProgressResult

    suspend fun saveProgress(state: ReaderState)

    fun preloadChapters(
        scope: CoroutineScope,
        chapterList: List<Chapter>,
        currentChapterId: String,
        state: ReaderState? = null,
    )

    fun performDynamicPreload(
        scope: CoroutineScope,
        state: ReaderState,
        currentChapterId: String,
        triggerExpansion: Boolean = false,
    )

    suspend fun checkIfNewAdjacentChaptersLoaded(
        state: ReaderState,
        currentChapterIndex: Int,
    ): Boolean

    suspend fun splitContent(
        state: ReaderState,
        restoreProgress: Float? = null,
        includeAdjacentChapters: Boolean = false,
    ): ReaderSplitContentResult

    fun buildVirtualPages(
        state: ReaderState,
        preserveCurrentIndex: Boolean = true,
    ): ReaderBuildVirtualPagesResult

    fun fetchAllBookContentAndPaginateInBackground(
        bookId: String,
        chapterList: List<Chapter>,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density,
    )

    suspend fun loadBookReviews(bookId: String): ImmutableList<BookReview>

    fun cancelPreload()
}

interface ReaderSettingsGateway {
    fun loadSettings(): ReaderSettings

    suspend fun updateSettings(
        newSettings: ReaderSettings,
        state: ReaderState,
    ): ReaderSettingsChangeResult
}

interface ReaderHistoryGateway {
    suspend fun saveHistory(
        bookId: String,
        chapterId: String,
        bookTitle: String? = null,
        author: String? = null,
        coverUrl: String? = null,
        chapterTitle: String? = null,
    )
}

@Stable
data class ReaderInitResult(
    val settings: ReaderSettings,
    val chapterList: ImmutableList<Chapter>,
    val initialChapter: Chapter,
    val initialChapterIndex: Int,
    val initialPageData: PageData,
    val initialPageIndex: Int,
    val pageCountCache: PageCountCacheData?,
)

sealed interface ReaderFlipResult {
    @Stable
    data class VirtualPageChanged(
        val newVirtualPageIndex: Int,
        val newVirtualPage: VirtualPage,
        val needsPreloadCheck: Boolean = false,
    ) : ReaderFlipResult

    @Stable
    data class ChapterChanged(
        val switchResult: ReaderSwitchChapterResult,
    ) : ReaderFlipResult

    @Stable
    data class Failure(
        val error: StableThrowable,
    ) : ReaderFlipResult

    data object NoOp : ReaderFlipResult

    @Stable
    data class NeedsVirtualPageRebuild(
        val newVirtualPageIndex: Int,
        val newVirtualPage: VirtualPage,
        val needsPreloadCheck: Boolean,
    ) : ReaderFlipResult
}

sealed interface ReaderSwitchChapterResult {
    @Stable
    data class Success(
        val newChapterIndex: Int,
        val pageData: PageData,
        val initialPageIndex: Int = 0,
    ) : ReaderSwitchChapterResult

    @Stable
    data class Failure(
        val error: StableThrowable,
    ) : ReaderSwitchChapterResult

    data object NoOp : ReaderSwitchChapterResult
}

sealed interface ReaderSeekProgressResult {
    @Stable
    data class Success(
        val newChapterIndex: Int,
        val newPageData: PageData,
        val newPageIndex: Int,
    ) : ReaderSeekProgressResult

    @Stable
    data class Failure(
        val error: StableThrowable,
    ) : ReaderSeekProgressResult

    data object NoOp : ReaderSeekProgressResult
}

@Stable
data class ReaderSettingsChangeResult(
    val newPageData: PageData?,
    val newPageIndex: Int = 0,
)

sealed interface ReaderSplitContentResult {
    @Stable
    data class Success(
        val pageData: PageData,
        val safePageIndex: Int,
    ) : ReaderSplitContentResult

    @Stable
    data class Failure(
        val error: StableThrowable,
    ) : ReaderSplitContentResult
}

sealed interface ReaderBuildVirtualPagesResult {
    @Stable
    data class Success(
        val virtualPages: ImmutableList<VirtualPage>,
        val newVirtualPageIndex: Int,
        val loadedChapterData: ImmutableMap<String, PageData>,
    ) : ReaderBuildVirtualPagesResult

    @Stable
    data class Failure(
        val error: StableThrowable,
    ) : ReaderBuildVirtualPagesResult
}
