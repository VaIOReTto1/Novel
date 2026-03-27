package com.novel.page.read.service

import android.annotation.SuppressLint
import java.util.Locale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Density
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ReaderServiceConfig
import com.novel.page.read.service.common.SafeService
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.service.common.SessionCache
import com.novel.page.read.utils.ReaderLogTags
import com.novel.page.read.viewmodel.Chapter
import com.novel.page.read.viewmodel.ChapterCache
import com.novel.page.read.viewmodel.PageData
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.api.getBookContentHighPriority
import com.novel.utils.network.api.getBookChaptersHighPriority
import com.novel.utils.network.api.front.BookService
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 章节服务类 - 重构优化版
 * 
 * 负责管理章节内容的获取、缓存和预加载：
 * 1. 章节列表的获取和缓存
 * 2. 章节内容的加载和预加载
 * 3. 会话级内存缓存管理（基于SessionCache接口）
 * 4. 章节分页数据的管理
 * 5. 多级缓存策略（内存缓存 + 磁盘缓存 + 网络获取）
 * 
 * 优化特性：
 * - 继承SafeService统一异步调度和错误处理
 * - 使用SessionCache接口管理内存缓存
 * - 支持缓存统计和性能监控
 * - 结构化日志记录
 * - 配置化参数管理
 */
@Singleton
class ChapterService @Inject constructor(
    private val cachedBookRepository: CachedBookRepository,
    private val sessionCache: SessionCache<String, ChapterCache>,
    private val bookService: BookService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : SafeService(dispatchers, logger) {

    companion object {
        private const val TAG = ReaderLogTags.CHAPTER_SERVICE
    }

    override fun getServiceTag(): String = TAG

    /**
     * 获取章节列表
     * 
     * 从CachedBookRepository获取指定书籍的章节列表
     * 
     * @param bookId 书籍ID
     * @return 章节列表
     */
    suspend fun getChapterList(bookId: String): List<Chapter> {
        return safeIoWithDefault(emptyList()) {
            withPerformanceMonitoring("getChapterList") {
                logger.logDebug("获取章节列表: bookId=$bookId", TAG)
                
                // 获取章节列表，优先使用高优先级网络请求确保数据最新
                val chapters = try {
                    cachedBookRepository.getBookChaptersWithIncrementalSync(
                        bookId = bookId.toLong(),
                        strategy = com.novel.utils.network.cache.CacheStrategy.NETWORK_FIRST
                    )
                } catch (e: Exception) {
                    logger.logWarning("缓存获取章节失败，使用高优先级网络请求: ${e.message}", TAG)
                    bookService.getBookChaptersHighPriority(bookId.toLong()).data ?: emptyList()
                }
                
                val result = chapters.map { chapter ->
                    Chapter(
                        id = chapter.id.toString(),
                        chapterName = chapter.chapterName,
                        chapterNum = chapter.chapterNum.toString(),
                        isVip = if (chapter.isVip == 1) "1" else "0"
                    )
                }
                
                logger.logInfo("章节列表获取成功: ${result.size}章", TAG)
                result
            }
        }
    }

    /**
     * 获取章节内容
     * 
     * 优先从会话缓存获取，缓存未命中时从Repository获取
     * 
     * @param chapterId 章节ID
     * @return 章节内容数据，失败返回null
     */
    suspend fun getChapterContent(chapterId: String): ChapterContentData? {
        return withPerformanceMonitoring("getChapterContent") {
            logger.logDebug("获取章节内容: chapterId=$chapterId", TAG)
            
            // 1. 优先从会话缓存获取
            sessionCache.get(chapterId)?.let { cachedChapter ->
                logger.logDebug("从会话缓存命中: ${cachedChapter.chapter.chapterName}", TAG)
                logCacheStats()
                return@withPerformanceMonitoring ChapterContentData(
                    chapterId = cachedChapter.chapter.id,
                    chapterName = cachedChapter.chapter.chapterName,
                    content = cachedChapter.content
                )
            }
            
            // 2. 从Repository获取，失败时使用高优先级请求
            val contentData = safeIo {
                try {
                    cachedBookRepository.getBookContentWithIncrementalSync(
                        chapterId = chapterId.toLong(),
                        strategy = com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST
                    )
                } catch (e: Exception) {
                    logger.logWarning("缓存获取内容失败，使用高优先级网络请求: ${e.message}", TAG)
                    bookService.getBookContentHighPriority(chapterId.toLong()).data
                }
            }
            
            if (contentData != null) {
                logger.logInfo("从Repository获取章节内容成功: ${contentData.chapterInfo.chapterName}", TAG)
                
                // 3. 创建章节对象并添加到会话缓存
                val chapter = Chapter(
                    id = contentData.chapterInfo.id.toString(),
                    chapterName = contentData.chapterInfo.chapterName,
                    chapterNum = contentData.chapterInfo.chapterNum.toString(),
                    isVip = if (contentData.chapterInfo.isVip == 1) "1" else "0"
                )
                
                val chapterCache = ChapterCache(
                    chapter = chapter,
                    content = contentData.bookContent
                )
                
                addToSessionCache(chapterId, chapterCache)
                
                return@withPerformanceMonitoring ChapterContentData(
                    chapterId = chapter.id,
                    chapterName = chapter.chapterName,
                    content = contentData.bookContent
                )
            } else {
                logger.logWarning("章节内容获取失败: chapterId=$chapterId", TAG)
                return@withPerformanceMonitoring null
            }
        }
    }

    /**
     * 预加载章节内容
     * 
     * 异步预加载指定章节，用于提升阅读体验
     * 预加载的内容会存储在会话缓存中
     * 
     * 优化：如果有容器尺寸和阅读设置信息，同时进行分页处理
     * 
     * @param chapterId 章节ID
     * @param containerSize 容器尺寸（可选，用于分页）
     * @param readerSettings 阅读设置（可选，用于分页）
     * @param density 屏幕密度（可选，用于分页）
     */
    suspend fun preloadChapter(
        chapterId: String,
        containerSize: IntSize? = null,
        readerSettings: ReaderSettings? = null,
        density: Density? = null
    ) {
        safeIo {
            withPerformanceMonitoring("preloadChapter") {
                // 检查是否已在会话缓存中
                val cachedChapter = sessionCache.get(chapterId)
                if (cachedChapter != null) {
                    logger.logDebug("章节已在会话缓存中，跳过预加载: chapterId=$chapterId", TAG)
                    
                    // 如果章节已缓存但没有分页数据，且有必要的参数，则进行分页
                    if (cachedChapter.pageData == null && 
                        containerSize != null && readerSettings != null && density != null) {
                        
                        logger.logDebug("为已缓存章节添加分页数据: ${cachedChapter.chapter.chapterName}", TAG)
                        
                        // 进行分页处理
                        val pages = com.novel.page.read.utils.PageSplitter.splitContent(
                            content = cachedChapter.content,
                            containerSize = containerSize,
                            readerSettings = readerSettings,
                            density = density
                        )
                        
                        val pageData = com.novel.page.read.viewmodel.PageData(
                            chapterId = cachedChapter.chapter.id,
                            chapterName = cachedChapter.chapter.chapterName,
                            content = cachedChapter.content,
                            pages = pages.toImmutableList(),
                            isFirstChapter = false, // 这里简化处理
                            isLastChapter = false   // 这里简化处理
                        )
                        
                        val updatedChapter = cachedChapter.copy(pageData = pageData)
                        sessionCache.put(chapterId, updatedChapter)
                        
                        logger.logDebug("章节分页完成: ${cachedChapter.chapter.chapterName}, 页数=${pages.size}", TAG)
                    }
                    
                    return@withPerformanceMonitoring
                }
                
                logger.logDebug("开始预加载章节: chapterId=$chapterId", TAG)
                
                // 从Repository获取内容，失败时使用高优先级请求
                val contentData = try {
                    cachedBookRepository.getBookContentWithIncrementalSync(
                        chapterId = chapterId.toLong(),
                        strategy = com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST
                    )
                } catch (e: Exception) {
                    logger.logWarning("预加载缓存获取失败，使用高优先级网络请求: ${e.message}", TAG)
                    bookService.getBookContentHighPriority(chapterId.toLong()).data
                }
                
                if (contentData != null) {
                    val chapter = Chapter(
                        id = contentData.chapterInfo.id.toString(),
                        chapterName = contentData.chapterInfo.chapterName,
                        chapterNum = contentData.chapterInfo.chapterNum.toString(),
                        isVip = if (contentData.chapterInfo.isVip == 1) "1" else "0"
                    )
                    
                    var chapterCache = ChapterCache(
                        chapter = chapter,
                        content = contentData.bookContent
                    )
                    
                    // 如果有分页参数，同时进行分页处理
                    if (containerSize != null && readerSettings != null && density != null && 
                        containerSize.width > 0 && containerSize.height > 0) {
                        
                        logger.logDebug("预加载时同时进行分页处理: ${chapter.chapterName}", TAG)
                        
                        val pages = com.novel.page.read.utils.PageSplitter.splitContent(
                            content = contentData.bookContent,
                            containerSize = containerSize,
                            readerSettings = readerSettings,
                            density = density
                        )
                        
                        val pageData = com.novel.page.read.viewmodel.PageData(
                            chapterId = chapter.id,
                            chapterName = chapter.chapterName,
                            content = contentData.bookContent,
                            pages = pages.toImmutableList(),
                            isFirstChapter = false, // 这里简化处理
                            isLastChapter = false   // 这里简化处理
                        )
                        
                        chapterCache = chapterCache.copy(pageData = pageData)
                        logger.logDebug("预加载分页完成: ${chapter.chapterName}, 页数=${pages.size}", TAG)
                    }
                    
                    addToSessionCache(chapterId, chapterCache)
                    logger.logInfo("章节预加载完成: ${chapter.chapterName}", TAG)
                } else {
                    logger.logWarning("章节预加载失败: chapterId=$chapterId", TAG)
                }
            }
        }
    }

    /**
     * 获取会话缓存中的章节
     * 
     * @param chapterId 章节ID
     * @return 缓存的章节数据，未找到返回null
     */
    fun getCachedChapter(chapterId: String): ChapterCache? {
        return sessionCache.get(chapterId)
    }

    /**
     * 设置缓存章节的分页数据
     * 
     * @param chapterId 章节ID
     * @param pageData 分页数据
     */
    fun setCachedChapterPageData(chapterId: String, pageData: PageData) {
        sessionCache.get(chapterId)?.let { cachedChapter ->
            val updatedChapter = cachedChapter.copy(pageData = pageData)
            sessionCache.put(chapterId, updatedChapter)
            logger.logDebug("更新章节分页数据: ${cachedChapter.chapter.chapterName}, 页数=${pageData.pages.size}", TAG)
        }
    }

    /**
     * 加载书籍信息
     *
     * 用于在阅读器首页显示书籍详情
     *
     * @param bookId 书籍ID
     * @return 书籍信息，失败时返回null
     */
    suspend fun loadBookInfo(bookId: String): PageData.BookInfo? {
        val bookIdLong = bookId.toLongOrNull() ?: return null

        return safeIo {
            withPerformanceMonitoring("loadBookInfo") {
                val bookInfoData = cachedBookRepository.getBookInfoWithIncrementalSync(bookIdLong)
                bookInfoData?.let {
                    PageData.BookInfo(
                        bookId = bookId,
                        bookName = it.bookName,
                        authorName = it.authorName,
                        bookDesc = it.bookDesc,
                        picUrl = it.picUrl,
                        visitCount = it.visitCount,
                        wordCount = it.wordCount,
                        categoryName = it.categoryName
                    )
                }
            }
        }
    }

    /**
     * 添加到会话缓存
     * 
     * 使用SessionCache接口管理缓存大小
     * 
     * @param chapterId 章节ID
     * @param chapterCache 章节缓存数据
     */
    private fun addToSessionCache(chapterId: String, chapterCache: ChapterCache) {
        sessionCache.put(chapterId, chapterCache)
        logger.logDebug("添加到会话缓存: ${chapterCache.chapter.chapterName}", TAG)
        logCacheStats()
    }

    /**
     * 记录缓存统计信息
     */
    @SuppressLint("DefaultLocale")
    private fun logCacheStats() {
        if (ReaderServiceConfig.ENABLE_PERFORMANCE_LOGGING) {
            val stats = sessionCache.getStats()
            logger.logPerformance(
                "SessionCache: size=${stats.size}/${stats.maxSize}, " +
                "hitRate=${String.format(Locale.US, "%.2f", stats.hitRate * 100)}%, " +
                "evictions=${stats.evictionCount}",
                0, TAG
            )
        }
    }

    /**
     * 清空会话缓存
     * 
     * 在阅读会话结束时调用，释放内存
     */
    fun clearSessionCache() {
        val stats = sessionCache.getStats()
        sessionCache.clear()
        logger.logInfo("清空会话缓存: ${stats.size}个章节", TAG)
    }

    /**
     * 章节内容数据
     */
    data class ChapterContentData(
        val chapterId: String,
        val chapterName: String,
        val content: String
    )
}
