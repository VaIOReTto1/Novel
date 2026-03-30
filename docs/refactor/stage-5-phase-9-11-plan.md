# 第五阶段重构计划（Stage 5 = Phase 9-11）

## 摘要
- 第五阶段定义为：`Stage 5 = Phase 9 + Phase 10 + Phase 11`
- 当前状态：`planned`
- 默认推进顺序固定为：
  - `Phase 9` 运行可靠性与业务连续性
  - `Phase 10` 无障碍、合规、供应链与双端协作治理
  - `Phase 11` 数据质量与可维护性收敛
- 第五阶段不重开已关闭的 `Stage 4`，只承接蓝图中“遗漏优化点清单”的系统化宿主、验证与关闭标准。

## 当前控制面状态
- `Stage 4 = validated`
- `Stage 4 closeout` 继续以 `2026-03-30` 结论为准
- `Stage 5 = in_progress`
- `Phase 9 = validated`
- `Phase 10 = planned`
- `Phase 11 = planned`

## 阶段定位
- `Stage 4` 已经把包体积、依赖、构建效率、observability、rollback 和 ADR 治理宿主落盘。
- `Stage 5` 的目标不是重做前四阶段，而是把剩余“高用户风险 + 高长期维护成本”的遗漏项收成下一轮硬化计划。
- `Stage 5` 继续坚持：
  - 不改 route / Bridge payload / RN `componentName` 的既有对外语义
  - 不把长期治理混成新的架构大重写
  - 先宿主文档与验证矩阵，再推进实现
  - 每个子主题都必须有 `Rollback ID`

## 当前仓库入口基线
- `Stage 4` 已经提供可复用治理入口：
  - `phase-7` / `phase-8` closeout
  - `module-owner-matrix-2026-03-27.md`
  - `api-surface-review-checklist.md`
  - `rollback-index.md`
  - `decision-log.md`
- 当前已能复用的半成品入口包括：
  - `RefactorFeatureFlags`
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
  - `RequestIdInterceptor`
  - `Bridge schema / compatibility governance`
  - `production mock exit governance`
  - `ExportUserDataUseCase / ImportUserDataUseCase`
- 当前仍没有的平台级能力包括：
  - 统一 Crash / ANR 平台
  - 远程配置 / canary 平台
  - 自动 reviewer / CODEOWNERS 分发
  - 全仓 version catalog / BOM

## 进入条件
- `Stage 4 = validated`
- `Stage 4 closeout summary`、`README.md`、`phase-7-8-validation-board.md` 口径一致
- 当前关键验证命令持续可执行：
  - `npm run harness:check`
  - `npm test -- --runInBand`
  - `android/gradlew.bat app:testDebugUnitTest`
  - `android/gradlew.bat app:lintDebug`
  - `android/gradlew.bat app:compileDebugAndroidTestKotlin`
  - `android/gradlew.bat :macrobenchmark:assemble`

## 文档结构
- `docs/refactor/stage-5-phase-9-11-plan.md`
- `docs/refactor/phases/phase-9-runtime-resilience-and-continuity.md`
- `docs/refactor/phases/phase-10-accessibility-compliance-supply-chain.md`
- `docs/refactor/phases/phase-11-data-quality-and-maintainability.md`
- `docs/refactor/tracking/phase-9-11-validation-board.md`
- `docs/refactor/stage-5-closeout-summary.md`
- 公共宿主文档：
  - `docs/refactor/phase-9/runtime-resilience-matrix-2026-03-30.md`
  - `docs/refactor/phase-10/accessibility-audit-matrix-2026-03-30.md`
  - `docs/refactor/phase-10/bridge-schema-manifest-2026-03-30.md`
  - `docs/refactor/phase-10/rn-component-registry-2026-03-30.md`
  - `docs/refactor/phase-10/compliance-and-sensitive-log-governance-2026-03-30.md`
  - `docs/refactor/phase-10/supply-chain-audit-playbook-2026-03-30.md`
  - `docs/refactor/phase-11/error-empty-state-catalog-2026-03-30.md`
  - `docs/refactor/phase-11/naming-directory-state-model-guide-2026-03-30.md`
- 继续复用：
  - `docs/refactor/master-roadmap.md`
  - `docs/refactor/README.md`
  - `docs/refactor/stage-4-closeout-summary.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/rollback-index.md`

## Stage 5 协作编制规则
- `Phase 9`
  - 基线编制：`1 Leader + 4 helpers`
  - 默认角色：
    - `RuntimeResilienceAgent`
    - `LifecycleRecoveryAgent`
    - `WeakNetworkContinuityAgent`
    - `ContinuityEvidenceAgent`
- `Phase 10`
  - 基线编制：`1 Leader + 4 helpers`
  - 默认角色：
    - `AccessibilityAuditAgent`
    - `CompliancePolicyAgent`
    - `SupplyChainAuditAgent`
    - `BridgeRegistryAgent`
- `Phase 11`
  - 基线编制：`1 Leader + 3 helpers`
  - 默认角色：
    - `DataQualityExitAgent`
    - `EmptyStateCatalogAgent`
    - `MaintainabilityGuideAgent`

## 关键边界
### Phase 9
- 只处理运行可靠性、恢复与业务连续性
- 不在本阶段引入线上观测平台或远程配置
- 不把 Reader / Welfare / RN Host 的运行恢复问题混成新的模块化或性能专项

### Phase 10
- 只处理无障碍、合规、供应链与双端协作治理
- 不在本阶段承诺未存在的平台化能力
- Bridge schema / RN 组件名允许集中治理，不允许直接改既有对外契约

### Phase 11
- 只处理 Stage 5 剩余的 mock、fallback、空态、命名、目录、状态模型与错误文案
- 不在 Phase 9/10 边界未稳定前提前关目录或文案规范

## 阶段退出条件
- `V9-*`、`V10-*`、`V11-*` 全部达到 `green`
- `Stage 5 closeout summary` 文档闭环完成
- harness 快照与 current-focus 已切到对应口径

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`in_progress`
