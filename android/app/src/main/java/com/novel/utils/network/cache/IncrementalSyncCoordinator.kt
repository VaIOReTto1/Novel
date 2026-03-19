package com.novel.utils.network.cache

import com.novel.core.StableThrowable

internal data class IncrementalCacheSaveRequest<T>(
    val key: String,
    val data: T,
    val config: CacheConfig,
    val serverVersion: String? = null,
    val lastModified: String? = null,
    val eTag: String? = null,
    val contentHash: String? = null,
)

internal class IncrementalSyncCoordinator {

    suspend fun <T> coordinate(
        key: String,
        config: CacheConfig,
        networkCall: suspend (lastModified: String?, eTag: String?) -> IncrementalNetworkResponse<T>,
        loadCachedEntry: suspend () -> CacheEntry<T>?,
        updateAccessInfo: suspend (CacheEntry<T>) -> Unit,
        saveCacheData: suspend (IncrementalCacheSaveRequest<T>) -> Unit,
        calculateContentHash: (T) -> String,
        onRequestPrepared: (lastModified: String?, eTag: String?) -> Unit = { _, _ -> },
        onDataUpdated: (hasChanged: Boolean) -> Unit = {},
        onNetworkError: (StableThrowable) -> Unit = {},
        onUnexpectedError: (Exception) -> Unit = {},
    ): IncrementalSyncResult<T> {
        return try {
            val cachedEntry = loadCachedEntry()
            val lastModified = cachedEntry?.lastModified
            val eTag = cachedEntry?.eTag
            onRequestPrepared(lastModified, eTag)

            val response = networkCall(lastModified, eTag)

            when (response) {
                is IncrementalNetworkResponse.NotModified -> {
                    cachedEntry?.let {
                        updateAccessInfo(it)
                        IncrementalSyncResult.NoChange(it.data)
                    } ?: IncrementalSyncResult.Error(
                        StableThrowable(Exception("No cached data available")),
                    )
                }

                is IncrementalNetworkResponse.Modified -> {
                    val newHash = response.data?.let(calculateContentHash)
                    saveCacheData(
                        IncrementalCacheSaveRequest(
                            key = key,
                            data = response.data,
                            config = config,
                            serverVersion = response.serverVersion,
                            lastModified = response.lastModified,
                            eTag = response.eTag,
                            contentHash = newHash,
                        ),
                    )

                    onDataUpdated(cachedEntry?.contentHash != newHash)
                    IncrementalSyncResult.Updated(
                        newData = response.data,
                        hasChanged = cachedEntry?.contentHash != newHash,
                    )
                }

                is IncrementalNetworkResponse.Error -> {
                    onNetworkError(response.error)
                    cachedEntry?.let {
                        IncrementalSyncResult.Error(response.error, it.data)
                    } ?: IncrementalSyncResult.Error(response.error)
                }
            }
        } catch (error: Exception) {
            onUnexpectedError(error)
            val cachedEntry = loadCachedEntry()
            IncrementalSyncResult.Error(StableThrowable(error), cachedEntry?.data)
        }
    }
}
