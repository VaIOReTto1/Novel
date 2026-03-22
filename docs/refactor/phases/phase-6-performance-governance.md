# Phase 6 - 性能专项与基准治理

## 目标
- 在 `Phase 5` 验证后的模块边界上建立稳定的性能 baseline、预算和回归证据。
- 覆盖启动、首页/搜索滚动、Reader、Welfare/WebView、RN Host、Bridge 关键链路。
- 让性能问题从“临时观察”升级为“有预算、有证据、有门禁”的长期治理对象。

## 范围
- startup / scroll macrobenchmark
- baseline profile 与编译型探针
- Search 日志样本基线
- Reader 关键链路基线
- Welfare / WebView / RN Host / Bridge 基线
- 预算表与关闭总结文档

## 非目标
- 不继续做大的架构拆分
- 不以性能专项为名改产品语义
- 不做 `Phase 7` 的包体积 / 构建效率专项

## 进入条件
- `Phase 5 = validated`
- 模块图与依赖边界稳定
- 性能关键路径对应模块和页面入口已可单独定位

## 当前模块边界输入
- `core-common`
  - shared core state / mvi / domain / result / logging
- `core-storage`
  - storage facade and settings pilot
- `core-network`
  - network contract layer
- `core-bridge-contract`
  - pure bridge delegate/helper contracts
- `feature-home`
  - current first slice: `HomePerformanceOptimizer`
- `feature-search`
  - current first slice: `SearchPreferenceStorage`
- `feature-welfare`
  - current slices: error/loading components + internal utilities
- `feature-rn-host`
  - current first slice: `SettingsPreferenceStorage`
- `app`
  - remains composition root
  - still hosts `Reader`
  - still hosts RN/Application bootstrap and root host wiring

## 当前基线入口
- `docs/refactor/phase-6/stage-3-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- `docs/refactor/phase-6/scroll-benchmark-run-2026-03-21.md`
- `docs/refactor/phase-6/baseline-profile-run-2026-03-21.md`
- `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- `docs/refactor/phase-6/performance-budget-summary.md`
- `docs/refactor/phase-6/database-index-and-fts-governance-2026-03-22.md`
- `docs/refactor/phase-6/cache-cleanup-governance-2026-03-22.md`
- `docs/refactor/phase-6/phase-6-closeout-assessment.md`
- `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`

## 协作编制
### Leader Mode
- `single leader / three helpers`

### Base Helper Count
- `3`

### Scale-Up Triggers
- 启动、滚动/Reader、WebView/Bridge 三条性能线都具备独立证据脚手架时，可扩展额外 helper 补专项取证。
- baseline profile、macrobenchmark 与页面专项可以无锁并行时，可短期扩容。

### Scale-Down Triggers
- 当前只在刷新 baseline 或补关闭总结文档时，保持最小编制即可。
- 若设备矩阵不足或 benchmark 波动过大，先收缩到 `Leader + 当前专项 owner` 稳定口径。

### Agent Roster
- `StartupBudgetAgent`
  - 启动、baseline profile、macrobenchmark
- `ScrollReaderPerfAgent`
  - 首页/搜索滚动、Reader 关键链路
- `WebViewBridgePerfAgent`
  - Welfare/WebView、RN Host、Bridge 性能取证

### Lock Strategy
- `LOCK-STARTUP-BENCH`
- `LOCK-SCROLL-READER-PERF`
- `LOCK-WEBVIEW-BRIDGE-PERF`

### Retry Window
- benchmark / profile / macrobenchmark：`2` 次同环境重跑
- 专项数据分析或报告生成：`2` 次口径修正重试
- 若两次后仍无法得到稳定趋势，必须登记为残余风险，而不是强行关闭

### Escalation Window
- 任何性能优化要求改变 UI 语义或业务功能语义
- Reader 分页、翻页、核心渲染行为必须变化
- Phase 6 被迫演化为新的架构重构阶段
- 关键专项只能依赖不稳定或不可追溯的设备环境

### Leader-only Actions
- 固定预算口径
- 同步看板 / README / 关闭总结
- 串行执行 benchmark / 验证命令

## 任务拆解
| ID | 任务 | 预期产出 |
| --- | --- | --- |
| P6.1 | 刷新 Stage 3 性能基线 | 新模块边界上的 baseline 文档落盘 |
| P6.2 | 冷启动 / 热启动专项 | 启动预算、baseline profile、macrobenchmark 证据稳定 |
| P6.3 | 首页 / 搜索 / Reader 关键链路专项 | 关键交互预算与回归证据稳定 |
| P6.4 | Welfare / WebView / Bridge 专项 | WebView、RN Host、Bridge 初始化/交互性能证据稳定 |
| P6.5 | 性能观测与报告模板 | 性能 diff、问题归因、预算表统一 |
| P6.6 | 输出 Stage 3 关闭总结与 Phase 7 进入条件 | 下一阶段入口清晰 |

## 交付物
- Stage 3 性能 baseline 文档
- 启动、滚动、Reader、WebView/Bridge 专项报告
- benchmark / profile / blocker 证据归档
- 性能预算表
- Phase 6 关闭总结文档
- Phase 6 优化机会盘点文档
- Phase 7 进入条件文档

## 硬阈值
- 不允许关键路径出现未解释的性能回退
- benchmark / baseline profile 命令必须满足二选一：
  - 成功并留痕
  - 失败但具备可复现 blocker 与 remediation path
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`
- repo 级静态债不得回退：
  - `RN lint errors = 0`
  - `detekt weighted issues <= 1901`

## 风险与回滚
- 风险：
  - benchmark 波动受设备环境影响
  - Reader 与 WebView 专项容易触及高风险体验路径
  - 预算制定过于激进会压垮节奏
- 回滚：
  - 专项优化必须按单主题提交
  - 若优化收益不稳定或出现关键体验回退，直接回退该原子提交

## 检验计划
- `V6-01` Stage 3 性能 baseline 稳定
- `V6-02` 启动预算与 baseline profile 稳定
- `V6-03` 首页 / 搜索 / Reader 关键链路预算稳定
- `V6-04` Welfare / WebView / Bridge 性能验证稳定
- `V6-05` 性能观测与报告模板闭环
- `V6-06` Stage 3 关闭总结与 Phase 7 进入条件明确

## 关闭结论
- `V6-01 ~ V6-06` 已全部达到关闭条件
- `Stage 3 关闭总结` 已完成
- `Phase 7` 入口已固定为包体积、依赖与构建效率治理
- `DN2101` 的 `cmd package compile` 阻塞项已以已接受的环境阻塞项方式留痕，不再阻塞本阶段关闭

## 说明
- `Phase 6 validated` 的含义是：
  - 基线、证据、预算和 blocker 归档已经闭环
- 它不等于：
  - 所有性能优化动作都已经完成
- `Phase 6` 关闭后仍允许继续推进低风险 backlog 优化，但这些 follow-up：
  - 不重开 `Phase 6` 状态
  - 不改写 `Stage 3 = validated`
  - 必须同步记录到 `decision-log.md` 与 `phase-6-optimization-opportunity-catalog.md`
- 尚未完成的优化点，统一见：
  - `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`validated`
