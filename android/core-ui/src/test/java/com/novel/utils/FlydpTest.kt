package com.novel.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FlydpTest {

    @Test
    fun wdp_appliesGlobalScaleX() {
        val originalScale = globalScaleX
        try {
            globalScaleX = 1.5f

            assertThat(10.wdp.value).isEqualTo(15f)
        } finally {
            globalScaleX = originalScale
        }
    }

    @Test
    fun ssp_appliesGlobalScaleX() {
        val originalScale = globalScaleX
        try {
            globalScaleX = 2f

            assertThat(12.ssp.value).isEqualTo(24f)
        } finally {
            globalScaleX = originalScale
        }
    }
}
