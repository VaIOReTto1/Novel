# Host Risk Run - author-ai - 2026-03-20

- Scenario: `aipage` 首次进入宿主页验证
- Command:
  - `adb connect 192.168.8.130:5555`
  - `adb shell am start -n com.novel/.ComposeMainActivity --es debug_route aipage`
  - `adb shell input keyevent 224`
  - `adb shell wm dismiss-keyguard`
  - `adb shell input swipe 540 1800 540 300`
- Route / Page: `aipage`
- Device / API: `192.168.8.130:5555 / DN2101 / Android 13 / API 33`
- Network: `Wi-Fi adb`
- Build Variant: `debug`
- Expected:
  - `ComposeMainActivity` 成功承接 `debug_route=aipage`
  - 作者/AI 宿主页成功挂载
  - 页面可进入、无白屏
- Actual:
  - 当前保留日志确认 `ComposeMainActivity` 通过 `debug_route=aipage` 被拉起，宿主页验证命令真实执行过
  - 已归档 `aipage-host-first-open-2026-03-20.png` 与 `aipage-host-unlocked-2026-03-20.png`
  - 同轮人工复核结论为 AI 页面显示正常，无白屏、无首开挂载失败
  - 当前 `aipage` 的保留 XML/log 不如 `profile` 完整：`uiautomator` 原始 dump 未稳定留下可复用的正向 UI 层级，因此该条以截图归档 + 人工复核为主证据
- Evidence Files:
  - `docs/refactor/evidence/aipage-host-first-open-2026-03-20.png`
  - `docs/refactor/evidence/aipage-host-unlocked-2026-03-20.png`
  - `docs/refactor/evidence/aipage-logcat-unlocked-2026-03-20.txt`
- Result: `pass`

## Host Chain
- Route Source: `NavigationUtil.kt`
- Host Container: `ReactNativePage.kt`
- Root Cache Source: `MainApplication.getOrCreateReactRootView()`
- Bridge Exit: `NavigationBridgeModule`

## Notes
- 该场景满足 `V4-05` 对“至少一个作者/AI 宿主页场景”的覆盖要求。
- 现有证据已足够支撑 `aipage` 纳入本轮正向样本，但若后续需要补强初始化时序，可单独再做一次 AI 页专项日志采集。
