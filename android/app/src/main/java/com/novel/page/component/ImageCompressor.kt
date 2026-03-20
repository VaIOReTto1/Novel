package com.novel.page.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import com.novel.utils.TimberLogger
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

/**
 * 图片压缩配置
 * 
 * @param targetWidth 目标宽度，0表示保持原比例
 * @param targetHeight 目标高度，0表示保持原比例
 * @param maxSize 最大尺寸限制，图片最大边不超过此值
 * @param quality 压缩质量(0-100)
 * @param format 目标格式
 * @param enableSmartCompression 是否启用智能压缩（根据图片内容调整策略）
 * @param maxFileSizeKB 最大文件大小限制(KB)，0表示无限制
 */
@Stable
data class CompressionConfig(
    val targetWidth: Int = 0,
    val targetHeight: Int = 0,
    val maxSize: Int = 2048,
    val quality: Int = 85,
    val format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    val enableSmartCompression: Boolean = true,
    val maxFileSizeKB: Int = 0,
    val preserveAlpha: Boolean = true
) {
    companion object {
        /**
         * 创建适合网络传输的压缩配置
         */
        fun forNetwork() = CompressionConfig(
            maxSize = 1024,
            quality = 75,
            format = Bitmap.CompressFormat.JPEG,
            maxFileSizeKB = 200
        )
        
        /**
         * 创建适合缓存的压缩配置
         */
        fun forCache() = CompressionConfig(
            maxSize = 2048,
            quality = 90,
            format = Bitmap.CompressFormat.JPEG
        )
        
        /**
         * 创建缩略图压缩配置
         */
        fun forThumbnail(size: Int = 300) = CompressionConfig(
            targetWidth = size,
            targetHeight = size,
            quality = 70,
            format = Bitmap.CompressFormat.JPEG
        )
        
        /**
         * 根据网络状态创建配置
         */
        fun forNetworkType(isWifi: Boolean, isLowMemory: Boolean = false) = when {
            isWifi && !isLowMemory -> CompressionConfig(
                maxSize = 2048,
                quality = 90,
                format = Bitmap.CompressFormat.JPEG
            )
            isWifi && isLowMemory -> CompressionConfig(
                maxSize = 1024,
                quality = 80,
                format = Bitmap.CompressFormat.JPEG,
                maxFileSizeKB = 300
            )
            else -> CompressionConfig(
                maxSize = 800,
                quality = 70,
                format = Bitmap.CompressFormat.JPEG,
                maxFileSizeKB = 150
            )
        }
    }
}

/**
 * 压缩结果信息
 * 
 * @param compressedBitmap 压缩后的Bitmap
 * @param originalSize 原始尺寸
 * @param compressedSize 压缩后尺寸
 * @param originalBytes 原始文件大小(bytes)
 * @param compressedBytes 压缩后文件大小(bytes)
 * @param compressionRatio 压缩比例
 * @param processingTimeMs 处理时间(毫秒)
 */
@Stable
data class CompressionResult(
    val compressedBitmap: Bitmap,
    val originalSize: Pair<Int, Int>,
    val compressedSize: Pair<Int, Int>,
    val originalBytes: Int,
    val compressedBytes: Int,
    val compressionRatio: Float,
    val processingTimeMs: Long
)

/**
 * 高级图片压缩器
 * 
 * 提供多种压缩策略：
 * - 质量压缩：调整JPEG质量参数
 * - 尺寸压缩：智能等比例缩放
 * - 采样压缩：降低像素密度
 * - 格式转换：JPEG/PNG/WebP互转
 * - 智能压缩：根据图片内容自适应策略
 * 
 * 特性：
 * - 多线程异步处理
 * - 内存优化，及时回收中间Bitmap
 * - 支持Bitmap复用池
 * - 详细的压缩统计信息
 */
@Singleton
@Stable
class ImageCompressor @Inject constructor(
    private val bitmapPool: BitmapPool?,
    private val config: ImageOptimizationConfig
) {
    companion object {
        private const val TAG = "ImageCompressor"
        private const val MAX_ITERATIONS = 5 // 最大迭代次数，防止无限循环
        private const val SIZE_STEP_RATIO = 0.9f // 每次缩小比例
    }
    
    /**
     * 异步压缩图片
     * 
     * @param bitmap 原始Bitmap
     * @param compressionConfig 压缩配置
     * @return 压缩结果
     */
    suspend fun compressBitmap(
        bitmap: Bitmap,
        compressionConfig: CompressionConfig = CompressionConfig()
    ): CompressionResult? = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        
        try {
            if (bitmap.isRecycled) {
                TimberLogger.w(TAG, "尝试压缩已回收的Bitmap")
                return@withContext null
            }
            
            val originalSize = bitmap.width to bitmap.height
            val originalBytes = bitmap.allocationByteCount
            
            TimberLogger.d(TAG, "开始压缩图片: ${bitmap.width}x${bitmap.height}, " +
                    "原始大小: ${originalBytes / 1024}KB")
            
            // 1. 尺寸压缩
            var processedBitmap = resizeBitmap(bitmap, compressionConfig)
            
            // 2. 质量压缩（如果有文件大小限制）
            if (compressionConfig.maxFileSizeKB > 0) {
                processedBitmap = compressToTargetSize(processedBitmap, compressionConfig)
            }
            
            // 3. 格式优化
            processedBitmap = optimizeFormat(processedBitmap, compressionConfig)
            
            val compressedSize = processedBitmap.width to processedBitmap.height
            val compressedBytes = processedBitmap.allocationByteCount
            val processingTime = System.currentTimeMillis() - startTime
            val compressionRatio = compressedBytes.toFloat() / originalBytes
            
            TimberLogger.i(TAG, "图片压缩完成: ${compressedSize.first}x${compressedSize.second}, " +
                    "压缩后大小: ${compressedBytes / 1024}KB, " +
                    "压缩比: ${(compressionRatio * 100).toInt()}%, " +
                    "耗时: ${processingTime}ms")
            
            CompressionResult(
                compressedBitmap = processedBitmap,
                originalSize = originalSize,
                compressedSize = compressedSize,
                originalBytes = originalBytes,
                compressedBytes = compressedBytes,
                compressionRatio = compressionRatio,
                processingTimeMs = processingTime
            )
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "图片压缩失败", e)
            null
        }
    }
    
    /**
     * 从字节数组压缩图片
     */
    suspend fun compressFromBytes(
        imageBytes: ByteArray,
        compressionConfig: CompressionConfig = CompressionConfig()
    ): CompressionResult? = withContext(Dispatchers.IO) {
        try {
            // 使用采样率解码，减少内存占用
            val options = BitmapFactory.Options().apply {
                // 先获取图片尺寸信息
                inJustDecodeBounds = true
            }
            
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            
            // 计算采样率
            options.inSampleSize = calculateInSampleSize(options, compressionConfig)
            options.inJustDecodeBounds = false
            
            // 尝试从复用池获取Bitmap
            if (bitmapPool != null) {
                val targetWidth = options.outWidth / options.inSampleSize
                val targetHeight = options.outHeight / options.inSampleSize
                options.inBitmap = bitmapPool.get(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            }
            
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                ?: return@withContext null
                
            compressBitmap(bitmap, compressionConfig)
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "从字节数组压缩图片失败", e)
            null
        }
    }
    
    // ========== Private Helper Methods ==========
    
    /**
     * 尺寸压缩：智能等比例缩放
     */
    private suspend fun resizeBitmap(
        bitmap: Bitmap,
        config: CompressionConfig
    ): Bitmap = withContext(Dispatchers.IO) {
        val (currentWidth, currentHeight) = bitmap.width to bitmap.height
        
        // 计算目标尺寸
        val (targetWidth, targetHeight) = calculateTargetSize(
            currentWidth, currentHeight, config
        )
        
        if (targetWidth == currentWidth && targetHeight == currentHeight) {
            return@withContext bitmap
        }
        
        try {
            // 尝试从复用池获取Bitmap
            val reusableBitmap = if (bitmapPool != null) {
                bitmapPool.get(targetWidth, targetHeight, bitmap.config ?: Bitmap.Config.ARGB_8888)
            } else null
            
            val scaledBitmap = if (reusableBitmap != null) {
                val canvas = Canvas(reusableBitmap)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                val srcRect = Rect(0, 0, currentWidth, currentHeight)
                val destRect = Rect(0, 0, targetWidth, targetHeight)
                canvas.drawBitmap(bitmap, srcRect, destRect, paint)
                reusableBitmap
            } else {
                Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
            }
            
            TimberLogger.v(TAG, "尺寸压缩: ${currentWidth}x${currentHeight} -> ${targetWidth}x${targetHeight}")
            
            // 如果创建了新的Bitmap，回收原Bitmap到复用池
            if (scaledBitmap !== bitmap) {
                bitmapPool?.put(bitmap)
            }
            
            scaledBitmap
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "尺寸压缩失败", e)
            bitmap
        }
    }
    
    /**
     * 压缩到目标文件大小
     */
    private suspend fun compressToTargetSize(
        bitmap: Bitmap,
        config: CompressionConfig
    ): Bitmap = withContext(Dispatchers.IO) {
        if (config.maxFileSizeKB <= 0) return@withContext bitmap
        
        var quality = config.quality
        var currentBitmap = bitmap
        var iteration = 0
        
        while (iteration < MAX_ITERATIONS && quality > 10) {
            val size = getCompressedSize(currentBitmap, config.format, quality)
            val sizeKB = size / 1024
            
            TimberLogger.v(TAG, "质量压缩迭代 ${iteration + 1}: 质量=$quality, 大小=${sizeKB}KB")
            
            if (sizeKB <= config.maxFileSizeKB) {
                break
            }
            
            // 如果文件还是太大，降低质量并可能进一步缩小尺寸
            if (sizeKB > config.maxFileSizeKB * 2) {
                // 文件太大，同时降低质量和尺寸
                quality = (quality * 0.7).toInt()
                val newSize = (min(currentBitmap.width, currentBitmap.height) * SIZE_STEP_RATIO).toInt()
                val newConfig = config.copy(
                    targetWidth = newSize,
                    targetHeight = newSize,
                    quality = quality
                )
                currentBitmap = resizeBitmap(currentBitmap, newConfig)
            } else {
                // 只降低质量
                quality = (quality * 0.8).toInt()
            }
            
            iteration++
        }
        
        TimberLogger.d(TAG, "质量压缩完成: 最终质量=$quality, 迭代次数=$iteration")
        currentBitmap
    }
    
    /**
     * 格式优化
     */
    private suspend fun optimizeFormat(
        bitmap: Bitmap,
        config: CompressionConfig
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            // WebP格式优化（Android 4.0+支持WebP，Android 4.2.1+支持有损WebP）
            if (config.format == Bitmap.CompressFormat.WEBP && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return@withContext convertToWebP(bitmap, config)
            }
            
            // JPEG格式优化
            if (config.format == Bitmap.CompressFormat.JPEG) {
                return@withContext optimizeJpeg(bitmap, config)
            }
            
            bitmap
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "格式优化失败", e)
            bitmap
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun convertToWebP(bitmap: Bitmap, config: CompressionConfig): Bitmap {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.WEBP, config.quality, outputStream)
        val webpBytes = outputStream.toByteArray()
        
        return BitmapFactory.decodeByteArray(webpBytes, 0, webpBytes.size) ?: bitmap
    }
    
    private fun optimizeJpeg(bitmap: Bitmap, config: CompressionConfig): Bitmap {
        // 如果原图有透明度但要转换为JPEG，需要添加白色背景
        if (!config.preserveAlpha && bitmap.hasAlpha()) {
            val jpegBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(jpegBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            
            TimberLogger.v(TAG, "JPEG优化：移除透明通道并添加白色背景")
            return jpegBitmap
        }
        
        return bitmap
    }
    
    /**
     * 计算目标尺寸
     */
    private fun calculateTargetSize(
        currentWidth: Int,
        currentHeight: Int,
        config: CompressionConfig
    ): Pair<Int, Int> {
        // 如果指定了具体的目标尺寸
        if (config.targetWidth > 0 && config.targetHeight > 0) {
            return config.targetWidth to config.targetHeight
        }
        
        // 如果只指定了一个维度，保持比例
        if (config.targetWidth > 0) {
            val ratio = config.targetWidth.toFloat() / currentWidth
            return config.targetWidth to (currentHeight * ratio).toInt()
        }
        
        if (config.targetHeight > 0) {
            val ratio = config.targetHeight.toFloat() / currentHeight
            return (currentWidth * ratio).toInt() to config.targetHeight
        }
        
        // 如果都没指定，使用maxSize限制
        val maxDimension = max(currentWidth, currentHeight)
        if (maxDimension > config.maxSize) {
            val ratio = config.maxSize.toFloat() / maxDimension
            return (currentWidth * ratio).toInt() to (currentHeight * ratio).toInt()
        }
        
        // 不需要调整尺寸
        return currentWidth to currentHeight
    }
    
    /**
     * 计算采样率
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        config: CompressionConfig
    ): Int {
        val (reqWidth, reqHeight) = calculateTargetSize(
            options.outWidth, options.outHeight, config
        )
        
        var inSampleSize = 1
        
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            val halfHeight = options.outHeight / 2
            val halfWidth = options.outWidth / 2
            
            while ((halfHeight / inSampleSize) >= reqHeight && 
                   (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    /**
     * 获取压缩后的文件大小
     */
    private fun getCompressedSize(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ): Int {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, outputStream)
        return outputStream.size()
    }
} 
