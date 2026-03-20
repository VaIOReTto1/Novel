package com.novel.page.search.repository

import com.google.common.truth.Truth.assertThat
import com.novel.page.search.viewmodel.BookInfoRespDto
import com.novel.page.search.viewmodel.FilterState
import com.novel.page.search.viewmodel.SortBy
import com.novel.page.search.viewmodel.UpdateStatus
import com.novel.page.search.viewmodel.VipStatus
import com.novel.page.search.viewmodel.WordCountRange
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class SearchResultCacheStoreTest {

    @Test
    fun returnsCachedResultWhenEntryIsFresh() {
        var now = 1_000L
        val store = SearchResultCacheStore(
            currentTimeProvider = { now },
            cacheDurationMs = 500L,
            maxEntries = 2,
        )
        val params = searchParams(page = 1)

        store.cacheSearchResult(
            params = params,
            books = listOf(book(id = 1, name = "cached")),
            totalResults = 1,
            hasMore = true,
        )

        val cached = store.getCachedSearchResult(params)

        assertThat(cached).isNotNull()
        assertThat(cached?.result?.list?.first()?.bookName).isEqualTo("cached")
    }

    @Test
    fun removesExpiredEntryWhenReading() {
        var now = 1_000L
        val store = SearchResultCacheStore(
            currentTimeProvider = { now },
            cacheDurationMs = 100L,
            maxEntries = 2,
        )
        val params = searchParams(page = 1)

        store.cacheSearchResult(
            params = params,
            books = listOf(book(id = 1, name = "expired")),
            totalResults = 1,
            hasMore = false,
        )
        now = 1_200L

        val cached = store.getCachedSearchResult(params)

        assertThat(cached).isNull()
        assertThat(store.isSearchResultCacheAvailable(params)).isFalse()
    }

    @Test
    fun evictsOldestEntryWhenCapacityIsExceeded() {
        var now = 1_000L
        val store = SearchResultCacheStore(
            currentTimeProvider = { now },
            cacheDurationMs = 1_000L,
            maxEntries = 2,
        )

        val first = searchParams(query = "first", page = 1)
        val second = searchParams(query = "second", page = 1)
        val third = searchParams(query = "third", page = 1)

        store.cacheSearchResult(first, listOf(book(1, "first")), totalResults = 1, hasMore = false)
        now = 1_100L
        store.cacheSearchResult(second, listOf(book(2, "second")), totalResults = 1, hasMore = false)
        now = 1_200L
        store.cacheSearchResult(third, listOf(book(3, "third")), totalResults = 1, hasMore = false)

        assertThat(store.getCachedSearchResult(first)).isNull()
        assertThat(store.getCachedSearchResult(second)).isNotNull()
        assertThat(store.getCachedSearchResult(third)).isNotNull()
    }

    @Test
    fun clearSearchResultCacheRemovesAllEntries() {
        val store = SearchResultCacheStore()
        val params = searchParams(page = 1)

        store.cacheSearchResult(params, listOf(book(1, "cached")), totalResults = 1, hasMore = false)
        store.clearSearchResultCache()

        assertThat(store.getCachedSearchResult(params)).isNull()
    }

    private fun searchParams(query: String = "keyword", page: Int): SearchParams {
        return SearchParams(
            query = query,
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

    private fun book(id: Long, name: String): BookInfoRespDto {
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
