package com.novel.page.search.component

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.novel.page.component.RankingNumber
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

/**
 * 推荐榜单区域组件
 * 
 * 水平滚动显示多个榜单，包括点击榜、推荐榜、新书榜等
 * 每个榜单显示前15条记录，支持查看完整榜单
 * 
 * @param novelRanking 点击榜数据
 * @param dramaRanking 推荐榜数据  
 * @param newBookRanking 新书榜数据
 * @param onRankingItemClick 榜单项点击回调
 * @param onViewFullRanking 查看完整榜单回调
 */
@Composable
fun RankingSection(
    novelRanking: ImmutableList<SearchRankingItem>,
    dramaRanking: ImmutableList<SearchRankingItem>,
    newBookRanking: ImmutableList<SearchRankingItem>,
    onRankingItemClick: (Long) -> Unit,
    onViewFullRanking: (String) -> Unit
) {
    val TAG = "RankingSection"
    
    // 记录榜单数据状态
    TimberLogger.d(TAG, "渲染榜单区域 - 点击榜:${novelRanking.size}项, 推荐榜:${dramaRanking.size}项, 新书榜:${newBookRanking.size}项")

    val viewClick = remember(onViewFullRanking) {
        {
            TimberLogger.d(TAG, "查看完整点击榜")
            onViewFullRanking("点击榜")
        }
    }

    val viewNewBook = remember(onViewFullRanking) {
        {
            TimberLogger.d(TAG, "查看完整新书榜")
            onViewFullRanking("新书榜")
        }
    }

    val viewDrama = remember(onViewFullRanking) {
        {
            TimberLogger.d(TAG, "查看完整推荐榜")
            onViewFullRanking("推荐榜")
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.wdp),
    ) {
        item(key = "spacer_start") {
            Spacer(modifier = Modifier.width(8.wdp))
        }
        item(key = "ranking_novel") {
            RankingSectionItem(
                title = "点击榜",
                items = novelRanking,
                onItemClick = onRankingItemClick,
                onViewFullRanking = viewClick
            )
        }
        item(key = "ranking_drama") {
            RankingSectionItem(
                title = "推荐榜",
                items = dramaRanking,
                onItemClick = onRankingItemClick,
                onViewFullRanking = viewDrama
            )
        }
        item(key = "ranking_newbook") {
            RankingSectionItem(
                title = "新书榜",
                items = newBookRanking,
                onItemClick = onRankingItemClick,
                onViewFullRanking = viewNewBook
            )
        }
        item(key = "spacer_end") {
            Spacer(modifier = Modifier.width(8.wdp))
        }
    }
}

/**
 * 单个榜单区域组件
 * 
 * 显示特定类型的排行榜，包含标题和排行列表
 * 使用圆角背景卡片样式展示
 * 
 * @param title 榜单标题
 * @param items 榜单数据列表
 * @param onItemClick 榜单项点击回调
 * @param onViewFullRanking 查看完整榜单回调
 */
@Composable
private fun RankingSectionItem(
    title: String,
    items: ImmutableList<SearchRankingItem>,
    onItemClick: (Long) -> Unit,
    onViewFullRanking: () -> Unit
) {
    val TAG = "RankingSectionItem"
    
    // 记录单个榜单渲染
    TimberLogger.v(TAG, "渲染榜单: $title, 包含${items.size}项")
    
    Column(
        modifier = Modifier
            .width(225.wdp)
            .background(
                color = NovelColors.NovelBookBackground,
                shape = RoundedCornerShape(8.wdp)
            )
            .padding(vertical = 20.wdp, horizontal = 12.wdp)
    ) {
        // 榜单标题
        NovelText(
            text = title,
            fontSize = 16.ssp,
            fontWeight = FontWeight.Bold,
            color = NovelColors.NovelText,
            modifier = Modifier.padding(bottom = 12.wdp)
        )

        // 榜单列表
        RankingList(
            items = items,
            onItemClick = onItemClick,
            onViewFullRanking = onViewFullRanking
        )
    }
}

/**
 * 排行榜项数据类
 * 
 * 榜单中单个条目的数据结构
 * 
 * @param id 书籍ID
 * @param title 书籍标题
 * @param author 作者名字
 * @param rank 排名序号
 */
@Stable
data class SearchRankingItem(
    val id: Long,
    val title: String,
    val author: String,
    val rank: Int
)


/**
 * 榜单列表组件
 * 
 * 显示排行榜的文本列表格式：序号 + 标题 + 作者名字
 * 最多显示15条记录，超过时提供"查看完整榜单"链接
 * 
 * @param items 榜单数据列表
 * @param onItemClick 榜单项点击回调
 * @param onViewFullRanking 查看完整榜单回调
 * @param modifier 修饰符
 */
@Composable
fun RankingList(
    items: ImmutableList<SearchRankingItem>,
    onItemClick: (Long) -> Unit,
    onViewFullRanking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "RankingList"
    
    // 记录列表渲染状态
    TimberLogger.v(TAG, "渲染榜单列表: ${items.size}项，显示前${minOf(15, items.size)}项")
    
    Column(
        modifier = modifier.padding(horizontal = 8.wdp),
        verticalArrangement = Arrangement.spacedBy(4.wdp)
    ) {
        items.take(15).forEach { item ->
            val click = remember(onItemClick, item.id) {
                {
                    TimberLogger.d("RankingListItem", "点击榜单项: ${item.title} (ID:${item.id}, 排名:${item.rank})")
                    onItemClick(item.id)
                }
            }
            RankingListItem(
                item = item,
                onClick = click
            )
        }
        
        // 如果超过15个且提供了完整榜单回调，显示"查看完整榜单>"链接
        if (items.size > 15) {
            Spacer(modifier = Modifier.height(4.wdp))
            NovelText(
                text = "查看完整榜单 >",
                fontSize = 13.ssp,
                fontWeight = FontWeight.Medium,
                color = NovelColors.NovelMain,
                modifier = Modifier
                    .fillMaxWidth()
                    .debounceClickable(onClick = {
                        TimberLogger.d(TAG, "点击查看完整榜单，总共${items.size}项")
                        onViewFullRanking()
                    })
                    .padding(vertical = 8.wdp, horizontal = 4.wdp)
            )
        }
    }
}

/**
 * 单个榜单列表项组件
 * 
 * 显示排行榜中的单个条目，包含排名、书名和作者
 * 支持文本溢出时的省略显示
 * 
 * @param item 榜单项数据
 * @param onClick 点击回调
 * @param modifier 修饰符
 */
@Composable
private fun RankingListItem(
    item: SearchRankingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .debounceClickable(onClick = onClick),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.wdp)
    ) {
        // 排名序号
        RankingNumber(
            rank = item.rank,
        )

        // 标题和作者信息
        Column {
            // 书籍标题
            NovelText(
                text = item.title,
                fontSize = 13.ssp,
                lineHeight = 13.ssp,
                fontWeight = FontWeight.Medium,
                color = NovelColors.NovelText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // 作者名字
            NovelText(
                text = item.author,
                fontSize = 12.ssp,
                fontWeight = FontWeight.Normal,
                color = NovelColors.NovelTextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
