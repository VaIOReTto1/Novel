# Phase 6 优化机会盘点

## 摘要
- 本文档不推翻 `Phase 6 = validated`。
- 它回答三件事：
  - 哪些优化已被本轮实现关闭
  - 哪些已经补到证据，但仍需下一轮深化
  - 哪些仍是下一批 backlog
- 当前权威补充入口：
  - [phase-6-optimization-addendum-2026-03-27.md](./phase-6-optimization-addendum-2026-03-27.md)
  - [device-evidence-addendum-2026-03-27.md](./device-evidence-addendum-2026-03-27.md)
  - [device-evidence-addendum-2026-03-28.md](./device-evidence-addendum-2026-03-28.md)

## 本轮结果总览
| 方向 | 已被本轮实现关闭 | 已补证据但仍需二次深化 | 仍未进入实施的下一批 backlog |
| --- | --- | --- | --- |
| Startup | 首帧后 gate-driven prewarm、固定 delay 移除、正式任务清单落地、主页面非关键 overlay 延后到首帧后 | 首帧时间仍偏高 | compiled-mode / 第二设备复验 |
| Reader | `init / settings_update / flip` 动作预算与真机样本补齐、恢复提示策略收口、动作级归档模板落地、多次采样矩阵落地 | benchmark 化仍可深化 | 完整动作级压测矩阵 |
| Welfare / WebView | bootstrap / once-only 协调继续收口，首开/复开样本补齐、路径矩阵落地 | FCP / TTI 仍有波动 | cache / cookie / 更深 benchmark |
| Search | trigger-aware retry、`INITIAL_ENTRY / CATEGORY_SWITCH / FILTER_APPLY / LOAD_MORE` 真机样本补齐、热点动作矩阵落地、多次采样矩阵落地 | benchmark 与正式 budget diff 仍待深化 | 热点 benchmark 与正式 budget diff |
| RN Host / Bridge | `COLD_OPEN / OPEN / REUSED` 语义与主题同步样本补齐、返回缓存策略显式化 | root view 生命周期治理仍可继续沉淀 | 更系统的 host path 规范与批量调用收益复盘 |
| 数据库 / 缓存 | 治理报告增强完成、样例输出与 recommendation 解读落地 | 收益证明仍不足 | 索引、`FTS4`、cleanup 对 IO / 内存 / 电量的定量复盘 |

## 一、已被本轮实现关闭
### Startup
- `ComposeMainActivityFirstFrameCoordinator` 已从固定 `delay(100/200)` 收敛到显式 first-frame plan。
- `ReactNativePrewarmCoordinator` 与 `StartupDeferredInitializationCoordinator` 的触发已收口为首帧后 gate-driven 路径。
- `StartupDeferredInitializationCoordinator` 已从粗粒度布尔分支升级为正式任务清单，包含：
  - task id
  - priority
  - trigger
  - expected benefit
- `MainPage` 的短剧 toast 与启动弹窗判定已延后到首帧后再揭示。
- `2026-03-27` 真机样本已证明：
  - `prewarm_after_first_frame`
  - `create_react_context_in_background`
  两条日志会在首帧后落地。

### Reader
- `ReaderPerformanceTraceCoordinator` 的 `init / settings_update / flip` 预算与 trace 格式已经落地。
- `ReaderRestoreHintCoordinator` 已从固定时长退出，转为 restore-aware dismissal。
- `2026-03-27` 已补到当天真机 `init` 与 `settings_update` 样本。
- Reader 动作级归档模板已落到独立文档。
- `flip` 的自动取证 scenario 已落地，后续无需继续依赖人工 swipe。
- `2026-03-28` 已补到当天真机 `flip` 样本。
- Reader 多次采样矩阵已落到独立文档。

### Welfare / WebView
- `WelfarePageBootstrapCoordinator` 与 `WelfareWebPerformanceCoordinator` 已继续收口分散副作用。
- `2026-03-27` 已补到：
  - 首开样本
  - 返回首页后复开样本
  - `savedState restore` 样本
- Welfare / WebView 路径矩阵已落到独立文档。

### Search
- `SearchRetryPolicyCoordinator` 已按 `INITIAL_ENTRY / FILTER_APPLY / USER_RETRY / LOAD_MORE` 固定触发源语义。
- `2026-03-27` 已补到：
  - `INITIAL_ENTRY`
  - `CATEGORY_SWITCH`
  - `FILTER_APPLY`
  的设备样本。
- Search 热点动作矩阵已落到独立文档。
- `LOAD_MORE` 的 debug-only `pageSize override` 场景已落地，后续无需再靠碰运气找多页 query。
- `2026-03-28` 已补到当天真机 `LOAD_MORE` 样本。
- Search 多次采样矩阵已落到独立文档。

### RN Host / Bridge
- `ReactNativeHostPathTraceCoordinator` 的 `COLD_OPEN / OPEN / REUSED` 已从日志枚举推进到当天设备证据。
- `ReactNativeThemeSyncCoordinator` 与 `ReactNativePage.kt` 的主题同步规则已有运行时样本支撑。
- 宿主页返回时的 root view cache 语义已由显式 policy 守门，不再只靠 `destroyOnBack` 的隐含理解。

### 数据库 / 缓存
- `DatabaseGovernanceReportGenerator` 已具备 summary / scan risk / FTS coverage warning。
- `CacheGovernanceReportGenerator` 已具备 cleanup ratio / bytes cleaned / warning 输出。

## 二、已补证据但仍需二次深化
### Startup
- `2026-03-27` 样本中：
  - `首帧绘制 = 1019ms`
  - `完全加载 = 1086ms`
- 当前已经有样本且任务清单已 formalize，但优化建议仍提示“首帧渲染时间较长”，说明这条线还没到彻底关账。
- 当前还新增了 `MainPage` deferred overlays 优化，但真机前后对比样本被无线 adb 安装失败阻塞。

### Reader
- 当前已经有 `init / settings_update` 的当天真机样本。
- `flip` 也已补到当天真机样本。
- 当前更适合继续深化的是“多次采样 / 压测矩阵”，而不是补第一条动作样本。

### Welfare / WebView
- 首开与复开样本都已补齐。
- 但 FCP / TTI 仍存在波动，说明当前更多是“路径可追溯”，还不是“收益已稳定收敛”。

### Search
- 首开、分类切换、筛选应用都已有样本。
- `LOAD_MORE` 也已补到当天真机样本。
- 当前更适合继续深化的是 benchmark / budget diff，而不是首条分页样本。

### RN Host / Bridge
- 当前已经能在当天样本里看到：
  - `COLD_OPEN`
  - `OPEN`
  - `REUSED`
  - `theme synced to RN: light`
- 返回时的缓存保留 / 清理语义已经显式化，但 `ReactRootView` 完整生命周期治理仍未沉淀成更正式的长期规范。

### 数据库 / 缓存
- 当前 recommendation / warning 已经足以支持治理讨论。
- 样例输出与 recommendation 解读也已落地。
- 但它们仍是“风险识别入口”，不是“收益已经被定量证明”。

## 三、仍未进入实施的下一批 backlog
### Startup
- compiled-mode startup / baseline profile 的第二设备复验
- 首帧后任务收益的长期复核与更细粒度扩展
- 当前 `DN2101` 上 compiled-mode benchmark 安装路径仍被无线 adb `device offline` 阻塞

### Reader
- 动作级 benchmark / 压测矩阵

### Welfare / WebView
- 更深层的 WebView cache / cookie / 预加载收益量化
- 更系统的专项 benchmark 化

### Search
- 搜索结果页热点 benchmark
- 正式 budget / diff 文档

### RN Host / Bridge
- root view cache 生命周期的正式治理规范
- Bridge 批量调用与线程切换的系统收益结论

### 数据库 / 缓存
- 索引收益与 `FTS4` 最优性的定量复盘
- cleanup 对 IO / 内存 / 电量的量化收益

## 证据更新
- Startup
  - `docs/refactor/evidence/phase6-startup-logcat-2026-03-27.txt`
  - `docs/refactor/phase-6/startup-deferred-task-catalog-2026-03-27.md`
  - `docs/refactor/evidence/perf-multisample-2026-03-28.txt`
  - `docs/refactor/phase-6/perf-multisample-matrix-2026-03-28.md`
  - `docs/refactor/evidence/startup-compilation-probe-blocker-2026-03-28.txt`
  - `docs/refactor/phase-6/startup-compilation-probe-blocker-2026-03-28.md`
  - `docs/refactor/phase-6/main-page-deferred-overlays-2026-03-28.md`
- Search
  - `docs/refactor/evidence/search-hot-actions-logcat-2026-03-27.txt`
  - `docs/refactor/evidence/search-load-more-probe-2026-03-27.txt`
  - `docs/refactor/evidence/search-page-size-sweep-2026-03-27.txt`
  - `docs/refactor/evidence/search-load-more-logcat-2026-03-28.txt`
  - `docs/refactor/phase-6/search-hot-action-matrix-2026-03-27.md`
  - `docs/refactor/phase-6/search-load-more-debug-scenario-2026-03-27.md`
- Welfare / WebView
  - `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-27.txt`
  - `docs/refactor/evidence/welfare-webview-path-matrix-logcat-2026-03-28.txt`
  - `docs/refactor/phase-6/welfare-webview-path-matrix-2026-03-28.md`
- RN Host
  - `docs/refactor/evidence/rn-host-path-logcat-2026-03-27.txt`
  - `docs/refactor/phase-6/rn-host-root-view-cache-policy-2026-03-27.md`
- Reader
  - `docs/refactor/evidence/reader-performance-logcat-2026-03-27.txt`
  - `docs/refactor/evidence/reader-flip-logcat-2026-03-28.txt`
  - `docs/refactor/phase-6/reader-action-evidence-template-2026-03-27.md`
  - `docs/refactor/phase-6/reader-flip-debug-scenario-2026-03-27.md`
  - `docs/refactor/phase-6/perf-multisample-matrix-2026-03-28.md`
- 数据库 / 缓存
  - `docs/refactor/phase-6/database-governance-sample-output-2026-03-28.md`
  - `docs/refactor/phase-6/cache-governance-sample-output-2026-03-28.md`

## 总结
- 本轮已实现关闭的条目，不应继续留在“仍可继续优化的点”里。
- 当前真正还在 backlog 里的，主要是：
  - Startup 首帧继续收敛
  - Search / Reader 更系统的 benchmark 与 budget diff
  - Welfare / WebView 更深 benchmark
  - 数据库 / 缓存收益复盘
- 这些项都应继续留在 `Phase 6` 的优化池，而不是误转成 `Phase 7` 的主线内容。
