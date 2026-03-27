package com.novel.page.search.repository

import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.toImmutableList

internal data class SearchQueryRequest(
    val keyword: String? = null,
    val workDirection: Int? = null,
    val categoryId: Int? = null,
    val isVip: Int? = null,
    val bookStatus: Int? = null,
    val wordCountMin: Int? = null,
    val wordCountMax: Int? = null,
    val updateTimeMin: String? = null,
    val sort: String? = null,
    val pageNum: Int = 1,
    val pageSize: Int = 20,
    val strategy: com.novel.utils.network.cache.CacheStrategy =
        com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST,
)

internal class SearchQueryRepository {

    suspend fun searchBooksWithCache(
        params: SearchParams,
        getCachedSearchResult: (SearchParams) -> CachedSearchResult?,
        cacheSearchResult: (SearchParams, List<BookInfoRespDto>, Int, Boolean) -> Unit,
        executeRemoteSearch: suspend (SearchQueryRequest) -> SearchService.BookSearchResponse?,
    ): PageRespDtoBookInfoRespDto? {
        val cached = getCachedSearchResult(params)
        if (cached != null) {
            return cached.result
        }

        val request = SearchQueryRequest(
            keyword = params.query,
            categoryId = params.categoryId,
            isVip = params.filters.isVip.value,
            bookStatus = params.filters.updateStatus.value,
            wordCountMin = params.filters.wordCountRange.min,
            wordCountMax = params.filters.wordCountRange.max,
            sort = params.filters.sortBy.value,
            pageNum = params.page,
            pageSize = params.pageSize,
            strategy = com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST,
        )

        return try {
            val response = executeRemoteSearch(request)
            if (response?.ok == true && response.data != null) {
                val books = response.data.list.map { searchBook ->
                    BookInfoRespDto(
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
                        firstChapterId = searchBook.firstChapterId,
                        lastChapterId = searchBook.lastChapterId,
                        lastChapterName = searchBook.lastChapterName,
                        updateTime = searchBook.updateTime,
                    )
                }

                val totalResults = response.data.total.toInt()
                val hasMore = response.data.pages > params.page
                cacheSearchResult(params, books, totalResults, hasMore)

                PageRespDtoBookInfoRespDto(
                    pageNum = response.data.pageNum,
                    pageSize = response.data.pageSize,
                    total = response.data.total,
                    list = books.toImmutableList(),
                    pages = response.data.pages,
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    suspend fun searchBooks(
        keyword: String? = null,
        workDirection: Int? = null,
        categoryId: Int? = null,
        isVip: Int? = null,
        bookStatus: Int? = null,
        wordCountMin: Int? = null,
        wordCountMax: Int? = null,
        updateTimeMin: String? = null,
        sort: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 20,
        strategy: com.novel.utils.network.cache.CacheStrategy =
            com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST,
        executeRemoteSearch: suspend (SearchQueryRequest) -> SearchService.BookSearchResponse?,
    ): SearchService.BookSearchResponse? {
        val request = SearchQueryRequest(
            keyword = keyword,
            workDirection = workDirection,
            categoryId = categoryId,
            isVip = isVip,
            bookStatus = bookStatus,
            wordCountMin = wordCountMin,
            wordCountMax = wordCountMax,
            updateTimeMin = updateTimeMin,
            sort = sort,
            pageNum = pageNum,
            pageSize = pageSize,
            strategy = strategy,
        )

        return try {
            executeRemoteSearch(request)
        } catch (_: Exception) {
            null
        }
    }
}
