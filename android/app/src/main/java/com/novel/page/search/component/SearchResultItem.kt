package com.novel.page.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelImageView
import com.novel.page.component.NovelText
import com.novel.page.search.utils.SearchUtils
import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlin.random.Random

/**
 * 搜索结果项组件
 */
@Composable
fun SearchResultItem(
    book: BookInfoRespDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .debounceClickable(onClick = onClick)
            .padding(horizontal = 16.wdp, vertical = 12.wdp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左：封面图
            NovelImageView(
                imageUrl = book.picUrl ?: "",
                modifier = Modifier
                    .size(80.wdp, 100.wdp)
                    .clip(RoundedCornerShape(6.wdp))
                    .background(NovelColors.NovelTextGray.copy(alpha = 0.1f)),
                contentScale = ContentScale.Crop,
                placeholderContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NovelColors.NovelTextGray.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        NovelText(
                            text = "📖",
                            fontSize = 16.ssp,
                            color = NovelColors.NovelTextGray
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.width(12.wdp))
            
            // 右：书名、作者、简介、标签行
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 书名
                NovelText(
                    text = book.bookName ?: "未知书名",
                    fontSize = 16.ssp,
                    fontWeight = FontWeight.Bold,
                    color = NovelColors.NovelText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.wdp))
                
                // 作者
                NovelText(
                    text = "作者：${book.authorName ?: "未知作者"}",
                    fontSize = 13.ssp,
                    color = NovelColors.NovelTextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(6.wdp))
                
                // 简介
                NovelText(
                    text = book.bookDesc ?: "暂无简介",
                    fontSize = 13.ssp,
                    lineHeight = 18.ssp,
                    color = NovelColors.NovelText.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.wdp))
                
                // 标签行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.wdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 读者数标签
                    SearchInfoChip(
                        text = "${SearchUtils.formatSearchResultCount(book.visitCount.toInt())}人在读",
                        backgroundColor = NovelColors.NovelMain.copy(alpha = 0.1f),
                        textColor = NovelColors.NovelMain
                    )
                    
                    // 字数标签
                    SearchInfoChip(
                        text = SearchUtils.formatSearchResultCount(book.wordCount),
                        backgroundColor = NovelColors.NovelTextGray.copy(alpha = 0.1f),
                        textColor = NovelColors.NovelTextGray
                    )
                    
                    // 状态标签
                    SearchInfoChip(
                        text = if (book.bookStatus == 1) "已完结" else "连载中",
                        backgroundColor = if (book.bookStatus == 1) 
                            NovelColors.NovelTextGray.copy(alpha = 0.1f) 
                        else 
                            NovelColors.NovelMain.copy(alpha = 0.1f),
                        textColor = if (book.bookStatus == 1) 
                            NovelColors.NovelTextGray 
                        else 
                            NovelColors.NovelMain
                    )
                }
            }
        }
        
        // 分隔线
        Spacer(modifier = Modifier.height(12.wdp))
        HorizontalDivider(
            color = NovelColors.NovelTextGray.copy(alpha = 0.1f),
            thickness = 0.5.wdp
        )
    }
}

/**
 * 搜索信息标签组件
 */
@Composable
private fun SearchInfoChip(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(10.wdp)
            )
            .padding(horizontal = 6.wdp, vertical = 2.wdp),
        contentAlignment = Alignment.Center
    ) {
        NovelText(
            text = text,
            fontSize = 10.ssp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
} 