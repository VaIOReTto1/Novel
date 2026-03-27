package com.novel.page.welfare.component

data class WelfareNavigationPlan(
    val shouldStartPageLoadMonitoring: Boolean,
    val shouldResetPerLoadMarkers: Boolean,
)

class WelfareWebPerformanceCoordinator {

    private var firstContentfulPaintRecorded = false
    private var pageLoadCompleteRecorded = false
    private var timeToInteractiveRecorded = false
    private var lastTrackedUrl: String? = null

    fun createNavigationPlan(currentUrl: String): WelfareNavigationPlan {
        if (currentUrl.isBlank() || currentUrl == lastTrackedUrl) {
            return WelfareNavigationPlan(
                shouldStartPageLoadMonitoring = false,
                shouldResetPerLoadMarkers = false,
            )
        }

        lastTrackedUrl = currentUrl
        resetForNewPageLoad()
        return WelfareNavigationPlan(
            shouldStartPageLoadMonitoring = true,
            shouldResetPerLoadMarkers = true,
        )
    }

    fun resetForNewPageLoad() {
        firstContentfulPaintRecorded = false
        pageLoadCompleteRecorded = false
        timeToInteractiveRecorded = false
    }

    fun shouldRecordFirstContentfulPaint(): Boolean {
        if (firstContentfulPaintRecorded) {
            return false
        }

        firstContentfulPaintRecorded = true
        return true
    }

    fun shouldRecordPageLoadComplete(
        isLoading: Boolean,
        currentUrl: String,
    ): Boolean {
        if (pageLoadCompleteRecorded || isLoading || currentUrl.isBlank()) {
            return false
        }

        pageLoadCompleteRecorded = true
        return true
    }

    fun shouldRecordTimeToInteractive(progress: Int): Boolean {
        if (timeToInteractiveRecorded || progress < 100) {
            return false
        }

        timeToInteractiveRecorded = true
        return true
    }
}
