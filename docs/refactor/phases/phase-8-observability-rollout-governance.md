# Phase 8 - 可观测性、灰度与长期治理

## 目标
- 在 `Phase 7` 固定 size / dependency / build 基线之后，把当前仓库已有但分散的治理能力收成长期控制面：
  - observability 指标目录
  - feature flag / kill switch registry
  - rollout / rollback playbook
  - ADR / reviewer / owner 治理规则
- 让后续 feature 演进不再依赖口头约定或一次性 closeout。

## 范围
- 启动、Bridge、WebView、缓存、权限、构建产物等指标宿主文档与命名规范
- `RefactorFeatureFlags` 的治理边界、开关登记、默认值与使用规则
- rollout / canary / kill switch / rollback 的文档化流程
- ADR、owner、review checklist、release rollback 入口治理

## 非目标
- 不在本阶段补建远程配置或线上可观测性平台
- 不重开 `Phase 6` 性能专项
- 不借治理名义重开 `Phase 5` 模块化迁移
- 不修改 UI、route、bridge payload 语义

## 进入条件
- `Phase 7 = validated`
- `Phase 7` 的 size / dependency / build 基线已固定
- `Phase 7` 残余风险已留痕，不再混入 `Phase 8` 主线

## 当前仓库入口基线
- 本地 feature flag 已存在：
  - `android/core-common/src/main/java/com/novel/core/config/RefactorFeatureFlags.kt`
  - 默认值由 `BuildConfig` 与 `NovelUserDefaultsBackedRefactorFeatureFlags` 提供
- 局部可观测性代码已存在：
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
  - `RequestIdInterceptor` / `X-Request-Id` / `X-Trace-Id`
- 当前仍不存在统一的：
  - Crash 平台接入
  - ANR 平台接入
  - remote config 平台
  - 线上 rollout 平台
- 因此 Phase 8 的第一职责是“建立制度和宿主文档”，不是伪造平台现状。

## 协作编制
### Leader Mode
- `single leader / three helpers`

### Base Helper Count
- `3`

### Scale-Up Triggers
- 当 observability、flag registry、rollout playbook 三条线都已有独立材料可并行整理时，可短期扩容一名 helper。

### Scale-Down Triggers
- 当只剩 closeout 或单线治理收尾时，回到最小编制。

### Agent Roster
- `ObservabilityAgent`
  - 指标目录、命名规范、现有 monitor 宿主化
- `GovernanceAdrAgent`
  - ADR、review checklist、owner / validator 机制
- `RolloutFlagAgent`
  - flag registry、kill switch、rollout / rollback playbook

### Lock Strategy
- `LOCK-OBSERVABILITY-CONTROL`
- `LOCK-GOVERNANCE-ADR`
- `LOCK-ROLLOUT-FLAGS`

### Retry Window
- 文档抽样核对、registry 校验：`2` 轮
- 若两轮后仍存在“代码事实与控制面不一致”，必须登记残余风险

### Escalation Window
- 任一治理方案要求引入新的线上平台或 SDK
- 任一治理方案要求改变 route / bridge payload / 用户可感知行为

### Leader-only Actions
- 统一 Phase 8 指标命名口径
- 串行确认 ADR / reviewer / rollback 入口
- 同步 README、验证看板、决策日志与 closeout

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P8.1 | 固定 observability 指标目录与命名口径 | 当前局部 monitor 与 trace 能力可追溯 |
| P8.2 | 固定 feature flag / kill switch registry | 开关默认值、owner 与用途明确 |
| P8.3 | 固定 rollout / rollback playbook | 发布与回退步骤可执行 |
| P8.4 | 固定 ADR / reviewer / owner 机制 | 关键变更不再只依赖口头协作 |
| P8.5 | 输出 Stage 4 closeout 与长期维护入口 | 后续阶段入口清晰 |

## 交付物
- observability 指标目录文档
- feature flag / kill switch registry
- rollout / rollback playbook
- ADR / review / owner 治理文档

## 当前已落盘工件
- `docs/refactor/phase-8/observability-metric-catalog-2026-03-30.md`
- `docs/refactor/phase-8/feature-flag-and-kill-switch-registry-2026-03-30.md`
- `docs/refactor/phase-8/rollout-and-rollback-playbook-2026-03-30.md`
- `docs/refactor/phase-8/adr-reviewer-owner-governance-2026-03-30.md`
- `docs/refactor/phase-8/phase-8-closeout-assessment.md`

## 当前状态
- `validated（Phase 8 closeout 生效于 2026-03-30）`
