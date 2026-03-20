package com.novel.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.launch
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import com.novel.ui.theme.NovelColors

/**
 * 侧滑状态数据类 - 用于传递拖拽状态信息
 */
@Stable
data class SwipeBackState(
    val isDragging: Boolean = false,
    val offsetX: Float = 0f,
    val hintText: String = "",
    val hintAlpha: Float = 0f,
    val isOverThreshold: Boolean = false
)

/**
 * 高性能的 iOS 式侧滑返回手势处理 Modifier
 *
 * @param edgeWidthDp 检测手势的左侧热区宽度
 * @param firstThreshold 第一阶段阈值（屏宽百分比）- 显示"右滑退出阅读器"
 * @param completeThreshold 完成返回的阈值（屏宽百分比）- 显示"松开退出阅读器"
 * @param onSwipeStateChange 拖拽状态变化回调
 * @param onLeftSwipeToReader 左滑进入阅读器的回调（可选）
 */
@SuppressLint("RememberReturnType", "ConfigurationScreenWidthHeight")
fun Modifier.iosSwipeBack(
    edgeWidthDp: Dp = 300.wdp,
    firstThreshold: Float = 0.15f,
    completeThreshold: Float = 0.33f,
    onSwipeStateChange: ((SwipeBackState) -> Unit)? = null,
    onLeftSwipeToReader: (() -> Unit)? = null
): Modifier = composed {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    // 优化：预计算像素值，避免重复计算
    val currenct = LocalConfiguration.current
    val temp by remember(density) {
        derivedStateOf {
            val w = with(density) { currenct.screenWidthDp.dp.toPx() }
            val e = with(density) { edgeWidthDp.toPx() }
            val f = w * firstThreshold
            val c = w * completeThreshold
            listOf(w, e, f, c)
        }
    }
    val (widthPx, edgeWidthPx, firstThresholdPx, completeThresholdPx) = temp

    // 性能优化：使用单个 Animatable 而不是多个状态
    val offsetX = remember { Animatable(0f) }

    // 拖拽状态管理，避免频繁状态更新
    var isDragging by remember { mutableStateOf(false) }
    var justFinishedDrag by remember { mutableStateOf(false) }
    
    // 左滑手势状态
    var isLeftSwipeGesture by remember { mutableStateOf(false) }
    var leftSwipeDistance by remember { mutableFloatStateOf(0f) }
    var leftSwipeStartTime by remember { mutableLongStateOf(0L) }

    // 提示文字状态 - 使用 derivedStateOf 优化性能
    val hintText by remember {
        derivedStateOf {
            when {
                !isDragging || offsetX.value < firstThresholdPx -> ""
                offsetX.value >= completeThresholdPx -> "松\n开\n退\n出\n阅\n读\n器"
                else -> "右\n滑\n退\n出\n阅\n读\n器"
            }
        }
    }

    // 提示透明度 - 使用 derivedStateOf 优化性能
    val hintAlpha by remember {
        derivedStateOf {
            when {
                !isDragging -> 0f
                offsetX.value < firstThresholdPx -> 0f
                offsetX.value >= completeThresholdPx -> 1f
                else -> (offsetX.value - firstThresholdPx) / (completeThresholdPx - firstThresholdPx) * 0.8f + 0.2f
            }
        }
    }

    // 判断是否超过完成阈值
    val isOverThreshold by remember {
        derivedStateOf {
            offsetX.value >= completeThresholdPx
        }
    }

    val context = LocalContext.current

    // 优化：缓存震动器实例
    val vibrator = remember {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // 记录上次状态，优化震动触发
    var prevOverThreshold by remember { mutableStateOf(false) }

    LaunchedEffect(isOverThreshold) {
        if (isOverThreshold && !prevOverThreshold) {
            vibrator?.vibrate(
                VibrationEffect.createOneShot(
                    50, // 时长 50ms
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
        prevOverThreshold = isOverThreshold
    }

    // 优化：减少状态变化回调频率，使用快照机制
    LaunchedEffect(isDragging, offsetX.value, hintText, hintAlpha, isOverThreshold, justFinishedDrag) {
        val currentState = SwipeBackState(
            isDragging = isDragging,
            offsetX = offsetX.value,
            hintText = hintText,
            hintAlpha = hintAlpha,
            isOverThreshold = isOverThreshold
        )
        
        onSwipeStateChange?.invoke(currentState)
        
        // 如果刚完成拖拽且超过阈值，触发额外的完成回调
        if (justFinishedDrag && isOverThreshold) {
            justFinishedDrag = false
        }
    }

    // 性能优化：NestedScroll 连接复用，避免重复创建
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // 只在水平滑动且向右时拦截
                val dx = available.x
                return if (isDragging && dx > 0 && offsetX.value > 0) {
                    scope.launch {
                        offsetX.snapTo((offsetX.value + dx).coerceAtMost(widthPx))
                    }
                    Offset(dx, 0f)
                } else {
                    Offset.Zero
                }
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                if (isDragging) {
                    isDragging = false
                    justFinishedDrag = true
                    NavViewModel.navController.value?.let {
                        decideFinish(offsetX, completeThresholdPx)
                    }
                }
                return super.onPostFling(consumed, available)
            }
        }
    }

    this
        .pointerInput(Unit) {
            // 性能优化：集成的手势检测，处理两种手势
            detectHorizontalDragGestures(
                onDragStart = { pos ->
                    // 检测右滑返回手势（从左边缘开始）
                    if (pos.x <= edgeWidthPx) {
                        isDragging = true
                        isLeftSwipeGesture = false
                        justFinishedDrag = false
                    }
                    // 检测左滑进入阅读器手势（从右半部分开始）
                    else if (onLeftSwipeToReader != null && pos.x > widthPx * 0.4f) {
                        isLeftSwipeGesture = true
                        leftSwipeDistance = 0f
                        leftSwipeStartTime = System.currentTimeMillis()
                        isDragging = false // 这不是右滑返回手势
                    }
                },
                onHorizontalDrag = { _, dragAmount ->
                    if (isDragging && !isLeftSwipeGesture) {
                        // 处理右滑返回手势
                        val newOffset = (offsetX.value + dragAmount).coerceIn(0f, widthPx)
                        scope.launch { offsetX.snapTo(newOffset) }
                    } else if (isLeftSwipeGesture && onLeftSwipeToReader != null) {
                        // 处理左滑进入阅读器手势
                        if (dragAmount < 0) {
                            leftSwipeDistance += dragAmount
                        }
                        // 如果开始向右滑动，取消左滑手势
                        if (dragAmount > 10f && leftSwipeDistance > -50f) {
                            isLeftSwipeGesture = false
                        }
                    }
                },
                onDragEnd = {
                    if (isDragging && !isLeftSwipeGesture) {
                        // 处理右滑返回手势结束
                        isDragging = false
                        justFinishedDrag = true
                        scope.launch {
                            NavViewModel.navController.value?.let {
                                decideFinish(offsetX, completeThresholdPx)
                            }
                        }
                    } else if (isLeftSwipeGesture && onLeftSwipeToReader != null) {
                        // 处理左滑进入阅读器手势结束
                        val dragDuration = System.currentTimeMillis() - leftSwipeStartTime
                        
                        // 验证左滑手势有效性
                        val isValidLeftSwipe = leftSwipeDistance < -120f && // 向左滑动至少120像素
                            dragDuration < 800L && // 滑动时间不超过800ms
                            dragDuration > 100L // 滑动时间至少100ms
                        
                        if (isValidLeftSwipe) {
                            onLeftSwipeToReader.invoke()
                        }
                        
                        // 重置左滑状态
                        isLeftSwipeGesture = false
                        leftSwipeDistance = 0f
                    }
                },
                onDragCancel = {
                    if (isDragging) {
                        isDragging = false
                        justFinishedDrag = false
                        scope.launch {
                            offsetX.animateTo(
                                0f,
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessMedium,
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            )
                        }
                    }
                    // 重置左滑状态
                    isLeftSwipeGesture = false
                    leftSwipeDistance = 0f
                }
            )
        }
        // 性能关键：使用 graphicsLayer 做硬件加速的位移
        .graphicsLayer {
            translationX = offsetX.value
            // 轻微阴影效果提升视觉体验
            shadowElevation = if (isDragging && offsetX.value > 0) 8f else 0f
        }
        .nestedScroll(connection)
}

/**
 * iOS 风格的侧滑返回背景指示器
 * 显示在被滑出的背景区域，而不是当前页面上
 */
@Composable
fun SwipeBackIndicator(
    swipeState: SwipeBackState,
    modifier: Modifier = Modifier,
    textColor: Color = NovelColors.NovelTextGray
) {
    Box(
        modifier = modifier
            .padding(end = 8.wdp)
            .fillMaxHeight()
            .width(20.wdp), // 宽度跟随滑动距离
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = swipeState.hintText,
            lineHeight = 10.sp,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

/**
 * 带有背景指示器的侧滑返回容器组件
 * 指示器显示在被滑出的背景区域
 *
 * @param modifier 修饰符
 * @param edgeWidthDp 检测手势的左侧热区宽度
 * @param firstThreshold 第一阶段阈值
 * @param completeThreshold 完成返回的阈值
 * @param backgroundColor 背景颜色
 * @param onSwipeComplete 侧滑完成回调
 * @param onLeftSwipeToReader 左滑进入阅读器回调（可选）
 * @param content 子内容
 */
@Composable
fun SwipeBackContainer(
    modifier: Modifier = Modifier,
    edgeWidthDp: Dp = 300.wdp,
    firstThreshold: Float = 0.05f,
    completeThreshold: Float = 0.3f,
    backgroundColor: Color = NovelColors.NovelBookBackground.copy(alpha = 0.7f), // 默认浅灰色背景
    textColor: Color = NovelColors.NovelTextGray,
    onSwipeComplete: (() -> Unit)? = null, // 侧滑完成回调
    onLeftSwipeToReader: (() -> Unit)? = null, // 左滑进入阅读器回调
    content: @Composable BoxScope.() -> Unit
) {
    var swipeState by remember { mutableStateOf(SwipeBackState()) }
    var hasTriggeredComplete by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        // 背景层 - 显示指示器的地方
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .width(swipeState.offsetX.dp / 3)
        ) {
            // 背景指示器 - 显示在被露出的区域
            SwipeBackIndicator(
                swipeState = swipeState,
                modifier = Modifier.align(Alignment.CenterEnd),
                textColor = textColor
            )
        }

        // 前景内容层 - 可以滑动的页面
        Box(
            modifier = Modifier
                .fillMaxSize()
                .iosSwipeBack(
                    edgeWidthDp = edgeWidthDp,
                    firstThreshold = firstThreshold,
                    completeThreshold = completeThreshold,
                    onSwipeStateChange = { newState ->
                        val prevState = swipeState
                        swipeState = newState
                        
                        // 检测拖拽刚结束且超过阈值的情况
                        if (prevState.isDragging && !newState.isDragging && newState.isOverThreshold && !hasTriggeredComplete) {
                            hasTriggeredComplete = true
                            onSwipeComplete?.invoke()
                        }
                        
                        // 重置完成标志（当重新开始拖拽时）
                        if (newState.isDragging && !prevState.isDragging) {
                            hasTriggeredComplete = false
                        }
                    },
                    onLeftSwipeToReader = onLeftSwipeToReader
                )
        ) {
            content()
        }
    }
}

/**
 * 简化版本：为任意 Composable 增加基础的 iOS 式侧滑返回手势
 * 不包含提示UI，性能更优
 */
fun Modifier.iosSwipeBackBasic(
    edgeWidthDp: Dp = 300.wdp,
    completeThreshold: Float = 0.33f
): Modifier = iosSwipeBack(
    edgeWidthDp = edgeWidthDp,
    firstThreshold = 0.15f,
    completeThreshold = completeThreshold,
    onSwipeStateChange = null
)

/**
 * 性能优化版本：决定是否完成返回操作
 */
private suspend fun decideFinish(
    anim: Animatable<Float, AnimationVector1D>,
    thresholdPx: Float,
) {
    if (anim.value >= thresholdPx) {
        // 快速完成动画并执行返回 - 使用NavViewModel来触发返回事件
        NavViewModel.navigateBack() // 使用我们的NavViewModel来触发返回事件
    } else {
        // 弹性回弹动画
        anim.animateTo(
            0f,
            animationSpec = spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
    }
}
