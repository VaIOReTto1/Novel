# 原始蓝图 Phase 3-6 兑现情况审计

## 摘要
- 本文档用于对照 [Novel 超大型 Android 重构执行蓝图 v2.md](/d:/program/Novel/docs/refactor/Novel%20超大型%20Android%20重构执行蓝图%20v2.md) 中 `Phase 3-6` 的原始目标，审计当前仓库事实。
- 它不推翻现有阶段结论，而是回答：
  - 哪些目标已实现
  - 哪些只做到部分实现
  - 哪些已延期承接
  - 哪些仍未实现
- 截至 `2026-03-23` 复核，本审计结论继续成立；尤其是 `Phase 5` 仍不能被表述为“蓝图已全部兑现”，当前只是首轮 checkpoint 已归档、阶段本身已重新切回 `in_progress`。

## Phase 3 基础设施收口
### 原始目标
- 把“多套系统并存”收成“一套主系统”。

### 仓库事实
- 高风险主网络路径已经统一到 `NetworkFacade` 入口，但并不代表全仓库所有 legacy transport 语义都被彻底清零。
- 协程模型在阶段触达范围内统一了大部分高风险路径，但 `runBlocking` 并没有在全仓库绝迹。
- `NovelUserDefaults -> DataStore` 只做到：
  - `StorageFacade`
  - `SettingsDataStorePilot`
  的抽象与试点；当前试点范围已扩展到设置域与用户态 mirror，但仍不是全量迁移，也没有切换全部生产读路径。
- `AppError` 已在首批 Home / Search / Bridge 边界落地。
- `trace id / request id` 已通过 `RequestIdInterceptor` 在 `OkHttp` 请求链路注入 `X-Request-Id / X-Trace-Id` header，并已把结构化 trace 日志补到 `ApiService / RetrofitClient / NavigationBridgeNetworkGateway` 这三类高风险边界，但还没形成正式仓库级 observability 闭环。
- `KeyChain` 有历史迁移演练证据，但“恢复策略正式闭环”没有以 Phase 3 权威文档完全固化。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 网络层统一到唯一主栈 | `部分实现` |
| `ApiService/RetrofitClient` 降级为适配层 | `部分实现` |
| 协程模型统一 | `部分实现` |
| `NovelUserDefaults` 迁往 `DataStore` 抽象 | `部分实现` |
| SharedPreferences 兼容迁移 | `已实现` |
| KeyChain 保留并补恢复策略 | `部分实现` |
| 错误模型统一进入 `AppError` | `部分实现` |
| 日志脱敏 | `部分实现` |
| trace id / request id 能力统一 | `部分实现` |

### 偏差原因
- 阶段选择了“先收高风险主路径”的策略，而不是全量仓库级一次性清底。
- 为了降低风险，DataStore 只做了低风险试点，没有推动全量 `NovelUserDefaults` 迁移。
- 当前 `DataStore` 已从双布尔值试点扩展到设置域和用户态 mirror，但依旧停留在“兼容迁移样本 + 写链镜像”阶段，而不是仓库级主读路径切换。
- 日志与 request tracing 更偏平台级治理；当前已完成 header 注入与主 legacy 网络壳 / bridge gateway 的结构化留痕，但仍缺 trace id 贯穿日志、错误、证据与阶段门禁的完整 observability 方案。

### 后续承接阶段
- `core-network` 深化与真正的唯一主通路：后续架构 / 基础设施治理
- `DataStore` 全量迁移：后续存储治理
- `trace id / request id`：后续可观测性治理

## Phase 4 边界收口与超大类拆分
### 原始目标
- 把“功能还在，但复杂度失控”的状态扭回来。

### 仓库事实
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 的阶段目标已完成。
- `ReaderViewModel` 没按原蓝图的大拆路线推进，而是采用轻触式：
  - `ReaderSettingsCoordinator`
  - `ReaderHistoryCoordinator`
  - `ReaderMappingHelper`
- `BridgeFacade` 已形成。
- 但原蓝图中的 package 目标结构没有按原文本完整落成。
- “schema 校验与兼容字段策略”没有形成与 `BridgeFacade` 同强度的独立交付物。
- “生产路径去 mock”只完成了触达范围收口，不是全量清除所有生产 mock。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 单 `app` 内先做逻辑模块化 | `已实现` |
| package 目标结构完整落成 | `未实现` |
| `NavigationBridgeModule` 拆分 | `已实现` |
| `HomeViewModel` 拆分 | `已实现` |
| `SearchRepository` 拆分 | `已实现` |
| `NetworkCacheManager` 拆分 | `已实现` |
| `ReaderViewModel` 按原顺序大拆 | `延期承接` |
| Bridge 重构并统一 `BridgeFacade` 出口 | `已实现` |
| schema 校验与兼容字段策略独立化 | `部分实现` |
| 生产路径去 mock | `部分实现` |

### 偏差原因
- 阶段执行时优先保证 UI/业务语义不变，因此对 Reader 采取了更保守的轻触式减重。
- 生产 mock 的全量清理会扩大范围，故被压缩为“触达范围收口”。
- 原蓝图 package 结构偏理想化，实际实现围绕高风险类和边界收口展开。

### 后续承接阶段
- Reader 深拆：后续 Reader 专项治理
- schema / compatibility 策略增强：后续 Bridge 治理
- 生产 mock 余项：后续功能边界与数据源治理

## Phase 5 真正的模块化演进
### 原始目标
- 在边界稳定后做真正的 Gradle 模块拆分，降低编译、依赖、认知复杂度。

### 仓库事实
- 实际拆分顺序与蓝图不同，且 `core-storage`、契约优先版 `core-network` 作为起始事实已经存在。
- `2026-03-21` 的 `Phase 5 validated` 只应视为首轮 closeout checkpoint；截至 `2026-03-23`，由于模块图仍明显偏浅，`Phase 5` 已重新切回 `in_progress`。
- 本轮实际落地的首批模块包括：
  - `core-common`
  - `core-ui`
  - `core-bridge`
  - `core-bridge-contract`
  - `feature-welfare`
  - `feature-home`
  - `feature-search`
  - `feature-rn-host`
- `feature-login`、`feature-reader` 没在本轮落地。
- `app` 仍作为 composition root。
- `module owner / API surface 审查 / 构建时间统计` 没有形成和原蓝图同等强度的制度化文档闭环。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 在边界稳定后做 Gradle 模块拆分 | `已实现` |
| 默认拆分顺序完全按蓝图执行 | `未实现` |
| `core-common` | `已实现` |
| `core-network` 深化到可复用共享基础设施 | `部分实现` |
| `core-storage` | `已实现` |
| `core-ui` | `已实现` |
| `core-bridge` | `部分实现`（`core-bridge` 与 `core-bridge-contract` 已落地，但共享层仍未完全替代 app 宿主 glue） |
| `feature-home` | `已实现`（已从首轮最小切口继续深化，但仍未达到完整 feature 搬迁终态） |
| `feature-search` | `已实现`（已从首轮最小切口继续深化，但仍未达到完整 feature 搬迁终态） |
| `feature-login` | `未实现` |
| `feature-welfare` | `已实现`（已从首轮及扩展切口继续深化，但仍未达到完整 feature 搬迁终态） |
| `feature-reader` | `未实现` |
| 引入 module owner | `延期承接` |
| 引入 API surface 审查 | `延期承接` |
| 引入构建时间统计 | `延期承接` |

### 偏差原因
- 真实模块化遵循了“app 作为 composition root”“先低风险模块切口”的保守策略。
- `Reader` 与 `login` 没有进入本轮，主要是因为复杂度和敏感度仍高。
- `core-bridge-contract` 是比蓝图中的 `core-bridge` 更保守的最小落地形态。

### 后续承接阶段
- `core-network` 深化：后续 core 模块治理
- `feature-login` / `feature-reader`：后续模块化深化
- owner / API surface / build-time governance：后续工程治理阶段

## Phase 6 性能专项治理
### 原始目标
- 在结构稳定基础上做真实收益优化，而不是前期边改边猜。

### 仓库事实
- 本轮 `Phase 6` 的强项是：
  - startup / scroll benchmark 绿色套件
  - baseline profile blocker 固化
  - Search / Reader / Welfare / RN Host / Bridge 正式证据
  - 性能预算摘要
- closeout 后继续落地的低风险收益包括：
  - 非关键启动初始化延后到首帧后
  - RN 预热延后到首帧后
  - Reader 初始化去重与设置触发分页刷新收敛
  - Welfare 初始化去重与 WebView `FCP / TTI` 接线
  - Search 分类筛选延后加载
  - RN context 就绪后的主题补发同步
- 本轮 `Phase 6` 的弱项是：
  - 很多真实优化动作尚未展开或只完成到第一层收敛
  - 启动、Compose、Reader、RN Host、数据库与缓存收益复盘都没有完成原蓝图级别的系统优化闭环
  - 数据库索引 / FTS4 / 缓存治理虽然已经有报告入口，但还没有升级成真实收益结论

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 启动专项基线建立 | `已实现` |
| Application 冷启动任务压缩 | `部分实现` |
| RN 上下文预热策略真正优化 | `部分实现` |
| 首帧前非必要初始化清理 | `部分实现` |
| Compose 热点系统优化 | `部分实现` |
| Reader 动作级压测与边界深化 | `部分实现` |
| RN Host 生命周期 / Bridge 线程切换规范优化 | `部分实现` |
| 数据库索引收益、FTS4、缓存清理收益复盘 | `未实现` |
| 核心指标全部优于基线且优化项都有数据证明 | `部分实现` |

### 偏差原因
- 本轮更偏重“把基线、证据与第一层低风险收益跑通”，而不是大规模性能重构。
- `DN2101` 的设备 compile blocker 让 compiled-mode startup/profile 无法成为强证据来源。
- Reader、RN Host、WebView 这些路径虽然已采证且落了部分优化，但还没深挖到动作级和治理级。

### 后续承接阶段
- 继续留在后续性能专项待办池
- 不应误塞给 `Phase 7` 的 size/build/dependency 主线

## 总结
- `Phase 3-6` 总体上达成了“分阶段收口、保守落地、文档闭环”的目标。
- 但如果用最初蓝图的理想强度衡量：
  - `Phase 3` 是“高风险主路径统一了”，不是“全仓库彻底只剩一套系统”
  - `Phase 4` 是“边界和超大类主要矛盾收口了”，不是“package 理想结构完整落地”
  - `Phase 5` 是“模块化真正启动并闭环了”，不是“蓝图列的所有模块都落地了”
  - `Phase 6` 是“基线和证据达标了”，不是“性能优化已经做完了”
