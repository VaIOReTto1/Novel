package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable

import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.utils.TimberLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.FlipDirection
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderState
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * 切换章节用例
 * 
 * 负责处理章节切换的完整流程：
 * 1. 保存当前章节的阅读进度
 * 2. 加载目标章节内容
 * 3. 分页处理新章节
 * 4. 触发预加载相邻章节
 * 5. 根据翻页方向确定初始页面位置
 */
@Stable
class SwitchChapterUseCase @Inject constructor(
    private val chapterService: ChapterService,
    private val paginateChapterUseCase: PaginateChapterUseCase,
    private val preloadChaptersUseCase: PreloadChaptersUseCase,
    private val saveProgressUseCase: SaveProgressUseCase,
    private val splitContentUseCase: SplitContentUseCase,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.SWITCH_CHAPTER_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    sealed class SwitchResult {
        data class Success(
            val newChapterIndex: Int,
            val pageData: PageData,
            val initialPageIndex: Int = 0
        ) : SwitchResult()
        @Stable
        data class Failure(@Stable val error: StableThrowable) : SwitchResult()
        data object NoOp : SwitchResult()
    }

    /**
     * 执行章节切换
     * 
     * @param state 当前阅读器状态
     * @param newChapterId 目标章节ID
     * @param scope 协程作用域
     * @param flipDirection 翻页方向（可选）
     * @return 切换结果
     */
    suspend fun execute(
        state: ReaderState,
        newChapterId: String,
        scope: CoroutineScope,
        flipDirection: FlipDirection? = null
    ): SwitchResult {
        return executeWithResult("切换章节") {
        val currentChapter = state.currentChapter
        if (currentChapter == null) {
            logger.logWarning("当前章节为空，无法切换", TAG)
            return@executeWithResult SwitchResult.NoOp
        }
        
        if (currentChapter.id == newChapterId) {
            logger.logDebug("目标章节与当前章节相同，无需切换", TAG)
            return@executeWithResult SwitchResult.NoOp
        }

        val newChapterIndex = state.chapterList.indexOfFirst { it.id == newChapterId }
        if (newChapterIndex == -1) {
            val error = StableThrowable(IllegalArgumentException("目标章节在列表中不存在"))
            TimberLogger.e(TAG, "目标章节在列表中不存在: $newChapterId", error)
            return@executeWithResult SwitchResult.Failure(error)
        }
        
        val newChapter = state.chapterList[newChapterIndex]
        logOperationStart("切换章节", "从 ${currentChapter.chapterName} 切换到 ${newChapter.chapterName}")

        // 1. 保存当前章节的阅读进度
        logger.logDebug("保存当前章节进度", TAG)
        saveProgressUseCase.execute(state)

        try {
            // 2. 获取新章节内容
            logger.logDebug("加载章节内容: ${newChapter.chapterName}", TAG)
            val chapterContent = chapterService.getChapterContent(newChapterId)
            if (chapterContent == null) {
                val error = StableThrowable(IllegalStateException("无法加载章节内容"))
                TimberLogger.e(TAG, "无法加载章节内容: ${newChapter.chapterName}", error)
                return@executeWithResult SwitchResult.Failure(error)
            }

            // 3. 创建用于内容分割的状态
            val stateForSplitting = state.copy(
                currentChapter = newChapter,
                currentChapterIndex = newChapterIndex,
                bookContent = chapterContent.content,
                currentPageIndex = when (flipDirection) {
                    FlipDirection.PREVIOUS -> -1 // 将在分割后设置为最后一页
                    FlipDirection.NEXT -> 0
                    else -> 0
                }
            )

            // 4. 使用 SplitContentUseCase 分割内容
            logger.logDebug("开始内容分割", TAG)
            val splitResult = splitContentUseCase.execute(
                state = stateForSplitting,
                restoreProgress = null,
                includeAdjacentChapters = true
            )

            val (pageData, initialPageIndex) = when (splitResult) {
                is SplitContentUseCase.SplitResult.Success -> {
                    val adjustedPageIndex = when (flipDirection) {
                        FlipDirection.PREVIOUS -> (splitResult.pageData.pages.size - 1).coerceAtLeast(0)
                        else -> splitResult.safePageIndex
                    }
                    logger.logDebug("内容分割成功，页数: ${splitResult.pageData.pages.size}", TAG)
                    splitResult.pageData to adjustedPageIndex
                }
                is SplitContentUseCase.SplitResult.Failure -> {
                    // 回退到基础分页
                    logger.logWarning("内容分割失败，使用基础分页", TAG)
                    val basicPageData = paginateChapterUseCase.execute(
                        chapter = newChapter,
                        content = chapterContent.content,
                        readerSettings = state.readerSettings,
                        containerSize = state.containerSize,
                        density = state.density!!,
                        isFirstChapter = newChapterIndex == 0,
                        isLastChapter = newChapterIndex == state.chapterList.size - 1
                    )
                    val adjustedPageIndex = when (flipDirection) {
                        FlipDirection.PREVIOUS -> (basicPageData.pages.size - 1).coerceAtLeast(0)
                        else -> 0
                    }
                    basicPageData to adjustedPageIndex
                }
            }

            // 5. 触发周围章节的预加载
            logger.logDebug("触发预加载相邻章节", TAG)
            preloadChaptersUseCase.execute(scope, state.chapterList, newChapterId)

            val result = SwitchResult.Success(newChapterIndex, pageData, initialPageIndex)
            logOperationComplete("切换章节", "成功切换到 ${newChapter.chapterName}，初始页面: $initialPageIndex")
            result
        } catch (e: Exception) {
            val stableError = StableThrowable(e)
            TimberLogger.e(TAG, "章节切换失败", stableError)
            SwitchResult.Failure(stableError)
        }
        }.getOrElse { throwable ->
            val stableError = StableThrowable(throwable)
            TimberLogger.e(TAG, "章节切换失败", stableError)
            SwitchResult.Failure(stableError)
        }
    }
}
