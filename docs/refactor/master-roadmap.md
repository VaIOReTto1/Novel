# Novel 重构总路线图

## 0. 当前权威状态
- 当前控制面已切换到：`Stage 4 = validated`
- 当前已关闭前半段：`Phase 7 = validated`
- 当前后半段：`Phase 8 = validated`
- 当前新阶段：`Stage 5 = in_progress`
- 当前已关闭前半段：`Phase 9 = validated`
- 当前排队阶段：`Phase 10 = planned`
- `Stage 3 = validated` 继续以 `2026-03-26` closeout 为准
- 当前 authority 入口：
  - `docs/refactor/README.md`
  - `docs/refactor/stage-4-phase-7-8-plan.md`
  - `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
  - `docs/refactor/tracking/phase-7-8-validation-board.md`

## 1. 项目现状摘要
- 当前项目是 `React Native + Android Compose/Kotlin` 的混合架构大型小说阅读应用。
- Android 原生已承担首页、书详情、阅读器、搜索、登录、福利等关键体验页面；RN 侧承载分类、书架、我的、设置、作者、AI 等大量业务页面。
- Android 侧已接入 Hilt、MVI、Room、Paging3、Baseline Profile、Macrobenchmark、加密存储、性能监控、图片与网络缓存，但体系化程度不足。
- 当前工程存在网络栈双轨并存、超大类膨胀、发布与治理体系仍未完全制度化、数据库迁移策略不安全、自动化护栏薄弱、资源与依赖膨胀、协议边界不够稳定等问题。

## 2. 主要问题分层

### 架构
- Hilt 提供的网络栈和手写 `RetrofitClient/ApiService` 并存，主通路不唯一。
- `ReaderViewModel`、`NavigationBridgeModule`、`NetworkCacheManager`、`HomeViewModel` 等类过大，边界不清晰。
- Native、Bridge、RN 页面之间存在业务逻辑下沉不一致的问题。

### 性能
- 启动与基准测试能力已接入，但基线、设备矩阵、退化门禁尚未固化。
- 阅读器、首页、Bridge、WebView 等主路径缺少持续性能预算和稳定回归机制。

### 依赖
- Android 依赖治理尚未统一到版本目录或 BOM。
- 构建插件与 Kotlin/Compose/Hilt 声明存在重复和潜在冲突。

### 包体积
- Android 资源尤其字体资源体积偏大。
- Release 已开启 `minify` 与 `shrinkResources`，但仍缺 size baseline / artifact diff / JS-native assets 治理。

### 安全
- Release 路径仍存在全局明文流量、硬编码 `http://` endpoint、权限过宽、证书锁定未真正落地等问题。
- WebView 安全策略、外链策略、mixed content 策略尚未体系化。

### 维护
- README 中部分“已完成能力”与真实工程成熟度不完全一致。
- 缺少长期有效的 ADR、协议文档、变更审查和责任归属机制。

### 测试
- Android JVM 单元测试基础薄弱，核心逻辑更多停留在 `androidTest` 或文档层。
- 稳定的 fixture、fake data source、Bridge contract tests 尚未建立。

### 发布
- Release 构建链路已基本生产化，且仓库已存在 `verification-metadata.xml`，但环境注入、catalog/BOM、供应链审计和依赖统一治理仍不完整。
- Phase 间没有系统化的关闭标准和回滚触发条件。

### 可观测性
- 启动、Bridge、WebView 等局部监控能力已存在，但尚未沉淀为统一指标目录、长期看板与 rollout 控制面。

### 合规
- 权限申请说明、最小权限策略、日志脱敏、WebView 内容来源与第三方依赖合规能力需要补强。

## 3. 全局目标
- 不影响现有 UI 设计。
- 不影响现有功能语义。
- 不影响当前业务推进节奏。
- 将项目逐步重构为一个可长期维护、可安全发布、可量化优化、可原子化演进的 Android 混合架构工程。

## 4. 全局原则
- 先护栏后重构。
- 先收口后拆分。
- 先稳定核心链路后做高级优化。
- 全程可逆，优先支持适配层、双轨期、灰度与回滚。
- 所有大改动必须有基线、验证、证据和决策记录。

## 5. 阶段总览

### Phase 0
- 建立可测量、可比较、可回滚的基线、控制面板、资产清单、风险图谱和禁区清单。

### Phase 1
- 收口发布、安全与合规链路，使工程具备正式发布能力。

### Phase 2
- 建立真实可阻断的自动化质量门禁和测试基础设施。

### Phase 3
- 做基础设施收口，统一网络、存储、协程、错误模型与日志体系。

### Phase 4
- 进行边界收口与超大类拆分，先在单模块内逻辑模块化，再为正式模块化铺路。

### Phase 5
- 正式推进 Gradle 模块化，按 `core -> feature` 顺序拆分，阅读器最后处理。

### Phase 6
- 开展性能专项治理，覆盖启动、滚动、翻页、WebView、Bridge、缓存与数据库。

### Phase 7
- 开展包体积、依赖与构建效率治理。

### Phase 8
- 建设可观测性、灰度、特性开关、团队治理和长期演进机制。

### Phase 9
- 运行可靠性与业务连续性：电量/热量、进程恢复、弱网离线、设置导入导出、历史与用户态连续性。

### Phase 10
- 无障碍、合规、供应链与双端协作治理：accessibility、敏感日志、依赖审计、Bridge schema、RN component registry。

### Phase 11
- 数据质量与可维护性：生产 mock 退场、fallback / 空态统一、命名/目录/状态模型、错误文案与用户提示。

## 5.0 当前 Stage 分组
- `Stage 1 = Phase 0-2`
  - 基线、发布安全、质量护栏
- `Stage 2 = Phase 3-4`
  - 基础设施收口、边界收口与超大类拆分
- `Stage 3 = Phase 5-6`
  - 真正模块化、性能专项与预算治理
- `Stage 4 = Phase 7-8`
  - 包体积/依赖/构建效率治理，以及可观测性、灰度和长期机制建设
- `Stage 5 = Phase 9-11`
  - 运行硬化、合规与供应链、数据质量与可维护性收敛

### 为什么 Stage 3 不是 `Phase 5-7`
- `Phase 5` 与 `Phase 6` 强耦合：
  - 先有稳定模块图，性能专项才有清晰观测边界
  - 性能预算要建立在模块边界不再大幅漂移的前提上
- `Phase 7` 更适合在 `Phase 5-6` 完成后单独处理：
  - 包体积、依赖和构建效率基线会被模块化大幅改变
  - 若提前并入，会让阶段目标过宽且基线持续漂移

## 5.1 Phase-aware Subagent 规划规则
- 从本规则生效起，每个新 phase 文档都必须新增 `协作编制` 区块。
- `协作编制` 至少必须包含：
  - `Leader Mode`
  - `Base Helper Count`
  - `Scale-Up Triggers`
  - `Scale-Down Triggers`
  - `Agent Roster`
  - `Lock Strategy`
  - `Retry Window`
  - `Escalation Window`
  - `Leader-only Actions`
- 所有 helper 与 leader 默认模型固定为 `GPT-5.4`。
- 写的是 `基线人数 + 扩缩规则`，不是写死人数。
- 每次进入新 phase 时，必须在 phase 文档中重新确认当期实际编制。

### Phase Staffing 总表
| Phase | Base Helper Count | 默认角色类型 | 典型扩容条件 |
| --- | --- | --- | --- |
| Phase 0 | `2` | 盘点/基线、设备/证据 | Android 与 RN 盘点、动态取证并行 |
| Phase 1 | `3` | 发布构建、安全合规、迁移验证 | 签名、WebView、迁移演练、benchmark 并行 |
| Phase 2 | `4` | JVM/fixture、Bridge contract、smoke/CI、静态债 | smoke 与 CI 门禁拆线并行 |
| Phase 3 | `4` | Core infra、Bridge boundary、Storage/error、Validation | 作为历史样本记录，不回填旧阶段文档 |
| Phase 4 | `4` | BridgeFacade、Feature split、Reader/cache light、Host/quality | `NavigationBridgeModule` 与 `HomeViewModel` 可独立拆分 |
| Phase 5 | `5` | Module graph、core module、feature A、feature B、build integration | 模块搬迁与构建集成并行 |
| Phase 6 | `3` | Startup、Reader/scroll、benchmark/observability | WebView 与 DB 性能专项独立成线 |
| Phase 7 | `3` | Size shrink、dependency/build、artifact diff | npm 与 Gradle 依赖治理拆线 |
| Phase 8 | `3` | Observability、governance/ADR、rollout/flag | Crash/ANR 与治理建设并行 |
| Phase 9 | `4` | Runtime resilience、lifecycle recovery、continuity evidence | Reader/Welfare/RN Host/登录与用户态分线 |
| Phase 10 | `4` | Accessibility、compliance、supply chain、bridge registry | 无障碍、供应链与双端协作并行 |
| Phase 11 | `3` | Data quality、empty-state catalog、maintainability guide | mock 退场与规范收口并行 |

## 6. 全局补充优化点
- 无障碍：语义标签、TalkBack、点击区域、对比度、字体缩放与阅读器极限字号。
- 电量与热量：阅读器持续阅读、章节预取、WebView 活动页、后台协程与轮询。
- 进程重建：进程被杀、配置变化、后台恢复、RN context 丢失恢复。
- 低内存恢复：页面重建、缓存回收后恢复、WebView 与阅读器状态恢复。
- WebView 合规：域名白名单、第三方内容来源、外链策略、混合内容和 Cookie 管理。
- 供应链安全：Gradle 依赖验证、npm audit、lockfile 一致性、构建插件审计。
- Feature flag / kill switch：关键架构切换能力、应急回滚开关、灰度控制。
- 桥接协议版本化：Bridge event、payload、RN 组件名、route 语义统一文档化。
- 可观测性：Crash、ANR、启动、卡顿、Bridge、WebView、缓存、网络质量、包体积。
- 团队治理：ADR、模块 owner、Validator、阶段关卡、Review checklist。

## 7. 本轮明确不做的事项
- 不做 KMP。
- 不做全面 RN -> Compose 重写。
- 不做大规模 UI 改版。
- 不在 Reader 模块完全稳定之前优先拆 Reader Gradle 模块。

## 8. 阶段依赖与门禁
- 采用严格门禁：
  - `Phase 0 validated -> Phase 1`
  - `Phase 1 validated -> Phase 2`
  - `Phase 2 validated -> Stage 2`
  - `Stage 2 validated -> Stage 3`
  - `Stage 3 validated -> Stage 4`
- 任一阶段存在 `red` 验证项时不得关闭阶段。
- 阶段关闭必须同步更新：  
  - `docs/refactor/README.md`
  - 对应阶段文档
  - 对应 validation board
  - 对应 stage closeout
  - `decision-log.md`
- 阶段关闭或进入新阶段前，必须确认：
  - 当前 phase 的 `协作编制` 已写入阶段文档
  - helper 锁策略无路径重叠
  - `Leader-only Actions` 已明确

## 9. 原子提交策略
- 每次提交只做一类变化：
  - 文档骨架
  - 阶段文档
  - 跟踪看板
  - 基线资料
  - 构建治理
  - 安全治理
  - 测试设施
- 一个提交不混入文档、构建、测试和业务功能的多类变化。
- 每次原子化改动后立即提交 Git，提交信息使用中文。
- 所有提交都需要可回退、可解释、可验证。

## 10. 成功标准
- 重构期间核心功能和 UI 行为保持稳定。
- 每个阶段都有明确的进入条件、硬阈值、退出条件和证据沉淀。
- 后续架构重构建立在自动化护栏和生产化工程基础之上，而不是直接进入代码重写。
- 每个新 phase 都有明确的 helper 基线人数、扩缩条件、锁与升级窗口，不再临时口头决定协作方式。
