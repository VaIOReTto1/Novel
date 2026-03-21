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
- `:macrobenchmark`
  - 基准与 profile 相关产物

## 当前依赖方向
- `:app -> :core-common`
- `:app -> :core-storage`
- `:app -> :core-network`
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
- `core-common` 已完成第一批共享基础抽离，`StateAdapter / RefactorFeatureFlags / LegacyApiServiceAdapter` 仍暂留 `app`。

## 当前阻塞与下一步
- 当前未解决的 `core-common` 遗留：
  - `android/app/src/main/java/com/novel/core/adapter/StateAdapter.kt`
  - `android/app/src/main/java/com/novel/core/config/RefactorFeatureFlags.kt`
  - `android/app/src/main/java/com/novel/core/network/LegacyApiServiceAdapter.kt`
- 下一步主线：
  - 建立 `core-bridge-contract` 最小模块
  - 继续深化 `core-network`

## 验证证据
- `android/gradlew.bat :core-common:testDebugUnitTest`
- `android/gradlew.bat :core-storage:testDebugUnitTest :core-network:testDebugUnitTest`
- `android/gradlew.bat :app:testDebugUnitTest`
