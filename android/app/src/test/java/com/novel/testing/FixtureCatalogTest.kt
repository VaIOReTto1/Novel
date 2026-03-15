package com.novel.testing

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FixtureCatalogTest {

    @Test
    fun `reader history fixture is reproducible`() {
        val items = FixtureCatalog.readerHistoryItems()

        assertThat(items).hasSize(2)
        assertThat(items.first().bookId).isEqualTo("1334318497132552192")
        assertThat(items.first().chapterTitle).isEqualTo("第二章 先付款可好？")
    }

    @Test
    fun `user defaults fixture exposes stable reading settings`() {
        val snapshot = FixtureCatalog.userDefaultsSnapshot()

        assertThat(snapshot.fontSize).isEqualTo(16)
        assertThat(snapshot.pageFlipEffect).isEqualTo("PAGECURL")
        assertThat(snapshot.backgroundColor).isEqualTo("#FFF5F5DC")
    }

    @Test
    fun `home books fixture exposes deterministic recommendation set`() {
        val snapshot = FixtureCatalog.homeBooksSnapshot()

        assertThat(snapshot.recommendations).hasSize(2)
        assertThat(snapshot.recommendations.map { it.title }).containsExactly(
            "我反夺舍了诸天大佬",
            "我有家诸天最强当铺"
        )
    }

    @Test
    fun `fake reader history source returns latest record from fixture`() {
        val source = FakeReaderHistorySource()

        assertThat(source.latest().title).isEqualTo("元尊")
        assertThat(source.getAll().last().author).isEqualTo("银霜骑士")
    }
}
