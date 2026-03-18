# Phase 4 原子拆分 Backlog

## 目标
- 把 `Wave 2 / Wave 3 / Wave 4` 的代码工作预先拆成可直接派发的原子主题。
- 确保后续即使用户不在线，也能持续按单主题推进，而不是每次重新决定先拆哪一刀。

## 使用规则
- 每次只允许选择一个 `Atomic Theme` 执行。
- 每个主题都必须：
  - 绑定 `Wave`
  - 绑定 `Owner Agent`
  - 绑定 `Lock ID`
  - 有明确的退出条件
  - 能生成 `Rollback ID`

## Wave 2 Backlog
| Atomic Theme ID | Theme | Owner | Lock | 退出条件 |
| --- | --- | --- | --- | --- |
| W2-A01 | 新增 `NavigationBridgeFacade` 壳，保留现有模块名与对外签名 | `BridgeFacadeSplitAgent` | `LOCK-BRIDGE-FACADE` | facade 壳存在，外部协议不变 |
| W2-A02 | 抽离 `NavigationRouteDelegate` | `BridgeFacadeSplitAgent` | `LOCK-BRIDGE-FACADE` | 纯导航方法迁出，导航协议不变 |
| W2-A03 | 抽离 `NavigationQueryDelegate` | `BridgeFacadeSplitAgent` | `LOCK-BRIDGE-FACADE` | 查询类 Promise 迁出，contract 回归通过 |
| W2-A04 | 抽离 `NavigationHostDelegate` | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | 组件缓存/注册/route 通知迁出 |
| W2-A05 | 抽离 `SelectionMenuDelegate` | `BridgeFacadeSplitAgent` | `LOCK-BRIDGE-FACADE` | `WritePageSelectionMenuAction` 行为不变 |
| W2-A06 | 宿主页挂载链验证：`profile` / `settings` | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | 首开无白屏，证据归档完成 |
| W2-A07 | 宿主页挂载链验证：作者/AI 场景 | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | 写作或 AI 宿主页有正向证据 |

## Wave 3 Backlog
| Atomic Theme ID | Theme | Owner | Lock | 退出条件 |
| --- | --- | --- | --- | --- |
| W3-A01 | 抽离 `HomeIntentCoordinator` / 启动与刷新协调逻辑 | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | `HomeViewModel` 主体减重，行为不变 |
| W3-A02 | 抽离 `HomePagingCoordinator` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 分页逻辑边界独立，分页行为不变 |
| W3-A03 | 抽离 `HomeStateProjector` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 状态投影独立，UI 语义不变 |
| W3-A04 | 抽离 `SearchHistoryStore` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 搜索历史逻辑独立，存储契约不变 |
| W3-A05 | 抽离 `SearchResultCacheStore` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 搜索缓存逻辑独立，结果不变 |
| W3-A06 | 抽离 `SearchRankingRepository` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 榜单聚合逻辑独立 |
| W3-A07 | 抽离 `SearchQueryRepository` | `FeatureBoundarySplitAgent` | `LOCK-HOME-SEARCH-SPLIT` | 搜索查询与刷新逻辑独立 |
| W3-A08 | 抽离 `CacheVersionMigrator` / `CacheEntryStore` | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | 版本迁移与读写边界独立 |
| W3-A09 | 抽离 `IncrementalSyncCoordinator` | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | 增量同步边界独立 |
| W3-A10 | 抽离 `CacheCleanupCoordinator` / `CacheStatsReporter` | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | 清理与统计边界独立 |

## Wave 4 Backlog
| Atomic Theme ID | Theme | Owner | Lock | 退出条件 |
| --- | --- | --- | --- | --- |
| W4-A01 | 抽离 Reader `settings` 边界 | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | Reader 设置读写与 ViewModel 主体分离 |
| W4-A02 | 抽离 Reader `history` 边界 | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | 历史读写边界独立 |
| W4-A03 | 抽离 Reader `mapping / helper` 边界 | `CacheReaderLightAgent` | `LOCK-CACHE-READER-LIGHT` | helper 与 mapping 脱离主 ViewModel |
| W4-A04 | 建立生产 mock inventory | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | mock 清单形成，范围冻结 |
| W4-A05 | 清理触达范围内生产 mock | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | 正式路径 mock 清零 |
| W4-A06 | Phase 4 touched-files 静态债清零 | `HostRiskQualityAgent` | `LOCK-HOST-QUALITY` | touched files lint / detekt 为 0 |
| W4-A07 | 输出 Phase 4 closeout 资料 | `LeaderAgent` | `LOCK-REFRACTOR-DOCS` | closeout 文档可评审 |
| W4-A08 | 输出 Phase 5 进入条件清单 | `LeaderAgent` | `LOCK-REFRACTOR-DOCS` | 进入条件客观化 |

## 推荐执行顺序
1. `W2-A01`
2. `W2-A02`
3. `W2-A03`
4. `W2-A06`
5. `W2-A07`
6. `W3-A01 ~ W3-A10`
7. `W4-A01 ~ W4-A08`

## 当前建议下一步
- `Wave 2 / W2-A01`
  - 先做 `NavigationBridgeFacade` 兼容壳设计与代码入口清点
