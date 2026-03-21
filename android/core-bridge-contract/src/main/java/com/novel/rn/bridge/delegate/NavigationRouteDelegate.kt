package com.novel.rn.bridge.delegate

class NavigationRouteDelegate(
    private val navigateToRoute: (String) -> Unit
) {

    fun navigateToTimedSwitch() {
        navigateToRoute("timed_switch")
    }

    fun navigateToHelpSupport() {
        navigateToRoute("help_support")
    }

    fun navigateToPrivacyPolicy() {
        navigateToRoute("privacy_policy")
    }

    fun navigateToHistory() {
        navigateToRoute("history")
    }

    fun navigateToMessage() {
        navigateToRoute("message")
    }
}
