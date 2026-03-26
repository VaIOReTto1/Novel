# Novel 重构控制面板

## 当前状态
- 当前阶段：`Phase 5`
- 阶段状态：`validated`
- 最新 closeout：`2026-03-26 reopen closeout`
- 历史 checkpoint：`2026-03-21` 的 `Phase 5 = validated`、`Phase 6 = validated`、`Stage 3 = validated` 保留为历史记录；当前权威口径以后续 reopen 收口结果为准

## 当前结论
- `Phase 5` 已完成 reopen 高风险收口，`app` 仅保留 Android 强制入口、route/page wrapper 和极薄 host adapter。
- `Stage 3 = Phase 5-6` 重新回到 `validated`。
- `Phase 6` 继续保持 `validated`，本轮未引入新的性能回归证据。
- `Phase 7` 继续保持 `planned`，尚未启动。

## 当前模块事实
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

## 关键入口
- [Phase 5 当前模块图](./phase-5/module-graph-current-state.md)
- [Phase 5 当前验证矩阵](./phase-5/module-verification-matrix-2026-03-26.md)
- [Phase 5 当前 host-compat 验证](./phase-5/host-compat-validation-2026-03-26.md)
- [Phase 5 closeout 评估](./phase-5/phase-5-closeout-assessment.md)
- [Phase 5-6 验证看板](./tracking/phase-5-6-validation-board.md)
- [决策日志](./tracking/decision-log.md)
- [回滚索引](./tracking/rollback-index.md)
- [Stage 3 closeout summary](./stage-3-closeout-summary.md)

## 历史文档
- [Phase 5 模块验证矩阵（2026-03-21 checkpoint）](./phase-5/module-verification-matrix-2026-03-21.md)
- [Phase 5 模块验证矩阵（2026-03-23 reopen 中间态）](./phase-5/module-verification-matrix-2026-03-23.md)
- [Phase 5 host-compat（2026-03-21 checkpoint）](./phase-5/host-compat-validation-2026-03-21.md)

## 使用规则
- 阶段状态更新时，必须同步更新本文件、模块图、验证看板、decision log 与 rollback index。
- reopen closeout 之后，新的事实以最新日期文档为准，旧 checkpoint 只保留历史追溯价值。
