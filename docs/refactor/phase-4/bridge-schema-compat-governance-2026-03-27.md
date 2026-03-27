# Phase 4 Bridge Schema / Compatibility Governance

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 4 carried debt 收口`
- 当前结论：`已补齐独立治理工件`

## 目的
- 把 `BridgeFacade` 已落地之后仍散落在测试、宿主页样本和旧 contract catalog 中的兼容规则，收敛为一份独立治理文档。
- 明确哪些内容属于 `Bridge schema / compatibility` 的稳定外部契约，哪些只能作为内部实现自由调整。
- 为 `gap-analysis` 提供“这条不是只有代码实现，也有正式治理工件”的权威落点。

## 适用范围
- Android Native Module 名称与公开 `ReactMethod`
- route / componentName / initialProps 的兼容语义
- Promise payload 字段与错误映射
- `DeviceEventEmitter` 事件名与 payload
- RN Host 首次挂载、主题同步、route 语义的兼容要求

## 契约面一览
| 契约面 | Producer | Consumer | 不允许变化 | 当前守门证据 |
| --- | --- | --- | --- | --- |
| Native module 名称 | `NavigationBridgeModule.kt`, `UserBridgeModule.kt` | `src/utils/bridge/**` | `NavigationBridge` / `UserBridge` 名称 | `__tests__/bridge/NativeBridgeEventContracts.test.ts`, `docs/refactor/phase-2/bridge-contract-catalog.md` |
| 关键 Promise 方法 | `NavigationBridgeModule.kt`, `UserBridgeModule.kt` | `src/utils/bridge/NavigationBridge.ts`, `src/utils/bridge/UserBridge.ts` | 方法名、参数语义、核心返回字段 | `__tests__/bridge/UserBridge.contract.test.ts`, `__tests__/bridge/NavigationBridge.contract.test.ts`, `__tests__/bridge/NativeBridgeEventContracts.test.ts` |
| 事件：`ThemeChanged` | `android/core-ui/src/main/java/com/novel/ui/theme/ThemeManager.kt` | `src/utils/theme/themeStore.ts`, `TimedSwitchPage.tsx` | 事件名、`colorScheme/currentThemeMode/followSystem` | `__tests__/bridge/NativeBridgeEventContracts.test.ts`, `__tests__/smoke/SettingsPage.smoke.test.tsx` |
| 事件：`WritePageSelectionMenuAction` | `NavigationBridgeModule.kt` | `src/page/Writer/WritePage/WritePage.tsx` | 事件名、`action/start/end`，以及 `selectedText` 可选语义 | `__tests__/bridge/NativeBridgeEventContracts.test.ts` |
| route / componentName | `NavigationUtil.kt`, `ReactNativePage.kt` | Compose route graph 与 RN 页面注册 | route 名、RN `componentName`、桥接跳转语义 | `docs/refactor/phase-4/host-risk-run-profile-2026-03-20.md`, `docs/refactor/phase-4/host-risk-run-settings-2026-03-20.md`, `docs/refactor/phase-4/host-risk-run-author-ai-2026-03-20.md` |
| 宿主页主题同步 | `ReactNativePage.kt`, `ReactNativeThemeSyncCoordinator.kt` | RN Host 首帧主题体验 | 优先使用 `ThemeManager` 当前实际主题，不改对外 payload 语义 | `__tests__/bridge/NativeBridgeEventContracts.test.ts`, `docs/refactor/phase-5/host-compat-validation-2026-03-26.md` |

## 当前仓库事实
- `BridgeFacade + delegates` 已是当前宿主页与桥接主路径的事实实现。
- `NavigationBridge`、`UserBridge`、`ThemeChanged`、`WritePageSelectionMenuAction` 仍是当前 RN 侧可见的稳定契约面。
- route 与 RN 组件名仍由 `NavigationUtil.kt` 和 `index.js` / `AppRegistry.registerComponent` 配对守门，不允许单边漂移。
- `Phase 5` reopen 期间对 RN Host 的修复，仍遵守“不改 route / bridge payload / componentName 对外语义”的约束。

## 变更规则
### 允许的内部变化
- `NavigationBridgeModule`、`SettingsBridgeModule` 内部实现继续下沉到 facade / delegate / feature 模块。
- Promise 错误映射可以继续统一到 mapper / coordinator 层。
- 宿主页 attach、theme sync、context-ready 时序可以继续优化，只要对外契约不变。

### 不允许的变化
- 变更 Native module 名称。
- 变更 route 名、RN `componentName`、Bridge 公开方法名。
- 静默增删 Promise payload 核心字段。
- 改写 `ThemeChanged` / `WritePageSelectionMenuAction` 的事件名或必需字段。
- 在未同步 fixture / contract test / control-plane 文档前修改 bridge 契约。

## 审查清单
1. Native producer、RN wrapper、fixture / contract test 是否一起更新。
2. 若改动触及 route / componentName，`NavigationUtil.kt` 与 RN 注册入口是否同步。
3. 若改动触及主题或事件，`ThemeManager`、RN listener、smoke / contract test 是否同步。
4. 若改动触及宿主页挂载时序，是否补充至少一条 `profile / settings / aipage` 样本或对应 addendum。
5. 若改动触及 Promise payload，是否明确兼容策略：
   - 向后兼容新增可选字段
   - 向后不兼容字段删除或重命名必须拆专项，不混入日常优化

## 当前残余 debt
- 当前仍缺自动生成的 bridge schema manifest；契约守门仍以文档 + fixture + contract tests 为主。
- route / componentName 的一致性尚未有脚本化检查，仍依赖 code review 与 smoke 组合。
- `settings / aipage / profile` 之外的 RN Host 页面，还没有同强度的宿主兼容样本归档。

## 关闭结论
- 自 `2026-03-27` 起，`schema / compatibility` 不再只是 `BridgeFacade` 的附带说明，而是有独立治理入口。
- 后续若 `gap-analysis` 再提到这条 debt，应把重点放在“自动化强度不够”而不是“没有治理工件”。

## 主要引用
- `docs/refactor/phase-2/bridge-contract-catalog.md`
- `docs/refactor/phase-4/bridge-facade-delegate-map.md`
- `docs/refactor/phase-4/host-risk-validation-matrix.md`
- `docs/refactor/phase-5/host-compat-validation-2026-03-26.md`
- `__tests__/bridge/UserBridge.contract.test.ts`
- `__tests__/bridge/NavigationBridge.contract.test.ts`
- `__tests__/bridge/NativeBridgeEventContracts.test.ts`
- `__tests__/smoke/SettingsPage.smoke.test.tsx`
