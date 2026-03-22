package com.novel.core.storage

class SettingsDataStoreMirrorCoordinator {

    fun createSettingsSnapshot(
        nightMode: String,
        followSystemTheme: Boolean,
        autoNightModeEnabled: Boolean,
        nightStartTime: String,
        nightEndTime: String,
    ): SettingsDataStoreSnapshot {
        return SettingsDataStoreSnapshot(
            nightMode = nightMode,
            followSystemTheme = followSystemTheme,
            autoNightModeEnabled = autoNightModeEnabled,
            nightStartTime = nightStartTime,
            nightEndTime = nightEndTime,
        )
    }

    fun createReaderSnapshot(
        pageFlipEffect: String? = null,
        fontSize: Int? = null,
        brightness: Float? = null,
        backgroundColor: String? = null,
        textColor: String? = null,
    ): SettingsDataStoreSnapshot {
        return SettingsDataStoreSnapshot(
            pageFlipEffect = pageFlipEffect,
            fontSize = fontSize,
            brightness = brightness,
            backgroundColor = backgroundColor,
            textColor = textColor,
        )
    }

    fun createUserSessionSnapshot(
        isLoggedIn: Boolean,
        tokenExpiresAt: Long? = null,
        userId: Int? = null,
        newsType: Int? = null,
    ): SettingsDataStoreSnapshot {
        return SettingsDataStoreSnapshot(
            isLoggedIn = isLoggedIn,
            tokenExpiresAt = tokenExpiresAt,
            userId = userId,
            newsType = newsType,
        )
    }

    suspend fun mirrorIfEnabled(
        isEnabled: Boolean,
        snapshot: SettingsDataStoreSnapshot,
        mirror: suspend (SettingsDataStoreSnapshot) -> Unit,
    ) {
        if (isEnabled) {
            mirror(snapshot)
        }
    }

    suspend fun clearUserStateIfEnabled(
        isEnabled: Boolean,
        clearUserState: suspend () -> Unit,
    ) {
        if (isEnabled) {
            clearUserState()
        }
    }
}
