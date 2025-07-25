package com.novel.page.search.service

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.page.search.component.SearchRankingItem
import com.novel.utils.network.cache.CacheStrategy
import com.novel.utils.network.repository.CachedBookRepository
import com.novel.utils.network.api.front.BookService
import com.novel.utils.network.api.getNewestRankBooksHighPriority
import com.novel.utils.network.api.getUpdateRankBooksHighPriority
import com.novel.utils.network.api.getVisitRankBooksHighPriority
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 搜索服务类
 * 
 * 核心功能：
 * - 榜单数据获取：热门小说、热门短剧、新书排行榜
 * - 缓存优先策略：提升响应速度，减少网络请求
 * - 数据转换：将API响应转换为UI所需的数据格式
 * - 异常处理：网络异常时返回空列表，确保应用稳定性
 * 
 * 设计特点：
 * - 单例模式，全局唯一实例
 * - 依赖注入CachedBookRepository
 * - 统一的异常处理和日志记录
 * - 支持不同类型榜单的数据获取
 */
@Stable
@Singleton
class SearchService @Inject constructor(
    /** 缓存书籍仓库，提供带缓存的数据访问 */
    private val cachedBookRepository: CachedBookRepository,
    private val bookService: BookService
) {
    
    companion object {
        private const val TAG = "SearchService"
    }
    
    /**
     * 获取热门小说榜单
     * 
     * 基于访问量排序的小说榜单，使用缓存优先策略
     * @return 热门小说排行榜列表，失败时返回空列表
     */
    suspend fun getHotNovelRanking(): List<SearchRankingItem> {
        return try {
            TimberLogger.d(TAG, "开始获取热门小说榜单")
            val rankBooks = try {
                cachedBookRepository.getVisitRankBooks(CacheStrategy.CACHE_FIRST)
            } catch (e: Exception) {
                TimberLogger.w(TAG, "缓存获取热门小说榜单失败，使用高优先级网络请求: ${e.message}")
                bookService.getVisitRankBooksHighPriority().data ?: emptyList()
            }
            
            val result = rankBooks.mapIndexed { index, book ->
                SearchRankingItem(
                    id = book.id,
                    title = book.bookName,
                    author = book.authorName,
                    rank = index + 1
                )
            }
            
            TimberLogger.d(TAG, "热门小说榜单获取成功，共${result.size}本书")
            result
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取热门小说榜单失败", e)
            emptyList()
        }
    }
    
    /**
     * 获取热门短剧榜单
     * 
     * 基于更新频率排序的短剧榜单，使用缓存优先策略
     * @return 热门短剧排行榜列表，失败时返回空列表
     */
    suspend fun getHotDramaRanking(): List<SearchRankingItem> {
        return try {
            TimberLogger.d(TAG, "开始获取热门短剧榜单")
            val rankBooks = try {
                cachedBookRepository.getUpdateRankBooks(CacheStrategy.CACHE_FIRST)
            } catch (e: Exception) {
                TimberLogger.w(TAG, "缓存获取热门短剧榜单失败，使用高优先级网络请求: ${e.message}")
                bookService.getUpdateRankBooksHighPriority().data ?: emptyList()
            }
            
            val result = rankBooks.mapIndexed { index, book ->
                SearchRankingItem(
                    id = book.id,
                    title = book.bookName,
                    author = book.authorName,
                    rank = index + 1
                )
            }
            
            TimberLogger.d(TAG, "热门短剧榜单获取成功，共${result.size}本书")
            result
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取热门短剧榜单失败", e)
            emptyList()
        }
    }
    
    /**
     * 获取新书榜单
     * 
     * 基于发布时间排序的新书榜单，使用缓存优先策略
     * @return 新书排行榜列表，失败时返回空列表
     */
    suspend fun getNewBookRanking(): List<SearchRankingItem> {
        return try {
            TimberLogger.d(TAG, "开始获取新书榜单")
            val rankBooks = try {
                cachedBookRepository.getNewestRankBooks(CacheStrategy.CACHE_FIRST)
            } catch (e: Exception) {
                TimberLogger.w(TAG, "缓存获取新书榜单失败，使用高优先级网络请求: ${e.message}")
                bookService.getNewestRankBooksHighPriority().data ?: emptyList()
            }
            
            val result = rankBooks.mapIndexed { index, book ->
                SearchRankingItem(
                    id = book.id,
                    title = book.bookName,
                    author = book.authorName,
                    rank = index + 1
                )
            }
            
            TimberLogger.d(TAG, "新书榜单获取成功，共${result.size}本书")
            result
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取新书榜单失败", e)
            emptyList()
        }
    }
}