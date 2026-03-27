# Search Hot Action Matrix - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / Search backlog 深化`
- 当前结论：`热点动作矩阵已落地，分页样本仍缺`

## 目的
- 固定 Search 线后续取证时优先覆盖的热点动作。
- 避免以后再用“搜索还要补 benchmark”这种泛化表述，却说不清到底要补哪些动作。

## 当前热点动作矩阵
| Action | Trigger | Current Evidence | Current Status | Next Step |
| --- | --- | --- | --- | --- |
| `INITIAL_ENTRY` | debug route 冷启进入 `search_result` | `search-hot-actions-logcat-2026-03-27.txt` | `有当天设备样本` | 后续可补 benchmark / budget diff |
| `CATEGORY_SWITCH` | 搜索结果页点击分类 chip | `search-hot-actions-logcat-2026-03-27.txt` | `有当天设备样本` | 后续可补稳定多次样本 |
| `FILTER_APPLY` | 打开筛选 sheet 后点击“确定” | `search-hot-actions-logcat-2026-03-27.txt` | `有当天设备样本` | 后续可补筛选组合差异 |
| `LOAD_MORE` | 列表触底分页 | `search-load-more-probe-2026-03-27.txt` | `当前缺样本` | 需要换 query / 数据集或专用探针 |

## 当前取证命令
### 首开
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "search_result?query=的"`

### 分类切换
- `adb shell input tap 220 380`

### 筛选应用
- `adb shell input tap 975 380`
- `adb shell input tap 220 920`
- `adb shell input tap 720 2120`

## 当前字段口径
- `trigger`
- `query`
- `page`
- `resultCount`
- `hasMore`
- `durationMs`

## 当前结论
- Search 当前最重要的热点动作入口已经有正式矩阵，而不是只剩零散 log sample。
- 真正还没补上的重点是 `LOAD_MORE`，不是首开 / 分类 / 筛选。

## 主要引用
- `docs/refactor/evidence/search-hot-actions-logcat-2026-03-27.txt`
- `docs/refactor/evidence/search-load-more-probe-2026-03-27.txt`
- `android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchPerformanceTraceCoordinator.kt`
- `android/feature-search/src/main/java/com/novel/page/search/viewmodel/SearchRetryPolicyCoordinator.kt`
