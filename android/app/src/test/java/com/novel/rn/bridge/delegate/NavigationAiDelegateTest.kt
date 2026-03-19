package com.novel.rn.bridge.delegate

import com.novel.utils.network.api.author.ai.AiService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationAiDelegateTest {

    private val delegate = NavigationAiDelegate()

    @Test
    fun buildResult_returnsSuccessWhenResponseOk() {
        val result = delegate.buildResult(
            response = AiService.AiResponse(
                code = "0",
                message = "ok",
                data = "rewritten",
                ok = true
            ),
            defaultErrorCode = "AI_POLISH_ERROR"
        )

        assertTrue(result is NavigationAiResult.Success)
        assertEquals("rewritten", (result as NavigationAiResult.Success).data)
    }

    @Test
    fun buildResult_returnsFailureWhenResponseNotOk() {
        val result = delegate.buildResult(
            response = AiService.AiResponse(
                code = "1",
                message = "bad request",
                data = null,
                ok = false
            ),
            defaultErrorCode = "AI_EXPAND_ERROR"
        )

        assertTrue(result is NavigationAiResult.Failure)
        assertEquals("AI_EXPAND_ERROR", (result as NavigationAiResult.Failure).errorCode)
        assertEquals("bad request", result.errorMessage)
    }
}
