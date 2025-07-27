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
import com.novel.utils.TimberLogger

/**
 * 福利页面可访问性助手
 * 
 * 功能特点：
 * - 检测无障碍服务状态
 * - 为WebView添加语义描述
 * - 提供键盘导航支持
 * - 优化屏幕阅读器体验
 */
class WelfareAccessibilityHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "WelfareAccessibilityHelper"
    }
    
    private val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    
    /**
     * 检查是否启用了无障碍服务
     */
    fun isAccessibilityEnabled(): Boolean {
        return accessibilityManager.isEnabled
    }
    
    /**
     * 检查是否启用了触摸探索模式
     */
    fun isTouchExplorationEnabled(): Boolean {
        return accessibilityManager.isTouchExplorationEnabled
    }
    
    /**
     * 配置WebView的无障碍设置
     */
    fun configureWebViewAccessibility(webView: WebView, pageTitle: String = "福利页面") {
        try {
            webView.apply {
                // 设置内容描述
                contentDescription = "$pageTitle - 网页内容"
                
                // 启用无障碍功能
                settings.apply {
                    // 确保JavaScript可以与无障碍服务交互
                    javaScriptEnabled = true
                    
                    // 启用DOM存储，某些无障碍功能可能需要
                    domStorageEnabled = true
                }
                
                // 设置焦点
                isFocusable = true
                isFocusableInTouchMode = true
            }
            
            TimberLogger.d(TAG, "WebView无障碍配置完成: $pageTitle")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "配置WebView无障碍功能失败", e)
        }
    }
    
    /**
     * 获取无障碍状态信息
     */
    fun getAccessibilityStatus(): AccessibilityStatus {
        return AccessibilityStatus(
            isEnabled = isAccessibilityEnabled(),
            isTouchExplorationEnabled = isTouchExplorationEnabled(),
            enabledServices = getEnabledAccessibilityServices()
        )
    }
    
    /**
     * 获取已启用的无障碍服务列表
     */
    private fun getEnabledAccessibilityServices(): List<String> {
        return try {
            accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            ).map { it.id }
        } catch (e: Exception) {
            TimberLogger.e(TAG, "获取无障碍服务列表失败", e)
            emptyList()
        }
    }
}

/**
 * 无障碍状态数据类
 */
data class AccessibilityStatus(
    val isEnabled: Boolean = false,
    val isTouchExplorationEnabled: Boolean = false,
    val enabledServices: List<String> = emptyList()
)

/**
 * 为Composable添加无障碍语义
 */
@Composable
fun Modifier.welfareAccessibility(
    description: String,
    role: Role = Role.Button
): Modifier {
    return this.semantics {
        contentDescription = description
        // 移除role设置，因为它是私有属性
    }
}

/**
 * 无障碍状态监听器
 */
@Composable
fun AccessibilityStateMonitor(
    onAccessibilityStateChanged: (AccessibilityStatus) -> Unit
) {
    val context = LocalContext.current
    val accessibilityHelper = remember { WelfareAccessibilityHelper(context) }
    
    val currentStatus by remember { mutableStateOf(accessibilityHelper.getAccessibilityStatus()) }
    
    LaunchedEffect(Unit) {
        // 初始状态回调
        onAccessibilityStateChanged(currentStatus)
        
        // 这里可以添加状态变化监听器
        // 由于AccessibilityManager没有直接的状态变化监听器
        // 可以考虑使用定时检查或其他方式
        TimberLogger.d("AccessibilityStateMonitor", "无障碍状态监听器已启动")
    }
}

/**
 * WebView无障碍配置器
 */
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
    
    // 监听无障碍状态变化
    AccessibilityStateMonitor { status ->
        if (status.isEnabled) {
            TimberLogger.d("WebViewAccessibilityConfigurator", "检测到无障碍服务已启用")
            webView?.let { view ->
                accessibilityHelper.configureWebViewAccessibility(view, pageTitle)
            }
        }
    }
}