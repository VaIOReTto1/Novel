# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 2`
- 阶段状态：`ready_for_validation`
- 门禁模式：`严格门禁`
- 最近结论：第一阶段（Phase 0-2）已完成基线、发布安全、质量门禁、核心 smoke、Bridge contract、质量工作流、证据归档标准与 flake 规则。
- 下一步：先完成第一阶段正式关闭评审，再按进入条件启动第二阶段（Stage 2 = Phase 3-4）。

## 阶段状态总表
| Phase | 名称 | 状态 | 进入条件 | 退出条件 |
| --- | --- | --- | --- | --- |
| Phase 0 | 基线与控制面板 | validated | `docs/refactor/` 文档骨架已创建 | 全部 `V0-*` 至少为 `green` |
| Phase 1 | 发布、安全与合规治理 | validated | `Phase 0 = validated` | 全部 `V1-*` 为 `green` |
| Phase 2 | 质量门禁与自动化护栏 | ready_for_validation | `Phase 1 = validated` | 全部 `V2-*` 为 `green` |
| Phase 3 | 基础设施收口 | planned | 第一阶段正式 `validated` | 全部 `V3-*` 为 `green` |
| Phase 4 | 边界收口与超大类拆分 | planned | `Phase 3 = validated` | 全部 `V4-*` 为 `green` |

## 最近验证结论
- `V0-01 ~ V0-06` 已全部具备绿色验证证据，Phase 0 已完成。
- `V1-01 ~ V1-09` 已全部具备绿色验证证据，Phase 1 已完成。
- `V2-*` 已形成完整证据链，当前处于 `ready_for_validation`。
- 第二阶段规划文档已落盘，但尚未进入执行状态。

## 文档索引
- [总重构路线图](./master-roadmap.md)
- [第一阶段重构总结（Phase 0-2）](./stage-1-phase-0-2-summary.md)
- [第二阶段重构计划（Stage 2 = Phase 3-4）](./stage-2-phase-3-4-plan.md)
- [Phase 0 - 基线与控制面板](./phases/phase-0-foundation.md)
- [Phase 1 - 发布、安全与合规治理](./phases/phase-1-release-security.md)
- [Phase 2 - 质量门禁与自动化护栏](./phases/phase-2-quality-gates.md)
- [Phase 3 - 基础设施收口](./phases/phase-3-infra-consolidation.md)
- [Phase 4 - 边界收口与超大类拆分](./phases/phase-4-boundary-and-class-split.md)
- [Phase 0-2 验证看板](./tracking/phase-0-2-validation-board.md)
- [Phase 3-4 验证看板](./tracking/phase-3-4-validation-board.md)
- [第二阶段静态债基线](./tracking/stage-2-static-debt-baseline.md)
- [偏差与决策日志](./tracking/decision-log.md)
- [原子提交规范](./tracking/atomic-commit-guide.md)

## 使用规则
- 所有阶段状态更新必须同步到本文件、阶段文档和验证看板。
- 所有进入下一阶段的决定必须在 `decision-log.md` 中登记。
- 所有原子化改动完成后立即提交 Git，提交信息使用中文。
