package com.novel

internal class MainApplicationStartupLifecycleReporter(
    private val onFirstActivityCreate: () -> Unit,
    private val onFirstFrameDrawn: () -> Unit,
    private val onAppFullyLoaded: () -> Unit,
    private val afterFirstFrame: () -> Unit,
) {

    fun markFirstActivityCreate() {
        onFirstActivityCreate()
    }

    fun markFirstFrameDrawn() {
        onFirstFrameDrawn()
        afterFirstFrame()
    }

    fun markAppFullyLoaded() {
        onAppFullyLoaded()
    }
}
