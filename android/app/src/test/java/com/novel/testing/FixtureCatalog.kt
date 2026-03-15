package com.novel.testing

import com.novel.page.read.service.HistoryItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

object FixtureCatalog {
    private val json = Json { ignoreUnknownKeys = true }

    fun readerHistoryItems(): List<HistoryItem> {
        return json.decodeFromString(FixtureLoader.loadText("fixtures/reader_history_sample.json"))
    }

    fun userDefaultsSnapshot(): UserDefaultsSnapshot {
        return json.decodeFromString(FixtureLoader.loadText("fixtures/user_defaults_sample.json"))
    }

    fun homeBooksSnapshot(): HomeBooksSnapshot {
        return json.decodeFromString(FixtureLoader.loadText("fixtures/home_books_sample.json"))
    }
}

@Serializable
data class UserDefaultsSnapshot(
    val fontSize: Int,
    val brightness: Double,
    val backgroundColor: String,
    val textColor: String,
    val pageFlipEffect: String,
    val isFollowSystemTheme: Boolean,
    val nightModeStartTime: String,
    val nightModeEndTime: String
)

@Serializable
data class HomeBooksSnapshot(
    val recommendations: List<HomeBookFixture>
)

@Serializable
data class HomeBookFixture(
    val id: Long,
    val title: String,
    val author: String,
    val category: String,
    val coverUrl: String,
    val wordCount: Int,
    val readCount: Int
)
