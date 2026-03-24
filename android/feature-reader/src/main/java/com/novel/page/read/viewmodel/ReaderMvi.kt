package com.novel.page.read.viewmodel

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.novel.core.mvi.MviEffect
import com.novel.core.mvi.MviIntent
import com.novel.core.mvi.MviState
import com.novel.page.read.repository.PageCountCacheData
import com.novel.page.read.repository.ProgressiveCalculationState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class ReaderIntent : MviIntent {
    data class InitReader(val bookId: String, val chapterId: String?) : ReaderIntent()
    data object Retry : ReaderIntent()
    data class PageFlip(val direction: FlipDirection) : ReaderIntent()
    data object PreviousChapter : ReaderIntent()
    data object NextChapter : ReaderIntent()
    data class SwitchToChapter(val chapterId: String) : ReaderIntent()
    data class SeekToProgress(val progress: Float) : ReaderIntent()
    data class UpdateSettings(val settings: ReaderSettings) : ReaderIntent()
    data class UpdateContainerSize(val size: IntSize, val density: Density) : ReaderIntent()
    data class ToggleMenu(val show: Boolean) : ReaderIntent()
    data class ShowChapterList(val show: Boolean) : ReaderIntent()
    data class ShowSettingsPanel(val show: Boolean) : ReaderIntent()
    data class SaveProgress(val force: Boolean = false) : ReaderIntent()
    data class PreloadChapters(val currentChapterId: String) : ReaderIntent()

    data class SaveToHistory(
        val bookId: String,
        val chapterId: String,
        val bookTitle: String? = null,
        val author: String? = null,
        val coverUrl: String? = null,
        val chapterTitle: String? = null,
    ) : ReaderIntent()

    data class UpdateScrollPosition(val pageIndex: Int) : ReaderIntent()
    data class UpdateSlideIndex(val index: Int) : ReaderIntent()
    data class ShowProgressRestoredHint(val show: Boolean) : ReaderIntent()
    data class LoadBookReviews(val bookId: String) : ReaderIntent()
    data class BookReviewsLoadSuccess(val reviews: ImmutableList<BookReview>) : ReaderIntent()
    data class BookReviewsLoadFailure(val error: String) : ReaderIntent()
}

@Stable
data class ReaderState(
    override val version: Long = 0L,
    override val isLoading: Boolean = false,
    override val error: String? = null,
    val bookId: String = "",
    val chapterList: ImmutableList<Chapter> = persistentListOf(),
    val currentChapter: Chapter? = null,
    val currentChapterIndex: Int = 0,
    val bookContent: String = "",
    val readingProgress: Float = 0f,
    val readerSettings: ReaderSettings = ReaderSettings.getDefault(),
    val currentPageData: PageData? = null,
    val currentPageIndex: Int = 0,
    val isSwitchingChapter: Boolean = false,
    val containerSize: IntSize = IntSize.Zero,
    val density: Density? = null,
    val virtualPages: ImmutableList<VirtualPage> = persistentListOf(),
    val virtualPageIndex: Int = 0,
    val loadedChapterData: Map<String, PageData> = emptyMap(),
    val pageCountCache: PageCountCacheData? = null,
    val paginationState: ProgressiveCalculationState = ProgressiveCalculationState(),
    val previousChapterData: PageData? = null,
    val nextChapterData: PageData? = null,
    val isMenuVisible: Boolean = false,
    val isChapterListVisible: Boolean = false,
    val isSettingsPanelVisible: Boolean = false,
    val showProgressRestoredHint: Boolean = false,
    val bookReviews: ImmutableList<BookReview> = persistentListOf(),
    val isLoadingReviews: Boolean = false,
    val reviewsError: String? = null,
) : MviState {

    override val isEmpty: Boolean
        get() = !isLoading && !hasError && chapterList.isEmpty()

    override val isSuccess: Boolean
        get() = !isLoading && !hasError && currentChapter != null

    val isFirstChapter: Boolean
        get() = currentChapterIndex == 0

    val isLastChapter: Boolean
        get() = currentChapterIndex >= chapterList.size - 1

    val computedReadingProgress: Float
        get() {
            if (readerSettings.pageFlipEffect == PageFlipEffect.VERTICAL) {
                if (chapterList.isEmpty()) return 0f
                return (currentChapterIndex + 1).toFloat() / chapterList.size.toFloat()
            }

            val cache = pageCountCache ?: return 0f
            if (cache.totalPages <= 0) return 0f

            val chapterRange = cache.chapterPageRanges.find { it.chapterId == currentChapter?.id } ?: return 0f
            val globalCurrentPage = chapterRange.startPage + currentPageIndex
            return (globalCurrentPage + 1).toFloat() / cache.totalPages.toFloat()
        }
}

sealed class ReaderEffect : MviEffect {
    data class NavigateBack(val reason: String = "") : ReaderEffect()
    data class NavigateToBookDetail(val bookId: String) : ReaderEffect()
    data class NavigateToChapter(val chapterId: String) : ReaderEffect()
    data class ShowToast(val message: String) : ReaderEffect()
    data class ShowSnackbar(val message: String, val actionLabel: String? = null) : ReaderEffect()
    data class SetBrightness(val brightness: Float) : ReaderEffect()
    data class SetKeepScreenOn(val keepOn: Boolean) : ReaderEffect()
    data class TriggerHapticFeedback(val type: HapticFeedbackType = HapticFeedbackType.LIGHT) : ReaderEffect()
    data class ShareContent(val content: String, val title: String) : ReaderEffect()
    data class ShowErrorDialog(
        val title: String,
        val message: String,
        val canRetry: Boolean = true,
    ) : ReaderEffect()

    data class SaveProgressCompleted(val success: Boolean) : ReaderEffect()
    data class PreloadCompleted(val chapterId: String, val success: Boolean) : ReaderEffect()
}

enum class HapticFeedbackType {
    LIGHT,
    MEDIUM,
    HEAVY,
}

@Immutable
data class Chapter(
    val id: String,
    val chapterName: String,
    val chapterNum: String? = null,
    val isVip: String = "0",
)

enum class FlipDirection {
    PREVIOUS,
    NEXT,
}

sealed class VirtualPage {
    data object BookDetailPage : VirtualPage()
    data class ContentPage(val chapterId: String, val pageIndex: Int) : VirtualPage()
    data class ChapterSection(val chapterId: String) : VirtualPage()
}

@Stable
data class ChapterCache(
    val chapter: Chapter,
    val content: String,
    var pageData: PageData? = null,
)

@Stable
data class PageData(
    val chapterId: String,
    val chapterName: String,
    val content: String,
    val pages: ImmutableList<String>,
    val isFirstPage: Boolean = false,
    val isLastPage: Boolean = false,
    val isFirstChapter: Boolean = false,
    val isLastChapter: Boolean = false,
    val nextChapterData: PageData? = null,
    val previousChapterData: PageData? = null,
    val bookInfo: BookInfo? = null,
    val hasBookDetailPage: Boolean = false,
) {
    val pageCount: Int get() = pages.size

    @Immutable
    data class BookInfo(
        val bookId: String,
        val bookName: String,
        val authorName: String,
        val bookDesc: String,
        val picUrl: String,
        val visitCount: Long,
        val wordCount: Int,
        val categoryName: String,
    )
}

@Stable
data class BookReview(
    val id: String,
    val content: String,
    val rating: Int,
    val readTime: String,
    val userName: String,
    val userPhoto: String? = null,
    val commentTime: String? = null,
)

@Immutable
data class BackgroundTheme(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
)

@Stable
data class ReaderInfo(
    val paginationState: ProgressiveCalculationState,
    val pageCountCache: PageCountCacheData?,
    val currentChapter: Chapter?,
    val perChapterPageIndex: Int,
)
