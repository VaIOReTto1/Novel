# Current Focus

## 当前状态
- 当前分支：`main`
- 当前权威结论：`Stage 6 = in_progress`，`Phase 12 = validated`，`Phase 13 = in_progress`，`Phase 14 = planned`
- 当前 refactor authority 以 `docs/refactor/README.md` 为准
- 当前 Android 模块图保持为 `app + core-* + feature-* + macrobenchmark`

## 最近完成
- `2026-03-30` 已完成 `Stage 4` closeout
- `2026-03-30` 已完成 `Stage 5` closeout
- `2026-03-31` 已建立 `Stage 6 = Phase 12-14` 的控制面
- `2026-03-31` 已完成 `Phase 12` closeout，并将 `Stage 6` 切到 `in_progress`
- `2026-03-31` 已启动 `Phase 13`，并完成 `Profile / Settings` 第一波域收口
- `2026-03-31` 已继续完成 `Bookshelf / History / Watchlist / Comment` 多个页面域波次的首轮收口
- `2026-03-31` 已继续完成 `Writer / AIWriteAssistant / BookManage` 首轮收口，并为 `WritePage` 建立 helper 护栏

## 默认下一主线
- 当前执行阶段：`Stage 6 = in_progress`
- 当前活动 phase：`Phase 13 = in_progress`
- 当前已关闭阶段：`Phase 12 = validated`
- 当前已完成波次：
  - `Profile + app root preload`
  - `Settings`
  - `Bookshelf / History / Watchlist`
  - `Comment / ReviewDetail / WriteReview`
- 当前已推进到：
  - `Writer / AIWriteAssistant / BookManage`
- 默认下一落点：`Community / remaining heavy pages / WritePage deeper extraction`
- 后续阶段：`Phase 14 = RN contract / registry / mock-fallback / maintainability`

## 当前已确认的 RN 事实
- `src/utils/runtime/**` 已成为运行时收口层。
- `src/utils/bridge/**` 已成为原生桥接包装层。
- `src/**` 中的 `NativeModules` / `DeviceEventEmitter` / `BackHandler` 已不再散落在页面与 store 中。
- `rawPrimitivesBoundary` 结构测试已建立，可作为后续 Phase 13 的回归护栏。

## Blockers / Known Drift
- Root `README.md` 仍可能落后于当前 refactor authority。
- `android/gradle/libs.versions.toml` 仍缺失，Android 依赖版本继续分散在多个 Gradle 脚本中。
- `org.gradle.configuration-cache=false` 仍未推进到全仓默认开启。
- 当前仓库已有本地 `RefactorFeatureFlags` 与局部性能 monitor，但仍无统一 Crash / ANR / 灰度平台。
- `docs/harness/generated/workspace-snapshot.md` 需要在 authority 更新后重新由脚本生成。

## Primary Source Refs
- [docs/refactor/README.md](../refactor/README.md)
- [docs/refactor/stage-6-phase-12-14-plan.md](../refactor/stage-6-phase-12-14-plan.md)
- [docs/refactor/phases/phase-12-rn-runtime-and-bridge-consolidation.md](../refactor/phases/phase-12-rn-runtime-and-bridge-consolidation.md)
- [docs/refactor/phase-12/phase-12-closeout-assessment.md](../refactor/phase-12/phase-12-closeout-assessment.md)
- [docs/refactor/phases/phase-13-rn-page-domain-refactor.md](../refactor/phases/phase-13-rn-page-domain-refactor.md)
- [docs/refactor/phase-13/profile-settings-domain-wave-2026-03-31.md](../refactor/phase-13/profile-settings-domain-wave-2026-03-31.md)
- [docs/refactor/phase-13/bookshelf-comment-domain-wave-2026-03-31.md](../refactor/phase-13/bookshelf-comment-domain-wave-2026-03-31.md)
- [docs/refactor/phase-13/writer-domain-wave-2026-03-31.md](../refactor/phase-13/writer-domain-wave-2026-03-31.md)
- [docs/refactor/tracking/phase-12-14-validation-board.md](../refactor/tracking/phase-12-14-validation-board.md)
- [docs/refactor/tracking/decision-log.md](../refactor/tracking/decision-log.md)

## Last Reviewed
- `2026-03-31` by Phase 13 wave 5
