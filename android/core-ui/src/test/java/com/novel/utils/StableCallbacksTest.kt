package com.novel.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StableCallbacksTest {

    @Test
    fun conditionalCallback_returnsTrueCallbackWhenConditionIsTrue() {
        var value = 0

        val callback = StableCallbacks.conditionalCallback<Int>(
            condition = true,
            trueCallback = { next: Int -> value = next },
            falseCallback = { next: Int -> value = -next },
        )

        callback(7)

        assertThat(value).isEqualTo(7)
    }

    @Test
    fun conditionalCallback_returnsFalseCallbackWhenConditionIsFalse() {
        var value = 0

        val callback = StableCallbacks.conditionalCallback<Int>(
            condition = false,
            trueCallback = { next: Int -> value = next },
            falseCallback = { next: Int -> value = -next },
        )

        callback(9)

        assertThat(value).isEqualTo(-9)
    }
}
