package com.novel.page.search.component

import com.novel.utils.TimberLogger
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 搜索历史记录区域组件
 *
 * 显示用户搜索历史记录，支持展开/收起和清空所有历史
 * 超过4条记录时显示展开/收起按钮，提供删除所有历史功能
 *
 * @param history 历史记录列表
 * @param isExpanded 是否展开显示所有历史
 * @param onHistoryClick 历史记录点击回调
 * @param onToggleExpansion 展开/收起切换回调
 */
@Composable
fun SearchHistorySection(
    history: ImmutableList<String>,
    isExpanded: Boolean,
    onHistoryClick: (String) -> Unit,
    onToggleExpansion: () -> Unit
) {
    val TAG = "SearchHistorySection"

    // 记录历史记录状态
    TimberLogger.d(TAG, "渲染搜索历史: 总数=${history.size}, 展开状态=$isExpanded")

    val toggleExpand = remember(onToggleExpansion, isExpanded) {
        {
            TimberLogger.d(TAG, "切换历史展开状态: $isExpanded -> ${!isExpanded}")
            onToggleExpansion()
        }
    }
    val clearAll = remember(onHistoryClick) {
        {
            TimberLogger.d(TAG, "清空所有搜索历史")
            onHistoryClick("")
        }
    }

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
                            onClick = toggleExpand
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
                            onClick = clearAll
                        )
                        .padding(start = 10.wdp),
                    contentDescription = "删除所有搜索历史",
                )
            }
        }

        Spacer(modifier = Modifier.height(8.wdp))

        // 历史记录网格
        HistoryGrid(
            history = if (isExpanded) history.toImmutableList() else history.take(4)
                .toImmutableList(),
            onItemClick = onHistoryClick
        )
    }
}


/**
 * 搜索历史记录网格组件
 *
 * 使用两列固定布局展示历史记录，每条记录占一格
 * 自动处理奇数个条目的布局对齐
 *
 * @param history 要显示的历史记录列表
 * @param onItemClick 单个历史记录点击回调
 * @param modifier 修饰符
 */
@Composable
fun HistoryGrid(
    history: ImmutableList<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "HistoryGrid"

    // 记录网格渲染信息
    TimberLogger.v(TAG, "渲染历史网格: ${history.size}条记录")

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
                        onClick = {
                            TimberLogger.d(TAG, "点击历史记录: ${rowItems[0]}")
                            onItemClick(rowItems[0])
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // 第二列
                if (rowItems.size > 1) {
                    HistoryItem(
                        text = rowItems[1],
                        onClick = {
                            TimberLogger.d(TAG, "点击历史记录: ${rowItems[1]}")
                            onItemClick(rowItems[1])
                        },
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
 * 单个历史记录项组件
 *
 * 显示单条搜索历史，支持文本溢出省略显示
 *
 * @param text 历史记录文本内容
 * @param onClick 点击回调
 * @param modifier 修饰符
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
