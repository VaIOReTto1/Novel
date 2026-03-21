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
| V5-01 | Phase 5 | 模块图与依赖规则稳定 | 根工程模块图、依赖方向、禁止环依赖规则明确 | `docs/refactor/stage-3-phase-5-6-plan.md`, `docs/refactor/phases/phase-5-gradle-modularization.md`, `settings.gradle*`, 模块图文档 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V5-02 | Phase 5 | 首批 `core/*` 模块稳定 | `core-common / core-network / core-storage / core-bridge-contract` 可构建可集成 | `phase-5` 文档、模块源码、模块级测试命令 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V5-03 | Phase 5 | 首批 `feature/*` 模块稳定 | `feature-home / feature-search / feature-welfare / feature-rn-host` 可构建可集成 | `phase-5` 文档、模块源码、回归命令 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V5-04 | Phase 5 | Bridge / RN Host 跨模块兼容稳定 | route、payload、RN Host 语义不变 | Bridge contract、host 验证、模块集成证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V5-05 | Phase 5 | 模块级 build/test/lint 矩阵稳定 | 模块化后命令矩阵可执行且可追溯 | 模块级 CI/workflow 文档、命令证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V5-06 | Phase 5 | Phase 6 进入条件明确 | 性能专项入口与 carried debt 清晰 | `docs/refactor/phases/phase-6-performance-governance.md`, `decision-log.md` | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |

## Phase 6
| ID | Phase | Item | Expected | Evidence | Actual | Status | Result Analysis | Owner | Validator | Validated On |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| V6-01 | Phase 6 | Stage 3 性能 baseline 稳定 | 启动、滚动、Reader、WebView/Bridge baseline 可追溯 | `docs/refactor/phases/phase-6-performance-governance.md`, baseline 文档、证据归档 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-02 | Phase 6 | 启动预算与 baseline profile 稳定 | 冷启动/热启动预算和 profile 证据成立 | benchmark / baseline profile 证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-03 | Phase 6 | 首页/搜索/Reader 关键链路预算稳定 | 核心交互无未解释回退 | 专项性能报告、回归命令 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-04 | Phase 6 | Welfare/WebView/Bridge 性能验证稳定 | WebView、RN Host、Bridge 关键路径预算稳定 | 专项报告与运行证据 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-05 | Phase 6 | 性能观测与报告模板闭环 | 性能 diff、预算表、观测模板稳定 | 专项文档与 evidence 规则 | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
| V6-06 | Phase 6 | Stage 3 closeout 与 Phase 7 进入条件明确 | 第三阶段关闭总结完成，下一阶段入口清晰 | `docs/refactor/stage-3-closeout-summary.md`, `decision-log.md` | 待执行 | `planned` | `yellow` | 当前重构实施者 | 阶段门禁批准者 | `n/a` |
