package com.novel.utils.network.cache

import android.content.Context
import androidx.compose.runtime.Stable
import com.novel.core.StableThrowable
import com.novel.utils.TimberLogger
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 缓存条目数据
 */
data class CacheEntry<T>(
    val key: String,
    val data: T,
    val cacheTime: Long,
    val expiryTime: Long
)

/**
 * 缓存配置
 */
data class CacheConfig(
    val maxAge: Long = TimeUnit.HOURS.toMillis(1), // 默认1小时过期
    val maxEntries: Int = 1000, // 最大缓存条目数
    val refreshThreshold: Double = 0.3 // 缓存刷新阈值：当剩余有效时间低于30%时才后台更新
)

/**
 * 缓存策略
 */
enum class CacheStrategy {
    CACHE_FIRST,    // 先从缓存获取，然后异步更新
    NETWORK_FIRST,  // 先从网络获取，失败时从缓存获取
    CACHE_ONLY,     // 只从缓存获取
    NETWORK_ONLY,   // 只从网络获取
    SMART_FALLBACK  // 智能兜底：缓存优先，失败时网络重试，最后使用过期缓存
}

/**
 * 缓存结果
 */
@Stable
sealed class CacheResult<T> {
    @Stable
    data class Success<T>(val data: T, val fromCache: Boolean) : CacheResult<T>()
    @Stable
    data class Error<T>(val error: StableThrowable, val cachedData: T? = null) : CacheResult<T>()
}

/**
 * 通用网络缓存管理器
 * 
 * 核心功能：
 * 1. 多级缓存策略：内存+磁盘双重缓存
 * 2. 智能缓存管理：自动过期清理和容量控制
 * 3. 灵活缓存策略：Cache-First、Network-First、Smart-Fallback等
 * 4. 异常安全处理：网络失败时智能降级到缓存数据
 * 5. 泛型支持：可缓存任意类型的数据结构
 * 6. 响应式状态：实时监控缓存更新状态
 * 
 * 缓存策略详解：
 * - CACHE_FIRST: 优先使用缓存，后台异步更新
 * - NETWORK_FIRST: 优先网络请求，失败时使用缓存
 * - CACHE_ONLY: 仅使用缓存数据，适用于离线场景
 * - NETWORK_ONLY: 仅使用网络数据，适用于实时性要求高的场景
 * - SMART_FALLBACK: 智能兜底，多重重试机制
 */
@Stable
@Singleton
class NetworkCacheManager @Inject constructor(
    @ApplicationContext @Stable private val context: Context,
    @Stable private val gson: Gson
) {
    companion object {
        private const val TAG = "NetworkCacheManager"
        private const val CACHE_DIR_NAME = "network_cache"
    }

    @Stable
    private val cacheDir = File(context.cacheDir, CACHE_DIR_NAME)
    
    // 内存缓存
    @Stable
    private val memoryCache = mutableMapOf<String, CacheEntry<*>>()
    
    // 缓存更新状态
    @Stable
    private val _cacheUpdateState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    @Stable
    val cacheUpdateState: StateFlow<Map<String, Boolean>> = _cacheUpdateState.asStateFlow()
    
    init {
        cacheDir.mkdirs()
        TimberLogger.d(TAG, "网络缓存管理器初始化，缓存目录: ${cacheDir.absolutePath}")
        // 清理过期缓存
        cleanExpiredCaches()
    }
    
    /**
     * 获取缓存数据（支持cache-first策略，增强兜底机制）
     * @param key 缓存键
     * @param config 缓存配置
     * @param networkCall 网络请求函数
     * @param strategy 缓存策略
     * @param onCacheUpdate 缓存更新回调
     * @param typeToken 类型标记，用于解决泛型类型擦除问题
     */
    suspend fun <T> getCachedData(
        key: String,
        config: CacheConfig = CacheConfig(),
        networkCall: suspend () -> T,
        strategy: CacheStrategy = CacheStrategy.CACHE_FIRST,
        onCacheUpdate: ((T) -> Unit)? = null,
        typeToken: TypeToken<T>? = null
    ): CacheResult<T> = withContext(Dispatchers.IO) {
        
        when (strategy) {
            CacheStrategy.CACHE_FIRST -> {
                // 先从缓存获取
                val cachedData = getCachedDataInternal(key, typeToken = typeToken)
                
                if (cachedData != null && isValidData(cachedData)) {
                    // 检查缓存是否需要刷新
                    val shouldRefresh = shouldRefreshCache(key, config)
                    if (shouldRefresh) {
                        // 只在缓存即将过期时才异步更新网络数据
                        TimberLogger.d(TAG, "Cache is stale for key: $key, refreshing in background")
                        updateCacheAsync(key, config, networkCall, onCacheUpdate, typeToken)
                    } else {
                        TimberLogger.d(TAG, "Cache is fresh for key: $key, skipping network update")
                    }
                    return@withContext CacheResult.Success(cachedData, true)
                } else {
                    // 缓存数据无效或不存在，同步获取网络数据
                    TimberLogger.d(TAG, "Cache data invalid or missing for key: $key, fetching from network")
                    try {
                        val networkData = networkCall()
                        // 检查网络数据是否为有效数据（兜底检查）
                        if (isValidData(networkData)) {
                            saveCacheData(key, networkData, config, typeToken)
                            return@withContext CacheResult.Success(networkData, false)
                        } else {
                            TimberLogger.w(TAG, "Network data is invalid for key: $key, retrying...")
                            // 如果网络数据无效，稍作延迟后重试一次
                            kotlinx.coroutines.delay(1000)
                            val retryNetworkData = networkCall()
                            if (isValidData(retryNetworkData)) {
                                saveCacheData(key, retryNetworkData, config, typeToken)
                                return@withContext CacheResult.Success(retryNetworkData, false)
                            } else {
                                // 如果网络重试仍失败，尝试返回过期的缓存数据作为兜底
                                if (cachedData != null) {
                                    TimberLogger.w(TAG, "Using expired cache data as fallback for key: $key")
                                    return@withContext CacheResult.Success(cachedData, true)
                                }
                                return@withContext CacheResult.Error(StableThrowable(Exception("Network data is invalid after retry")))
                            }
                        }
                    } catch (e: Exception) {
                        TimberLogger.e(TAG, "Network request failed for key: $key", e)
                        // 网络失败时，尝试返回缓存数据（即使可能过期）作为兜底
                        if (cachedData != null) {
                            TimberLogger.w(TAG, "Using cached data as fallback for key: $key")
                            return@withContext CacheResult.Error(StableThrowable(e), cachedData)
                        }
                        return@withContext CacheResult.Error(StableThrowable(e))
                    }
                }
            }
            
            CacheStrategy.NETWORK_FIRST -> {
                try {
                    val networkData = networkCall()
                    if (isValidData(networkData)) {
                        saveCacheData(key, networkData, config, typeToken)
                        return@withContext CacheResult.Success(networkData, false)
                    } else {
                        TimberLogger.w(TAG, "Network data is invalid for key: $key, falling back to cache")
                        val cachedData = getCachedDataInternal(key, typeToken = typeToken)
                        if (cachedData != null) {
                            return@withContext CacheResult.Success(cachedData, true)
                        } else {
                            return@withContext CacheResult.Error(StableThrowable(Exception("Both network and cache data unavailable")))
                        }
                    }
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "Network request failed, trying cache for key: $key", e)
                    val cachedData = getCachedDataInternal(key, typeToken = typeToken)
                    if (cachedData != null) {
                        return@withContext CacheResult.Success(cachedData, true)
                    } else {
                        return@withContext CacheResult.Error(StableThrowable(e))
                    }
                }
            }
            
            CacheStrategy.CACHE_ONLY -> {
                val cachedData = getCachedDataInternal(key, typeToken = typeToken)
                if (cachedData != null && isValidData(cachedData)) {
                    return@withContext CacheResult.Success(cachedData, true)
                } else {
                    return@withContext CacheResult.Error(StableThrowable(Exception("No valid cached data found")))
                }
            }
            
            CacheStrategy.NETWORK_ONLY -> {
                try {
                    val networkData = networkCall()
                    if (isValidData(networkData)) {
                        saveCacheData(key, networkData, config, typeToken)
                        return@withContext CacheResult.Success(networkData, false)
                    } else {
                        return@withContext CacheResult.Error(StableThrowable(Exception("Network data is invalid")))
                    }
                } catch (e: Exception) {
                    return@withContext CacheResult.Error(StableThrowable(e))
                }
            }
            
            CacheStrategy.SMART_FALLBACK -> {
                // 多重兜底策略
                try {
                    // 1. 先尝试从有效缓存获取
                    val cachedData = getCachedDataInternal(key, typeToken = typeToken)
                    if (cachedData != null && isValidData(cachedData)) {
                        // 异步更新网络数据
                        updateCacheAsync(key, config, networkCall, onCacheUpdate, typeToken)
                        return@withContext CacheResult.Success(cachedData, true)
                    }
                    
                    // 2. 缓存无效，尝试网络请求
                    val networkData = networkCall()
                    if (isValidData(networkData)) {
                        saveCacheData(key, networkData, config, typeToken)
                        return@withContext CacheResult.Success(networkData, false)
                    }
                    
                    // 3. 网络数据无效，再次尝试网络请求
                    kotlinx.coroutines.delay(1000)
                    val retryNetworkData = networkCall()
                    if (isValidData(retryNetworkData)) {
                        saveCacheData(key, retryNetworkData, config, typeToken)
                        return@withContext CacheResult.Success(retryNetworkData, false)
                    }
                    
                    // 4. 所有正常途径失败，尝试使用过期缓存
                    val expiredCachedData = getCachedDataInternal(key, allowExpired = true, typeToken = typeToken)
                    if (expiredCachedData != null) {
                        TimberLogger.w(TAG, "Using expired cache data as final fallback for key: $key")
                        return@withContext CacheResult.Error(StableThrowable(Exception("All data sources failed")), expiredCachedData)
                    }
                    
                    // 5. 完全失败
                    return@withContext CacheResult.Error(StableThrowable(Exception("All fallback strategies failed")))
                    
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "Smart fallback failed for key: $key", e)
                    // 作为最后的兜底，尝试返回过期缓存
                    val expiredCachedData = getCachedDataInternal(key, allowExpired = true, typeToken = typeToken)
                    if (expiredCachedData != null) {
                        return@withContext CacheResult.Error(StableThrowable(e), expiredCachedData)
                    }
                    return@withContext CacheResult.Error(StableThrowable(e))
                }
            }
        }
    }
    
    /**
     * 检查缓存是否需要刷新
     * @param key 缓存键
     * @param config 缓存配置
     * @return true如果缓存需要刷新，false如果缓存仍然新鲜
     */
    private suspend fun shouldRefreshCache(key: String, config: CacheConfig): Boolean {
        try {
            // 先检查内存缓存
            val memoryCacheEntry = memoryCache[key]
            if (memoryCacheEntry != null) {
                return isCacheStale(memoryCacheEntry.cacheTime, memoryCacheEntry.expiryTime, config)
            }
            
            // 检查磁盘缓存
            val diskCacheFile = File(cacheDir, "$key.json")
            if (diskCacheFile.exists()) {
                val cacheEntryJson = diskCacheFile.readText()
                val type = object : TypeToken<CacheEntry<Any?>>() {}.type
                val cacheEntry = gson.fromJson<CacheEntry<Any?>>(cacheEntryJson, type)
                return isCacheStale(cacheEntry.cacheTime, cacheEntry.expiryTime, config)
            }
            
            // 如果没有缓存，则需要刷新
            return true
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to check cache staleness for key: $key", e)
            return true // 出错时安全地选择刷新
        }
    }
    
    /**
     * 检查缓存是否过期或即将过期
     * @param cacheTime 缓存创建时间
     * @param expiryTime 缓存过期时间
     * @param config 缓存配置
     * @return true如果缓存过期或即将过期需要刷新
     */
    private fun isCacheStale(cacheTime: Long, expiryTime: Long, config: CacheConfig): Boolean {
        val currentTime = System.currentTimeMillis()
        
        // 如果已经过期，肯定需要刷新
        if (currentTime >= expiryTime) {
            return true
        }
        
        // 计算缓存剩余有效时间占总有效时间的比例
        val totalCacheAge = config.maxAge
        val remainingAge = expiryTime - currentTime
        val freshnessRatio = remainingAge.toDouble() / totalCacheAge
        
        // 如果剩余有效时间低于刷新阈值，则需要刷新
        return freshnessRatio < config.refreshThreshold
    }
    
    /**
     * 检查数据是否有效
     */
    private fun <T> isValidData(data: T?): Boolean {
        return when (data) {
            null -> false
            is List<*> -> data.isNotEmpty()
            is Map<*, *> -> data.isNotEmpty()
            is String -> data.isNotBlank()
            else -> true
        }
    }
    
    /**
     * 异步更新缓存
     */
    private fun <T> updateCacheAsync(
        key: String,
        config: CacheConfig,
        networkCall: suspend () -> T,
        onCacheUpdate: ((T) -> Unit)? = null,
        typeToken: TypeToken<T>? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 标记正在更新
                updateCacheState(key, true)
                
                val networkData = networkCall()
                saveCacheData(key, networkData, config, typeToken)
                
                // 回调通知缓存已更新
                onCacheUpdate?.invoke(networkData)
                
                TimberLogger.d(TAG, "Cache updated for key: $key")
            } catch (e: Exception) {
                TimberLogger.e(TAG, "Failed to update cache for key: $key", e)
            } finally {
                // 标记更新完成
                updateCacheState(key, false)
            }
        }
    }
    
    /**
     * 内部获取缓存数据（支持获取过期缓存）- 修复泛型类型擦除问题
     */
    @Suppress("UNCHECKED_CAST")
    private suspend fun <T> getCachedDataInternal(
        key: String,
        allowExpired: Boolean = false,
        typeToken: TypeToken<T>? = null
    ): T? = withContext(Dispatchers.IO) {
        try {
            // 先从内存缓存获取
            val memoryCacheEntry = memoryCache[key] as? CacheEntry<T>
            if (memoryCacheEntry != null) {
                if (!isCacheExpired(memoryCacheEntry.expiryTime) || allowExpired) {
                    TimberLogger.d(TAG, "Cache hit from memory for key: $key")
                    return@withContext memoryCacheEntry.data
                }
            }
            
                            // 从磁盘缓存获取
                val diskCacheFile = File(cacheDir, "$key.json")
                if (diskCacheFile.exists()) {
                    val cacheEntryJson = diskCacheFile.readText()
                    
                    // 使用具体的类型信息进行反序列化
                    val cacheEntry = if (typeToken != null) {
                        // 构造 CacheEntry<T> 的完整类型
                        val cacheEntryType = TypeToken.getParameterized(CacheEntry::class.java, typeToken.type).type
                        gson.fromJson<CacheEntry<T>>(cacheEntryJson, cacheEntryType)
                    } else {
                        // 兜底处理：尝试直接反序列化，但这可能导致类型转换错误
                        val type = object : TypeToken<CacheEntry<T>>() {}.type
                        try {
                            gson.fromJson<CacheEntry<T>>(cacheEntryJson, type)
                        } catch (e: ClassCastException) {
                            TimberLogger.e(TAG, "ClassCastException during deserialization for key: $key, clearing cache", e)
                            diskCacheFile.delete()
                            memoryCache.remove(key)
                            return@withContext null
                        }
                    }
                    
                    if (!isCacheExpired(cacheEntry.expiryTime)) {
                        // 更新内存缓存
                        memoryCache[key] = cacheEntry
                        TimberLogger.d(TAG, "Cache hit from disk for key: $key")
                        return@withContext cacheEntry.data
                    } else if (allowExpired) {
                        // 允许过期缓存时，仍然返回数据但不更新内存缓存
                        TimberLogger.d(TAG, "Returning expired cache data for key: $key")
                        return@withContext cacheEntry.data
                    } else {
                        // 过期则删除
                        diskCacheFile.delete()
                        memoryCache.remove(key)
                        TimberLogger.d(TAG, "Cache expired and cleaned for key: $key")
                    }
                }
            
            null
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to get cached data for key: $key", e)
            // 如果反序列化失败，清理相关缓存
            try {
                File(cacheDir, "$key.json").delete()
                memoryCache.remove(key)
            } catch (cleanupException: Exception) {
                TimberLogger.e(TAG, "Failed to cleanup corrupted cache for key: $key", cleanupException)
            }
            null
        }
    }
    
    /**
     * 保存缓存数据 - 支持类型标记
     */
    private suspend fun <T> saveCacheData(
        key: String,
        data: T,
        config: CacheConfig,
        typeToken: TypeToken<T>? = null
    ) = withContext(Dispatchers.IO) {
        try {
            val currentTime = System.currentTimeMillis()
            val cacheEntry = CacheEntry(
                key = key,
                data = data,
                cacheTime = currentTime,
                expiryTime = currentTime + config.maxAge
            )
            
            // 保存到内存缓存
            memoryCache[key] = cacheEntry
            
            // 保存到磁盘缓存
            val diskCacheFile = File(cacheDir, "$key.json")
            val cacheEntryJson = if (typeToken != null) {
                // 使用具体的类型信息进行序列化
                val cacheEntryType = TypeToken.getParameterized(CacheEntry::class.java, typeToken.type).type
                gson.toJson(cacheEntry, cacheEntryType)
            } else {
                gson.toJson(cacheEntry)
            }
            diskCacheFile.writeText(cacheEntryJson)
            
            // 检查内存缓存大小，如果超过限制则清理最老的条目
            if (memoryCache.size > config.maxEntries) {
                val oldestKey = memoryCache.entries
                    .minByOrNull { it.value.cacheTime }?.key
                oldestKey?.let { memoryCache.remove(it) }
            }
            
            TimberLogger.d(TAG, "Cache saved and verified for key: $key")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to save cache for key: $key", e)
        }
    }
    
    /**
     * 检查缓存是否过期
     */
    private fun isCacheExpired(expiryTime: Long): Boolean {
        return System.currentTimeMillis() > expiryTime
    }
    
    /**
     * 更新缓存状态
     */
    private fun updateCacheState(key: String, isUpdating: Boolean) {
        val currentState = _cacheUpdateState.value.toMutableMap()
        if (isUpdating) {
            currentState[key] = true
        } else {
            currentState.remove(key)
        }
        _cacheUpdateState.value = currentState
    }
    
    /**
     * 清理过期缓存
     */
    private fun cleanExpiredCaches() {
        try {
            
            // 清理磁盘缓存
            cacheDir.listFiles()?.forEach { file ->
                try {
                    val cacheEntryJson = file.readText()
                    val type = object : TypeToken<CacheEntry<Any?>>() {}.type
                    val cacheEntry = gson.fromJson<CacheEntry<Any?>>(cacheEntryJson, type)
                    
                    if (isCacheExpired(cacheEntry.expiryTime)) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    // 解析失败的文件直接删除
                    file.delete()
                }
            }
            
            // 清理内存缓存
            val expiredKeys = memoryCache.entries
                .filter { isCacheExpired(it.value.expiryTime) }
                .map { it.key }
            
            expiredKeys.forEach { memoryCache.remove(it) }
            
            TimberLogger.d(TAG, "Expired cache cleaned")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to clean expired caches", e)
        }
    }
    
    /**
     * 清理指定key的缓存
     */
    suspend fun clearCache(key: String) = withContext(Dispatchers.IO) {
        try {
            memoryCache.remove(key)
            File(cacheDir, "$key.json").delete()
            TimberLogger.d(TAG, "Cache cleared for key: $key")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to clear cache for key: $key", e)
        }
    }
    
    /**
     * 清理所有缓存
     */
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        try {
            memoryCache.clear()
            cacheDir.listFiles()?.forEach { it.delete() }
            TimberLogger.d(TAG, "All cache cleared")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to clear all cache", e)
        }
    }
    
    /**
     * 检查缓存是否存在
     */
    suspend fun isCacheExists(key: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // 检查内存缓存
            val memoryCacheEntry = memoryCache[key]
            if (memoryCacheEntry != null && !isCacheExpired(memoryCacheEntry.expiryTime)) {
                return@withContext true
            }
            
            // 检查磁盘缓存
            val diskCacheFile = File(cacheDir, "$key.json")
            if (diskCacheFile.exists()) {
                val cacheEntryJson = diskCacheFile.readText()
                val type = object : TypeToken<CacheEntry<Any?>>() {}.type
                val cacheEntry = gson.fromJson<CacheEntry<Any?>>(cacheEntryJson, type)
                return@withContext !isCacheExpired(cacheEntry.expiryTime)
            }
            
            false
        } catch (e: Exception) {
            TimberLogger.e(TAG, "Failed to check cache existence for key: $key", e)
            false
        }
    }
}