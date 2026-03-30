# Error And Empty State Catalog

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 11`
- 当前结论：`已固定错误态 / 空态 / fallback 宿主目录`

## 当前重点域
| Domain | Current Anchor | Current Fact |
| --- | --- | --- |
| User bridge | `src/utils/bridge/UserBridge.ts` | 已从 mock user fallback 收口到 fail-closed 返回 `null/false/0` |
| Search / Home / Cache | `NetworkCacheManager`, `CachedBookRepository`, `SearchRankingRepository` | fallback 分散存在，仍缺统一可观测规则 |
| Heavy RN pages | `mock-inventory-report.md`, `production-mock-exit-governance-2026-03-27.md` | 剩余 heavy pages 已有 carried debt 清单 |
| Reader / History | Reader / history services | 恢复与空态入口仍缺统一 catalog |

## 收口目标
- 统一空态
- 统一错误态
- 统一 fallback 可观测字段
- 明确哪些场景允许 fail-closed，哪些必须给用户提示
