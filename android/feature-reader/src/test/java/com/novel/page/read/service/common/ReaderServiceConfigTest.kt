package com.novel.page.read.service.common

import com.google.common.truth.Truth.assertThat
import com.novel.page.read.viewmodel.PageFlipEffect
import org.junit.Test

class ReaderServiceConfigTest {

    @Test
    fun defaultPageFlipEffect_matchesReaderEnum() {
        assertThat(ReaderServiceConfig.DEFAULT_PAGE_FLIP_EFFECT).isEqualTo(PageFlipEffect.PAGECURL)
        assertThat(ReaderServiceConfig.MAX_SESSION_CACHE_SIZE).isEqualTo(12)
    }
}
