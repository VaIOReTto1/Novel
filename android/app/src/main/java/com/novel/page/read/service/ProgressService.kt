package com.novel.page.read.service

import androidx.compose.runtime.Stable
import com.novel.page.read.repository.ReadingProgressData
import com.novel.page.read.repository.ReadingProgressRepository
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ReaderServiceConfig
import com.novel.page.read.service.common.SafeService
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.PageFlipEffect
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 进度服务类 - 重构优化版
 *
 * 负责阅读进度的计算和保存：
 * 1. 章节内进度计算（基于当前页面在章节中的位置）
 * 2. 全书进度计算（基于页数缓存的全局页码）
 * 3. 阅读进度持久化保存
 * 4. 多种翻页模式的进度适配
 *
 * 优化特性：
 * - 继承SafeService统一异步调度和错误处理
 * - 使用Timber日志系统进行结构化日志记录
 * - 支持性能监控和超时保护
 * - 统一配置管理和异常处理
 * - 支持页面级精确进度定位
 * - 兼容纵向滚动和分页模式
 * - 自动处理章节边界的进度连续性
 * - 基于全书页数缓存的统一进度体系
 */
@Singleton
@Stable
class ProgressService @Inject constructor(
    private val readingProgressRepository: ReadingProgressRepository,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : SafeService(dispatchers, logger) {

    companion object {
        private const val TAG = ReaderLogTags.PROGRESS_SERVICE
    }

    override fun getServiceTag(): String = TAG

    /**
     * 保存阅读进度
     *
     * 计算并保存当前的阅读位置，包括：
     * - 书籍ID和章节ID
     * - 章节内页面索引
     * - 基于页数缓存的全局页面索引
     * - 章节内进度百分比
     * - 全书进度百分比
     * - 当前翻页效果设置
     *
     * @param bookId 书籍唯一标识
     * @param chapterId 章节唯一标识
     * @param currentPageIndex 章节内当前页面索引（从0开始，-1表示书籍详情页）
     * @param pageCountCache 全书页数缓存数据，用于计算全局进度
     * @param pageFlipEffect 当前翻页效果设置
     * @param computedReadingProgress 预计算的阅读进度（0.0-1.0）
     * @param currentPageData 当前页面数据，用于计算章节内进度
     */
    suspend fun saveProgress(
        bookId: String,
        chapterId: String,
        currentPageIndex: Int,
        pageCountCache: com.novel.page.read.repository.PageCountCacheData?,
        pageFlipEffect: PageFlipEffect,
        computedReadingProgress: Float,
        currentPageData: com.novel.page.read.viewmodel.PageData?
    ) {
        safeIoWithTimeout({
            withPerformanceMonitoring("saveProgress") {
                logger.logDebug("保存阅读进度: 书籍=$bookId, 章节=$chapterId, 页面=$currentPageIndex", TAG)

                val globalPageIndex = calculateGlobalPageIndex(pageCountCache, chapterId, currentPageIndex)
                val chapterProgress = calculateChapterProgress(currentPageData, currentPageIndex)

                val progressData = ReadingProgressData(
                    bookId = bookId,
                    chapterId = chapterId,
                    pageIndex = currentPageIndex,
                    globalPageIndex = globalPageIndex,
                    chapterProgress = chapterProgress,
                    globalProgress = computedReadingProgress,
                    pageFlipEffect = pageFlipEffect
                )

                logger.logInfo("进度数据: 章节进度=${(progressData.chapterProgress * 100).toInt()}%, " +
                        "全书进度=${(progressData.globalProgress * 100).toInt()}%, " +
                        "全局页面=${progressData.globalPageIndex}", TAG)

                readingProgressRepository.saveReadingProgress(progressData)

                logger.logDebug("阅读进度保存成功", TAG)
            }
        }, ReaderServiceConfig.CACHE_OPERATION_TIMEOUT_MS) ?: run {
            logger.logError("阅读进度保存超时或失败", null, TAG)
        }
    }

    /**
     * 获取阅读进度
     *
     * @param bookId 书籍ID
     * @return 阅读进度数据，未找到时返回null
     */
    suspend fun getProgress(bookId: String): ReadingProgressData? {
        return safeIoWithTimeout({
            withPerformanceMonitoring("getProgress") {
                logger.logDebug("获取阅读进度: bookId=$bookId", TAG)

                val progress = readingProgressRepository.getReadingProgress(bookId)

                if (progress != null) {
                    logger.logInfo("进度恢复成功: 章节=${progress.chapterId}, " +
                            "页面=${progress.pageIndex}, " +
                            "全局页面=${progress.globalPageIndex}, " +
                            "翻页效果=${progress.pageFlipEffect}", TAG)
                } else {
                    logger.logDebug("未找到保存的阅读进度", TAG)
                }

                progress
            }
        }, ReaderServiceConfig.CACHE_OPERATION_TIMEOUT_MS) ?: run {
            logger.logWarning("阅读进度获取超时，返回null", TAG)
            null
        }
    }

    /**
     * 批量获取多本书的阅读进度
     *
     * @param bookIds 书籍ID列表
     * @return 阅读进度数据映射
     */
    suspend fun getBatchProgress(bookIds: List<String>): Map<String, ReadingProgressData> {
        return safeIoWithDefault(emptyMap()) {
            withPerformanceMonitoring("getBatchProgress") {
                logger.logDebug("批量获取阅读进度: ${bookIds.size}本书", TAG)

                val progressMap = mutableMapOf<String, ReadingProgressData>()

                bookIds.forEach { bookId ->
                    readingProgressRepository.getReadingProgress(bookId)?.let { progress ->
                        progressMap[bookId] = progress
                    }
                }

                logger.logInfo("批量进度获取完成: ${progressMap.size}/${bookIds.size}本书有进度记录", TAG)
                progressMap
            }
        }
    }

    /**
     * 清理阅读进度
     *
     * @param bookId 书籍ID
     */
    suspend fun clearProgress(bookId: String) {
        safeIo {
            withPerformanceMonitoring("clearProgress") {
                logger.logDebug("清理阅读进度: bookId=$bookId", TAG)

                readingProgressRepository.clearReadingProgress(bookId)

                logger.logInfo("阅读进度清理成功", TAG)
            }
        } ?: logger.logError("阅读进度清理失败", null, TAG)
    }

    /**
     * 计算全局页面索引
     *
     * 基于页数缓存中的章节页面范围信息，计算当前页面在全书中的绝对位置
     *
     * @param pageCountCache 页数缓存数据
     * @param chapterId 当前章节ID
     * @param currentPageIndex 章节内页面索引
     * @return 全局页面索引
     */
    private fun calculateGlobalPageIndex(
        pageCountCache: com.novel.page.read.repository.PageCountCacheData?,
        chapterId: String,
        currentPageIndex: Int
    ): Int {
        return try {
            if (pageCountCache != null) {
                val chapterRange = pageCountCache.chapterPageRanges.find { it.chapterId == chapterId }
                if (chapterRange != null) {
                    val globalIndex = chapterRange.startPage + currentPageIndex.coerceAtLeast(0)
                    logger.logDebug("全局页面索引计算: 章节起始页=${chapterRange.startPage} + 当前页=$currentPageIndex = $globalIndex", TAG)
                    return globalIndex
                }
            }

            val fallbackIndex = currentPageIndex.coerceAtLeast(0)
            logger.logDebug("页数缓存未找到，使用章节内索引: $fallbackIndex", TAG)
            fallbackIndex
        } catch (e: Exception) {
            logger.logError("全局页面索引计算失败，使用章节内索引", e, TAG)
            currentPageIndex.coerceAtLeast(0)
        }
    }

    /**
     * 计算章节内进度
     *
     * 基于当前页面在章节中的位置计算进度百分比
     *
     * @param currentPageData 当前页面数据
     * @param pageIndex 页面索引
     * @return 章节内进度（0.0-1.0）
     */
    private fun calculateChapterProgress(
        currentPageData: com.novel.page.read.viewmodel.PageData?,
        pageIndex: Int
    ): Float {
        return try {
            when {
                currentPageData == null -> {
                    logger.logDebug("页面数据为空，章节进度为0", TAG)
                    0f
                }
                pageIndex == -1 -> {
                    logger.logDebug("书籍详情页，章节进度为0", TAG)
                    0f
                }
                currentPageData.pages.isEmpty() -> {
                    logger.logDebug("章节页面为空，章节进度为0", TAG)
                    0f
                }
                else -> {
                    val progress = (pageIndex + 1).toFloat() / currentPageData.pages.size.toFloat()
                    logger.logDebug("章节进度计算: (${pageIndex + 1})/${currentPageData.pages.size} = ${(progress * 100).toInt()}%", TAG)
                    progress.coerceIn(0f, 1f)
                }
            }
        } catch (e: Exception) {
            logger.logError("章节进度计算失败，返回0", e, TAG)
            0f
        }
    }
}
