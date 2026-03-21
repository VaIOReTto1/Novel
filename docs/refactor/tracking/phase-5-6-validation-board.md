# Phase 5-6 验证看板

## 使用规则
- 每条记录必须填写 `Expected / Evidence / Actual / Status / Result Analysis / Owner / Validator / Validated On`
- `Status` 允许值：
  - `planned`
  - `in_progress`
  - `blocked`
  - `ready_for_validation`
  - `validated`
  - `not_met`
  - `deferred`
- `Result Analysis` 允许值：
  - `green`
  - `yellow`
  - `red`

## Phase 5
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V5-01 | Phase 5 | 模块图与依赖规则稳定 | 根工程模块图、依赖方向、禁止环依赖规则明确 | `docs/refactor/stage-3-phase-5-6-plan.md`, `docs/refactor/phases/phase-5-gradle-modularization.md`, `settings.gradle*`, `docs/refactor/phase-5/module-graph-current-state.md` | 当前模块图已纳入 `:core-common / :core-bridge-contract / :core-storage / :core-network / :feature-home / :feature-search / :feature-welfare / :feature-rn-host`，共享构建约定已落地，首批依赖方向已形成稳定快照 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |
| V5-02 | Phase 5 | 首批 `core/*` 模块稳定 | `core-common / core-network / core-storage / core-bridge-contract` 可构建可集成 | `phase-5` 文档、模块源码、模块级测试命令 | `core-storage`、`core-network`、`core-common`、`core-bridge-contract` 均已落地，并已通过模块级单测；`app:testDebugUnitTest` 当前继续通过 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |
| V5-03 | Phase 5 | 首批 `feature/*` 模块稳定 | `feature-home / feature-search / feature-welfare / feature-rn-host` 可构建可集成 | `phase-5` 文档、模块源码、回归命令 | `feature-welfare` 已完成两轮低风险切口；`feature-search`、`feature-home`、`feature-rn-host` 已完成首轮最小切口；四条 feature 线当前都已具备模块级验证证据，并在当前 build graph 下通过 `:app:testDebugUnitTest` | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |
| V5-04 | Phase 5 | Bridge / RN Host 跨模块兼容稳定 | route、payload、RN Host 语义不变 | `docs/refactor/phase-5/host-compat-validation-2026-03-21.md`, Bridge contract tests, `SettingsPage.smoke.test.tsx`, Phase 4 host evidence | 当前模块化切口未触碰 RN/Application 宿主根逻辑；`profile / settings / aipage` 后模块化 spot-check 已回到 `com.novel` host 容器；Bridge contract 与 RN settings smoke 继续通过 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |
| V5-05 | Phase 5 | 模块级 build/test/lint 矩阵稳定 | 模块化后命令矩阵可执行且可追溯 | `docs/refactor/phase-5/module-verification-matrix-2026-03-21.md` | 当前模块级矩阵与 Stage 3 收尾命令均已执行并归档，含 `lintDebug / compileDebugAndroidTestKotlin / :macrobenchmark:assemble / app:testDebugUnitTest / npm test` | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |
| V5-06 | Phase 5 | Phase 6 进入条件明确 | 性能专项入口与 carried debt 清晰 | `docs/refactor/phases/phase-6-performance-governance.md`, `decision-log.md` | `Phase 6` 文档已补充当前真实模块边界输入与明确进入条件；`core-network` 深化保留为 carried debt，但不再阻塞 Phase 6 启动 | `validated` | `green` | 当前重构实施者 | 阶段门禁批准者 | `2026-03-21` |

## Phase 6
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V6-01 | Phase 6 | Stage 3 性能 baseline 稳定 | 启动、滚动、Reader、WebView/Bridge baseline 可追溯 | `docs/refactor/phases/phase-6-performance-governance.md`, baseline 文档、证据归档 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-02 | Phase 6 | 启动预算与 baseline profile 稳定 | 冷启动/热启动预算和 profile 证据成立 | benchmark / baseline profile 证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-03 | Phase 6 | 首页/搜索/Reader 关键链路预算稳定 | 核心交互无未解释回退 | 专项性能报告、回归命令 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-04 | Phase 6 | Welfare/WebView/Bridge 性能验证稳定 | WebView、RN Host、Bridge 关键路径预算稳定 | 专项报告与运行证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-05 | Phase 6 | 性能观测与报告模板闭环 | 性能 diff、预算表、观测模板稳定 | 专项文档与 evidence 规则 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-06 | Phase 6 | Stage 3 closeout 与 Phase 7 进入条件明确 | 第三阶段关闭总结完成，下一阶段入口清晰 | `docs/refactor/stage-3-closeout-summary.md`, `decision-log.md` | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
