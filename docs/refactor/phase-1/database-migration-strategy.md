# Phase 1 - 数据库迁移策略

## 目标
- 为 `V1-05` 提供正式的数据库发布策略说明。
- 明确当前版本、发布行为、debug 与 release 的差异，以及后续版本升级要求。

## 当前数据库基线
- 数据库类：`com.novel.utils.dao.NovelDatabase`
- 当前版本：`4`
- schema 文件：
  - `android/app/schemas/com.novel.utils.dao.NovelDatabase/4.json`

## 当前行为

### Debug
- 允许 `fallbackToDestructiveMigration(true)`
- 目标：
  - 降低本地调试与快速试验成本
  - 不阻塞开发者频繁重装和 schema 迭代

### Release
- 不允许默认 destructive migration
- 目标：
  - 发布路径必须依赖显式迁移策略或明确升级计划
  - 避免用户数据被无提示清空

## 迁移原则
- 每次数据库版本升级都必须：
  - 导出新 schema
  - 保留旧 schema
  - 在变更说明中说明表结构影响
  - 评估是否需要显式 `Migration`
- 若无法提供显式迁移：
  - 不得直接走正式发布
  - 必须先在迁移演练矩阵中给出明确处置方案

## 当前表结构覆盖
- `users`
- `home_books`
- `home_banners`
- `home_categories`

## 后续版本演进要求
- `version 4 -> 5` 及以后：
  - 新增/删除字段必须先更新 schema
  - 影响主路径的字段变更必须先补演练样本
  - 迁移失败策略必须可观测

## 发布前检查项
- `exportSchema = true`
- `schemas/.../<version>.json` 已生成
- release 路径未默认 destructive migration
- 如有版本变化，已有迁移说明
- 如有高风险变更，已在迁移演练矩阵中排期

## 当前结论
- 当前代码已经实现了：
  - schema 导出
  - debug-only destructive migration
  - release 禁止默认 destructive migration
- 下一步重点从“策略存在”转向“演练落地”。
