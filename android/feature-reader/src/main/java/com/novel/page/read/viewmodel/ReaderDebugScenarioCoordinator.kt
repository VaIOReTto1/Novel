package com.novel.page.read.viewmodel

data class ReaderAutoFlipPlan(
    val shouldTriggerAutoFlip: Boolean,
    val direction: FlipDirection?,
)

class ReaderDebugScenarioCoordinator {

    private var hasTriggeredAutoFlip = false

    fun resolveAutoFlipDirection(rawDirection: String?): FlipDirection? {
        return when (rawDirection?.trim()?.lowercase()) {
            "next" -> FlipDirection.NEXT
            "previous", "prev" -> FlipDirection.PREVIOUS
            else -> null
        }
    }

    fun createAutoFlipPlan(
        isInitialized: Boolean,
        hasCurrentPage: Boolean,
        direction: FlipDirection?,
    ): ReaderAutoFlipPlan {
        if (!isInitialized || !hasCurrentPage || direction == null || hasTriggeredAutoFlip) {
            return ReaderAutoFlipPlan(
                shouldTriggerAutoFlip = false,
                direction = null,
            )
        }

        hasTriggeredAutoFlip = true
        return ReaderAutoFlipPlan(
            shouldTriggerAutoFlip = true,
            direction = direction,
        )
    }
}
