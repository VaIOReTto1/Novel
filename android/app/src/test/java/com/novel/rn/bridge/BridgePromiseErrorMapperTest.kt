package com.novel.rn.bridge

import com.novel.core.result.AppError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class BridgePromiseErrorMapperTest {

    @Test
    fun map_mapsTimeoutToTimeoutCodeAndMessage() {
        val result = BridgePromiseErrorMapper.map(
            throwable = SocketTimeoutException("slow"),
            defaultCode = "SETTING_ERROR",
            defaultMessagePrefix = "设置失败",
            timeoutMessage = "设置操作超时"
        )

        assertEquals("TIMEOUT_ERROR", result.code)
        assertEquals("设置操作超时", result.message)
        assertTrue(result.appError is AppError.Timeout)
    }

    @Test
    fun map_mapsNetworkErrorToDefaultCodeWithPrefix() {
        val result = BridgePromiseErrorMapper.map(
            throwable = IOException("offline"),
            defaultCode = "SETTING_ERROR",
            defaultMessagePrefix = "设置失败"
        )

        assertEquals("SETTING_ERROR", result.code)
        assertEquals("设置失败: offline", result.message)
        assertTrue(result.appError is AppError.Network)
    }

    @Test
    fun map_fallsBackToGenericMessageWhenThrowableHasNoMessage() {
        val result = BridgePromiseErrorMapper.map(
            throwable = IllegalStateException(),
            defaultCode = "CHECK_ERROR",
            defaultMessagePrefix = "检查失败"
        )

        assertEquals("CHECK_ERROR", result.code)
        assertEquals("检查失败: 未知错误", result.message)
        assertTrue(result.appError is AppError.Unexpected)
    }
}
