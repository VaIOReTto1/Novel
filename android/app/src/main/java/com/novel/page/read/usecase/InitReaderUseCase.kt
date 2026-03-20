package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.ProgressService
import com.novel.page.read.service.SettingsService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.page.read.viewmodel.ReaderState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject

/**
 * 初始化阅读器结果数据
 * 包含初始化成功后需要的所有数据
 */
@Stable
data class InitReaderResult(
    val settings: ReaderSettings,
    val chapterList: ImmutableList<Chapter>,
    val initialChapter: Chapter,
    val initialChapterIndex: Int,
    val initialPageData: PageData,
    val initialPageIndex: Int,
    val pageCountCache: com.novel.page.read.repository.PageCountCacheData?
)

/**
 * 初始化阅读器用例
 * 
 * 负责阅读器的完整初始化流程：
 * 1. 加载用户设置和章节列表
 * 2. 确定初始章节（从指定章节或恢复进度）
 * 3. 加载并分页初始章节内容
 * 4. 启动预加载和后台分页任务
 * 5. 返回初始化结果
 */
@Stable
class InitReaderUseCase @Inject constructor(
    private val settingsService: SettingsService,
    private val chapterService: ChapterService,
    private val progressService: ProgressService,
    private val paginateChapterUseCase: PaginateChapterUseCase,
    private val preloadChaptersUseCase: PreloadChaptersUseCase,
    private val splitContentUseCase: SplitContentUseCase,
    private val paginationService: PaginationService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {

    companion object {
        private const val TAG = ReaderLogTags.INIT_READER_USE_CASE
    }

    override fun getServiceTag(): String = TAG
    /**
     * 执行阅读器初始化
     * 
     * @param bookId 书籍ID
     * @param chapterId 指定章节ID（可选，为null时恢复上次阅读进度）
     * @param initialState 初始状态（包含容器尺寸等信息）
     * @param scope 协程作用域
     * @return 初始化结果
     */
    suspend fun execute(
        bookId: String,
        chapterId: String?,
        initialState: ReaderState,
        scope: CoroutineScope
    ): Result<InitReaderResult> {
        return executeWithResult("初始化阅读器") {
            logOperationStart("初始化阅读器", "书籍ID=$bookId, 章节ID=$chapterId")
            // 1. 加载设置和章节列表
            logger.logDebug("加载用户设置和章节列表", TAG)
            val settings = settingsService.loadSettings()
            val chapterList = chapterService.getChapterList(bookId)
            if (chapterList.isEmpty()) {
                throw Exception("未找到章节列表")
            }
            logger.logInfo("章节列表加载完成，共 ${chapterList.size} 章", TAG)

            // 2. 确定初始章节和页面索引
            logger.logDebug("确定初始章节和页面索引", TAG)
            val progress = chapterId?.let { null } ?: progressService.getProgress(bookId)
            val initialChapter = chapterList.find { it.id == (chapterId ?: progress?.chapterId) } ?: chapterList.first()
            val initialChapterIndex = chapterList.indexOf(initialChapter)
            val initialPageIndex = progress?.pageIndex ?: 0
            logger.logInfo("初始章节确定: ${initialChapter.chapterName}，页面索引: $initialPageIndex", TAG)

            // 3. 获取初始章节内容
            logger.logDebug("获取初始章节内容", TAG)
            val initialContent = chapterService.getChapterContent(initialChapter.id)
                ?: throw Exception("章节内容加载失败")
            logger.logInfo("初始章节内容加载完成，内容长度: ${initialContent.content.length}", TAG)

            // 4. 创建分页状态
            val stateForSplitting = initialState.copy(
                bookId = bookId,
                chapterList = chapterList.toImmutableList(),
                currentChapter = initialChapter,
                currentChapterIndex = initialChapterIndex,
                bookContent = initialContent.content,
                readerSettings = settings,
                currentPageIndex = initialPageIndex
            )

            // 5. 使用SplitContentUseCase进行内容分割
            val splitResult = splitContentUseCase.execute(
                state = stateForSplitting,
                restoreProgress = null,
                includeAdjacentChapters = true
            )

            val (initialPageData, safePageIndex) = when (splitResult) {
                is SplitContentUseCase.SplitResult.Success -> {
                    splitResult.pageData to splitResult.safePageIndex
                }
                is SplitContentUseCase.SplitResult.Failure -> {
                    // 降级到基础分页
                    val basicPageData = if (initialState.density != null &&
                        initialState.containerSize.width > 0 && initialState.containerSize.height > 0) {
                        // 正常分页路径
                        paginateChapterUseCase.execute(
                            chapter = initialChapter,
                            content = initialContent.content,
                            readerSettings = settings,
                            containerSize = initialState.containerSize,
                            density = initialState.density!!,
                            isFirstChapter = initialChapterIndex == 0,
                            isLastChapter = initialChapterIndex == chapterList.size - 1
                        )
                    } else {
                        // 当容器信息尚未就绪时，降级为单页数据，避免空指针异常
                        logger.logWarning(
                            "容器尺寸或 Density 未就绪，使用单页降级策略进行初始化", TAG
                        )
                        com.novel.page.read.viewmodel.PageData(
                            chapterId = initialChapter.id,
                            chapterName = initialChapter.chapterName,
                            content = initialContent.content,
                            pages = listOf(initialContent.content).toImmutableList(),
                            isFirstChapter = initialChapterIndex == 0,
                            isLastChapter = initialChapterIndex == chapterList.size - 1,
                            hasBookDetailPage = initialChapterIndex == 0
                        )
                    }
                    basicPageData to initialPageIndex.coerceIn(0, basicPageData.pages.size - 1)
                }
            }
            
            // 6. 启动预加载任务（传递状态信息以支持分页处理）
            logger.logDebug("启动预加载任务", TAG)
            val finalStateForPreload = stateForSplitting.copy(
                currentPageData = initialPageData,
                currentPageIndex = safePageIndex,
                readerSettings = settings
            )
            preloadChaptersUseCase.execute(scope, chapterList, initialChapter.id, finalStateForPreload)

            // 7. 启动后台全书分页计算（非纵向滚动模式）
            if (initialState.containerSize.width > 0 && initialState.containerSize.height > 0) {
                logger.logDebug("启动后台全书分页计算", TAG)
                scope.launch {
                    paginationService.fetchAllBookContentAndPaginateInBackground(
                        bookId = bookId,
                        chapterList = chapterList,
                        readerSettings = settings,
                        containerSize = initialState.containerSize,
                        density = initialState.density!!
                    )
                }
            }

            // 8. 立即获取或创建页数缓存，确保首次打开时页数能正确显示
            logger.logDebug("获取页数缓存", TAG)
            val pageCountCache = paginationService.getPageCountCache(
                bookId = bookId,
                fontSize = settings.fontSize,
                containerSize = initialState.containerSize
            )

            val result = InitReaderResult(
                settings = settings,
                chapterList = chapterList.toImmutableList(),
                initialChapter = initialChapter,
                initialChapterIndex = initialChapterIndex,
                initialPageData = initialPageData,
                initialPageIndex = safePageIndex,
                pageCountCache = pageCountCache
            )

            logOperationComplete("初始化阅读器", "成功初始化，初始章节: ${initialChapter.chapterName}")
            result
        }
    }
}
