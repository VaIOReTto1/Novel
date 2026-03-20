package com.novel.page.component

import androidx.compose.runtime.Stable

// ImageLoadingStrategy 现在定义在 NovelImageView.kt 中

/**
 * 内存等级枚举
 * 根据设备内存容量进行差异化配置
 */
@Stable  
enum class MemoryTier {
    /** 低内存设备 (<2GB) */
    LOW,
    /** 中等内存设备 (2-4GB) */
    MEDIUM, 
    /** 高内存设备 (>4GB) */
    HIGH
}

/**
 * 图片优化配置类
 * 
 * 提供图片加载系统的完整配置选项，支持：
 * - 内存管理优化参数
 * - 图片压缩和格式转换设置
 * - 渐进式加载配置
 * - 预加载策略控制
 * 
 * @param memoryCacheSizeMB 内存缓存大小(MB)
 * @param diskCacheSizeMB 磁盘缓存大小(MB)
 * @param maxImageDimension 图片最大尺寸限制
 * @param enableBitmapPool 是否启用Bitmap复用池
 * @param enableMemoryPressureHandling 是否启用内存压力处理
 * @param enableImageCompression 是否启用图片压缩
 * @param compressionQuality 压缩质量(0-100)
 * @param enableProgressiveLoading 是否启用渐进式加载
 * @param enablePreloading 是否启用预加载
 * @param preloadDistance 预加载距离(列表项数量)
 * @param loadingStrategy 默认加载策略
 * @param memoryTier 设备内存等级
 */
@Stable
data class ImageOptimizationConfig(
    // 内存管理配置
    val memoryCacheSizeMB: Int = 20,
    val diskCacheSizeMB: Long = 100,
    val maxImageDimension: Int = 2048,
    val enableBitmapPool: Boolean = true,
    val enableMemoryPressureHandling: Boolean = true,
    val memoryPressureThreshold: Float = 0.8f,
    
    // 图片压缩配置
    val enableImageCompression: Boolean = true,
    val compressionQuality: Int = 85,
    val enableWebP: Boolean = true,
    val enableAdaptiveCompression: Boolean = true,
    
    // 渐进式加载配置
    val enableProgressiveLoading: Boolean = true,
    val progressiveSteps: Int = 3,
    val blurRadius: Float = 25f,
    
    // 预加载配置
    val enablePreloading: Boolean = true,
    val preloadDistance: Int = 5,
    val maxPreloadConcurrency: Int = 3,
    
    // 策略配置
    val loadingStrategy: ImageLoadingStrategy = ImageLoadingStrategy.STANDARD,
    val memoryTier: MemoryTier = MemoryTier.MEDIUM
) {
    companion object {
        /**
         * 根据设备内存容量创建优化配置
         * 
         * @param availableMemoryMB 设备可用内存(MB)
         * @return 适配的配置实例
         */
        fun forMemoryCapacity(availableMemoryMB: Long): ImageOptimizationConfig {
            return when {
                availableMemoryMB < 2048 -> lowMemoryConfig()
                availableMemoryMB < 4096 -> mediumMemoryConfig() 
                else -> highMemoryConfig()
            }
        }
        
        /**
         * 低内存设备配置
         * 优先考虑内存使用效率
         */
        fun lowMemoryConfig() = ImageOptimizationConfig(
            memoryCacheSizeMB = 10,
            diskCacheSizeMB = 50,
            maxImageDimension = 1024,
            compressionQuality = 75,
            progressiveSteps = 2,
            preloadDistance = 2,
            maxPreloadConcurrency = 1,
            memoryTier = MemoryTier.LOW,
            loadingStrategy = ImageLoadingStrategy.HIGH_PERFORMANCE
        )
        
        /**
         * 中等内存设备配置
         * 平衡性能和内存使用
         */
        fun mediumMemoryConfig() = ImageOptimizationConfig(
            memoryCacheSizeMB = 20,
            diskCacheSizeMB = 100,
            maxImageDimension = 2048,
            compressionQuality = 85,
            progressiveSteps = 3,
            preloadDistance = 5,
            maxPreloadConcurrency = 2,
            memoryTier = MemoryTier.MEDIUM,
            loadingStrategy = ImageLoadingStrategy.STANDARD
        )
        
        /**
         * 高内存设备配置
         * 追求最佳用户体验
         */
        fun highMemoryConfig() = ImageOptimizationConfig(
            memoryCacheSizeMB = 40,
            diskCacheSizeMB = 200,
            maxImageDimension = 4096,
            compressionQuality = 95,
            progressiveSteps = 4,
            preloadDistance = 8,
            maxPreloadConcurrency = 3,
            memoryTier = MemoryTier.HIGH,
            loadingStrategy = ImageLoadingStrategy.HIGH_QUALITY
        )
        
        /**
         * 测试环境配置
         * 便于单元测试和调试
         */
        fun testConfig() = ImageOptimizationConfig(
            memoryCacheSizeMB = 5,
            diskCacheSizeMB = 20,
            enableBitmapPool = false,
            enableMemoryPressureHandling = false,
            enablePreloading = false
        )
    }
} 
