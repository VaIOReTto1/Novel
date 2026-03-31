# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 14`
- 阶段状态：`validated`
- 当前 Stage：`Stage 6 = Phase 12-14`
- Stage 状态：`validated`
- 最新生效切换：`2026-03-31 Stage 6 closeout`

## 当前结论
- `Stage 3 = validated`
- `Stage 4 = validated`
- `Stage 5 = validated`
- `Stage 6 = validated`
- `Phase 12 = validated`
- `Phase 13 = validated`
- `Phase 14 = validated`
- 当前没有新的 active refactor phase 正在执行，默认进入长期维护 / reopen 模式。

## Stage 6 关闭摘要
- `Phase 12` 完成 RN runtime / bridge consolidation。
- `Phase 13` 完成 RN 页面域首轮收口，主要页面已进入 `page -> domain model -> store/hook` 的委派模式。
- `Phase 14` 完成 RN contract quality、component registry consistency、mock/fallback catalog 与 maintainability guide 的 repo-local 治理闭环。

## 当前权威入口
- [Stage 6 计划](./stage-6-phase-12-14-plan.md)
- [Phase 13 closeout assessment](./phase-13/phase-13-closeout-assessment.md)
- [Phase 14 closeout assessment](./phase-14/phase-14-closeout-assessment.md)
- [Stage 6 closeout summary](./stage-6-closeout-summary.md)
- [Phase 12-14 验证看板](./tracking/phase-12-14-validation-board.md)
- [decision-log.md](./tracking/decision-log.md)
- [rollback-index.md](./tracking/rollback-index.md)

## 历史阶段入口
- [Stage 4 closeout summary](./stage-4-closeout-summary.md)
- [Stage 5 closeout summary](./stage-5-closeout-summary.md)

## 使用规则
- 以后如需继续推进 RN 结构治理，应以 reopen 或新 Stage 形式进入，而不是回写本次 closeout 结论。
- 阶段状态变化时，先更新 `docs/refactor/**`，再同步 `docs/harness/**`，最后刷新 `workspace-snapshot.md`。
