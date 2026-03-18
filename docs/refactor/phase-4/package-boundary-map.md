# Phase 4 包边界图与迁移映射

## 目标
- 为 `Wave 1` 提供唯一的逻辑模块化边界参考。
- 在不改 UI 语义、不改业务功能语义的前提下，先锁定 Phase 4 应当形成的包内边界。
- 给 `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 的拆分提供稳定迁移落点。

## 当前边界问题
- Bridge、宿主页、导航与协议兼容逻辑集中在 `NavigationBridgeModule` 与 `NavigationUtil`，跨 RN / Native 边界职责混杂。
- `HomeViewModel` 同时承担：
  - 初始加载
  - 刷新
  - 榜单/分类过滤
  - 分页数据流
  - RN 数据同步
  - 状态适配
- `SearchRepository` 同时承担：
  - 搜索历史存储
  - 榜单聚合
  - 搜索结果缓存
  - 搜索接口调用
  - UI 状态辅助
- `NetworkCacheManager` 同时承担：
  - 内存/磁盘缓存
  - 增量同步
  - LRU/TTL 清理
  - 后台清理调度
  - 清理统计

## Phase 4 目标边界
### 1. Bridge 边界
- 目标包：
  - `com.novel.rn.bridge.facade`
  - `com.novel.rn.bridge.delegate`
  - `com.novel.rn.bridge.host`
- 责任拆分：
  - `facade`
    - 对 RN 暴露唯一 Bridge 出口
    - 承接 `NavigationBridgeModule` 的外部接口兼容
  - `delegate`
    - 按职责分拆导航、宿主页、缓存管理、AI/作者能力、阅读历史、主题/设置桥接
  - `host`
    - 宿主页挂载、缓存复用、Theme 注入、ReactRootView 生命周期

### 2. 首页边界
- 目标包：
  - `com.novel.page.home.coordinator`
  - `com.novel.page.home.loader`
  - `com.novel.page.home.paging`
  - `com.novel.page.home.state`
- 责任拆分：
  - `coordinator`
    - Intent 调度、主流程编排
  - `loader`
    - 初始加载、刷新、分类/榜单切换
  - `paging`
    - 推荐分页、分类分页
  - `state`
    - `HomeStateAdapter` / UI 投影 / 状态汇总

### 3. 搜索边界
- 目标包：
  - `com.novel.page.search.history`
  - `com.novel.page.search.ranking`
  - `com.novel.page.search.cache`
  - `com.novel.page.search.query`
- 责任拆分：
  - `history`
    - 搜索历史与展开状态
  - `ranking`
    - 榜单聚合与 DTO 转换
  - `cache`
    - 搜索结果缓存、过期控制
  - `query`
    - 搜索参数、网络查询、刷新逻辑

### 4. 缓存边界
- 目标包：
  - `com.novel.utils.network.cache.store`
  - `com.novel.utils.network.cache.sync`
  - `com.novel.utils.network.cache.cleanup`
  - `com.novel.utils.network.cache.stats`
- 责任拆分：
  - `store`
    - 缓存条目、序列化、读写
  - `sync`
    - 增量同步、版本迁移、条件请求
  - `cleanup`
    - 清理策略、后台清理调度
  - `stats`
    - 清理统计与可观测性数据

### 5. Reader 轻触边界
- 目标包：
  - `com.novel.page.read.mapping`
  - `com.novel.page.read.settings`
  - `com.novel.page.read.history`
  - `com.novel.page.read.helper`
- 限制：
  - 仅抽离 helper/mapping/settings/history 边界
  - 禁止触碰分页、翻页、核心渲染语义

## 迁移映射
| 当前对象 | 目标落点 | Phase 4 Wave | Owner | 备注 |
| --- | --- | --- | --- | --- |
| `NavigationBridgeModule` | `bridge.facade + bridge.delegate` | Wave 2 | `BridgeFacadeSplitAgent` | 保持旧协议兼容壳 |
| `ReactNativePage` | `bridge.host` | Wave 2 | `HostRiskQualityAgent` | 宿主页验证事实来源 |
| `NavigationUtil` | `bridge.host` / `route mapping docs` | Wave 2 | `HostRiskQualityAgent` | 不改 route 语义 |
| `HomeViewModel` | `home.coordinator + loader + paging + state` | Wave 3 | `FeatureBoundarySplitAgent` | 先拆协调与加载 |
| `SearchRepository` | `search.history + ranking + cache + query` | Wave 3 | `FeatureBoundarySplitAgent` | 先拆缓存与历史 |
| `NetworkCacheManager` | `cache.store + sync + cleanup + stats` | Wave 3 | `CacheReaderLightAgent` | 清理策略最后拆 |
| `ReaderViewModel` 辅助边界 | `read.mapping + settings + history + helper` | Wave 4 | `CacheReaderLightAgent` | 仅轻触，不改核心行为 |

## 不允许跨越的边界
- 不允许在 `Phase 4` 中把宿主页问题和 Route 语义修改混成同一主题。
- 不允许在拆 `NavigationBridgeModule` 时顺手修改 Bridge payload。
- 不允许在拆 `HomeViewModel` / `SearchRepository` 时引入新的 mock。
- 不允许在 Reader 边界抽离时触碰分页、翻页、渲染策略。

## 当前建议下一步
- `Wave 1 / Atomic Theme 01`
  - 产出 `BridgeFacade` 与 delegate 职责切片表
- `Wave 1 / Atomic Theme 02`
  - 产出 `HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 的职责切片图
