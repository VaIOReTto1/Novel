package com.novel.page.search.repository

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.page.search.viewmodel.FilterState
import com.novel.page.search.viewmodel.SortBy
import com.novel.page.search.viewmodel.UpdateStatus
import com.novel.page.search.viewmodel.VipStatus
import com.novel.page.search.viewmodel.WordCountRange
import com.novel.utils.network.api.front.SearchService
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SearchQueryRepositoryTest {

    @Test
    fun searchBooksWithCache_returnsCachedResultWithoutRemoteCall() {
        runBlocking {
            val repository = SearchQueryRepository()
            val params = searchParams(page = 1)
            val cachedResult = CachedSearchResult(
                params = params,
                result = PageRespDtoBookInfoRespDto(
                    pageNum = 1,
                    pageSize = 20,
                    total = 1,
                    list = persistentListOf(bookInfo(1, "cached")),
                    pages = 1,
                ),
                cacheTime = 1L,
            )
            var remoteCalled = false

            val result = repository.searchBooksWithCache(
                params = params,
                getCachedSearchResult = { cachedResult },
                cacheSearchResult = { _, _, _, _ -> error("cache should not be updated") },
                executeRemoteSearch = {
                    remoteCalled = true
                    null
                },
            )

            assertThat(remoteCalled).isFalse()
            assertThat(result).isEqualTo(cachedResult.result)
        }
    }

    @Test
    fun searchBooksWithCache_mapsRemoteResponseAndCachesIt() {
        runBlocking {
            val repository = SearchQueryRepository()
            val params = searchParams(page = 2)
            var cachedPayload: PageRespDtoBookInfoRespDto? = null
            var cachedTotal: Int? = null
            var cachedHasMore: Boolean? = null
            var builtRequest: SearchQueryRequest? = null

            val result = repository.searchBooksWithCache(
                params = params,
                getCachedSearchResult = { null },
                cacheSearchResult = { _, books, totalResults, hasMore ->
                    cachedPayload = PageRespDtoBookInfoRespDto(
                        pageNum = params.page.toLong(),
                        pageSize = 20L,
                        total = totalResults.toLong(),
                        list = books.toImmutableList(),
                        pages = if (hasMore) (params.page + 1).toLong() else params.page.toLong(),
                    )
                    cachedTotal = totalResults
                    cachedHasMore = hasMore
                },
                executeRemoteSearch = { request ->
                    builtRequest = request
                    SearchService.BookSearchResponse(
                        code = "200",
                        message = "ok",
                        ok = true,
                        data = SearchService.PageResponse(
                            pageNum = 2,
                            pageSize = 20,
                            total = 45,
                            pages = 3,
                            list = persistentListOf(
                                SearchService.BookInfo(
                                    id = 11,
                                    categoryId = 9,
                                    categoryName = "玄幻",
                                    picUrl = "cover",
                                    bookName = "remote",
                                    authorId = 1,
                                    authorName = "author",
                                    bookDesc = "desc",
                                    bookStatus = 1,
                                    visitCount = 100,
                                    wordCount = 1000,
                                    commentCount = 2,
                                    firstChapterId = 1,
                                    lastChapterId = 2,
                                    lastChapterName = "chapter",
                                    updateTime = "today",
                                ),
                            ),
                        ),
                    )
                },
            )

            assertThat(builtRequest).isEqualTo(
                SearchQueryRequest(
                    keyword = "keyword",
                    categoryId = 1,
                    isVip = null,
                    bookStatus = null,
                    wordCountMin = null,
                    wordCountMax = null,
                    sort = "null",
                    pageNum = 2,
                    pageSize = 20,
                    strategy = com.novel.utils.network.cache.CacheStrategy.CACHE_FIRST,
                ),
            )
            assertThat(result?.list?.first()?.bookName).isEqualTo("remote")
            assertThat(cachedTotal).isEqualTo(45)
            assertThat(cachedHasMore).isTrue()
            assertThat(cachedPayload?.list?.first()?.bookName).isEqualTo("remote")
        }
    }

    @Test
    fun searchBooks_buildsRequestAndReturnsRemoteResponse() {
        runBlocking {
            val repository = SearchQueryRepository()
            var builtRequest: SearchQueryRequest? = null

            val response = repository.searchBooks(
                keyword = "search",
                workDirection = 3,
                categoryId = 7,
                isVip = 1,
                bookStatus = 0,
                wordCountMin = 100,
                wordCountMax = 500,
                updateTimeMin = "2026-03-01",
                sort = "visit_count desc",
                pageNum = 4,
                pageSize = 30,
                strategy = com.novel.utils.network.cache.CacheStrategy.NETWORK_ONLY,
                executeRemoteSearch = { request ->
                    builtRequest = request
                    SearchService.BookSearchResponse(
                        code = "200",
                        message = "ok",
                        ok = true,
                        data = null,
                    )
                },
            )

            assertThat(builtRequest).isEqualTo(
                SearchQueryRequest(
                    keyword = "search",
                    workDirection = 3,
                    categoryId = 7,
                    isVip = 1,
                    bookStatus = 0,
                    wordCountMin = 100,
                    wordCountMax = 500,
                    updateTimeMin = "2026-03-01",
                    sort = "visit_count desc",
                    pageNum = 4,
                    pageSize = 30,
                    strategy = com.novel.utils.network.cache.CacheStrategy.NETWORK_ONLY,
                ),
            )
            assertThat(response?.ok).isTrue()
        }
    }

    private fun searchParams(page: Int): SearchParams {
        return SearchParams(
            query = "keyword",
            page = page,
            categoryId = 1,
            filters = FilterState(
                updateStatus = UpdateStatus.ALL,
                isVip = VipStatus.ALL,
                wordCountRange = WordCountRange.ALL,
                sortBy = SortBy.NULL,
            ),
            isLoadMore = false,
        )
    }

    private fun bookInfo(id: Long, name: String): BookInfoRespDto {
        return BookInfoRespDto(
            id = id,
            categoryId = 1,
            categoryName = "分类",
            picUrl = "cover",
            bookName = name,
            authorId = 2,
            authorName = "Author",
            bookDesc = "desc",
            bookStatus = 1,
            visitCount = 10,
            wordCount = 1000,
            commentCount = 3,
            firstChapterId = 1,
            lastChapterId = 2,
            lastChapterName = "chapter",
            updateTime = "today",
        )
    }
}
