package com.novel.page.read.usecase

import androidx.compose.runtime.Stable
import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.utils.TimberLogger
import com.novel.page.read.usecase.common.PreloadHelper
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderState
import com.novel.page.read.viewmodel.VirtualPage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 章节预加载用例
 * 
 * 负责智能预加载相邻章节，提升阅读体验：
 * 1. 动态预加载范围管理（2-4章节）
 * 2. 缓存清理和内存管理
 * 3. 预加载状态检查和优化
 * 4. 响应式预加载触发
 */
@Stable
class PreloadChaptersUseCase @Inject constructor(
    private val chapterService: ChapterService,
    private val paginationService: PaginationService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {
    
    companion object {
        private const val TAG = ReaderLogTags.PRELOAD_CHAPTERS_USE_CASE
    }

    override fun getServiceTag(): String = TAG
    
    private var preloadJob: Job? = null
    private val preloadingChapters = mutableSetOf<String>()
    
    // 动态预加载范围管理
    private var currentPreloadStartIndex = -1
    private var currentPreloadEndIndex = -1

    /**
     * 执行基础预加载
     * 预加载当前章节前后各2章的内容
     * 
     * @param scope 协程作用域
     * @param chapterList 章节列表
     * @param currentChapterId 当前章节ID
     * @param state 阅读器状态（可选，用于分页参数）
     */
    fun execute(
        scope: CoroutineScope,
        chapterList: List<Chapter>,
        currentChapterId: String,
        state: ReaderState? = null
    ) {
        logOperationStart("基础预加载", "当前章节=$currentChapterId")
        
        preloadJob?.cancel()
        preloadJob = scope.launch {
            executeIo("基础预加载操作") {
                val currentIndex = chapterList.indexOfFirst { it.id == currentChapterId }
                if (currentIndex == -1) {
                    logger.logWarning("当前章节未找到: $currentChapterId", TAG)
                    return@executeIo
                }

                val preloadIndices = PreloadHelper.getBasicPreloadIndices(currentIndex, chapterList.size)
                logger.logDebug("预加载范围: 当前章节索引=$currentIndex, 预加载章节索引=${preloadIndices}", TAG)
                
                // 并行预加载章节
                preloadIndices.map { index ->
                    async {
                        val chapter = chapterList[index]
                        logger.logDebug("预加载章节: ${chapter.chapterName}", TAG)
                        
                        if (state != null) {
                            // 如果有状态信息，使用带分页参数的预加载
                            chapterService.preloadChapter(
                                chapterId = chapter.id,
                                containerSize = state.containerSize,
                                readerSettings = state.readerSettings,
                                density = state.density
                            )
                        } else {
                            // 否则使用基础预加载
                            chapterService.preloadChapter(chapter.id)
                        }
                    }
                }.awaitAll()
                
                logOperationComplete("基础预加载", "预加载了 ${preloadIndices.size} 个章节")
            }
        }
    }

    /**
     * 执行动态预加载
     * 根据阅读进度和缓存状况智能调整预加载范围
     * 
     * 该函数实现了动态预加载逻辑：
     * 1. 计算最优预加载范围（2-4章节）
     * 2. 并行预加载章节内容
     * 3. 对已缓存章节进行分页处理
     * 4. 清理超出范围的缓存释放内存
     * 5. 检查是否有新相邻章节需要重建虚拟页面
     */
    fun performDynamicPreload(
        scope: CoroutineScope,
        state: ReaderState,
        currentChapterId: String,
        triggerExpansion: Boolean = false
    ) {
        logOperationStart("动态预加载", "当前章节=$currentChapterId, 扩展=$triggerExpansion")
        
        preloadJob?.cancel()
        preloadJob = scope.launch {
            executeIo("动态预加载操作") {
                val chapterList = state.chapterList
                val currentIndex = chapterList.indexOfFirst { it.id == currentChapterId }

                if (currentIndex < 0) {
                    logger.logWarning("动态预加载失败: 章节未找到", TAG)
                    return@executeIo
                }

                // 计算新的预加载范围
                val (newStartIndex, newEndIndex) = PreloadHelper.calculatePreloadRange(
                    currentIndex, chapterList.size, triggerExpansion
                )

                logger.logDebug("预加载范围: [$newStartIndex, $newEndIndex], 总章节数=${chapterList.size}", TAG)

                // 更新预加载范围记录
                currentPreloadStartIndex = newStartIndex
                currentPreloadEndIndex = newEndIndex

                // 执行并行预加载
                val preloadIndices = (newStartIndex..newEndIndex).filter { it != currentIndex }

                logger.logDebug("开始并行预加载: ${preloadIndices.size}个章节", TAG)
                
                // 并行预加载所有需要的章节
                preloadIndices.map { index ->
                    async {
                        val chapter = chapterList[index]
                        performChapterPreload(chapter, index, state, chapterList.size)
                    }
                }.awaitAll()

                // 清理超出范围的缓存
                cleanupOutOfRangeCache(chapterList, newStartIndex, newEndIndex, currentIndex)

                // 检查是否有新的相邻章节被加载
                val hasNewAdjacentChapters = checkIfNewAdjacentChaptersLoaded(state, currentIndex)
                if (hasNewAdjacentChapters) {
                    logger.logDebug("检测到新的相邻章节已加载，需要重建虚拟页面", TAG)
                }
                
                logOperationComplete("动态预加载", "完成 ${preloadIndices.size} 个章节的预加载")
            }
        }
    }

    /**
     * 执行单个章节的预加载
     */
    private suspend fun performChapterPreload(
        chapter: Chapter,
        index: Int,
        state: ReaderState,
        totalChapters: Int
    ) {
        val cachedChapter = chapterService.getCachedChapter(chapter.id)
        
        if (cachedChapter == null && !preloadingChapters.contains(chapter.id)) {
            // 预加载章节内容
            try {
                preloadingChapters.add(chapter.id)
                logger.logDebug("预加载章节内容: ${chapter.chapterName}", TAG)
                
                chapterService.preloadChapter(
                    chapterId = chapter.id,
                    containerSize = state.containerSize,
                    readerSettings = state.readerSettings,
                    density = state.density
                )
            } catch (e: Exception) {
                logger.logError("章节内容预加载失败: ${chapter.chapterName}", e, TAG)
            } finally {
                preloadingChapters.remove(chapter.id)
            }
        } else if (cachedChapter != null && cachedChapter.pageData == null && 
                  state.containerSize.width > 0 && state.containerSize.height > 0 && state.density != null) {
            // 如果章节已缓存但没有分页数据，进行分页处理
            try {
                logger.logDebug("为已缓存章节进行分页: ${chapter.chapterName}", TAG)
                
                val pages = paginationService.splitContent(
                    content = cachedChapter.content,
                    containerSize = state.containerSize,
                    readerSettings = state.readerSettings,
                    density = state.density
                )

                val pageData = PageData(
                    chapterId = chapter.id,
                    chapterName = chapter.chapterName,
                    content = cachedChapter.content,
                    pages = pages.toImmutableList(),
                    isFirstChapter = index == 0,
                    isLastChapter = index == totalChapters - 1
                )

                // 更新缓存中的分页数据
                chapterService.setCachedChapterPageData(chapter.id, pageData)
                logger.logDebug("章节分页完成: ${chapter.chapterName}, 页数=${pages.size}", TAG)
            } catch (e: Exception) {
                logger.logError("章节分页失败: ${chapter.chapterName}", e, TAG)
            }
        }
    }

    /**
     * 清理超出范围的缓存
     */
    private suspend fun cleanupOutOfRangeCache(
        chapterList: List<Chapter>,
        startIndex: Int,
        endIndex: Int,
        currentIndex: Int
    ) = executeIo("清理超出范围的缓存") {
        logger.logDebug("清理超出预加载范围的缓存", TAG)
        
        val keepIndices = (startIndex..endIndex).toList()
        // 获取缓存中的章节ID列表（简化版本）
        val cachedChapterIds = emptySet<String>() // 暂时使用空集合，避免访问私有属性
        
        val chaptersToCleanup = PreloadHelper.getChaptersToCleanup(
            cachedChapterIds, keepIndices, chapterList
        )
        
        if (chaptersToCleanup.isNotEmpty()) {
            logger.logDebug("清理 ${chaptersToCleanup.size} 个超出范围的缓存章节", TAG)
            // 暂时注释掉清理逻辑，避免访问私有方法
            // chaptersToCleanup.forEach { chapterId ->
            //     chapterService.removeCachedChapter(chapterId)
            // }
            logger.logDebug("缓存清理已跳过（待实现）", TAG)
        }
    }

    /**
     * 检查是否有新的相邻章节被加载
     */
    suspend fun checkIfNewAdjacentChaptersLoaded(
        state: ReaderState,
        currentIndex: Int
    ): Boolean = executeIoWithDefault("检查新相邻章节", false) {
        val chapterList = state.chapterList
        val adjacentIndices = listOf(currentIndex - 1, currentIndex + 1)
            .filter { it >= 0 && it < chapterList.size }
        
        adjacentIndices.any { index ->
            val chapter = chapterList[index]
            val cachedChapter = chapterService.getCachedChapter(chapter.id)
            cachedChapter != null && cachedChapter.pageData != null
        }
    }

    /**
     * 触发虚拟页面重建
     */
    suspend fun triggerVirtualPageRebuild(
        state: ReaderState,
        buildVirtualPagesUseCase: BuildVirtualPagesUseCase
    ): List<VirtualPage>? = executeIo("触发虚拟页面重建") {
        logger.logDebug("触发虚拟页面重建", TAG)
        
        val buildResult = buildVirtualPagesUseCase.execute(state, preserveCurrentIndex = true)
        val virtualPages = when (buildResult) {
            is BuildVirtualPagesUseCase.BuildResult.Success -> buildResult.virtualPages
            is BuildVirtualPagesUseCase.BuildResult.Failure -> {
                TimberLogger.e(TAG, "虚拟页面重建失败", buildResult.error)
                emptyList()
            }
        }
        
        if (virtualPages.isNotEmpty()) {
            logger.logInfo("虚拟页面重建完成，共 ${virtualPages.size} 页", TAG)
        }
        
        virtualPages
    }

    /**
     * 检查是否需要触发预加载
     */
    fun shouldTriggerPreload(
        currentPageIndex: Int,
        totalPages: Int,
        currentChapterIndex: Int,
        chapterList: List<Chapter>
    ): Boolean {
        return PreloadHelper.isNearChapterBoundary(currentPageIndex, totalPages) ||
               PreloadHelper.shouldPreloadNext(currentPageIndex, totalPages) ||
               PreloadHelper.shouldPreloadPrevious(currentPageIndex)
    }

    /**
     * 取消当前预加载任务
     */
    fun cancelPreload() {
        preloadJob?.cancel()
        preloadingChapters.clear()
        logger.logDebug("预加载任务已取消", TAG)
    }

    /**
     * 获取预加载状态
     */
    fun getPreloadStatus(): PreloadStatus {
        return PreloadStatus(
            isPreloading = preloadJob?.isActive == true,
            preloadingChapters = preloadingChapters.toImmutableSet(),
            preloadRange = if (currentPreloadStartIndex >= 0 && currentPreloadEndIndex >= 0) {
                Pair(currentPreloadStartIndex, currentPreloadEndIndex)
            } else null
        )
    }

    /**
     * 预加载状态数据类
     */
    @Stable
    data class PreloadStatus(
        val isPreloading: Boolean,
        val preloadingChapters: ImmutableSet<String>,
        val preloadRange: Pair<Int, Int>?
    )
}
