# Phase 4 拆分结果矩阵

## 摘要
- 统计日期：`2026-03-21`
- 目标：把 `V4-01 / V4-02 / V4-03` 的代码成果收成一份可直接验证的结果矩阵
- 说明：
  - `Phase 4` 的关闭标准不是“目标类行数必须大幅下降”，而是“职责边界已被抽离、契约保持不变、验证链成立”
  - 2026-03-21 的 `LOC` 会受到 import 展开与静态债治理影响，因此只作为辅助事实，不作为单独关闭门槛

## 结果矩阵
| Target | 2026-03-19 基线 | 2026-03-21 当前规模 | 已抽离职责 | 结果 |
| --- | --- | --- | --- | --- |
| `NavigationBridgeModule` | `1095` 行 | `1180` 行 | `NavigationBridgeFacade`、`NavigationRouteDelegate`、`NavigationQueryDelegate`、`NavigationHostDelegate`、`SelectionMenuDelegate`、`NavigationContentQueryDelegate`、`NavigationAuthorDelegate`、`NavigationAiDelegate`、`NavigationThemeDelegate` | Bridge 已形成 facade + delegates 的稳定骨架，关闭判断以职责抽离和契约兼容为准 |
| `HomeViewModel` | `827` 行 | `832` 行 | `HomeStateProjector`、`HomeInitialLoadCoordinator`、`HomeRefreshCoordinator`、`HomePagingCoordinator` | 首页的状态投影、首次加载、刷新、分页已解耦为独立协作者 |
| `SearchRepository` | `718` 行 | `468` 行 | `SearchHistoryStore`、`SearchResultCacheStore`、`SearchRankingRepository`、`SearchQueryRepository` | 搜索历史、缓存、榜单、主查询职责已分离 |
| `NetworkCacheManager` | `1242` 行 | `1104` 行 | `CacheVersionMigrator`、`IncrementalSyncCoordinator`、`CacheCleanupCoordinator`、`CacheStatsReporter` | 缓存迁移、增量同步、清理和统计已拆为独立职责 |
| `ReaderViewModel` 轻触边界 | `925` 行 | `1036` 行 | `ReaderSettingsCoordinator`、`ReaderHistoryCoordinator`、`ReaderMappingHelper` | Reader 已完成 settings/history/mapping 轻触减重，未触碰分页/翻页核心行为 |

## 契约保持情况
### Bridge / Host
- `NavigationBridge` 对 RN 的模块名、方法名、Promise resolve/reject 语义保持不变
- route、Bridge event、payload 字段名未变
- `profile -> settings -> aipage` 宿主页链路已完成正向验证

### Feature / Reader
- `Home`、`Search`、`Cache` 的外部业务语义保持不变
- Reader 只处理 helper/mapping/settings/history，未重写分页、翻页或核心渲染

## 验证入口
- `android/gradlew.bat app:testDebugUnitTest`
- `android/app/src/test/java/com/novel/page/home/viewmodel/HomeStateProjectorTest.kt`
- `android/app/src/test/java/com/novel/page/home/viewmodel/HomeInitialLoadCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/page/home/viewmodel/HomeRefreshCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/page/home/viewmodel/HomePagingCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/page/search/repository/SearchHistoryStoreTest.kt`
- `android/app/src/test/java/com/novel/page/search/repository/SearchResultCacheStoreTest.kt`
- `android/app/src/test/java/com/novel/page/search/repository/SearchRankingRepositoryTest.kt`
- `android/app/src/test/java/com/novel/page/search/repository/SearchQueryRepositoryTest.kt`
- `android/app/src/test/java/com/novel/page/read/viewmodel/ReaderSettingsCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/page/read/viewmodel/ReaderHistoryCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/page/read/viewmodel/ReaderMappingHelperTest.kt`
- `android/app/src/test/java/com/novel/utils/network/cache/CacheVersionMigratorTest.kt`
- `android/app/src/test/java/com/novel/utils/network/cache/IncrementalSyncCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/utils/network/cache/CacheCleanupCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/utils/network/cache/CacheStatsReporterTest.kt`

## 关闭结论
- `V4-01`
  - 包边界骨架稳定，关闭证据成立
- `V4-02`
  - 指定超大类拆分按 `Phase 4` 范围要求完成，关闭证据成立
- `V4-03`
  - `BridgeFacade` 与 delegate 骨架完成，且契约兼容证据成立
