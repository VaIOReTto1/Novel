# Phase 6 优化机会盘点

## 摘要
- 本文档不推翻 `Phase 6 = validated`。
- 它的目标是明确说明：
  - 达到基线不等于没有继续优化空间。
  - 当前 `Phase 6` 主要完成了“基线、证据、预算、blocker 固化”。
  - 仍有一批真实性能优化点值得继续推进。

## 使用规则
- 这里列的是“继续优化的机会”，不是“已确认回归”。
- 每个条目都必须区分：
  - 已完成的优化
  - 仅完成测量 / 取证
  - 仍可继续优化的点
- 若后续真的执行优化，必须重新补证据，而不能直接复用这里的推断。

## 一、启动
### 现状
- 启动基线已建立，当前绿色套件稳定。
- 但启动专项更多停留在“把测量路径跑通”，而不是“把冷启动任务真正压缩到极限”。

### 已完成的优化
- 默认 benchmark 套件已从 compiled-mode 噪声中拆出，避免环境 blocker 污染日常基线。
- `StartupPerformanceMonitor` 已能追踪主要初始化项。

### 仅完成测量 / 取证
- `ThemeManager`、`SoLoader`、`RetrofitClient`、`SettingsUtils` 的初始化耗时已采集。
- 启动 `timeToInitialDisplayMs` baseline 已采集。

### 仍可继续优化的点
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt) 中无条件调用 `createReactContextInBackground()`，仍可继续评估：
  - 预热收益
  - 对纯 Native 冷启动的主线程与总启动成本影响
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt) 中仍有分段 `delay(...)` 和初始化串行时序，可继续压缩首帧前负担。
- 当前还没有把“首帧前非必要初始化清单”做成正式治理项。

### 证据与代码锚点
- `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt)
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项 backlog

## 二、Reader
### 现状
- Reader init 基线已形成。
- 但 Reader 性能治理还没深入到动作级与服务边界级。

### 已完成的优化
- `Phase 4` 已完成轻触式减重：
  - `ReaderSettingsCoordinator`
  - `ReaderHistoryCoordinator`
  - `ReaderMappingHelper`
- Reader init 已有正式 baseline。

### 仅完成测量 / 取证
- 当前只拿到了 Reader init 稳定样本。
- flip / settings update 仍然只有“缺口说明”，没有动作级直接数值样本。

### 仍可继续优化的点
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt) 中对同一 `bookId/chapterId` 存在两段 `LaunchedEffect` 初始化路径，属于明显可疑重复初始化点。
- 同文件里 `UpdateContainerSize` 在设置变更和初始化路径里都有触发，值得核查是否带来额外分页成本。
- `ShowProgressRestoredHint` 的固定 `delay(1000)` / `delay(3000)` 仍是粗粒度时序策略，可继续评估是否造成无意义等待或重组负担。
- Reader 的分页、翻页、预取、设置、历史、评论虽然已有局部边界，但还没有形成原蓝图级别的完整独立压测矩阵。

### 证据与代码锚点
- `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt)
- [InitReaderUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/InitReaderUseCase.kt)
- [FlipPageUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/FlipPageUseCase.kt)
- [UpdateSettingsUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/UpdateSettingsUseCase.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项 backlog

## 三、Welfare / WebView
### 现状
- Welfare / WebView 已形成正式基线。
- 但当前主要是“日志样本 + 生命周期留痕”，不等于专项优化做完。

### 已完成的优化
- `WelfarePerformanceMonitor`、`WebViewPreloadManager` 等能力已经存在。
- WebView 首次加载样本、初始化耗时样本已经记录。

### 仅完成测量 / 取证
- 当前只拿到了 init / page load / WebView load 的日志样本。
- 还没有专门的 Welfare macrobenchmark。

### 仍可继续优化的点
- [WelfarePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt) 有两段 `LaunchedEffect(Unit)`，并存在重复 `InitializePage` / `startPageLoad` 路径，存在重复初始化嫌疑。
- `WelfarePerformanceMonitor` 中 `recordFirstContentfulPaint()`、`recordTimeToInteractive()` 已有代码，但没有真正接入当前链路，是明显的下一步优化入口。
- 当前 `WelfarePage` 的副作用分发较多，仍可继续收敛不必要的重复渲染与状态传播。

### 证据与代码锚点
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- [WelfarePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt)
- [WebViewComponent.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/component/WebViewComponent.kt)
- [WelfarePerformanceMonitor.kt](/d:/program/Novel/android/feature-welfare/src/main/java/com/novel/page/welfare/utils/WelfarePerformanceMonitor.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项 backlog

## 四、Search
### 现状
- Search 结果页已有正式 log-sample baseline。
- 但搜索链路目前更像“可测量了”，不是“已经做完热点优化”。

### 已完成的优化
- `Phase 4` 已完成 `SearchHistoryStore / SearchResultCacheStore / SearchRankingRepository / SearchQueryRepository` 等拆分。
- 当前能够通过 debug route 稳定采 SearchResult 链路。

### 仅完成测量 / 取证
- 现在只有搜索结果页 log sample。
- 还没有搜索结果页专项 benchmark 或专门性能回归套件。

### 仍可继续优化的点
- [SearchResultViewModel.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt) 初始化时立即 `loadCategoryFilters()`，同时还要走搜索链路和重试链路，可继续评估首开结果页里的非必要加载。
- 同一文件中的重试策略当前是固定递增 `delay`，还没有做“结果页首开”与“用户主动重试”区分。
- 搜索结果页仍缺针对分页、分类筛选、首开渲染的专项 benchmark。

### 证据与代码锚点
- `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- [SearchResultViewModel.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt)
- [SearchResultPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/SearchResultPage.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项 backlog

## 五、RN Host / Bridge
### 现状
- RN Host 与 Bridge 现在已经有宿主页 rerun 证据和兼容守门。
- 但这更多证明“没回归”，并不等于“性能已经优化到位”。

### 已完成的优化
- `profile / settings / aipage` 的宿主页首开样本已补齐。
- Bridge contract tests 和 RN settings smoke 持续守门。

### 仅完成测量 / 取证
- 当前主要是 route jump、RN context ready、host attach 的日志样本。
- 还没有把 ReactRootView 生命周期、Bridge 批量调用和线程切换做成独立优化结论。

### 仍可继续优化的点
- `ThemeChanged` 当前仍存在“RN context 未就绪时跳过发送”的路径，可继续优化事件重发与就绪时机。
- `ReactRootView` 缓存生命周期虽然已有兼容证据，但还没有形成真正的性能优化规范。
- `首开` 与 `复开` 的策略还没有分开优化。

### 证据与代码锚点
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- [ReactNativePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/rn/ReactNativePage.kt)
- [ThemeManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ui/theme/ThemeManager.kt)
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项 backlog

## 六、数据库与缓存
### 现状
- 原始蓝图把索引收益、FTS4 复盘、缓存清理对 IO/内存/电量影响列为 `Phase 6` 目标。
- 当前仓库里缺少这三项已经形成正式结论的 Phase 6 文档。

### 已完成的优化
- 暂无可以作为 `Phase 6` 正式收尾结论的明确专项文档。

### 仅完成测量 / 取证
- 目前更多停留在已有缓存体系、数据库配置与历史阶段证据层面。

### 仍可继续优化的点
- 校验数据库索引的真实收益。
- 复盘 `FTS4` 是否仍是当前最优方案。
- 观察缓存清理对 IO、内存和电量的影响，并形成专项证据。

### 证据与代码锚点
- [DatabaseModule.kt](/d:/program/Novel/android/app/src/main/java/com/novel/di/DatabaseModule.kt)
- [NetworkCacheManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt)
- [CachedBookRepository.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/repository/CachedBookRepository.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项 backlog

## 总结
- `Phase 6` 已完成的是：
  - baseline
  - evidence
  - budget
  - accepted blocker 固化
- `Phase 6` 尚未完成的是：
  - 大量真实性能优化动作
- 因此正确口径应是：
  - `Phase 6 达标关闭`
  - `但仍然存在一批值得继续做的性能优化点`
