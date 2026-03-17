package com.novel.page.read.service.settings

import com.novel.core.storage.StorageFacade
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReaderSettingsStorage @Inject constructor(
    private val storageFacade: StorageFacade
) {

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
}
