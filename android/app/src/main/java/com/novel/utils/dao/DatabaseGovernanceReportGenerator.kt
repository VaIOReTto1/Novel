package com.novel.utils.dao

data class DatabaseIndexEntry(
    val tableName: String,
    val indexName: String,
    val unique: Boolean,
    val columns: List<String>,
    val origin: String,
)

data class DatabaseFtsEntry(
    val name: String,
    val sql: String,
)

data class DatabaseTriggerEntry(
    val name: String,
    val tableName: String,
    val sql: String,
)

data class DatabaseQueryPlanEntry(
    val id: String,
    val sql: String,
    val details: List<String>,
)

data class DatabaseGovernanceSummary(
    val totalIndexes: Int,
    val ftsTableCount: Int,
    val triggerCount: Int,
    val queryPlanCount: Int,
    val queriesWithTableScan: Int,
)

data class DatabaseGovernanceRecommendation(
    val id: String,
    val severity: String,
    val message: String,
)

data class DatabaseGovernanceReport(
    val indexes: List<DatabaseIndexEntry>,
    val ftsTables: List<DatabaseFtsEntry>,
    val triggers: List<DatabaseTriggerEntry>,
    val queryPlans: List<DatabaseQueryPlanEntry>,
    val summary: DatabaseGovernanceSummary,
    val recommendations: List<DatabaseGovernanceRecommendation>,
)

interface DatabaseGovernanceSource {
    fun listTrackedTables(): List<String>
    fun listIndexes(tableName: String): List<DatabaseIndexEntry>
    fun listFtsTables(): List<DatabaseFtsEntry>
    fun listTriggers(): List<DatabaseTriggerEntry>
    fun explainQueryPlan(sql: String, bindArgs: List<Any?> = emptyList()): List<String>
}

class DatabaseGovernanceReportGenerator(
    private val source: DatabaseGovernanceSource,
) {

    fun generate(): DatabaseGovernanceReport {
        val indexes = source.listTrackedTables()
            .flatMap { tableName -> source.listIndexes(tableName) }
            .sortedWith(compareBy<DatabaseIndexEntry> { it.tableName }.thenBy { it.indexName })

        val queryPlans = defaultQuerySpecs().map { spec ->
            DatabaseQueryPlanEntry(
                id = spec.id,
                sql = spec.sql,
                details = source.explainQueryPlan(spec.sql, spec.bindArgs),
            )
        }

        val ftsTables = source.listFtsTables()
        val triggers = source.listTriggers()
        val summary = buildSummary(
            indexes = indexes,
            ftsTables = ftsTables,
            triggers = triggers,
            queryPlans = queryPlans,
        )

        return DatabaseGovernanceReport(
            indexes = indexes,
            ftsTables = ftsTables,
            triggers = triggers,
            queryPlans = queryPlans,
            summary = summary,
            recommendations = buildRecommendations(
                summary = summary,
                ftsTables = ftsTables,
                triggers = triggers,
                queryPlans = queryPlans,
            ),
        )
    }

    fun toMarkdown(
        report: DatabaseGovernanceReport,
        generatedOn: String,
    ): String {
        return buildString {
            appendLine("# 数据库索引与FTS4治理报告 / Database Governance Report")
            appendLine()
            appendLine("## 摘要 / Summary")
            appendLine("- generated_on: `$generatedOn`")
            appendLine("- total_indexes: `${report.summary.totalIndexes}`")
            appendLine("- fts_table_count: `${report.summary.ftsTableCount}`")
            appendLine("- trigger_count: `${report.summary.triggerCount}`")
            appendLine("- query_plan_count: `${report.summary.queryPlanCount}`")
            appendLine("- queries_with_table_scan: `${report.summary.queriesWithTableScan}`")
            appendLine()
            appendLine("## 当前索引清单")
            appendLine("| 表 | 索引名 | 唯一 | 列 | 来源 |")
            appendLine("| --- | --- | --- | --- | --- |")
            report.indexes.forEach { entry ->
                appendLine(
                    "| ${entry.tableName} | ${entry.indexName} | ${entry.unique} | ${entry.columns.joinToString(", ")} | ${entry.origin} |",
                )
            }
            appendLine()
            appendLine("## FTS4 表")
            report.ftsTables.forEach { entry ->
                appendLine("- `${entry.name}`")
                appendLine("  - `${entry.sql}`")
            }
            appendLine()
            appendLine("## FTS4 触发器")
            report.triggers.forEach { trigger ->
                appendLine("- `${trigger.name}` -> `${trigger.tableName}`")
                appendLine("  - `${trigger.sql}`")
            }
            appendLine()
            appendLine("## 关键查询计划")
            report.queryPlans.forEach { plan ->
                appendLine("### `${plan.id}`")
                appendLine("- sql: `${plan.sql}`")
                plan.details.forEach { detail ->
                    appendLine("- plan: `$detail`")
                }
                appendLine()
            }
            appendLine("## 风险提示 / Recommendations")
            if (report.recommendations.isEmpty()) {
                appendLine("- none")
            } else {
                report.recommendations.forEach { recommendation ->
                    appendLine(
                        "- ${recommendation.severity} `${recommendation.id}`: ${recommendation.message}",
                    )
                }
            }
        }
    }

    private fun buildSummary(
        indexes: List<DatabaseIndexEntry>,
        ftsTables: List<DatabaseFtsEntry>,
        triggers: List<DatabaseTriggerEntry>,
        queryPlans: List<DatabaseQueryPlanEntry>,
    ): DatabaseGovernanceSummary {
        val queriesWithTableScan = queryPlans.count { plan ->
            plan.details.any(::containsTableScan)
        }

        return DatabaseGovernanceSummary(
            totalIndexes = indexes.size,
            ftsTableCount = ftsTables.size,
            triggerCount = triggers.size,
            queryPlanCount = queryPlans.size,
            queriesWithTableScan = queriesWithTableScan,
        )
    }

    private fun buildRecommendations(
        summary: DatabaseGovernanceSummary,
        ftsTables: List<DatabaseFtsEntry>,
        triggers: List<DatabaseTriggerEntry>,
        queryPlans: List<DatabaseQueryPlanEntry>,
    ): List<DatabaseGovernanceRecommendation> {
        val recommendations = mutableListOf<DatabaseGovernanceRecommendation>()

        val scannedPlans = queryPlans.filter { plan ->
            plan.details.any(::containsTableScan)
        }
        if (scannedPlans.isNotEmpty()) {
            recommendations += DatabaseGovernanceRecommendation(
                id = "query-plan-table-scan",
                severity = "risk",
                message = "Table scans still appear in query plans: ${scannedPlans.joinToString { it.id }}",
            )
        }

        if (ftsTables.isEmpty()) {
            recommendations += DatabaseGovernanceRecommendation(
                id = "fts-coverage-missing",
                severity = "risk",
                message = "No FTS coverage was detected in the current governance snapshot.",
            )
        } else if (triggers.isEmpty()) {
            recommendations += DatabaseGovernanceRecommendation(
                id = "fts-trigger-missing",
                severity = "warning",
                message = "FTS tables exist but trigger coverage is missing from the snapshot.",
            )
        }

        if (summary.totalIndexes == 0) {
            recommendations += DatabaseGovernanceRecommendation(
                id = "tracked-index-missing",
                severity = "warning",
                message = "Tracked tables currently expose no secondary indexes in the governance snapshot.",
            )
        }

        return recommendations
    }

    private fun containsTableScan(detail: String): Boolean {
        return TABLE_SCAN_REGEX.containsMatchIn(detail)
    }

    private fun defaultQuerySpecs(): List<QuerySpec> {
        return listOf(
            QuerySpec(
                id = "home_books_by_type",
                sql = "SELECT * FROM home_books WHERE type = ? ORDER BY sortOrder ASC, updateTime DESC",
                bindArgs = listOf("recommend"),
            ),
            QuerySpec(
                id = "home_categories_sorted",
                sql = "SELECT * FROM home_categories ORDER BY sortOrder ASC",
            ),
            QuerySpec(
                id = "user_by_uid",
                sql = "SELECT * FROM users WHERE uid = ?",
                bindArgs = listOf("1"),
            ),
        )
    }

    private data class QuerySpec(
        val id: String,
        val sql: String,
        val bindArgs: List<Any?> = emptyList(),
    )

    private companion object {
        val TABLE_SCAN_REGEX = Regex("""\bSCAN(?:\s+TABLE)?\b""", RegexOption.IGNORE_CASE)
    }
}

class RoomDatabaseGovernanceSource(
    private val database: NovelDatabase,
) : DatabaseGovernanceSource {

    override fun listTrackedTables(): List<String> = listOf(
        "users",
        "home_books",
        "home_banners",
        "home_categories",
    )

    override fun listIndexes(tableName: String): List<DatabaseIndexEntry> {
        val db = database.openHelper.readableDatabase
        val indexEntries = mutableListOf<DatabaseIndexEntry>()
        db.query("PRAGMA index_list(`$tableName`)").useCursor { indexCursor ->
            while (indexCursor.moveToNext()) {
                val indexName = indexCursor.getString(indexCursor.getColumnIndexOrThrow("name"))
                val unique = indexCursor.getInt(indexCursor.getColumnIndexOrThrow("unique")) == 1
                val origin = indexCursor.getString(indexCursor.getColumnIndexOrThrow("origin"))
                val columns = mutableListOf<String>()
                db.query("PRAGMA index_info(`$indexName`)").useCursor { infoCursor ->
                    while (infoCursor.moveToNext()) {
                        columns += infoCursor.getString(infoCursor.getColumnIndexOrThrow("name"))
                    }
                }
                indexEntries += DatabaseIndexEntry(
                    tableName = tableName,
                    indexName = indexName,
                    unique = unique,
                    columns = columns,
                    origin = origin,
                )
            }
        }
        return indexEntries
    }

    override fun listFtsTables(): List<DatabaseFtsEntry> {
        val db = database.openHelper.readableDatabase
        val entries = mutableListOf<DatabaseFtsEntry>()
        db.query(
            "SELECT name, sql FROM sqlite_master WHERE type = 'table' AND sql LIKE 'CREATE VIRTUAL TABLE%' AND name LIKE '%fts%'",
        ).useCursor { cursor ->
            while (cursor.moveToNext()) {
                entries += DatabaseFtsEntry(
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    sql = cursor.getString(cursor.getColumnIndexOrThrow("sql")),
                )
            }
        }
        return entries
    }

    override fun listTriggers(): List<DatabaseTriggerEntry> {
        val db = database.openHelper.readableDatabase
        val entries = mutableListOf<DatabaseTriggerEntry>()
        db.query(
            "SELECT name, tbl_name, sql FROM sqlite_master WHERE type = 'trigger' AND name LIKE 'book_fts_%'",
        ).useCursor { cursor ->
            while (cursor.moveToNext()) {
                entries += DatabaseTriggerEntry(
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    tableName = cursor.getString(cursor.getColumnIndexOrThrow("tbl_name")),
                    sql = cursor.getString(cursor.getColumnIndexOrThrow("sql")),
                )
            }
        }
        return entries
    }

    override fun explainQueryPlan(sql: String, bindArgs: List<Any?>): List<String> {
        val db = database.openHelper.readableDatabase
        val details = mutableListOf<String>()
        db.query("EXPLAIN QUERY PLAN $sql", bindArgs.toTypedArray()).useCursor { cursor ->
            while (cursor.moveToNext()) {
                details += cursor.getString(cursor.getColumnIndexOrThrow("detail"))
            }
        }
        return details
    }
}

private inline fun <T> android.database.Cursor.useCursor(block: (android.database.Cursor) -> T): T {
    return use { block(it) }
}
