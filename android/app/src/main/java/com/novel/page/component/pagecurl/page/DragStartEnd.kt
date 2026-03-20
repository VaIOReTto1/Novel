package com.novel.page.component.pagecurl.page

import com.novel.utils.TimberLogger
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import com.novel.page.component.pagecurl.config.PageCurlConfig
import com.novel.page.component.pagecurl.config.PageCurlConfig.DragInteraction.PointerBehavior
import com.novel.page.component.pagecurl.utils.multiply
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 起始-结束拖拽手势修饰符
 * 基于拖拽起始和结束位置的交互模式
 * 用户需要从指定区域开始拖拽并在指定区域结束才能完成翻页
 *
 * @param dragInteraction 拖拽交互配置
 * @param state PageCurl内部状态
 * @param enabledForward 是否启用向前拖拽
 * @param enabledBackward 是否启用向后拖拽
 * @param scope 协程作用域
 * @param onChange 页面变化回调
 * @return 应用了拖拽手势的修饰符
 */
@ExperimentalPageCurlApi
internal fun Modifier.dragStartEnd(
    dragInteraction: PageCurlConfig.StartEndDragInteraction,
    state: PageCurlState.InternalState,
    enabledForward: Boolean,
    enabledBackward: Boolean,
    scope: CoroutineScope,
    onChange: (Int) -> Unit,
): Modifier = this.composed {
    val isEnabledForward = rememberUpdatedState(enabledForward)
    val isEnabledBackward = rememberUpdatedState(enabledBackward)

    TimberLogger.d("DragStartEnd", "配置起始-结束拖拽手势 - 向前: $enabledForward, 向后: $enabledBackward")

    pointerInput(state) {
        // 计算相对区域的实际像素坐标
        val forwardStartRect by lazy { 
            val rect = dragInteraction.forward.start.multiply(size)
            TimberLogger.v("DragStartEnd", "向前拖拽起始区域: $rect")
            rect
        }
        val forwardEndRect by lazy { 
            val rect = dragInteraction.forward.end.multiply(size)
            TimberLogger.v("DragStartEnd", "向前拖拽结束区域: $rect")
            rect
        }
        val backwardStartRect by lazy { 
            val rect = dragInteraction.backward.start.multiply(size)
            TimberLogger.v("DragStartEnd", "向后拖拽起始区域: $rect")
            rect
        }
        val backwardEndRect by lazy { 
            val rect = dragInteraction.backward.end.multiply(size)
            TimberLogger.v("DragStartEnd", "向后拖拽结束区域: $rect")
            rect
        }

        // 向前拖拽配置
        val forwardConfig = DragConfig(
            edge = state.forward,
            start = state.rightEdge,
            end = state.leftEdge,
            isEnabled = { isEnabledForward.value },
            isDragSucceed = { _, end -> 
                val success = forwardEndRect.contains(end)
                if (success) {
                    TimberLogger.d("DragStartEnd", "向前拖拽成功结束于目标区域")
                }
                success
            },
            onChange = { 
                TimberLogger.d("DragStartEnd", "向前翻页完成")
                onChange(+1) 
            }
        )
        
        // 向后拖拽配置
        val backwardConfig = DragConfig(
            edge = state.backward,
            start = state.leftEdge,
            end = state.rightEdge,
            isEnabled = { isEnabledBackward.value },
            isDragSucceed = { _, end -> 
                val success = backwardEndRect.contains(end)
                if (success) {
                    TimberLogger.d("DragStartEnd", "向后拖拽成功结束于目标区域")
                }
                success
            },
            onChange = { 
                TimberLogger.d("DragStartEnd", "向后翻页完成")
                onChange(-1) 
            }
        )

        // 检测卷曲手势
        detectCurlGestures(
            scope = scope,
            newEdgeCreator = when (dragInteraction.pointerBehavior) {
                PointerBehavior.Default -> NewEdgeCreator.Default()
                PointerBehavior.PageEdge -> NewEdgeCreator.PageEdge()
            },
            getConfig = { start, _ ->
                val config = if (forwardStartRect.contains(start)) {
                    TimberLogger.v("DragStartEnd", "从向前拖拽起始区域开始")
                    forwardConfig
                } else if (backwardStartRect.contains(start)) {
                    TimberLogger.v("DragStartEnd", "从向后拖拽起始区域开始")
                    backwardConfig
                } else {
                    null
                }

                // 如果找到配置，取消之前的动画并重置状态
                if (config != null) {
                    scope.launch {
                        state.animateJob?.cancel()
                        state.reset()
                    }
                }

                config
            },
        )
    }
} 
