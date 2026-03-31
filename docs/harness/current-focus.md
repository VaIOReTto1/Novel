# Current Focus

## 当前状态
- 当前分支：`main`
- 当前权威结论：`Stage 6 = validated`，`Phase 12 = validated`，`Phase 13 = validated`，`Phase 14 = validated`
- 当前 refactor authority 以 `docs/refactor/README.md` 为准
- 当前 Android 模块图保持为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `2026-03-30` 已完成 `Stage 4` closeout
- `2026-03-30` 已完成 `Stage 5` closeout
- `2026-03-31` 已完成 `Stage 6` closeout

## 默认下一主线
- 当前没有新的 active refactor stage。
- 默认进入长期维护 / reopen 模式。
- 后续如需继续推进 RN 治理，应通过 reopen 或新 Stage 进入。

## 当前已确认的 RN 事实
- `src/utils/runtime/**` 已稳定为 runtime 收口层。
- `src/utils/bridge/**` 已稳定为原生桥接包装层。
- 主要 RN 页面域已完成首轮 page-model 化。
- `componentRegistry.ts` 已与所有 `*Component.tsx` 注册入口对齐，并有自动化测试守护。

## Blockers / Known Drift
- Root `README.md` 仍可能滞后于当前 refactor authority。
- `android/gradle/libs.versions.toml` 仍缺失。
- 当前仓库仍无统一 Crash / ANR / 灰度平台。

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-6-closeout-summary.md](../refactor/stage-6-closeout-summary.md)
- [docs/refactor/phase-13/phase-13-closeout-assessment.md](../refactor/phase-13/phase-13-closeout-assessment.md)
- [docs/refactor/phase-14/phase-14-closeout-assessment.md](../refactor/phase-14/phase-14-closeout-assessment.md)
- [docs/refactor/tracking/phase-12-14-validation-board.md](../refactor/tracking/phase-12-14-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-03-31` by Stage 6 closeout
