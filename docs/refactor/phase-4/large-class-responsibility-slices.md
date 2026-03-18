# Phase 4 超大类职责切片图

## 目标
- 让后续执行者不用重新读完整大类，也能知道应该先切哪一刀。
- 为 `Wave 2 / Wave 3 / Wave 4` 的原子主题提供职责切片依据。

## 1. NavigationBridgeModule
### 当前职责切片
- `Selection Menu`
  - `attachSelectionMenu`
  - `detachSelectionMenu`
- `纯导航路由`
  - `goToLogin`
  - `navigateToSettings`
  - `navigateBack`
  - `navigateToTimedSwitch`
  - `navigateToHelpSupport`
  - `navigateToPrivacyPolicy`
  - `navigateToHistory`
  - `navigateToMessage`
  - `navigateToBecomeWriter*`
  - `navigateToWritePage`
  - `navigateToAIPage`
  - `navigateToReader`
  - 其余 `navigateTo*`
- `Bridge 读操作`
  - `getHomeBooksHighPriority`
  - `getReadingHistory`
  - `getAuthorStatus`
  - `getAuthorBooks`
  - `getBookCategories`
  - `searchBooks`
  - `getBridgeStatus`
  - `getCurrentActualTheme`
  - `getCurrentNightMode`
- `缓存与组件管理`
  - `clearComponentCache`
  - `clearAllComponentCache`
  - `registerComponent`
  - `notifyRouteChanged`
- `AI / 作者操作`
  - `aiPolish`
  - `aiExpand`
  - `aiCondense`
  - `aiContinue`
  - `registerAuthor`
- `主题与 Promise 协调`
  - `changeTheme`
  - `observeEffectForPromise`

### 推荐切分顺序
1. `NavigationBridgeFacade`
2. `NavigationRouteDelegate`
3. `NavigationQueryDelegate`
4. `NavigationAiDelegate`
5. `NavigationHostDelegate`
6. `SelectionMenuDelegate`

## 2. HomeViewModel
### 当前职责切片
- `MVI 基础与状态投影`
  - `createInitialState`
  - `getReducer`
  - `screenState`
  - `uiState`
- `启动与恢复`
  - `init`
  - `loadInitialData`
  - `restoreDataIfNeeded`
- `刷新与筛选`
  - `refreshData`
  - `selectCategoryFilter`
  - `selectRankType`
  - `loadCategoryFilters`
- `分页`
  - `loadMoreRecommend`
  - `loadMoreHomeRecommend`
  - `createCategoryPagingData`
  - `updateCategoryPagingData`
  - `refreshHomeRecommendPaging`
- `推荐数据加载`
  - `loadHomeRecommendBooks`
  - `getCurrentCategoryId`
- `外部同步`
  - `collectHomeData`
  - RN 数据同步相关逻辑

### 推荐切分顺序
1. `HomeIntentCoordinator`
2. `HomeInitialLoader`
3. `HomeRefreshCoordinator`
4. `HomePagingCoordinator`
5. `HomeStateProjector`

## 3. SearchRepository
### 当前职责切片
- `搜索结果缓存`
  - `generateCacheKey`
  - `isCacheValid`
  - `getCachedSearchResult`
  - `cacheSearchResult`
  - `cleanExpiredCache`
  - `clearSearchResultCache`
  - `isSearchResultCacheAvailable`
- `搜索历史`
  - `getSearchHistory`
  - `addSearchHistory`
  - `clearSearchHistory`
  - `getHistoryExpansionState`
  - `saveHistoryExpansionState`
- `榜单聚合`
  - `getNovelRanking`
  - `getDramaRanking`
  - `getNewBookRanking`
  - `getAllRankingData`
- `搜索查询`
  - `searchBooksWithCache`
  - `searchBooks`
  - `refreshSearchResults`
  - `isSearchCacheAvailable`
- `缓存清理桥接`
  - `clearSearchCache`

### 推荐切分顺序
1. `SearchHistoryStore`
2. `SearchResultCacheStore`
3. `SearchRankingRepository`
4. `SearchQueryRepository`

## 4. NetworkCacheManager
### 当前职责切片
- `版本迁移`
  - `handleCacheVersionMigration`
  - `migrateCacheVersion`
- `缓存核心存储`
  - `calculateContentHash`
  - 读写缓存条目相关核心逻辑
- `增量同步`
  - `getDataWithIncrementalSync`
  - `shouldRefreshCache`
  - `isCacheStale`
  - `isCacheExpired`
- `缓存清理`
  - `cleanExpiredCaches`
  - `performSmartCleanup`
  - `performLRUCleanup`
  - `performTimeBasedCleanup`
  - `performHybridCleanup`
  - `performStoragePressureCleanup`
- `后台清理调度`
  - `startBackgroundCleanup`
  - `shouldPerformCleanup`
  - `stopBackgroundCleanup`
- `状态与统计`
  - `updateCacheState`
  - `calculateCacheSize`
  - `getCleanupStats`

### 推荐切分顺序
1. `CacheVersionMigrator`
2. `CacheEntryStore`
3. `IncrementalSyncCoordinator`
4. `CacheCleanupCoordinator`
5. `CacheStatsReporter`

## 5. ReaderViewModel
### 当前职责切片
- `初始化`
  - `handleInitReaderAsync`
- `翻页与切章`
  - `handlePageFlipAsync`
  - 各种 chapter switch / seek
- `设置与进度`
  - `handleUpdateSettingsAsync`
  - `handleSaveProgressAsync`
  - `handleSaveToHistoryAsync`
- `分页与虚拟页辅助`
  - `buildVirtualPages`
  - `splitContentAndBuildVirtualPages` 等相关流程
- `评论与周边加载`
  - `handleLoadBookReviewsAsync`

### Phase 4 允许切片
- `settings`
- `history`
- `mapping`
- `helper`

### Phase 4 禁止切片
- 分页算法
- 翻页行为
- 核心渲染流程

## 下一步建议
- 先基于本文件产出：
  - `BridgeFacade` / delegates 切片表
  - `HomeViewModel` / `SearchRepository` / `NetworkCacheManager` 的 Atomic Theme 清单
