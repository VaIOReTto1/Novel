# Reader 性能基线 - 2026-03-21

## 场景
- 通过 debug route 冷启动进入 Reader。
- 路由：`reader/1334318497132552192?chapterId=1334318500051787776`
- 本次尝试的动作：
  - 打开 Reader
  - 执行一次水平 swipe，尝试抓取翻页样本

## 命令
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776"`
- `adb shell input swipe 900 1200 180 1200 300`
- `adb logcat -d | Select-String <Reader patterns>`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`

## 预期
- Reader 路由应无“请求错误”地成功打开。
- 基线文档至少应覆盖：
  - reader init 路径
  - page flip 动作
  - settings change 动作
  - 当前缺口说明

## 实际结果
- `NavigationSetup` 在 `21:36:08.897` 跳到 Reader 路由。
- `ReaderPage` 在 `21:36:09.163` 打出参数变化与“开始加载书籍和章节内容”日志。
- Reader 首次状态快照立即可见：
  - 背景色：`#FFF5F5DC`
  - 文字色：`#FF2E2E2E`
  - 字号：`16sp`
  - 翻页效果：`PAGECURL`
- `ReaderPage` 在 `21:36:10.384` 记录了历史保存，说明 init 路径已到达稳定页面载荷。
- 后台分页随后继续推进，样本窗口内能看到持续的 `ReaderViewModel` 分页进度日志。
- 本次 swipe 没有抓到可信的 `FlipPageUseCase` 直接日志样本。
- 本次运行也没有抓到可信的 `UpdateSettingsUseCase` 直接日志样本。

## 证据
- `docs/refactor/evidence/reader-performance-logcat-2026-03-21.txt`
- `docs/refactor/tracking/decision-log.md`

## 结论
- `通过，但保留缺口`

## 残余风险
- Reader init 已有证据，但以下两项仍缺可信的直接数值样本：
  - flip action
  - settings update action
- 如果后续要把这两项提升成硬性能门禁，仍需要单独的自动化路径或 debug-only 探针。
