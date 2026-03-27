# 原始蓝图 Phase 3-6 兑现情况审计

## 摘要
- 本文档以 [Novel 超大型 Android 重构执行蓝图 v2](/d:/program/Novel/docs/refactor/Novel%20超大型%20Android%20重构执行蓝图%20v2.md) 为原始输入，审计 `Phase 3-6` 在当前仓库中的兑现程度。
- 当前阶段口径以 [docs/refactor/README.md](/d:/program/Novel/docs/refactor/README.md) 为准：
  - `Phase 5 = validated`
  - `Stage 3 = Phase 5-6 = validated`
  - `Phase 6 = validated`
  - `Phase 7 = planned`
- 本文档不负责改阶段状态；它负责回答：
  - 哪些蓝图目标已经兑现
  - 哪些只兑现到主路径或治理入口
  - 哪些仍是 carried debt
  - 本轮到底关闭了哪些项，哪些没有

## 审计总览
| 阶段 | 当前审计结论 | 本轮关闭项 | 当前残余 debt 归属 |
| --- | --- | --- | --- |
| `Phase 3` | `部分兑现` | `无新增关闭项，仅补齐当前执行动作映射` | `core-network / storage / observability governance` |
| `Phase 4` | `主要兑现，carried debt 已治理化` | `schema / compatibility 独立治理`、`生产 mock 退出治理规则` | `Reader 深拆`、`RN heavy pages mock 退场` |
| `Phase 5` | `主要兑现，治理工件已补齐` | `module owner matrix`、`API surface checklist`、`build-time baseline / diff 入口` | `core-network / core-bridge 深化自动化`、`Phase 7 build governance` |
| `Phase 6` | `已兑现主目标，仍有持续优化 backlog` | `Wave 1-3 优化`、`2026-03-27` 设备证据补齐、Startup 任务清单治理、RN Host 返回缓存策略显式化 | `Search LOAD_MORE`、`Reader flip`、数据库/缓存收益复盘 |

## Phase 3 基础设施收口
### 原始蓝图目标
- 把“多套系统并存”收成“一套主系统”。

### 当前仓库事实
- 高风险主网络路径已经统一到 `NetworkFacade` 入口，但仓库中仍保留 legacy transport 兼容壳。
- `StorageFacade + SettingsDataStorePilot` 已经覆盖设置域与部分用户态配置试点，但还不是全量 DataStore 主读。
- `AppError`、`X-Request-Id / X-Trace-Id` 已在 Home / Search / Bridge 等高风险边界落地。
- `trace id / request id` 已能进入主请求链路和 bridge 网络入口，但仍没有完整 observability 平台。

### 已兑现项
- 高风险主网络入口已收敛到 `NetworkFacade` 主通路。
- `StorageFacade`、`SettingsDataStorePilot`、SharedPreferences 兼容层已经建立。
- `AppError` 第一批统一边界已经完成。
- `RequestIdInterceptor` 与主链路 trace 注入已经完成。

### 仍未兑现项
| 条目 | 当前执行动作 | 当前状态 |
| --- | --- | --- |
| 全仓库真正只剩一套网络系统 | 继续以 `core-network` 深化为主，不重开大范围替换 | `carried debt` |
| DataStore 从试点扩到全量正式主读 | 保持安全域渐进迁移，不把镜像试点伪装成全量完成 | `carried debt` |
| trace / request id 从链路留痕升级到统一证据体系 | 继续作为 observability 治理项保留 | `carried debt` |
| KeyChain 恢复策略形成仓库级治理闭环 | 仍未形成独立治理入口 | `carried debt` |

### 偏差原因
- `Phase 3` 真实执行策略是“先收高风险主路径”，不是一次性把全仓库旧系统清零。
- 存储迁移为了降低风险采用 mirror / pilot 策略，没有直接切成全量主读。
- trace / request id 以“可追踪、可取证”为先，而不是在本阶段就做完整监控平台。

### 本轮要关闭的项
- 本轮没有把 `Phase 3` debt 伪装成已关闭。
- 本轮只补齐了这些 debt 的当前执行动作和归属阶段，避免它们继续漂浮在“以后再看”的状态。

### 本轮关闭后的残余 debt
- `core-network` 仍需继续深化到更完整的共享基础设施。
- `DataStore` 仍需继续扩大主读范围。
- `trace / request id` 仍需进入更正式的证据与 observability 治理。

## Phase 4 边界收口与超大类拆分
### 原始蓝图目标
- 把“功能还在，但复杂度失控”的状态扭回来。

### 当前仓库事实
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 的首轮拆分已经完成。
- `ReaderViewModel` 没按蓝图理想顺序做大拆，而是以 settings / history / mapping / trace 等轻触式减重为主。
- `BridgeFacade` 与 delegate 体系已经落地。
- `2026-03-27` 起新增两份独立治理工件：
  - `docs/refactor/phase-4/bridge-schema-compat-governance-2026-03-27.md`
  - `docs/refactor/phase-4/production-mock-exit-governance-2026-03-27.md`

### 已兑现项
- 单 `app` 内先做逻辑模块化边界已完成。
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 的阶段目标已兑现。
- `BridgeFacade` 成立且旧协议兼容已保留。
- `schema / compatibility` 已不再只是附着在 `BridgeFacade` 上，而是形成独立治理入口。
- 生产 mock 不再只有 inventory，已经形成退出治理规则与 carried debt 归属。

### 仍未兑现项
| 条目 | 当前执行动作 | 当前状态 |
| --- | --- | --- |
| Reader 按原蓝图顺序大拆 | 继续保持“轻触式优化 + coordinator/policy 下沉” | `carried debt` |
| 理想 package 目标结构完整落成 | 不再作为当前阶段强制门禁，只保留为蓝图理想形态 | `carried debt` |
| RN heavy pages 主数据 mock 全部退出生产路径 | 以退出治理文档管理，不在本轮硬清 | `carried debt` |

### 偏差原因
- 实际执行优先保持 UI / 业务语义稳定，因此 Reader 没走大拆路线。
- mock 清理若做成全量强推，会把范围扩成新的数据源专项。
- 蓝图中的 package 目标结构偏理想化，真实实现更偏“先收主要矛盾”。

### 本轮要关闭的项
- `schema / compatibility 独立治理`
  - 已由 `bridge-schema-compat-governance-2026-03-27.md` 关闭“没有独立治理工件”的缺口。
- `生产路径去 mock` 的治理层
  - 已由 `production-mock-exit-governance-2026-03-27.md` 关闭“只有 inventory、没有退出治理”的缺口。

### 本轮关闭后的残余 debt
- `ReaderViewModel` 深拆仍是明确延期项，不应伪装成已完成。
- RN heavy pages 的主数据 mock 仍未退出，但现在已经有清单、owner 面和退出前提。
- package 结构的理想终态仍未完全落地，但不再是当前阶段关闭的阻塞项。

## Phase 5 真正的模块化演进
### 原始蓝图目标
- 在边界稳定后做真正的 Gradle 模块拆分，并补上 owner、API surface、build-time 治理。

### 当前仓库事实
- 当前模块图已经稳定为：
  - `app + core-* + feature-* + macrobenchmark`
- `app` 已压薄为 Android 强制入口、route/page wrapper、RN module adapter 与极薄 host adapter。
- `home/search/login/book/reader/rn-host/welfare` 的稳定 feature 根已经进入对应模块。
- `2026-03-27` 起补齐了三份工程治理工件：
  - `module-owner-matrix-2026-03-27.md`
  - `api-surface-review-checklist.md`
  - `build-time-baseline-and-diff-entrypoints-2026-03-27.md`
- 同日也记录了当前 build-time 基线样本：`docs/refactor/evidence/build-time-baseline-2026-03-27.json`

### 已兑现项
- Gradle 模块图与 `app` 薄壳边界已兑现。
- `feature-home / search / login / book / reader / rn-host / welfare` 的阶段目标已兑现。
- `module owner` 已从口头共识升级为矩阵工件。
- `API surface` 已从隐含 review 规则升级为正式 checklist。
- `build-time baseline / diff` 已不再只有 Phase 7 计划项，而是有 Phase 5 当天样本入口。

### 仍未兑现项
| 条目 | 当前执行动作 | 当前状态 |
| --- | --- | --- |
| `core-network` 深化到更完整共享基础设施 | 继续保留为后续 core governance 深化线 | `carried debt` |
| `core-bridge / core-bridge-contract` 的更强自动化护栏 | 继续依赖 contract tests + control-plane 文档，尚无脚本化 schema manifest | `carried debt` |
| CODEOWNERS / reviewer 自动分发 / build diff 自动化 | 当前只落了文档工件，还没到平台自动化 | `carried debt` |

### 偏差原因
- 真实模块化优先保证 `app` 仍是稳定 composition root，而不是按蓝图理想顺序机械推进。
- 当前阶段先把治理工件落到 repo 内，未同步上升为平台级自动护栏。
- 更完整的 build-efficiency / artifact diff 体系仍属于 `Phase 7` 的专项范围。

### 本轮要关闭的项
- `module owner / API surface / build-time governance`
  - 本轮已由三份正式工程工件关闭“延期承接但没有落点”的状态。

### 本轮关闭后的残余 debt
- `core-network` 与 `core-bridge` 的进一步深化仍在。
- owner / API / build-time 的自动化强度仍不足，但已经不是“没有工件”，而是“还没进入平台护栏”。
- `Phase 7` 仍保持 `planned`，后续负责更完整的 artifact / dependency / build 治理。

## Phase 6 性能专项治理
### 原始蓝图目标
- 在结构稳定基础上做真实收益优化与证据闭环，而不是边改边猜。

### 当前仓库事实
- `Wave 1-3` 的代码优化已落地，并同步到 `phase-6-optimization-addendum-2026-03-27.md`。
- `2026-03-27` 新增设备侧证据总入口：
  - `docs/refactor/phase-6/device-evidence-addendum-2026-03-27.md`
- `2026-03-27` 同时新增两份治理落点：
  - `docs/refactor/phase-6/startup-deferred-task-catalog-2026-03-27.md`
  - `docs/refactor/phase-6/rn-host-root-view-cache-policy-2026-03-27.md`
- 当天设备样本已经覆盖：
  - Startup
  - Search `INITIAL_ENTRY / CATEGORY_SWITCH / FILTER_APPLY`
  - Welfare / WebView 首开与复开
  - RN Host `COLD_OPEN / OPEN / REUSED`
  - Reader `init / settings_update`
- 数据库与缓存治理报告已增强为 recommendation / warning 入口，但还不是最终收益证明。

### 已兑现项
- startup / scroll / search / reader / welfare / rn host / bridge 的基线与预算入口已建立。
- `Wave 1-3` 的首轮优化已经完成。
- 本轮 device evidence 已把关键路径从“仅文档/单测可见”推进到“有当天设备样本”。
- 数据库 / 缓存治理入口已从静态盘点升级为 summary + recommendation / warning。
- Startup 首帧后任务已经从布尔开关升级为正式任务清单。
- RN Host 返回时的 root view cache 语义已经从隐含规则升级为显式 policy。

### 仍未兑现项
| 条目 | 当前执行动作 | 当前状态 |
| --- | --- | --- |
| Search `LOAD_MORE` 设备样本 | 当天对 `的 / 天 / 王 / 都市` 真实 query 探针均返回 `hasMore=false`，保留待补 | `carried debt` |
| Reader `flip` 直接设备样本 | 已尝试 swipe，但未抓到可信 trace，继续保留 | `carried debt` |
| Startup 首帧时间进一步收敛 | 任务清单已 formalize，但监控仍给出“首帧渲染时间较长”建议 | `carried debt` |
| Welfare / WebView 更深层 cache / cookie / benchmark 复盘 | 已有首开/复开样本，尚未形成专项 benchmark | `carried debt` |
| 数据库索引收益、`FTS4` 最优性、cleanup 对 IO / 内存 / 电量收益复盘 | 当前仍停在治理报告增强，不是最终收益证明 | `carried debt` |

### 偏差原因
- `Phase 6` 真实策略更偏“基线 + 预算 + 低风险优化 + 设备证据补齐”，不是一次性把所有热点都打成硬门禁。
- 真实设备样本受后端数据集与交互路径限制，`Search LOAD_MORE`、`Reader flip` 仍不能假装已经拿到。
- 数据库与缓存治理更接近“风险识别入口”，而不是完整性能科学实验。

### 本轮要关闭的项
- `Wave 1-3` 首轮优化收敛
- Startup / Search / Welfare-WebView / RN Host / Reader 的设备侧样本补齐
- `COLD_OPEN / OPEN / REUSED` 的宿主页语义从单测/日志设计推进为当天设备证据
- Startup 首帧后任务清单正式化
- RN Host 返回缓存策略正式化

### 本轮关闭后的残余 debt
- `Search LOAD_MORE` 与 `Reader flip` 仍需更稳定的设备路径或专用探针。
- Welfare / WebView 仍缺专项 benchmark。
- Startup 虽已有正式任务清单，但仍需要继续压低首帧时间。
- RN Host 虽已有返回缓存 policy，但更完整的 root view 生命周期治理仍待继续沉淀。
- 数据库 / 缓存仍缺真实收益复盘。
- 这些项都应继续留在 `Phase 6` backlog，不应误塞给 `Phase 7`。

## 总结
- 当前正确口径仍然是：
  - `Stage 3 = validated`
  - `Phase 5 = validated`
  - `Phase 6 = validated`
  - `Phase 7 = planned`
- 但蓝图视角下的剩余事实也应明确保留：
  - `Phase 3` 仍有基础设施 carried debt
  - `Phase 4` 的结构性 debt 已治理化，但没有被“全部清零”
  - `Phase 5` 的治理工件已补齐，但自动化与 deeper core governance 仍在后续
  - `Phase 6` 已完成主目标与当天设备证据补齐，但仍存在真实的持续优化 backlog
