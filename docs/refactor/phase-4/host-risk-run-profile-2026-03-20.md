# Host Risk Run - profile - 2026-03-20

- Scenario: `profile` 冷启动后首次进入宿主页验证
- Command:
  - `adb connect 192.168.8.130:5555`
  - `adb shell am start -n com.novel/.ComposeMainActivity --es debug_route profile`
  - `adb shell input keyevent 224`
  - `adb shell wm dismiss-keyguard`
  - `adb shell input swipe 540 1800 540 300`
- Route / Page: `profile`
- Device / API: `192.168.8.130:5555 / DN2101 / Android 13 / API 33`
- Network: `Wi-Fi adb`
- Build Variant: `debug`
- Expected:
  - `ComposeMainActivity` 成功承接 `debug_route=profile`
  - `ReactNativePage` 成功挂载 `Novel`
  - `ProfilePage` 成功首开，无白屏
  - 初始主题 props 正常注入
- Actual:
  - `profile-window-unlocked.xml` 明确出现 `com.novel` 页面层级与 `我的 / 我的消息 / 成为作家 / 浏览历史` 等可识别元素
  - `profile-logcat-unlocked.txt` 保留了 `BridgeViewModel` 初始化、`ReactNativePage` 获取缓存 `ReactRootView`、主题预传递，以及 `ReactNativeJS: Running "Novel"` 且 `nativeMessage=ProfilePage` 的正向链路
  - `profile-rerun-logcat.txt` 还补到了 `ReactNativePage` 从 `isContextReady: false` 到 `RN上下文状态变更为就绪` 的初始化时序证据
  - 同轮人工复核结论为页面显示正常，无白屏或卡死
- Evidence Files:
  - `docs/refactor/evidence/profile-host-first-open-2026-03-20.png`
  - `docs/refactor/evidence/profile-host-rerun-2026-03-20.png`
  - `docs/refactor/evidence/profile-host-unlocked-2026-03-20.png`
  - `docs/refactor/evidence/profile-window-unlocked-2026-03-20.xml`
  - `docs/refactor/evidence/profile-logcat-unlocked-2026-03-20.txt`
  - `docs/refactor/evidence/profile-rerun-logcat-2026-03-20.txt`
- Result: `pass`

## Host Chain
- Route Source: `NavigationUtil.kt`
- Host Container: `ReactNativePage.kt`
- Root Cache Source: `MainApplication.getOrCreateReactRootView()`
- Bridge Exit: `NavigationBridgeModule`

## Notes
- 该场景是本轮 `V4-05` 中证据最强的一条，覆盖了路由进入、宿主页挂载、React 上下文就绪与主题初始注入。
- 当前保留的正向证据已足以支撑 `profile-host` 进入 `ready_for_validation`。
