package com.novel.page.read.service.settings

import com.novel.core.config.RefactorFeatureFlags
import java.util.Locale
import com.novel.core.storage.SettingsDataStoreMirrorCoordinator
import com.novel.core.storage.SettingsDataStorePilot
import com.novel.core.storage.StorageFacade
import com.novel.page.read.viewmodel.ReaderSettings
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReaderSettingsStorage @Inject constructor(
    private val storageFacade: StorageFacade,
    private val settingsDataStorePilot: SettingsDataStorePilot,
    private val refactorFeatureFlags: RefactorFeatureFlags,
) {

    private val mirrorCoordinator = SettingsDataStoreMirrorCoordinator()
    private val mirrorScope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))
    private val mirrorMutex = Mutex()

    fun setPageFlipEffect(value: String) {
        storageFacade.putString(NovelUserDefaultsKey.PAGE_FLIP_EFFECT, value)
    }

    fun getPageFlipEffect(): String? = storageFacade.getString(NovelUserDefaultsKey.PAGE_FLIP_EFFECT)

    fun setFontSize(value: Int) {
        storageFacade.putInt(NovelUserDefaultsKey.FONT_SIZE, value)
    }

    fun getFontSize(): Int? = storageFacade.getInt(NovelUserDefaultsKey.FONT_SIZE)

    fun setBrightness(value: Float) {
        storageFacade.putFloat(NovelUserDefaultsKey.BRIGHTNESS, value)
    }

    fun getBrightness(): Float? = storageFacade.getFloat(NovelUserDefaultsKey.BRIGHTNESS)

    fun setBackgroundColor(value: String) {
        storageFacade.putString(NovelUserDefaultsKey.BACKGROUND_COLOR, value)
    }

    fun getBackgroundColor(): String? = storageFacade.getString(NovelUserDefaultsKey.BACKGROUND_COLOR)

    fun setTextColor(value: String) {
        storageFacade.putString(NovelUserDefaultsKey.TEXT_COLOR, value)
    }

    fun getTextColor(): String? = storageFacade.getString(NovelUserDefaultsKey.TEXT_COLOR)

    fun mirrorSettings(settings: ReaderSettings) {
        mirrorReaderSettings(
            mirrorCoordinator.createReaderSnapshot(
                pageFlipEffect = settings.pageFlipEffect.name,
                fontSize = settings.fontSize,
                brightness = settings.brightness,
                backgroundColor = String.format(Locale.US, "#%08X", settings.backgroundColor.toArgb()),
                textColor = String.format(Locale.US, "#%08X", settings.textColor.toArgb()),
            ),
        )
    }

    private fun mirrorReaderSettings(snapshot: com.novel.core.storage.SettingsDataStoreSnapshot) {
        mirrorScope.launch {
            mirrorMutex.withLock {
                mirrorCoordinator.mirrorIfEnabled(
                    isEnabled = refactorFeatureFlags.enableSettingsDataStorePilot(),
                    snapshot = snapshot,
                    mirror = settingsDataStorePilot::mirror,
                )
            }
        }
    }
}
