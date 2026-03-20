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
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderState
import javax.inject.Inject

/**
 * 跳转阅读进度用例
 * 
 * 负责根据进度百分比跳转到指定位置：
 * 1. 计算目标页面的全局索引
 * 2. 查找对应的章节和页面
 * 3. 加载章节内容并分页
 * 4. 返回跳转结果
 */
@Stable
class SeekProgressUseCase @Inject constructor(
    private val paginationService: PaginationService,
    private val chapterService: ChapterService,
    private val paginateChapterUseCase: PaginateChapterUseCase,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.SEEK_PROGRESS_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    sealed class SeekResult {
        data class Success(
            val newChapterIndex: Int,
            val newPageData: PageData,
            val newPageIndex: Int
        ) : SeekResult()
        @Stable
        data class Failure(@Stable val error: StableThrowable) : SeekResult()
        data object NoOp : SeekResult()
    }

    /**
     * 执行跳转操作
     * 
     * @param progress 目标进度（0.0-1.0）
     * @param state 当前阅读器状态
     * @return 跳转结果
     */
    suspend fun execute(
        progress: Float,
        state: ReaderState
    ): SeekResult {
        return executeWithResult("跳转阅读进度") {
        logOperationStart("跳转阅读进度", "目标进度=${(progress * 100).toInt()}%")
        
        val pageCountCache = state.pageCountCache
        if (pageCountCache == null) {
            logger.logWarning("页数缓存为空，无法跳转", TAG)
            return@executeWithResult SeekResult.NoOp
        }
        
        if (pageCountCache.totalPages <= 0) {
            logger.logWarning("总页数为0，无法跳转", TAG)
            return@executeWithResult SeekResult.NoOp
        }
        
        val targetGlobalPage = (progress * pageCountCache.totalPages).toInt()
            .coerceIn(0, pageCountCache.totalPages - 1)
        logger.logDebug("计算目标页面: 全局页面索引=$targetGlobalPage", TAG)
        
        val targetChapterInfo = paginationService.findChapterByAbsolutePage(pageCountCache, targetGlobalPage)
        if (targetChapterInfo == null) {
            val error = StableThrowable(IllegalStateException("无法找到目标进度对应的章节"))
            TimberLogger.e(TAG, "无法找到目标进度对应的章节", error)
            return@executeWithResult SeekResult.Failure(error)
        }

        val (targetChapterId, targetPageIndex) = targetChapterInfo
        logger.logDebug("目标位置: 章节ID=$targetChapterId, 页面索引=$targetPageIndex", TAG)
        
        if (targetChapterId == state.currentChapter?.id) {
            logger.logDebug("目标位置在当前章节内", TAG)
        }

        val targetChapterIndex = state.chapterList.indexOfFirst { it.id == targetChapterId }
        if (targetChapterIndex == -1) {
            val error = StableThrowable(IllegalArgumentException("章节在列表中不存在"))
            TimberLogger.e(TAG, "章节在列表中不存在: $targetChapterId", error)
            return@executeWithResult SeekResult.Failure(error)
        }
        
        val targetChapter = state.chapterList[targetChapterIndex]
        logger.logDebug("目标章节: ${targetChapter.chapterName}", TAG)

        try {
            val chapterContent = chapterService.getChapterContent(targetChapterId)
            if (chapterContent == null) {
                val error = StableThrowable(IllegalStateException("无法加载章节内容"))
                TimberLogger.e(TAG, "无法加载章节内容: ${targetChapter.chapterName}", error)
                return@executeWithResult SeekResult.Failure(error)
            }

            val newPageData = paginateChapterUseCase.execute(
                chapter = targetChapter,
                content = chapterContent.content,
                readerSettings = state.readerSettings,
                containerSize = state.containerSize,
                density = state.density!!,
                isFirstChapter = targetChapterIndex == 0,
                isLastChapter = targetChapterIndex == state.chapterList.size - 1
            )
            
            val result = SeekResult.Success(targetChapterIndex, newPageData, targetPageIndex)
            logOperationComplete("跳转阅读进度", "成功跳转到 ${targetChapter.chapterName} 第${targetPageIndex + 1}页")
            result
        } catch(e: Exception) {
            val stableError = StableThrowable(e)
            TimberLogger.e(TAG, "跳转进度失败", stableError)
            SeekResult.Failure(stableError)
        }
        }.getOrElse { throwable ->
            val stableError = StableThrowable(throwable)
            TimberLogger.e(TAG, "跳转进度失败", stableError)
            SeekResult.Failure(stableError)
        }
    }
}
