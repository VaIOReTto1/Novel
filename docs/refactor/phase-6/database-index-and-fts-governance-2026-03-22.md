# 数据库索引与 FTS4 治理报告

## 摘要
- 首次建立日期：`2026-03-22`
- 最近更新：`2026-03-27`
- 目标：为 `NovelDatabase` 维持一份可持续演进的治理输出，固定：
  - 当前索引清单
  - FTS4 表与触发器
  - 关键查询计划
  - 风险提示与摘要结论
- 当前口径：
  - 治理入口已形成
  - 风险提示能力已形成
  - 索引收益与 FTS4 最优性复盘仍未完成

## 工具入口
- 运行时代码入口：
  - [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt)
- 关键输入：
  - [DatabaseModule.kt](/d:/program/Novel/android/app/src/main/java/com/novel/di/DatabaseModule.kt)
  - [NovelDatabase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt)

## 当前治理输出
- 当前索引清单
- FTS4 表与触发器清单
- 关键 `EXPLAIN QUERY PLAN` 探针
- 摘要字段：
  - `total_indexes`
  - `fts_table_count`
  - `trigger_count`
  - `query_plan_count`
  - `queries_with_table_scan`
- 风险提示：
  - `query-plan-table-scan`
  - `fts-coverage-missing`
  - `fts-trigger-missing`
  - `tracked-index-missing`

## 当前判断
- 已完成：
  - 数据库治理报告生成器落地
  - 显式索引 / FTS4 / 触发器 / 关键查询探针固定成统一入口
  - 风险提示与摘要结论能力落地
- 未完成：
  - 索引收益的定量复盘
  - `FTS4` 与其他方案的横向比较
  - 真机 / 本地运行时 `EXPLAIN QUERY PLAN` 结果的长期归档

## 残余风险
- 当前输出更偏“治理入口 + 风险识别”，不是最终收益证明。
- 若未来表结构调整，需要同步校验索引、FTS4 与触发器兼容性。
