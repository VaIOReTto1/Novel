package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Density
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * 章节分页用例
 *
 * 负责将章节内容分页处理：
 * 1. 调用分页服务分割内容
 * 2. 创建 PageData 对象
 * 3. 设置章节边界标记
 * 4. 记录分页耗时和结果
 */
@Stable
class PaginateChapterUseCase @Inject constructor(
    private val paginationService: PaginationService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {

    companion object {
        private const val TAG = ReaderLogTags.PAGINATE_CHAPTER_USE_CASE
    }

    override fun getServiceTag(): String = TAG

    /**
     * 执行章节分页
     *
     * @param chapter 章节信息
     * @param content 章节内容
     * @param readerSettings 阅读器设置
     * @param containerSize 容器尺寸
     * @param density 屏幕密度
     * @param isFirstChapter 是否为第一章
     * @param isLastChapter 是否为最后一章
     * @return 分页后的页面数据
     */
    fun execute(
        chapter: Chapter,
        content: String,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density,
        isFirstChapter: Boolean,
        isLastChapter: Boolean
    ): PageData {
        logOperationStart("章节分页", "章节=${chapter.chapterName}, 内容长度=${content.length}")

        return try {
            logger.logDebug(
                "开始分页: 章节=${chapter.chapterName}, 内容长度=${content.length}, " +
                        "容器尺寸=${containerSize.width}x${containerSize.height}",
                TAG
            )

            val pages =
                paginationService.splitContent(content, containerSize, readerSettings, density)

            val pageData = PageData(
                chapterId = chapter.id,
                chapterName = chapter.chapterName,
                content = content,
                pages = pages.toImmutableList(),
                isFirstChapter = isFirstChapter,
                isLastChapter = isLastChapter,
                hasBookDetailPage = isFirstChapter
            )

            logger.logInfo(
                "章节分页完成: ${chapter.chapterName}, 共${pages.size}页, " +
                        "首章=$isFirstChapter, 末章=$isLastChapter",
                TAG
            )

            logOperationComplete("章节分页", "分页完成，共${pages.size}页")
            pageData
        } catch (e: Exception) {
            logger.logError("章节分页失败: ${chapter.chapterName}", e, TAG)
            throw e
        }
    }
} 
