# 第三阶段重构计划（Stage 3 = Phase 5-6）

## 摘要
- 第三阶段建议明确为：`Stage 3 = Phase 5 + Phase 6`
- `Phase 5`：真正的 Gradle 模块化准备与首批模块拆分
- `Phase 6`：性能专项与基准治理
- 不纳入：
  - `Phase 7` 的包体积/依赖/构建效率专项
  - `Phase 8+` 的治理、灰度与长期机制建设

## 阶段定位
- `Stage 2` 已经把基础设施、边界和超大类职责收到了可模块化的程度。
- `Stage 3` 的合理顺序应当是：
  - 先做 `Phase 5`，把稳定边界落成真正的 Gradle 模块图
  - 再做 `Phase 6`，在新模块边界上建立性能预算、baseline profile 和专项回归
- `Phase 7` 不适合并入本阶段，因为包体积/依赖/构建效率治理需要在模块图稳定后再做，避免前后基线持续漂移。

## 进入条件
- `Stage 2 = validated`
- `Phase 5 entry checklist` 已落盘并作为唯一入口约束：
  - `docs/refactor/phase-4/phase-5-entry-checklist.md`
- 当前关键回归命令持续可执行：
  - `npm test -- --runInBand`
  - `android/gradlew.bat app:testDebugUnitTest`
  - `android/gradlew.bat app:lintDebug`
  - `android/gradlew.bat app:compileDebugAndroidTestKotlin`
  - `android/gradlew.bat :macrobenchmark:assemble`

## 文档结构
- `docs/refactor/stage-3-phase-5-6-plan.md`
- `docs/refactor/phases/phase-5-gradle-modularization.md`
- `docs/refactor/phases/phase-6-performance-governance.md`
- `docs/refactor/tracking/phase-5-6-validation-board.md`
- `docs/refactor/tracking/stage-3-static-debt-baseline.md`
- `docs/refactor/stage-3-closeout-summary.md`
- 继续复用：
  - `docs/refactor/master-roadmap.md`
  - `docs/refactor/README.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/subagent-dispatch-log.md`
  - `docs/refactor/tracking/rollback-index.md`

## Stage 3 协作编制规则
- `Phase 5`
  - 基线编制：`1 Leader + 5 helpers`
  - 默认角色：
    - `ModuleGraphAgent`
    - `CoreModularizationAgent`
    - `FeatureExtractionAgentA`
    - `FeatureExtractionAgentB`
    - `BuildIntegrationGuardAgent`
- `Phase 6`
  - 基线编制：`1 Leader + 3 helpers`
  - 默认角色：
    - `StartupBudgetAgent`
    - `ScrollReaderPerfAgent`
    - `WebViewBridgePerfAgent`

## 关键边界
### Phase 5
- 目标是“真正模块化”，不是继续停留在单 `app` 模块内逻辑分包
- 先拆稳定基础层和稳定 feature
- Reader 仍然后置，不在本阶段做最终模块拆分

### Phase 6
- 目标是“建立稳定的性能预算与回归体系”
- 先做 baseline，再做优化，再做持续门禁
- 不把性能专项混成新的架构大重写

## 静态债策略
- 第三阶段继承第二阶段收敛结果作为基线：
  - `RN lint errors = 0`
  - `RN lint warnings = 953`
  - `detekt weighted issues = 1901`
- 第三阶段默认目标：
  - 不回退
  - 不新增 touched-files 红项
  - 不把静态债治理重新膨胀成主线之外的噪音

## 阶段退出条件
- `Phase 5`、`Phase 6` 全部验证项达到 `green`
- `Stage 3` closeout 文档闭环完成
- 控制面板可切换到下一阶段

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`planned`
