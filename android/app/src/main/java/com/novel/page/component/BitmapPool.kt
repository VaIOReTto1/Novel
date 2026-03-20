package com.novel.page.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.novel.utils.TimberLogger
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Stable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bitmap复用池接口
 * 定义Bitmap对象复用的基本操作
 */
interface BitmapPool {
    /**
     * 获取指定尺寸的复用Bitmap
     * 
     * @param width 需要的宽度
     * @param height 需要的高度
     * @param config Bitmap配置，默认ARGB_8888
     * @return 可复用的Bitmap，如果池中没有合适的则返回null
     */
    fun get(width: Int, height: Int, config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap?
    
    /**
     * 将Bitmap放回复用池
     * 
     * @param bitmap 要回收的Bitmap
     * @return 是否成功放入池中
     */
    fun put(bitmap: Bitmap): Boolean
    
    /**
     * 清空复用池
     */
    fun clear()
    
    /**
     * 获取当前池大小
     */
    fun size(): Int
    
    /**
     * 获取池的内存使用量(字节)
     */
    fun getCurrentSizeBytes(): Long
}

/**
 * LRU策略的Bitmap复用池实现
 * 
 * 特性：
 * - 基于LRU算法管理Bitmap生命周期
 * - 支持不同尺寸和配置的Bitmap分类存储
 * - 自动内存压力检测和释放
 * - 线程安全设计
 * - 详细的统计信息和日志
 * 
 * @param maxSizeBytes 最大内存限制(字节)
 * @param maxBitmapCount 最大Bitmap数量限制
 */
@Singleton
@Stable
class LruBitmapPool @Inject constructor() : BitmapPool {
    private val maxSizeBytes: Long = 10 * 1024 * 1024 // 10MB
    private val maxBitmapCount: Int = 50
    
    companion object {
        private const val TAG = "LruBitmapPool"
        private const val BITMAP_SIZE_TOLERANCE = 0.1f // 10%的尺寸容差
    }
    
    // 按尺寸分类的Bitmap存储池
    @Stable
    private val pools = ConcurrentHashMap<String, LinkedHashMap<Long, Bitmap>>()
    
    // 当前内存使用量
    @Stable 
    private val currentSizeBytes = AtomicInteger(0)
    
    // 当前Bitmap数量
    @Stable
    private val currentCount = AtomicInteger(0)
    
    // 统计信息
    @Stable
    private val hitCount = AtomicInteger(0)
    @Stable
    private val missCount = AtomicInteger(0)
    @Stable
    private val putCount = AtomicInteger(0)
    @Stable
    private val evictionCount = AtomicInteger(0)
    
    @VisibleForTesting
    internal val lock = Any()
    
    override fun get(width: Int, height: Int, config: Bitmap.Config): Bitmap? {
        if (width <= 0 || height <= 0) {
            TimberLogger.w(TAG, "无效的Bitmap尺寸: ${width}x${height}")
            return null
        }
        
        val key = getBitmapKey(width, height, config)
        val targetSize = getBitmapSize(width, height, config)
        
        synchronized(lock) {
            val pool = pools[key] ?: return recordMiss()
            
            // 寻找最合适的Bitmap（尺寸匹配或稍大）
            val iterator = pool.entries.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                val bitmap = entry.value
                
                if (isBitmapSuitable(bitmap, width, height, config)) {
                    iterator.remove()
                    updateSizeAndCount(-getBitmapSizeBytes(bitmap), -1)
                    
                    hitCount.incrementAndGet()
                    TimberLogger.v(TAG, "复用池命中: ${width}x${height}, 配置: $config")
                    return bitmap
                }
            }
            
            // 清理空的池
            if (pool.isEmpty()) {
                pools.remove(key)
            }
            
            return recordMiss()
        }
    }
    
    override fun put(bitmap: Bitmap): Boolean {
        if (bitmap.isRecycled) {
            TimberLogger.w(TAG, "尝试放入已回收的Bitmap")
            return false
        }
        
        if (!bitmap.isMutable) {
            TimberLogger.v(TAG, "不可变Bitmap不能复用")
            return false
        }
        
        val size = getBitmapSizeBytes(bitmap)
        if (size > maxSizeBytes / 4) { // 单个Bitmap不能超过池容量的1/4
            TimberLogger.w(TAG, "Bitmap过大，无法放入复用池: ${size}字节")
            return false
        }
        
        val key = getBitmapKey(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        
        synchronized(lock) {
            // 检查容量限制
            while ((currentSizeBytes.get() + size > maxSizeBytes || 
                    currentCount.get() >= maxBitmapCount) && 
                   currentCount.get() > 0) {
                evictOldest()
            }
            
            val pool = pools.getOrPut(key) { 
                LinkedHashMap<Long, Bitmap>(16, 0.75f, true) // LRU访问顺序
            }
            
            val timestamp = System.currentTimeMillis()
            pool[timestamp] = bitmap
            
            updateSizeAndCount(size, 1)
            putCount.incrementAndGet()
            
            TimberLogger.v(TAG, "Bitmap已放入复用池: ${bitmap.width}x${bitmap.height}, " +
                    "当前池大小: ${currentCount.get()}/${maxBitmapCount}")
            return true
        }
    }
    
    override fun clear() {
        synchronized(lock) {
            pools.values.forEach { pool ->
                pool.values.forEach { bitmap ->
                    if (!bitmap.isRecycled) {
                        bitmap.recycle()
                    }
                }
                pool.clear()
            }
            pools.clear()
            currentSizeBytes.set(0)
            currentCount.set(0)
            
            TimberLogger.d(TAG, "复用池已清空")
        }
    }
    
    override fun size(): Int = currentCount.get()
    
    override fun getCurrentSizeBytes(): Long = currentSizeBytes.get().toLong()
    
    /**
     * 获取详细的统计信息
     */
    fun getStats(): BitmapPoolStats {
        return BitmapPoolStats(
            hitCount = hitCount.get(),
            missCount = missCount.get(),
            putCount = putCount.get(),
            evictionCount = evictionCount.get(),
            currentCount = currentCount.get(),
            currentSizeBytes = currentSizeBytes.get().toLong(),
            maxSizeBytes = maxSizeBytes,
            hitRate = if (hitCount.get() + missCount.get() > 0) {
                hitCount.get().toFloat() / (hitCount.get() + missCount.get())
            } else 0f
        )
    }
    
    /**
     * 强制清理策略，在内存压力下调用
     */
    fun trimMemory() {
        synchronized(lock) {
            val targetSize = maxSizeBytes / 2 // 清理到一半容量
            while (currentSizeBytes.get() > targetSize && currentCount.get() > 0) {
                evictOldest()
            }
            TimberLogger.d(TAG, "内存压力清理完成，当前大小: ${currentSizeBytes.get()}字节")
        }
    }
    
    // =============  Private Helper Methods  =============
    
    private fun getBitmapKey(width: Int, height: Int, config: Bitmap.Config): String {
        return "${width}x${height}_${config.name}"
    }
    
    private fun getBitmapSize(width: Int, height: Int, config: Bitmap.Config): Int {
        return width * height * when (config) {
            Bitmap.Config.ALPHA_8 -> 1
            Bitmap.Config.RGB_565 -> 2
            Bitmap.Config.ARGB_4444 -> 2
            Bitmap.Config.ARGB_8888 -> 4
            else -> 4
        }
    }
    
    private fun getBitmapSizeBytes(bitmap: Bitmap): Long {
        return bitmap.allocationByteCount.toLong()
    }
    
    private fun isBitmapSuitable(bitmap: Bitmap, width: Int, height: Int, config: Bitmap.Config): Boolean {
        return bitmap.width >= width && 
               bitmap.height >= height && 
               (bitmap.config == config || bitmap.config == null) &&
               // 尺寸不能相差太多，避免内存浪费
               bitmap.width <= width * (1 + BITMAP_SIZE_TOLERANCE) &&
               bitmap.height <= height * (1 + BITMAP_SIZE_TOLERANCE)
    }
    
    private fun recordMiss(): Bitmap? {
        missCount.incrementAndGet()
        return null
    }
    
    private fun evictOldest() {
        // 找到最老的Bitmap并移除
        for ((key, pool) in pools) {
            if (pool.isNotEmpty()) {
                val oldestEntry = pool.entries.iterator().next()
                val bitmap = oldestEntry.value
                pool.remove(oldestEntry.key)
                
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
                
                updateSizeAndCount(-getBitmapSizeBytes(bitmap), -1)
                evictionCount.incrementAndGet()
                
                if (pool.isEmpty()) {
                    pools.remove(key)
                }
                return
            }
        }
    }
    
    private fun updateSizeAndCount(sizeChange: Long, countChange: Int) {
        currentSizeBytes.addAndGet(sizeChange.toInt())
        currentCount.addAndGet(countChange)
    }
}

/**
 * Bitmap池统计信息
 */
@Stable
data class BitmapPoolStats(
    val hitCount: Int,
    val missCount: Int,
    val putCount: Int,
    val evictionCount: Int,
    val currentCount: Int,
    val currentSizeBytes: Long,
    val maxSizeBytes: Long,
    val hitRate: Float
) {
    override fun toString(): String {
        return "BitmapPoolStats(hit: $hitCount, miss: $missCount, put: $putCount, " +
               "evicted: $evictionCount, current: $currentCount/${maxSizeBytes/1024/1024}MB, " +
               "hitRate: ${(hitRate * 100).toInt()}%)"
    }
} 
