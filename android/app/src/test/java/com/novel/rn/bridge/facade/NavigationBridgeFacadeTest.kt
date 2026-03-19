package com.novel.rn.bridge.facade

import com.novel.rn.bridge.BridgeIntent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NavigationBridgeFacadeTest {

    @Test
    fun goToLogin_usesBridgeIntentWhenBridgeSinkAvailable() {
        val recorder = Recorder()
        val facade = DefaultNavigationBridgeFacade(
            bridgeIntentSink = { recorder.lastIntent = it },
            navigateToRoute = { recorder.lastRoute = it },
            navigateBack = { recorder.backCalls += 1 },
            clearComponentCache = { recorder.clearedComponent = it }
        )

        facade.goToLogin()

        assertEquals(BridgeIntent.NavigateToLogin, recorder.lastIntent)
        assertNull(recorder.lastRoute)
        assertEquals(0, recorder.backCalls)
    }

    @Test
    fun navigateToSettings_fallsBackToDirectRouteWhenBridgeSinkMissing() {
        val recorder = Recorder()
        val facade = DefaultNavigationBridgeFacade(
            bridgeIntentSink = null,
            navigateToRoute = { recorder.lastRoute = it },
            navigateBack = { recorder.backCalls += 1 },
            clearComponentCache = { recorder.clearedComponent = it }
        )

        facade.navigateToSettings()

        assertEquals("settings", recorder.lastRoute)
        assertNull(recorder.lastIntent)
        assertEquals(0, recorder.backCalls)
    }

    @Test
    fun navigateBack_usesBridgeIntentWhenBridgeSinkAvailable() {
        val recorder = Recorder()
        val facade = DefaultNavigationBridgeFacade(
            bridgeIntentSink = { recorder.lastIntent = it },
            navigateToRoute = { recorder.lastRoute = it },
            navigateBack = { recorder.backCalls += 1 },
            clearComponentCache = { recorder.clearedComponent = it }
        )

        facade.navigateBack("SettingsPageComponent")

        assertEquals(
            BridgeIntent.NavigateBack("SettingsPageComponent"),
            recorder.lastIntent
        )
        assertNull(recorder.clearedComponent)
        assertEquals(0, recorder.backCalls)
    }

    @Test
    fun navigateBack_clearsCacheAndFallsBackWhenBridgeSinkMissing() {
        val recorder = Recorder()
        val facade = DefaultNavigationBridgeFacade(
            bridgeIntentSink = null,
            navigateToRoute = { recorder.lastRoute = it },
            navigateBack = { recorder.backCalls += 1 },
            clearComponentCache = { recorder.clearedComponent = it }
        )

        facade.navigateBack("SettingsPageComponent")

        assertEquals("SettingsPageComponent", recorder.clearedComponent)
        assertEquals(1, recorder.backCalls)
        assertNull(recorder.lastIntent)
    }

    private class Recorder {
        var lastIntent: BridgeIntent? = null
        var lastRoute: String? = null
        var clearedComponent: String? = null
        var backCalls: Int = 0
    }
}
