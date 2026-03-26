# Current Focus

## 当前状态
- 当前分支：`main`
- 当前权威结论：`Phase 5 = in_progress`，`Stage 3 = in_progress`，`Phase 6 = validated`
- 当前 refactor 控制面板以 `docs/refactor/README.md` 为准
- 当前 Android 模块图已经稳定为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `Phase 0-4` 已经完成并关闭
- `Phase 5` 在 `2026-03-21` 留下首轮 closeout checkpoint，但当前仓库事实仍处于 reopen 深化阶段
- `Phase 6` 保持 `validated`，当前没有因为 reopen 再次被重开

## 默认下一主线
- 默认下一主线：继续推进 `Phase 5` reopen 深化，而不是提前进入 `Phase 7`
- 第一落点：继续下沉 `core-network / core-bridge` 与各 `feature-*` 的深层迁移
- 第二落点：补齐 reopen 版验证矩阵、closeout 与 rollback 文档，让 `Phase 5` 和 `Stage 3` 重新具备关闭条件

## Blockers / Known Drift
- 根 `README.md` 的技术版本和成熟度描述仍可能落后于当前代码与当前 refactor 文档
- `2026-03-21` 的 `Phase 5 / Stage 3 validated` 只应视为历史 checkpoint，不能直接当当前状态
- `2026-03-26` 的 reopen closeout 记录也只能当历史痕迹，当前项目级权威文档仍是 `Phase 5 = in_progress`
- `docs/harness/generated/workspace-snapshot.md` 在 `HEAD` 变化后必须重新生成
- `.trae/rules/project_rules.md` 现在只是 shim，不再承担项目事实说明

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-3-phase-5-6-plan.md](../refactor/stage-3-phase-5-6-plan.md)
- [docs/refactor/phases/phase-5-gradle-modularization.md](../refactor/phases/phase-5-gradle-modularization.md)
- [docs/refactor/tracking/phase-5-6-validation-board.md](../refactor/tracking/phase-5-6-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-03-26` by harness rollout v2 follow-up
