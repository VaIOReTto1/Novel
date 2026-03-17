package com.novel.rn.settings

import com.novel.core.storage.StorageFacade
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsPreferenceStorage @Inject constructor(
    private val storageFacade: StorageFacade
) {

    fun setNightMode(mode: String) {
        storageFacade.putString(PREF_NIGHT_MODE, mode)
    }

    fun getNightMode(): String = storageFacade.getString(PREF_NIGHT_MODE) ?: DEFAULT_NIGHT_MODE

    fun setFollowSystemTheme(follow: Boolean) {
        storageFacade.putString(PREF_FOLLOW_SYSTEM, follow.toString())
    }

    fun isFollowSystemTheme(): Boolean =
        storageFacade.getString(PREF_FOLLOW_SYSTEM)?.toBoolean() ?: DEFAULT_FOLLOW_SYSTEM

    fun setAutoNightModeEnabled(enabled: Boolean) {
        storageFacade.putString(PREF_AUTO_NIGHT_MODE, enabled.toString())
    }

    fun isAutoNightModeEnabled(): Boolean =
        storageFacade.getString(PREF_AUTO_NIGHT_MODE)?.toBoolean() ?: DEFAULT_AUTO_NIGHT_MODE

    fun setNightModeTime(startTime: String, endTime: String) {
        storageFacade.putString(PREF_NIGHT_START_TIME, startTime)
        storageFacade.putString(PREF_NIGHT_END_TIME, endTime)
    }

    fun getNightModeStartTime(): String =
        storageFacade.getString(PREF_NIGHT_START_TIME) ?: DEFAULT_NIGHT_START_TIME

    fun getNightModeEndTime(): String =
        storageFacade.getString(PREF_NIGHT_END_TIME) ?: DEFAULT_NIGHT_END_TIME

    companion object {
        const val PREF_NIGHT_MODE = "night_mode"
        const val PREF_AUTO_NIGHT_MODE = "auto_night_mode"
        const val PREF_FOLLOW_SYSTEM = "follow_system_theme"
        const val PREF_NIGHT_START_TIME = "night_start_time"
        const val PREF_NIGHT_END_TIME = "night_end_time"

        private const val DEFAULT_NIGHT_MODE = "auto"
        private const val DEFAULT_AUTO_NIGHT_MODE = false
        private const val DEFAULT_FOLLOW_SYSTEM = true
        private const val DEFAULT_NIGHT_START_TIME = "22:00"
        private const val DEFAULT_NIGHT_END_TIME = "06:00"
    }
}
