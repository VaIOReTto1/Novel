# Database Governance Sample Output - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 目标：为 `DatabaseGovernanceReportGenerator` 补一份可回链的样例输出与 recommendation 解读。

## 样例来源
- 生成器：
  - `android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt`
- 校验样例：
  - `android/app/src/test/java/com/novel/utils/dao/DatabaseGovernanceReportGeneratorTest.kt`

## 样例快照
### 健康样例
- `total_indexes = 2`
- `fts_table_count = 1`
- `trigger_count = 3`
- `query_plan_count = 3`
- `queries_with_table_scan = 1`
- recommendation:
  - `risk query-plan-table-scan: Table scans still appear in query plans: home_categories_sorted`

### 风险样例
- `total_indexes = 0`
- `fts_table_count = 0`
- `trigger_count = 0`
- `query_plan_count = 3`
- `queries_with_table_scan = 3`
- recommendations:
  - `risk query-plan-table-scan`
  - `risk fts-coverage-missing`
  - `warning tracked-index-missing`

## recommendation 解读
| recommendation | 含义 | 当前用途 |
| --- | --- | --- |
| `query-plan-table-scan` | 当前探针里仍出现全表扫描 | 作为索引收益复盘入口 |
| `fts-coverage-missing` | 当前 snapshot 没有 FTS 覆盖 | 作为全文检索治理入口 |
| `fts-trigger-missing` | 有 FTS 表但缺触发器 | 作为 FTS 同步风险提示 |
| `tracked-index-missing` | tracked tables 没有二级索引 | 作为基础索引治理入口 |

## 当前结论
- 数据库治理不再只有“有生成器”，也已经有了可读的样例输出和 recommendation 解释。
- 当前仍未完成的是“收益量化”，不是“不会输出结果”。
