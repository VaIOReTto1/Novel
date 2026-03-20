package com.novel.page.read.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.novel.page.component.NovelText
import com.novel.page.read.LocalReaderInfo
import com.novel.page.read.viewmodel.ReaderMappingHelper
import com.novel.utils.ssp

/**
 * 阅读器导航信息组件
 * 显示在左上角的章节信息和导航按钮
 */
@Composable
fun ReaderNavigationInfo(
    chapterName: String?,     // 当前章节信息
    modifier: Modifier = Modifier
) {
    if (chapterName == null) return
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        NovelText(
            text = chapterName,
            color = Color.Gray.copy(alpha = 0.8f),
            fontSize = 10.ssp
        )
    }
}

/**
 * 阅读器页面信息组件 - 最优化版本
 * 显示在左下角的页码信息 - 支持全局页码和计算中状态，实时更新绝对页码
 * 统一管理所有翻页模式的页码显示逻辑
 */
@Composable
fun ReaderPageInfo(
    modifier: Modifier = Modifier,
    currentChapterIndex: Int? = null, // 外部传入的当前章节索引
    totalChapters: Int? = null // 外部传入的总章节数
) {
    val readerInfo = LocalReaderInfo.current
    val isCalculating = readerInfo.paginationState.isCalculating

    // 计算总页数
    val totalPages by remember(readerInfo.pageCountCache, readerInfo.paginationState) {
        derivedStateOf {
            ReaderMappingHelper.totalPages(readerInfo)
        }
    }

    // 计算当前全书绝对页码
    val currentGlobalPage by remember(
        readerInfo.pageCountCache, 
        readerInfo.currentChapter, 
        readerInfo.perChapterPageIndex,
        currentChapterIndex,
        totalChapters
    ) {
        derivedStateOf {
            ReaderMappingHelper.calculateGlobalPageNumber(
                readerInfo = readerInfo,
                currentChapterIndex = currentChapterIndex,
                totalChapters = totalChapters
            )
        }
    }

    // 构建页码信息字符串
    val pageInfo by remember(isCalculating, currentGlobalPage, totalPages) {
        derivedStateOf {
            ReaderMappingHelper.buildPageInfoText(
                readerInfo = readerInfo,
                currentChapterIndex = currentChapterIndex,
                totalChapters = totalChapters,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        NovelText(
            text = pageInfo,
            color = Color.Gray.copy(alpha = 0.8f),
            fontSize = 10.ssp
        )
    }
}
