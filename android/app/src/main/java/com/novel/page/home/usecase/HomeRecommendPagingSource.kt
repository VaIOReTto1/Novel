package com.novel.page.home.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.novel.utils.network.api.front.HomeService
import com.novel.utils.TimberLogger
import com.novel.utils.network.cache.CacheStrategy
import javax.inject.Inject
import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * 首页推荐书籍的PagingSource
 * 处理客户端分页逻辑：一次性加载所有数据，然后按页分割
 */
@Stable
class HomeRecommendPagingSource @Inject constructor(
    @Stable
    private val getHomeRecommendBooksUseCase: GetHomeRecommendBooksUseCase
) : PagingSource<Int, HomeService.HomeBook>() {
    
    companion object {
        private const val TAG = "HomeRecommendPagingSource"
        private const val STARTING_PAGE_INDEX = 1
        private const val PAGE_SIZE = 8
    }
    
    // 缓存全部数据，避免重复网络请求 - 使用@Stable标记
    @Stable
    @Volatile // 添加 @Volatile 确保线程安全
    private var cachedBooks: ImmutableList<HomeService.HomeBook>? = null
    
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeService.HomeBook> {
        return try {
            val page = params.key ?: STARTING_PAGE_INDEX
            TimberLogger.d(TAG, "Loading page: $page, load size: ${params.loadSize}")
            
            // 如果是首次加载或缓存为空，从网络获取所有数据
            if (cachedBooks == null || page == STARTING_PAGE_INDEX) {
                val strategy = if (page == STARTING_PAGE_INDEX) {
                    CacheStrategy.CACHE_FIRST
                } else {
                    CacheStrategy.CACHE_FIRST
                }
                
                cachedBooks = getHomeRecommendBooksUseCase(
                    GetHomeRecommendBooksUseCase.Params(strategy = strategy)
                ).toImmutableList()
                TimberLogger.d(TAG, "Loaded ${cachedBooks?.size} total books from network")
            }
            
            val allBooks = cachedBooks ?: emptyList<HomeService.HomeBook>().toImmutableList()
            
            // 计算当前页的数据
            val startIndex = (page - 1) * PAGE_SIZE
            val endIndex = startIndex + PAGE_SIZE
            val pageData = allBooks.drop(startIndex).take(PAGE_SIZE)
            
            // 计算下一页和上一页
            val nextKey = if (endIndex >= allBooks.size) null else page + 1
            val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
            
            TimberLogger.d(
                TAG, 
                "Page $page loaded: ${pageData.size} items, " +
                "startIndex: $startIndex, endIndex: $endIndex, " +
                "nextKey: $nextKey, prevKey: $prevKey"
            )
            
            LoadResult.Page(
                data = pageData,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Error loading page", e)
            LoadResult.Error(e)
        }
    }
    
    override fun getRefreshKey(state: PagingState<Int, HomeService.HomeBook>): Int? {
        // 返回最接近当前位置的页面键
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
    
    /**
     * 清除缓存，强制重新加载数据
     */
    fun clearCache() {
        cachedBooks = null
        TimberLogger.d(TAG, "Cache cleared")
    }
}
