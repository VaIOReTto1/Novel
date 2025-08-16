package com.novel.page.book.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList

/**
 * 书评区域组件
 * 展示热门书评列表，包含评分和阅读时长
 */
@Composable
fun BookReviewsSection(
    reviews: ImmutableList<BookDetailUiState.BookReview>,
    onMoreReviewsClick: () -> Unit = {}
) {
    val TAG = "BookReviewsSection"

    // 记录书评数量
    if (reviews.isNotEmpty()) {
        TimberLogger.d(TAG, "展示 ${reviews.size} 条书评")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.wdp)
    ) {
        // 标题行，包含"热门书评"和"更多书评"按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NovelText(
                text = "热门书评",
                fontSize = 18.ssp,
                fontWeight = FontWeight.Bold,
                color = NovelColors.NovelText
            )

            // 更多书评按钮
            Row(
                modifier = Modifier
                    .debounceClickable(
                        onClick = {
                            TimberLogger.d(TAG, "点击更多书评按钮")
                            onMoreReviewsClick()
                        }
                    )
                    .padding(horizontal = 8.wdp, vertical = 4.wdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.wdp)
            ) {
                NovelText(
                    text = "更多书评",
                    fontSize = 14.ssp,
                    color = NovelColors.NovelMain
                )
            }
        }

        reviews.forEach { review ->
            BookReviewItem(review = review)
        }
    }
}

/**
 * 单个书评项组件
 * 包含头像、评论内容、星级评分和阅读时长
 */
@Composable
private fun BookReviewItem(review: BookDetailUiState.BookReview) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.wdp),
        horizontalArrangement = Arrangement.spacedBy(7.wdp)
    ) {
        // 评论者头像（实心圆圈）
        Box(
            modifier = Modifier
                .padding(top = 2.wdp)
                .size(30.wdp)
                .background(NovelColors.NovelTextGray, CircleShape)
        )

        // 评论内容
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 评论文本
            NovelText(
                text = review.content,
                fontSize = 12.ssp,
                color = NovelColors.NovelText.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 星级评分
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.wdp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 渲染星级评分
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) NovelColors.NovelText.copy(alpha = 0.8f) else NovelColors.NovelTextGray,
                        modifier = Modifier.size(12.wdp)
                    )
                }
                // 阅读时长
                NovelText(
                    text = review.readTime,
                    fontSize = 12.ssp,
                    color = NovelColors.NovelTextGray,
                    modifier = Modifier.padding(start = 4.wdp)
                )
            }
        }
    }
}
