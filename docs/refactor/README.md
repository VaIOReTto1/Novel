# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 15`
- 阶段状态：`in_progress`
- 当前 Stage：`Stage 7 = Phase 15-18`
- Stage 状态：`in_progress`
- 最新生效切换：`2026-03-31 Stage 7 activation`

## 当前结论
- `Stage 3 = validated`
- `Stage 4 = validated`
- `Stage 5 = validated`
- `Stage 6 = validated`
- `Stage 7 = in_progress`
- `Phase 15 = in_progress`
- `Phase 16 = in_progress`
- `Phase 17 = in_progress`
- `Phase 18 = in_progress`

## Stage 7 摘要
- `Phase 15` 负责建立 Stage 7 控制面、页面与组件视觉盘点、机器可对账清单、Figma 审计页和事实对账门禁。
- `Phase 16` 负责 Figma 基础系统、语义化 Token、跨端导出链路与平台适配规则。
- `Phase 17` 负责图标、图片、插画、版权台账、共享基元与展示基建。
- `Phase 18` 负责双端页面重皮肤、视觉回归、无障碍与收尾门禁。

## 当前权威入口
- [Stage 7 计划](./stage-7-phase-15-18-plan.md)
- [Phase 15 宿主文档](./phases/phase-15-visual-audit-and-control-plane.md)
- [Phase 16 宿主文档](./phases/phase-16-figma-foundations-and-token-source.md)
- [Phase 17 宿主文档](./phases/phase-17-asset-governance-and-shared-primitives.md)
- [Phase 18 宿主文档](./phases/phase-18-visual-rollout-and-quality-gates.md)
- [Phase 15-18 验证看板](./tracking/phase-15-18-validation-board.md)
- [decision-log.md](./tracking/decision-log.md)
- [rollback-index.md](./tracking/rollback-index.md)

## 历史阶段入口
- [Stage 4 closeout summary](./stage-4-closeout-summary.md)
- [Stage 5 closeout summary](./stage-5-closeout-summary.md)
- [Stage 6 closeout summary](./stage-6-closeout-summary.md)

## 使用规则
- `Stage 7` 是新的视觉系统与资产治理主线，不复用历史上已关闭的 `Stage 4 / Phase 7` 命名。
- Stage 状态变更时，先更新 `docs/refactor/**`，再同步 `docs/harness/**`，最后刷新 `workspace-snapshot.md`。
- Stage 7 的 page / component / asset / figma 对账结果必须以脚本输出和 Figma frame map 为准，不以手工目录为准。
