# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 0`
- 阶段状态：`in_progress`
- 门禁模式：`严格门禁`
- 最近结论：已完成重构文档体系初始化，开始落地 Phase 0 基线与资产盘点
- 下一步：补齐 `Phase 0/1/2` 阶段文档与跟踪看板，然后开始首批基线采集与风险登记

## 阶段状态总表
| Phase | 名称 | 状态 | 进入条件 | 退出条件 |
| --- | --- | --- | --- | --- |
| Phase 0 | 基线与控制面板 | in_progress | `docs/refactor/` 文档骨架已创建 | 全部 `V0-*` 至少为 `green` |
| Phase 1 | 发布、安全与合规治理 | planned | `Phase 0 = validated` | 全部 `V1-*` 为 `green` |
| Phase 2 | 质量门禁与自动化护栏 | planned | `Phase 1 = validated` | 全部 `V2-*` 为 `green` |

## 最近一次验证结论
- 当前尚未进入正式阶段关闭验证。
- 第一轮目标是完成 Phase 0 文档、资产盘点、风险清单、测量协议和验证看板。

## 文档索引
- [总重构路线图](./master-roadmap.md)
- [Phase 0 - 基线与控制面板](./phases/phase-0-foundation.md)
- [Phase 1 - 发布、安全与合规治理](./phases/phase-1-release-security.md)
- [Phase 2 - 质量门禁与自动化护栏](./phases/phase-2-quality-gates.md)
- [Phase 0-2 验证看板](./tracking/phase-0-2-validation-board.md)
- [偏差与决策日志](./tracking/decision-log.md)
- [原子提交规范](./tracking/atomic-commit-guide.md)

## 使用规则
- 所有阶段状态更新必须同步到本文件、阶段文档和验证看板。
- 所有进入下一阶段的决定必须在 `decision-log.md` 中登记。
- 所有原子化改动完成后立即提交 Git，提交信息使用中文。
