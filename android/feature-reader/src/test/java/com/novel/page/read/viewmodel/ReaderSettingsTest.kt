package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReaderSettingsTest {

    @Test
    fun defaultSettings_usePageCurlAndStandardFontSize() {
        val defaults = ReaderSettings.getDefault()

        assertThat(defaults.fontSize).isEqualTo(16)
        assertThat(defaults.pageFlipEffect).isEqualTo(PageFlipEffect.PAGECURL)
    }
}
