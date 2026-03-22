package com.novel.utils

import android.os.SystemClock
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * 防抖点击Modifier扩展
 * 
 * 功能特点：
 * - 防止用户快速重复点击
 * - 基于时间间隔的防抖机制
 * - 保持原有clickable的完整功能
 * - 支持自定义防抖间隔时间
 * 
 * 技术实现：
 * - SystemClock.elapsedRealtime()获取精确时间
 * - remember保存上次点击时间状态
 * - composed确保每次重组时创建新实例
 * 
 * 使用场景：
 * - 按钮防重复提交
 * - 列表项点击保护
 * - 网络请求防抖
 * 
 * 用法示例：
 * ```
 * Button(
 *     modifier = Modifier.debounceClickable(
 *         intervalMillis = 1000L,
 *         onClick = { /* 处理点击 */ }
 *     )
 * )
 * ```
 */
/**
 * 防抖点击扩展函数
 * 
 * @param intervalMillis 防抖间隔时间（毫秒），默认500ms
 * @param onClick 点击回调函数
 * @param enabled 是否启用点击，默认true
 * @param interactionSource 交互源，用于自定义点击效果
 */
@Composable
fun Modifier.debounceClickable(
    intervalMillis: Long = 500L,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier = composed {
    var lastTime by remember { mutableLongStateOf(0L) }
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = interactionSource
    ) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastTime > intervalMillis) {
            lastTime = now
            onClick()
        }
    }
}
