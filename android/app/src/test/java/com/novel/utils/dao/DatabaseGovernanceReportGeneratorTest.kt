package com.novel.utils.dao

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseGovernanceReportGeneratorTest {

    @Test
    fun generate_buildsIndexFtsQueryPlanAndSummarySections() {
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
        assertEquals(
            "SEARCH TABLE home_books USING INDEX idx_home_book_category_type_sort",
            report.queryPlans.first().details.first(),
        )
        assertEquals(3, report.summary.queryPlanCount)
        assertEquals(1, report.summary.queriesWithTableScan)
        assertEquals(1, report.recommendations.size)
    }

    @Test
    fun generate_addsWarningsForMissingFtsCoverageAndTableScans() {
        val generator = DatabaseGovernanceReportGenerator(
            source = RiskyDatabaseGovernanceSource(),
        )

        val report = generator.generate()

        assertTrue(report.recommendations.size >= 2)
        assertTrue(report.recommendations.any { it.id == "query-plan-table-scan" })
        assertTrue(report.recommendations.any { it.id == "fts-coverage-missing" })
    }

    @Test
    fun generate_detectsTableScanWhenEqpOmitsTableKeyword() {
        val generator = DatabaseGovernanceReportGenerator(
            source = EqpVariantDatabaseGovernanceSource(),
        )

        val report = generator.generate()

        assertEquals(3, report.summary.queryPlanCount)
        assertEquals(3, report.summary.queriesWithTableScan)
        assertTrue(report.recommendations.any { it.id == "query-plan-table-scan" })
    }

    @Test
    fun toMarkdown_rendersSummaryAndRecommendationSections() {
        val generator = DatabaseGovernanceReportGenerator(
            source = FakeDatabaseGovernanceSource(),
        )

        val markdown = generator.toMarkdown(
            report = generator.generate(),
            generatedOn = "2026-03-22",
        )

        assertTrue(markdown.contains("#"))
        assertTrue(markdown.contains("book_fts_insert"))
        assertTrue(markdown.contains("home_books_by_type"))
        assertTrue(markdown.contains("queries_with_table_scan"))
        assertTrue(markdown.contains("risk"))
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
                sql.contains("home_books") -> listOf(
                    "SEARCH TABLE home_books USING INDEX idx_home_book_category_type_sort",
                )

                sql.contains("home_categories") -> listOf(
                    "SCAN TABLE home_categories USING INDEX idx_category_order",
                )

                else -> listOf("SEARCH TABLE users USING PRIMARY KEY")
            }
        }
    }

    private class RiskyDatabaseGovernanceSource : DatabaseGovernanceSource {
        override fun listTrackedTables(): List<String> = listOf("home_books")

        override fun listIndexes(tableName: String): List<DatabaseIndexEntry> = emptyList()

        override fun listFtsTables(): List<DatabaseFtsEntry> = emptyList()

        override fun listTriggers(): List<DatabaseTriggerEntry> = emptyList()

        override fun explainQueryPlan(sql: String, bindArgs: List<Any?>): List<String> {
            return listOf("SCAN TABLE home_books")
        }
    }

    private class EqpVariantDatabaseGovernanceSource : DatabaseGovernanceSource {
        override fun listTrackedTables(): List<String> = listOf("home_books")

        override fun listIndexes(tableName: String): List<DatabaseIndexEntry> = emptyList()

        override fun listFtsTables(): List<DatabaseFtsEntry> = emptyList()

        override fun listTriggers(): List<DatabaseTriggerEntry> = emptyList()

        override fun explainQueryPlan(sql: String, bindArgs: List<Any?>): List<String> {
            return listOf("SCAN home_books")
        }
    }
}
