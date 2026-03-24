package com.novel.page.read.viewmodel

import com.google.common.truth.Truth.assertThat
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Test

class ReaderStateAdapterTest {

    @Test
    fun getChapterIndex_returnsMatchingIndex() {
        val adapter = MutableStateFlow(
            ReaderState(
                chapterList = persistentListOf(
                    Chapter(id = "c1", chapterName = "第一章"),
                    Chapter(id = "c2", chapterName = "第二章"),
                ),
            ),
        ).asReaderAdapter()

        assertThat(adapter.getChapterIndex("c2")).isEqualTo(1)
    }

    @Test
    fun formatReadingTime_formatsHoursAndMinutes() {
        val adapter = MutableStateFlow(ReaderState()).asReaderAdapter()

        assertThat(adapter.formatReadingTime(90 * 60 * 1000L)).isEqualTo("1小时30分钟")
    }
}
