package com.novel.core.config

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
