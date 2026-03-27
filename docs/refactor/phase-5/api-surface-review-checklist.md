# Phase 5 API Surface Review Checklist

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 5 reopen closeout 后治理补齐`
- 当前结论：`已形成 API surface 审查清单`

## 何时必须使用
- 新增或修改 `public` / `internal` 给跨模块调用的 Kotlin 类型、函数、接口。
- 变更 Hilt 入口、gateway 接口、feature 对 `app` 的依赖形态。
- 变更 route、RN `componentName`、Bridge 方法名、Promise payload、事件 payload。
- 变更 `api` / `implementation` 依赖边界、resources 可见性、manifest / entrypoint 暴露方式。

## 硬停止项
- 任何会改变 route、bridge payload、RN 组件名、持久化 schema 对外语义的改动，不能伪装成普通优化或模块内重构。
- 任何把稳定 feature 逻辑重新塞回 `:app` 的改动，默认拒绝。
- 任何把业务语义塞进 `:core-common / :core-ui / :core-network / :core-bridge*` 的改动，默认拒绝。

## 审查清单
1. 这个改动是否真的需要暴露成跨模块 API，而不是模块内实现细节。
2. 对外暴露的类型是否只包含调用方需要知道的最小字段和最小能力。
3. 如果变更触达 bridge / route / RN host，是否同步检查：
   - `NavigationUtil.kt`
   - `src/utils/bridge/**`
   - `__tests__/bridge/**`
   - `docs/refactor/phase-4/bridge-schema-compat-governance-2026-03-27.md`
4. 如果变更触达 `ThemeManager`、`ReactNativePage`、`ReactNativeHostPathTraceCoordinator`，是否补充 host 兼容样本或 device evidence。
5. 新增依赖是否放在正确层级：
   - feature 间不得无理由互相直连
   - `app` 不应重新成为功能实现主仓
   - `api` 依赖必须有明确下游消费者
6. 新增资源、manifest 配置或 entrypoint 时，是否会扩大模块外可见面。
7. 新增接口是否具备最小验证：
   - JVM 单测 / Jest contract test / smoke test / device 样本 至少一种
8. 若是治理报告或辅助工具输出，是否清楚标明：
   - generated / script-owned
   - human-maintained
9. 文档是否同步更新到对应控制面：
   - `docs/refactor/README.md`
   - phase 文档
   - addendum / evidence
10. 若评审意见在同一类接口上重复出现两次以上，是否应升级成文档或检查脚本。

## 最小交付要求
### Bridge / RN Host 变更
- `__tests__/bridge/**` 中对应 contract test
- 必要时补 `__tests__/smoke/SettingsPage.smoke.test.tsx`
- 若影响宿主页路径或主题同步，再补 device evidence

### Android feature / core 模块变更
- 对应 `:module:testDebugUnitTest`
- 触达 `:app` 或共享边界时，再补 `app:testDebugUnitTest`

### 构建或依赖边界变更
- `app:lintDebug`
- `app:compileDebugAndroidTestKotlin`
- 必要时补 `:macrobenchmark:assemble`

## 当前结论
- 自 `2026-03-27` 起，`Phase 5` 的 API surface 审查不再只是 reopen closeout 里的隐含要求，而是有单独 checklist。
- 后续若 `gap-analysis` 继续把这条记为 debt，重点应是“自动化还不够”而不是“没有审查规则”。

## 主要引用
- `docs/refactor/phase-5/module-owner-matrix-2026-03-27.md`
- `docs/refactor/phase-4/bridge-schema-compat-governance-2026-03-27.md`
- `docs/refactor/phase-5/module-graph-current-state.md`
- `docs/refactor/phase-5/phase-5-closeout-assessment.md`
