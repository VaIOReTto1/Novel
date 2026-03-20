package com.novel.page.component.pagecurl.page

import com.novel.utils.TimberLogger
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.novel.page.component.pagecurl.config.PageCurlConfig
import com.novel.page.component.pagecurl.config.rememberPageCurlConfig

/**
 * PageCurl 仿真翻页组件
 * 
 * 提供逼真的书页卷曲翻页效果，支持拖拽和点击手势
 * 采用高性能渲染策略，确保流畅的动画体验
 *
 * @param count 页面总数
 * @param modifier 修饰符
 * @param state PageCurl的状态，用于程序化改变当前页面或观察变化
 * @param config PageCurl的配置，包含手势和动画设置
 * @param content 内容lambda，提供页面可组合项，接收页面编号参数
 */
@ExperimentalPageCurlApi
@Composable
fun PageCurl(
    count: Int,
    modifier: Modifier = Modifier,
    state: PageCurlState = rememberPageCurlState(),
    config: PageCurlConfig = rememberPageCurlConfig(),
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {
        // 设置状态：页面数量和约束条件
        state.setup(count, constraints)

        // 获取更新的状态值，确保状态同步
        val updatedCurrent by rememberUpdatedState(state.current)
        val internalState by rememberUpdatedState(state.internalState ?: return@BoxWithConstraints)
        val updatedConfig by rememberUpdatedState(config)

        TimberLogger.d("PageCurl", "渲染页面 $updatedCurrent/$count")

        // 根据配置选择拖拽手势类型
        val dragGestureModifier = when (val interaction = updatedConfig.dragInteraction) {
            is PageCurlConfig.GestureDragInteraction ->
                Modifier
                    .dragGesture(
                        dragInteraction = interaction,
                        state = internalState,
                        enabledForward = updatedConfig.dragForwardEnabled && updatedCurrent < state.max - 1,
                        enabledBackward = updatedConfig.dragBackwardEnabled && updatedCurrent > 0,
                        scope = scope,
                        onChange = { 
                            TimberLogger.d("PageCurl", "页面变化: ${updatedCurrent} -> ${updatedCurrent + it}")
                            state.current = updatedCurrent + it 
                        }
                    )

            is PageCurlConfig.StartEndDragInteraction ->
                Modifier
                    .dragStartEnd(
                        dragInteraction = interaction,
                        state = internalState,
                        enabledForward = updatedConfig.dragForwardEnabled && updatedCurrent < state.max - 1,
                        enabledBackward = updatedConfig.dragBackwardEnabled && updatedCurrent > 0,
                        scope = scope,
                        onChange = { 
                            TimberLogger.d("PageCurl", "页面变化: ${updatedCurrent} -> ${updatedCurrent + it}")
                            state.current = updatedCurrent + it 
                        }
                    )
        }

        Box(
            Modifier
                .then(dragGestureModifier)
                .tapGesture(
                    config = updatedConfig,
                    scope = scope,
                    onTapForward = state::next,
                    onTapBackward = state::prev,
                )
        ) {
            // 使用key来同步状态更新，确保UI与状态保持一致
            key(updatedCurrent, internalState.forward.value, internalState.backward.value) {
                // 绘制下一页（背景页）- 在翻页时显示
                if (updatedCurrent + 1 < state.max) {
                    content(updatedCurrent + 1)
                }

                // 绘制当前页（前景页）- 带有卷曲效果
                if (updatedCurrent < state.max) {
                    val forward = internalState.forward.value
                    Box(Modifier.drawCurl(updatedConfig, forward.top, forward.bottom)) {
                        content(updatedCurrent)
                    }
                }

                // 绘制上一页（向后翻页时显示）
                if (updatedCurrent > 0) {
                    val backward = internalState.backward.value
                    Box(Modifier.drawCurl(updatedConfig, backward.top, backward.bottom)) {
                        content(updatedCurrent - 1)
                    }
                }
            }
        }
    }
}
