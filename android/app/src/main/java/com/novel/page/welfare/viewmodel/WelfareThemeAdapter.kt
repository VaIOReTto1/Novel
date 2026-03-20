package com.novel.page.welfare.component

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import android.webkit.WebView
import com.novel.utils.TimberLogger

/**
 * 福利页面深色模式适配器
 * 
 * 功能特点：
 * - 自动检测系统深色模式状态
 * - 动态调整WebView深色模式设置
 * - 提供深色模式状态回调
 * - 支持手动切换深色模式
 */
@Composable
fun WelfareThemeAdapter(
    webView: WebView?,
    onThemeChanged: (Boolean) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val isSystemDarkTheme = isSystemInDarkTheme()
    
    var currentDarkMode by remember { mutableStateOf(isSystemDarkTheme) }
    
    // 监听系统深色模式变化
    LaunchedEffect(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        val isDarkMode = (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (currentDarkMode != isDarkMode) {
            currentDarkMode = isDarkMode
            updateWebViewDarkMode(webView, isDarkMode)
            onThemeChanged(isDarkMode)
            TimberLogger.d("WelfareThemeAdapter", "深色模式状态变更: $isDarkMode")
        }
    }
    
    // 初始化WebView深色模式
    LaunchedEffect(webView) {
        webView?.let {
            updateWebViewDarkMode(it, currentDarkMode)
        }
    }
}

/**
 * 更新WebView深色模式设置
 */
fun updateWebViewDarkMode(webView: WebView?, isDarkMode: Boolean) {
    webView?.let { view ->
        try {
            // 1. 设置WebView原生深色模式 - 使用新的API适配Android 13+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // Android 13+ 使用新的API
                view.settings.isAlgorithmicDarkeningAllowed = isDarkMode
                TimberLogger.d("WelfareThemeAdapter", "WebView新API深色模式已更新: $isDarkMode")
            } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                // Android 10-12 使用旧的API
                val forceDarkMode = if (isDarkMode) {
                    WebSettingsCompat.FORCE_DARK_ON
                } else {
                    WebSettingsCompat.FORCE_DARK_OFF
                }
                WebSettingsCompat.setForceDark(view.settings, forceDarkMode)
                TimberLogger.d("WelfareThemeAdapter", "WebView旧API深色模式已更新: $isDarkMode")
            }
            
            // 2. 注入CSS样式作为备用方案
            if (isDarkMode) {
                injectDarkModeCSS(view)
            } else {
                removeDarkModeCSS(view)
            }
            
            TimberLogger.d("WelfareThemeAdapter", "WebView深色模式完整更新: $isDarkMode")
        } catch (e: Exception) {
            TimberLogger.e("WelfareThemeAdapter", "更新WebView深色模式失败", e)
        }
    }
}

/**
 * 注入深色模式CSS样式
 */
private fun injectDarkModeCSS(webView: WebView) {
    val darkModeCSS = """
        javascript:(function() {
            var style = document.getElementById('welfare-dark-mode-style');
            if (!style) {
                style = document.createElement('style');
                style.id = 'welfare-dark-mode-style';
                style.innerHTML = `
                    html, body {
                        background-color: #1a1a1a !important;
                        color: #e0e0e0 !important;
                        filter: invert(1) hue-rotate(180deg) !important;
                    }
                    img, video, iframe, svg {
                        filter: invert(1) hue-rotate(180deg) !important;
                    }
                    [style*="background"] {
                        background-color: #1a1a1a !important;
                    }
                    [style*="color"] {
                        color: #e0e0e0 !important;
                    }
                `;
                document.head.appendChild(style);
            }
        })()
    """.trimIndent()
    
    webView.evaluateJavascript(darkModeCSS, null)
}

/**
 * 移除深色模式CSS样式
 */
private fun removeDarkModeCSS(webView: WebView) {
    val removeCSS = """
        javascript:(function() {
            var style = document.getElementById('welfare-dark-mode-style');
            if (style) {
                style.remove();
            }
        })()
    """.trimIndent()
    
    webView.evaluateJavascript(removeCSS, null)
}

/**
 * 深色模式状态数据类
 */
data class ThemeState(
    val isDarkMode: Boolean = false,
    val isSystemDarkMode: Boolean = false,
    val isWebViewDarkModeSupported: Boolean = false
)

/**
 * 获取当前深色模式状态
 */
@Composable
fun rememberThemeState(): ThemeState {
    val isSystemDarkTheme = isSystemInDarkTheme()
    val isWebViewDarkModeSupported = remember {
        WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)
    }
    
    return remember(isSystemDarkTheme) {
        ThemeState(
            isDarkMode = isSystemDarkTheme,
            isSystemDarkMode = isSystemDarkTheme,
            isWebViewDarkModeSupported = isWebViewDarkModeSupported
        )
    }
}
