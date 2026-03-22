package com.novel.page.welfare.component

internal class WelfareWebPerformanceCoordinator {

    private var firstContentfulPaintRecorded = false
    private var timeToInteractiveRecorded = false

    fun resetForNewPageLoad() {
        firstContentfulPaintRecorded = false
        timeToInteractiveRecorded = false
    }

    fun shouldRecordFirstContentfulPaint(): Boolean {
        if (firstContentfulPaintRecorded) {
            return false
        }

        firstContentfulPaintRecorded = true
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
