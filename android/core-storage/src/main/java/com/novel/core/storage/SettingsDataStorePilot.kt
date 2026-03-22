package com.novel.core.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context

data class SettingsDataStoreSnapshot(
    val nightMode: String? = null,
    val followSystemTheme: Boolean? = null,
    val autoNightModeEnabled: Boolean? = null,
    val nightStartTime: String? = null,
    val nightEndTime: String? = null,
    val pageFlipEffect: String? = null,
    val fontSize: Int? = null,
    val brightness: Float? = null,
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val isLoggedIn: Boolean? = null,
    val tokenExpiresAt: Long? = null,
    val userId: Int? = null,
    val newsType: Int? = null,
)

@Singleton
class SettingsDataStorePilot @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun writeFollowSystemTheme(value: Boolean) {
        mirror(SettingsDataStoreSnapshot(followSystemTheme = value))
    }

    suspend fun readFollowSystemTheme(): Boolean? =
        dataStore.data.first()[FOLLOW_SYSTEM_THEME_KEY]

    suspend fun writeAutoNightModeEnabled(value: Boolean) {
        mirror(SettingsDataStoreSnapshot(autoNightModeEnabled = value))
    }

    suspend fun readAutoNightModeEnabled(): Boolean? =
        dataStore.data.first()[AUTO_NIGHT_MODE_KEY]

    suspend fun mirror(snapshot: SettingsDataStoreSnapshot) {
        dataStore.edit { preferences ->
            snapshot.nightMode?.let { preferences[NIGHT_MODE_KEY] = it }
            snapshot.followSystemTheme?.let { preferences[FOLLOW_SYSTEM_THEME_KEY] = it }
            snapshot.autoNightModeEnabled?.let { preferences[AUTO_NIGHT_MODE_KEY] = it }
            snapshot.nightStartTime?.let { preferences[NIGHT_START_TIME_KEY] = it }
            snapshot.nightEndTime?.let { preferences[NIGHT_END_TIME_KEY] = it }
            snapshot.pageFlipEffect?.let { preferences[PAGE_FLIP_EFFECT_KEY] = it }
            snapshot.fontSize?.let { preferences[FONT_SIZE_KEY] = it }
            snapshot.brightness?.let { preferences[BRIGHTNESS_KEY] = it }
            snapshot.backgroundColor?.let { preferences[BACKGROUND_COLOR_KEY] = it }
            snapshot.textColor?.let { preferences[TEXT_COLOR_KEY] = it }
            snapshot.isLoggedIn?.let { preferences[IS_LOGGED_IN_KEY] = it }
            snapshot.tokenExpiresAt?.let { preferences[TOKEN_EXPIRES_AT_KEY] = it }
            snapshot.userId?.let { preferences[USER_ID_KEY] = it }
            snapshot.newsType?.let { preferences[NEWS_TYPE_KEY] = it }
        }
    }

    suspend fun clearUserState() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN_KEY)
            preferences.remove(TOKEN_EXPIRES_AT_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }

    suspend fun readSnapshot(): SettingsDataStoreSnapshot {
        val currentPreferences = dataStore.data.first()
        return SettingsDataStoreSnapshot(
            nightMode = currentPreferences[NIGHT_MODE_KEY],
            followSystemTheme = currentPreferences[FOLLOW_SYSTEM_THEME_KEY],
            autoNightModeEnabled = currentPreferences[AUTO_NIGHT_MODE_KEY],
            nightStartTime = currentPreferences[NIGHT_START_TIME_KEY],
            nightEndTime = currentPreferences[NIGHT_END_TIME_KEY],
            pageFlipEffect = currentPreferences[PAGE_FLIP_EFFECT_KEY],
            fontSize = currentPreferences[FONT_SIZE_KEY],
            brightness = currentPreferences[BRIGHTNESS_KEY],
            backgroundColor = currentPreferences[BACKGROUND_COLOR_KEY],
            textColor = currentPreferences[TEXT_COLOR_KEY],
            isLoggedIn = currentPreferences[IS_LOGGED_IN_KEY],
            tokenExpiresAt = currentPreferences[TOKEN_EXPIRES_AT_KEY],
            userId = currentPreferences[USER_ID_KEY],
            newsType = currentPreferences[NEWS_TYPE_KEY],
        )
    }

    suspend fun migrateIfNeeded(
        legacySnapshot: SettingsDataStoreSnapshot
    ): SettingsDataStoreSnapshot {
        val currentPreferences = dataStore.data.first()

        dataStore.edit { preferences ->
            if (!preferences.contains(NIGHT_MODE_KEY)) {
                legacySnapshot.nightMode?.let { preferences[NIGHT_MODE_KEY] = it }
            }
            if (!preferences.contains(FOLLOW_SYSTEM_THEME_KEY)) {
                legacySnapshot.followSystemTheme?.let { preferences[FOLLOW_SYSTEM_THEME_KEY] = it }
            }
            if (!preferences.contains(AUTO_NIGHT_MODE_KEY)) {
                legacySnapshot.autoNightModeEnabled?.let { preferences[AUTO_NIGHT_MODE_KEY] = it }
            }
            if (!preferences.contains(NIGHT_START_TIME_KEY)) {
                legacySnapshot.nightStartTime?.let { preferences[NIGHT_START_TIME_KEY] = it }
            }
            if (!preferences.contains(NIGHT_END_TIME_KEY)) {
                legacySnapshot.nightEndTime?.let { preferences[NIGHT_END_TIME_KEY] = it }
            }
            if (!preferences.contains(PAGE_FLIP_EFFECT_KEY)) {
                legacySnapshot.pageFlipEffect?.let { preferences[PAGE_FLIP_EFFECT_KEY] = it }
            }
            if (!preferences.contains(FONT_SIZE_KEY)) {
                legacySnapshot.fontSize?.let { preferences[FONT_SIZE_KEY] = it }
            }
            if (!preferences.contains(BRIGHTNESS_KEY)) {
                legacySnapshot.brightness?.let { preferences[BRIGHTNESS_KEY] = it }
            }
            if (!preferences.contains(BACKGROUND_COLOR_KEY)) {
                legacySnapshot.backgroundColor?.let { preferences[BACKGROUND_COLOR_KEY] = it }
            }
            if (!preferences.contains(TEXT_COLOR_KEY)) {
                legacySnapshot.textColor?.let { preferences[TEXT_COLOR_KEY] = it }
            }
            if (!preferences.contains(IS_LOGGED_IN_KEY)) {
                legacySnapshot.isLoggedIn?.let { preferences[IS_LOGGED_IN_KEY] = it }
            }
            if (!preferences.contains(TOKEN_EXPIRES_AT_KEY)) {
                legacySnapshot.tokenExpiresAt?.let { preferences[TOKEN_EXPIRES_AT_KEY] = it }
            }
            if (!preferences.contains(USER_ID_KEY)) {
                legacySnapshot.userId?.let { preferences[USER_ID_KEY] = it }
            }
            if (!preferences.contains(NEWS_TYPE_KEY)) {
                legacySnapshot.newsType?.let { preferences[NEWS_TYPE_KEY] = it }
            }
        }

        return readSnapshot()
    }

    companion object {
        private val NIGHT_MODE_KEY = stringPreferencesKey("night_mode")
        private val FOLLOW_SYSTEM_THEME_KEY = booleanPreferencesKey("follow_system_theme")
        private val AUTO_NIGHT_MODE_KEY = booleanPreferencesKey("auto_night_mode")
        private val NIGHT_START_TIME_KEY = stringPreferencesKey("night_start_time")
        private val NIGHT_END_TIME_KEY = stringPreferencesKey("night_end_time")
        private val PAGE_FLIP_EFFECT_KEY = stringPreferencesKey("pageFlipEffect")
        private val FONT_SIZE_KEY = intPreferencesKey("fontSize")
        private val BRIGHTNESS_KEY = floatPreferencesKey("brightness")
        private val BACKGROUND_COLOR_KEY = stringPreferencesKey("backgroundColor")
        private val TEXT_COLOR_KEY = stringPreferencesKey("textColor")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("isLoggedIn")
        private val TOKEN_EXPIRES_AT_KEY = longPreferencesKey("tokenExpiresAt")
        private val USER_ID_KEY = intPreferencesKey("uid")
        private val NEWS_TYPE_KEY = intPreferencesKey("newsType")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object SettingsDataStorePilotModule {

    @Provides
    @Singleton
    fun provideSettingsPilotDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            produceFile = { File(context.filesDir, "settings-pilot.preferences_pb") }
        )
    }
}
