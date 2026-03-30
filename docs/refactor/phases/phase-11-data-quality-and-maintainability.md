# Phase 11 - 数据质量与可维护性

## 目标
- 收掉仍会反复返工的 mock、fallback、空态、命名、目录、状态模型和错误文案问题。
- 让 Stage 5 的最后一段不是“再加一个大包”，而是把前两段沉淀成长期可维护规则。

## 范围
- 剩余生产 mock 退场与 backlog 分层
- fallback 可观测与异常空态统一
- 命名规范、目录规范、状态模型
- 错误文案与用户提示统一

## 非目标
- 不在本阶段大规模接真实数据源
- 不把所有 RN heavy pages 一次性清零
- 不修改已稳定的 route / bridge 契约

## 当前仓库入口基线
- 生产 mock 退出治理已有：
  - `mock-inventory-report.md`
  - `production-mock-exit-governance-2026-03-27.md`
- fallback / 空态散落在：
  - `UserBridge.ts`
  - `NetworkCacheManager`
  - `CachedBookRepository`
  - `SearchRankingRepository`
- 命名、目录、状态模型、错误文案尚无单一宿主指南

## 协作编制
### Leader Mode
- `single leader / three helpers`

### Base Helper Count
- `3`

### Agent Roster
- `DataQualityExitAgent`
- `EmptyStateCatalogAgent`
- `MaintainabilityGuideAgent`

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P11.1 | 固定剩余生产 mock 退出 backlog | heavy pages 与 data-source debt 分层明确 |
| P11.2 | 固定 fallback / 异常空态目录 | 可观测与 fail-closed 语义明确 |
| P11.3 | 固定命名 / 目录 / 状态模型指南 | 长期维护入口统一 |
| P11.4 | 固定错误文案与用户提示目录 | 用户可见文本不再继续发散 |
| P11.5 | 输出 Phase 11 与 Stage 5 closeout 宿主 | Stage 5 闭环完成 |

## 交付物
- `error-empty-state-catalog-2026-03-30.md`
- `naming-directory-state-model-guide-2026-03-30.md`
- Phase 11 closeout 评估

## 当前状态
- `planned`
