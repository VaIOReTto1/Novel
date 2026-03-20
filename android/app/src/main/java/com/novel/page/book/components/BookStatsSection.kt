package com.novel.page.book.components

import com.novel.utils.TimberLogger
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.novel.page.book.utils.formatWordCount
import com.novel.page.book.viewmodel.BookDetailUiState
import com.novel.page.component.NovelText
import com.novel.ui.theme.NovelColors
import com.novel.utils.NovelDateFormatter
import com.novel.utils.ssp
import com.novel.utils.wdp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 简单的ViewModel用于为Composable提供NovelDateFormatter实例
 * 避免在Composable中直接注入依赖
 */
@HiltViewModel
class DateFormatterViewModel @Inject constructor(
    val dateFormatter: NovelDateFormatter
) : ViewModel()

/**
 * 书籍统计信息展示组件
 * 显示评分、阅读人数、字数和更新时间等信息
 */
@Composable
fun BookStatsSection(
    bookInfo: BookDetailUiState.BookInfo?,
    lastChapter: BookDetailUiState.LastChapter?,
    dateFormatter: NovelDateFormatter = hiltViewModel<DateFormatterViewModel>().dateFormatter
) {
    val TAG = "BookStatsSection"

    // 空数据保护
    if (bookInfo == null) {
        TimberLogger.w(TAG, "BookInfo为空，跳过渲染")
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.wdp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 评分
        StatsItem(
            value = "9.4分",
            subtitle = "${formatWordCount(bookInfo.visitCount.toInt())}人点评中"
        )

        // 分隔线
        Box(
            modifier = Modifier
                .width(0.3.wdp)
                .height(23.wdp)
                .background(NovelColors.NovelTextGray)
        )

        // 正在阅读
        StatsItem(
            value = "${formatWordCount(bookInfo.visitCount.toInt())}人",
            subtitle = "正在阅读"
        )

        // 分隔线
        Box(
            modifier = Modifier
                .width(0.3.wdp)
                .height(23.wdp)
                .background(NovelColors.NovelTextGray)
        )

        // 字数和更新时间 - 使用注入的 NovelDateFormatter
        val updateTime = lastChapter?.chapterUpdateTime?.let { 
            dateFormatter.parseNewsDate(it) 
        }?.takeIf { it.isNotEmpty() } ?: "14小时前更新"
        
        StatsItem(
            value = "${formatWordCount(bookInfo.wordCount)}字",
            subtitle = updateTime
        )
        
        TimberLogger.v(TAG, "展示统计信息 - 访问量: ${bookInfo.visitCount}, 字数: ${bookInfo.wordCount}")
    }
}

/**
 * 统计项组件
 * 显示数值和描述信息，支持数字和单位分离显示
 */
@Composable
private fun StatsItem(
    value: String,
    subtitle: String
) {
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        // 使用正则表达式分离数字与单位
        val match = Regex("^([0-9.]+)(.*)$").find(value)
        val number = match?.groupValues?.get(1) ?: value
        val unit = match?.groupValues?.get(2) ?: ""

        Row(verticalAlignment = Alignment.Bottom) {
            NovelText(
                text = number,
                fontSize = 14.ssp,
                lineHeight = 16.ssp,
                fontWeight = FontWeight.Bold,
                color = NovelColors.NovelText,
                modifier = Modifier.padding(end = 2.wdp)
            )
            if (unit.isNotEmpty()) {
                NovelText(
                    text = unit,
                    fontSize = 10.ssp,
                    lineHeight = 10.ssp,
                    fontWeight = FontWeight.Bold,
                    color = NovelColors.NovelText,
                    modifier = Modifier.padding(bottom = 1.wdp)
                )
            }
        }
        if (subtitle.isNotEmpty()) {
            NovelText(
                text = subtitle,
                fontSize = 10.ssp,
                lineHeight = 10.ssp,
                color = NovelColors.NovelTextGray
            )
        }
    }
}
