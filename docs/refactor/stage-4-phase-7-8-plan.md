# 第四阶段重构计划（Stage 4 = Phase 7-8）

## 摘要
- 第四阶段定义为：`Stage 4 = Phase 7 + Phase 8`
- 当前已关闭前半段：`Phase 7 = validated`
- 当前后半段结果：`Phase 8 = validated`
- 第四阶段不重开 `Phase 5-6` 的模块化与性能 debt，只承接：
  - 包体积治理
  - 依赖治理
  - 构建效率治理
  - 可观测性治理
  - rollout / kill switch / ADR / reviewer / owner 等长期机制建设

## 当前控制面状态
- `Stage 3 = validated`
- `Stage 3 closeout` 继续以 `2026-03-26` 结论为准
- `Stage 4 = validated`
- `Phase 7 = validated`
- `Phase 8 = validated`

## 阶段定位
- 第三阶段已经把 Android 模块图稳定为 `app + core-* + feature-* + macrobenchmark`，`app` 保持 thin-app 组合入口。
- 第四阶段的目标不是继续搬模块，也不是重做性能专项，而是把当前已经存在但尚未制度化的工程能力收成长期门禁。
- 第四阶段继续坚持：
  - 不改 UI 语义
  - 不改业务功能语义
  - 不借治理名义重开架构大改
  - 全程可逆，优先先建基线、diff、registry、playbook，再推进优化

## 当前仓库入口基线
- 模块图以 `android/settings.gradle` 为准，当前模块集合已经稳定。
- `android/app/build.gradle` 已开启 release `minifyEnabled true` 与 `shrinkResources true`，Stage 4 在此基础上继续做 size baseline / diff，而不是重写 Phase 1。
- `android/gradle/verification-metadata.xml` 已存在，说明 Gradle 依赖校验已有入口，但尚未上升为完整供应链治理体系。
- `android/gradle/libs.versions.toml` 仍不存在，依赖与插件版本仍分散在 `android/build.gradle`、`android/app/build.gradle` 与各模块脚本中。
- `android/gradle.properties` 仍保持 `org.gradle.configuration-cache=false`，Stage 4 需要把“为什么没开”“哪些任务阻塞”收敛成显式结论。
- 仓库已有本地治理起点：
  - `RefactorFeatureFlags`
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
- 仓库当前仍缺统一的 Crash / ANR / rollout / remote config / kill switch 平台级治理入口，Phase 8 只承接“制度化与宿主文档”，不假装这些平台已经存在。

## 进入条件
- `Stage 3 = validated`
- `Stage 3 closeout summary`、`Phase 5-6 validation board` 与当前模块图口径一致
- 当前关键验证命令持续可执行：
  - `npm run harness:check`
  - `npm test -- --runInBand`
  - `android/gradlew.bat app:testDebugUnitTest`
  - `android/gradlew.bat app:lintDebug`
  - `android/gradlew.bat app:compileDebugAndroidTestKotlin`
  - `android/gradlew.bat :macrobenchmark:assemble`

## 文档结构
- `docs/refactor/stage-4-phase-7-8-plan.md`
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/phases/phase-8-observability-rollout-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
- 当前 closeout 入口：
  - `docs/refactor/stage-4-closeout-summary.md`
  - `docs/refactor/phase-7/phase-7-closeout-assessment.md`
  - `docs/refactor/phase-8/phase-8-closeout-assessment.md`
- 继续复用：
  - `docs/refactor/master-roadmap.md`
  - `docs/refactor/README.md`
  - `docs/refactor/stage-3-closeout-summary.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/subagent-dispatch-log.md`
  - `docs/refactor/tracking/rollback-index.md`

## Stage 4 协作编制规则
- `Phase 7`
  - 基线编制：`1 Leader + 3 helpers`
  - 默认角色：
    - `SizeBudgetAgent`
    - `DependencyGraphAgent`
    - `BuildEfficiencyAgent`
- `Phase 8`
  - 基线编制：`1 Leader + 3 helpers`
  - 默认角色：
    - `ObservabilityAgent`
    - `GovernanceAdrAgent`
    - `RolloutFlagAgent`
- Stage 4 内任何新任务都必须先确认：
  - helper 是否只持有单锁
  - 是否会把 `Phase 7` 与 `Phase 8` 意外并行打开
  - 是否存在必须由 leader 串行完成的验证或控制面写入

## 关键边界
### Phase 7
- 目标是“把包体积、依赖与构建效率变成长期门禁对象”
- 优先盘清：
  - size baseline / artifact diff
  - Gradle / npm dependency inventory
  - build hot path / clean vs incremental baseline
  - configuration cache 关闭原因
- 不在 Phase 7 中重开：
  - 模块结构大迁移
  - Reader / Search / Welfare 的更深性能专项
  - UI 或协议语义变更
- 当前关闭事实：
  - `V7-01 ~ V7-05` 已全部达到当前约定状态
  - closeout 入口见 `docs/refactor/phase-7/phase-7-closeout-assessment.md`

### Phase 8
- 目标是“把现有局部治理能力升格成长期控制面”
- 以已有 `RefactorFeatureFlags` 与局部 monitor 为起点，建立：
  - 指标目录
  - flag / kill switch registry
  - rollout / rollback playbook
  - ADR / reviewer / owner 机制
- 不在 Phase 8 中假装已经拥有：
  - 统一 Crash 平台
  - 统一 ANR 平台
  - 远程配置平台
  - 完整线上灰度系统
- 当前关闭事实：
  - `V8-01 ~ V8-05` 已全部达到当前约定状态
  - closeout 入口见 `docs/refactor/phase-8/phase-8-closeout-assessment.md`

## 阶段退出条件
- `Phase 7` 与 `Phase 8` 的验证项全部达到约定状态
- 第四阶段 closeout 文档闭环完成
- 控制面板可以切换到后续阶段或长期维护模式

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`validated`
