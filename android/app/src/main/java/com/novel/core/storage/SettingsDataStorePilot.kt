package com.novel.core.storage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    val followSystemTheme: Boolean?,
    val autoNightModeEnabled: Boolean?
)

@Singleton
class SettingsDataStorePilot @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    suspend fun writeFollowSystemTheme(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[FOLLOW_SYSTEM_THEME_KEY] = value
        }
    }

    suspend fun readFollowSystemTheme(): Boolean? =
        dataStore.data.first()[FOLLOW_SYSTEM_THEME_KEY]

    suspend fun writeAutoNightModeEnabled(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_NIGHT_MODE_KEY] = value
        }
    }

    suspend fun readAutoNightModeEnabled(): Boolean? =
        dataStore.data.first()[AUTO_NIGHT_MODE_KEY]

    suspend fun migrateIfNeeded(
        legacyFollowSystemTheme: String?,
        legacyAutoNightMode: String?
    ): SettingsDataStoreSnapshot {
        val currentPreferences = dataStore.data.first()
        val hasFollowSystemTheme = currentPreferences.contains(FOLLOW_SYSTEM_THEME_KEY)
        val hasAutoNightMode = currentPreferences.contains(AUTO_NIGHT_MODE_KEY)

        if (hasFollowSystemTheme && hasAutoNightMode) {
            return SettingsDataStoreSnapshot(
                followSystemTheme = currentPreferences[FOLLOW_SYSTEM_THEME_KEY],
                autoNightModeEnabled = currentPreferences[AUTO_NIGHT_MODE_KEY]
            )
        }

        dataStore.edit { preferences ->
            if (!preferences.contains(FOLLOW_SYSTEM_THEME_KEY)) {
                legacyFollowSystemTheme?.toBooleanStrictOrNull()?.let { value ->
                    preferences[FOLLOW_SYSTEM_THEME_KEY] = value
                }
            }
            if (!preferences.contains(AUTO_NIGHT_MODE_KEY)) {
                legacyAutoNightMode?.toBooleanStrictOrNull()?.let { value ->
                    preferences[AUTO_NIGHT_MODE_KEY] = value
                }
            }
        }

        return SettingsDataStoreSnapshot(
            followSystemTheme = readFollowSystemTheme(),
            autoNightModeEnabled = readAutoNightModeEnabled()
        )
    }

    companion object {
        private val FOLLOW_SYSTEM_THEME_KEY = booleanPreferencesKey("follow_system_theme")
        private val AUTO_NIGHT_MODE_KEY = booleanPreferencesKey("auto_night_mode")
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
