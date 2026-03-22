package com.novel.utils.dao

import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseGovernanceReportGeneratorTest {

    @Test
    fun generate_buildsIndexFtsAndQueryPlanSections() {
        val generator = DatabaseGovernanceReportGenerator(
            source = FakeDatabaseGovernanceSource(),
        )

        val report = generator.generate()

        assertEquals(2, report.indexes.size)
        assertEquals("idx_home_book_category_type_sort", report.indexes.first().indexName)
        assertEquals(1, report.ftsTables.size)
        assertEquals("book_fts", report.ftsTables.first().name)
        assertEquals(3, report.triggers.size)
        assertEquals(3, report.queryPlans.size)
        assertEquals("home_books_by_type", report.queryPlans.first().id)
        assertEquals("SEARCH TABLE home_books USING INDEX idx_home_book_category_type_sort", report.queryPlans.first().details.first())
    }

    @Test
    fun toMarkdown_rendersChineseHeadingsAndKeyRows() {
        val generator = DatabaseGovernanceReportGenerator(
            source = FakeDatabaseGovernanceSource(),
        )

        val markdown = generator.toMarkdown(
            report = generator.generate(),
            generatedOn = "2026-03-22",
        )

        assert(markdown.contains("# 数据库索引与FTS4治理报告"))
        assert(markdown.contains("## 当前索引清单"))
        assert(markdown.contains("book_fts_insert"))
        assert(markdown.contains("home_books_by_type"))
    }

    private class FakeDatabaseGovernanceSource : DatabaseGovernanceSource {
        override fun listTrackedTables(): List<String> = listOf("home_books", "users")

        override fun listIndexes(tableName: String): List<DatabaseIndexEntry> {
            return when (tableName) {
                "home_books" -> listOf(
                    DatabaseIndexEntry(
                        tableName = "home_books",
                        indexName = "idx_home_book_category_type_sort",
                        unique = false,
                        columns = listOf("category", "type", "sortOrder"),
                        origin = "c",
                    ),
                )
                "users" -> listOf(
                    DatabaseIndexEntry(
                        tableName = "users",
                        indexName = "idx_user_last_update_time",
                        unique = false,
                        columns = listOf("lastUpdateTime"),
                        origin = "c",
                    ),
                )
                else -> emptyList()
            }
        }

        override fun listFtsTables(): List<DatabaseFtsEntry> {
            return listOf(
                DatabaseFtsEntry(
                    name = "book_fts",
                    sql = "CREATE VIRTUAL TABLE book_fts USING fts4(...)",
                ),
            )
        }

        override fun listTriggers(): List<DatabaseTriggerEntry> {
            return listOf(
                DatabaseTriggerEntry("book_fts_insert", "home_books", "INSERT trigger"),
                DatabaseTriggerEntry("book_fts_update", "home_books", "UPDATE trigger"),
                DatabaseTriggerEntry("book_fts_delete", "home_books", "DELETE trigger"),
            )
        }

        override fun explainQueryPlan(sql: String, bindArgs: List<Any?>): List<String> {
            return when {
                sql.contains("home_books") -> listOf("SEARCH TABLE home_books USING INDEX idx_home_book_category_type_sort")
                sql.contains("home_categories") -> listOf("SCAN TABLE home_categories USING INDEX idx_category_order")
                else -> listOf("SEARCH TABLE users USING PRIMARY KEY")
            }
        }
    }
}
