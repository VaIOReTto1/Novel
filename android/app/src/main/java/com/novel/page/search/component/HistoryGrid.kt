package com.novel.page.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

/**
 * 搜索历史记录区域
 */
@Composable
fun SearchHistorySection(
    history: List<String>,
    isExpanded: Boolean,
    onHistoryClick: (String) -> Unit,
    onToggleExpansion: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.wdp)
    ) {
        // 标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NovelText(
                text = "搜索历史",
                fontSize = 16.ssp,
                fontWeight = FontWeight.Bold,
                color = NovelColors.NovelText
            )

            Row(
                horizontalArrangement = Arrangement.End,
            ) {
                if (history.size > 4) {
                    NovelText(
                        modifier = Modifier.debounceClickable(
                            onClick = { onToggleExpansion() }
                        ),
                        text = if (isExpanded) "收起" else "展开",
                        fontSize = 14.ssp,
                        color = NovelColors.NovelMain
                    )
                }
                Icon(
                    Icons.Rounded.Delete,
                    tint = NovelColors.NovelText,
                    modifier = Modifier
                        .debounceClickable(
                            onClick = {
                                onHistoryClick("")
                            }
                        )
                        .padding(start = 10.wdp),
                    contentDescription = "删除所有搜索历史",
                )
            }
        }

        Spacer(modifier = Modifier.height(8.wdp))

        // 历史记录网格
        HistoryGrid(
            history = if (isExpanded) history else history.take(4),
            onItemClick = onHistoryClick
        )
    }
}


/**
 * 搜索历史记录网格组件
 * 使用两列固定布局，每条记录占一格
 */
@Composable
fun HistoryGrid(
    history: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 将历史记录按两列分组
    val chunkedHistory = history.chunked(2)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.wdp)
    ) {
        chunkedHistory.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.wdp)
            ) {
                // 第一列
                if (rowItems.isNotEmpty()) {
                    HistoryItem(
                        text = rowItems[0],
                        onClick = { onItemClick(rowItems[0]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // 第二列
                if (rowItems.size > 1) {
                    HistoryItem(
                        text = rowItems[1],
                        onClick = { onItemClick(rowItems[1]) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

/**
 * 单个历史记录项
 */
@Composable
private fun HistoryItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(160.wdp),
        contentAlignment = Alignment.TopStart
    ) {
        NovelText(
            text = text,
            fontSize = 14.ssp,
            fontWeight = FontWeight.Normal,
            color = NovelColors.NovelText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = modifier
                .clickable { onClick() },
        )
    }
} 