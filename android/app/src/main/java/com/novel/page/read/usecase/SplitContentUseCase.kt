package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable

import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.utils.TimberLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import kotlinx.collections.immutable.toImmutableList
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderState
import javax.inject.Inject

/**
 * 内容分割用例
 * 
 * 负责将章节内容分割成页面，并处理相邻章节数据：
 * 1. 当前章节内容分页
 * 2. 相邻章节数据加载和分页
 * 3. 进度恢复和页面索引计算
 * 4. 书籍信息加载（首章）
 */
@Stable
class SplitContentUseCase @Inject constructor(
    private val paginationService: PaginationService,
    private val chapterService: ChapterService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.SPLIT_CONTENT_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    sealed class SplitResult {
        data class Success(
            val pageData: PageData,
            val safePageIndex: Int
        ) : SplitResult()

        @Stable
        data class Failure(@Stable val error: StableThrowable) : SplitResult()
    }

    /**
     * 执行内容分割
     * 
     * @param state 当前阅读器状态
     * @param restoreProgress 需要恢复的进度（0.0-1.0），用于重新分页后恢复位置
     * @param includeAdjacentChapters 是否包含相邻章节数据
     */
    suspend fun execute(
        state: ReaderState,
        restoreProgress: Float? = null,
        includeAdjacentChapters: Boolean = false
    ): SplitResult {
        return executeWithResult("内容分割") {
        val currentChapter = state.currentChapter
        val chapterContent = state.bookContent
        val density = state.density
        
        if (currentChapter == null || chapterContent.isEmpty() || density == null) {
            val error = StableThrowable(IllegalStateException("缺少必要的分割参数"))
            TimberLogger.e(TAG, "内容分割失败: 缺少必要参数", error)
            return@executeWithResult SplitResult.Failure(error)
        }

        logOperationStart("内容分割", "章节=${currentChapter.chapterName}, 内容长度=${chapterContent.length}")

        // 1. 分割当前章节内容
        val pages = paginationService.splitContent(
            content = chapterContent,
            containerSize = state.containerSize,
            readerSettings = state.readerSettings,
            density = density
        )

        logger.logDebug("章节分页完成: 页数=${pages.size}", TAG)

        // 2. 创建基础PageData
        var currentPageData = PageData(
            chapterId = currentChapter.id,
            chapterName = currentChapter.chapterName,
            content = chapterContent,
            pages = pages.toImmutableList(),
            isFirstChapter = state.isFirstChapter,
            isLastChapter = state.isLastChapter,
            hasBookDetailPage = state.isFirstChapter
        )

        // 3. 加载书籍信息（如果是第一章）
        if (state.isFirstChapter) {
            logger.logDebug("加载书籍信息", TAG)
            val bookInfo = chapterService.loadBookInfo(state.bookId)
            if (bookInfo != null) {
                currentPageData = currentPageData.copy(bookInfo = bookInfo)
                logger.logDebug("书籍信息加载成功: ${bookInfo.bookName}", TAG)
            } else {
                logger.logWarning("书籍信息加载失败", TAG)
            }
        }

        // 4. 加载相邻章节数据
        if (includeAdjacentChapters) {
            logger.logDebug("开始加载相邻章节数据", TAG)
            val (previousChapterData, nextChapterData) = loadAdjacentChapterData(state)
            currentPageData = currentPageData.copy(
                previousChapterData = previousChapterData,
                nextChapterData = nextChapterData
            )
            logger.logDebug("相邻章节数据加载完成: 前章=${previousChapterData != null}, 后章=${nextChapterData != null}", TAG)
        }

        // 5. 计算安全的页面索引
        val safePageIndex = calculateSafePageIndex(state, currentPageData, restoreProgress)
        
        logger.logDebug("内容分割成功: 最终页面索引=$safePageIndex", TAG)
        
        // 6. 更新章节缓存中的分页数据
        chapterService.setCachedChapterPageData(currentChapter.id, currentPageData)

            val result = SplitResult.Success(currentPageData, safePageIndex)
            logOperationComplete("内容分割", "分割完成，页数=${pages.size}，页面索引=$safePageIndex")
            result
        }.getOrElse { throwable ->
            val stableError = StableThrowable(throwable)
            TimberLogger.e(TAG, "内容分割失败", stableError)
            SplitResult.Failure(stableError)
        }
    }

    /**
     * 加载相邻章节数据
     * 加载并分页前后章节的内容
     */
    private suspend fun loadAdjacentChapterData(state: ReaderState): Pair<PageData?, PageData?> {
        val chapterList = state.chapterList
        val currentChapterIndex = state.currentChapterIndex
        val density = state.density ?: return Pair(null, null)

        var previousChapterData: PageData? = null
        var nextChapterData: PageData? = null

        // 加载前一章节
        val prevIndex = currentChapterIndex - 1
        if (prevIndex >= 0) {
            try {
                val prevChapter = chapterList[prevIndex]
                logger.logDebug("加载前序章节: ${prevChapter.chapterName}", TAG)
                
                val prevContent = chapterService.getChapterContent(prevChapter.id)
                if (prevContent != null) {
                    val prevPages = paginationService.splitContent(
                        content = prevContent.content,
                        containerSize = state.containerSize,
                        readerSettings = state.readerSettings,
                        density = density
                    )
                    
                    previousChapterData = PageData(
                        chapterId = prevChapter.id,
                        chapterName = prevChapter.chapterName,
                        content = prevContent.content,
                        pages = prevPages.toImmutableList(),
                        isFirstChapter = prevIndex == 0,
                        isLastChapter = false
                    )
                    
                    // 更新缓存
                    chapterService.setCachedChapterPageData(prevChapter.id, previousChapterData)
                    logger.logDebug("前序章节加载成功: 页数=${prevPages.size}", TAG)
                }
            } catch (e: Exception) {
                logger.logError("前序章节加载失败", e, TAG)
            }
        }

        // 加载后一章节
        val nextIndex = currentChapterIndex + 1
        if (nextIndex < chapterList.size) {
            try {
                val nextChapter = chapterList[nextIndex]
                logger.logDebug("加载后续章节: ${nextChapter.chapterName}", TAG)
                
                val nextContent = chapterService.getChapterContent(nextChapter.id)
                if (nextContent != null) {
                    val nextPages = paginationService.splitContent(
                        content = nextContent.content,
                        containerSize = state.containerSize,
                        readerSettings = state.readerSettings,
                        density = density
                    )
                    
                    nextChapterData = PageData(
                        chapterId = nextChapter.id,
                        chapterName = nextChapter.chapterName,
                        content = nextContent.content,
                        pages = nextPages.toImmutableList(),
                        isFirstChapter = false,
                        isLastChapter = nextIndex == chapterList.size - 1
                    )
                    
                    // 更新缓存
                    chapterService.setCachedChapterPageData(nextChapter.id, nextChapterData)
                    logger.logDebug("后续章节加载成功: 页数=${nextPages.size}", TAG)
                }
            } catch (e: Exception) {
                logger.logError("后续章节加载失败", e, TAG)
            }
        }

        return Pair(previousChapterData, nextChapterData)
    }

    /**
     * 计算安全的页面索引
     * 根据当前状态、分页结果和进度恢复需求计算合适的页面索引
     */
    private fun calculateSafePageIndex(
        state: ReaderState,
        pageData: PageData,
        restoreProgress: Float?
    ): Int {
        val pages = pageData.pages
        if (pages.isEmpty()) {
            logger.logWarning("页面为空，返回索引0", TAG)
            return 0
        }

        return when {
            // 如果有恢复进度，按进度计算
            restoreProgress != null -> {
                val progressIndex = (restoreProgress * pages.size).toInt()
                val safeIndex = progressIndex.coerceIn(0, pages.size - 1)
                logger.logDebug("按进度恢复页面索引: 进度=$restoreProgress -> 索引=$safeIndex", TAG)
                safeIndex
            }
            
            // 如果当前页面索引有效，保持不变
            state.currentPageIndex in 0 until pages.size -> {
                logger.logDebug("保持当前页面索引: ${state.currentPageIndex}", TAG)
                state.currentPageIndex
            }
            
            // 如果是书籍详情页（索引-1），保持不变
            state.currentPageIndex == -1 && pageData.hasBookDetailPage -> {
                logger.logDebug("保持书籍详情页索引: -1", TAG)
                -1
            }
            
            // 其他情况，使用第一页
            else -> {
                logger.logDebug("使用默认页面索引: 0", TAG)
                0
            }
        }
    }
}
