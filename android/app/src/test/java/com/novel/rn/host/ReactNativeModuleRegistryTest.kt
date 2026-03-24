package com.novel.rn.host

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactNativeModuleRegistryTest {

    @Test
    fun `createModules preserves factory order`() {
        val created = mutableListOf<String>()
        val registry = ReactNativeModuleRegistry(
            moduleFactories = listOf(
                { created += "settings"; "settings" },
                { created += "navigation"; "navigation" },
                { created += "user"; "user" },
            ),
        )

        val modules = registry.createModules()

        assertThat(modules).containsExactly("settings", "navigation", "user").inOrder()
        assertThat(created).containsExactly("settings", "navigation", "user").inOrder()
    }
}
