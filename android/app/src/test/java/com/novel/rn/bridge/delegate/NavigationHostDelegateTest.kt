package com.novel.rn.bridge.delegate

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationHostDelegateTest {

    @Test
    fun registerComponent_delegatesToBridgeHostSink() {
        val recorder = HostRecorder()
        val delegate = NavigationHostDelegate(
            registerComponent = { recorder.registered += it },
            notifyRouteChanged = { recorder.routes += it },
            clearComponentCache = { component ->
                recorder.cleared += component
                NavigationHostResult.Success("ok")
            },
            clearAllComponentCache = {
                recorder.clearAllCalls += 1
                NavigationHostResult.Success("all")
            }
        )

        delegate.registerComponent("SettingsPageComponent")

        assertEquals(listOf("SettingsPageComponent"), recorder.registered)
    }

    @Test
    fun notifyRouteChanged_delegatesToBridgeHostSink() {
        val recorder = HostRecorder()
        val delegate = NavigationHostDelegate(
            registerComponent = { recorder.registered += it },
            notifyRouteChanged = { recorder.routes += it },
            clearComponentCache = { component ->
                recorder.cleared += component
                NavigationHostResult.Success("ok")
            },
            clearAllComponentCache = {
                recorder.clearAllCalls += 1
                NavigationHostResult.Success("all")
            }
        )

        delegate.notifyRouteChanged("settings")

        assertEquals(listOf("settings"), recorder.routes)
    }

    @Test
    fun clearComponentCache_returnsSuccessMessage() {
        val recorder = HostRecorder()
        val delegate = NavigationHostDelegate(
            registerComponent = { recorder.registered += it },
            notifyRouteChanged = { recorder.routes += it },
            clearComponentCache = { component ->
                recorder.cleared += component
                NavigationHostResult.Success("cleared-$component")
            },
            clearAllComponentCache = {
                recorder.clearAllCalls += 1
                NavigationHostResult.Success("all")
            }
        )

        val result = delegate.clearComponentCache("SettingsPageComponent")

        assertEquals("cleared-SettingsPageComponent", (result as NavigationHostResult.Success).message)
        assertEquals(listOf("SettingsPageComponent"), recorder.cleared)
    }

    @Test
    fun clearAllComponentCache_returnsSuccessMessage() {
        val recorder = HostRecorder()
        val delegate = NavigationHostDelegate(
            registerComponent = { recorder.registered += it },
            notifyRouteChanged = { recorder.routes += it },
            clearComponentCache = { component ->
                recorder.cleared += component
                NavigationHostResult.Success("ok")
            },
            clearAllComponentCache = {
                recorder.clearAllCalls += 1
                NavigationHostResult.Success("all")
            }
        )

        val result = delegate.clearAllComponentCache()

        assertEquals("all", (result as NavigationHostResult.Success).message)
        assertEquals(1, recorder.clearAllCalls)
    }

    private class HostRecorder {
        val registered = mutableListOf<String>()
        val routes = mutableListOf<String>()
        val cleared = mutableListOf<String>()
        var clearAllCalls: Int = 0
    }
}
