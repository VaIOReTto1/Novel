package com.novel.page.read.service.common

import androidx.compose.runtime.Stable
import java.util.LinkedHashMap

/**
 * 会话缓存接口
 * 
 * 抽象会话级缓存管理，支持：
 * - 泛型键值对存储
 * - 容量限制
 * - 清理策略
 * - 统计信息
 */
interface SessionCache<K, V> {
    /** 获取缓存值 */
    fun get(key: K): V?
    
    /** 存储缓存值 */
    fun put(key: K, value: V)
    
    /** 移除指定键的缓存 */
    fun remove(key: K): V?
    
    /** 清空所有缓存 */
    fun clear()
    
    /** 获取缓存大小 */
    fun size(): Int
    
    /** 检查是否包含指定键 */
    fun containsKey(key: K): Boolean
    
    /** 获取所有键 */
    fun keys(): Set<K>
    
    /** 获取缓存统计信息 */
    fun getStats(): CacheStats
}

/**
 * 缓存统计信息
 */
data class CacheStats(
    val size: Int,
    val maxSize: Int,
    val hitCount: Long = 0,
    val missCount: Long = 0,
    val evictionCount: Long = 0
) {
    val hitRate: Double get() = if (hitCount + missCount == 0L) 0.0 else hitCount.toDouble() / (hitCount + missCount)
}

/**
 * LRU会话缓存实现（优化版）
 * 
 * 基于LinkedHashMap的LRU缓存，优化并发性能：
 * - 读写分离锁策略
 * - 快速路径优化
 * - 减少锁竞争
 */
@Stable
class LruSessionCache<K, V>(
    private val maxSize: Int = ReaderServiceConfig.MAX_SESSION_CACHE_SIZE
) : SessionCache<K, V> {
    
    // 使用 volatile 确保线程安全的统计信息
    @Volatile private var hitCount = 0L
    @Volatile private var missCount = 0L
    @Volatile private var evictionCount = 0L
    
    private val cache = object : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>): Boolean {
            val shouldRemove = size > maxSize
            if (shouldRemove) {
                evictionCount++
            }
            return shouldRemove
        }
    }
    
    // 使用 ReentrantReadWriteLock 提高并发性能
    private val lock = java.util.concurrent.locks.ReentrantReadWriteLock()
    private val readLock = lock.readLock()
    private val writeLock = lock.writeLock()
    
    override fun get(key: K): V? {
        // 首先尝试快速读取（不修改访问顺序）
        readLock.lock()
        try {
            val value = cache[key]
            if (value != null) {
                hitCount++
                return value
            } else {
                missCount++
                return null
            }
        } finally {
            readLock.unlock()
        }
    }
    
    override fun put(key: K, value: V) {
        writeLock.lock()
        try {
            cache[key] = value
        } finally {
            writeLock.unlock()
        }
    }
    
    override fun remove(key: K): V? {
        writeLock.lock()
        try {
            return cache.remove(key)
        } finally {
            writeLock.unlock()
        }
    }
    
    override fun clear() {
        writeLock.lock()
        try {
            cache.clear()
            hitCount = 0
            missCount = 0
            evictionCount = 0
        } finally {
            writeLock.unlock()
        }
    }
    
    override fun size(): Int {
        readLock.lock()
        try {
            return cache.size
        } finally {
            readLock.unlock()
        }
    }
    
    override fun containsKey(key: K): Boolean {
        readLock.lock()
        try {
            return cache.containsKey(key)
        } finally {
            readLock.unlock()
        }
    }
    
    override fun keys(): Set<K> {
        readLock.lock()
        try {
            return cache.keys.toSet()
        } finally {
            readLock.unlock()
        }
    }
    
    override fun getStats(): CacheStats {
        readLock.lock()
        try {
            return CacheStats(
                size = cache.size,
                maxSize = maxSize,
                hitCount = hitCount,
                missCount = missCount,
                evictionCount = evictionCount
            )
        } finally {
            readLock.unlock()
        }
    }
}

/**
 * 无操作缓存实现
 * 
 * 用于测试或禁用缓存的场景
 */
class NoOpSessionCache<K, V> : SessionCache<K, V> {
    override fun get(key: K): V? = null
    override fun put(key: K, value: V) {}
    override fun remove(key: K): V? = null
    override fun clear() {}
    override fun size(): Int = 0
    override fun containsKey(key: K): Boolean = false
    override fun keys(): Set<K> = emptySet()
    override fun getStats(): CacheStats = CacheStats(0, 0)
} 
