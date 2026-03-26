# Phase 6 优化机会盘点

## 摘要
- 本文档不推翻 `Phase 6 = validated`。
- 它用于回答三件事：
  - 哪些优化已经落地
  - 哪些只完成了测量 / 取证
  - 哪些仍值得继续推进
- 截至 `2026-03-27`，本轮三波优化已经继续消化了原 backlog 的一部分，补充记录见：
  - [phase-6-optimization-addendum-2026-03-27.md](./phase-6-optimization-addendum-2026-03-27.md)

## 使用规则
- 这里列的是“继续优化的机会”，不是“已确认回归”。
- 每个条目必须区分：
  - 已完成的优化
  - 仅完成测量 / 取证
  - 仍可继续优化的点
- 若后续继续执行优化，必须重新补证据，不能直接复用此处推断。

## 本轮分波结果
| Wave | 范围 | 当前状态 |
| --- | --- | --- |
| Wave 1 | 启动 + Reader + Welfare/WebView | `已完成首轮收敛` |
| Wave 2 | Search + RN Host/Bridge | `已完成首轮收敛` |
| Wave 3 | 数据库与缓存治理 | `已完成治理增强` |

## 一、启动
### 已完成的优化
- 默认 benchmark 套件已从 compiled-mode 噪声中拆出，避免环境 blocker 污染日常基线。
- `StartupPerformanceMonitor` 已能追踪主要初始化项。
- `MainApplication` 已通过 `StartupDeferredInitializationCoordinator` 将非关键初始化延后到首帧后执行。
- `ComposeMainActivityFirstFrameCoordinator` 已从固定 `delay(100/200)` 收敛为显式 first-frame plan，不再依赖定时器串联首帧与 fully-loaded 标记。

### 仅完成测量 / 取证
- `ThemeManager`、`SoLoader`、`RetrofitClient`、`SettingsUtils` 的初始化耗时已采集。
- 启动 `timeToInitialDisplayMs` baseline 已采集。

### 仍可继续优化的点
- 首帧后任务清单目前仍是粗粒度的 network / settings 两组，尚未形成“任务清单 + 优先级 + 收益复核”的正式治理表。
- compiled-mode startup / baseline profile 仍受 `DN2101` 设备 compile blocker 影响，后续仍需第二设备复验。

### 代码锚点
- [MainApplication.kt](/d:/program/Novel/android/app/src/main/java/com/novel/MainApplication.kt)
- [ComposeMainActivityFirstFrameCoordinator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/ComposeMainActivityFirstFrameCoordinator.kt)

### 建议优先级
- `medium`

## 二、Reader
### 已完成的优化
- `ReaderStartupCoordinator` 已继续收敛初始化入口。
- `ReaderSettingsRefreshCoordinator` 已把分页刷新收敛到真正需要重分页的设置变化。
- `ReaderRestoreHintCoordinator` 现在只自动关闭“恢复入口触发的提示”，不再无差别依赖固定显示时长。
- `ReaderPerformanceTraceCoordinator` 已补上 `init / flip / settings_update` 动作级预算与状态输出。

### 仅完成测量 / 取证
- Reader init 稳定样本已存在。
- `flip / settings_update` 的动作级 probe 已接通，但仍未形成正式 budget 文档。

### 仍可继续优化的点
- 恢复提示虽然已从固定等待改为 restore-aware dismissal，但进一步的“首次有效交互关闭”仍可继续细化。
- Reader 仍缺完整的动作级压测矩阵，尤其是翻页与设置链路的直接样本归档。

### 代码锚点
- [ReaderPage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/read/ReaderPage.kt)
- [ReaderRestoreHintCoordinator.kt](/d:/program/Novel/android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderRestoreHintCoordinator.kt)
- [ReaderPerformanceTraceCoordinator.kt](/d:/program/Novel/android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderPerformanceTraceCoordinator.kt)

### 建议优先级
- `medium`

## 三、Welfare / WebView
### 已完成的优化
- `WelfarePageBootstrapCoordinator` 继续收敛首次 bootstrap。
- `WelfareWebPerformanceCoordinator` 继续集中 `FCP / TTI / pageLoadComplete` 的 once-only 判定。
- `WelfarePageContent` 中部分散的副作用、可见性与页面加载判定已进一步向 coordinator 层收口。

### 仅完成测量 / 取证
- 当前拥有 init / page load / WebView load 的日志样本。
- 仍没有独立 Welfare macrobenchmark。

### 仍可继续优化的点
- WebView 预加载复用、cookie / cache 策略与页面切换收益仍缺定量复盘。
- Welfare / WebView 仍缺首开、复开、回退复用三条路径的专项 benchmark。

### 代码锚点
- [WelfarePageContent.kt](/d:/program/Novel/android/feature-welfare/src/main/java/com/novel/page/welfare/WelfarePageContent.kt)
- [WelfareWebPerformanceCoordinator.kt](/d:/program/Novel/android/feature-welfare/src/main/java/com/novel/page/welfare/component/WelfareWebPerformanceCoordinator.kt)

### 建议优先级
- `medium`

## 四、Search
### 已完成的优化
- `SearchCategoryFilterLoadCoordinator` 已把分类筛选加载延后到真正需要时。
- `SearchRetryPolicyCoordinator` 现在按 `INITIAL_ENTRY / FILTER_APPLY / USER_RETRY / LOAD_MORE` 区分重试策略。
- 非 `LOAD_MORE` 重试会转换到 `USER_RETRY`，`LOAD_MORE` 继续禁止自动重试。
- `SearchPerformanceTraceCoordinator` 已固定 metadata 输出顺序，便于日志对比。

### 仅完成测量 / 取证
- 当前有搜索结果页 log sample。
- 尚未形成分页 / 分类筛选 / 首屏渲染的专项 benchmark。

### 仍可继续优化的点
- 搜索结果页仍缺热点动作 benchmark。
- 首开渲染、分页与筛选切换仍未形成正式 budget / diff 文档。

### 代码锚点
- [SearchResultViewModel.kt](/d:/program/Novel/android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt)
- [SearchRetryPolicyCoordinator.kt](/d:/program/Novel/android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchRetryPolicyCoordinator.kt)

### 建议优先级
- `medium`

## 五、RN Host / Bridge
### 已完成的优化
- `ReactNativeThemeSyncCoordinator` 已从布尔返回升级为显式 action model。
- `ReactNativeHostPathTraceCoordinator` 现在能区分 `COLD_OPEN / OPEN / REUSED`。
- `ReactNativePage.kt` 已补上主题同步和返回路径的接线修复。

### 仅完成测量 / 取证
- 当前主要仍是 route jump、RN context ready、host attach 的日志样本。
- `feature-rn-host` 的 Gradle 单测在本机仍存在模块级构建噪音。

### 仍可继续优化的点
- `ReactRootView` 缓存生命周期仍没有沉淀为正式性能治理规范。
- 首开与复开的收益对比仍需专项样本补充。
- Bridge 批量调用与线程切换仍偏“兼容性守门”，尚未形成系统收益结论。

### 代码锚点
- [ReactNativeThemeSyncCoordinator.kt](/d:/program/Novel/android/feature-rn-host/src/main/java/com/novel/rn/ReactNativeThemeSyncCoordinator.kt)
- [ReactNativeHostPathTraceCoordinator.kt](/d:/program/Novel/android/feature-rn-host/src/main/java/com/novel/rn/ReactNativeHostPathTraceCoordinator.kt)
- [ReactNativePage.kt](/d:/program/Novel/android/app/src/main/java/com/novel/rn/ReactNativePage.kt)

### 建议优先级
- `medium`

## 六、数据库与缓存
### 已完成的优化
- `DatabaseGovernanceReportGenerator` 已从静态盘点增强为：
  - summary
  - table scan 风险提示
  - FTS 覆盖缺失提示
- `CacheGovernanceReportGenerator` 已从纯统计增强为：
  - `cleanup_reduction_ratio`
  - `average_bytes_cleaned_per_run`
  - 高频 cleanup / 大幅条目回落 / 无空间释放提示

### 仅完成测量 / 取证
- 数据库治理当前已具备结构基线与 recommendation 输出。
- 缓存治理当前已具备 cleanup 摘要与 recommendation 输出。

### 仍可继续优化的点
- 索引收益、`FTS4` 最优性、cleanup 对 IO / 内存 / 电量的真实收益复盘仍未完成。
- 当前治理增强更适合做“风险识别入口”，还不是最终收益证明。

### 代码锚点
- [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt)
- [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt)

### 建议优先级
- `medium`

## 总结
- `Phase 6` 已完成的是：
  - baseline
  - evidence
  - budget
  - blocker 固化
- `2026-03-27` 这一轮又继续完成了三波真实优化：
  - 启动 / Reader / Welfare-WebView 的 coordinator 收敛
  - Search / RN Host 的 trigger-aware policy 与路径分流
  - 数据库 / 缓存治理报告增强
- `Phase 6` 尚未完成的是：
  - 更深层的 benchmark、收益复盘与治理闭环
- 正确口径仍然是：
  - `Phase 6 达标关闭`
  - `closeout 后持续推进优化`
  - `但仍存在值得继续做的性能优化点`
