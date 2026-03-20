package com.novel.page.home.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.novel.utils.network.api.front.SearchService
import com.novel.utils.TimberLogger
import com.novel.utils.network.cache.CacheStrategy
import javax.inject.Inject
import androidx.compose.runtime.Stable

/**
 * 分类推荐书籍的PagingSource
 * 处理服务器端分页逻辑：每次从API获取一页数据
 */
@Stable
class CategoryRecommendPagingSource @Inject constructor(
    @Stable
    private val getCategoryRecommendBooksUseCase: GetCategoryRecommendBooksUseCase,
    @Stable
    private val categoryId: Int
) : PagingSource<Int, SearchService.BookInfo>() {
    
    companion object {
        private const val TAG = "CategoryRecommendPagingSource"
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 8
    }
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchService.BookInfo> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            TimberLogger.d(TAG, "Loading page: $page for categoryId: $categoryId")
            
            val response = getCategoryRecommendBooksUseCase(
                GetCategoryRecommendBooksUseCase.Params(
                    categoryId = categoryId,
                    pageNum = page,
                    pageSize = PAGE_SIZE,
                    strategy = CacheStrategy.CACHE_FIRST
                )
            )
            
            val books = response.list
            val totalPages = response.pages.toInt()
            
            // 计算下一页和上一页
            val nextKey = if (page >= totalPages || books.size < PAGE_SIZE) null else page + 1
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            
            TimberLogger.d(
                TAG, 
                "Page $page loaded: ${books.size} items for categoryId: $categoryId, " +
                "totalPages: $totalPages, nextKey: $nextKey, prevKey: $prevKey"
            )
            
            LoadResult.Page(
                data = books,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Error loading page for categoryId: $categoryId", e)
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, SearchService.BookInfo>): Int? {
        // 返回最接近当前位置的页面键
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

/**
 * CategoryRecommendPagingSource的工厂类
 * 用于根据categoryId创建不同的PagingSource实例
 */
@Stable
class CategoryRecommendPagingSourceFactory @Inject constructor(
    @Stable
    private val getCategoryRecommendBooksUseCase: GetCategoryRecommendBooksUseCase
) {
    
    fun create(categoryId: Int): CategoryRecommendPagingSource {
        return CategoryRecommendPagingSource(getCategoryRecommendBooksUseCase, categoryId)
    }
} 
