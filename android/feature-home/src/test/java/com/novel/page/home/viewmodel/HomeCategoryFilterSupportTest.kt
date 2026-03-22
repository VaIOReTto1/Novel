package com.novel.page.home.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import org.junit.Test

class HomeCategoryFilterSupportTest {

    @Test
    fun normalizeSelectedFilter_rewritesLegacyRecommendToHome() {
        assertThat(HomeCategoryFilterSupport.normalizeSelectedFilter("推荐"))
            .isEqualTo("首页")
    }

    @Test
    fun isHomeFilter_acceptsHomeAndLegacyRecommend() {
        assertThat(HomeCategoryFilterSupport.isHomeFilter("首页")).isTrue()
        assertThat(HomeCategoryFilterSupport.isHomeFilter("推荐")).isTrue()
        assertThat(HomeCategoryFilterSupport.isHomeFilter("玄幻")).isFalse()
    }

    @Test
    fun normalizeFilters_replacesLegacyHomeEntryAndDeduplicates() {
        val normalized = HomeCategoryFilterSupport.normalizeFilters(
            persistentListOf(
                CategoryInfo("0", "推荐"),
                CategoryInfo("1", "玄幻"),
                CategoryInfo("0", "首页"),
                CategoryInfo("2", "都市"),
            ),
        )

        assertThat(normalized).containsExactly(
            CategoryInfo("0", "首页"),
            CategoryInfo("1", "玄幻"),
            CategoryInfo("2", "都市"),
        ).inOrder()
    }

    @Test
    fun normalizeFilters_prependsHomeWhenMissing() {
        val normalized = HomeCategoryFilterSupport.normalizeFilters(
            persistentListOf(
                CategoryInfo("1", "玄幻"),
                CategoryInfo("2", "都市"),
            ),
        )

        assertThat(normalized.first()).isEqualTo(CategoryInfo("0", "首页"))
    }
}
