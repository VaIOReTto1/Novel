package com.novel.testing

import com.novel.page.read.service.HistoryItem

class FakeReaderHistorySource(
    private val items: List<HistoryItem> = FixtureCatalog.readerHistoryItems()
) {
    fun getAll(): List<HistoryItem> = items

    fun latest(): HistoryItem = items.first()
}
