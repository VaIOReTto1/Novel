package com.novel.page.welfare.component

import android.content.res.Configuration
import android.webkit.WebView
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
import com.novel.core.logging.CoreLogger

@Composable
fun WelfareThemeAdapter(
    webView: WebView?,
    onThemeChanged: (Boolean) -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val isSystemDarkTheme = isSystemInDarkTheme()
    var currentDarkMode by remember { mutableStateOf(isSystemDarkTheme) }

    LaunchedEffect(configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        val isDarkMode =
            (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (currentDarkMode != isDarkMode) {
            currentDarkMode = isDarkMode
            updateWebViewDarkMode(webView, isDarkMode)
            onThemeChanged(isDarkMode)
            CoreLogger.d("WelfareThemeAdapter", "深色模式状态变更: $isDarkMode")
        }
    }

    LaunchedEffect(webView) {
        webView?.let { updateWebViewDarkMode(it, currentDarkMode) }
    }
}

fun updateWebViewDarkMode(webView: WebView?, isDarkMode: Boolean) {
    webView?.let { view ->
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                view.settings.isAlgorithmicDarkeningAllowed = isDarkMode
                CoreLogger.d("WelfareThemeAdapter", "WebView新API深色模式已更新: $isDarkMode")
            } else if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val forceDarkMode = if (isDarkMode) {
                    WebSettingsCompat.FORCE_DARK_ON
                } else {
                    WebSettingsCompat.FORCE_DARK_OFF
                }
                WebSettingsCompat.setForceDark(view.settings, forceDarkMode)
                CoreLogger.d("WelfareThemeAdapter", "WebView旧API深色模式已更新: $isDarkMode")
            }

            if (isDarkMode) {
                injectDarkModeCSS(view)
            } else {
                removeDarkModeCSS(view)
            }

            CoreLogger.d("WelfareThemeAdapter", "WebView深色模式完整更新: $isDarkMode")
        } catch (e: Exception) {
            CoreLogger.e("WelfareThemeAdapter", "更新WebView深色模式失败", e)
        }
    }
}

private fun injectDarkModeCSS(webView: WebView) {
    val darkModeCss = """
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

    webView.evaluateJavascript(darkModeCss, null)
}

private fun removeDarkModeCSS(webView: WebView) {
    val removeCss = """
        javascript:(function() {
            var style = document.getElementById('welfare-dark-mode-style');
            if (style) {
                style.remove();
            }
        })()
    """.trimIndent()

    webView.evaluateJavascript(removeCss, null)
}

data class ThemeState(
    val isDarkMode: Boolean = false,
    val isSystemDarkMode: Boolean = false,
    val isWebViewDarkModeSupported: Boolean = false,
)

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
            isWebViewDarkModeSupported = isWebViewDarkModeSupported,
        )
    }
}
