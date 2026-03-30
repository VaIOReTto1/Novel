# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 13`
- 阶段状态：`in_progress`
- 当前 Stage：`Stage 6 = Phase 12-14`
- Stage 状态：`in_progress`
- 最新生效切换：`2026-03-31 Phase 12 closeout`

## 当前结论
- `Stage 3 = validated`，继续以 `2026-03-26` closeout 为收口事实。
- `Stage 4 = validated`，`Phase 7-8` 已完成当前计划内治理闭环。
- `Stage 5 = validated`，`Phase 9-11` 已完成当前计划内治理闭环。
- `Stage 6 = in_progress`，当前执行线已切到 RN 重构续规划。
- `Phase 12 = validated`，已完成 RN 运行时与桥接入口的第一轮收口。
- `Phase 13 = in_progress`，当前已完成 `Profile / Settings`、`Bookshelf / History / Watchlist / Comment`，并继续推进到 `Writer` 域的首轮收口。
- `Phase 14 = planned`，将承接 RN contract、registry、mock/fallback 与 maintainability 治理。

## 当前 repo 事实
- 当前仓库是 `React Native + Android Compose/Kotlin` 的混合工程。
- Android 模块图稳定为：`app + core-* + feature-* + macrobenchmark`。
- RN 主代码仍在 `src/**`，并通过 bridge / host glue 与 Android 通信。
- `Phase 12` 当前已确认：
  - `src/utils/runtime/**` 为运行时入口层
  - `src/utils/bridge/**` 为桥接包装层
  - `src/**` 中的 `NativeModules` / `DeviceEventEmitter` / `BackHandler` 已不再散落在页面与 store 中

## 当前主线入口
- [Stage 6 计划](./stage-6-phase-12-14-plan.md)
- [Phase 12 宿主文档](./phases/phase-12-rn-runtime-and-bridge-consolidation.md)
- [Phase 12 closeout assessment](./phase-12/phase-12-closeout-assessment.md)
- [Phase 13 宿主文档](./phases/phase-13-rn-page-domain-refactor.md)
- [Phase 13 Wave 1 记录](./phase-13/profile-settings-domain-wave-2026-03-31.md)
- [Phase 13 Wave 2-4 记录](./phase-13/bookshelf-comment-domain-wave-2026-03-31.md)
- [Phase 13 Wave 5 记录](./phase-13/writer-domain-wave-2026-03-31.md)
- [Phase 12-14 验证看板](./tracking/phase-12-14-validation-board.md)

## 已关闭阶段入口
- [Stage 4 closeout summary](./stage-4-closeout-summary.md)
- [Phase 7 closeout assessment](./phase-7/phase-7-closeout-assessment.md)
- [Phase 8 closeout assessment](./phase-8/phase-8-closeout-assessment.md)
- [Phase 7-8 验证看板](./tracking/phase-7-8-validation-board.md)
- [Stage 5 closeout summary](./stage-5-closeout-summary.md)
- [Phase 9 closeout assessment](./phase-9/phase-9-closeout-assessment.md)
- [Phase 10 closeout assessment](./phase-10/phase-10-closeout-assessment.md)
- [Phase 11 closeout assessment](./phase-11/phase-11-closeout-assessment.md)
- [Phase 9-11 验证看板](./tracking/phase-9-11-validation-board.md)

## 决策与回滚
- [master-roadmap.md](./master-roadmap.md)
- [decision-log.md](./tracking/decision-log.md)
- [rollback-index.md](./tracking/rollback-index.md)

## 使用规则
- 阶段状态变更时，必须先更新本目录下的 authority 文档。
- 然后同步：
  - `docs/harness/current-focus.md`
  - `docs/harness/session-log.md`
  - `docs/harness/generated/workspace-snapshot.md`
- 关闭结论以：
  - `README`
  - 当前阶段宿主文档
  - 当前 validation board
  三者一致为准。
