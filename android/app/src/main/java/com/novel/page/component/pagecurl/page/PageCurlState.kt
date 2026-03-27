package com.novel.page.component.pagecurl.page

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 记住PageCurlState实例
 * 
 * 这个函数会自动保存和恢复翻页状态，确保配置变化后状态保持
 *
 * @param initialCurrent 初始当前页面索引
 * @return 记住的PageCurlState实例
 */
@ExperimentalPageCurlApi
@Composable
fun rememberPageCurlState(
    initialCurrent: Int = 0,
): PageCurlState =
    rememberSaveable(
        initialCurrent,
        saver = Saver(
            save = { it.current },
            restore = { PageCurlState(initialCurrent = it) }
        )
    ) {
        PageCurlState(
            initialCurrent = initialCurrent,
        )
    }

/**
 * 翻页状态管理器
 * 
 * 用于管理翻页组件的状态，包括：
 * - 当前页面索引
 * - 翻页动画状态
 * - 页面总数管理
 * - 内部动画控制
 * 
 * 特点：
 * - 支持向前向后翻页
 * - 平滑的动画过渡
 * - 自动边界处理
 * - 响应式状态更新
 * 
 * @param initialCurrent 初始页面索引
 * @param initialMax 初始最大页面数
 */
@Stable
class PageCurlState(
    initialCurrent: Int = 0,
    initialMax: Int = 0
) {
    /**
     * 可观察的当前页面索引
     */
    var current: Int by mutableIntStateOf(initialCurrent)
        internal set

    /**
     * 可观察的翻页进度
     * 向前翻页时从0变化到1，向后翻页时从0变化到-1
     */
    val progress: Float get() = internalState?.progress ?: 0f

    /**
     * 页面总数（内部使用）
     */
    internal var max: Int = initialMax
        private set

    /**
     * 内部状态（包含动画状态和约束条件）
     */
    internal var internalState: InternalState? by mutableStateOf(null)
        private set

    /**
     * 设置页面数量和约束条件
     * 
     * @param count 页面总数
     * @param constraints 布局约束
     */
    internal fun setup(count: Int, constraints: Constraints) {
        max = count
        if (current >= count) {
            current = (count - 1).coerceAtLeast(0)
        }

        // 如果约束条件没有变化，不需要重新创建内部状态
        if (internalState?.constraints == constraints) {
            return
        }

        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        // 定义左右边界Edge（翻页的起始和结束位置）
        val left = Edge(Offset(0f, 0f), Offset(0f, maxHeightPx))
        val right = Edge(Offset(maxWidthPx, 0f), Offset(maxWidthPx, maxHeightPx))

        // 创建前进和后退动画控制器
        val forward = Animatable(right, Edge.VectorConverter, Edge.VisibilityThreshold)
        val backward = Animatable(left, Edge.VectorConverter, Edge.VisibilityThreshold)

        internalState = InternalState(constraints, left, right, forward, backward)
    }

    /**
     * 立即跳转到指定页面（无动画）
     *
     * @param value 目标页面索引
     */
    suspend fun snapTo(value: Int) {
        current = value.coerceIn(0, max - 1)
        internalState?.reset()
    }

    /**
     * 前进到下一页（带动画）
     *
     * @param block 动画控制块
     */
    suspend fun next(block: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = DefaultNext) {
        internalState?.animateTo(
            target = { current + 1 },
            animate = { forward.block(it) }
        )
    }

    /**
     * 后退到上一页（带动画）
     *
     * @param block 动画控制块
     */
    suspend fun prev(block: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = DefaultPrev) {
        internalState?.animateTo(
            target = { current - 1 },
            animate = { backward.block(it) }
        )
    }

    /**
     * 内部状态类
     * 
     * 管理动画状态和进度计算
     */
    internal inner class InternalState(
        val constraints: Constraints,
        val leftEdge: Edge,
        val rightEdge: Edge,
        val forward: Animatable<Edge, AnimationVector4D>,
        val backward: Animatable<Edge, AnimationVector4D>,
    ) {

        var animateJob: Job? = null

        val progress: Float by derivedStateOf {
            if (forward.value != rightEdge) {
                1f - forward.value.centerX / constraints.maxWidth
            } else if (backward.value != leftEdge) {
                -backward.value.centerX / constraints.maxWidth
            } else {
                0f
            }
        }

        suspend fun reset() {
            forward.snapTo(rightEdge)
            backward.snapTo(leftEdge)
        }

        suspend fun animateTo(
            target: () -> Int,
            animate: suspend InternalState.(Size) -> Unit
        ) {
            animateJob?.cancel()

            val targetIndex = target()
            if (targetIndex < 0 || targetIndex >= max) {
                return
            }

            coroutineScope {
                animateJob = launch {
                    try {
                        reset()
                        animate(Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()))
                    } finally {
                        withContext(NonCancellable) {
                            snapTo(target())
                        }
                    }
                }
            }
        }
    }
}

/**
 * 表示具有2个点的线的包装器：[top]和[bottom]
 */
data class Edge(val top: Offset, val bottom: Offset) {

    internal val centerX: Float = (top.x + bottom.x) * 0.5f

    internal companion object {
        val VectorConverter: TwoWayConverter<Edge, AnimationVector4D> =
            TwoWayConverter(
                convertToVector = { AnimationVector4D(it.top.x, it.top.y, it.bottom.x, it.bottom.y) },
                convertFromVector = { Edge(Offset(it.v1, it.v2), Offset(it.v3, it.v4)) }
            )

        val VisibilityThreshold: Edge =
            Edge(Offset.VisibilityThreshold, Offset.VisibilityThreshold)
    }
}

/**
 * 默认向前翻页动画
 */
private val DefaultNext: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = { size ->
    animateTo(
        targetValue = size.start,
        animationSpec = keyframes {
            durationMillis = DEFAULT_ANIM_DURATION
            size.end at 0
            size.middle at DEFAULT_MID_POINT_DURATION
        }
    )
}

/**
 * 默认向后翻页动画
 */
private val DefaultPrev: suspend Animatable<Edge, AnimationVector4D>.(Size) -> Unit = { size ->
    animateTo(
        targetValue = size.end,
        animationSpec = keyframes {
            durationMillis = DEFAULT_ANIM_DURATION
            size.start at 0
            size.middle at DEFAULT_ANIM_DURATION - DEFAULT_MID_POINT_DURATION
        }
    )
}

private const val DEFAULT_ANIM_DURATION: Int = 450
private const val DEFAULT_MID_POINT_DURATION: Int = 150

private val Size.start: Edge
    get() = Edge(Offset(0f, 0f), Offset(0f, height))

private val Size.middle: Edge
    get() = Edge(Offset(width, height / 2f), Offset(width / 2f, height))

private val Size.end: Edge
    get() = Edge(Offset(width, height), Offset(width, height))

