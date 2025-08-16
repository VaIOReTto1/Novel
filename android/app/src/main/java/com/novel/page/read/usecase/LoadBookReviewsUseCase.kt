package com.novel.page.read.usecase

import com.novel.page.read.usecase.common.BaseUseCase
import com.novel.page.read.viewmodel.BookReview
import com.novel.utils.network.api.front.BookService
import com.novel.utils.TimberLogger
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.utils.ReaderLogTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * 加载书籍评论用例
 * 
 * 负责从API获取真实评论数据，并在数据不足时补充mock数据
 * 实现责任分离，将评论数据处理逻辑从UI层移到业务层
 */
class LoadBookReviewsUseCase @Inject constructor(
    private val bookService: BookService,
    dispatchers: DispatcherProvider,
    logger: ServiceLogger
) : BaseUseCase(dispatchers, logger) {

    companion object {
        private const val TAG = "LoadBookReviewsUseCase"
        private const val MIN_REVIEWS_COUNT = 2 // 最少显示2条评论
        private const val MAX_REVIEWS_COUNT = 5 // 最多显示5条评论
    }

    override fun getServiceTag(): String = TAG

    /**
     * 执行加载书籍评论
     * 
     * @param bookId 书籍ID
     * @return 评论列表
     */
    suspend fun execute(bookId: String): ImmutableList<BookReview> {
        return executeIoWithDefault("加载书籍评论", persistentListOf()) {
            TimberLogger.d(TAG, "开始加载书籍评论: bookId=$bookId")
            
            // 1. 尝试从API获取真实评论数据
            val apiReviews = loadReviewsFromApi(bookId)
            
            // 2. 如果API返回了评论数据，进行数据补充
            if (apiReviews.isNotEmpty()) {
                TimberLogger.d(TAG, "API返回${apiReviews.size}条评论，进行数据补充")
                val enhancedReviews = enhanceReviewsWithMockData(apiReviews)
                enhancedReviews.toImmutableList()
            } else {
                // 3. 如果API没有返回评论数据，使用mock数据
                TimberLogger.d(TAG, "API未返回评论数据，使用mock数据")
                generateMockReviews(bookId).toImmutableList()
            }
        }
    }

    /**
     * 从API获取评论数据
     */
    private suspend fun loadReviewsFromApi(bookId: String): List<BookReview> {
        return try {
            val bookIdLong = bookId.toLongOrNull() ?: return emptyList()
            val response = bookService.getNewestCommentsBlocking(bookIdLong)
            
            if (response.ok == true && response.data != null) {
                val apiComments = response.data.comments
                val commentTotal = response.data.commentTotal
                
                TimberLogger.d(TAG, "API返回评论总数: $commentTotal, 实际返回: ${apiComments.size}")
                
                // 转换API数据格式
                apiComments.map { comment ->
                    BookReview(
                        id = comment.id.toString(),
                        content = comment.commentContent,
                        rating = generateRandomRating(), // API没有评分字段，随机生成
                        readTime = generateRandomReadTime(), // API没有阅读时间，随机生成
                        userName = comment.commentUser,
                        userPhoto = comment.commentUserPhoto,
                        commentTime = comment.commentTime
                    )
                }
            } else {
                TimberLogger.w(TAG, "API返回数据无效: ok=${response.ok}, data=${response.data}")
                emptyList()
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "API获取评论失败", e)
            emptyList()
        }
    }

    /**
     * 使用mock数据补充评论
     */
    private fun enhanceReviewsWithMockData(apiReviews: List<BookReview>): List<BookReview> {
        val enhancedReviews = mutableListOf<BookReview>()
        enhancedReviews.addAll(apiReviews)
        
        // 如果API评论数量不足，补充mock数据
        if (enhancedReviews.size < MIN_REVIEWS_COUNT) {
            val mockReviews = generateMockReviews("mock")
            val neededCount = MIN_REVIEWS_COUNT - enhancedReviews.size
            
            for (i in 0 until neededCount) {
                if (i < mockReviews.size) {
                    enhancedReviews.add(mockReviews[i])
                }
            }
        }
        
        // 限制最大显示数量
        return enhancedReviews.take(MAX_REVIEWS_COUNT)
    }

    /**
     * 生成mock评论数据
     */
    private fun generateMockReviews(bookId: String): List<BookReview> {
        val mockReviews = listOf(
            BookReview(
                id = "mock_1",
                content = "这个职业(老板)无敌了，全天下的天才为之打工。",
                rating = 5,
                readTime = "阅读54分钟后点评",
                userName = "用户1",
                userPhoto = "https://example.com/avatar1.jpg",
                commentTime = "2024-01-15 14:30:00"
            ),
            BookReview(
                id = "mock_2",
                content = "很不错的脑洞，题材也很新颖，就是主角有点感太低了，全是手下在发力，主角变考全程躺平...",
                rating = 4,
                readTime = "阅读2小时后点评",
                userName = "用户2",
                userPhoto = "https://example.com/avatar2.jpg",
                commentTime = "2024-01-14 16:45:00"
            ),
            BookReview(
                id = "mock_3",
                content = "情节紧凑，人物塑造生动，值得一读的好书！",
                rating = 5,
                readTime = "阅读1小时后点评",
                userName = "书虫小王",
                userPhoto = "https://example.com/avatar3.jpg",
                commentTime = "2024-01-13 20:15:00"
            ),
            BookReview(
                id = "mock_4",
                content = "作者文笔不错，世界观设定很独特，期待后续发展。",
                rating = 4,
                readTime = "阅读3小时后点评",
                userName = "文学爱好者",
                userPhoto = "https://example.com/avatar4.jpg",
                commentTime = "2024-01-12 11:20:00"
            ),
            BookReview(
                id = "mock_5",
                content = "整体来说还不错，就是更新有点慢，希望作者能保持质量。",
                rating = 4,
                readTime = "阅读1.5小时后点评",
                userName = "追更达人",
                userPhoto = "https://example.com/avatar5.jpg",
                commentTime = "2024-01-11 09:30:00"
            )
        )
        
        return mockReviews
    }

    /**
     * 生成随机评分（4-5星）
     */
    private fun generateRandomRating(): Int {
        return (4..5).random()
    }

    /**
     * 生成随机阅读时间
     */
    private fun generateRandomReadTime(): String {
        val times = listOf(
            "阅读30分钟后点评",
            "阅读1小时后点评",
            "阅读2小时后点评",
            "阅读3小时后点评",
            "阅读1天后点评"
        )
        return times.random()
    }
}
