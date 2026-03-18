package com.novel.core.config

import com.novel.utils.Store.UserDefaults.NovelUserDefaults
import com.novel.utils.Store.UserDefaults.NovelUserDefaultsKey
import org.junit.Assert.assertEquals
import org.junit.Test

class RefactorFeatureFlagsTest {

    @Test
    fun returnsBuildConfigDefaultsWhenNoOverrideExists() {
        val flags = NovelUserDefaultsBackedRefactorFeatureFlags(
            userDefaults = FakeNovelUserDefaults(),
            defaults = RefactorFeatureFlagDefaults(
                enableBridgeErrorMapper = true,
                enableBridgeSharedScopes = true,
                enableSettingsDataStorePilot = false
            )
        )

        assertEquals(true, flags.enableBridgeErrorMapper())
        assertEquals(true, flags.enableBridgeSharedScopes())
        assertEquals(false, flags.enableSettingsDataStorePilot())
    }

    @Test
    fun usesStringOverridesWhenPresent() {
        val userDefaults = FakeNovelUserDefaults().apply {
            setString("refactor_bridge_error_mapper_enabled", "false")
            setString("refactor_settings_datastore_pilot_enabled", "true")
        }
        val flags = NovelUserDefaultsBackedRefactorFeatureFlags(
            userDefaults = userDefaults,
            defaults = RefactorFeatureFlagDefaults(
                enableBridgeErrorMapper = true,
                enableBridgeSharedScopes = true,
                enableSettingsDataStorePilot = false
            )
        )

        assertEquals(false, flags.enableBridgeErrorMapper())
        assertEquals(true, flags.enableSettingsDataStorePilot())
    }

    private class FakeNovelUserDefaults : NovelUserDefaults {
        private val enumStore = mutableMapOf<NovelUserDefaultsKey, Any>()
        private val stringStore = mutableMapOf<String, String>()

        override fun <T> set(value: T, forKey: NovelUserDefaultsKey) {
            enumStore[forKey] = value as Any
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: NovelUserDefaultsKey): T? = enumStore[key] as? T

        override fun remove(key: NovelUserDefaultsKey) {
            enumStore.remove(key)
        }

        override fun contains(key: NovelUserDefaultsKey): Boolean = enumStore.containsKey(key)

        override fun clearAll() {
            enumStore.clear()
            stringStore.clear()
        }

        override fun setString(key: String, value: String) {
            stringStore[key] = value
        }

        override fun getString(key: String): String? = stringStore[key]

        override fun remove(key: String) {
            stringStore.remove(key)
        }
    }
}
