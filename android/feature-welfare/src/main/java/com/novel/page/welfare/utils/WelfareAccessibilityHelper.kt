package com.novel.page.welfare.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.novel.core.logging.CoreLogger

class WelfareAccessibilityHelper(private val context: Context) {

    companion object {
        private const val TAG = "WelfareAccessibilityHelper"
    }

    private val accessibilityManager =
        context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

    fun isAccessibilityEnabled(): Boolean = accessibilityManager.isEnabled

    fun isTouchExplorationEnabled(): Boolean = accessibilityManager.isTouchExplorationEnabled

    fun configureWebViewAccessibility(webView: WebView, pageTitle: String = "福利页面") {
        try {
            webView.apply {
                contentDescription = "$pageTitle - 网页内容"
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                }
                isFocusable = true
                isFocusableInTouchMode = true
            }
            CoreLogger.d(TAG, "WebView无障碍配置完成: $pageTitle")
        } catch (e: Exception) {
            CoreLogger.e(TAG, "配置WebView无障碍功能失败", e)
        }
    }

    fun getAccessibilityStatus(): AccessibilityStatus {
        return AccessibilityStatus(
            isEnabled = isAccessibilityEnabled(),
            isTouchExplorationEnabled = isTouchExplorationEnabled(),
            enabledServices = getEnabledAccessibilityServices()
        )
    }

    private fun getEnabledAccessibilityServices(): List<String> {
        return try {
            accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).map { it.id }
        } catch (e: Exception) {
            CoreLogger.e(TAG, "获取无障碍服务列表失败", e)
            emptyList()
        }
    }
}

data class AccessibilityStatus(
    val isEnabled: Boolean = false,
    val isTouchExplorationEnabled: Boolean = false,
    val enabledServices: List<String> = emptyList()
)

@Composable
fun Modifier.welfareAccessibility(
    description: String,
    role: Role = Role.Button
): Modifier {
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun AccessibilityStateMonitor(
    onAccessibilityStateChanged: (AccessibilityStatus) -> Unit
) {
    val context = LocalContext.current
    val accessibilityHelper = remember { WelfareAccessibilityHelper(context) }
    val currentStatus by remember { mutableStateOf(accessibilityHelper.getAccessibilityStatus()) }

    LaunchedEffect(Unit) {
        onAccessibilityStateChanged(currentStatus)
        CoreLogger.d("AccessibilityStateMonitor", "无障碍状态监听器已启动")
    }
}

@Composable
fun WebViewAccessibilityConfigurator(
    webView: WebView?,
    pageTitle: String = "福利页面",
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val accessibilityHelper = remember { WelfareAccessibilityHelper(context) }

    LaunchedEffect(webView, pageTitle, isLoading) {
        webView?.let { view ->
            val description = if (isLoading) {
                "$pageTitle - 正在加载"
            } else {
                pageTitle
            }
            accessibilityHelper.configureWebViewAccessibility(view, description)
        }
    }

    AccessibilityStateMonitor { status ->
        if (status.isEnabled) {
            CoreLogger.d("WebViewAccessibilityConfigurator", "检测到无障碍服务已启用")
            webView?.let { view ->
                accessibilityHelper.configureWebViewAccessibility(view, pageTitle)
            }
        }
    }
}
