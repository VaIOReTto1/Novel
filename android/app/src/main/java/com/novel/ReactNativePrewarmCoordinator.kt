package com.novel

internal class ReactNativePrewarmCoordinator {

    private var hasPrewarmed = false

    fun shouldPrewarmAfterFirstFrame(): Boolean {
        if (hasPrewarmed) {
            return false
        }

        hasPrewarmed = true
        return true
    }
}
