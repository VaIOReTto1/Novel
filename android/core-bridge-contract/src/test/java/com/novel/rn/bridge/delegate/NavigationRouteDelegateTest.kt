package com.novel.rn.bridge.delegate

import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationRouteDelegateTest {

    @Test
    fun navigateToTimedSwitch_usesExpectedRoute() {
        val recorder = RouteRecorder()
        val delegate = NavigationRouteDelegate { route -> recorder.routes += route }

        delegate.navigateToTimedSwitch()

        assertEquals(listOf("timed_switch"), recorder.routes)
    }

    @Test
    fun navigateToHelpSupport_usesExpectedRoute() {
        val recorder = RouteRecorder()
        val delegate = NavigationRouteDelegate { route -> recorder.routes += route }

        delegate.navigateToHelpSupport()

        assertEquals(listOf("help_support"), recorder.routes)
    }

    @Test
    fun navigateToPrivacyPolicy_usesExpectedRoute() {
        val recorder = RouteRecorder()
        val delegate = NavigationRouteDelegate { route -> recorder.routes += route }

        delegate.navigateToPrivacyPolicy()

        assertEquals(listOf("privacy_policy"), recorder.routes)
    }

    @Test
    fun navigateToHistoryAndMessage_keepExistingRouteNames() {
        val recorder = RouteRecorder()
        val delegate = NavigationRouteDelegate { route -> recorder.routes += route }

        delegate.navigateToHistory()
        delegate.navigateToMessage()

        assertEquals(listOf("history", "message"), recorder.routes)
    }

    private class RouteRecorder {
        val routes = mutableListOf<String>()
    }
}
