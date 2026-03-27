# Phase 5 Module Owner Matrix

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 5 reopen closeout 后治理补齐`
- 当前结论：`已形成模块 owner 矩阵`

## 使用规则
- 本矩阵描述的是“默认 owner 面”和“变更进入哪个评审面”，不是额外开新组织结构。
- 若仓库未单独指派模块负责人，默认 owner 仍是：`当前重构实施者`。
- 任何跨 `app / core-* / feature-*` 的边界变更，都必须先按本矩阵识别主 owner 面，再决定验证入口。

## 模块 Owner 矩阵
| Module | 默认 Owner | Owner 面 | 主要职责 | 重点评审项 | 最小验证 |
| --- | --- | --- | --- | --- | --- |
| `:app` | `当前重构实施者` | Android host / composition root | `Application`、`Activity`、route wrapper、RN module adapter、Hilt glue | 是否重新变胖、是否侵入 feature 逻辑、route 语义是否漂移 | `app:testDebugUnitTest`, `app:lintDebug`, `app:compileDebugAndroidTestKotlin` |
| `:core-common` | `当前重构实施者` | runtime primitives | MVI / result / concurrency / logging 基础件 | 通用抽象是否泄漏业务语义 | `:core-common:testDebugUnitTest` |
| `:core-ui` | `当前重构实施者` | theme / shared UI | 主题、通用 Compose 组件、RN 主题桥接上游 | 主题事件、共享组件 API 是否兼容 | `:core-ui:testDebugUnitTest`, RN contract / smoke 相关回归 |
| `:core-bridge` | `当前重构实施者` | bridge runtime | bridge facade、gateway、coroutine scope、共享 host adapter | 对外 bridge 语义是否变更、错误映射是否一致 | `:core-bridge:testDebugUnitTest`, `__tests__/bridge/**` |
| `:core-bridge-contract` | `当前重构实施者` | bridge contract | delegate / helper / contract 纯契约层 | 是否引入 route / payload 漂移 | `:core-bridge-contract:testDebugUnitTest`, `__tests__/bridge/**` |
| `:core-storage` | `当前重构实施者` | storage governance | `StorageFacade`、DataStore、兼容存储层 | 是否新增业务层直连存储、迁移语义是否可回退 | `:core-storage:testDebugUnitTest` |
| `:core-network` | `当前重构实施者` | network governance | `NetworkFacade`、executor、interceptor、legacy adapter | 是否重新分叉主网络通路、trace / request id 是否保真 | `:core-network:testDebugUnitTest`, `:app:testDebugUnitTest` 相关网络用例 |
| `:feature-home` | `当前重构实施者` | Home feature | 首页状态机、restore / paging 协调、首页 gateway | 是否把稳定逻辑重新塞回 `app` | `:feature-home:testDebugUnitTest` |
| `:feature-search` | `当前重构实施者` | Search feature | 搜索页状态机、筛选、trace、retry policy | trigger 语义、筛选行为、性能 trace 是否稳定 | `:feature-search:testDebugUnitTest` |
| `:feature-login` | `当前重构实施者` | Login feature | 登录状态机与登录相关 gateway | 登录语义、用户态桥接、宿主入口兼容 | `:feature-login:testDebugUnitTest` |
| `:feature-book` | `当前重构实施者` | Book feature | 书籍详情状态机、格式化、gateway | 详情页语义、跳转到 Reader 语义 | `:feature-book:testDebugUnitTest` |
| `:feature-reader` | `当前重构实施者` | Reader feature | Reader 状态层、恢复 / 设置 / trace 协调器 | 不得重开大拆；优化只限 coordinator / policy | `:feature-reader:testDebugUnitTest` |
| `:feature-rn-host` | `当前重构实施者` | RN host runtime | `BridgeViewModel`、`SettingsViewModel`、React host path / theme sync | `COLD_OPEN / OPEN / REUSED`、theme sync、host attach 时序 | `:feature-rn-host:testDebugUnitTest`, `__tests__/bridge/**`, device host sample |
| `:feature-welfare` | `当前重构实施者` | Welfare / WebView | welfare 状态层、bootstrap、WebView 性能协调 | 首开 / 复开 / 回退复用路径与 once-only 语义 | `:feature-welfare:testDebugUnitTest`, device Welfare sample |
| `:macrobenchmark` | `当前重构实施者` | performance evidence | startup / scroll benchmark 工具链 | benchmark 是否仍能产出可信样本 | `:macrobenchmark:assemble`, connected benchmark 样本 |

## 评审升级规则
### 必须升级到跨 owner 面评审
- `:app` 变更触达 `feature-*` 业务逻辑。
- `:core-bridge*` 变更触达 route、payload、事件或 RN `componentName`。
- `:core-network` 变更触达主请求入口、trace / request id 语义。
- `:feature-rn-host` 变更触达主题同步、root view cache、context-ready path。
- `:feature-reader` 变更试图越过“轻触式优化”边界，进入新的大拆。

### 可在单 owner 面内关闭
- 只影响模块内部协作者、且不改变对外 API / route / payload / schema。
- 只补充测试、文档、治理报告、性能样本，而不改变运行时语义。

## 当前残余治理缺口
- owner 面已明确，但仍缺脚本化 CODEOWNERS / 自动 reviewer 分发。
- 当前更多依赖 control-plane 文档和评审纪律，而不是平台自动护栏。

## 主要引用
- `docs/refactor/phase-5/module-graph-current-state.md`
- `docs/refactor/phase-5/module-verification-matrix-2026-03-26.md`
- `docs/refactor/phase-5/host-compat-validation-2026-03-26.md`
- `docs/refactor/phase-4/bridge-schema-compat-governance-2026-03-27.md`
