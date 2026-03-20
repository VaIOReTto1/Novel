# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 4`
- 阶段状态：`in_progress`
- 门禁模式：`严格门禁`
- 最近结论：第一阶段（Phase 0-2）已正式关闭并标记为 `validated`，第二阶段（Stage 2 = Phase 3-4）中的 `Phase 3` 已正式关闭并标记为 `validated`。高风险生产网络路径已统一切入 `NetworkFacade`，Bridge 层匿名协程作用域与 `MainApplication` 的按需初始化入口已完成收口，`StorageFacade`/`AppError` 第一批目标均已达成，Bridge 与存储回退开关及网络结构性回退边界也已明确。
- 下一步：继续 `Wave 4`，优先收束 `V4-04` 的生产 mock 剩余项结论，并开始 `Phase 4` closeout 文档
- 当前 Phase 默认编制：`4 helpers + 1 Leader`
  - `BridgeFacadeSplitAgent`
  - `FeatureBoundarySplitAgent`
  - `CacheReaderLightAgent`
  - `HostRiskQualityAgent`
- 当前激活波次：`Wave 4`

## 阶段状态总表
| Phase | 名称 | 状态 | 进入条件 | 退出条件 |
| --- | --- | --- | --- | --- |
| Phase 0 | 基线与控制面板 | validated | `docs/refactor/` 文档骨架已创建 | 全部 `V0-*` 至少为 `green` |
| Phase 1 | 发布、安全与合规治理 | validated | `Phase 0 = validated` | 全部 `V1-*` 为 `green` |
| Phase 2 | 质量门禁与自动化护栏 | validated | `Phase 1 = validated` | 全部 `V2-*` 为 `green` |
| Phase 3 | 基础设施收口 | validated | 第一阶段正式 `validated` | 全部 `V3-*` 为 `green` |
| Phase 4 | 边界收口与超大类拆分 | planned | `Phase 3 = validated` | 全部 `V4-*` 为 `green` |

## 最近验证结论
- `V0-01 ~ V0-06` 已全部具备绿色验证证据，Phase 0 已完成。
- `V1-01 ~ V1-09` 已全部具备绿色验证证据，Phase 1 已完成。
- `V2-01 ~ V2-09` 已全部具备绿色验证证据，Phase 2 已完成。
- 第二阶段规划文档已落盘，`V3-05` 静态债基线已建立。
- `V3-01 ~ V3-07` 已全部具备绿色验证证据，`Phase 3` 已正式完成。
- `V4-05` 宿主页风险验证已进入 `ready_for_validation/green`。
- `V4-06` 第二阶段静态债已达到当前阶段阈值，并进入 `ready_for_validation/green`。

## 文档索引
- [总重构路线图](./master-roadmap.md)
- [第一阶段重构总结（Phase 0-2）](./stage-1-phase-0-2-summary.md)
- [第二阶段重构计划（Stage 2 = Phase 3-4）](./stage-2-phase-3-4-plan.md)
- [第二阶段当前状态文档](./stage-2-closeout-summary.md)
- [第一阶段关闭总结（Phase 2 closeout）](./phase-2/phase-2-closeout-assessment.md)
- [Phase 3 关闭评审与签字确认报告](./phase-3/phase-3-closeout-assessment.md)
- [Phase 0 - 基线与控制面板](./phases/phase-0-foundation.md)
- [Phase 1 - 发布、安全与合规治理](./phases/phase-1-release-security.md)
- [Phase 2 - 质量门禁与自动化护栏](./phases/phase-2-quality-gates.md)
- [Phase 3 - 基础设施收口](./phases/phase-3-infra-consolidation.md)
- [Phase 4 - 边界收口与超大类拆分](./phases/phase-4-boundary-and-class-split.md)
- [Phase 4 Wave Tracker](./phase-4/phase-4-wave-tracker.md)
- [Phase 4 包边界图与迁移映射](./phase-4/package-boundary-map.md)
- [Phase 4 超大类职责切片图](./phase-4/large-class-responsibility-slices.md)
- [Phase 4 BridgeFacade 与 Delegate 映射表](./phase-4/bridge-facade-delegate-map.md)
- [Phase 4 宿主页挂载与风险验证矩阵](./phase-4/host-risk-validation-matrix.md)
- [Phase 4 原子拆分 Backlog](./phase-4/atomic-split-backlog.md)
- [Phase-aware GPT-5.4 协作策略](./collaboration/phase-aware-gpt5.4-subagent-policy.md)
- [Phase-aware GPT-5.4 协作策略评审报告](./collaboration/phase-aware-gpt5.4-subagent-policy-review.md)
- [双人交叉评审模板](./templates/two-person-cross-review-template.md)
- [Phase 0-2 验证看板](./tracking/phase-0-2-validation-board.md)
- [Phase 3-4 验证看板](./tracking/phase-3-4-validation-board.md)
- [第二阶段静态债基线](./tracking/stage-2-static-debt-baseline.md)
- [Subagent Dispatch Log](./tracking/subagent-dispatch-log.md)
- [Rollback Index](./tracking/rollback-index.md)
- [偏差与决策日志](./tracking/decision-log.md)
- [原子提交规范](./tracking/atomic-commit-guide.md)

## 使用规则
- 所有阶段状态更新必须同步到本文件、阶段文档和验证看板。
- 所有进入下一阶段的决定必须在 `decision-log.md` 中登记。
- 所有原子化改动完成后立即提交 Git，提交信息使用中文。
- 所有新 phase 文档必须先写 `协作编制`，再写任务拆解。
