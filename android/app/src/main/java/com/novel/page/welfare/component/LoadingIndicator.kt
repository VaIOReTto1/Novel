package com.novel.page.welfare.component

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.novel.ui.theme.NovelColors

/**
 * WebView加载指示器组件
 *
 * 提供：
 * - 线性进度条（显示加载进度）
 * - 圆形加载指示器（初始加载状态）
 * - 加载文本提示
 */
@Composable
fun LoadingIndicator(
    progress: Int,
    isVisible: Boolean = true,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    if (isVisible && progress < 100) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress / 100f,
            animationSpec = tween(durationMillis = 300),
            label = "progress_animation"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = modifier.fillMaxWidth(),
            color = NovelColors.NovelMain,
            trackColor = Color.Transparent,
            // 末端用直角，而不是圆角
            strokeCap = StrokeCap.Butt
        )
    }
}

/**
 * 初始加载状态组件
 * 在WebView首次加载时显示
 */
@Composable
fun InitialLoadingState(
    message: String = "正在加载页面...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

/**
 * 错误状态组件
 * 在WebView加载失败时显示
 */
@Composable
fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "页面加载失败",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            androidx.compose.material3.TextButton(
                onClick = onRetry
            ) {
                Text(
                    text = "重试",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}