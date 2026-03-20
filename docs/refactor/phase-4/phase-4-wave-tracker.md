# Phase 4 Wave Tracker

## 当前状态
- 当前阶段：`Phase 4`
- 当前状态：`in_progress`
- 当前激活波次：`Wave 3`
- 自治模式：`enabled`
- 默认编制：`1 Leader + 4 helpers`
- 当前建议下一原子主题：`Wave 3 / W3-S02 / SearchResultCacheStore`

## Wave 总表
| Wave | Status | Goal | Primary Owners | Primary Locks | Exit Evidence |
| --- | --- | --- | --- | --- | --- |
| Wave 1 | `ready_for_validation` | 建立边界骨架、拆分地图、BridgeFacade 外围映射 | `BridgeFacadeSplitAgent`, `FeatureBoundarySplitAgent` | `LOCK-BRIDGE-FACADE`, `LOCK-HOME-SEARCH-SPLIT` | 包边界图、职责切片图、delegate 映射表 |
| Wave 2 | `in_progress` | 收口 Bridge 与宿主页边界 | `BridgeFacadeSplitAgent`, `HostRiskQualityAgent` | `LOCK-BRIDGE-FACADE`, `LOCK-HOST-QUALITY` | BridgeFacade 接口、兼容映射、host 风险验证清单 |
| Wave 3 | `in_progress` | 拆 Home/Search/Cache 超大类 | `FeatureBoundarySplitAgent`, `CacheReaderLightAgent` | `LOCK-HOME-SEARCH-SPLIT`, `LOCK-CACHE-READER-LIGHT` | 拆分前后职责对照、定向测试、静态债结果 |
| Wave 4 | `planned` | Reader 轻触减重、mock 清理、阶段收尾 | `CacheReaderLightAgent`, `HostRiskQualityAgent`, `LeaderAgent` | `LOCK-CACHE-READER-LIGHT`, `LOCK-HOST-QUALITY`, `LOCK-REFRACTOR-DOCS` | mock 清单、closeout 文档、Phase 5 进入条件 |

## Wave Summary Rules
- 每次波次切换都必须：
  - 更新本文件
  - 更新 `decision-log.md`
  - 同步 `phase-3-4-validation-board.md`
- 每个波次至少要记录：
  - 当前状态
  - 目标
  - 已完成原子主题
  - 当前 blocker
  - 下一步

## 当前未关闭风险
- `profile-host / RN Host` 当前未形成正式验证证据
- 当前 shell 的 `adb devices -l` 返回空设备列表，`W2-A11` 暂时不能实跑
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 尚未完成目标拆分
- 静态债第二轮收敛尚未开始

## 已完成的 Wave 1 资料
- `docs/refactor/phase-4/package-boundary-map.md`
- `docs/refactor/phase-4/large-class-responsibility-slices.md`
- `docs/refactor/phase-4/bridge-facade-delegate-map.md`
- `docs/refactor/phase-4/host-risk-validation-matrix.md`
- `docs/refactor/phase-4/atomic-split-backlog.md`

## 已完成的原子主题
- `Wave 2 / W2-A01`
  - 新增 `NavigationBridgeFacade` 兼容壳
  - 当前先收口 `goToLogin`、`navigateToSettings`、`navigateBack`
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/facade/NavigationBridgeFacade.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/facade/NavigationBridgeFacadeTest.kt`
    - commit: `5092915`
- `Wave 2 / W2-A02`
  - 新增 `NavigationRouteDelegate` 最小实现
  - 当前先收口 `timed_switch`、`help_support`、`privacy_policy`、`history`、`message`
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationRouteDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationRouteDelegateTest.kt`
    - commit: `acbcfb7`
- `Wave 2 / W2-A03`
  - 新增 `NavigationQueryDelegate` 最小实现
  - 当前先收口 `getBridgeStatus`、`getCurrentActualTheme`、`getCurrentNightMode`
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationQueryDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationQueryDelegateTest.kt`
    - commit: `4743b7c`
- `Wave 2 / W2-A04`
  - 新增 `NavigationHostDelegate` 最小实现
  - 当前先收口 `registerComponent`、`notifyRouteChanged`、`clearComponentCache`、`clearAllComponentCache`
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationHostDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationHostDelegateTest.kt`
    - commit: `673cd4f`
- `Wave 2 / W2-A05`
  - 新增 `SelectionMenuDelegate` 最小实现
  - 当前先收口 `attachSelectionMenu` / `detachSelectionMenu` 中的 action 解析与事件 payload 组装
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/SelectionMenuDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/SelectionMenuDelegateTest.kt`
    - commit: `c3a2bd1`
- `Wave 2 / W2-A06`
  - 新增 `NavigationContentQueryDelegate` 最小实现
  - 当前先收口 `getReadingHistory`、`getHomeBooksHighPriority`、`getAuthorStatus`、`getAuthorBooks`、`getBookCategories`、`searchBooks` 的结果组装层
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationContentQueryDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationContentQueryDelegateTest.kt`
    - commit: `99967fb`
- `Wave 2 / W2-A07`
  - 新增 `NavigationAuthorDelegate` 最小实现
  - 当前先收口 `navigateToBecomeWriterWithFlag`、`navigateToWritePage`、`navigateToBookManage` 和 `registerAuthor` 的请求构造
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationAuthorDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationAuthorDelegateTest.kt`
    - commit: `46ab116`
- `Wave 2 / W2-A10`
  - 宿主页验证准备与证据模板已落地
  - 当前已固定 `profile`、`settings`、作者/AI 三类验证模板与 checklist
  - 证据：
    - `docs/refactor/phase-4/host-risk-run-profile-template.md`
    - `docs/refactor/phase-4/host-risk-run-settings-template.md`
    - `docs/refactor/phase-4/host-risk-run-author-ai-template.md`
    - `docs/refactor/phase-4/host-risk-evidence-checklist.md`
    - commit: `9ead07c`
- `Wave 2 / W2-A08`
  - 新增 `NavigationAiDelegate` 最小实现
  - 当前先收口 `aiPolish`、`aiExpand`、`aiCondense`、`aiContinue` 的成功/失败结果判断
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationAiDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationAiDelegateTest.kt`
    - commit: `49c5a2c`
- `Wave 2 / W2-A09`
  - 新增 `NavigationThemeDelegate` 最小实现
  - 当前先收口 `changeTheme` 中 `SettingsEffect.ShowToast / ShowError` 到 Promise 的映射逻辑
  - 证据：
    - `android/app/src/main/java/com/novel/rn/bridge/delegate/NavigationThemeDelegate.kt`
    - `android/app/src/test/java/com/novel/rn/bridge/delegate/NavigationThemeDelegateTest.kt`
    - commit: `4bbdbda`
- `Wave 3 / W3-H01`
  - 新增 `HomeStateProjector` 最小实现
  - 当前先将 `HomeStateAdapter` 中的首页状态投影逻辑抽离为独立 projector，不改变 UI 状态语义
  - 证据：
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeStateProjector.kt`
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeStateAdapter.kt`
    - `android/app/src/test/java/com/novel/page/home/viewmodel/HomeStateProjectorTest.kt`
    - commit: `b49247f`
- `Wave 3 / W3-S01`
  - 新增 `SearchHistoryStore` 最小实现
  - 当前先将 `SearchRepository` 中搜索历史与展开态的 JSON 读写、去重置顶、10 条上限和异常兜底逻辑收口到独立 store
  - 证据：
    - `android/app/src/main/java/com/novel/page/search/repository/SearchHistoryStore.kt`
    - `android/app/src/main/java/com/novel/page/search/repository/SearchRepository.kt`
    - `android/app/src/test/java/com/novel/page/search/repository/SearchHistoryStoreTest.kt`
    - commit: `75eb67f`
- `Wave 3 / W3-C01`
  - 新增 `CacheVersionMigrator` 最小实现
  - 当前先将 `NetworkCacheManager` 中缓存版本迁移与异常回退逻辑抽离为独立 migrator，不改变旧缓存版本升级与全量清理语义
  - 证据：
    - `android/app/src/main/java/com/novel/utils/network/cache/CacheVersionMigrator.kt`
    - `android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt`
    - `android/app/src/test/java/com/novel/utils/network/cache/CacheVersionMigratorTest.kt`
    - commit: `c754ef3`
- `Wave 3 / W3-H02`
  - 新增 `HomeInitialLoadCoordinator` 最小实现
  - 当前先将 `HomeViewModel.loadInitialData()` 中首次加载 orchestration、成功 intent 序列和失败兜底逻辑收口到独立 coordinator，不改变首页初始加载语义
  - 证据：
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeInitialLoadCoordinator.kt`
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeViewModel.kt`
    - `android/app/src/test/java/com/novel/page/home/viewmodel/HomeInitialLoadCoordinatorTest.kt`
    - commit: `11ae6f2`
- `Wave 3 / W3-C02`
  - 新增 `IncrementalSyncCoordinator` 最小实现
  - 当前先将 `NetworkCacheManager.getDataWithIncrementalSync()` 中条件请求 orchestration、304/Modified/Error 分支和异常兜底逻辑收口到独立 coordinator，不改变增量同步对外返回语义
  - 证据：
    - `android/app/src/main/java/com/novel/utils/network/cache/IncrementalSyncCoordinator.kt`
    - `android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt`
    - `android/app/src/test/java/com/novel/utils/network/cache/IncrementalSyncCoordinatorTest.kt`
    - commit: `0a24aad`
- `Wave 3 / W3-H03`
  - 新增 `HomeRefreshCoordinator` 最小实现
  - 当前先将 `HomeViewModel.refreshData()` 中刷新 orchestration、成功/失败 intent 序列、toast 和缓存更新逻辑收口到独立 coordinator，不改变下拉刷新语义
  - 证据：
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeRefreshCoordinator.kt`
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeViewModel.kt`
    - `android/app/src/test/java/com/novel/page/home/viewmodel/HomeRefreshCoordinatorTest.kt`
    - commit: `39b957f`
- `Wave 3 / W3-C03`
  - 新增 `CacheCleanupCoordinator` 最小实现
  - 当前先将 `NetworkCacheManager.performSmartCleanup()` 中 cleanup 策略分发、统计累加和耗时汇总逻辑收口到独立 coordinator，不改变清理策略和对外统计语义
  - 证据：
    - `android/app/src/main/java/com/novel/utils/network/cache/CacheCleanupCoordinator.kt`
    - `android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt`
    - `android/app/src/test/java/com/novel/utils/network/cache/CacheCleanupCoordinatorTest.kt`
    - commit: `aa59be7`
- `Wave 3 / W3-H04`
  - 新增 `HomePagingCoordinator` 最小实现
  - 当前先将 `HomeViewModel` 中分类推荐分页与首页推荐分页的“加载更多” orchestration、下一页计算和成功/失败 intent 组装逻辑收口到独立 coordinator，不改变分页语义
  - 证据：
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomePagingCoordinator.kt`
    - `android/app/src/main/java/com/novel/page/home/viewmodel/HomeViewModel.kt`
    - `android/app/src/test/java/com/novel/page/home/viewmodel/HomePagingCoordinatorTest.kt`
    - commit: `3a76dd0`

## 下一步
- 按静默连续推进协议切到 Wave 3：
  - `W3-S02` `SearchResultCacheStore`
  - `W3-S03` `SearchRankingRepository`
- `W2-A11` 保持待补证状态，直到设备可用或成为最后唯一 blocker
