package com.novel.page.read.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.novel.page.component.NovelText
import com.novel.page.component.PaperTexture
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import com.novel.utils.HtmlTextUtil
import com.novel.utils.ssp
import com.novel.utils.wdp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Composable
private fun ChapterTitleHeader(title: String, readerSettings: ReaderSettings) {
    NovelText(
        text = title,
        fontSize = (readerSettings.fontSize + 4).ssp,
        fontWeight = FontWeight.Bold,
        color = readerSettings.textColor,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(readerSettings.backgroundColor) // Necessary for sticky headers to not be transparent
            .padding(vertical = 16.wdp)
    )
}

@Composable
private fun ChapterContentText(content: String, readerSettings: ReaderSettings, modifier: Modifier = Modifier) {
    NovelText(
        text = HtmlTextUtil.cleanHtml(content),
        fontSize = readerSettings.fontSize.ssp,
        color = readerSettings.textColor,
        lineHeight = (readerSettings.fontSize * 1.5).ssp,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * 上下滚动容器 - 章节无缝衔接版本，包含纸张纹理，支持边界检测和章节切换
 * 优化版：动态更新导航信息，无缝拼接章节内容，实时更新页码和章节信息
 */
@Composable
fun VerticalScrollContainer(
    uiState: ReaderState,
    readerSettings: ReaderSettings,
    onChapterChange: (FlipDirection) -> Unit,
    onVerticalScrollPageChange: (Int) -> Unit,
    onClick: () -> Unit
) {
    val listState = rememberLazyListState()
    val loadedChapters = uiState.loadedChapterData.values.toList()
    
    // 当前显示的章节名称（动态更新）
    var currentDisplayChapterName by remember { mutableStateOf(uiState.currentChapter?.chapterName ?: "") }
    
    // 当前显示的页码信息（实时计算）
    var currentPageInfo by remember { mutableStateOf("1 / 1") }
    var currentAbsolutePage by remember { mutableIntStateOf(1) }

    // 实时同步页面变化到 ViewModel - 添加防抖机制
    var lastReportedPage by remember { mutableIntStateOf(-1) }
    
    // 动态更新当前显示的章节名称和页码信息
    LaunchedEffect(listState) {
        snapshotFlow { 
            listState.layoutInfo.visibleItemsInfo to listState.firstVisibleItemScrollOffset 
        }
            .map { (visibleItems, _) ->
                // 计算当前滚动位置对应的章节和页码
                calculateCurrentPositionInfo(visibleItems, uiState, loadedChapters)
            }
            .distinctUntilChanged()
            .collect { positionInfo ->
                positionInfo?.let { (chapterName, pageInfo, absolutePage) ->
                    currentDisplayChapterName = chapterName
                    currentPageInfo = pageInfo
                    currentAbsolutePage = absolutePage
                    
                    // 实时通知ViewModel当前页码变化（添加防抖避免过于频繁的调用）
                    val pageIndex = absolutePage - 1 // 转换为0-based索引
                    if (pageIndex != lastReportedPage) {
                        lastReportedPage = pageIndex
                        onVerticalScrollPageChange(pageIndex)
                    }
                }
            }
    }

    // 检测滚动边界并自动加载章节
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .filter { it.isNotEmpty() }
            .collect { visibleItems ->
                val firstVisible = visibleItems.first()
                val lastVisible = visibleItems.last()
                val totalItems = listState.layoutInfo.totalItemsCount

                // 检测是否滚动到顶部（需要加载上一章）
                if (firstVisible.index <= 1 && !uiState.isSwitchingChapter && !uiState.isFirstChapter) {
                    onChapterChange(FlipDirection.PREVIOUS)
                }

                // 检测是否滚动到底部（需要加载下一章）
                if (lastVisible.index >= totalItems - 2 && !uiState.isSwitchingChapter && !uiState.isLastChapter) {
                    onChapterChange(FlipDirection.NEXT)
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部导航信息 - 使用动态更新的章节名称
        ReaderNavigationInfo(
            chapterName = currentDisplayChapterName,
            modifier = Modifier.padding(start = 12.wdp, top = 12.wdp)
        )

        // 主要内容区域
        PaperTexture(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            alpha = 0.04f,
            density = 1.0f,
            seed = uiState.bookId.hashCode().toLong()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(readerSettings.backgroundColor)
                    .clickable { onClick() }
                    .padding(horizontal = 16.wdp, vertical = 10.wdp),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Use dp for arrangement
            ) {
                // 上一章加载指示器
                if (uiState.isSwitchingChapter && uiState.chapterList.firstOrNull()?.id != uiState.currentChapter?.id) {
                     item(key = "load_previous_indicator") {
                        LoadingItem(readerSettings = readerSettings, text = "加载上一章中...")
                     }
                }

                // 渲染所有已加载的章节
                items(
                    count = loadedChapters.size,
                    key = { index -> "chapter_${loadedChapters[index].chapterId}" }
                ) { index ->
                    val chapterData = loadedChapters[index]
                    Column {
                        ChapterTitleHeader(
                            title = chapterData.chapterName,
                            readerSettings = readerSettings
                        )
                        ChapterContentText(
                            content = chapterData.content,
                            readerSettings = readerSettings,
                            modifier = Modifier.padding(bottom = 24.wdp)
                        )
                    }
                }


                // 下一章预加载指示器
                if (uiState.isSwitchingChapter && uiState.chapterList.lastOrNull()?.id != uiState.currentChapter?.id) {
                    item(key = "load_next_indicator") {
                        LoadingItem(readerSettings = readerSettings, text = "加载下一章中...")
                    }
                }
            }
        }

        // 底部页面信息 - 显示实时计算的页码信息
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.wdp, bottom = 3.wdp)
        ) {
            NovelText(
                text = currentPageInfo,
                color = Color.Gray.copy(alpha = 0.8f),
                fontSize = 10.ssp
            )
        }
    }
}

@Composable
private fun LoadingItem(readerSettings: ReaderSettings, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.wdp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.wdp)
        ) {
            CircularProgressIndicator(
                color = readerSettings.textColor,
                modifier = Modifier.size(24.wdp)
            )
            NovelText(
                text = text,
                fontSize = 12.ssp,
                color = readerSettings.textColor.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 计算当前滚动位置对应的章节和页码信息
 * 返回 Triple(章节名称, 页码信息, 绝对页码)
 */
private fun calculateCurrentPositionInfo(
    visibleItems: List<LazyListItemInfo>,
    uiState: ReaderState,
    loadedChapters: List<PageData>
): Triple<String, String, Int>? {
    if (visibleItems.isEmpty() || loadedChapters.isEmpty()) return null

    // 找到当前可见的主要章节（占据最大可见面积的章节）
    val mainVisibleChapter = findMainVisibleChapter(visibleItems, loadedChapters)
        ?: return null

    val (chapterData, visibleOffset) = mainVisibleChapter

    // 计算该章节的估算页码
    val estimatedPageInChapter = calculateEstimatedPageInChapter(
        chapterData = chapterData,
        visibleOffset = visibleOffset,
        readerSettings = uiState.readerSettings
    )

    // 计算绝对页码
    val absolutePage = calculateAbsolutePageNumber(
        chapterData = chapterData,
        pageInChapter = estimatedPageInChapter,
        uiState = uiState,
        loadedChapters = loadedChapters
    )

    // 计算总页数
    val totalPages = calculateTotalPages(uiState)

    val pageInfo = "$absolutePage / $totalPages"

    return Triple(chapterData.chapterName, pageInfo, absolutePage)
}

/**
 * 找到占据最大可见面积的章节 - 改进版，支持更精确的滚动偏移计算
 */
private fun findMainVisibleChapter(
    visibleItems: List<LazyListItemInfo>,
    loadedChapters: List<PageData>
): Pair<PageData, Float>? {
    // 找到第一个可见的章节项
    val firstChapterItem = visibleItems.firstOrNull { item ->
        val key = item.key as? String
        key?.startsWith("chapter_") == true
    } ?: return null

    val chapterId = (firstChapterItem.key as String).removePrefix("chapter_")
    val chapterData = loadedChapters.find { it.chapterId == chapterId } ?: return null

    // 计算更精确的可见偏移比例
    val visibleOffset = if (firstChapterItem.size > 0) {
        // 考虑第一个可见项的滚动偏移
        firstChapterItem.size - (-firstChapterItem.offset).coerceAtLeast(0)
        val scrollProgress = (-firstChapterItem.offset).toFloat() / firstChapterItem.size.toFloat()
        scrollProgress.coerceIn(0f, 1f)
    } else 0f

    return Pair(chapterData, visibleOffset)
}

/**
 * 计算章节内的估算页码
 */
private fun calculateEstimatedPageInChapter(
    chapterData: PageData,
    visibleOffset: Float,
    readerSettings: ReaderSettings
): Int {
    // 如果有分页数据，使用分页数据
    if (chapterData.pages.isNotEmpty()) {
        val pageIndex = (visibleOffset * chapterData.pages.size).toInt()
        return (pageIndex + 1).coerceIn(1, chapterData.pages.size)
    }
    
    // 否则使用估算方法
    val estimatedPagesInChapter = estimateChapterPages(chapterData.content, readerSettings)
    val currentPageInChapter = (visibleOffset * estimatedPagesInChapter).toInt() + 1
    return currentPageInChapter.coerceIn(1, estimatedPagesInChapter)
}

/**
 * 估算章节页数
 */
private fun estimateChapterPages(content: String, readerSettings: ReaderSettings): Int {
    // 基于内容长度和字体大小的简单估算
    val cleanContent = HtmlTextUtil.cleanHtml(content)
    val charCount = cleanContent.length
    val baseCharsPerPage = when {
        readerSettings.fontSize <= 14 -> 800
        readerSettings.fontSize <= 18 -> 600
        readerSettings.fontSize <= 22 -> 400
        else -> 300
    }
    return (charCount / baseCharsPerPage).coerceAtLeast(1)
}

/**
 * 计算绝对页码
 */
private fun calculateAbsolutePageNumber(
    chapterData: PageData,
    pageInChapter: Int,
    uiState: ReaderState,
    loadedChapters: List<PageData>
): Int {
    // 如果有全局页码缓存，使用缓存数据
    val pageCountCache = uiState.pageCountCache
    if (pageCountCache != null) {
        val chapterRange = pageCountCache.chapterPageRanges.find {
            it.chapterId == chapterData.chapterId 
        }
        if (chapterRange != null) {
            return (chapterRange.startPage + pageInChapter).coerceIn(1, pageCountCache.totalPages)
        }
    }

    // 否则使用估算方法
    var pagesBeforeCurrentChapter = 0
    val chapterList = uiState.chapterList
    val currentChapterIndex = chapterList.indexOfFirst { it.id == chapterData.chapterId }
    
    for (i in 0 until currentChapterIndex) {
        val chapter = chapterList[i]
        val loadedChapter = loadedChapters.find { it.chapterId == chapter.id }
        pagesBeforeCurrentChapter += if (loadedChapter != null) {
            if (loadedChapter.pages.isNotEmpty()) {
                loadedChapter.pages.size
            } else {
                estimateChapterPages(loadedChapter.content, uiState.readerSettings)
            }
        } else {
            10 // 默认估算
        }
    }

    return pagesBeforeCurrentChapter + pageInChapter
}

/**
 * 计算总页数
 */
private fun calculateTotalPages(
    uiState: ReaderState
): Int {
    // 优先使用页码缓存
    val pageCountCache = uiState.pageCountCache
    if (pageCountCache != null) {
        return pageCountCache.totalPages
    }
    
    // 使用渐进计算状态
    if (uiState.paginationState.estimatedTotalPages > 0) {
        return uiState.paginationState.estimatedTotalPages
    }
    
    // 最后使用简单估算
    return uiState.chapterList.size * 10 // 假设每章平均10页
}
