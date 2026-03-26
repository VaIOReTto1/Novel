# 原始蓝图 Phase 3-6 兑现情况审计

## 摘要
- 本文档用于对照 [Novel 超大型 Android 重构执行蓝图 v2](/d:/program/Novel/docs/refactor/Novel%20超大型%20Android%20重构执行蓝图%20v2.md) 中 `Phase 3-6` 的原始目标，审计当前仓库事实。
- 当前阶段口径以 [docs/refactor/README.md](/d:/program/Novel/docs/refactor/README.md) 为准：
  - `Phase 5 = validated`
  - `Stage 3 = Phase 5-6 = validated`
  - `Phase 6 = validated`
  - `Phase 7 = planned`
- 本文档不推翻既有 closeout 结论，而是回答：
  - 哪些蓝图目标已经兑现
  - 哪些只兑现到主路径或首轮切口
  - 哪些仍属于 carried debt
  - 哪些后续应继续留在优化或治理待办池

## 审计总览
| 阶段 | 蓝图目标 | 当前审计结论 |
| --- | --- | --- |
| `Phase 3` | 基础设施收口，收成“一套主系统” | `部分兑现` |
| `Phase 4` | 边界收口与超大类拆分 | `主要兑现，仍有 carried debt` |
| `Phase 5` | 真正的 Gradle 模块化演进 | `首轮 closeout 已兑现，但未达到蓝图理想终态` |
| `Phase 6` | 基线、预算与真实收益优化闭环 | `基线与证据已兑现，深层优化仍未做完` |

## Phase 3 基础设施收口
### 原始目标
- 把“多套系统并存”收成“一套主系统”。

### 当前仓库事实
- 高风险主网络路径已经统一到 `NetworkFacade` 入口，但仓库中仍保留 legacy transport 适配壳。
- 协程模型在阶段触达范围内已统一大部分高风险路径，但 `runBlocking` 没有在全仓库绝迹。
- `NovelUserDefaults -> DataStore` 已形成：
  - `StorageFacade`
  - `SettingsDataStorePilot`
  的抽象与试点，且试点范围已扩展到设置域和用户态 mirror；但这仍不是全量迁移。
- `AppError` 已在 Home / Search / Bridge 等高风险边界落地。
- `RequestIdInterceptor` 已在主 `OkHttp` 路径注入 `X-Request-Id / X-Trace-Id`，并把 trace 留痕补到 `ApiService / RetrofitClient / NavigationBridgeNetworkGateway`。
- `KeyChain` 有迁移演练与兼容保留，但“恢复策略正式闭环”仍未形成仓库级统一治理文档。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 网络层统一到唯一主栈 | `部分实现` |
| `ApiService / RetrofitClient` 降级为适配层 | `部分实现` |
| 协程模型统一 | `部分实现` |
| `NovelUserDefaults` 迁往 `DataStore` 抽象 | `部分实现` |
| SharedPreferences 兼容迁移 | `已实现` |
| KeyChain 保留并补恢复策略 | `部分实现` |
| 错误模型统一进入 `AppError` | `部分实现` |
| 日志脱敏与 trace id / request id 治理 | `部分实现` |

### 偏差原因
- 本阶段选择了“先收高风险主路径”，没有做全仓库一次性清底。
- `DataStore` 采用试点和 mirror 策略以降低迁移风险，没有直接切到全部生产主读路径。
- trace / request id 先以“可取证、可追踪”为目标，并未扩展成完整 observability 体系。

### 后续承接
- `core-network` 深化与真正唯一主通路：后续基础设施治理
- `DataStore` 全量迁移：后续存储治理
- trace / request id：后续可观测性治理

## Phase 4 边界收口与超大类拆分
### 原始目标
- 把“功能还在，但复杂度失控”的状态扭回来。

### 当前仓库事实
- 已完成高风险类的首轮边界收口：
  - `NavigationBridgeModule`
  - `HomeViewModel`
  - `SearchRepository`
  - `NetworkCacheManager`
- `ReaderViewModel` 没按蓝图的大拆路线推进，而是采用轻触式减重：
  - `ReaderSettingsCoordinator`
  - `ReaderHistoryCoordinator`
  - `ReaderMappingHelper`
- `BridgeFacade` 已形成并成为统一出口。
- 蓝图中理想化的 package 目标结构没有完整落成。
- schema 校验与兼容字段策略没有形成与 `BridgeFacade` 同强度的独立交付。
- 生产路径去 mock 只完成了触达范围收口，不是全仓库全面清零。

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
| 统一 `BridgeFacade` 出口 | `已实现` |
| schema / compatibility 独立治理 | `部分实现` |
| 生产路径去 mock | `部分实现` |

### 偏差原因
- 执行时优先保证 UI 和业务语义不变，因此对 Reader 采用更保守的轻触式减重。
- mock 清理如果做全量会放大范围，因此收敛为“触达范围收口”。
- 蓝图 package 结构偏理想化，实际实现围绕高风险类和边界收口展开。

### 后续承接
- Reader 深拆：后续 Reader 专项治理
- schema / compatibility：后续 Bridge 治理
- mock 余项：后续边界与数据源治理

## Phase 5 真正的模块化演进
### 原始目标
- 在边界稳定后做真正的 Gradle 模块拆分，降低编译、依赖与认知复杂度。

### 当前仓库事实
- 当前模块图已形成：
  - `app`
  - `core-common`
  - `core-ui`
  - `core-bridge`
  - `core-bridge-contract`
  - `core-storage`
  - `core-network`
  - `feature-home`
  - `feature-search`
  - `feature-login`
  - `feature-book`
  - `feature-reader`
  - `feature-rn-host`
  - `feature-welfare`
- `app` 已压薄为 Android 强制入口、route/page wrapper、RN module adapter 与极薄 host adapter。
- `home/search/login/book/reader/rn-host` 根状态机或主状态层已进入各自 feature 模块。
- `2026-03-26 reopen closeout` 已成为当前权威事实，`2026-03-21` 只保留为历史 checkpoint。
- 但蓝图里更深层的 module owner、API surface 审查、构建时间治理，并未形成同等强度的制度化闭环。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 在边界稳定后做 Gradle 模块拆分 | `已实现` |
| 拆分顺序完全按蓝图执行 | `未实现` |
| `core-common` | `已实现` |
| `core-network` 深化为共享基础设施 | `部分实现` |
| `core-storage` | `已实现` |
| `core-ui` | `已实现` |
| `core-bridge` | `部分实现` |
| `feature-home` | `已实现（按当前阶段目标）` |
| `feature-search` | `已实现（按当前阶段目标）` |
| `feature-login` | `已实现（按当前阶段目标）` |
| `feature-book` | `已实现（按当前阶段目标）` |
| `feature-reader` | `已实现（按当前阶段目标）` |
| `feature-welfare` | `已实现（按当前阶段目标）` |
| module owner / API surface / build-time governance | `延期承接` |

### 偏差原因
- 真实模块化遵循“`app` 作为 composition root + 先低风险切口”的保守策略，而非完全按蓝图顺序推进。
- `core-bridge-contract` 是比蓝图中 `core-bridge` 更保守的契约层落地形态。
- 当前阶段目标已经兑现，但蓝图理想终态中的制度化治理尚未同步完成。

### 后续承接
- `core-network` 深化：后续 core 模块治理
- owner / API surface / build-time：后续工程治理阶段

## Phase 6 性能专项治理
### 原始目标
- 在结构稳定基础上做真实收益优化，而不是前期边改边猜。

### 当前仓库事实
- 已完成 startup / scroll benchmark、Search / Reader / Welfare / RN Host / Bridge 基线与预算摘要。
- `DN2101` 设备 compile blocker 已被固化为环境阻塞项，不再误判为仓库代码回归。
- closeout 后又继续落地一批低风险优化：
  - 非关键启动初始化延后到首帧后
  - RN 预热延后到首帧后
  - Reader 初始化去重与设置触发分页刷新收敛
  - Welfare 初始化去重与 WebView `FCP / TTI` 接线
  - Search 分类筛选延后加载
  - RN context 就绪后的主题补发同步
- 数据库索引 / FTS4 / 缓存治理已形成治理入口，但尚未形成收益复盘闭环。

### 兑现结论
| 原始动作 | 结论 |
| --- | --- |
| 启动专项基线建立 | `已实现` |
| Application 冷启动任务压缩 | `部分实现` |
| RN 上下文预热策略真正优化 | `部分实现` |
| 首帧前非必要初始化清理 | `部分实现` |
| Reader 动作级压测与边界深化 | `部分实现` |
| RN Host 生命周期 / Bridge 线程切换规范优化 | `部分实现` |
| 数据库索引收益、FTS4、缓存清理收益复盘 | `未实现` |
| 核心指标全部优于基线且优化项都有数据证明 | `部分实现` |

### 偏差原因
- 当前 `Phase 6` 更偏重“基线、证据、预算、blocker 固化 + 首轮低风险优化”，而不是把深层热点全部做完。
- Reader、RN Host、WebView、数据库与缓存虽已取证，但很多仍停留在“已测量 / 已接 probe / 已有治理入口”的阶段。

### 后续承接
- 继续留在后续性能专项待办池
- 不应误塞给 `Phase 7` 的 size / dependency / build 主线

## 总结
- `Phase 3-6` 已经兑现了“分阶段收口、保守落地、证据闭环”的主目标。
- 但如果按蓝图理想强度衡量：
  - `Phase 3` 是“主路径统一了”，不是“全仓库只剩一套系统”
  - `Phase 4` 是“主要矛盾收口了”，不是“理想 package 结构完整落地”
  - `Phase 5` 是“模块化真实落地并完成本阶段 closeout”，不是“所有治理配套都做完了”
  - `Phase 6` 是“基线和证据达标”，不是“性能优化已经做完”
- 因此当前正确口径应是：
  - `Stage 3 = validated`
  - `Phase 5 = validated`
  - `Phase 6 = validated`
  - `Phase 7 = planned`
  - 同时保留 `Phase 3-6` 的 carried debt 与后续性能 backlog
