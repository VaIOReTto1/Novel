# Reader Action Evidence Template - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / Reader backlog 深化`
- 当前结论：`动作级样本归档模板已落地，flip 设备样本仍缺`

## 目的
- 固定 Reader 动作级证据归档时必须保留的字段与最低样本要求。
- 把 `init / settings_update / flip` 从“知道要测”升级成“知道怎么留证据”。

## 适用动作
| Action | Required | Current Status |
| --- | --- | --- |
| `init` | `yes` | `2026-03-27` 已有当天真机样本 |
| `settings_update` | `yes` | `2026-03-27` 已有当天真机样本 |
| `flip` | `yes` | `当天仍缺可信设备样本` |

## 每条样本必须包含
1. 日期
2. 设备标识
3. 路由或进入方式
4. 操作动作
5. `phase=start` 与 `phase=finish` 日志
6. `durationMs`
7. `budgetMs`
8. `budgetStatus`
9. 附带 metadata

## 推荐模板
```md
### <action>
- Date: `YYYY-MM-DD`
- Device: `<device-id / model / android-version>`
- Route: `<debug route or navigation path>`
- Command:
  - `<adb / test command>`
- Start Trace:
  - `<phase=start ...>`
- Finish Trace:
  - `<phase=finish ...>`
- Conclusion:
  - `within / over / missing`
- Raw Evidence:
  - `<path>`
```

## 当前字段口径
### `init`
- `bookId`
- `entry`
- `chapterId`
- `pageCount`

### `settings_update`
- `mode`
- `fontSize`
- `rebuildVirtualPages`
- `pageCount`

### `flip`
- `source`
- `direction` 或 `fromIndex/toIndex`
- `mode`
- `outcome`

## 当前结论
- `ReaderPerformanceTraceCoordinator` 已经能稳定输出动作级预算字段。
- 现在缺的不是模板，而是 `flip` 的稳定设备取证路径。

## 主要引用
- `docs/refactor/evidence/reader-performance-logcat-2026-03-27.txt`
- `android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderPerformanceTraceCoordinator.kt`
- `android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderViewModel.kt`
