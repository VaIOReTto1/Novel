package com.novel.page.component

import android.graphics.Bitmap
import com.novel.utils.TimberLogger
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.novel.ui.theme.NovelColors
import com.novel.utils.wdp
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 渐进式加载状态
 */
@Stable
enum class ProgressiveLoadState {
    /** 初始状态，显示占位符 */
    INITIAL,
    /** 加载缩略图中 */
    LOADING_THUMBNAIL,
    /** 缩略图已加载 */
    THUMBNAIL_LOADED,
    /** 加载低质量图中 */
    LOADING_LOW_QUALITY,
    /** 低质量图已加载 */
    LOW_QUALITY_LOADED,
    /** 加载高质量图中 */
    LOADING_HIGH_QUALITY,
    /** 高质量图已加载（最终状态） */
    HIGH_QUALITY_LOADED,
    /** 加载失败 */
    ERROR
}

/**
 * 渐进式加载层级配置
 * 
 * @param size 图片尺寸限制
 * @param quality 压缩质量
 * @param blurRadius 模糊半径，0表示不模糊
 * @param fadeDuration 淡入动画时长(毫秒)
 */
@Stable
data class ProgressiveLayer(
    val size: Int,
    val quality: Int,
    val blurRadius: Float = 0f,
    val fadeDuration: Int = 300
) {
    companion object {
        /**
         * 创建默认的三层渐进式配置
         */
        fun createDefaultLayers(): List<ProgressiveLayer> = listOf(
            // 第一层：缩略图 (100px, 低质量, 高模糊)
            ProgressiveLayer(size = 100, quality = 50, blurRadius = 15f, fadeDuration = 200),
            // 第二层：低质量图 (400px, 中等质量, 轻微模糊)
            ProgressiveLayer(size = 400, quality = 70, blurRadius = 5f, fadeDuration = 300),
            // 第三层：高质量图 (原始尺寸, 高质量, 无模糊)
            ProgressiveLayer(size = Int.MAX_VALUE, quality = 90, blurRadius = 0f, fadeDuration = 400)
        )
        
        /**
         * 创建快速加载配置（两层）
         */
        fun createFastLayers(): List<ProgressiveLayer> = listOf(
            ProgressiveLayer(size = 200, quality = 60, blurRadius = 10f, fadeDuration = 150),
            ProgressiveLayer(size = Int.MAX_VALUE, quality = 85, blurRadius = 0f, fadeDuration = 250)
        )
        
        /**
         * 创建高质量配置（四层）
         */
        fun createHighQualityLayers(): List<ProgressiveLayer> = listOf(
            ProgressiveLayer(size = 80, quality = 40, blurRadius = 20f, fadeDuration = 100),
            ProgressiveLayer(size = 200, quality = 60, blurRadius = 10f, fadeDuration = 200),
            ProgressiveLayer(size = 600, quality = 80, blurRadius = 2f, fadeDuration = 300),
            ProgressiveLayer(size = Int.MAX_VALUE, quality = 95, blurRadius = 0f, fadeDuration = 400)
        )
    }
}

/**
 * 渐进式加载结果
 */
@Stable
data class ProgressiveLoadResult(
    val bitmap: Bitmap,
    val layer: Int,
    val isBlurred: Boolean,
    val loadTime: Long
)

/**
 * 渐进式图片加载器
 * 
 * 实现多层次图片加载策略：
 * 1. 首先加载模糊的小尺寸缩略图
 * 2. 然后加载中等质量的图片
 * 3. 最后加载高质量的完整图片
 * 4. 每个层级之间使用平滑的淡入动画过渡
 * 
 * 特点：
 * - 支持自定义层级配置
 * - 智能模糊效果生成
 * - 流畅的过渡动画
 * - 内存优化和及时回收
 * - 错误处理和降级策略
 */
@Singleton
@Stable
class ProgressiveImageLoader @Inject constructor(
    private val imageLoader: ImageLoaderService,
    private val imageCompressor: ImageCompressor,
    private val config: ImageOptimizationConfig
) {
    companion object {
        private const val TAG = "ProgressiveImageLoader"
        private const val MAX_BLUR_RADIUS = 25f
    }
    
    /**
     * 异步加载渐进式图片
     * 
     * @param url 图片URL
     * @param layers 渐进式层级配置
     * @param needToken 是否需要认证Token
     * @param onProgress 加载进度回调
     * @return 渐进式加载结果流
     */
    suspend fun loadProgressively(
        url: String,
        layers: List<ProgressiveLayer> = ProgressiveLayer.createDefaultLayers(),
        needToken: Boolean = false,
        onProgress: (ProgressiveLoadResult) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            TimberLogger.d(TAG, "开始渐进式加载: $url, ${layers.size}层")
            
            // 首先加载原始图片
            val originalBitmap = imageLoader.loadImage(url, needToken)
                ?: throw IllegalStateException("无法加载原始图片")
            
            // 按层级顺序处理
            for ((index, layer) in layers.withIndex()) {
                val startTime = System.currentTimeMillis()
                
                try {
                    val layerBitmap = createLayerBitmap(originalBitmap, layer, index)
                    val loadTime = System.currentTimeMillis() - startTime
                    
                    val result = ProgressiveLoadResult(
                        bitmap = layerBitmap,
                        layer = index,
                        isBlurred = layer.blurRadius > 0,
                        loadTime = loadTime
                    )
                    
                    onProgress(result)
                    
                    TimberLogger.v(TAG, "层级${index + 1}加载完成: ${layerBitmap.width}x${layerBitmap.height}, " +
                            "质量: ${layer.quality}, 模糊: ${layer.blurRadius}, 耗时: ${loadTime}ms")
                    
                    // 短暂延迟，让用户看到过渡效果
                    if (index < layers.size - 1) {
                        delay(layer.fadeDuration / 2L)
                    }
                    
                } catch (e: Exception) {
                    TimberLogger.e(TAG, "层级${index + 1}加载失败", e)
                    // 如果是第一层失败，抛出异常；否则继续下一层
                    if (index == 0) throw e
                }
            }
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "渐进式加载失败: $url", e)
            throw e
        }
    }
    
    /**
     * 创建指定层级的Bitmap
     */
    private suspend fun createLayerBitmap(
        originalBitmap: Bitmap,
        layer: ProgressiveLayer,
        layerIndex: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        
        // 1. 尺寸和质量压缩
        val compressionConfig = CompressionConfig(
            maxSize = if (layer.size == Int.MAX_VALUE) config.maxImageDimension else layer.size,
            quality = layer.quality,
            format = Bitmap.CompressFormat.JPEG
        )
        
        val compressedResult = imageCompressor.compressBitmap(originalBitmap, compressionConfig)
        var layerBitmap = compressedResult?.compressedBitmap ?: originalBitmap
        
        // 2. 应用模糊效果
        if (layer.blurRadius > 0f) {
            layerBitmap = applyBlurEffect(layerBitmap, layer.blurRadius)
        }
        
        layerBitmap
    }
    
    /**
     * 应用模糊效果
     * 使用RenderScript实现高效模糊
     */
    private suspend fun applyBlurEffect(
        bitmap: Bitmap,
        blurRadius: Float
    ): Bitmap = withContext(Dispatchers.IO) {
        try {
            val radius = blurRadius.coerceIn(0f, MAX_BLUR_RADIUS)
            if (radius <= 0f) return@withContext bitmap
            
            // 创建输出Bitmap
            val outputBitmap = Bitmap.createBitmap(
                bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888
            )
            
            // 使用简单的近似模糊算法（避免RenderScript依赖）
            applySimpleBlur(bitmap, outputBitmap, radius.toInt())
            
            TimberLogger.v(TAG, "模糊效果应用完成: 半径=$radius")
            outputBitmap
            
        } catch (e: Exception) {
            TimberLogger.e(TAG, "模糊效果应用失败", e)
            bitmap // 失败时返回原图
        }
    }
    
    /**
     * 简单模糊算法实现
     * 替代RenderScript，避免额外依赖
     */
    private fun applySimpleBlur(input: Bitmap, output: Bitmap, radius: Int) {
        val width = input.width
        val height = input.height
        val pixels = IntArray(width * height)
        val blurredPixels = IntArray(width * height)
        
        input.getPixels(pixels, 0, width, 0, 0, width, height)
        
        for (y in 0 until height) {
            for (x in 0 until width) {
                var red = 0
                var green = 0
                var blue = 0
                var alpha = 0
                var count = 0
                
                // 采样周围像素
                for (dy in -radius..radius) {
                    for (dx in -radius..radius) {
                        val nx = (x + dx).coerceIn(0, width - 1)
                        val ny = (y + dy).coerceIn(0, height - 1)
                        val pixel = pixels[ny * width + nx]
                        
                        alpha += (pixel shr 24) and 0xFF
                        red += (pixel shr 16) and 0xFF
                        green += (pixel shr 8) and 0xFF
                        blue += pixel and 0xFF
                        count++
                    }
                }
                
                // 计算平均值
                alpha /= count
                red /= count
                green /= count
                blue /= count
                
                blurredPixels[y * width + x] = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
            }
        }
        
        output.setPixels(blurredPixels, 0, width, 0, 0, width, height)
    }
}

/**
 * 渐进式图片加载Composable组件
 * 
 * 提供声明式的渐进式图片加载体验，自动处理状态管理和动画过渡
 * 
 * @param url 图片URL
 * @param layers 渐进式层级配置
 * @param needToken 是否需要认证Token
 * @param modifier 修饰符
 * @param contentScale 内容缩放模式
 * @param placeholder 占位符组件
 * @param errorContent 错误状态组件
 * @param onLoadingStateChange 加载状态变化回调
 */
@Composable
fun ProgressiveImageView(
    url: String?,
    layers: List<ProgressiveLayer> = ProgressiveLayer.createDefaultLayers(),
    needToken: Boolean = false,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    placeholder: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NovelColors.NovelMainLight),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                strokeWidth = 2.wdp,
                color = NovelColors.NovelTextGray
            )
        }
    },
    errorContent: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NovelColors.NovelMainLight),
            contentAlignment = Alignment.Center
        ) {
            // 显示错误图标或重试按钮
        }
    },
    onLoadingStateChange: (ProgressiveLoadState) -> Unit = {}
) {
    if (url.isNullOrEmpty()) {
        errorContent()
        return
    }
    
    var loadState by remember(url) { mutableStateOf(ProgressiveLoadState.INITIAL) }
    var currentBitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    var nextBitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    
    val progressiveLoader = LocalImageLoaderService.current as? ProgressiveImageLoader
    val scope = rememberCoroutineScope()
    
    // 加载状态动画
    val loadingAlpha by animateFloatAsState(
        targetValue = if (currentBitmap != null) 0f else 1f,
        animationSpec = tween(300),
        label = "loading_alpha"
    )
    
    // 图片过渡动画
    val imageAlpha by animateFloatAsState(
        targetValue = if (currentBitmap != null) 1f else 0f,
        animationSpec = tween(300),
        label = "image_alpha"
    )
    
    LaunchedEffect(url) {
        if (progressiveLoader != null) {
            loadState = ProgressiveLoadState.LOADING_THUMBNAIL
            onLoadingStateChange(loadState)
            
            try {
                progressiveLoader.loadProgressively(
                    url = url,
                    layers = layers,
                    needToken = needToken
                ) { result ->
                    nextBitmap = result.bitmap
                    
                    // 延迟更新，让动画更自然
                    scope.launch {
                        delay(result.layer * 100L) // 递增延迟
                        currentBitmap = nextBitmap
                        
                        // 更新加载状态
                        loadState = when (result.layer) {
                            0 -> ProgressiveLoadState.THUMBNAIL_LOADED
                            layers.size - 1 -> ProgressiveLoadState.HIGH_QUALITY_LOADED
                            else -> ProgressiveLoadState.LOW_QUALITY_LOADED
                        }
                        onLoadingStateChange(loadState)
                    }
                }
                
            } catch (e: Exception) {
                loadState = ProgressiveLoadState.ERROR
                onLoadingStateChange(loadState)
                TimberLogger.e("ProgressiveImageView", "渐进式加载失败: $url", e)
            }
        }
    }
    
    Box(modifier = modifier) {
        // 占位符 (loading状态)
        if (loadingAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(loadingAlpha)
            ) {
                placeholder()
            }
        }
        
        // 图片内容
        currentBitmap?.let { bitmap ->
            if (imageAlpha > 0f) {
                Image(
                    painter = BitmapPainter(bitmap.asImageBitmap()),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(imageAlpha),
                    contentScale = contentScale
                )
            }
        }
        
        // 错误状态
        if (loadState == ProgressiveLoadState.ERROR) {
            errorContent()
        }
    }
    
    // 清理资源
    DisposableEffect(url) {
        onDispose {
            currentBitmap?.let { if (!it.isRecycled) it.recycle() }
            nextBitmap?.let { if (!it.isRecycled) it.recycle() }
        }
    }
} 
