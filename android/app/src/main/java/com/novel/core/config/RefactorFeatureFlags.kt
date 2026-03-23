package com.novel.core.config

import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NovelUserDefaultsBackedRefactorFeatureFlags @Inject constructor(
    private val userDefaults: NovelUserDefaults,
    private val defaults: RefactorFeatureFlagDefaults = RefactorFeatureFlagDefaults(
        enableBridgeErrorMapper = com.novel.BuildConfig.REFACTOR_ENABLE_BRIDGE_ERROR_MAPPER,
        enableBridgeSharedScopes = com.novel.BuildConfig.REFACTOR_ENABLE_BRIDGE_SHARED_SCOPES,
        enableSettingsDataStorePilot = com.novel.BuildConfig.REFACTOR_ENABLE_SETTINGS_DATASTORE_PILOT
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
