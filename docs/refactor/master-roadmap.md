# Novel 重构总路线图

## 0. 当前权威状态
- `Stage 3 = validated`
- `Stage 4 = validated`
- `Phase 7 = validated`
- `Phase 8 = validated`
- `Stage 5 = validated`
- `Phase 9 = validated`
- `Phase 10 = validated`
- `Phase 11 = validated`
- `Stage 6 = in_progress`
- `Phase 12 = validated`
- 默认下一主线：`Phase 13 = planned`

## 1. 项目现状摘要
- 当前仓库是 `React Native + Android Compose/Kotlin` 的混合架构小说应用。
- Android 侧已稳定为 `app + core-* + feature-* + macrobenchmark`。
- RN 侧主要位于 `src/**`，仍承载大量业务页面、store、hooks 与 bridge 交互。
- Stage 4 与 Stage 5 已完成 Android 结构、治理与长期维护层的阶段性收口。
- 当前新主线转向 RN：先收入口，再收页面域，最后收 contract / quality / maintainability。

## 2. Stage 切分
| Stage | Phase | 主题 | 当前状态 |
| --- | --- | --- | --- |
| Stage 1 | Phase 0-2 | 基线、发布安全、质量门禁 | `validated` |
| Stage 2 | Phase 3-4 | 基础设施收口、边界收口与超大类拆分 | `validated` |
| Stage 3 | Phase 5-6 | 模块化深化与性能专项治理 | `validated` |
| Stage 4 | Phase 7-8 | 包体积 / 依赖 / 构建效率 + observability / rollout / ADR | `validated` |
| Stage 5 | Phase 9-11 | 运行硬化、合规与供应链、数据质量与维护性 | `validated` |
| Stage 6 | Phase 12-14 | RN runtime、页面域、contract 与长期治理 | `in_progress` |

## 3. 当前活动阶段
### Phase 12
- 主题：RN 运行时与桥接收口
- 状态：`validated`
- 结果：
  - 建立 `src/utils/runtime/**`
  - 建立 `SettingsBridge`
  - 让 `appInit`、`themeStore`、`nativeEventListener` 与页面返回逻辑委派到统一 wrapper
  - 建立 `rawPrimitivesBoundary` 结构护栏测试

### Phase 13
- 主题：RN 页面域与 store/hook/component 边界重构
- 状态：`in_progress`
- 顺序：
  1. `Profile + app root preload`
  2. `Settings + TimeSwitch + privacy/help`
  3. `Bookshelf / History / Watchlist / Community`
  4. `Comment / ReviewDetail / WriteReview`
  5. `Writer / AIWriteAssistant / BookManage`
  6. `ScrollBox heavy pages`
- 当前已开始第一波：`Profile / Settings`
- 当前已推进到中段：`Bookshelf / History / Watchlist / Comment`
- 当前已推进到后段：`Writer / AIWriteAssistant / BookManage`
- 当前已推进到剩余 heavy pages：`Community / RecommendBook / MemberCenter / BecomeWriter`

### Phase 14
- 主题：RN contract、质量与 maintainability 收口
- 状态：`planned`
- 范围：
  - bridge contract tests / smoke
  - component registry consistency
  - mock / fallback / fail-closed catalog
  - naming / directory / state model guide

## 4. 历史阶段摘要
### Stage 4
- `Phase 7` 完成 size / dependency / build efficiency 第一轮治理闭环。
- `Phase 8` 完成 observability / flag / rollback / ADR 治理宿主落盘。

### Stage 5
- `Phase 9` 完成运行恢复、弱网 / 离线、Token 连续性与导入导出 / 历史恢复收口。
- `Phase 10` 完成无障碍、合规、供应链与双端协作治理宿主落盘。
- `Phase 11` 完成数据质量与可维护性治理宿主落盘。

## 5. 当前权威入口
- [README.md](./README.md)
- [Stage 6 计划](./stage-6-phase-12-14-plan.md)
- [Phase 12 宿主文档](./phases/phase-12-rn-runtime-and-bridge-consolidation.md)
- [Phase 12 closeout assessment](./phase-12/phase-12-closeout-assessment.md)
- [Phase 12-14 验证看板](./tracking/phase-12-14-validation-board.md)
- [decision-log.md](./tracking/decision-log.md)
- [rollback-index.md](./tracking/rollback-index.md)
