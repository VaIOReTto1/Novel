package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PageFlipEffectTest {

    @Test
    fun pageCurl_displayName_isStable() {
        assertThat(PageFlipEffect.PAGECURL.displayName).isEqualTo("书卷")
        assertThat(PageFlipEffect.VERTICAL.displayName).isEqualTo("上下")
    }
}
