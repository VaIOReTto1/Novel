package com.novel.rn

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.react.ReactInstanceManager
import com.facebook.react.bridge.ReactApplicationContext
import com.novel.core.logging.CoreLogger
import com.novel.ui.theme.NovelColors

enum class MviModuleType {
    SETTINGS,
    BRIDGE,
    BOTH,
}

@SuppressLint("VisibleForTests")
@Composable
fun ReactNativePageContent(
    componentName: String,
    destroyOnBack: Boolean,
    reactInstanceManager: ReactInstanceManager,
    rootView: View,
    isContextReady: Boolean,
    onContextReadyChanged: (Boolean) -> Unit,
    onReactContextReady: (ReactApplicationContext) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val tag = "ReactNativePage"

    if (destroyOnBack) {
        BackHandler(enabled = true) {
            CoreLogger.d(tag, "BackHandler触发 for $componentName, 准备销毁缓存并返回")
            onNavigateBack()
        }
    }

    DisposableEffect(reactInstanceManager, componentName, isContextReady) {
        CoreLogger.d(tag, "DisposableEffect启动 for $componentName")

        val contextListener = if (!isContextReady) {
            CoreLogger.d(tag, "添加RN上下文监听器 for $componentName")
            ReactInstanceManager.ReactInstanceEventListener { reactCtx ->
                CoreLogger.d(tag, "RN上下文状态变更为就绪 for $componentName")
                onContextReadyChanged(true)
                onReactContextReady(reactCtx as ReactApplicationContext)
            }.also { listener ->
                reactInstanceManager.addReactInstanceEventListener(listener)
            }
        } else {
            reactInstanceManager.currentReactContext?.let {
                onReactContextReady(it as ReactApplicationContext)
            }
            null
        }

        onDispose {
            contextListener?.let { listener ->
                CoreLogger.d(tag, "移除RN上下文监听器，防止内存泄漏 for $componentName")
                reactInstanceManager.removeReactInstanceEventListener(listener)
            }
        }
    }

    AndroidView(
        factory = {
            CoreLogger.d(tag, "AndroidView factory返回缓存的ReactRootView for $componentName")
            rootView
        },
        modifier = Modifier
            .fillMaxSize()
            .background(NovelColors.NovelBackground),
    )

    if (!isContextReady) {
        CoreLogger.d(tag, "显示加载指示器 for $componentName")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }
}
