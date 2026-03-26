# Phase 6 优化补充记录（2026-03-27）

## 摘要
- 本文档记录 `2026-03-27` 这一轮围绕 `Phase 6` backlog 落地的三波优化。
- 它不改变 `Phase 6 = validated` 的阶段结论，只补充：
  - 本轮新增落地了哪些低风险收益
  - 哪些条目已经从“可继续优化”转为“已实现的首轮收敛”
  - 哪些深层治理仍保留在后续专项待办池

## Wave 1：启动 + Reader + Welfare/WebView
- 启动
  - `ComposeMainActivityFirstFrameCoordinator` 从固定 `delay(100/200)` 过渡到显式 first-frame plan。
  - `ReactNativePrewarmCoordinator` 与 `StartupDeferredInitializationCoordinator` 继续保留，但触发策略改为 gate-driven。
- Reader
  - `ReaderRestoreHintCoordinator` 改为只自动关闭“恢复入口触发的提示”。
  - `ReaderPerformanceTraceCoordinator` 新增 `init / flip / settings_update` 动作级预算与状态输出。
- Welfare / WebView
  - `WelfarePageContent` 中分散的副作用继续收口到 `WelfarePageBootstrapCoordinator` 和 `WelfareWebPerformanceCoordinator`。
  - `FCP / TTI / pageLoadComplete` 的 once-only 判定继续集中。

### Wave 1 代码锚点
- [ComposeMainActivityFirstFrameCoordinator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivityFirstFrameCoordinator.kt)
- [ReaderRestoreHintCoordinator.kt](/d:/program/Novel/android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderRestoreHintCoordinator.kt)
- [ReaderPerformanceTraceCoordinator.kt](/d:/program/Novel/android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderPerformanceTraceCoordinator.kt)
- [WelfarePageContent.kt](/d:/program/Novel/android/feature-welfare/src/main/java/com/novel/page/welfare/WelfarePageContent.kt)

### Wave 1 验证
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :feature-reader:testDebugUnitTest --tests com.novel.page.read.viewmodel.ReaderRestoreHintCoordinatorTest --tests com.novel.page.read.viewmodel.ReaderPerformanceTraceCoordinatorTest`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :feature-welfare:testDebugUnitTest --tests com.novel.page.welfare.viewmodel.WelfarePageBootstrapCoordinatorTest --tests com.novel.page.welfare.component.WelfareWebPerformanceCoordinatorTest`
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :app:testDebugUnitTest --tests com.novel.ComposeMainActivityFirstFrameCoordinatorTest --tests com.novel.ReactNativePrewarmCoordinatorTest --tests com.novel.StartupDeferredInitializationCoordinatorTest`

## Wave 2：Search + RN Host/Bridge
- Search
  - `SearchRetryPolicyCoordinator` 现在按触发源区分 `INITIAL_ENTRY / FILTER_APPLY / USER_RETRY / LOAD_MORE`。
  - 非 `LOAD_MORE` 重试会转换为 `USER_RETRY`，同时保留失败 trace 的原始 trigger 语义。
  - `SearchPerformanceTraceCoordinator` 输出的 metadata 顺序被固定，便于日志样本对比。
- RN Host / Bridge
  - `ReactNativeHostPathTraceCoordinator` 现在区分 `COLD_OPEN / OPEN / REUSED`。
  - `ReactNativeThemeSyncCoordinator` 由布尔返回改为显式 action model。
  - `ReactNativePage.kt` 补上主题同步调用对齐与宿主页返回路径的编译修复。

### Wave 2 代码锚点
- [SearchRetryPolicyCoordinator.kt](/d:/program/Novel/android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchRetryPolicyCoordinator.kt)
- [SearchResultViewModel.kt](/d:/program/Novel/android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt)
- [ReactNativeHostPathTraceCoordinator.kt](/d:/program/Novel/android/feature-rn-host/src/main/java/com/novel/rn/ReactNativeHostPathTraceCoordinator.kt)
- [ReactNativeThemeSyncCoordinator.kt](/d:/program/Novel/android/feature-rn-host/src/main/java/com/novel/rn/ReactNativeThemeSyncCoordinator.kt)
- [ReactNativePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/rn/ReactNativePage.kt)

### Wave 2 验证
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :feature-search:testDebugUnitTest --tests com.novel.page.search.viewmodel.SearchRetryPolicyCoordinatorTest --tests com.novel.page.search.viewmodel.SearchPerformanceTraceCoordinatorTest`
- `ReactNativeThemeSyncCoordinatorTest` 与 `ReactNativeHostPathTraceCoordinatorTest` 的逻辑测试已落地；`feature-rn-host` Gradle 单测在本机仍存在模块级生成源码/构建产物噪音，需要后续继续清理验证环境。

## Wave 3：数据库与缓存治理
- 数据库
  - `DatabaseGovernanceReportGenerator` 从纯静态盘点升级为：
    - summary
    - query-plan table scan 风险提示
    - FTS 覆盖缺失提示
- 缓存
  - `CacheGovernanceReportGenerator` 从纯统计升级为：
    - cleanup reduction ratio
    - average bytes cleaned per run
    - 高频 cleanup / 大幅条目回落 / 无空间释放提示

### Wave 3 代码锚点
- [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt)
- [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt)

### Wave 3 验证
- `android/gradlew.bat --no-daemon "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false" :app:testDebugUnitTest --tests com.novel.utils.dao.DatabaseGovernanceReportGeneratorTest --tests com.novel.utils.network.cache.CacheGovernanceReportGeneratorTest`

## 残余风险
- `feature-rn-host` 的 Gradle 单测在本机仍可受到生成源码或构建目录噪音影响；当前逻辑级测试和 app 侧相关测试已通过，但模块级 clean verification 仍建议在后续补证。
- 本轮没有重跑完整 benchmark 套件，仍属于“选择性补基线”。
- 数据库与缓存治理已经拥有更强的治理输出，但索引收益、FTS4 最优性、IO / 内存 / 电量收益复盘仍未完成。
