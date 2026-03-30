# 第六阶段重构计划（Stage 6 = Phase 12-14）

## 摘要
- 第六阶段定义为：`Stage 6 = Phase 12 + Phase 13 + Phase 14`
- 当前状态：`in_progress`
- 当前阶段状态固定为：
  - `Stage 5 = validated`
  - `Stage 6 = in_progress`
  - `Phase 12 = validated`
  - `Phase 13 = in_progress`
  - `Phase 14 = planned`
- 当前默认执行主线为：`Phase 13 = RN 页面域重构`

## 当前结论
- `Phase 12` 已完成第一轮关闭，RN 运行时入口、bridge gateway、event hub 与 back navigation 已形成 repo-local 宿主与最小自动化护栏。
- `src/**` 中的 `NativeModules` / `DeviceEventEmitter` / `BackHandler` 已收口到约定 wrapper：
  - `src/utils/bridge/**`
  - `src/utils/runtime/eventHub.ts`
  - `src/utils/runtime/backNavigation.ts`
- `App.tsx / index.js / src/utils/appInit.ts` 的入口职责已开始从旧兼容层拆向：
  - `runtimeCoordinator`
  - `componentRegistry`
  - `pageStateCache`
  - `preload`
- 现有对外契约未改动：
  - `route`
  - bridge payload
  - RN `componentName`
  - native module name

## Stage 6 定位
- 本阶段不重开 `Stage 1-5` 的已关闭结论。
- 本阶段不是 RN 重写计划，而是把 `src/**` 从“页面 + store + bridge + runtime 混杂单体”收成：
  - 可维护的运行时入口层
  - 清晰的页面域边界
  - 可追溯的 contract / smoke / 质量护栏

## 已落地的 Phase 12 事实
- 新增运行时宿主：
  - `src/utils/runtime/runtimeCoordinator.ts`
  - `src/utils/runtime/componentRegistry.ts`
  - `src/utils/runtime/eventHub.ts`
  - `src/utils/runtime/backNavigation.ts`
  - `src/utils/runtime/pageStateCache.ts`
  - `src/utils/runtime/preload.ts`
- 新增桥接包装：
  - `src/utils/bridge/SettingsBridge.ts`
- 兼容层已改为委派：
  - `src/utils/appInit.ts`
  - `src/utils/nativeEventListener.ts`
  - `src/utils/theme/themeStore.ts`
  - `src/utils/bridge/NavigationBridge.ts`
- 已完成首轮页面 / store 直连收口样本：
  - `Profile / Category / Settings / TimeSwitch`
  - `History / Message / MyReservation / RecommendBook / MemberCenter / ViewedUsers / BecomeWriter`
  - `FeedbackHelp`
  - `Comment / ReviewDetail / WriteReview`
  - `Writer / AIWriteAssistant / BookManage / WritePage`

## 后续阶段边界
### Phase 13
- 只处理页面域边界重构：
  - `store / hooks / components / types / styles`
- 默认顺序不变：
  1. `Profile + app root preload`
  2. `Settings + TimeSwitch + privacy/help`
  3. `Bookshelf / History / Watchlist / Community`
  4. `Comment / ReviewDetail / WriteReview`
  5. `Writer / AIWriteAssistant / BookManage`
  6. `ScrollBox heavy pages`
- 当前已开始第一波：
  - `Profile + app root preload`
  - `Settings + TimeSwitch + privacy/help`

### Phase 14
- 只处理 RN 长期治理层：
  - bridge contract tests
  - component registry consistency
  - mock / fallback / fail-closed catalog
  - naming / directory / state model guide

## 验证入口
- `npm test -- --runInBand __tests__/runtime/backNavigation.test.ts __tests__/runtime/eventHub.test.ts __tests__/runtime/runtimeCoordinator.test.ts __tests__/runtime/rawPrimitivesBoundary.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/UserBridge.contract.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`
- `npm run harness:check`

## 权威入口
- [Phase 12 宿主文档](./phases/phase-12-rn-runtime-and-bridge-consolidation.md)
- [Phase 12 closeout assessment](./phase-12/phase-12-closeout-assessment.md)
- [Phase 13 宿主文档](./phases/phase-13-rn-page-domain-refactor.md)
- [Phase 13 Wave 1 记录](./phase-13/profile-settings-domain-wave-2026-03-31.md)
- [Phase 12-14 验证看板](./tracking/phase-12-14-validation-board.md)
- [Stage 6 closeout summary](./stage-6-closeout-summary.md)

## 备注
- `Stage 6` 当前仍处于执行中，不代表 `Phase 13` / `Phase 14` 已开始实施。
- 后续只要阶段状态继续变化，必须同步更新：
  - `docs/refactor/README.md`
  - `docs/refactor/tracking/phase-12-14-validation-board.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/rollback-index.md`
  - `docs/harness/current-focus.md`
