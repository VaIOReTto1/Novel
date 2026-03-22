# Phase 5 - Gradle 模块化与边界搬迁

## 目标
- 把 `Stage 2` 已稳定下来的包边界落成真正的 Gradle 模块图
- 继续深化已有首轮切口，并补齐 `core-ui / core-bridge / feature-book / feature-login / feature-reader`
- 先拆稳定基础层，再拆稳定 feature，保持 app 主入口可用
- 为 `Phase 6` 的性能预算建立更清晰的模块级观测边界

## 范围
- 根工程模块图设计
- 首批 `core/*` 模块落地
- 首批 `feature/*` 模块落地
- `app` 模块瘦身为组合入口
- 模块级测试/构建/依赖边界收口

## 非目标
- 不拆 `MainApplication / ComposeMainActivity / NavigationUtil / NavViewModel / route graph / RN host roots`
- 不改 route / Bridge payload / RN 组件名
- 不把模块化扩张成新的 UI / 业务语义重写
- 不做 `Phase 6` 的性能专项实现

## 进入条件
- `Stage 2 = validated`
- `docs/refactor/phase-4/phase-5-entry-checklist.md` 已接受为唯一 carried debt 入口
- `app` 仍可稳定通过当前核心回归命令

## 当前真实进度
- `2026-03-21` 已完成首轮 `Phase 5 validated` checkpoint，但当前模块图仍明显偏浅，`feature-book / feature-login / feature-reader` 虽已落地但仍未进入完整功能层迁移阶段，`core-network / core-bridge` 也仍处于继续深化阶段。
- 打开书籍“请求错误”运行时 blocker 已修复，`BookService` 的空 `chapterUpdateTime` 回归测试已作为 Phase 5 首个守门用例保留。
- `android/gradle/android-library-common.gradle` 已落地，`core/*` 模块开始复用统一 Android library 构建约定。
- `android/core-common` 已落地并完成首轮共享基础抽离。
- `android/core-ui` 已落地并接管主题体系、尺寸/点击工具与首批基础 Compose 组件。
- `android/core-storage` 已落地并完成模块级单测。
- `android/core-network` 已从“契约优先”继续推进到 request trace、Gson immutable adapter 等共享网络基础件落地，当前仍需继续向更完整的共享基础设施深化。
- `android/core-bridge` 已落地并接管桥接 MVI 契约、共享 reducer、Promise error mapper、bridge facade 与 network gateway。
- `android/core-bridge-contract` 已落地并完成第一批纯桥接 delegate/helper 抽离。
- `android/feature-welfare` 已落地两轮低风险切口，当前以 app wrapper + feature 内部组件/工具共存方式推进。
- `android/feature-search` 已从“单存储切口”深化到承载搜索结果页 trigger source、查询参数和性能/重试协调器。
- `android/feature-home` 已从“单 helper 模块”深化到承载首页分类语义契约与 restore 判定协调器，并配合 app 侧修复首页首开自动加载问题。
- `android/feature-rn-host` 已从“单存储切口”深化到承载主题补发与冷热路径追踪协调器。
- `android/feature-welfare` 也已进一步接管 bootstrap 与 WebView 性能协调器。
- `android/feature-book` 已落地并接管书籍详情的 MVI 契约与通用格式化工具。
- `android/feature-login` 已落地并接管登录页的 MVI 契约、reducer 与状态更新器。
- `android/feature-reader` 已落地并接管阅读器启动/恢复/动作 trace 协调器。
- Community 页现已补齐评论详情跳转、搜索/通知/发布桥接与原生分享，`handleUserPress / handleSubscribe` 也已改为显式 deferred，而非继续留死 TODO。
- `Phase 5` 当前已由历史 closeout checkpoint 重新切回 `in_progress`，后续不得再按“首轮最小切口已完成”误判为蓝图目标已兑现。

## 固定执行顺序
1. `doc/state sync`
2. `core-ui`
3. `deepen core-network / core-bridge`
4. `feature-home + homepage first-load fix`
5. `feature-search`
6. `feature-welfare / feature-rn-host`
7. `feature-book / feature-login`
8. `feature-reader`
9. `Community wiring + validation refresh`
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
| P5.1 | 重开 `Phase 5 / Stage 3` 并固定目标模块图 | 当前阶段状态、模块边界、依赖方向与历史 checkpoint 口径统一 |
| P5.2 | 新增 `core-ui` | 通用主题与基础 Compose 组件脱离 `app` |
| P5.3 | 深化 `core-network / core-bridge` | 共享网络原语、桥接共享层与宿主 glue 边界更清晰 |
| P5.4 | 深化 `feature-home` 并修复首页首开 | 首页实现迁入 feature 模块，首开无需手动刷新 |
| P5.5 | 深化 `feature-search` | 搜索页面、viewmodel、repository 与测试迁出 `app` |
| P5.6 | 深化 `feature-welfare / feature-rn-host` | Welfare 与 RN Host 支撑层继续迁入 feature 模块 |
| P5.7 | 新增 `feature-book / feature-login` | 书籍详情与登录 feature 真正进入独立模块 |
| P5.8 | 新增 `feature-reader` | Reader 进入独立 feature 模块，但宿主根逻辑仍留 `app` |
| P5.9 | 打通 Community 与刷新模块级验证矩阵 | Community 现有跳转/分享闭环，模块级 build/test/lint 命令可追溯 |
| P5.10 | 输出 reopen closeout 与下一阶段入口 | 深化版 Phase 5 closeout 完整、Stage 3 再次具备关闭条件 |

## 交付物
- `settings.gradle` 模块图
- 深化后的 `core/*` 模块
- 深化后的 `feature/*` 模块
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
