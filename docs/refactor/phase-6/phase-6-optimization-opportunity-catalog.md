# Phase 6 优化机会盘点

## 摘要
- 本文档不推翻 `Phase 6 = validated`。
- `2026-03-22` 之后，多批 closeout 后原子优化已经继续落地，本文以当前仓库事实重新分类：
  - 哪些优化已经完成
  - 哪些只完成了测量 / 取证
  - 哪些仍值得继续推进
- 它的目标是明确说明：
  - 达到基线不等于没有继续优化空间。
  - 当前 `Phase 6` 主要完成了“基线、证据、预算、blocker 固化”，并在 closeout 后继续落了一批低风险收益项。
  - 仍有一批真实性能优化点值得继续推进。
- 截至 `2026-03-24` 复核，本文中“仍可继续优化的点”并不是过期占位；它们仍然是未被当前代码完全消化的真实 backlog。

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
- closeout 后已经补上“首帧后再做非关键初始化”与“RN 预热延后到首帧后”两项真实优化。
- 但启动专项仍未走到“冷启动任务治理完全收敛”的程度。

### 已完成的优化
- 默认 benchmark 套件已从 compiled-mode 噪声中拆出，避免环境 blocker 污染日常基线。
- `StartupPerformanceMonitor` 已能追踪主要初始化项。
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt) 已通过 `StartupDeferredInitializationCoordinator` 将 `RetrofitClient`、`SettingsUtils` 等非关键初始化延后到首帧后执行。
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt) 已通过 `ReactNativePrewarmCoordinator` 将 RN 预热调整为首帧后再触发，不再在 Activity 创建阶段无条件抢占首屏预算。

### 仅完成测量 / 取证
- `ThemeManager`、`SoLoader`、`RetrofitClient`、`SettingsUtils` 的初始化耗时已采集。
- 启动 `timeToInitialDisplayMs` baseline 已采集。

### 仍可继续优化的点
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt) 当前只把非关键初始化粗粒度收敛为 network / settings 两组，尚未形成“首帧后任务清单 + 优先级 + 收益复核”的正式治理项。
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt) 当前仍依赖固定 `delay(100)` / `delay(200)` 节奏衔接首帧标记、RN 预热与 fully-loaded 标记，后续可继续细化 warm / cold path 策略并减少定时驱动。
- compiled-mode startup / baseline profile 仍受 `DN2101` 设备 compile blocker 影响，需要第二设备复验后才能判断更进一步的编译型收益。

### 证据与代码锚点
- `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt)
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项待办池

## 二、Reader
### 现状
- Reader init 基线已形成。
- closeout 后已完成初始化去重与设置触发分页刷新的收敛。
- 但 Reader 性能治理仍未深入到动作级与完整服务边界级。

### 已完成的优化
- `Phase 4` 已完成轻触式减重：
  - `ReaderSettingsCoordinator`
  - `ReaderHistoryCoordinator`
  - `ReaderMappingHelper`
- Reader init 已有正式 baseline。
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt) 已通过 `ReaderStartupCoordinator` 收敛初始化入口，避免同一 `bookId/chapterId` 走重复 init 路径。
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt) 已通过 `ReaderSettingsRefreshCoordinator` 把分页刷新收敛到真正需要重新分页的设置变化上，不再把所有设置变化都等价成一次刷新。
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt) 已通过 `ReaderRestoreHintCoordinator` 改为只在“恢复入口 + 初始化成功 + 首个页面数据就绪”时显示恢复提示，不再使用固定 `delay(1000)` 的前置等待。

### 仅完成测量 / 取证
- 当前只拿到了 Reader init 稳定样本。
- `ReaderViewModel` 已补上 `init / flip / settings_update` 动作级轻量性能 probe，开始为后续动作级样本提供统一计时日志入口。
- flip / settings update 仍然缺正式预算值与直接样本归档，当前还处于“probe 已接通、专项取证尚未闭环”阶段。

### 仍可继续优化的点
- 恢复提示当前仍保留固定可见时长，后续可继续评估是否需要更精细的隐藏时机。
- Reader 的分页、翻页、预取、设置、历史、评论虽然已有局部边界，但还没有形成原蓝图级别的完整独立压测矩阵。
- 目前仍缺 flip / settings 的动作级直接样本，意味着 Reader 治理还停留在“先收 init 与重分页噪声”，还没有进入热点动作优化阶段。

### 证据与代码锚点
- `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt)
- [InitReaderUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/InitReaderUseCase.kt)
- [FlipPageUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/FlipPageUseCase.kt)
- [UpdateSettingsUseCase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/usecase/UpdateSettingsUseCase.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项待办池

## 三、Welfare / WebView
### 现状
- Welfare / WebView 已形成正式基线。
- closeout 后已补上初始化去重与 `FCP / TTI` 接线。
- 但当前仍主要是“日志样本 + 生命周期留痕 + 首轮埋点接通”，不等于专项优化做完。

### 已完成的优化
- `WelfarePerformanceMonitor`、`WebViewPreloadManager` 等能力已经存在。
- [WelfarePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt) 已通过 `WelfarePageBootstrapCoordinator` 收敛首次 bootstrap，只在需要时初始化 preload manager、启动监控并派发 `InitializePage`。
- [WebViewComponent.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/component/WebViewComponent.kt) 已通过 `WelfareWebPerformanceCoordinator` 接通 `recordFirstContentfulPaint()` 与 `recordTimeToInteractive()`。
- [WelfarePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt) 与 [WebViewComponent.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/component/WebViewComponent.kt) 已共用同一份 `WelfareWebPerformanceCoordinator`，为同一次页面加载补上 `FCP / TTI / pageLoadComplete` 的 once-only 保护。
- WebView 首次加载样本、初始化耗时样本已经记录。

### 仅完成测量 / 取证
- 当前只拿到了 init / page load / WebView load 的日志样本。
- 还没有专门的 Welfare macrobenchmark。

### 仍可继续优化的点
- 当前 `WelfarePage` 的副作用、可见性与加载状态监听仍较多，后续仍需继续核查重复上报、重复渲染与状态传播噪声。
- 预加载 WebView 复用、cookie / cache 策略与页面切换收益目前仍缺定量复盘。
- Welfare / WebView 仍缺更深层 macrobenchmark，尤其是首开、复开、回退复用三类路径的专项对比。

### 证据与代码锚点
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- [WelfarePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/WelfarePage.kt)
- [WebViewComponent.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/welfare/component/WebViewComponent.kt)
- [WelfarePerformanceMonitor.kt](/d:/program/Novel/android/feature-welfare/src/main/java/com/novel/page/welfare/utils/WelfarePerformanceMonitor.kt)

### 建议优先级
- `high`

### 建议承接阶段
- 后续性能专项待办池

## 四、Search
### 现状
- Search 结果页已有正式日志样本基线。
- closeout 后已把分类筛选加载从“初始化即加载”调整为“按触发器延后加载”。
- 但搜索链路目前更像“已开始治理首开负担”，不是“已经做完热点优化”。

### 已完成的优化
- `Phase 4` 已完成 `SearchHistoryStore / SearchResultCacheStore / SearchRankingRepository / SearchQueryRepository` 等拆分。
- 当前能够通过 debug route 稳定采 SearchResult 链路。
- [SearchResultViewModel.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt) 已通过 `SearchCategoryFilterLoadCoordinator` 延后 `loadCategoryFilters()`，避免结果页初始化即同步承担分类筛选加载成本。
- [SearchResultViewModel.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt) 已补上 `INITIAL_ENTRY / CATEGORY_SWITCH / FILTER_APPLY / LOAD_MORE` 四类动作来源标记，并为搜索主链补上统一性能 trace。
- `load more` 已从统一自动重试路径中拆出，失败时直接回滚页码并返回失败状态，不再进行多轮自动重试。

### 仅完成测量 / 取证
- 现在只有搜索结果页 log sample。
- 还没有搜索结果页专项 benchmark 或专门性能回归套件。
- 四类动作的 probe 已接通，但尚未沉淀为正式 budget / evidence 文档。

### 仍可继续优化的点
- 同一文件中的重试策略当前是固定递增 `delay`，还没有做“结果页首开”与“用户主动重试”区分。
- 搜索结果页仍缺针对分页、分类筛选、首开渲染的专项 benchmark。
- 搜索分页、筛选切换、结果首屏渲染还没有形成 hotspot 级别的 trace / benchmark 对照，说明治理尚未进入第二阶段。

### 证据与代码锚点
- `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- [SearchResultViewModel.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt)
- [SearchResultPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/search/SearchResultPage.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项待办池

## 五、RN Host / Bridge
### 现状
- RN Host 与 Bridge 现在已经有宿主页 rerun 证据和兼容守门。
- closeout 后已补上“RN context 就绪后主题补发同步”。
- 但这更多证明“关键链路不再明显失真”，并不等于“性能已经优化到位”。

### 已完成的优化
- `profile / settings / aipage` 的宿主页首开样本已补齐。
- Bridge contract tests 和 RN settings smoke 持续守门。
- [ReactNativePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/rn/ReactNativePage.kt) 已通过 `ReactNativeThemeSyncCoordinator` 在 RN context 就绪后补发主题同步，不再把 `ThemeChanged` 永久丢弃在“context 未就绪”路径。
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt) 与 [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt) 已补上 RN context 与 `ReactRootView` 的冷热路径 trace，可区分 `ALREADY_READY / FIRST_CREATE / REUSED`。

### 仅完成测量 / 取证
- 当前主要是 route jump、RN context ready、host attach 的日志样本。
- 还没有把 ReactRootView 生命周期、Bridge 批量调用和线程切换做成独立优化结论。
- 冷热路径 trace 已接通，但还没有围绕这些路径建立正式 budget / diff 文档。

### 仍可继续优化的点
- `ReactRootView` 缓存生命周期虽然已有兼容证据，但还没有形成真正的性能优化规范。
- `首开` 与 `复开` 的策略还没有分开优化。
- Bridge 批量调用、线程切换与宿主页 attach 生命周期目前仍偏“兼容性守门”，还没有形成系统收益结论。

### 证据与代码锚点
- `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- [ReactNativePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/rn/ReactNativePage.kt)
- [ThemeManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ui/theme/ThemeManager.kt)
- [ComposeMainActivity.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivity.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项待办池

## 六、数据库与缓存
### 现状
- 原始蓝图把索引收益、FTS4 复盘、缓存清理对 IO/内存/电量影响列为 `Phase 6` 目标。
- 当前仓库里已经开始补数据库与缓存治理入口，但还没有形成最终收益结论。

### 已完成的优化
- [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt) 已落地，开始固定当前索引、FTS4 表/触发器与关键查询计划探针。
- [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt) 已落地，开始固定当前缓存体积、条目量与 cleanup 统计。
- 已新增正式文档：
  - `docs/refactor/phase-6/database-index-and-fts-governance-2026-03-22.md`
  - `docs/refactor/phase-6/cache-cleanup-governance-2026-03-22.md`

### 仅完成测量 / 取证
- 当前数据库部分已具备结构基线与 query-plan 探针入口，但尚未沉淀为真实收益复盘。
- 缓存部分当前已具备治理报告入口与 cleanup 统计扩展，但尚未沉淀为 IO / 内存 / 电量收益复盘。

### 仍可继续优化的点
- 校验数据库索引的真实收益。
- 复盘 `FTS4` 是否仍是当前最优方案。
- 观察缓存清理对 IO、内存和电量的影响，并形成专项证据。

### 证据与代码锚点
- `docs/refactor/phase-6/database-index-and-fts-governance-2026-03-22.md`
- `docs/refactor/phase-6/cache-cleanup-governance-2026-03-22.md`
- [DatabaseModule.kt](/d:/program/Novel/android/app/src/main/java/com/novel/di/DatabaseModule.kt)
- [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt)
- [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt)
- [NetworkCacheManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt)
- [CachedBookRepository.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/repository/CachedBookRepository.kt)

### 建议优先级
- `medium`

### 建议承接阶段
- 后续性能专项待办池

## 总结
- `Phase 6` 已完成的是：
  - baseline
  - evidence
  - budget
  - 已接受阻塞项固化
- `2026-03-22` 之后又继续完成了一批 closeout 后低风险优化：
  - request / trace id header 注入
  - Reader 初始化去重与设置刷新收敛
  - Welfare 初始化去重与 WebView `FCP / TTI` 接线
  - Search 分类筛选延后加载
  - RN 主题补发同步
  - 非关键启动初始化与 RN 预热延后到首帧后
- `Phase 6` 尚未完成的是：
  - 大量更深层的性能治理动作
- 因此正确口径应是：
  - `Phase 6 达标关闭`
  - `且 closeout 后仍持续推进优化`
  - `但仍然存在一批值得继续做的性能优化点`
- 这些剩余优化点不应被“当前已达标”掩盖，也不应被误算成“已经全部完成”。
