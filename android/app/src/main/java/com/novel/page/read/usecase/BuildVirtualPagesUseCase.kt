package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable
import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.utils.TimberLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import javax.inject.Inject

/**
 * 构建虚拟页面用例
 * 
 * 负责构建统一的虚拟页面列表，支持所有翻页模式：
 * 1. 书籍详情页（首章首页前）
 * 2. 章节内容页面
 * 3. 相邻章节页面整合
 * 4. 虚拟页面索引计算和保持
 */
class BuildVirtualPagesUseCase @Inject constructor(
    private val chapterService: ChapterService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.BUILD_VIRTUAL_PAGES_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    sealed class BuildResult {
        @Stable
        data class Success(
            val virtualPages: ImmutableList<VirtualPage>,
            val newVirtualPageIndex: Int,
            val loadedChapterData: ImmutableMap<String, PageData>
        ) : BuildResult()

        @Stable
        data class Failure(@Stable val error: StableThrowable) : BuildResult()
    }

    /**
     * 执行虚拟页面构建
     * 
     * @param state 当前阅读器状态
     * @param preserveCurrentIndex 是否保持当前虚拟页面索引
     */
    fun execute(
        state: ReaderState,
        preserveCurrentIndex: Boolean = true
    ): BuildResult {
        return try {
            logOperationStart("构建虚拟页面", "preserve=$preserveCurrentIndex")
            
            val currentChapter = state.currentChapter
            val currentPageData = state.currentPageData
            
            if (currentChapter == null || currentPageData == null) {
                val error = StableThrowable(IllegalStateException("缺少当前章节或页面数据"))
                TimberLogger.e(TAG, "虚拟页面构建失败: 缺少必要数据", error)
                return BuildResult.Failure(error)
            }

            // 1. 构建虚拟页面列表
            val virtualPages = mutableListOf<VirtualPage>()
            val loadedChapterData = mutableMapOf<String, PageData>()

            // 2. 添加书籍详情页（如果是第一章）
            if (currentPageData.hasBookDetailPage) {
                virtualPages.add(VirtualPage.BookDetailPage)
                logger.logDebug("添加书籍详情页", TAG)
            }

            // 3. 获取扩展的相邻章节数据（前后各2-3章）
            val extendedChapterData = getExtendedAdjacentChapterData(state)
            logger.logDebug("获取扩展相邻章节数据: ${extendedChapterData.size}个章节", TAG)

            // 4. 按顺序添加章节页面（支持多章节范围）
            val chapterList = state.chapterList
            val currentChapterIndex = state.currentChapterIndex
            val maxRange = 3 // 最大构建范围：前后各3章

            // 计算实际构建范围
            val startIndex = (currentChapterIndex - maxRange).coerceAtLeast(0)
            val endIndex = (currentChapterIndex + maxRange).coerceAtMost(chapterList.size - 1)
            
            logger.logDebug("虚拟页面构建范围: [$startIndex, $endIndex], 当前章节索引=$currentChapterIndex", TAG)

            // 按顺序构建虚拟页面
            for (chapterIndex in startIndex..endIndex) {
                val chapter = chapterList[chapterIndex]
                val pageData = when (chapterIndex) {
                    currentChapterIndex -> currentPageData // 当前章节使用state中的数据
                    else -> extendedChapterData[chapter.id] // 其他章节从扩展数据中获取
                }

                if (pageData != null) {
                    // 添加该章节的所有页面到虚拟页面列表
                    pageData.pages.forEachIndexed { pageIndex, _ ->
                        virtualPages.add(VirtualPage.ContentPage(chapter.id, pageIndex))
                    }
                    loadedChapterData[chapter.id] = pageData
                    
                    val chapterType = when (chapterIndex) {
                        currentChapterIndex -> "当前"
                        in startIndex until currentChapterIndex -> "前序"
                        else -> "后续"
                    }
                    logger.logDebug("添加${chapterType}章节页面: ${chapter.chapterName}, 页数=${pageData.pages.size}", TAG)
                } else {
                    logger.logDebug("跳过未加载的章节: ${chapter.chapterName}", TAG)
                }
            }

            // 5. 计算新的虚拟页面索引
            val newVirtualPageIndex = calculateVirtualPageIndex(
                state, 
                virtualPages, 
                loadedChapterData, 
                preserveCurrentIndex
            )

            logger.logDebug("虚拟页面构建成功: 总页数=${virtualPages.size}, 当前索引=$newVirtualPageIndex", TAG)

            val result = BuildResult.Success(
                virtualPages.toImmutableList(), 
                newVirtualPageIndex, 
                loadedChapterData.toImmutableMap()
            )
            logOperationComplete("构建虚拟页面", "构建完成，总页数=${virtualPages.size}")
            result
            
        } catch (e: Exception) {
            val stableError = StableThrowable(e)
            TimberLogger.e(TAG, "虚拟页面构建失败", stableError)
            BuildResult.Failure(stableError)
        }
    }

    /**
     * 获取扩展的相邻章节数据
     * 支持前后各多章的数据获取（最多前后各3章）
     */
    private fun getExtendedAdjacentChapterData(state: ReaderState): Map<String, PageData> {
        val loadedChapterData = mutableMapOf<String, PageData>()
        val chapterList = state.chapterList
        val currentChapterIndex = state.currentChapterIndex
        val maxRange = 3 // 最大预加载范围：前后各3章

        // 获取前面的章节数据
        for (offset in 1..maxRange) {
            val prevIndex = currentChapterIndex - offset
            if (prevIndex >= 0) {
                val prevChapter = chapterList[prevIndex]
                val cachedChapter = chapterService.getCachedChapter(prevChapter.id)
                cachedChapter?.pageData?.let { pageData ->
                    loadedChapterData[prevChapter.id] = pageData
                    logger.logDebug("获取前序章节数据: ${prevChapter.chapterName}, 页数=${pageData.pages.size}", TAG)
                }
            }
        }

        // 获取后面的章节数据
        for (offset in 1..maxRange) {
            val nextIndex = currentChapterIndex + offset
            if (nextIndex < chapterList.size) {
                val nextChapter = chapterList[nextIndex]
                val cachedChapter = chapterService.getCachedChapter(nextChapter.id)
                cachedChapter?.pageData?.let { pageData ->
                    loadedChapterData[nextChapter.id] = pageData
                    logger.logDebug("获取后续章节数据: ${nextChapter.chapterName}, 页数=${pageData.pages.size}", TAG)
                }
            }
        }

        return loadedChapterData
    }

    /**
     * 计算虚拟页面索引
     * 根据当前状态和是否保持索引来计算合适的虚拟页面索引
     */
    private fun calculateVirtualPageIndex(
        state: ReaderState,
        virtualPages: List<VirtualPage>,
        loadedChapterData: Map<String, PageData>,
        preserveCurrentIndex: Boolean
    ): Int {
        if (virtualPages.isEmpty()) {
            logger.logWarning("虚拟页面为空，返回索引0", TAG)
            return 0
        }

        val currentChapter = state.currentChapter!!
        val currentPageIndex = state.currentPageIndex

        // 如果需要保持当前索引且索引有效
        if (preserveCurrentIndex && state.virtualPageIndex in virtualPages.indices) {
            val currentVirtualPage = virtualPages[state.virtualPageIndex]
            
            // 检查当前虚拟页面是否仍然有效
            when (currentVirtualPage) {
                is VirtualPage.ContentPage -> {
                    if (currentVirtualPage.chapterId == currentChapter.id && 
                        currentVirtualPage.pageIndex == currentPageIndex) {
                        logger.logDebug("保持当前虚拟页面索引: ${state.virtualPageIndex}", TAG)
                        return state.virtualPageIndex
                    }
                }
                is VirtualPage.BookDetailPage -> {
                    if (currentPageIndex == -1) {
                        logger.logDebug("保持书籍详情页索引: ${state.virtualPageIndex}", TAG)
                        return state.virtualPageIndex
                    }
                }
                is VirtualPage.ChapterSection -> {
                    // 章节区域处理
                }
            }
        }

        // 重新计算索引
        return when {
            // 书籍详情页
            currentPageIndex == -1 -> {
                val bookDetailIndex = virtualPages.indexOfFirst { it is VirtualPage.BookDetailPage }
                if (bookDetailIndex >= 0) {
                    logger.logDebug("定位到书籍详情页: $bookDetailIndex", TAG)
                    bookDetailIndex
                } else {
                    logger.logDebug("未找到书籍详情页，使用索引0", TAG)
                    0
                }
            }
            
            // 内容页面
            else -> {
                val targetPage = VirtualPage.ContentPage(currentChapter.id, currentPageIndex)
                val foundIndex = virtualPages.indexOfFirst { virtualPage ->
                    virtualPage is VirtualPage.ContentPage && 
                    virtualPage.chapterId == targetPage.chapterId && 
                    virtualPage.pageIndex == targetPage.pageIndex
                }
                
                if (foundIndex >= 0) {
                    logger.logDebug("定位到内容页面: 章节=${currentChapter.chapterName}, 页面=$currentPageIndex, 虚拟索引=$foundIndex", TAG)
                    foundIndex
                } else {
                    // 如果找不到精确匹配，找到当前章节的第一页
                    val chapterFirstPageIndex = virtualPages.indexOfFirst { virtualPage ->
                        virtualPage is VirtualPage.ContentPage && 
                        virtualPage.chapterId == currentChapter.id
                    }
                    
                    if (chapterFirstPageIndex >= 0) {
                        logger.logDebug("定位到章节首页: $chapterFirstPageIndex", TAG)
                        chapterFirstPageIndex
                    } else {
                        logger.logWarning("未找到匹配页面，使用索引0", TAG)
                        0
                    }
                }
            }
        }
    }
}
