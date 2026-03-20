# Host Risk Run - settings - 2026-03-20

- Scenario: `settings` 首次进入宿主页验证
- Command:
  - `adb connect 192.168.8.130:5555`
  - `adb shell am start -n com.novel/.ComposeMainActivity --es debug_route settings`
  - `adb shell input keyevent 224`
  - `adb shell wm dismiss-keyguard`
  - `adb shell input swipe 540 1800 540 300`
- Route / Page: `settings`
- Device / API: `192.168.8.130:5555 / DN2101 / Android 13 / API 33`
- Network: `Wi-Fi adb`
- Build Variant: `debug`
- Expected:
  - `ComposeMainActivity` 成功承接 `debug_route=settings`
  - `ReactNativePage` 宿主页挂载成功
  - 设置页正常显示，无白屏
- Actual:
  - 当前保留日志确认 `ComposeMainActivity` 通过 `debug_route=settings` 被拉起，宿主页验证命令真实执行过
  - 已归档 `settings-host-first-open-2026-03-20.png` 与 `settings-host-unlocked-2026-03-20.png`
  - 同轮人工复核结论为设置页显示正常，无白屏、无首开挂载失败
  - 现存 `settings-window-unlocked.xml` 与日志片段没有像 `profile` 那样完整保留 RN 初始化细节，因此本条的强证据主要来自截图归档 + 人工复核，而不是完整 XML/log 正链
- Evidence Files:
  - `docs/refactor/evidence/settings-host-first-open-2026-03-20.png`
  - `docs/refactor/evidence/settings-host-unlocked-2026-03-20.png`
  - `docs/refactor/evidence/settings-window-unlocked-2026-03-20.xml`
  - `docs/refactor/evidence/settings-logcat-unlocked-2026-03-20.txt`
- Result: `pass`

## Host Chain
- Route Source: `NavigationUtil.kt`
- Host Container: `ReactNativePage.kt`
- Root Cache Source: `MainApplication.getOrCreateReactRootView()`
- Bridge Exit: `NavigationBridgeModule`

## Notes
- 该场景已具备“命令触发 + 截图归档 + 人工正向验收”的证据闭环，但日志强度弱于 `profile`。
- 后续若需要把 `V4-05` 从 `ready_for_validation` 提升到更高确定性，可在不改逻辑前提下补一轮更完整的 RN 初始化日志采集。
