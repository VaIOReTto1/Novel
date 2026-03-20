package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.novel.page.component.NovelText
import com.novel.page.read.viewmodel.PageData
import com.novel.utils.HtmlTextUtil
import com.novel.utils.ssp
import com.novel.utils.wdp
import com.novel.page.book.components.BookCoverSection
import com.novel.page.book.components.BookTitleSection
import com.novel.page.book.components.AuthorSection
import com.novel.page.book.components.BookStatsSection
import com.novel.page.book.components.BookDescriptionSection
import com.novel.page.book.components.BookReviewsSection
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.NavViewModel
import com.novel.utils.debounceClickable
import kotlinx.collections.immutable.toImmutableList
import com.novel.page.read.viewmodel.BookReview
import kotlinx.collections.immutable.persistentListOf

/**
 * 通用页面内容显示组件（供PageCurl等新组件使用）
 * 支持第0页显示书籍详情页
 */
@Composable
fun PageContentDisplay(
    page: String,
    chapterName: String,
    isFirstPage: Boolean = false,
    isBookDetailPage: Boolean = false, // 新增：是否是书籍详情页
    bookInfo: PageData.BookInfo? = null, // 新增：书籍信息
    readerSettings: ReaderSettings = ReaderSettings(),
    // 新增：导航到阅读器回调
    showNavigationInfo: Boolean = true, // 新增：是否显示导航信息
    bookReviews: kotlinx.collections.immutable.ImmutableList<BookReview> = persistentListOf(), // 新增：书籍评论
    isLoadingReviews: Boolean = false, // 新增：是否正在加载评论
    onLoadBookReviews: ((String) -> Unit)? = null, // 新增：加载评论回调
    onClick: () -> Unit = {}
) {

    val onMoreReviewsClick = remember(bookInfo) {
        {
            bookInfo?.let { info ->
                NavViewModel.navigateToCommentPageWithBookInfo(
                    bookId = info.bookId,
                    bookName = info.bookName,
                    authorName = info.authorName,
                    picUrl = info.picUrl
                )
            }
            Unit
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(readerSettings.backgroundColor)
    ) {
        // 顶部导航信息 - 只在非书籍详情页且需要显示时显示
        if (!isBookDetailPage && showNavigationInfo) {
            ReaderNavigationInfo(
                chapterName = chapterName,
                modifier = Modifier.padding(start = 12.wdp, top = 12.wdp)
            )
        }

        // 主要内容区域
        if (isBookDetailPage) {
            // 书籍详情页 - 手势由外部容器（如PageFlipContainer）处理
            // 外部容器应根据翻页效果决定如何响应手势
            // 例如，PageCurl会处理自己的卷曲手势，而NoAnimation/Cover会使用自定义手势检测
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .debounceClickable(onClick = onClick)
            ) {
                BookDetailPageContent(
                    bookInfo = bookInfo,
                    bookReviews = bookReviews,
                    isLoadingReviews = isLoadingReviews,
                    onLoadBookReviews = onLoadBookReviews,
                    onMoreReviewsClick = onMoreReviewsClick
                )
            }
        } else {
            // 正常页面内容
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .debounceClickable(onClick = onClick)
                    .padding(horizontal = 16.wdp, vertical = 10.wdp)
            ) {
                NormalPageContent(
                    page = page,
                    chapterName = chapterName,
                    isFirstPage = isFirstPage,
                    readerSettings = readerSettings
                )
            }
        }

        // 底部页面信息 - 只在非书籍详情页且需要显示时显示
        if (!isBookDetailPage && showNavigationInfo) {
            ReaderPageInfo(
                modifier = Modifier.padding(start = 12.wdp, bottom = 3.wdp)
            )
        }
    }
}

/**
 * 书籍详情页内容
 */
@Composable
private fun BookDetailPageContent(
    bookInfo: PageData.BookInfo?,
    bookReviews: kotlinx.collections.immutable.ImmutableList<BookReview>,
    isLoadingReviews: Boolean,
    onLoadBookReviews: ((String) -> Unit)?,
    onMoreReviewsClick: () -> Unit = {}
) {
    if (bookInfo != null) {
        // 如果评论为空且没有正在加载，则触发加载评论
        LaunchedEffect(bookInfo.bookId, bookReviews.isEmpty(), isLoadingReviews) {
            if (bookReviews.isEmpty() && !isLoadingReviews && onLoadBookReviews != null) {
                onLoadBookReviews(bookInfo.bookId)
            }
        }
        
        BookDetailContent(
            bookInfo = bookInfo,
            bookReviews = bookReviews,
            isLoadingReviews = isLoadingReviews,
            onMoreReviewsClick = onMoreReviewsClick
        )
    }
}

/**
 * 正常页面内容
 */
@Composable
private fun NormalPageContent(
    page: String,
    chapterName: String,
    isFirstPage: Boolean,
    readerSettings: ReaderSettings
) {

    Column {
        // 只在第一页显示章节标题
        if (isFirstPage) {
            NovelText(
                text = chapterName,
                fontSize = (readerSettings.fontSize + 4).ssp,
                fontWeight = FontWeight.Bold,
                color = readerSettings.textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.wdp)
            )
        }

        // 页面内容
        NovelText(
            text = HtmlTextUtil.cleanHtml(page),
            fontSize = readerSettings.fontSize.ssp,
            color = readerSettings.textColor,
            lineHeight = (readerSettings.fontSize * 1.5).ssp
        )
    }
}

/**
 * 书籍详情页内容组件 - 简化版本，用于阅读器中
 */
@Composable
private fun BookDetailContent(
    bookInfo: PageData.BookInfo,
    bookReviews: kotlinx.collections.immutable.ImmutableList<BookReview>,
    isLoadingReviews: Boolean,
    onMoreReviewsClick: () -> Unit = {}
) {
    // 转换为BookDetailUiState.BookInfo格式
    val uiStateBookInfo = BookDetailUiState.BookInfo(
        id = bookInfo.bookId,
        bookName = bookInfo.bookName,
        authorName = bookInfo.authorName,
        bookDesc = bookInfo.bookDesc,
        picUrl = bookInfo.picUrl,
        visitCount = bookInfo.visitCount,
        wordCount = bookInfo.wordCount,
        categoryName = bookInfo.categoryName
    )

    // 模拟最新章节数据
    val lastChapter = BookDetailUiState.LastChapter(
        chapterName = "第一章",
        chapterUpdateTime = "2024-01-01 12:00:00"
    )

    // 转换评论数据格式
    val reviews = bookReviews.map { review ->
        BookDetailUiState.BookReview(
            id = review.id,
            content = review.content,
            rating = review.rating,
            readTime = review.readTime,
            userName = review.userName
        )
    }.toImmutableList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.wdp),
        verticalArrangement = Arrangement.spacedBy(16.wdp)
    ) {
        BookCoverSection(bookInfo = uiStateBookInfo)

        BookTitleSection(bookInfo = uiStateBookInfo)

        AuthorSection(bookInfo = uiStateBookInfo)

        BookStatsSection(
            bookInfo = uiStateBookInfo,
            lastChapter = lastChapter
        )

        BookDescriptionSection(
            description = uiStateBookInfo.bookDesc
        )

        BookReviewsSection(
            reviews = reviews.toImmutableList(),
            onMoreReviewsClick = onMoreReviewsClick
        )
    }
}
