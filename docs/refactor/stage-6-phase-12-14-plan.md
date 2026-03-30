# 第六阶段重构计划（Stage 6 = Phase 12-14）

## 摘要
- 第六阶段定义为：`Stage 6 = Phase 12 + Phase 13 + Phase 14`
- 当前状态：`planned`
- 推进顺序固定为：
  - `Phase 12` RN 运行时与桥接收口
  - `Phase 13` RN 页面域与 store/hook/组件边界重构
  - `Phase 14` RN 契约、数据质量与可维护性收口
- 第六阶段不重开已关闭的 `Stage 1-5`；它是当前 Android 线之后的 RN 重构主线。

## 当前控制面状态
- `Stage 5 = validated`
- `Stage 5 closeout` 继续以 `2026-03-30` 结论为准
- `Stage 6 = planned`
- `Phase 12 = planned`
- `Phase 13 = planned`
- `Phase 14 = planned`

## 阶段定位
- 当前 `src/**` 已形成独立的大型 RN 工程面：
  - 约 `323` 个文件
  - 约 `23` 个 store
  - 约 `23` 个 RN 页面注册入口
  - 约 `27` 个 hooks
- 当前 RN 侧不是“没有架构”，而是“入口、bridge、native event、page/store/hook 边界同时存在分散和重复”。
- 第六阶段的目标不是 RN -> Compose 重写，而是把 RN 收成：
  - 可维护的运行时入口层
  - 清晰的页面域边界
  - 可追溯的契约、测试与长期治理宿主

## 当前仓库入口基线
- 运行时入口：
  - `App.tsx`
  - `index.js`
  - `src/utils/appInit.ts`
- 稳定 bridge 包装层：
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`
- 现有原生事件入口：
  - `src/utils/nativeEventListener.ts`
  - `src/utils/theme/themeStore.ts`
- 当前问题集中点：
  - `src/utils/appInit.ts` 同时承载初始化、页面状态缓存、主题同步、用户预加载、设置预加载和页面注册
  - 多个页面和 store 仍直接使用 `NativeModules` / `DeviceEventEmitter` / `BackHandler`
  - 页面域内部 store/hook/component/types/styles 边界不统一
  - RN contract / smoke 仍不足以覆盖页面域重构

## 进入条件
- `Stage 5 = validated`
- `docs/refactor/README.md`、`stage-5-closeout-summary.md`、`phase-9-11-validation-board.md` 口径一致
- 当前关键验证命令持续可执行：
  - `npm test -- --runInBand`
  - `npm run lint`
  - `npm run harness:check`

## 文档结构
- `docs/refactor/stage-6-phase-12-14-plan.md`
- `docs/refactor/phases/phase-12-rn-runtime-and-bridge-consolidation.md`
- `docs/refactor/phases/phase-13-rn-page-domain-refactor.md`
- `docs/refactor/phases/phase-14-rn-contract-quality-and-maintainability.md`
- `docs/refactor/tracking/phase-12-14-validation-board.md`
- `docs/refactor/stage-6-closeout-summary.md`
- 公共宿主文档：
  - `docs/refactor/phase-12/rn-runtime-coordinator-2026-03-31.md`
  - `docs/refactor/phase-12/rn-bridge-gateway-2026-03-31.md`
  - `docs/refactor/phase-12/rn-event-hub-2026-03-31.md`
  - `docs/refactor/phase-12/rn-back-navigation-policy-2026-03-31.md`
  - `docs/refactor/phase-13/rn-domain-guide-2026-03-31.md`
  - `docs/refactor/phase-14/rn-contract-quality-host-2026-03-31.md`
  - `docs/refactor/phase-14/rn-component-registry-consistency-2026-03-31.md`
  - `docs/refactor/phase-14/rn-mock-fallback-catalog-2026-03-31.md`
- 继续复用：
  - `docs/refactor/master-roadmap.md`
  - `docs/refactor/README.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/rollback-index.md`
  - `docs/harness/current-focus.md`

## Stage 6 协作编制规则
- `Phase 12`
  - 基线编制：`1 Leader + 4 helpers`
  - 默认角色：
    - `RnRuntimeCoordinatorAgent`
    - `RnBridgeGatewayAgent`
    - `RnEventHubAgent`
    - `RnNavigationPolicyAgent`
- `Phase 13`
  - 基线编制：`1 Leader + 5 helpers`
  - 默认角色：
    - `ProfileRootAgent`
    - `SettingsDomainAgent`
    - `BookshelfDomainAgent`
    - `CommentDomainAgent`
    - `WriterDomainAgent`
- `Phase 14`
  - 基线编制：`1 Leader + 4 helpers`
  - 默认角色：
    - `RnContractTestAgent`
    - `RnRegistryAgent`
    - `RnDataQualityAgent`
    - `RnMaintainabilityAgent`

## 关键边界
### Phase 12
- 只处理运行时与桥接收口
- 允许最小 Android 触达，但只限 host / bridge glue
- 不改 route、payload、`componentName` 语义

### Phase 13
- 按页面域重构 store / hooks / components / types / styles
- 不允许一次性全仓目录重排
- 每次只围绕一个页面域关闭一类边界问题

### Phase 14
- 只处理 RN 契约、测试、registry、mock/fallback、命名/目录/状态模型
- 不再把运行时入口问题带回这一阶段

## 阶段退出条件
- `V12-*`、`V13-*`、`V14-*` 全部达到 `green`
- `Stage 6 closeout summary` 文档闭环完成
- harness 与 generated snapshot 已同步

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`planned`
