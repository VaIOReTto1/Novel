# Phase 5 - Gradle 模块化与边界搬迁

## 目标
- 把 `Stage 2` 已稳定下来的包边界落成真正的 Gradle 模块图
- 先拆稳定基础层，再拆稳定 feature，保持 app 主入口可用
- 为 `Phase 6` 的性能预算建立更清晰的模块级观测边界

## 范围
- 根工程模块图设计
- 首批 `core/*` 模块落地
- 首批 `feature/*` 模块落地
- `app` 模块瘦身为组合入口
- 模块级测试/构建/依赖边界收口

## 非目标
- 不做 Reader 最终模块拆分
- 不改 route / Bridge payload / RN 组件名
- 不把所有 RN heavy pages 一次性去 mock
- 不做 `Phase 6` 的性能专项实现

## 进入条件
- `Stage 2 = validated`
- `docs/refactor/phase-4/phase-5-entry-checklist.md` 已接受为唯一 carried debt 入口
- `app` 仍可稳定通过当前核心回归命令

## 当前真实进度
- 打开书籍“请求错误”运行时 blocker 已修复，`BookService` 的空 `chapterUpdateTime` 回归测试已作为 Phase 5 首个守门用例保留。
- `android/gradle/android-library-common.gradle` 已落地，`core/*` 模块开始复用统一 Android library 构建约定。
- `android/core-common` 已落地并完成首轮共享基础抽离。
- `android/core-storage` 已落地并完成模块级单测。
- `android/core-network` 已以“契约优先”方式落地首批抽离，当前仍需继续向共享基础设施深化。
- `android/core-bridge-contract` 已落地并完成第一批纯桥接 delegate/helper 抽离。
- `android/feature-welfare` 已落地两轮低风险切口，当前以 app wrapper + feature 内部组件/工具共存方式推进。
- `android/feature-search` 已落地首轮最小切口，当前只迁出低耦合存储层。
- `android/feature-home` 已落地首轮最小切口，当前只迁出低耦合性能 helper。
- `Phase 5` 已从 `planned` 切换为 `in_progress`，后续不得再按纯文档准备态解释本阶段。

## 固定执行顺序
1. `doc/state sync`
2. `build conventions`
3. `core-common`
4. `deepen core-network`
5. `core-bridge-contract`
6. `feature-welfare`
7. `feature-search`
8. `feature-home`
9. `feature-rn-host`
10. `validation / closeout`

## 协作编制
### Leader Mode
- `single leader / five helpers`

### Base Helper Count
- `5`

### Scale-Up Triggers
- `core-common` 与 `core-bridge-contract` 已稳定后，可并行推进 `feature-welfare` 与第二个 feature 模块。
- 构建约定、模块图证据、功能模块搬迁三条线已经无锁冲突时，可短期加大发散探索和代码准备并行度。

### Scale-Down Triggers
- 当前只做文档纠偏、看板同步或单一 `core/*` 模块切口时，不必同时启用全部 helpers。
- 若 `app:testDebugUnitTest`、`lintDebug` 或模块级编译出现高优先级回归，优先收缩到 `Leader + build guard + 当前锁持有者`。

### Agent Roster
- `ModuleGraphAgent`
  - 模块图、依赖方向、settings.gradle 与 build graph
- `CoreModularizationAgent`
  - `core/*` 模块抽离与公共 API 稳定
- `FeatureExtractionAgentA`
  - `feature-home / feature-search`
- `FeatureExtractionAgentB`
  - `feature-welfare / feature-rn-host`
- `BuildIntegrationGuardAgent`
  - 模块集成、测试矩阵、lint/detekt/assemble 护栏

### Lock Strategy
- `LOCK-MODULE-GRAPH`
- `LOCK-CORE-MODULES`
- `LOCK-FEATURE-A`
- `LOCK-FEATURE-B`
- `LOCK-BUILD-INTEGRATION`

### Retry Window
- 模块级代码切口：`2` 次最小修复重试
- Gradle / verification metadata / dependency verification：`2` 次串行重试
- 若第二次后仍失败，必须升级为 blocker 并回到上一个稳定原子提交点分析

### Escalation Window
- route / Bridge payload / RN 组件名需要变化
- Reader 范围被迫提前进入真正模块化
- Hilt / resources / manifest 聚合问题导致无法维持 `app` 作为稳定组合入口
- `V5-04` 的 Bridge / RN Host 兼容证据出现不可解释回退

### Leader-only Actions
- 调整阶段状态
- 写 tracking / closeout / README
- 执行最终合并、回滚和 Gradle 串行验证

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P5.1 | 固定目标模块图与依赖规则 | 模块边界、依赖方向、禁止环依赖规则落盘 |
| P5.2 | 建立首批构建约定 | `settings.gradle`、公共 convention、依赖共享策略稳定 |
| P5.3 | 抽离 `core-common` | 公共 Kotlin/Compose/utility 稳定落入基础模块 |
| P5.4 | 抽离 `core-network` 与 `core-storage` | `NetworkFacade` / `StorageFacade` 不再依赖 `app` |
| P5.5 | 抽离 `core-bridge-contract` | Bridge contract、host contract 与兼容层稳定 |
| P5.6 | 抽离 `feature-home` | 首页相关实现从 `app` 迁入 feature 模块 |
| P5.7 | 抽离 `feature-search` | 搜索相关实现从 `app` 迁入 feature 模块 |
| P5.8 | 抽离 `feature-welfare` / `feature-rn-host` | 福利、RN Host 相关实现进入稳定模块 |
| P5.9 | 模块级测试与 lint/detekt 矩阵补齐 | 模块级 build/test/lint 命令可追溯 |
| P5.10 | 输出 Phase 6 进入条件 | 性能专项的模块级 baseline 入口清晰 |

## 交付物
- `settings.gradle` 模块图
- 首批 `core/*` 模块
- 首批 `feature/*` 模块
- 模块依赖图与迁移映射
- 模块级测试/构建命令清单

## 硬阈值
- `app` 模块不再继续膨胀为功能实现主仓
- 不允许出现新模块环依赖
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`
- repo 级：
  - `RN lint errors = 0`
  - `detekt weighted issues <= 1901`

## 风险与回滚
- 风险：
  - 模块拆分导致 Hilt / resources / manifest 聚合问题
  - RN Host、Bridge、Native 之间跨模块契约抖动
  - 构建脚本改动导致 CI / 本地构建矩阵失稳
- 回滚：
  - 每个模块搬迁按单主题原子提交
  - 若某次搬迁导致集成回归，直接回到上一个稳定模块提交

## 检验计划
- `V5-01` 模块图与依赖规则稳定
- `V5-02` 首批 `core/*` 模块稳定
- `V5-03` 首批 `feature/*` 模块稳定
- `V5-04` Bridge / RN Host 跨模块兼容稳定
- `V5-05` 模块级 build/test/lint 矩阵稳定
- `V5-06` Phase 6 进入条件明确

## 退出条件
- `V5-*` 全绿
- `Phase 6` 可在新模块边界上直接启动

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`in_progress`
