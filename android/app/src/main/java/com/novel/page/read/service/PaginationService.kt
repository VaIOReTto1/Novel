package com.novel.page.read.service

import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.novel.page.read.repository.BookCacheData
import com.novel.page.read.repository.BookCacheManager
import com.novel.page.read.repository.PageCountCacheData
import com.novel.page.read.service.common.*
import com.novel.page.read.utils.PageSplitter
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.network.repository.CachedBookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.collections.immutable.toImmutableList

/**
 * 分页服务类 - 重构优化版
 * 
 * 负责章节内容的分页计算和全书页数缓存：
 * 1. 单章节内容分页计算
 * 2. 全书页数缓存管理
 * 3. 渐进式分页计算（后台任务）
 * 4. 页数查询和索引转换
 * 
 * 优化特性：
 * - 继承SafeService统一异步调度和错误处理
 * - 使用统一配置管理参数
 * - 结构化日志记录和性能监控
 * - 增强的错误处理和重试机制
 * - 可配置的分页参数
 */
@Singleton
@Stable
class PaginationService @Inject constructor(
    private val bookCacheManager: BookCacheManager,
    private val cachedBookRepository: CachedBookRepository,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : SafeService(dispatchers, logger) {
    
    companion object {
        private const val TAG = "PaginationService"
    }
    
    override fun getServiceTag(): String = TAG
    
    // 分页后台任务管理
    private var paginationJob: Job? = null

    /**
     * 分页状态流
     * 实时反馈渐进式分页的计算进度
     */
    val paginationState: StateFlow<com.novel.page.read.repository.ProgressiveCalculationState>
        get() = bookCacheManager.progressiveCalculationState

    /**
     * 分割章节内容为页面
     * 
     * 使用PageSplitter工具根据容器尺寸和阅读设置进行分页
     * 
     * @param content 章节内容文本
     * @param containerSize 容器尺寸（宽高）
     * @param readerSettings 阅读器设置（字体大小等）
     * @param density 屏幕密度信息
     * @return 分页后的文本列表
     */
    fun splitContent(
        content: String,
        containerSize: IntSize,
        readerSettings: ReaderSettings,
        density: Density
    ): List<String> {
        return withSyncPerformanceMonitoring("splitContent") {
            logger.logDebug("开始分割内容: 字体=${readerSettings.fontSize}sp, " +
                "容器=${containerSize.width}x${containerSize.height}", TAG)
            
            val pages = PageSplitter.splitContent(content, containerSize, readerSettings, density)
            
            logger.logDebug("内容分割完成: ${pages.size}页", TAG)
            pages
        }
    }

    /**
     * 获取页数缓存
     * 
     * 查询指定书籍在给定字体大小和容器尺寸下的分页缓存
     * 
     * @param bookId 书籍ID
     * @param fontSize 字体大小
     * @param containerSize 容器尺寸
     * @return 页数缓存数据，未找到返回null
     */
    suspend fun getPageCountCache(
        bookId: String,
        fontSize: Int,
        containerSize: IntSize
    ): PageCountCacheData? {
        return safeIo {
            withPerformanceMonitoring("getPageCountCache") {
                logger.logDebug("查询页数缓存: bookId=$bookId, 字体=${fontSize}sp, " +
                    "容器=${containerSize.width}x${containerSize.height}", TAG)
                
                val cache = bookCacheManager.getPageCountCache(bookId, fontSize, containerSize)
                
                if (cache != null) {
                    logger.logDebug("页数缓存命中: 总页数=${cache.totalPages}", TAG)
                } else {
                    logger.logDebug("页数缓存未命中", TAG)
                }
                
                cache
            }
        }
    }

    /**
     * 获取全书内容并在后台进行分页计算
     * 
     * 该函数负责：
     * 1. 检查或构建完整的书籍内容缓存
     * 2. 启动后台渐进式分页计算  
     * 3. 实时更新分页进度状态
     * 4. 处理全书页数缓存的生成和更新
     * 
     * @param bookId 书籍ID
     * @param chapterList 章节列表
     * @param readerSettings 阅读器设置
     * @param containerSize 容器尺寸
     * @param density 屏幕密度
     */
    fun fetchAllBookContentAndPaginateInBackground(
        bookId: String,
        chapterList: List<Chapter>,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density
    ) {
        withSyncPerformanceMonitoring("fetchAllBookContentAndPaginateInBackground") {
            logger.logInfo("启动后台全书分页计算: bookId=$bookId, 章节数=${chapterList.size}", TAG)
            
            // 取消之前的分页任务，确保只有一个任务在运行
            paginationJob?.cancel()
            paginationJob = CoroutineScope(dispatchers.io).launch {
                safeIo {
                    // 1. 获取或构建书籍内容缓存
                    val bookCache = bookCacheManager.getBookContentCache(bookId)
                    val allChapterIds = chapterList.map { it.id }.toSet()

                    val completeBookCache = if (bookCache != null && bookCache.chapterIds.toSet() == allChapterIds) {
                        // 缓存完整，直接使用
                        logger.logDebug("书籍缓存完整，直接使用", TAG)
                        bookCache
                    } else {
                        // 缓存不完整，重新构建，并在获取每章后立即分页
                        logger.logDebug("书籍缓存不完整，开始重新构建", TAG)
                        fetchAndBuildBookCache(
                            bookId = bookId,
                            chapterList = chapterList,
                            existingBookCache = bookCache,
                            readerSettings = readerSettings,
                            containerSize = containerSize,
                            density = density
                        )
                    }

                    // 2. 启动渐进式分页计算
                    startProgressivePagination(completeBookCache, readerSettings, containerSize, density)
                } ?: run {
                    logger.logError("后台全书分页计算失败", null, TAG)
                }
            }
        }
    }

    /**
     * 构建或更新书籍内容缓存
     * 
     * 获取缺失的章节内容并更新缓存
     * 
     * @param bookId 书籍ID
     * @param chapterList 完整章节列表
     * @param existingBookCache 现有缓存（可能不完整）
     * @param readerSettings 阅读器设置
     * @param containerSize 容器尺寸
     * @param density 屏幕密度
     * @return 完整的书籍缓存数据
     */
    private suspend fun fetchAndBuildBookCache(
        bookId: String,
        chapterList: List<Chapter>,
        existingBookCache: BookCacheData?,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density
    ): BookCacheData {
        return withPerformanceMonitoring("fetchAndBuildBookCache") {
            val cachedChapters = existingBookCache?.chapters?.toMutableList() ?: mutableListOf()
            // 提前推送"正在计算中"状态
            bookCacheManager.startDynamicPagination(chapterList.size)
            var currentCalculatedPages = 0 // 用于动态进度统计
            val cachedChapterIds = cachedChapters.map { it.chapterId }.toSet()
            val chaptersToFetch = chapterList.filterNot { cachedChapterIds.contains(it.id) }

            logger.logInfo("需要获取${chaptersToFetch.size}个缺失章节", TAG)

            // 顺序处理章节，避免过度并发导致ANR
            val fetchedChapters = mutableListOf<BookCacheData.ChapterContentData>()
            
            // 分批处理章节，避免内存压力
            chaptersToFetch.chunked(ReaderServiceConfig.PAGINATION_BATCH_SIZE).forEach { batch ->
                logger.logDebug("开始处理批次: ${batch.size}个章节", TAG)
                
                batch.forEach { chapter ->
                    val result = safeIoWithRetry(
                        maxRetries = 1, // 减少重试次数，避免雪崩
                        retryDelay = ReaderServiceConfig.RETRY_DELAY_MS
                    ) {
                        val contentData = cachedBookRepository.getBookContentWithIncrementalSync(
                            chapterId = chapter.id.toLong(),
                            strategy = com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST
                        )
                        
                        if (contentData != null) {
                            val chapterData = BookCacheData.ChapterContentData(
                                chapterId = contentData.chapterInfo.id.toString(),
                                chapterName = contentData.chapterInfo.chapterName,
                                content = contentData.bookContent,
                                chapterNum = contentData.chapterInfo.chapterNum
                            )
                            logger.logDebug("章节获取成功: ${contentData.chapterInfo.chapterName}", TAG)

                            // 立即对获取到的章节内容进行分页处理并缓存页数
                            if (containerSize.width > 0 && containerSize.height > 0) {
                                try {
                                    val pages = PageSplitter.splitContent(
                                        content = contentData.bookContent,
                                        containerSize = containerSize,
                                        readerSettings = readerSettings,
                                        density = density
                                    )

                                    // 保存单章节页数缓存，供后续快速命中
                                    bookCacheManager.saveChapterPageCountCache(
                                        chapterId = contentData.chapterInfo.id.toString(),
                                        fontSize = readerSettings.fontSize,
                                        containerSize = containerSize,
                                        pageCount = pages.size
                                    )

                                    logger.logDebug(
                                        "章节分页并缓存完成: ${contentData.chapterInfo.chapterName} -> ${pages.size}页",
                                        TAG
                                    )
                                } catch (e: Exception) {
                                    logger.logError("章节即时分页失败: ${contentData.chapterInfo.chapterName}", e, TAG)
                                }
                            }

                            chapterData
                        } else {
                            logger.logWarning("章节内容为空: ${chapter.chapterName}", TAG)
                            null
                        }
                    }
                    
                    result?.let {
                        // --- 更新动态分页进度 ——
                        currentCalculatedPages += try {
                            val charPages = bookCacheManager.getChapterPageCountCache(
                                it.chapterId,
                                readerSettings.fontSize,
                                containerSize
                            ) ?: 0
                            charPages
                        } catch (_: Exception) { 0 }

                        val calculatedChapters = fetchedChapters.size + cachedChapters.size
                        bookCacheManager.updateDynamicPaginationProgress(
                            currentCalculatedPages = currentCalculatedPages,
                            calculatedChapters = calculatedChapters,
                            totalChapters = chapterList.size
                        )

                        fetchedChapters.add(it)
                    }
                }
                
                // 批次间短暂延迟，减少系统压力
                kotlinx.coroutines.delay(100)
            }

            // 合并所有章节
            cachedChapters.addAll(fetchedChapters)
            
            // 按章节序号排序
            cachedChapters.sortBy { it.chapterNum }

            // 获取书籍信息
            val bookInfoData = safeIo {
                cachedBookRepository.getBookInfoWithIncrementalSync(bookId.toLong())
            }
            
            val bookInfoForCache = bookInfoData?.let {
                BookCacheData.BookInfo(
                    bookName = it.bookName,
                    authorName = it.authorName,
                    bookDesc = it.bookDesc,
                    picUrl = it.picUrl,
                    visitCount = it.visitCount,
                    wordCount = it.wordCount,
                    categoryName = it.categoryName
                )
            }

            val newBookCacheData = BookCacheData(
                bookId = bookId,
                chapters = cachedChapters.toImmutableList(),
                chapterIds = cachedChapters.map { it.chapterId }.toImmutableList(),
                cacheTime = System.currentTimeMillis(),
                bookInfo = bookInfoForCache
            )

            // 保存更新后的缓存
            bookCacheManager.saveBookContentCache(newBookCacheData)
            
            logger.logInfo("书籍缓存构建完成: ${cachedChapters.size}个章节", TAG)
            newBookCacheData
        }
    }

    /**
     * 启动渐进式分页计算
     * 
     * 检查是否已有页数缓存，如果没有则启动后台分页计算
     * 
     * @param bookCacheData 完整的书籍内容缓存
     * @param readerSettings 阅读器设置
     * @param containerSize 容器尺寸
     * @param density 屏幕密度
     */
    private suspend fun startProgressivePagination(
        bookCacheData: BookCacheData,
        readerSettings: ReaderSettings,
        containerSize: IntSize,
        density: Density
    ) {
        if (containerSize == IntSize.Zero) {
            logger.logWarning("容器尺寸无效，跳过分页计算", TAG)
            return
        }

        // 检查是否已有页数缓存
        val existingPageCountCache = bookCacheManager.getPageCountCache(
            bookCacheData.bookId,
            readerSettings.fontSize,
            containerSize
        )
        
        if (existingPageCountCache != null) {
            logger.logDebug("页数缓存已存在，跳过计算: 总页数=${existingPageCountCache.totalPages}", TAG)
            return // 已有缓存，无需重新计算
        }

        // 启动渐进式分页计算
        logger.logInfo("启动渐进式分页计算: ${bookCacheData.chapters.size}个章节", TAG)
        
        safeIo {
            withPerformanceMonitoring("progressivePagination") {
                bookCacheManager.calculateAllPagesProgressively(
                    bookCacheData = bookCacheData,
                    readerSettings = readerSettings,
                    containerSize = containerSize,
                    density = density,
                    onProgressUpdate = { calculatedPages, estimatedTotal ->
                        // 进度更新回调，记录计算进度
                        logger.logPerformance("分页计算进度: $calculatedPages/$estimatedTotal", 0, TAG)
                    }
                )
            }
        } ?: run {
            logger.logError("渐进式分页计算失败", null, TAG)
        }
    }

    /**
     * 根据全局页码查找对应的章节和页面
     * 
     * @param pageCountCache 页数缓存数据
     * @param globalPage 全局页码（从0开始）
     * @return 章节ID和章节内相对页码的配对，未找到返回null
     */
    fun findChapterByAbsolutePage(
        pageCountCache: PageCountCacheData, 
        globalPage: Int
    ): Pair<String, Int>? {
        return withSyncPerformanceMonitoring("findChapterByAbsolutePage") {
            logger.logDebug("查找全局页码对应章节: 页码=$globalPage", TAG)
            
            val result = bookCacheManager.findChapterByAbsolutePage(pageCountCache, globalPage)
            
            if (result != null) {
                logger.logDebug("找到对应章节: ${result.first}, 相对页码=${result.second}", TAG)
            } else {
                logger.logWarning("未找到全局页码对应的章节: 页码=$globalPage", TAG)
            }
            
            result
        }
    }

    /**
     * 取消后台分页任务
     * 在需要时停止正在进行的分页计算
     */
    fun cancelPaginationJob() {
        paginationJob?.cancel()
        paginationJob = null
        logger.logInfo("后台分页任务已取消", TAG)
    }

    /**
     * 获取分页任务状态
     * 
     * @return 分页任务是否正在运行
     */
    fun isPaginationRunning(): Boolean {
        return paginationJob?.isActive == true
    }

    /**
     * 清理分页缓存
     * 
     * @param bookId 书籍ID
     */
    suspend fun clearPaginationCache(bookId: String) {
        safeIo {
            withPerformanceMonitoring("clearPaginationCache") {
                logger.logDebug("清理分页缓存: bookId=$bookId", TAG)
                bookCacheManager.clearBookCache(bookId)
                logger.logInfo("分页缓存清理完成", TAG)
            }
        }
    }
} 