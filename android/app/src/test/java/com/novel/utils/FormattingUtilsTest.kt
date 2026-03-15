package com.novel.utils

import com.google.common.truth.Truth.assertThat
import com.novel.page.book.utils.formatWordCount
import org.junit.Test

class FormattingUtilsTest {

    @Test
    fun `formatWordCount formats large values using chinese units`() {
        assertThat(formatWordCount(15_000)).isEqualTo("1万")
        assertThat(formatWordCount(2_500)).isEqualTo("2千")
        assertThat(formatWordCount(999)).isEqualTo("999")
    }

    @Test
    fun `maskPhoneNumber masks middle digits when number is long enough`() {
        assertThat(maskPhoneNumber("13812345678")).isEqualTo("138****5678")
        assertThat(maskPhoneNumber("1234567")).isEqualTo("1234567")
    }
}
