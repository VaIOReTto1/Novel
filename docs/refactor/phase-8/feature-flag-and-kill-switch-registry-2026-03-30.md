# Phase 8 Feature Flag And Kill Switch Registry

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 8`
- 当前结论：`已固定当前仓库内的 feature flag / kill switch registry`

## 当前注册表
| Flag | Default | Override Key | Current Host | Primary Consumers | Purpose |
| --- | --- | --- | --- | --- | --- |
| `enableBridgeErrorMapper` | `true` | `refactor_bridge_error_mapper_enabled` | `RefactorFeatureFlags` / `NovelUserDefaultsBackedRefactorFeatureFlags` | `NavigationBridgeModule`, `UserBridgeModule`, `SettingsBridgeModule` | 控制 Bridge Promise 错误是否统一映射到 `AppError` 语义 |
| `enableBridgeSharedScopes` | `true` | `refactor_bridge_shared_scopes_enabled` | `RefactorFeatureFlags` | 当前作为共享 scope 能力开关保留 | 控制 Bridge 共享 coroutine scope 路径 |
| `enableSettingsDataStorePilot` | `false` | `refactor_settings_datastore_pilot_enabled` | `RefactorFeatureFlags` | `SettingsUtils`, `ReaderSettingsStorage`, `AuthService` | 控制 Settings / Reader 侧 DataStore 试点是否生效 |

## 当前来源
- 接口定义：
  - `android/core-common/src/main/java/com/novel/core/config/RefactorFeatureFlags.kt`
- 默认值与 override：
  - `android/core-storage/src/main/java/com/novel/core/config/NovelUserDefaultsBackedRefactorFeatureFlags.kt`
- Hilt 提供入口：
  - `android/app/src/main/java/com/novel/di/NovelUserDefaultsModule.kt`
- BuildConfig 默认值：
  - `android/app/build.gradle`
  - `android/feature-rn-host/build.gradle`

## 当前结论
- 当前仓库已经有本地 runtime flag 机制，但仍属于“repo-local override”，不是远程配置平台。
- 当前 kill switch 主要覆盖的是：
  - Bridge 错误映射
  - Bridge 共享 scopes
  - Settings DataStore 试点
- 当前不应把不存在的远程灰度开关伪装成已具备能力。

## 使用规则
1. 任何新 flag 都必须补充：
   - 默认值
   - override key
   - owner
   - consumer
   - rollback 目的
2. 若功能没有明确回退价值，不要为了“看起来现代”新增 flag。
3. 若一个 flag 被两个以上模块消费，必须在本注册表中登记。
4. 若一个问题已经能通过 `git revert + rollback-index` 快速回退，不必强行加 runtime flag。

## 当前 owner
- 默认 owner：`当前重构实施者`
- 若后续引入模块 owner 细分，以：
  - `module-owner-matrix-2026-03-27.md`
  - `rollback-index.md`
  - `decision-log.md`
  联合判定

## 主要引用
- `docs/refactor/phases/phase-8-observability-rollout-governance.md`
- `docs/refactor/tracking/rollback-index.md`
- `docs/refactor/phase-5/module-owner-matrix-2026-03-27.md`
