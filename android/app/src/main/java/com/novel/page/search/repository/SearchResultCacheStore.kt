package com.novel.page.search.repository

import androidx.compose.runtime.Stable
import com.novel.page.search.viewmodel.BookInfoRespDto
import kotlinx.collections.immutable.toImmutableList
import java.util.concurrent.ConcurrentHashMap

@Stable
class SearchResultCacheStore(
    private val currentTimeProvider: () -> Long = System::currentTimeMillis,
    private val cacheDurationMs: Long = 5 * 60 * 1000L,
    private val maxEntries: Int = 20,
) {

    @Stable
    private val searchResultCache = ConcurrentHashMap<String, CachedSearchResult>()

    fun getCachedSearchResult(params: SearchParams): CachedSearchResult? {
        val cacheKey = generateCacheKey(params)
        val cached = searchResultCache[cacheKey]

        return if (cached != null && isCacheValid(cached)) {
            cached
        } else {
            if (cached != null) {
                searchResultCache.remove(cacheKey)
            }
            null
        }
    }

    fun cacheSearchResult(
        params: SearchParams,
        books: List<BookInfoRespDto>,
        totalResults: Int,
        hasMore: Boolean,
    ) {
        val cacheKey = generateCacheKey(params)
        val cachedResult = CachedSearchResult(
            params = params,
            result = PageRespDtoBookInfoRespDto(
                pageNum = params.page.toLong(),
                pageSize = params.pageSize.toLong(),
                total = totalResults.toLong(),
                list = books.toImmutableList(),
                pages = if (hasMore) (params.page + 1).toLong() else params.page.toLong(),
            ),
            cacheTime = currentTimeProvider(),
        )

        if (searchResultCache.size >= maxEntries) {
            cleanExpiredCache()

            if (searchResultCache.size >= maxEntries) {
                val oldestKey = searchResultCache.minByOrNull { it.value.cacheTime }?.key
                oldestKey?.let(searchResultCache::remove)
            }
        }

        searchResultCache[cacheKey] = cachedResult
    }

    fun clearSearchResultCache() {
        searchResultCache.clear()
    }

    fun isSearchResultCacheAvailable(params: SearchParams): Boolean {
        return getCachedSearchResult(params) != null
    }

    private fun generateCacheKey(params: SearchParams): String {
        return "${params.query}-${params.categoryId}-${params.filters.hashCode()}-${params.page}-${params.pageSize}"
    }

    private fun isCacheValid(cached: CachedSearchResult): Boolean {
        return (currentTimeProvider() - cached.cacheTime) < cacheDurationMs
    }

    private fun cleanExpiredCache() {
        val currentTime = currentTimeProvider()
        val expiredKeys = searchResultCache.filter {
            (currentTime - it.value.cacheTime) >= cacheDurationMs
        }.keys

        expiredKeys.forEach(searchResultCache::remove)
    }
}
