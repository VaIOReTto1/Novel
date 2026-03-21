package com.novel.page.welfare.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ErrorType {
    NETWORK_ERROR,
    SSL_ERROR,
    HTTP_ERROR,
    TIMEOUT_ERROR,
    UNKNOWN_ERROR
}

data class ErrorInfo(
    val type: ErrorType,
    val title: String,
    val message: String,
    val icon: ImageVector,
    val primaryAction: String = "重试",
    val secondaryAction: String? = null
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnhancedErrorComponent(
    errorType: ErrorType = ErrorType.UNKNOWN_ERROR,
    customMessage: String? = null,
    onRetry: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val errorInfo = remember(errorType, customMessage) {
        getErrorInfo(errorType, customMessage)
    }

    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val iconScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "icon_scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 200,
            easing = FastOutSlowInEasing
        ),
        label = "content_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = errorInfo.icon,
            contentDescription = errorInfo.title,
            modifier = Modifier
                .size(80.dp)
                .scale(iconScale)
                .alpha(contentAlpha),
            tint = when (errorInfo.type) {
                ErrorType.NETWORK_ERROR -> MaterialTheme.colorScheme.error
                ErrorType.SSL_ERROR -> Color(0xFFFF6B35)
                ErrorType.HTTP_ERROR -> Color(0xFFFF8C42)
                ErrorType.TIMEOUT_ERROR -> Color(0xFFFFA726)
                ErrorType.UNKNOWN_ERROR -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = errorInfo.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(contentAlpha)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = errorInfo.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier
                .alpha(contentAlpha)
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 400,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 400
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = errorInfo.primaryAction,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                errorInfo.secondaryAction?.let { secondaryText ->
                    onSecondaryAction?.let { action ->
                        OutlinedButton(
                            onClick = action,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(44.dp),
                            shape = RoundedCornerShape(22.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.dp
                            )
                        ) {
                            Text(
                                text = secondaryText,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 400,
                    delayMillis = 600
                )
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = getHelpText(errorInfo.type),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

private fun getErrorInfo(errorType: ErrorType, customMessage: String?): ErrorInfo {
    return when (errorType) {
        ErrorType.NETWORK_ERROR -> {
            ErrorInfo(
                type = errorType,
                title = "网络连接失败",
                message = customMessage ?: "请检查您的网络连接，然后重试",
                icon = Icons.Default.Info,
                primaryAction = "重试",
                secondaryAction = "检查网络设置"
            )
        }
        ErrorType.SSL_ERROR -> ErrorInfo(
            type = errorType,
            title = "安全连接错误",
            message = customMessage ?: "网站的安全证书存在问题，无法建立安全连接",
            icon = Icons.Default.Info,
            primaryAction = "重试",
            secondaryAction = "了解更多"
        )
        ErrorType.HTTP_ERROR -> ErrorInfo(
            type = errorType,
            title = "服务器错误",
            message = customMessage ?: "服务器暂时无法响应，请稍后重试",
            icon = Icons.Default.Warning,
            primaryAction = "重试",
            secondaryAction = "反馈问题"
        )
        ErrorType.TIMEOUT_ERROR -> ErrorInfo(
            type = errorType,
            title = "连接超时",
            message = customMessage ?: "连接服务器超时，请检查网络状况后重试",
            icon = Icons.Default.Info,
            primaryAction = "重试",
            secondaryAction = "检查网络"
        )
        ErrorType.UNKNOWN_ERROR -> ErrorInfo(
            type = errorType,
            title = "加载失败",
            message = customMessage ?: "页面加载时遇到未知错误，请重试",
            icon = Icons.Default.Info,
            primaryAction = "重试"
        )
    }
}

private fun getHelpText(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.NETWORK_ERROR -> "确保设备已连接到互联网，或尝试切换到其他网络"
        ErrorType.SSL_ERROR -> "这可能是网站配置问题，建议联系网站管理员"
        ErrorType.HTTP_ERROR -> "服务器可能正在维护，请稍后再试或联系客服"
        ErrorType.TIMEOUT_ERROR -> "网络较慢时可能出现此问题，请耐心等待或重试"
        ErrorType.UNKNOWN_ERROR -> "如果问题持续存在，请尝试重启应用或联系技术支持"
    }
}
