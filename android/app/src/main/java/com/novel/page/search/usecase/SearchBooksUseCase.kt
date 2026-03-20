package com.novel.page.search.usecase

import androidx.compose.runtime.Stable
import com.novel.utils.TimberLogger
import com.novel.page.search.repository.SearchRepository
import com.novel.page.search.repository.PageRespDtoBookInfoRespDto
import com.novel.page.search.viewmodel.FilterState
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

/**
 * 搜索书籍业务用例
 * 
 * 职责：
 * - 封装书籍搜索业务逻辑
 * - 统一筛选条件处理
 * - 数据模型转换适配
 * - 分页查询支持
 * 
 * 设计模式：
 * - Clean Architecture用例层
 * - Repository模式数据访问
 * - Result包装错误处理
 */
@Stable
class SearchBooksUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    
    companion object {
        private const val TAG = "SearchBooksUseCase"
    }
    
    /**
     * 执行书籍搜索
     * @param keyword 搜索关键词
     * @param categoryId 分类ID（可选）
     * @param filters 筛选条件
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页大小
     * @return 搜索结果包装
     */
    suspend fun execute(
        keyword: String,
        categoryId: Int? = null,
        filters: FilterState = FilterState(),
        pageNum: Int = 1,
        pageSize: Int = 20
    ): Result<PageRespDtoBookInfoRespDto> {
        return try {
            TimberLogger.d(TAG, "开始搜索书籍: keyword='$keyword', page=$pageNum")
            
            val response = searchRepository.searchBooks(
                keyword = keyword,
                workDirection = null, // 可以根据需要设置
                categoryId = categoryId,
                isVip = filters.isVip.value,
                bookStatus = filters.updateStatus.value,
                wordCountMin = filters.wordCountRange.min,
                wordCountMax = filters.wordCountRange.max,
                updateTimeMin = null, // 可以根据需要设置
                sort = filters.sortBy.value,
                pageNum = pageNum,
                pageSize = pageSize
            )
            
            if (response?.ok == true && response.data != null) {
                TimberLogger.d(TAG, "搜索成功: 共${response.data.total}条结果")
                
                // 转换SearchService的响应数据为期望的类型
                val pageResp = PageRespDtoBookInfoRespDto(
                    pageNum = response.data.pageNum,
                    pageSize = response.data.pageSize,
                    total = response.data.total,
                    list = response.data.list.map { searchBook ->
                        // 转换SearchService.BookInfo为BookInfoRespDto
                        val bookInfoRespDto = com.novel.page.search.viewmodel.BookInfoRespDto(
                            id = searchBook.id,
                            categoryId = searchBook.categoryId,
                            categoryName = searchBook.categoryName,
                            picUrl = searchBook.picUrl,
                            bookName = searchBook.bookName,
                            authorId = searchBook.authorId,
                            authorName = searchBook.authorName,
                            bookDesc = searchBook.bookDesc,
                            bookStatus = searchBook.bookStatus,
                            visitCount = searchBook.visitCount,
                            wordCount = searchBook.wordCount,
                            commentCount = searchBook.commentCount,
                            updateTime = searchBook.updateTime,
                            firstChapterId = searchBook.firstChapterId,
                            lastChapterId = searchBook.lastChapterId,
                            lastChapterName = searchBook.lastChapterName
                        )
                        bookInfoRespDto
                    }.toImmutableList(),
                    pages = response.data.pages
                )
                Result.success(pageResp)
            } else {
                val errorMsg = response?.message ?: "搜索失败"
                TimberLogger.w(TAG, "搜索失败: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "搜索异常", e)
            Result.failure(e)
        }
    }
} 
