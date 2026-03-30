# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 8`
- 阶段状态：`validated`
- 当前 Stage：`Stage 4 = Phase 7-8`
- 最新生效切换：`2026-03-30 Stage 4 closeout`
- 历史 checkpoint：`2026-03-21` 的 `Phase 5 = validated`、`Phase 6 = validated`、`Stage 3 = validated` 与 `2026-03-26` 的 `Stage 3 reopen closeout` 继续保留为历史记录；当前权威口径以 Stage 4 控制面为准

## 当前结论
- `Stage 3 = validated`，继续以 `2026-03-26` closeout 为收口事实。
- `Stage 4 = validated`，`Phase 7-8` 已完成当前计划内治理闭环。
- `Phase 7 = validated`，已完成 size / dependency / build efficiency 的第一轮治理闭环。
- `Phase 8 = validated`，已完成 observability / flag / rollback / ADR 治理宿主落盘。

## 当前模块与工程事实
- Android 模块图已稳定为 `app + core-* + feature-* + macrobenchmark`。
- `:app`
  - 仅承载 `Application / Activity / Navigation / RN module / route wrapper / host adapter`
- `:core-common`
  - 共享日志、MVI、domain、并发与基础适配
- `:core-ui`
  - 共享主题与通用 Compose 组件
- `:core-bridge`
  - 共享 bridge facade、state adapter、network gateway
- `:core-bridge-contract`
  - 共享 bridge delegate/contract
- `:core-storage`
  - 存储抽象与兼容层
- `:core-network`
  - 共享网络契约与执行器适配
- `:feature-home`
  - 首页根状态机与首页 feature 协调层
- `:feature-search`
  - 搜索首页/结果页根状态机与搜索 feature 协调层
- `:feature-login`
  - 登录根状态机与登录 feature 协调层
- `:feature-book`
  - 书籍详情根状态机与书详情 feature 协调层
- `:feature-reader`
  - 阅读器根状态机、Reader 协调层与稳定 gateway contract
- `:feature-rn-host`
  - RN host 页面内容、Settings 主状态层与宿主 contract
- `:feature-welfare`
  - welfare feature 主状态层与页面内容
- Stage 4 相关基线：
  - release 已开启 `minifyEnabled true` 与 `shrinkResources true`
  - `android/gradle/verification-metadata.xml` 已存在
  - `android/gradle/libs.versions.toml` 仍缺失
  - `org.gradle.configuration-cache=false`
  - 已有本地 `RefactorFeatureFlags`、`StartupPerformanceMonitor`、`WelfarePerformanceMonitor`
  - 尚无统一的 Crash / ANR / 灰度平台

## 关键入口
- [Stage 4 计划](./stage-4-phase-7-8-plan.md)
- [Phase 7 宿主文档](./phases/phase-7-size-dependency-build-governance.md)
- [Phase 7 closeout assessment](./phase-7/phase-7-closeout-assessment.md)
- [Phase 8 宿主文档](./phases/phase-8-observability-rollout-governance.md)
- [Phase 8 closeout assessment](./phase-8/phase-8-closeout-assessment.md)
- [Stage 4 closeout summary](./stage-4-closeout-summary.md)
- [Phase 7-8 验证看板](./tracking/phase-7-8-validation-board.md)
- [Stage 3 closeout summary](./stage-3-closeout-summary.md)
- [Phase 5 当前模块图](./phase-5/module-graph-current-state.md)
- [决策日志](./tracking/decision-log.md)
- [回滚索引](./tracking/rollback-index.md)

## 历史文档
- [Stage 3 计划（已关闭阶段参考）](./stage-3-phase-5-6-plan.md)
- [Phase 5-6 验证看板](./tracking/phase-5-6-validation-board.md)
- [Phase 5 closeout 评估](./phase-5/phase-5-closeout-assessment.md)
- [Phase 5 模块验证矩阵（2026-03-21 checkpoint）](./phase-5/module-verification-matrix-2026-03-21.md)
- [Phase 5 模块验证矩阵（2026-03-23 reopen 中间态）](./phase-5/module-verification-matrix-2026-03-23.md)
- [Phase 5 host-compat（2026-03-21 checkpoint）](./phase-5/host-compat-validation-2026-03-21.md)

## 使用规则
- 阶段状态更新时，必须同步更新本文件、阶段宿主文档、验证看板、decision log 与 rollback index。
- 当前主线状态以 `README + 当前 Stage 宿主文档 + 当前 validation board` 三者一致为准。
- 已关闭阶段继续保留历史追溯价值，但不能覆盖当前 authority 口径。
