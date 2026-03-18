package com.novel.core.config

import com.novel.BuildConfig
import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import javax.inject.Inject
import javax.inject.Singleton

data class RefactorFeatureFlagDefaults(
    val enableBridgeErrorMapper: Boolean,
    val enableBridgeSharedScopes: Boolean,
    val enableSettingsDataStorePilot: Boolean
)

interface RefactorFeatureFlags {
    fun enableBridgeErrorMapper(): Boolean
    fun enableBridgeSharedScopes(): Boolean
    fun enableSettingsDataStorePilot(): Boolean
}

@Singleton
class NovelUserDefaultsBackedRefactorFeatureFlags @Inject constructor(
    private val userDefaults: NovelUserDefaults,
    private val defaults: RefactorFeatureFlagDefaults = RefactorFeatureFlagDefaults(
        enableBridgeErrorMapper = BuildConfig.REFACTOR_ENABLE_BRIDGE_ERROR_MAPPER,
        enableBridgeSharedScopes = BuildConfig.REFACTOR_ENABLE_BRIDGE_SHARED_SCOPES,
        enableSettingsDataStorePilot = BuildConfig.REFACTOR_ENABLE_SETTINGS_DATASTORE_PILOT
    )
) : RefactorFeatureFlags {

    override fun enableBridgeErrorMapper(): Boolean =
        readBooleanOverride("refactor_bridge_error_mapper_enabled") ?: defaults.enableBridgeErrorMapper

    override fun enableBridgeSharedScopes(): Boolean =
        readBooleanOverride("refactor_bridge_shared_scopes_enabled") ?: defaults.enableBridgeSharedScopes

    override fun enableSettingsDataStorePilot(): Boolean =
        readBooleanOverride("refactor_settings_datastore_pilot_enabled") ?: defaults.enableSettingsDataStorePilot

    private fun readBooleanOverride(key: String): Boolean? =
        userDefaults.getString(key)?.toBooleanStrictOrNull()
}
