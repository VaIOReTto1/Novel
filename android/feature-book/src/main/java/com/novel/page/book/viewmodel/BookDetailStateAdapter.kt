package com.novel.page.book.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.Stable
import com.novel.core.adapter.StateAdapter
import com.novel.core.ui.StateHolderImpl
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.StateFlow

@Stable
class BookDetailStateAdapter(
    stateFlow: StateFlow<BookDetailState>
) : StateAdapter<BookDetailState>(stateFlow) {

    @Composable
    fun bookInfoState(): State<BookDetailState.BookInfo?> =
        createStableState { it.bookInfo }

    @Composable
    fun lastChapterState(): State<BookDetailState.LastChapter?> =
        createStableState { it.lastChapter }

    @Composable
    fun reviewsState(): State<ImmutableList<BookDetailState.BookReview>> =
        createStableState { it.reviews }

    @Composable
    fun isDescriptionExpandedState(): State<Boolean> =
        createStableState { it.isDescriptionExpanded }

    @Composable
    fun isInBookshelfState(): State<Boolean> =
        createStableState { it.isInBookshelf }

    @Composable
    fun isAuthorFollowedState(): State<Boolean> =
        createStableState { it.isAuthorFollowed }

    @Composable
    fun currentBookIdState(): State<String?> =
        createStableState { it.currentBookId }

    @Composable
    fun bookNameState(): State<String?> =
        createStableState { it.bookInfo?.bookName }

    @Composable
    fun authorNameState(): State<String?> =
        createStableState { it.bookInfo?.authorName }

    @Composable
    fun bookDescState(): State<String?> =
        createStableState { it.bookInfo?.bookDesc }

    @Composable
    fun picUrlState(): State<String?> =
        createStableState { it.bookInfo?.picUrl }

    @Composable
    fun visitCountState(): State<Long> =
        createStableState { it.bookInfo?.visitCount ?: 0L }

    @Composable
    fun wordCountState(): State<Int> =
        createStableState { it.bookInfo?.wordCount ?: 0 }

    @Composable
    fun categoryNameState(): State<String?> =
        createStableState { it.bookInfo?.categoryName }

    @Composable
    fun hasBookInfoState(): State<Boolean> =
        createStableState { it.bookInfo != null }

    @Composable
    fun lastChapterNameState(): State<String?> =
        createStableState { it.lastChapter?.chapterName }

    @Composable
    fun lastChapterUpdateTimeState(): State<String?> =
        createStableState { it.lastChapter?.chapterUpdateTime }

    @Composable
    fun hasLastChapterState(): State<Boolean> =
        createStableState { it.lastChapter != null }

    @Composable
    fun reviewCountState(): State<Int> =
        createStableState { it.reviews.size }

    @Composable
    fun hasReviewsState(): State<Boolean> =
        createStableState { it.reviews.isNotEmpty() }

    @Composable
    fun averageRatingState(): State<Float> =
        createStableState {
            val reviews = it.reviews
            if (reviews.isEmpty()) 0f else reviews.map { review -> review.rating }.average().toFloat()
        }

    @Composable
    fun highRatingReviewsState(): State<ImmutableList<BookDetailState.BookReview>> =
        createStableState {
            it.reviews.filter { review -> review.rating >= 4 }.toImmutableList()
        }

    @Composable
    fun latestReviewsState(): State<ImmutableList<BookDetailState.BookReview>> =
        createStableState {
            it.reviews.take(3).toImmutableList()
        }

    fun canStartReading(): Boolean {
        val state = getCurrentSnapshot()
        return state.bookInfo != null && !state.isLoading
    }

    fun canAddToBookshelf(): Boolean {
        val state = getCurrentSnapshot()
        return state.bookInfo != null && !state.isInBookshelf && !state.isLoading
    }

    fun canRemoveFromBookshelf(): Boolean {
        val state = getCurrentSnapshot()
        return state.bookInfo != null && state.isInBookshelf && !state.isLoading
    }

    fun canFollowAuthor(): Boolean {
        val state = getCurrentSnapshot()
        return state.bookInfo != null && !state.isAuthorFollowed && !state.isLoading
    }

    fun canShareBook(): Boolean {
        val state = getCurrentSnapshot()
        return state.bookInfo != null && !state.isLoading
    }

    fun getBookshelfActionText(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "处理中..."
            state.isInBookshelf -> "移出书架"
            else -> "加入书架"
        }
    }

    fun getFollowAuthorActionText(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "处理中..."
            state.isAuthorFollowed -> "已关注"
            else -> "关注作者"
        }
    }

    fun getReadButtonText(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "加载中..."
            state.hasError -> "重新加载"
            state.lastChapter != null -> "开始阅读"
            else -> "暂无章节"
        }
    }

    fun getDescriptionToggleText(): String {
        return if (getCurrentSnapshot().isDescriptionExpanded) "收起" else "展开"
    }

    fun getBookStatsText(): String {
        val state = getCurrentSnapshot()
        val bookInfo = state.bookInfo ?: return "暂无统计信息"
        return "阅读量：${formatCount(bookInfo.visitCount)} | 字数：${formatWordCount(bookInfo.wordCount)}"
    }

    fun getReviewSummaryText(): String {
        val reviews = getCurrentSnapshot().reviews
        if (reviews.isEmpty()) {
            return "暂无评价"
        }

        val avgRating = reviews.map { it.rating }.average().toFloat()
        return if (reviews.size == 1) {
            "1条评价"
        } else {
            "${reviews.size}条评价，平均${String.format("%.1f", avgRating)}星"
        }
    }

    fun getLastChapterInfoText(): String {
        val lastChapter = getCurrentSnapshot().lastChapter
        return if (lastChapter != null) {
            "最新：${lastChapter.chapterName}"
        } else {
            "暂无章节"
        }
    }

    fun getUpdateTimeText(): String {
        val lastChapter = getCurrentSnapshot().lastChapter
        return if (lastChapter != null) {
            "更新于 ${lastChapter.chapterUpdateTime}"
        } else {
            "暂无更新"
        }
    }

    fun getBookDetailStatusSummary(): String {
        val state = getCurrentSnapshot()
        return when {
            state.isLoading -> "加载中"
            state.hasError -> "加载失败"
            state.isEmpty -> "暂无数据"
            state.bookInfo != null -> "加载完成"
            else -> "未知状态"
        }
    }

    fun shouldShowEmptyState(): Boolean {
        val state = getCurrentSnapshot()
        return !state.isLoading && !state.hasError && state.bookInfo == null
    }

    fun shouldShowRetryButton(): Boolean {
        val state = getCurrentSnapshot()
        return state.hasError && !state.isLoading
    }

    private fun formatCount(count: Long): String {
        return when {
            count >= 10000 -> "${count / 10000}万"
            count >= 1000 -> "${count / 1000}千"
            else -> count.toString()
        }
    }

    private fun formatWordCount(wordCount: Int): String {
        return when {
            wordCount >= 10000 -> "${wordCount / 10000}万字"
            wordCount >= 1000 -> "${wordCount / 1000}千字"
            else -> "${wordCount}字"
        }
    }

    fun toUiState(): StateHolderImpl<BookDetailUiState> {
        val state = getCurrentSnapshot()
        return StateHolderImpl(
            data = BookDetailUiState(
                bookInfo = state.bookInfo?.let { bookInfo ->
                    BookDetailUiState.BookInfo(
                        id = bookInfo.id,
                        bookName = bookInfo.bookName,
                        authorName = bookInfo.authorName,
                        bookDesc = bookInfo.bookDesc,
                        picUrl = bookInfo.picUrl,
                        visitCount = bookInfo.visitCount,
                        wordCount = bookInfo.wordCount,
                        categoryName = bookInfo.categoryName
                    )
                },
                lastChapter = state.lastChapter?.let { lastChapter ->
                    BookDetailUiState.LastChapter(
                        chapterName = lastChapter.chapterName,
                        chapterUpdateTime = lastChapter.chapterUpdateTime
                    )
                },
                reviews = state.reviews.map { review ->
                    BookDetailUiState.BookReview(
                        id = review.id,
                        content = review.content,
                        rating = review.rating,
                        readTime = review.readTime,
                        userName = review.userName
                    )
                }.toImmutableList(),
                isDescriptionExpanded = state.isDescriptionExpanded
            ),
            isLoading = state.isLoading,
            error = state.error
        )
    }
}

fun StateFlow<BookDetailState>.asBookDetailAdapter(): BookDetailStateAdapter {
    return BookDetailStateAdapter(this)
}

@Stable
data class BookDetailScreenState(
    val isLoading: Boolean,
    val error: String?,
    val bookInfo: BookDetailState.BookInfo?,
    val lastChapter: BookDetailState.LastChapter?,
    val reviews: ImmutableList<BookDetailState.BookReview>,
    val isDescriptionExpanded: Boolean,
    val isInBookshelf: Boolean,
    val isAuthorFollowed: Boolean,
    val canStartReading: Boolean,
    val canAddToBookshelf: Boolean,
    val canRemoveFromBookshelf: Boolean,
    val canFollowAuthor: Boolean,
    val canShareBook: Boolean,
    val bookshelfActionText: String,
    val followAuthorActionText: String,
    val readButtonText: String,
    val descriptionToggleText: String,
    val bookStatsText: String,
    val reviewSummaryText: String,
    val lastChapterInfoText: String,
    val updateTimeText: String,
    val bookDetailStatusSummary: String,
    val shouldShowEmptyState: Boolean,
    val shouldShowRetryButton: Boolean
)

fun BookDetailStateAdapter.toScreenState(): BookDetailScreenState {
    val snapshot = getCurrentSnapshot()
    return BookDetailScreenState(
        isLoading = snapshot.isLoading,
        error = snapshot.error,
        bookInfo = snapshot.bookInfo,
        lastChapter = snapshot.lastChapter,
        reviews = snapshot.reviews,
        isDescriptionExpanded = snapshot.isDescriptionExpanded,
        isInBookshelf = snapshot.isInBookshelf,
        isAuthorFollowed = snapshot.isAuthorFollowed,
        canStartReading = canStartReading(),
        canAddToBookshelf = canAddToBookshelf(),
        canRemoveFromBookshelf = canRemoveFromBookshelf(),
        canFollowAuthor = canFollowAuthor(),
        canShareBook = canShareBook(),
        bookshelfActionText = getBookshelfActionText(),
        followAuthorActionText = getFollowAuthorActionText(),
        readButtonText = getReadButtonText(),
        descriptionToggleText = getDescriptionToggleText(),
        bookStatsText = getBookStatsText(),
        reviewSummaryText = getReviewSummaryText(),
        lastChapterInfoText = getLastChapterInfoText(),
        updateTimeText = getUpdateTimeText(),
        bookDetailStatusSummary = getBookDetailStatusSummary(),
        shouldShowEmptyState = shouldShowEmptyState(),
        shouldShowRetryButton = shouldShowRetryButton()
    )
}

@Stable
data class BookDetailUiState(
    val bookInfo: BookInfo? = null,
    val lastChapter: LastChapter? = null,
    val reviews: ImmutableList<BookReview> = persistentListOf(),
    val isDescriptionExpanded: Boolean = false
) {
    @Immutable
    data class BookInfo(
        val id: String,
        val bookName: String,
        val authorName: String,
        val bookDesc: String,
        val picUrl: String,
        val visitCount: Long,
        val wordCount: Int,
        val categoryName: String
    )

    @Immutable
    data class LastChapter(
        val chapterName: String,
        val chapterUpdateTime: String
    )

    @Immutable
    data class BookReview(
        val id: String,
        val content: String,
        val rating: Int,
        val readTime: String,
        val userName: String
    )
}
