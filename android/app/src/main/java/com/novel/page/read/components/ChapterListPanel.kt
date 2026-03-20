package com.novel.page.read.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novel.page.read.viewmodel.Chapter
import com.novel.ui.theme.NovelColors
import com.novel.utils.wdp
import kotlinx.collections.immutable.ImmutableList

/**
 * 章节列表侧滑面板组件
 * 
 * 从底部弹起的章节目录面板，支持章节选择和VIP标识
 * 自动滚动到当前阅读章节
 * 
 * @param chapters 章节列表数据
 * @param currentChapterId 当前正在阅读的章节ID
 * @param backgroundColor 面板背景颜色
 * @param onChapterSelected 章节选择回调
 * @param onDismiss 关闭面板回调
 * @param modifier 修饰符
 */
@Composable
fun ChapterListPanel(
    chapters: ImmutableList<Chapter>,
    currentChapterId: String,
    backgroundColor: Color,
    onChapterSelected: (Chapter) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val TAG = "ChapterListPanel"
    val listState = rememberLazyListState()
    
    // 自动滚动到当前章节
    LaunchedEffect(currentChapterId, chapters) {
        val currentIndex = chapters.indexOfFirst { it.id == currentChapterId }
        if (currentIndex >= 0) {
            TimberLogger.d(TAG, "自动滚动到章节: $currentIndex")
            listState.scrollToItem(currentIndex)
        }
    }
    
    // 从下方弹起的布局，占据屏幕高度的75%
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
            .background(
                backgroundColor,
                RoundedCornerShape(topStart = 16.wdp, topEnd = 16.wdp)
            )
            .clip(RoundedCornerShape(topStart = 16.wdp, topEnd = 16.wdp))
    ) {
        Column {
            // 标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.wdp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "目录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NovelColors.NovelText
                )
                
                // 性能优化：缓存关闭回调，避免每次重组都创建新 Lambda
                val onDismissClick = remember(onDismiss) { {
                    TimberLogger.d(TAG, "关闭章节列表面板")
                    onDismiss()
                } }
                
                IconButton(
                    onClick = onDismissClick,
                    modifier = Modifier.size(24.wdp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = Color.Gray.copy(alpha = 0.7f)
                    )
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.3f)
            )
            
            // 章节列表
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.wdp)
            ) {
                items(chapters, key = { it.id }) { chapter ->
                    // 性能优化：缓存点击回调，避免每次重组都创建 Lambda
                    val onChapterClick = remember(chapter.id, onChapterSelected) { {
                        TimberLogger.d(TAG, "选择章节: ${chapter.chapterName}")
                        onChapterSelected(chapter) 
                    } }
                    
                    ChapterItem(
                        chapter = chapter,
                        isSelected = chapter.id == currentChapterId,
                        onClick = onChapterClick
                    )
                }
            }
        }
    }
}

/**
 * 单个章节项组件
 * 
 * 显示章节名称、VIP标识和选中状态
 * 
 * @param chapter 章节数据
 * @param isSelected 是否为当前选中章节
 * @param onClick 点击回调
 */
@Composable
private fun ChapterItem(
    chapter: Chapter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        NovelColors.NovelMain.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }
    
    val textColor = if (isSelected) {
        NovelColors.NovelMain
    } else {
        NovelColors.NovelText
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.wdp, vertical = 12.wdp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // VIP标识
        if (chapter.isVip == "1") {
            Card(
                modifier = Modifier
                    .size(20.wdp)
                    .clip(RoundedCornerShape(4.wdp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "VIP",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(8.wdp))
        }
        
        // 章节名称
        Text(
            text = chapter.chapterName,
            fontSize = 14.sp,
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        
        // 当前章节指示器
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(8.wdp)
                    .background(
                        NovelColors.NovelMain,
                        RoundedCornerShape(4.wdp)
                    )
            )
        }
    }
}
