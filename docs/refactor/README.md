# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 5`
- 阶段状态：`in_progress`
- 门禁模式：`严格门禁`
- 最近结论：`2026-03-21` 的 `Phase 5 = validated`、`Phase 6 = validated`、`Stage 3 = validated` 作为历史 checkpoint 保留；`2026-03-23` 起因模块化深度不足与 Community 收口仍未完成，当前口径重新切回 `Phase 5 = in_progress`、`Stage 3 = in_progress`。`Phase 6` 继续保持 `validated`，`Phase 7` 继续保持 `planned`。
- 下一步：继续深化 `feature-search / feature-welfare / feature-rn-host / feature-book / feature-login / feature-reader`，并收口 Community 现有跳转/分享。
- 当前 Phase 默认编制：`5 helpers + 1 Leader（reopen baseline）`

## 阶段状态总表
| Phase | 名称 | 状态 | 进入条件 | 退出条件 |
| --- | --- | --- | --- | --- |
| Phase 0 | 基线与控制面板 | validated | `docs/refactor/` 文档骨架已创建 | 全部 `V0-*` 至少为 `green` |
| Phase 1 | 发布、安全与合规治理 | validated | `Phase 0 = validated` | 全部 `V1-*` 为 `green` |
| Phase 2 | 质量门禁与自动化护栏 | validated | `Phase 1 = validated` | 全部 `V2-*` 为 `green` |
| Phase 3 | 基础设施收口 | validated | 第一阶段正式 `validated` | 全部 `V3-*` 为 `green` |
| Phase 4 | 边界收口与超大类拆分 | validated | `Phase 3 = validated` | 全部 `V4-*` 为 `green` |
| Phase 5 | Gradle 模块化准备、深化与拆分 | in_progress | `Stage 2 = validated` | 模块图、入口清单、深度模块迁移与组合入口稳定 |
| Phase 6 | 性能专项与基准治理 | validated | `Phase 5 = validated` | baseline、预算和专项证据闭环 |
| Phase 7 | 包体积、依赖与构建效率治理 | planned | `Phase 6 = validated` | size / dependency / build baseline 与关闭总结闭环 |

## 最近验证结论
- `V0-01 ~ V0-06` 已全部具备绿色验证证据，Phase 0 已完成。
- `V1-01 ~ V1-09` 已全部具备绿色验证证据，Phase 1 已完成。
- `V2-01 ~ V2-09` 已全部具备绿色验证证据，Phase 2 已完成。
- `V3-01 ~ V3-07` 已全部具备绿色验证证据，Phase 3 已正式完成。
- `V4-01 ~ V4-08` 已全部具备绿色验证证据，Phase 4 已正式完成。
- `V5-01 ~ V5-06` 已全部具备绿色验证证据，构成 `2026-03-21` 的 Phase 5 首轮 closeout checkpoint。
- `V6-01 ~ V6-06` 已全部具备绿色验证证据，Phase 6 已正式完成。
- `Stage 3 = Phase 5-6` 的 `2026-03-21` closeout checkpoint 已归档；当前因 Phase 5 深化重开而重新切回 `in_progress`。
- `DN2101` 的 `cmd package compile` 已被客观固化为已接受的环境阻塞项，不再误判为 `Phase 5/6` 仓库回归。

## 推荐阅读顺序
1. [第三阶段关闭总结](./stage-3-closeout-summary.md)
2. [Phase 6 关闭评审与签字确认报告](./phase-6/phase-6-closeout-assessment.md)
3. [原始蓝图 Phase 3-6 差异审计](./blueprint-v2-phase-3-6-gap-analysis.md)
4. [Phase 6 优化机会盘点](./phase-6/phase-6-optimization-opportunity-catalog.md)
5. [Phase 7 - 包体积、依赖与构建效率治理](./phases/phase-7-size-dependency-build-governance.md)

## 文档索引
- [总重构路线图](./master-roadmap.md)
- [第一阶段重构总结（Phase 0-2）](./stage-1-phase-0-2-summary.md)
- [第二阶段重构计划（Stage 2 = Phase 3-4）](./stage-2-phase-3-4-plan.md)
- [第二阶段关闭总结](./stage-2-closeout-summary.md)
- [第三阶段重构计划（Stage 3 = Phase 5-6）](./stage-3-phase-5-6-plan.md)
- [第三阶段关闭总结](./stage-3-closeout-summary.md)
- [原始蓝图 Phase 3-6 差异审计](./blueprint-v2-phase-3-6-gap-analysis.md)
- [Phase 0 - 基线与控制面板](./phases/phase-0-foundation.md)
- [Phase 1 - 发布、安全与合规治理](./phases/phase-1-release-security.md)
- [Phase 2 - 质量门禁与自动化护栏](./phases/phase-2-quality-gates.md)
- [Phase 3 - 基础设施收口](./phases/phase-3-infra-consolidation.md)
- [Phase 4 - 边界收口与超大类拆分](./phases/phase-4-boundary-and-class-split.md)
- [Phase 5 - Gradle 模块化与边界搬迁](./phases/phase-5-gradle-modularization.md)
- [Phase 5 关闭评审与签字确认报告](./phase-5/phase-5-closeout-assessment.md)
- [Phase 5 模块图现状](./phase-5/module-graph-current-state.md)
- [Phase 5 宿主页兼容验证](./phase-5/host-compat-validation-2026-03-21.md)
- [Phase 5 模块验证矩阵](./phase-5/module-verification-matrix-2026-03-21.md)
- [Phase 6 - 性能专项与基准治理](./phases/phase-6-performance-governance.md)
- [Stage 3 性能基线总入口](./phase-6/stage-3-performance-baseline-2026-03-21.md)
- [启动 Benchmark 运行记录](./phase-6/startup-benchmark-run-2026-03-21.md)
- [滚动 Benchmark 运行记录](./phase-6/scroll-benchmark-run-2026-03-21.md)
- [Baseline Profile 运行记录](./phase-6/baseline-profile-run-2026-03-21.md)
- [设备侧 Compile Blocker 记录](./phase-6/device-compile-blocker-2026-03-21.md)
- [搜索性能基线](./phase-6/search-performance-baseline-2026-03-21.md)
- [Reader 性能基线](./phase-6/reader-performance-baseline-2026-03-21.md)
- [WebView / Bridge 性能基线](./phase-6/webview-bridge-performance-baseline-2026-03-21.md)
- [数据库索引与 FTS4 治理报告](./phase-6/database-index-and-fts-governance-2026-03-22.md)
- [缓存清理治理报告](./phase-6/cache-cleanup-governance-2026-03-22.md)
- [Phase 6 性能预算摘要](./phase-6/performance-budget-summary.md)
- [Phase 6 优化机会盘点](./phase-6/phase-6-optimization-opportunity-catalog.md)
- [Phase 6 关闭评审与签字确认报告](./phase-6/phase-6-closeout-assessment.md)
- [Phase 7 - 包体积、依赖与构建效率治理](./phases/phase-7-size-dependency-build-governance.md)
- [Phase 0-2 验证看板](./tracking/phase-0-2-validation-board.md)
- [Phase 3-4 验证看板](./tracking/phase-3-4-validation-board.md)
- [Phase 5-6 验证看板](./tracking/phase-5-6-validation-board.md)
- [第二阶段静态债基线](./tracking/stage-2-static-debt-baseline.md)
- [第三阶段静态债基线](./tracking/stage-3-static-debt-baseline.md)
- [Subagent Dispatch Log](./tracking/subagent-dispatch-log.md)
- [Rollback Index](./tracking/rollback-index.md)
- [偏差与决策日志](./tracking/decision-log.md)

## 使用规则
- 所有阶段状态更新必须同步到本文件、阶段文档和验证看板。
- 所有进入下一阶段的决定必须在 `decision-log.md` 中登记。
- 所有原子化改动完成后立即提交 Git，提交信息使用中文。
- 所有新 phase 文档必须先写 `协作编制`，再写任务拆解。
