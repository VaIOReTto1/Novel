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

## 本轮结果总览
| 方向 | 已被本轮实现关闭 | 已补证据但仍需二次深化 | 仍未进入实施的下一批 backlog |
| --- | --- | --- | --- |
| Startup | 首帧后 gate-driven prewarm、固定 delay 移除 | 首帧时间仍偏高，任务清单还不够制度化 | compiled-mode / 第二设备复验 |
| Reader | `init / settings_update` 动作预算、恢复提示策略收口 | `flip` 设备样本仍缺 | 完整动作级压测矩阵 |
| Welfare / WebView | bootstrap / once-only 协调继续收口，首开/复开样本补齐 | FCP / TTI 仍有波动 | cache / cookie / 更深 benchmark |
| Search | trigger-aware retry、`INITIAL_ENTRY / CATEGORY_SWITCH / FILTER_APPLY` 设备样本补齐 | `LOAD_MORE` 样本缺口仍在 | 热点 benchmark 与正式 budget diff |
| RN Host / Bridge | `COLD_OPEN / OPEN / REUSED` 语义与主题同步样本补齐 | root view 生命周期治理仍可继续沉淀 | 更系统的 host path 规范与批量调用收益复盘 |
| 数据库 / 缓存 | 治理报告增强完成 | recommendation 已有，但收益证明不足 | 索引、`FTS4`、cleanup 对 IO / 内存 / 电量的定量复盘 |

## 一、已被本轮实现关闭
### Startup
- `ComposeMainActivityFirstFrameCoordinator` 已从固定 `delay(100/200)` 收敛到显式 first-frame plan。
- `ReactNativePrewarmCoordinator` 与 `StartupDeferredInitializationCoordinator` 的触发已收口为首帧后 gate-driven 路径。
- `2026-03-27` 真机样本已证明：
  - `prewarm_after_first_frame`
  - `create_react_context_in_background`
  两条日志会在首帧后落地。

### Reader
- `ReaderPerformanceTraceCoordinator` 的 `init / settings_update / flip` 预算与 trace 格式已经落地。
- `ReaderRestoreHintCoordinator` 已从固定时长退出，转为 restore-aware dismissal。
- `2026-03-27` 已补到当天真机 `init` 与 `settings_update` 样本。

### Welfare / WebView
- `WelfarePageBootstrapCoordinator` 与 `WelfareWebPerformanceCoordinator` 已继续收口分散副作用。
- `2026-03-27` 已补到：
  - 首开样本
  - 返回首页后复开样本
  - `savedState restore` 样本

### Search
- `SearchRetryPolicyCoordinator` 已按 `INITIAL_ENTRY / FILTER_APPLY / USER_RETRY / LOAD_MORE` 固定触发源语义。
- `2026-03-27` 已补到：
  - `INITIAL_ENTRY`
  - `CATEGORY_SWITCH`
  - `FILTER_APPLY`
  的设备样本。

### RN Host / Bridge
- `ReactNativeHostPathTraceCoordinator` 的 `COLD_OPEN / OPEN / REUSED` 已从日志枚举推进到当天设备证据。
- `ReactNativeThemeSyncCoordinator` 与 `ReactNativePage.kt` 的主题同步规则已有运行时样本支撑。

### 数据库 / 缓存
- `DatabaseGovernanceReportGenerator` 已具备 summary / scan risk / FTS coverage warning。
- `CacheGovernanceReportGenerator` 已具备 cleanup ratio / bytes cleaned / warning 输出。

## 二、已补证据但仍需二次深化
### Startup
- `2026-03-27` 样本中：
  - `首帧绘制 = 1019ms`
  - `完全加载 = 1086ms`
- 当前已经有样本，但优化建议仍提示“首帧渲染时间较长”，说明这条线还没到彻底关账。

### Reader
- 当前已经有 `init / settings_update` 的当天真机样本。
- 但 `flip` 仍没有可信设备样本，说明 Reader 动作级证据还没完全闭环。

### Welfare / WebView
- 首开与复开样本都已补齐。
- 但 FCP / TTI 仍存在波动，说明当前更多是“路径可追溯”，还不是“收益已稳定收敛”。

### Search
- 首开、分类切换、筛选应用都已有样本。
- 但 `search-load-more-probe-2026-03-27.txt` 显示多组 query 均返回 `hasMore=false`，所以 `LOAD_MORE` 的缺口仍然客观存在。

### RN Host / Bridge
- 当前已经能在当天样本里看到：
  - `COLD_OPEN`
  - `OPEN`
  - `REUSED`
  - `theme synced to RN: light`
- 但 `ReactRootView` 缓存生命周期治理仍未沉淀成更正式的长期规范。

### 数据库 / 缓存
- 当前 recommendation / warning 已经足以支持治理讨论。
- 但它们仍是“风险识别入口”，不是“收益已经被定量证明”。

## 三、仍未进入实施的下一批 backlog
### Startup
- compiled-mode startup / baseline profile 的第二设备复验
- 首帧后任务清单的优先级、收益复核与更正式治理表

### Reader
- `flip` 的稳定设备样本
- `settings_update` / `flip` / `init` 的完整动作级样本归档模板

### Welfare / WebView
- 更深层的 WebView cache / cookie / 预加载收益量化
- 首开 / 复开 / 回退复用的专项 benchmark 化

### Search
- `LOAD_MORE` 设备样本
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
- Search
  - `docs/refactor/evidence/search-hot-actions-logcat-2026-03-27.txt`
  - `docs/refactor/evidence/search-load-more-probe-2026-03-27.txt`
- Welfare / WebView
  - `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-27.txt`
- RN Host
  - `docs/refactor/evidence/rn-host-path-logcat-2026-03-27.txt`
- Reader
  - `docs/refactor/evidence/reader-performance-logcat-2026-03-27.txt`

## 总结
- 本轮已实现关闭的条目，不应继续留在“仍可继续优化的点”里。
- 当前真正还在 backlog 里的，主要是：
  - Search `LOAD_MORE`
  - Reader `flip`
  - Startup 首帧继续收敛
  - Welfare / WebView 更深 benchmark
  - 数据库 / 缓存收益复盘
- 这些项都应继续留在 `Phase 6` 的优化池，而不是误转成 `Phase 7` 的主线内容。
