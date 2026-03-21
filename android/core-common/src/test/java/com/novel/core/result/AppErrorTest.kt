package com.novel.core.result

import com.google.gson.JsonParseException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class AppErrorTest {

    @Test
    fun fromThrowable_mapsTimeoutsToTimeoutError() {
        val socketTimeout = AppError.fromThrowable(SocketTimeoutException("slow network"))
        val genericTimeout = AppError.fromThrowable(TimeoutException("timeout"))

        assertTrue(socketTimeout is AppError.Timeout)
        assertTrue(genericTimeout is AppError.Timeout)
    }

    @Test
    fun fromThrowable_mapsIoToNetworkError() {
        val result = AppError.fromThrowable(IOException("offline"))

        assertTrue(result is AppError.Network)
        assertEquals("offline", result.message)
    }

    @Test
    fun fromThrowable_mapsJsonParseToSerializationError() {
        val result = AppError.fromThrowable(JsonParseException("bad json"))

        assertTrue(result is AppError.Serialization)
        assertEquals("bad json", result.message)
    }

    @Test
    fun fromThrowable_mapsUnknownExceptionToUnexpectedError() {
        val result = AppError.fromThrowable(IllegalStateException("boom"))

        assertTrue(result is AppError.Unexpected)
        assertEquals("boom", result.message)
    }

    @Test
    fun dataResult_mapTransformsSuccessValue() {
        val result: DataResult<Int> = DataResult.Success(2)

        val mapped = result.map { it * 3 }

        assertTrue(mapped is DataResult.Success)
        assertEquals(6, (mapped as DataResult.Success).value)
    }

    @Test
    fun dataResult_mapKeepsFailureUntouched() {
        val failure: DataResult<Int> = DataResult.Failure(AppError.Network("offline"))

        val mapped = failure.map { it * 3 }

        assertTrue(mapped is DataResult.Failure)
        assertTrue((mapped as DataResult.Failure).error is AppError.Network)
    }
}
