package com.novel.page.read.service.common

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OptimizedDispatcherProviderTest {

    @Test
    fun createsDistinctIoAndMainDispatchers() {
        val provider = OptimizedDispatcherProvider()

        assertThat(provider.io).isNotSameInstanceAs(provider.main)
        assertThat(provider.default).isNotNull()
    }
}
