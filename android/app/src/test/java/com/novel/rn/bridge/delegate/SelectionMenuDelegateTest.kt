package com.novel.rn.bridge.delegate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SelectionMenuDelegateTest {

    @Test
    fun resolveAction_mapsKnownMenuIds() {
        val delegate = SelectionMenuDelegate()

        assertEquals("polish", delegate.resolveAction(0xA11001))
        assertEquals("expand", delegate.resolveAction(0xA11002))
        assertEquals("condense", delegate.resolveAction(0xA11003))
        assertEquals("continue", delegate.resolveAction(0xA11004))
    }

    @Test
    fun resolveAction_returnsNullForUnknownMenuId() {
        val delegate = SelectionMenuDelegate()

        assertNull(delegate.resolveAction(123))
    }

    @Test
    fun buildSelectionEvent_keepsSelectedTextWhenPresent() {
        val delegate = SelectionMenuDelegate()

        val event = delegate.buildSelectionEvent(
            action = "expand",
            selectedText = "hello",
            start = 1,
            end = 5
        )

        assertEquals("expand", event.action)
        assertEquals("hello", event.selectedText)
        assertEquals(1, event.start)
        assertEquals(5, event.end)
    }

    @Test
    fun buildSelectionEvent_clearsBlankSelectedText() {
        val delegate = SelectionMenuDelegate()

        val event = delegate.buildSelectionEvent(
            action = "polish",
            selectedText = "   ",
            start = 0,
            end = 0
        )

        assertNull(event.selectedText)
        assertEquals(0, event.start)
        assertEquals(0, event.end)
    }
}
