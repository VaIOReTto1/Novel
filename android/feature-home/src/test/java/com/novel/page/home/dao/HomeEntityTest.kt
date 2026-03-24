package com.novel.page.home.dao

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeEntityTest {

    @Test
    fun homeBookEntity_preservesOptionalDefaults() {
        val entity = HomeBookEntity(
            id = 1L,
            title = "Book",
            author = "Author",
            coverUrl = "cover",
            description = "desc",
            category = "玄幻",
            isCompleted = false,
            isVip = false,
            updateTime = 1L,
            type = "hot",
        )

        assertThat(entity.categoryId).isEqualTo(0L)
        assertThat(entity.rating).isEqualTo(0.0)
        assertThat(entity.lastChapterName).isNull()
    }
}
