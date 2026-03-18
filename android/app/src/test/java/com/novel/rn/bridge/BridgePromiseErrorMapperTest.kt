package com.novel.rn.bridge

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
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

    @Test
    fun rejectMapped_usesLegacyFormatWhenDisabled() {
        val promise = RecordingPromise()

        promise.rejectMapped(
            throwable = IOException("offline"),
            defaultCode = "SETTING_ERROR",
            defaultMessagePrefix = "设置失败",
            enabled = false
        )

        assertEquals("SETTING_ERROR", promise.code)
        assertEquals("设置失败: offline", promise.message)
    }

    private class RecordingPromise : Promise {
        var code: String? = null
        var message: String? = null

        override fun resolve(value: Any?) = Unit

        override fun reject(code: String, message: String?) {
            this.code = code
            this.message = message
        }

        override fun reject(code: String, throwable: Throwable?) {
            this.code = code
            this.message = throwable?.message
        }

        override fun reject(code: String, message: String?, throwable: Throwable?) {
            this.code = code
            this.message = message
        }

        override fun reject(throwable: Throwable) {
            this.message = throwable?.message
        }

        override fun reject(throwable: Throwable, userInfo: WritableMap) {
            this.message = throwable?.message
        }

        override fun reject(code: String, userInfo: WritableMap) {
            this.code = code
        }

        override fun reject(code: String, throwable: Throwable?, userInfo: WritableMap) {
            this.code = code
            this.message = throwable?.message
        }

        override fun reject(code: String, message: String?, userInfo: WritableMap) {
            this.code = code
            this.message = message
        }

        override fun reject(code: String?, message: String?, throwable: Throwable?, userInfo: WritableMap?) {
            this.code = code
            this.message = message
        }

        override fun reject(message: String) {
            this.message = message
        }
    }
}
