package com.novel.ui.showcase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NovelDesignShowcaseModelTest {

    @Test
    fun `sections expose foundations icons and media`() {
        val sections = novelDesignShowcaseSections()

        assertThat(sections.map { it.title }).containsExactly(
            "Foundations",
            "Icons",
            "Media",
        )
        assertThat(sections.first().description).contains("tokens")
    }
}
