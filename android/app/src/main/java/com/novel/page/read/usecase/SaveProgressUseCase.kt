package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.page.read.service.ProgressService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.ReaderState
import javax.inject.Inject

/**
 * 保存阅读进度用例
 * 
 * 负责将当前阅读状态保存到持久化存储：
 * 1. 保存当前章节和页面索引
 * 2. 保存阅读进度百分比
 * 3. 保存页面翻转效果设置
 * 4. 保存页数缓存信息
 */
@Stable
class SaveProgressUseCase @Inject constructor(
    private val progressService: ProgressService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.SAVE_PROGRESS_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    /**
     * 执行保存进度操作
     * 
     * @param state 当前阅读器状态
     */
    suspend fun execute(state: ReaderState) = executeIo("保存阅读进度") {
        val currentChapter = state.currentChapter
        if (currentChapter == null) {
            logger.logWarning("当前章节为空，无法保存进度", TAG)
            return@executeIo
        }

        logger.logDebug(
            "保存阅读进度: 书籍=${state.bookId}, 章节=${currentChapter.chapterName}, " +
            "页面=${state.currentPageIndex}, 进度=${state.computedReadingProgress}%", 
            TAG
        )

        progressService.saveProgress(
            bookId = state.bookId,
            chapterId = currentChapter.id,
            currentPageIndex = state.currentPageIndex,
            pageCountCache = state.pageCountCache,
            pageFlipEffect = state.readerSettings.pageFlipEffect,
            computedReadingProgress = state.computedReadingProgress,
            currentPageData = state.currentPageData
        )

        logger.logInfo("阅读进度保存成功", TAG)
    }

    /**
     * 批量保存进度（用于定期保存）
     * 
     * @param states 多个阅读器状态
     */
    suspend fun batchSave(states: List<ReaderState>) = executeIo("批量保存阅读进度") {
        if (states.isEmpty()) {
            logger.logDebug("没有需要保存的进度", TAG)
            return@executeIo
        }

        logger.logDebug("开始批量保存 ${states.size} 个阅读进度", TAG)
        
        var successCount = 0
        var failCount = 0
        
        states.forEach { state ->
            try {
                execute(state)
                successCount++
            } catch (e: Exception) {
                failCount++
                logger.logError("保存进度失败: 书籍=${state.bookId}", e, TAG)
            }
        }
        
        logger.logInfo("批量保存完成: 成功=$successCount, 失败=$failCount", TAG)
    }

    /**
     * 保存临时进度（不持久化，仅内存）
     * 
     * @param state 当前阅读器状态
     */
    suspend fun saveTemporary(state: ReaderState) = executeComputation("保存临时进度") {
        val currentChapter = state.currentChapter
        if (currentChapter == null) {
            logger.logWarning("当前章节为空，无法保存临时进度", TAG)
            return@executeComputation
        }

        logger.logDebug("保存临时进度: 章节=${currentChapter.chapterName}, 页面=${state.currentPageIndex}", TAG)
        
        // 这里可以保存到内存缓存或其他临时存储
        // 注意：这个方法可能需要在 ProgressService 中实现
        logger.logDebug("临时进度已记录", TAG)
    }
} 
