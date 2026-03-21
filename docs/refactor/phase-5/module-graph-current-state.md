# Phase 5 模块图现状（2026-03-21）

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
- `:feature-search`
  - 首轮 search feature 模块
  - 当前承载：
    - `SearchPreferenceStorage`
- `:feature-home`
  - 首轮 home feature 模块
  - 当前承载：
    - `HomePerformanceOptimizer`
- `:feature-rn-host`
  - 首轮 rn-host feature 模块
  - 当前承载：
    - `SettingsPreferenceStorage`

## 当前依赖方向
- `:app -> :core-common`
- `:app -> :core-bridge-contract`
- `:app -> :core-storage`
- `:app -> :core-network`
- `:app -> :feature-home`
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
2. `build conventions`
3. `core-common`
4. `deepen core-network`
5. `core-bridge-contract`
6. `feature-welfare`
7. `feature-search`
8. `feature-home`
9. `feature-rn-host`
10. `validation / closeout`

## 当前切口结果
- `BookService` 的空 `chapterUpdateTime` 回归已作为固定门禁保留。
- `core-storage` 已稳定。
- `core-network` 当前仍是“契约优先”首批切口，后续需要继续向共享基础设施深化。
- `core-bridge-contract` 已完成第一批纯 Kotlin bridge delegate/helper 抽离。
- `feature-welfare` 已完成两轮低风险切口，当前仍保留 `WelfarePage` 作为 app 宿主 wrapper。
- `feature-search` 已完成首轮最小切口，当前只迁出 `SearchPreferenceStorage`。
- `feature-home` 已完成首轮最小切口，当前只迁出 `HomePerformanceOptimizer`。
- `feature-rn-host` 已完成首轮最小切口，当前只迁出 `SettingsPreferenceStorage`。
- `core-common` 已完成第一批共享基础抽离，`StateAdapter / RefactorFeatureFlags / LegacyApiServiceAdapter` 仍暂留 `app`。

## 当前阻塞与下一步
- 当前未解决的 `core-common` 遗留：
  - `android/app/src/main/java/com/novel/core/adapter/StateAdapter.kt`
  - `android/app/src/main/java/com/novel/core/config/RefactorFeatureFlags.kt`
  - `android/app/src/main/java/com/novel/core/network/LegacyApiServiceAdapter.kt`
- 当前 `core-network` 深化策略暂缓：
  - 直接搬迁共享网络原语时曾触发默认 `app` 编译链不稳定，已回退到上一个稳定边界，后续需要换更保守的切口。
- 下一步主线：
  - 继续扩大 `feature-welfare`，直到能搬迁更多 welfare internals
  - 继续扩大 `feature-search / feature-home / feature-rn-host`，从纯 helper/store 进入下一批低耦合切口
  - 在单独原子主题里继续深化 `core-network`

## 验证证据
- `android/gradlew.bat :core-common:testDebugUnitTest`
- `android/gradlew.bat :core-bridge-contract:testDebugUnitTest`
- `android/gradlew.bat :feature-welfare:compileDebugKotlin`
- `android/gradlew.bat :feature-home:testDebugUnitTest`
- `android/gradlew.bat :feature-rn-host:testDebugUnitTest`
- `android/gradlew.bat :feature-search:testDebugUnitTest`
- `android/gradlew.bat :core-storage:testDebugUnitTest :core-network:testDebugUnitTest`
- `android/gradlew.bat :app:testDebugUnitTest`
