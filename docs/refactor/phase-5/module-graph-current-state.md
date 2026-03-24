# Phase 5 模块图现状（2026-03-24 reopen）

## 当前已落地图
- `:app`
  - 组合入口与宿主模块
  - 当前依赖：
    - `:core-common`
    - `:core-storage`
    - `:core-network`
    - `:macrobenchmark`
- `:core-common`
  - 共享基础能力
  - 当前承载：
    - `com.novel.core.*` 稳定包装
    - `com.novel.core.mvi.*`
    - `com.novel.core.domain.*`
    - `com.novel.core.result.*`
    - `com.novel.core.concurrency.*`
    - `com.novel.core.logging.CoreLogger`
- `:core-ui`
  - 通用主题与基础 Compose 组件
  - 当前承载：
    - `com.novel.ui.theme.*`
    - `com.novel.utils.StableCallbacks`
    - `com.novel.utils.flydp`
    - `com.novel.utils.debounceClickable`
    - `NovelText / NovelTextField / NovelButton / NovelDivider`
- `:core-bridge`
  - 桥接共享层
  - 当前承载：
    - `BridgeIntent / BridgeState / BridgeEffect`
    - `BridgeReducer`
    - `BridgePromiseErrorMapper`
    - `DefaultNavigationBridgeFacade`
    - `NavigationBridgeNetworkGateway`
- `:core-storage`
  - 存储抽象与兼容层
- `:core-network`
  - 网络契约层
- `:core-bridge-contract`
  - 第一批纯桥接 contract / delegate helper
- `:macrobenchmark`
  - 基准与 profile 相关产物
- `:feature-welfare`
  - 首轮 welfare feature 模块
  - 当前承载：
    - `EnhancedErrorComponent`
    - `SkeletonLoadingComponent`
    - `WelfarePerformanceMonitor`
    - `WebViewPreloadManager`
    - `WelfareAccessibilityHelper`
    - `WelfarePageBootstrapCoordinator`
    - `WelfareWebPerformanceCoordinator`
- `:feature-search`
  - 首轮 search feature 模块
  - 当前承载：
    - `SearchPreferenceStorage`
    - `SearchParams`
    - `SearchMvi`
    - `SearchReducer`
    - `SearchResultMvi`
    - `SearchResultStateAdapter`
    - `SearchStateAdapter`
    - `SearchTriggerSource`
    - `SearchCategoryFilterLoadCoordinator`
    - `SearchPerformanceTraceCoordinator`
    - `SearchRetryPolicyCoordinator`
- `:feature-home`
  - 首轮 home feature 模块
  - 当前承载：
    - `HomeBookEntity / HomeBannerEntity / HomeCategoryEntity`
    - `HomePerformanceOptimizer`
    - `CategoryInfo`
    - `HomeCategoryFilterSupport`
    - `HomeRestoreCoordinator`
- `:feature-book`
  - 首轮 book feature 模块
  - 当前承载：
    - `BookDetailMvi`
    - `formatWordCount`
    - `BookDetailStateAdapter`
- `:feature-login`
  - 首轮 login feature 模块
  - 当前承载：
    - `LoginMvi`
    - `LoginReducer`
    - `LoginStateUpdater`
    - `LoginStateAdapter`
- `:feature-reader`
  - 首轮 reader feature 模块
  - 当前承载：
    - `PageFlipEffect`
    - `ReaderServiceConfig`
    - `ReaderStartupCoordinator`
    - `ReaderRestoreHintCoordinator`
    - `ReaderPerformanceTraceCoordinator`
- `:feature-rn-host`
  - 首轮 rn-host feature 模块
  - 当前承载：
    - `SettingsPreferenceStorage`
    - `ReactNativeThemeSyncCoordinator`
    - `ReactNativeHostPathTraceCoordinator`

## reopen 目标模块图
- 已纳入本轮深化目标、但尚未真正落地：
- 已落地但仍明显偏浅、需要继续深化：
  - `:core-network`
  - `:core-bridge`
  - `:feature-book`
  - `:feature-login`
  - `:feature-reader`
  - `:feature-home`
  - `:feature-search`
  - `:feature-welfare`
  - `:feature-rn-host`

## 当前依赖方向
- `:app -> :core-common`
- `:app -> :core-ui`
- `:app -> :core-bridge`
- `:app -> :core-bridge-contract`
- `:app -> :core-storage`
- `:app -> :core-network`
 - `:app -> :feature-book`
- `:app -> :feature-home`
 - `:app -> :feature-login`
 - `:app -> :feature-reader`
- `:app -> :feature-rn-host`
- `:app -> :feature-search`
- `:app -> :feature-welfare`
- `:app -> :macrobenchmark`
- 当前未引入新的模块环依赖。

## 已落地的共享构建约定
- `android/gradle/android-library-common.gradle`
  - 统一：
    - `compileSdk / minSdk / targetSdk`
    - Java 17
    - `consumer-rules.pro`
    - `AndroidJUnitRunner`
    - `buildConfig` 开关入口
- 当前已接入该约定的模块：
  - `:core-common`
  - `:core-storage`
  - `:core-network`

## 当前固定执行顺序
1. `doc/state sync`
2. `core-ui`
3. `deepen core-network / core-bridge`
4. `feature-home + homepage first-load fix`
5. `feature-search`
6. `feature-welfare / feature-rn-host`
7. `feature-book / feature-login`
8. `feature-reader`
9. `Community wiring + validation refresh`
10. `validation / closeout`

## 当前切口结果
- `BookService` 的空 `chapterUpdateTime` 回归已作为固定门禁保留。
- `core-storage` 已稳定。
- `core-network` 当前仍是“契约优先”首批切口，后续需要继续向共享基础设施深化。
- `core-bridge-contract` 已完成第一批纯 Kotlin bridge delegate/helper 抽离。
- `feature-welfare` 已完成两轮低风险切口，当前仍保留 `WelfarePage` 作为 app 宿主 wrapper。
- `feature-search` 已不再只停留在存储层，当前已接管搜索结果页的 trigger source、查询参数与三类性能/重试协调器。
- `feature-home` 已不再只停留在性能 helper，当前已接管首页分类语义契约、restore 判定协调器，并配合 app 侧修复首页首开榜单与推荐流在 `CACHE_FIRST` 空结果下无法自动展示的问题。
- `feature-book` 已正式落地，并开始承载书籍详情的 MVI 契约与通用格式化工具。
- `feature-login` 已正式落地，并开始承载登录页的 MVI 契约、reducer 与状态更新器。
- `feature-reader` 已正式落地，并开始承载阅读器启动/恢复/动作 trace 协调器。
- `feature-rn-host` 已不再只停留在设置存储层，当前已接管主题补发与冷热路径追踪协调器。
- `core-common` 已完成第一批共享基础抽离，`StateAdapter`、`StateHolderImpl`、`RefactorFeatureFlags / RefactorFeatureFlagDefaults` 接口层，以及 `DispatcherProvider / DefaultDispatcherProvider` 共享协程调度接口层已迁入共享层；`NovelUserDefaultsBackedRefactorFeatureFlags` 已进一步迁入 `core-storage`，当前只剩 `OptimizedDispatcherProvider` 等宿主实现仍暂留 `app`。
- `core-ui` 已完成首轮真实落地，主题体系、尺寸/点击工具与首批基础 Compose 组件已迁出 `app`。
- `core-bridge` 已完成首轮真实落地，桥接 MVI 契约、共享 reducer、Promise error mapper、bridge facade、network gateway、`BridgeStateAdapter` 与 `BridgeCoroutineScopes` 已迁出 `app`。
- `core-network` 已不再只有契约壳，`RequestIdInterceptor / NetworkTraceLogHelper / ImmutableListTypeAdapter / LegacyApiExecutor / LegacyApiServiceAdapter` 已进入共享网络层；`DefaultLegacyApiExecutor` 当前作为宿主壳留在 `app`。
- `feature-welfare` 已接管 bootstrap 与 WebView 性能协调器，不再只是组件/工具集合。
- `2026-03-21` 的首轮 closeout 只能视为 checkpoint，不应被误读为蓝图中所有目标模块均已落地。

## 当前阻塞与下一步
- 当前未解决的 `core-common` 遗留：
  - `android/app/src/main/java/com/novel/page/read/service/common/DispatcherProvider.kt`（当前仅剩 `OptimizedDispatcherProvider` 兼容入口）
- 当前 `core-network` 深化策略暂缓：
  - 直接搬迁共享网络原语时曾触发默认 `app` 编译链不稳定，已回退到上一个稳定边界，后续需要换更保守的切口。
- 下一步主线：
  - 继续深化 `core-network / core-bridge`
  - 继续扩大各 `feature-*` 内部稳定边界

## 验证证据
- `android/gradlew.bat :core-common:testDebugUnitTest`
- `android/gradlew.bat :core-bridge-contract:testDebugUnitTest`
- `android/gradlew.bat :feature-welfare:compileDebugKotlin`
- `android/gradlew.bat :feature-home:testDebugUnitTest`
- `android/gradlew.bat :feature-rn-host:testDebugUnitTest`
- `android/gradlew.bat :feature-search:testDebugUnitTest`
- `android/gradlew.bat :core-storage:testDebugUnitTest :core-network:testDebugUnitTest`
- `android/gradlew.bat :app:testDebugUnitTest`
