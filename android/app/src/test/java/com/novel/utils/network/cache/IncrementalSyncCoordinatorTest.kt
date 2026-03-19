package com.novel.utils.network.cache

import com.google.common.truth.Truth.assertThat
import com.novel.core.StableThrowable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test

class IncrementalSyncCoordinatorTest {

    private val coordinator = IncrementalSyncCoordinator()

    @Test
    fun coordinate_returnsNoChangeAndUpdatesAccessWhenResponseIsNotModified() = runBlocking {
        val cachedEntry = CacheEntry(
            key = "books",
            data = "cached-data",
            cacheTime = 1L,
            expiryTime = 2L,
            lastModified = "last-modified",
            eTag = "etag",
        )
        var updatedEntry: CacheEntry<String>? = null

        val result = coordinator.coordinate<String>(
            key = "books",
            config = CacheConfig(),
            networkCall = { lastModified, eTag ->
                assertThat(lastModified).isEqualTo("last-modified")
                assertThat(eTag).isEqualTo("etag")
                IncrementalNetworkResponse.NotModified()
            },
            loadCachedEntry = { cachedEntry },
            updateAccessInfo = { entry -> updatedEntry = entry },
            saveCacheData = { fail("saveCacheData should not be called") },
            calculateContentHash = { "ignored" },
        )

        assertThat((result as IncrementalSyncResult.NoChange<String>).cachedData).isEqualTo("cached-data")
        assertThat(updatedEntry).isEqualTo(cachedEntry)
    }

    @Test
    fun coordinate_returnsErrorWhenResponseIsNotModifiedWithoutCachedData() = runBlocking {
        val result = coordinator.coordinate<String>(
            key = "books",
            config = CacheConfig(),
            networkCall = { _, _ -> IncrementalNetworkResponse.NotModified() },
            loadCachedEntry = { null },
            updateAccessInfo = { fail("updateAccessInfo should not be called") },
            saveCacheData = { fail("saveCacheData should not be called") },
            calculateContentHash = { "ignored" },
        )

        val error = result as IncrementalSyncResult.Error<String>
        assertThat(error.cachedData).isNull()
        assertThat(error.error.message).isEqualTo("No cached data available")
    }

    @Test
    fun coordinate_savesModifiedResponseAndMarksChangeWhenHashDiffers() = runBlocking {
        val cachedEntry = CacheEntry(
            key = "books",
            data = "cached-data",
            cacheTime = 1L,
            expiryTime = 2L,
            contentHash = "old-hash",
        )
        var savedRequest: IncrementalCacheSaveRequest<String>? = null

        val result = coordinator.coordinate<String>(
            key = "books",
            config = CacheConfig(maxAge = 99L),
            networkCall = { _, _ ->
                IncrementalNetworkResponse.Modified(
                    data = "fresh-data",
                    serverVersion = "v2",
                    lastModified = "new-last-modified",
                    eTag = "new-etag",
                )
            },
            loadCachedEntry = { cachedEntry },
            updateAccessInfo = { fail("updateAccessInfo should not be called") },
            saveCacheData = { request -> savedRequest = request },
            calculateContentHash = { value -> "hash:$value" },
        )

        val updated = result as IncrementalSyncResult.Updated<String>
        assertThat(updated.newData).isEqualTo("fresh-data")
        assertThat(updated.hasChanged).isTrue()
        assertThat(savedRequest).isEqualTo(
            IncrementalCacheSaveRequest(
                key = "books",
                data = "fresh-data",
                config = CacheConfig(maxAge = 99L),
                serverVersion = "v2",
                lastModified = "new-last-modified",
                eTag = "new-etag",
                contentHash = "hash:fresh-data",
            )
        )
    }

    @Test
    fun coordinate_returnsNetworkErrorWithCachedFallback() = runBlocking {
        val cachedEntry = CacheEntry(
            key = "books",
            data = "cached-data",
            cacheTime = 1L,
            expiryTime = 2L,
        )
        val networkError = StableThrowable(IllegalStateException("network failed"))

        val result = coordinator.coordinate<String>(
            key = "books",
            config = CacheConfig(),
            networkCall = { _, _ -> IncrementalNetworkResponse.Error(networkError) },
            loadCachedEntry = { cachedEntry },
            updateAccessInfo = { fail("updateAccessInfo should not be called") },
            saveCacheData = { fail("saveCacheData should not be called") },
            calculateContentHash = { "ignored" },
        )

        val error = result as IncrementalSyncResult.Error<String>
        assertThat(error.error).isEqualTo(networkError)
        assertThat(error.cachedData).isEqualTo("cached-data")
    }

    @Test
    fun coordinate_reloadsCachedDataWhenNetworkCallThrows() = runBlocking {
        var loadCount = 0

        val result = coordinator.coordinate<String>(
            key = "books",
            config = CacheConfig(),
            networkCall = { _, _ -> throw IllegalStateException("boom") },
            loadCachedEntry = {
                loadCount += 1
                if (loadCount == 1) {
                    null
                } else {
                    CacheEntry(
                        key = "books",
                        data = "stale-data",
                        cacheTime = 1L,
                        expiryTime = 2L,
                    )
                }
            },
            updateAccessInfo = { fail("updateAccessInfo should not be called") },
            saveCacheData = { fail("saveCacheData should not be called") },
            calculateContentHash = { "ignored" },
        )

        val error = result as IncrementalSyncResult.Error<String>
        assertThat(error.error.message).isEqualTo("boom")
        assertThat(error.cachedData).isEqualTo("stale-data")
        assertThat(loadCount).isEqualTo(2)
    }
}
