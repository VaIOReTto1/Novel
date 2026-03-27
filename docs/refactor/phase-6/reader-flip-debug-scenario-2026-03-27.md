# Reader Flip Debug Scenario - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / Reader flip gap unblock`
- 当前结论：`已具备自动 flip scenario，待设备恢复后补真机样本`

## 背景
- 当前 Reader `init / settings_update` 已有当天真机样本。
- `flip` 一直卡在人工 swipe / click 不稳定，问题不在 trace 本身，而在设备取证路径不稳定。

## 新增支架
- `RuntimeDebugScenarioStore` 现已支持：
  - `debug_reader_auto_flip`
- `ReaderDebugScenarioCoordinator`
  - 会在 Reader 初始化成功且已有当前页内容时，仅触发一次自动翻页
- `ReaderPage`
  - 已接入该 coordinator，并在满足条件时自动发送 `ReaderIntent.PageFlip`

## 推荐复现场景
### 命令
```powershell
adb shell am start -S -n com.novel/.ComposeMainActivity `
  --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776" `
  --es debug_reader_auto_flip next
```

### 预期
- Reader init 完成后自动触发一次 `PageFlip(NEXT)`
- `ReaderPerfProbe` 中出现：
  - `phase=start action=flip`
  - `phase=finish action=flip`

## 当前状态更新
- 这是 debug-only 的取证支架，不改变正式 route、bridge payload 或 Reader 语义。
- 该 scenario 已在 `2026-03-28` 被实际用于补齐 `flip` 真机样本：
  - `docs/refactor/phase-6/device-evidence-addendum-2026-03-28.md`

## 主要引用
- `android/core-common/src/main/java/com/novel/debug/RuntimeDebugScenarioStore.kt`
- `android/feature-reader/src/main/java/com/novel/page/read/viewmodel/ReaderDebugScenarioCoordinator.kt`
- `android/app/src/main/java/com/novel/page/read/ReaderPage.kt`
- `android/app/src/main/java/com/novel/ComposeMainActivity.kt`
- `android/feature-reader/src/test/java/com/novel/page/read/viewmodel/ReaderDebugScenarioCoordinatorTest.kt`
