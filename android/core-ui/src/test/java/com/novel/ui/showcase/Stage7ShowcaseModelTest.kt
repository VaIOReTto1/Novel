package com.novel.ui.showcase

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class Stage7ShowcaseModelTest {

    @Test
    fun `sections expose foundations icons and media`() {
        val sections = stage7ShowcaseSections()

        assertThat(sections.map { it.title }).containsExactly(
            "Foundations",
            "Icons",
            "Media",
        )
        assertThat(sections.first().description).contains("tokens")
    }
}
