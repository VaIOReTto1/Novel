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
import com.novel.page.read.viewmodel.ReaderInfo
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
            if (readerInfo.pageCountCache != null) {
                readerInfo.pageCountCache.totalPages
            } else {
                readerInfo.paginationState.estimatedTotalPages.takeIf { it > 0 } ?: 1
            }
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
            calculateGlobalPageNumber(
                readerInfo = readerInfo,
                currentChapterIndex = currentChapterIndex,
                totalChapters = totalChapters
            )
        }
    }

    // 构建页码信息字符串
    val pageInfo by remember(isCalculating, currentGlobalPage, totalPages) {
        derivedStateOf {
            when {
                isCalculating && totalPages > 0 -> "$currentGlobalPage / $totalPages (计算中...)"
                isCalculating -> "页数计算中..."
                totalPages > 0 -> "$currentGlobalPage / $totalPages"
                else -> "1 / 1" // 默认显示
            }
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

/**
 * 计算全书绝对页码的逻辑
 */
private fun calculateGlobalPageNumber(
    readerInfo: ReaderInfo,
    currentChapterIndex: Int?,
    totalChapters: Int?
): Int {
    // 优先使用页码缓存数据
    if (readerInfo.pageCountCache != null && readerInfo.currentChapter != null) {
        val chapterRange = readerInfo.pageCountCache.chapterPageRanges.find { 
            it.chapterId == readerInfo.currentChapter.id 
        }
        if (chapterRange != null) {
            val pageIndexInChapter = readerInfo.perChapterPageIndex.coerceAtLeast(0)
            val totalPages = readerInfo.pageCountCache.totalPages
            return (chapterRange.startPage + pageIndexInChapter + 1).coerceIn(1, totalPages)
        }
    }
    
    // 如果没有缓存数据，使用估算
    if (currentChapterIndex != null && totalChapters != null) {
        val estimatedPagesPerChapter = 5 // 假设每章平均5页
        val pageIndexInChapter = readerInfo.perChapterPageIndex.coerceAtLeast(0)
        return (currentChapterIndex * estimatedPagesPerChapter + pageIndexInChapter + 1).coerceAtLeast(1)
    }
    
    // 最后的兜底方案
    return (readerInfo.perChapterPageIndex + 1).coerceAtLeast(1)
}