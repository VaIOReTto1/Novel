@file:Suppress("MatchingDeclarationName")

package com.novel.page.component.pagecurl.page

import com.novel.utils.TimberLogger
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntSize
import com.novel.page.component.pagecurl.utils.rotate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.PI

/**
 * 拖拽配置数据类
 * 
 * 包含拖拽操作所需的所有配置信息
 *
 * @param edge 边缘动画控制器
 * @param start 起始边缘位置
 * @param end 结束边缘位置
 * @param isEnabled 是否启用拖拽的检查函数
 * @param isDragSucceed 拖拽是否成功的判断函数
 * @param onChange 变化回调
 */
internal data class DragConfig(
    val edge: Animatable<Edge, AnimationVector4D>,
    val start: Edge,
    val end: Edge,
    val isEnabled: () -> Boolean,
    val isDragSucceed: (Offset, Offset) -> Boolean,
    val onChange: () -> Unit,
)

/**
 * 检测卷曲手势的核心函数
 * 支持拖拽和惯性滑动，提供流畅的翻页体验
 *
 * @param scope 协程作用域
 * @param newEdgeCreator 新边缘创建器
 * @param getConfig 获取拖拽配置的函数
 */
internal suspend fun PointerInputScope.detectCurlGestures(
    scope: CoroutineScope,
    newEdgeCreator: NewEdgeCreator,
    getConfig: (Offset, Offset) -> DragConfig?,
) {
    val TAG = "DragCommonGesture"
    // 使用速度追踪器支持惯性滑动
    val velocityTracker = VelocityTracker()

    var config: DragConfig? = null
    var startOffset: Offset = Offset.Zero

    TimberLogger.d(TAG, "开始监听卷曲手势")

    detectCustomDragGestures(
        onDragStart = { start, end ->
            startOffset = start
            config = getConfig(start, end)
            val hasConfig = config != null
            
            if (hasConfig) {
                TimberLogger.v(TAG, "开始拖拽 - 起始位置: $start")
            }
            
            hasConfig
        },
        onDragEnd = { endOffset, complete ->
            config?.apply {
                val velocity = velocityTracker.calculateVelocity()
                val decay = splineBasedDecay<Offset>(this@detectCurlGestures)
                
                // 计算惯性滑动的结束位置
                val flingEndOffset = decay.calculateTargetValue(
                    Offset.VectorConverter,
                    endOffset,
                    Offset(velocity.x, velocity.y)
                ).let {
                    Offset(
                        it.x.coerceIn(0f, size.width.toFloat() - 1),
                        it.y.coerceIn(0f, size.height.toFloat() - 1)
                    )
                }

                val isSuccessful = complete && isDragSucceed(startOffset, flingEndOffset)
                TimberLogger.d(TAG, "拖拽结束 - 成功: $isSuccessful, 完成: $complete")

                scope.launch {
                    if (isSuccessful) {
                        // 拖拽成功，完成翻页动画
                        try {
                            edge.animateTo(end)
                        } catch (e: Exception) {
                            TimberLogger.e(TAG, "翻页动画执行失败", e)
                        } finally {
                            onChange()
                            edge.snapTo(start)
                        }
                    } else {
                        // 拖拽未成功，返回原始位置
                        try {
                            edge.animateTo(start)
                        } catch (e: Exception) {
                            TimberLogger.e(TAG, "返回原始位置动画失败", e)
                        } finally {
                            edge.snapTo(start)
                        }
                    }
                }
            }
        },
        onDrag = { change, _ ->
            config?.apply {
                if (!isEnabled()) {
                    TimberLogger.w(TAG, "拖拽被禁用，取消手势")
                    throw CancellationException()
                }

                velocityTracker.addPosition(System.currentTimeMillis(), change.position)

                scope.launch {
                    val target = newEdgeCreator.createNew(size, startOffset, change.position)
                    edge.animateTo(target)
                }
            }
        }
    )
}

/**
 * 自定义拖拽手势检测
 * 
 * 提供比标准拖拽手势更精细的控制
 *
 * @param onDragStart 拖拽开始回调
 * @param onDragEnd 拖拽结束回调
 * @param onDrag 拖拽进行中回调
 */
internal suspend fun PointerInputScope.detectCustomDragGestures(
    onDragStart: (Offset, Offset) -> Boolean,
    onDragEnd: (Offset, Boolean) -> Unit,
    onDrag: (change: PointerInputChange, dragAmount: Offset) -> Unit
) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var drag: PointerInputChange?
        var overSlop = Offset.Zero
        
        // 等待超过触摸阈值的拖拽
        do {
            drag = awaitTouchSlopOrCancellation(down.id) { change, over ->
                change.consume()
                overSlop = over
            }
        } while (drag != null && !drag.isConsumed)
        
        if (drag != null) {
            if (!onDragStart.invoke(down.position, drag.position)) {
                return@awaitEachGesture
            }
            
            onDrag(drag, overSlop)
            
            // 持续跟踪拖拽
            val completed = drag(drag.id) {
                drag = it
                onDrag(it, it.positionChange())
                it.consume()
            }
            
            onDragEnd(drag?.position ?: down.position, completed)
        }
    }
}

/**
 * 新边缘创建器抽象类
 * 
 * 用于根据拖拽位置创建新的页面边缘
 */
internal abstract class NewEdgeCreator {

    /**
     * 创建新的边缘
     * 
     * @param size 容器尺寸
     * @param startOffset 起始偏移
     * @param currentOffset 当前偏移
     * @return 新的边缘对象
     */
    abstract fun createNew(size: IntSize, startOffset: Offset, currentOffset: Offset): Edge

    /**
     * 创建向量对，计算页面卷曲的基础向量
     * 
     * @param size 容器尺寸
     * @param startOffset 起始偏移
     * @param currentOffset 当前偏移
     * @return 向量对（原始向量和旋转后的向量）
     */
    protected fun createVectors(size: IntSize, startOffset: Offset, currentOffset: Offset): Pair<Offset, Offset> {
        val vector = Offset(size.width.toFloat(), startOffset.y) - currentOffset
        val rotatedVector = vector.rotate(Offset.Zero, PI.toFloat() / 2)
        return vector to rotatedVector
    }

    /**
     * 默认边缘创建器
     * 创建基础的卷曲边缘效果
     */
    class Default : NewEdgeCreator() {
        override fun createNew(size: IntSize, startOffset: Offset, currentOffset: Offset): Edge {
            val vectors = createVectors(size, startOffset, currentOffset)
            return Edge(currentOffset - vectors.second, currentOffset + vectors.second)
        }
    }

    /**
     * 页面边缘创建器
     * 创建更真实的页面翻页效果
     */
    class PageEdge : NewEdgeCreator() {
        override fun createNew(size: IntSize, startOffset: Offset, currentOffset: Offset): Edge {
            val (vector, rotatedVector) = createVectors(size, startOffset, currentOffset)
            return Edge(currentOffset - rotatedVector + vector / 2f, currentOffset + rotatedVector + vector / 2f)
        }
    }
}
