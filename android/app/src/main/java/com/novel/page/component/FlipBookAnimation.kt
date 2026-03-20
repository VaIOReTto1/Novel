package com.novel.page.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.novel.ui.theme.NovelColors
import com.novel.utils.ssp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.novel.utils.wdp
import androidx.compose.ui.graphics.TransformOrigin
import com.facebook.react.uimanager.PixelUtil.dpToPx
import kotlinx.coroutines.coroutineScope
import com.novel.utils.NavViewModel
import com.novel.utils.TimberLogger
import com.novel.page.read.ReaderPage

/**
 * 翻书动画状态
 */
@Stable
data class FlipBookState(
    val isAnimating: Boolean = false,
    val isOpening: Boolean = true, // true: 打开书籍, false: 合上书籍
    val coverRotationProgress: Float = 0f, // 封面旋转进度 [0f, 1f]
    val scaleProgress: Float = 0f,    // 缩放进度 [0f, 1f]
    val alphaProgress: Float = 1f,    // 透明度进度 [1f, 0.3f]
    val bookId: String? = null,
    val originalImageUrl: String? = null,
    val originalPosition: Offset = Offset.Zero,
    val originalSize: androidx.compose.ui.geometry.Size = androidx.compose.ui.geometry.Size.Zero,
    val targetScale: Float = 1f,
    val showContent: Boolean = false, // 是否显示书籍内容页
    val hideOriginalImage: Boolean = false, // 是否隐藏原始图片（用于共享元素动画）
    val animationType: AnimationType = AnimationType.FLIP_3D // 动画类型
)

/**
 * 动画类型枚举
 */
enum class AnimationType {
    FLIP_3D,        // 3D翻书动画
    SCALE_FADE      // 放大透明动画
}

/**
 * 翻书动画控制器 - 真正的3D翻书效果，使用共享元素动画
 */
@Composable
fun rememberFlipBookAnimationController(): FlipBookAnimationController {
    return remember { FlipBookAnimationController() }
}

@Stable
class FlipBookAnimationController {
    private var _animationState by mutableStateOf(FlipBookState())
    val animationState: FlipBookState get() = _animationState

    // 动画完成回调
    private var onAnimationComplete: (() -> Unit)? = null

    /**
     * 开始放大透明动画（从推荐流进入）
     * 书籍封面放大到全屏并逐渐透明到0.3，然后显示BookDetailPage
     */
    suspend fun startScaleFadeAnimation(
        bookId: String,
        imageUrl: String,
        originalPosition: Offset,
        originalSize: androidx.compose.ui.geometry.Size,
        screenWidth: Float = 1080f,
        screenHeight: Float = 2400f
    ) {
        TimberLogger.d("FlipBookController", "开始放大透明动画: bookId=$bookId")

        // 计算目标缩放比例
        val horScale = screenWidth / originalSize.width
        val verScale = screenHeight / originalSize.height
        val targetScale = maxOf(horScale, verScale) * 1.2f // 稍微放大一点

        _animationState = FlipBookState(
            isAnimating = true,
            isOpening = true,
            bookId = bookId,
            originalImageUrl = imageUrl,
            originalPosition = originalPosition,
            originalSize = originalSize,
            targetScale = targetScale,
            showContent = true, // 立即开始预加载内容
            hideOriginalImage = true,
            animationType = AnimationType.SCALE_FADE
        )

        coroutineScope {
            // 创建放大和透明度动画
            val scaleAnimatable = Animatable(0f)
            val alphaAnimatable = Animatable(1f)

            // 启动并行动画 - 使用Spring动画提升自然度
            val scaleJob = launch {
                scaleAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = 2f, // 更大的阻尼比
                        stiffness = Spring.StiffnessLow, // 更低的刚度
                        visibilityThreshold = 0.001f
                    )
                )
            }

            val alphaJob = launch {
                alphaAnimatable.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 2f, // 更大的阻尼比
                        stiffness = Spring.StiffnessLow, // 更低的刚度
                        visibilityThreshold = 0.001f
                    )
                )
            }

            // 实时更新状态 - 优化更新频率
            val updateJob = launch {
                var lastScale = -1f
                var lastAlpha = -1f

                while (scaleAnimatable.isRunning || alphaAnimatable.isRunning) {
                    val currentScale = scaleAnimatable.value
                    val currentAlpha = alphaAnimatable.value

                    // 提高阈值，减少不必要的更新
                    if (kotlin.math.abs(currentScale - lastScale) > 0.01f ||
                        kotlin.math.abs(currentAlpha - lastAlpha) > 0.01f
                    ) {
                        _animationState = _animationState.copy(
                            scaleProgress = currentScale,
                            alphaProgress = currentAlpha
                        )

                        lastScale = currentScale
                        lastAlpha = currentAlpha
                    }

                    delay(16) // 约60fps更新
                }
            }

            // 等待动画完成
            scaleJob.join()
            alphaJob.join()
            updateJob.cancel()

            // 最终状态
            _animationState = _animationState.copy(
                scaleProgress = 1f,
                alphaProgress = 0f,
                showContent = true
            )

            onAnimationComplete?.invoke()
        }
    }

    /**
     * 开始翻书动画（打开书籍）
     * 使用共享元素动画：隐藏原始图片，在全局层显示动画
     */
    suspend fun startFlipAnimation(
        bookId: String,
        imageUrl: String,
        originalPosition: Offset,
        originalSize: androidx.compose.ui.geometry.Size,
        screenWidth: Float = 1080f,
        screenHeight: Float = 2400f
    ) {
        // 计算目标缩放比例
        val horScale = screenWidth / originalSize.width
        val verScale = screenHeight / originalSize.height
        val targetScale = maxOf(horScale, verScale)

        _animationState = FlipBookState(
            isAnimating = true,
            isOpening = true,
            bookId = bookId,
            originalImageUrl = imageUrl,
            originalPosition = originalPosition,
            originalSize = originalSize,
            targetScale = targetScale,
            showContent = true,
            hideOriginalImage = true // 隐藏原始图片，使用全局动画
        )

        coroutineScope {

            // 创建并行动画
            val coverRotationAnimatable = Animatable(0f)
            val scaleAnimatable = Animatable(0f)

            // 启动并行动画
            val coverRotationJob = launch {
                coverRotationAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
                    )
                )
            }

            val scaleJob = launch {
                scaleAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 800,
                        easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)
                    )
                )
            }

            // 优化：减少更新频率，只在值变化时更新
            val updateJob = launch {
                var lastRotation = -1f
                var lastScale = -1f

                while (coverRotationAnimatable.isRunning || scaleAnimatable.isRunning) {
                    val currentRotation = coverRotationAnimatable.value
                    val currentScale = scaleAnimatable.value

                    // 只在值发生显著变化时更新状态（减少不必要的重组）
                    if (kotlin.math.abs(currentRotation - lastRotation) > 0.001f ||
                        kotlin.math.abs(currentScale - lastScale) > 0.001f
                    ) {

                        _animationState = _animationState.copy(
                            coverRotationProgress = currentRotation,
                            scaleProgress = currentScale
                        )

                        lastRotation = currentRotation
                        lastScale = currentScale
                    }

                    delay(16) // 约60fps更新
                }
            }

            // 等待动画完成
            coverRotationJob.join()
            scaleJob.join()
            updateJob.cancel()

            _animationState = _animationState.copy(
                coverRotationProgress = 1f,
                scaleProgress = 1f
            )

            onAnimationComplete?.invoke()
        }
    }

    /**
     * 开始倒放动画（合上书籍）
     * 恢复原始图片显示，隐藏全局动画
     */
    private suspend fun startReverseAnimation() {
        val currentAnimationType = _animationState.animationType

        _animationState = _animationState.copy(
            isOpening = false,
            coverRotationProgress = if (currentAnimationType == AnimationType.FLIP_3D) 1f else 0f,
            scaleProgress = 1f, // 从全屏开始
            alphaProgress = if (currentAnimationType == AnimationType.SCALE_FADE) 0.7f else 1f,
            hideOriginalImage = true // 继续隐藏原始图片直到动画结束
        )

        coroutineScope {
            when (currentAnimationType) {
                AnimationType.SCALE_FADE -> {
                    // 放大透明动画的倒放：缩小到原始大小并恢复透明度
                    val scaleAnimatable = Animatable(1f)
                    val alphaAnimatable = Animatable(0f)

                    // 计算原始缩放比例（相对于全屏的比例）
                    val originalScale = 0f

                    val scaleJob = launch {
                        scaleAnimatable.animateTo(
                            targetValue = originalScale,
                            animationSpec = spring(
                                dampingRatio = 0.4f,
                                stiffness = Spring.StiffnessLow,
                                visibilityThreshold = 0.001f
                            )
                        )
                    }

                    val alphaJob = launch {
                        alphaAnimatable.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 400,
                                easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
                            )
                        )
                    }

                    val updateJob = launch {
                        var lastScale = -1f
                        var lastAlpha = -1f

                        while (scaleAnimatable.isRunning || alphaAnimatable.isRunning) {
                            TimberLogger.d(
                                "Animation",
                                "Scale: ${_animationState.scaleProgress}, Alpha: ${_animationState.alphaProgress}"
                            )
                            val currentScale = scaleAnimatable.value
                            val currentAlpha = alphaAnimatable.value

                            if (kotlin.math.abs(currentScale - lastScale) > 0.005f ||
                                kotlin.math.abs(currentAlpha - lastAlpha) > 0.005f
                            ) {
                                _animationState = _animationState.copy(
                                    scaleProgress = currentScale,
                                    alphaProgress = currentAlpha
                                )

                                lastScale = currentScale
                                lastAlpha = currentAlpha
                            }

                            delay(16)
                        }
                    }
                    TimberLogger.d(
                        "Animation",
                        "Scale: ${_animationState.scaleProgress}, Alpha: ${_animationState.alphaProgress}"
                    )

                    scaleJob.join()
                    alphaJob.join()
                    updateJob.cancel()
                }

                AnimationType.FLIP_3D -> {
                    // 3D翻书动画的倒放：缩小到原始大小
                    val coverRotationAnimatable = Animatable(1f)
                    val scaleAnimatable = Animatable(1f)

                    // 计算原始缩放比例（相对于全屏的比例）
                    val originalScale = if (animationState.targetScale > 0f) {
                        0.15f / animationState.targetScale
                    } else {
                        0.3f // 默认缩小到30%
                    }

                    val coverRotationJob = launch {
                        coverRotationAnimatable.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                            )
                        )
                    }

                    val scaleJob = launch {
                        scaleAnimatable.animateTo(
                            targetValue = originalScale,
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)
                            )
                        )
                    }

                    val updateJob = launch {
                        var lastRotation = -1f
                        var lastScale = -1f

                        while (coverRotationAnimatable.isRunning || scaleAnimatable.isRunning) {
                            val currentRotation = coverRotationAnimatable.value
                            val currentScale = scaleAnimatable.value

                            if (kotlin.math.abs(currentRotation - lastRotation) > 0.001f ||
                                kotlin.math.abs(currentScale - lastScale) > 0.001f
                            ) {
                                _animationState = _animationState.copy(
                                    coverRotationProgress = currentRotation,
                                    scaleProgress = currentScale
                                )

                                lastRotation = currentRotation
                                lastScale = currentScale
                            }

                            delay(16)
                        }
                    }

                    coverRotationJob.join()
                    scaleJob.join()
                    updateJob.cancel()
                }
            }

            // 延迟一小段时间，确保动画完全完成
            delay(100)

            // 清理状态 - 恢复原始图片显示，并触发导航返回
            _animationState = FlipBookState() // 完全重置状态，恢复原始图片

            // 动画完成后触发导航返回
            NavViewModel.navigateBack()
        }
    }

    /**
     * 触发倒放动画的便捷方法
     */
    suspend fun triggerReverseAnimation() {
        if (_animationState.isAnimating && _animationState.isOpening) {
            TimberLogger.d(
                "FlipBookController",
                "开始执行倒放动画: ${_animationState.animationType}"
            )
            startReverseAnimation()
        } else {
            TimberLogger.w(
                "FlipBookController",
                "无法触发倒放动画 - isAnimating: ${_animationState.isAnimating}, isOpening: ${_animationState.isOpening}"
            )
        }
    }
}

/**
 * 全局翻书动画覆盖层 - 真正的3D翻书效果，性能优化版本
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GlobalFlipBookOverlay(
    controller: FlipBookAnimationController,
    getBookImageUrl: ((String) -> String)? = null
) {
    val animationState = controller.animationState
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // 预计算屏幕尺寸，避免重复计算
    val screenSize by remember(configuration) {
        derivedStateOf {
            androidx.compose.ui.geometry.Size(
                configuration.screenWidthDp.dp.value.dpToPx(),
                configuration.screenHeightDp.dp.value.dpToPx()
            )
        }
    }

    // 性能关键：只有在动画进行时才渲染，完全避免无效渲染
    if (!animationState.isAnimating || animationState.bookId.isNullOrEmpty()) {
        return
    }

    val imageUrl by remember(animationState.bookId, animationState.originalImageUrl) {
        derivedStateOf {
            when {
                !animationState.originalImageUrl.isNullOrEmpty() -> animationState.originalImageUrl
                getBookImageUrl != null && animationState.bookId.isNotEmpty() ->
                    getBookImageUrl.invoke(animationState.bookId)

                else -> ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1000f)
    ) {
        // 书籍内容视图 (BookDetailPage) - 根据动画类型决定显示方式
        if (animationState.showContent) {
            when (animationState.animationType) {
                AnimationType.SCALE_FADE -> {
                    // 放大透明动画：BookDetailPage直接覆盖全屏
                    val contentAlpha by
                        remember(animationState.scaleProgress, animationState.isOpening) {
                            derivedStateOf {
                                when {
                                    !animationState.isOpening -> 0f // 倒放时立即隐藏内容
                                    animationState.scaleProgress > 0.7f -> 1f
                                    animationState.scaleProgress > 0.5f -> (animationState.scaleProgress - 0.5f) * 5f // 0.5-0.7区间淡入
                                    else -> 0f
                                }
                            }
                        }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = contentAlpha
                                // 倒放时快速隐藏，避免残影
                                if (!animationState.isOpening && animationState.scaleProgress < 1f) {
                                    alpha = 0f
                                }
                            }
                    ) {
                        ReaderPage(
                            bookId = animationState.bookId,
                            chapterId = null,
                            flipBookController = controller,
                        )
//                        BookDetailPage(
//                            bookId = animationState.bookId,
//                            fromRank = true,
//                            flipBookController = controller,
//                            onNavigateToReader = { bookId, chapterId ->
//                                NavViewModel.navigateToReader(bookId, chapterId)
//                            }
//                        )
                    }
                }

                AnimationType.FLIP_3D -> {
                    // 3D翻书动画：原有的缩放动画
                    val scaleAndOrigin by remember(
                        animationState.scaleProgress,
                        animationState.originalPosition,
                        screenSize
                    ) {
                        derivedStateOf {
                            val progress = animationState.scaleProgress
                            val scale = if (progress <= 0.5f) {
                                progress * 0.4f
                            } else {
                                0.2f + (progress - 0.5f) * 1.6f
                            }

                            val origin = TransformOrigin(
                                pivotFractionX = animationState.originalPosition.x / screenSize.width,
                                pivotFractionY = animationState.originalPosition.y / screenSize.height
                            )

                            scale to origin
                        }
                    }
                    val (currentScale, _) = scaleAndOrigin

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = currentScale
                                scaleY = currentScale
                                alpha = if (animationState.scaleProgress > 0.3f) 1f else 0.3f
                            }
                    ) {
//                        BookDetailPage(
//                            bookId = animationState.bookId,
//                            fromRank = true,
//                            flipBookController = controller,
//                            onNavigateToReader = { bookId, chapterId ->
//                                NavViewModel.navigateToReader(bookId, chapterId)
//                            }
//                        )
                        ReaderPage(
                            bookId = animationState.bookId,
                            chapterId = null,
                            flipBookController = controller,
                        )
                    }
                }
            }
        }

        // 书籍封面视图 - 根据动画类型显示不同效果
        if (animationState.hideOriginalImage) {
            when (animationState.animationType) {
                AnimationType.SCALE_FADE -> {
                    // 放大透明动画：封面从原始位置放大到全屏并透明化
                    val animationParams by remember(
                        animationState.scaleProgress,
                        animationState.alphaProgress,
                        animationState.originalPosition,
                        animationState.targetScale,
                        screenSize
                    ) {
                        derivedStateOf {
                            val scaleProgress = animationState.scaleProgress
                            val alphaProgress = animationState.alphaProgress
                            val baseX = animationState.originalPosition.x
                            val baseY = animationState.originalPosition.y - 120.wdp.value
                            val centerX = screenSize.width * 0.5f
                            val centerY = screenSize.height * 0.5f

                            // 使用平滑的缓动函数，让位移更自然
                            val easedProgress = if (scaleProgress <= 0.5f) {
                                2f * scaleProgress * scaleProgress // 加速
                            } else {
                                -1f + (4f - 2f * scaleProgress) * scaleProgress // 减速
                            }

                            val offsetX =
                                baseX + (centerX - baseX - animationState.originalSize.width * 0.5f) * easedProgress
                            val offsetY =
                                baseY + (centerY - baseY - animationState.originalSize.height * 0.5f) * easedProgress
                            val scale = 1f + (animationState.targetScale - 1f) * scaleProgress

                            Tuple4(offsetX, offsetY, scale, alphaProgress)
                        }
                    }
                    val (offsetX, offsetY, scale, alpha) = animationParams

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                                transformOrigin = TransformOrigin(0.5f, 0.5f)
                            }
                    ) {
                        // 书籍封面图片 - 动画模式
                        NovelImageView(
                            imageUrl = imageUrl,
                            loadingStrategy = ImageLoadingStrategy.ANIMATION,
                            useAdvancedCache = true,
                            modifier = Modifier
                                .size(
                                    animationState.originalSize.width.wdp / density.density,
                                    animationState.originalSize.height.wdp / density.density
                                )
                                .clip(RoundedCornerShape(8.wdp))
                                .background(NovelColors.NovelMain),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            placeholderContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NovelColors.NovelMain),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NovelText(
                                        text = "📖",
                                        fontSize = 20.ssp,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        )
                    }
                }

                AnimationType.FLIP_3D -> {
                    // 3D翻书动画：原有的旋转效果
                    val temp by remember(
                        animationState.scaleProgress,
                        animationState.coverRotationProgress,
                        animationState.originalPosition,
                        animationState.targetScale,
                        screenSize
                    ) {
                        derivedStateOf {
                            val scaleProgress = animationState.scaleProgress
                            val rotationProgress = animationState.coverRotationProgress
                            val baseX = animationState.originalPosition.x
                            val baseY = animationState.originalPosition.y - 120.wdp.value
                            val targetY = screenSize.height * 0.5f

                            val offsetX = (baseX * (1 - scaleProgress)).toInt()
                            val offsetY = (baseY + ((targetY - baseY) * scaleProgress)).toInt()
                            val rotationY = -90f * rotationProgress
                            val scale = 1f + (animationState.targetScale - 1f) * scaleProgress

                            Tuple4(offsetX, offsetY, rotationY, scale)
                        }
                    }
                    val (offsetX, offsetY, rotationY, scale) = temp

                    Box(
                        modifier = Modifier
                            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                            .graphicsLayer {
                                this.rotationY = rotationY.toFloat()
                                scaleX = scale.toFloat()
                                scaleY = scale.toFloat()
                                cameraDistance = 12f * density.density
                                transformOrigin = TransformOrigin(0f, 0.5f)
                                shadowElevation =
                                    if (animationState.coverRotationProgress > 0) 12.dp.toPx() else 0f
                                alpha = 1f
                            }
                    ) {
                        // 书籍封面图片 - 3D动画模式
                        NovelImageView(
                            imageUrl = imageUrl,
                            loadingStrategy = ImageLoadingStrategy.ANIMATION,
                            useAdvancedCache = true,
                            modifier = Modifier
                                .size(
                                    animationState.originalSize.width.wdp / density.density,
                                    animationState.originalSize.height.wdp / density.density
                                )
                                .clip(RoundedCornerShape(4.wdp))
                                .background(NovelColors.NovelMain),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            placeholderContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NovelColors.NovelMain),
                                    contentAlignment = Alignment.Center
                                ) {
                                    NovelText(
                                        text = "📖",
                                        fontSize = 20.ssp,
                                        color = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// 辅助数据类，用于减少对象创建
private data class Tuple4<T>(val first: T, val second: T, val third: T, val fourth: T)

/**
 * 简化的书籍点击处理器
 * 提供启动动画的接口，支持精确位置
 */
@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun rememberBookClickHandler(
    controller: FlipBookAnimationController,
    bookId: String,
    imageUrl: String,
    position: Offset = Offset.Zero,
    size: androidx.compose.ui.geometry.Size = androidx.compose.ui.geometry.Size.Zero,
): () -> Unit {
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    /* ---------- 纯计算 → derivedStateOf ---------- */

    // 1) 最终位置
    val finalPosition by remember(position) {
        derivedStateOf {
            if (position != Offset.Zero) position else Offset(200f, 300f)
        }
    }

    // 2) 最终尺寸
    val finalSize by remember(size) {
        derivedStateOf {
            if (size != androidx.compose.ui.geometry.Size.Zero) size
            else androidx.compose.ui.geometry.Size(150f, 200f)
        }
    }

    // 3) 屏幕像素尺寸
    val screenSizePx by remember(configuration, density) {
        derivedStateOf {
            val w = configuration.screenWidthDp * density.density
            val h = configuration.screenHeightDp * density.density
            w to h                       // Pair<Float, Float>
        }
    }

    /* ---------- click‑handler ---------- */

    return remember(bookId, imageUrl, finalPosition, finalSize, screenSizePx) {
        {
            coroutineScope.launch {
                controller.startFlipAnimation(
                    bookId = bookId,
                    imageUrl = imageUrl,
                    originalPosition = finalPosition,
                    originalSize = finalSize,
                    screenWidth  = screenSizePx.first,
                    screenHeight = screenSizePx.second,
                )
            }
        }
    }
}
