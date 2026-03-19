package com.novel.rn.bridge.delegate

data class SelectionMenuActionEvent(
    val action: String,
    val selectedText: String?,
    val start: Int,
    val end: Int
)

class SelectionMenuDelegate {

    fun resolveAction(itemId: Int): String? {
        return when (itemId) {
            0xA11001 -> "polish"
            0xA11002 -> "expand"
            0xA11003 -> "condense"
            0xA11004 -> "continue"
            else -> null
        }
    }

    fun buildSelectionEvent(
        action: String,
        selectedText: String?,
        start: Int,
        end: Int
    ): SelectionMenuActionEvent {
        return SelectionMenuActionEvent(
            action = action,
            selectedText = selectedText?.takeIf { it.isNotBlank() },
            start = start,
            end = end
        )
    }
}
