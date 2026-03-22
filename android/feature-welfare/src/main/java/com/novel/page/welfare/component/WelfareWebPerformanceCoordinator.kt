package com.novel.page.welfare.component

class WelfareWebPerformanceCoordinator {

    private var firstContentfulPaintRecorded = false
    private var pageLoadCompleteRecorded = false
    private var timeToInteractiveRecorded = false

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
