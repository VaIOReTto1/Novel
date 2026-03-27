package com.novel

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Stable
import androidx.lifecycle.lifecycleScope
import com.novel.debug.RuntimeDebugScenarioStore
import com.novel.page.component.ImageLoaderService
import com.novel.ui.theme.ThemeManager
import com.novel.utils.TimberLogger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@Stable
@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "ComposeMainActivity"
    }

    @Stable
    @Inject
    lateinit var imageLoaderService: ImageLoaderService

    @Stable
    private val reactInstanceManager: com.facebook.react.ReactInstanceManager?
        get() = mainApplication.reactNativeHost.reactInstanceManager

    private val firstFrameCoordinator = ComposeMainActivityFirstFrameCoordinator()

    private val mainApplication: MainApplication
        get() = application as MainApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as? MainApplication)?.markFirstActivityCreate()
        super.onCreate(savedInstanceState)
        TimberLogger.d(TAG, "Activity created")
        applyDebugRuntimeScenarios()

        setContent {
            ComposeMainActivityContent(
                imageLoaderService = imageLoaderService,
                debugRoute = debugRoute(),
            )
        }

        window.decorView.post {
            lifecycleScope.launch {
                firstFrameCoordinator.onFirstFrameRendered(
                    application = application as? MainApplication,
                    reactInstanceManager = reactInstanceManager,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        TimberLogger.d(TAG, "Activity resumed")
        reactInstanceManager?.onHostResume(this)
    }

    override fun onPause() {
        super.onPause()
        TimberLogger.d(TAG, "Activity paused")
        reactInstanceManager?.onHostPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        TimberLogger.d(TAG, "Activity destroyed")
        reactInstanceManager?.onHostDestroy(this)
        mainApplication.clearAllReactRootViewCache()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        TimberLogger.d(TAG, "Configuration changed")

        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> TimberLogger.d(TAG, "System switched to dark mode")
            Configuration.UI_MODE_NIGHT_NO -> TimberLogger.d(TAG, "System switched to light mode")
        }

        runCatching {
            ThemeManager.getInstance(this).notifySystemThemeChanged()
        }.onFailure { error ->
            TimberLogger.e(TAG, "Failed to notify system theme change", error)
        }
    }

    private fun debugRoute(): String? {
        return if (BuildConfig.DEBUG) {
            intent?.getStringExtra("debug_route")
        } else {
            null
        }
    }

    private fun applyDebugRuntimeScenarios() {
        if (!BuildConfig.DEBUG) {
            RuntimeDebugScenarioStore.updateSearchPageSizeOverride(null)
            return
        }

        RuntimeDebugScenarioStore.updateSearchPageSizeOverride(
            intent?.getStringExtra("debug_search_page_size")?.toIntOrNull(),
        )
        RuntimeDebugScenarioStore.updateReaderAutoFlipDirection(
            intent?.getStringExtra("debug_reader_auto_flip"),
        )
    }
}
