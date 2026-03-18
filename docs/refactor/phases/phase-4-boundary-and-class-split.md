# Phase 4 - 边界收口与超大类拆分

## 目标
- 把“功能还在，但复杂度失控”的状态收回到可维护区间。
- 在单 `app` 模块内完成逻辑模块化与超大类减重，为 `Phase 5` 正式模块化铺路。
- 在你不在线的情况下，也能持续按波次推进而不丢上下文。

## 范围
- 包内逻辑模块化
- Bridge 收口到 `BridgeFacade`
- 指定超大类拆分
- 生产路径去 mock
- `profile-host / RN Host` 风险验证补齐
- 第二阶段静态债第二轮收敛

## 非目标
- 不做正式 Gradle 模块拆分
- 不做 Reader 最终大拆
- 不做 route / Bridge payload / RN 组件名变更
- 不做产品语义改版
- 不做 Reader 分页、翻页、核心渲染行为重写

## 阶段策略
- 不改 UI 语义。
- 不改业务功能语义。
- Reader 只轻触，不升级为结构性重写。
- 先边界、后拆分、再静态债。
- 先宿主页与协议边界收口，再拆 Home/Search/Cache 超大类。
- 每轮只做单主题原子改动，并始终保留回滚路径。

## Phase 4 当前主攻对象事实表
- 统计时间：`2026-03-19`

| 对象 | 路径 | 规模 | Phase 4 角色 | 主要风险 |
| --- | --- | --- | --- | --- |
| `NavigationBridgeModule` | `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt` | `1095` 行 | BridgeFacade 核心拆分对象 | 协议兼容、Bridge 初始化时序 |
| `HomeViewModel` | `android/app/src/main/java/com/novel/page/home/viewmodel/HomeViewModel.kt` | `827` 行 | 首页状态与协调拆分对象 | 状态同步、导航副作用 |
| `SearchRepository` | `android/app/src/main/java/com/novel/page/search/repository/SearchRepository.kt` | `718` 行 | Search 数据职责拆分对象 | 数据职责耦合、历史配置读写 |
| `NetworkCacheManager` | `android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt` | `1242` 行 | Cache 职责边界拆分对象 | 缓存一致性、并发与回收 |
| `ReaderViewModel` | `android/app/src/main/java/com/novel/page/read/viewmodel/ReaderViewModel.kt` | `925` 行 | 仅轻触边界，不进入核心行为重写 | 触碰翻页、分页与渲染核心 |
| `NavigationUtil` | `android/app/src/main/java/com/novel/utils/NavigationUtil.kt` | `671` 行 | RN Host/Route 链路事实来源 | Route 与宿主页挂载链过长 |
| `ReactNativePage` | `android/app/src/main/java/com/novel/rn/ReactNativePage.kt` | `202` 行 | 宿主页挂载与主题注入核心点 | 白屏、主题同步、上下文时序 |

## 进入条件
- `Phase 3` 已 `validated`
- `NetworkFacade`、`StorageFacade`、`AppError`、rollback / kill switch 已成立
- 静态债基线已存在
- `Phase-aware GPT-5.4` 协作策略已落盘

## 协作编制
### Leader Mode
- `single leader / multi-helper`

### Base Helper Count
- `4`

### Scale-Up Triggers
- `NavigationBridgeModule` 与 `HomeViewModel` 可独立拆分且互不抢锁。
- `RN Host / profile-host` 风险验证可以独立运行，不与业务拆分类主题共享写锁。
- Host 风险验证与静态债收敛可从业务拆分类主题中独立出来时，允许扩容为 `5 helpers`。

### Scale-Down Triggers
- 当前只做文档、看板、关闭评审或证据归档。
- 当前只推进单一超大类拆分，不涉及 Host 风险验证。

### Agent Roster
- `BridgeFacadeSplitAgent`
  - 只管 `NavigationBridgeModule`、delegate、`BridgeFacade`、旧协议兼容
- `FeatureBoundarySplitAgent`
  - 只管 `HomeViewModel`、`SearchRepository`
- `CacheReaderLightAgent`
  - 只管 `NetworkCacheManager` 与 Reader helper/mapping/settings/history 轻触边界
- `HostRiskQualityAgent`
  - 只管 `ReactNativePage`、`NavigationUtil`、`profile-host / RN Host` 风险验证、静态债与证据
- `StaticDebtSweepAgent`
  - 仅在扩容到 `5 helpers` 后启用，承接 Phase 4 触达范围内的 detekt / lint 收敛

### Lock Strategy
- `LOCK-BRIDGE-FACADE`
  - 范围：`android/app/src/main/java/com/novel/rn/bridge/**`
- `LOCK-HOME-SEARCH-SPLIT`
  - 范围：`android/app/src/main/java/com/novel/page/home/**`, `android/app/src/main/java/com/novel/page/search/**`
- `LOCK-CACHE-READER-LIGHT`
  - 范围：`android/app/src/main/java/com/novel/utils/network/cache/**`, `android/app/src/main/java/com/novel/page/read/**`
- `LOCK-HOST-QUALITY`
  - 范围：`android/app/src/main/java/com/novel/rn/ReactNativePage.kt`, `android/app/src/main/java/com/novel/utils/NavigationUtil.kt`, `android/app/src/androidTest/**`, `__tests__/**`, `docs/refactor/evidence/**`

### Retry Window
- `0-15 min`
  - helper 自检、自修一次
- `15-30 min`
  - Leader 收窄范围后二次派发

### Escalation Window
- `30-45 min`
  - 进入 `hard escalation`
- 立即升级条件：
  - UI 语义变化
  - route / Bridge event / payload 语义变化
  - Reader 核心翻页或分页行为风险
  - 无法给出 rollback command
  - touched files 新增 lint / detekt 红项

### Leader-only Actions
- 更新阶段状态
- 更新验证看板
- 更新决策日志
- 维护 `README`、`phase-4-wave-tracker.md`、回滚索引
- 执行 Git commit / revert

## 双轨执行结构
### 任务轨
| 编号 | 任务 | 默认 Owner | 对应 Wave | 对应检验 |
| --- | --- | --- | --- | --- |
| P4.1 | 建立目标包结构与迁移映射 | `LeaderAgent + FeatureBoundarySplitAgent` | Wave 1 | V4-01 |
| P4.2 | 建立 `BridgeFacade` | `BridgeFacadeSplitAgent` | Wave 1 | V4-03 |
| P4.3 | 拆分 `NavigationBridgeModule` | `BridgeFacadeSplitAgent` | Wave 2 | V4-02 |
| P4.4 | 拆分 `HomeViewModel` | `FeatureBoundarySplitAgent` | Wave 3 | V4-02 |
| P4.5 | 拆分 `SearchRepository` | `FeatureBoundarySplitAgent` | Wave 3 | V4-02 |
| P4.6 | 拆分 `NetworkCacheManager` | `CacheReaderLightAgent` | Wave 3 | V4-02 |
| P4.7 | Reader 轻触减重 | `CacheReaderLightAgent` | Wave 4 | V4-02 |
| P4.8 | 清理第二阶段触达范围内生产 mock | `HostRiskQualityAgent` | Wave 4 | V4-04 |
| P4.9 | 补齐 `profile-host / RN Host` 专项验证 | `HostRiskQualityAgent` | Wave 2 | V4-05 |
| P4.10 | 第二阶段静态债第二轮收敛 | `HostRiskQualityAgent` | Wave 4 | V4-06 |
| P4.11 | 输出第二阶段关闭总结 | `LeaderAgent` | Wave 4 | V4-07 |

### 波次轨
#### Wave 1 - 边界骨架与拆分地图
- 目标：
  - 产出包边界图
  - 建立 `BridgeFacade` 外围接口和 delegate 映射表
  - 形成四个超大类的拆分蓝图
- 重点任务：
  - `P4.1`
  - `P4.2`
- 默认 Owner：
  - `BridgeFacadeSplitAgent`
  - `FeatureBoundarySplitAgent`
- 进入条件：
  - `Phase 3` 全绿
  - 当前目标类事实表已确认
- 退出条件：
  - `V4-01` 接近可验证
  - 所有目标类已有职责切片图
  - 未触碰任何用户可见行为

#### Wave 2 - Bridge 与宿主页边界收口
- 目标：
  - `NavigationBridgeModule` 拆分为 facade + delegates
  - 明确 `ReactNativePage` / `NavigationUtil` / profile-host 的宿主页挂载链
  - 把 `V4-03`、`V4-05` 变成可量化验证项
- 重点任务：
  - `P4.3`
  - `P4.9`
- 默认 Owner：
  - `BridgeFacadeSplitAgent`
  - `HostRiskQualityAgent`
- 进入条件：
  - `Wave 1` 完成边界图与 facade 外围映射
- 退出条件：
  - Bridge 统一出口成立
  - 旧协议兼容证据齐全
  - Host 风险验证清单形成

#### Wave 3 - Home/Search/Cache 大类拆分
- 目标：
  - 拆 `HomeViewModel`
  - 拆 `SearchRepository`
  - 拆 `NetworkCacheManager`
- 重点任务：
  - `P4.4`
  - `P4.5`
  - `P4.6`
- 默认 Owner：
  - `FeatureBoundarySplitAgent`
  - `CacheReaderLightAgent`
- 进入条件：
  - `Wave 1` 与 `Wave 2` 已明确边界和 Host 风险
- 退出条件：
  - `V4-02` 主体完成
  - touched files 静态债为 `0`
  - 不引入新 mock

#### Wave 4 - Reader 轻触减重 + mock 清理 + 阶段收尾
- 目标：
  - 只做 Reader helper/mapping/settings/history 边界抽离
  - 清理 Phase 4 触达范围内生产 mock
  - 完成 `V4-04`、`V4-06`、`V4-07`、`V4-08`
- 重点任务：
  - `P4.7`
  - `P4.8`
  - `P4.10`
  - `P4.11`
- 默认 Owner：
  - `CacheReaderLightAgent`
  - `HostRiskQualityAgent`
  - `LeaderAgent`
- 进入条件：
  - `Wave 3` 完成主拆分
- 退出条件：
  - `Phase 4` closeout 资料齐
  - 进入 `Phase 5` 的条件客观化
  - 所有 `V4-*` 可评审

## 长期运行协议
### Autonomy Contract
- 默认可持续推进：
  - 文档补充
  - 包边界分析
  - 代码拆分设计
  - 非破坏性验证
  - 单主题原子提交计划
  - 验证证据归档
- 默认不需要中断用户：
  - 波次内优先级微调
  - helper 数量在基线范围内调整
  - 先做哪个超大类拆分
  - 先做 Host 风险验证还是 cache 类拆分，只要不跨出 `Phase 4` 范围

### 重大决策升级清单
- route / Bridge event / payload 语义变化
- Reader 分页、翻页、核心渲染行为变更
- 需要把 Reader 从“轻触”升级成“结构性拆分”
- `Phase 4` 范围扩张到真正 Gradle 模块化
- 任何会改变 UI 语义或产品行为的方案

### 持续运行节奏
1. 读取 `phase-4-wave-tracker.md` 当前状态
2. 选择单主题原子任务
3. 确认锁与 owner helper
4. 执行验证
5. 更新看板、决策日志、证据、派发日志、回滚索引
6. 判断是否继续同一 wave

### 文档同步规则
- 每轮工作必须更新：
  - `docs/refactor/phase-4/phase-4-wave-tracker.md`
  - `docs/refactor/tracking/subagent-dispatch-log.md`
  - `docs/refactor/tracking/rollback-index.md`
- 仅在以下情况更新：
  - `decision-log.md`
    - 波次切换
    - blocker / high 风险
    - 重大决策
  - `README.md`
    - 阶段状态切换
    - 当前阶段默认编制变更

### 停机与恢复规则
- 未命中“重大决策升级清单”时，无需等待用户确认即可进入下一原子主题。
- 若某一 wave 在单锁范围内阻塞，但其他 wave 存在无依赖、无锁冲突任务，可切换波次继续推进。
- 若 `LOCK-HOST-QUALITY` 风险扩大到影响主路径稳定性，则冻结 Wave 2 与 Wave 4，仅保留文档分析工作。

## 长期派发日志规则
- 每轮工作都必须更新 `subagent-dispatch-log`。
- 每个 wave 必须形成 `wave summary` 并落到 `phase-4-wave-tracker.md`。
- 每个原子主题必须生成 `rollback id` 并写入 `rollback-index.md`。

## 交付物
- 包结构迁移映射
- `BridgeFacade` 与 delegate 设计
- 指定超大类拆分结果
- 生产 mock 清理清单
- `profile-host / RN Host` 风险验证材料
- 第二阶段关闭总结
- `phase-4-wave-tracker.md`
- `subagent-dispatch-log.md`
- `rollback-index.md`

## 硬阈值
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 必须完成拆分
- Bridge 新增直连调用点（绕过 `BridgeFacade`）= `0`
- 第二阶段触达范围内生产 mock 保留数 = `0`
- 每个 wave 必须有可追溯 summary
- 每个原子主题必须有 `rollback id`
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`

## 风险与回滚
- 风险：
  - `profile-host / RN Host` 挂载稳定性
  - Bridge 初始化时序
  - Reader 减重触碰阅读核心行为
  - 生产 mock 清理引发真实空态或弱网分支暴露
  - 超大类拆分期间职责边界判断失误
- 回滚：
  - facade、delegate、helper 拆分必须保留兼容入口
  - 若 `RN Host / profile-host` 风险扩大，允许暂停 Phase 4，仅保留 Phase 3 成果
  - 若 Reader 轻触减重引发翻页/分页核心回归，立即回退该组原子提交
  - 若任一波次无法给出 `rollback id`，该波次内原子主题不得继续提交

## 检验计划
- `V4-01` 包边界骨架稳定
- `V4-02` 指定超大类拆分完成
- `V4-03` `BridgeFacade` 成立且旧协议兼容
- `V4-04` 第二阶段触达范围内生产 mock 清理完成
- `V4-05` `profile-host / RN Host` 风险验证补齐
- `V4-06` 第二阶段静态债目标达标
- `V4-07` 第二阶段关闭总结完成
- `V4-08` Phase 5 进入条件明确

## 退出条件
- 全部 `V4-*` 为 `green`
- 指定超大类拆分完成并通过回归
- `profile-host / RN Host` 不再只有口头高风险说明
- 第二阶段可进入真正模块化准备阶段

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`planned`
