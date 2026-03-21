# WebView / Bridge 性能基线 - 2026-03-21

## 场景
- Welfare WebView 冷启动打开
- RN Host 冷启动覆盖：
  - `profile`
  - `settings`
  - `aipage`
- Bridge 兼容继续由已有 contract tests 与 RN smoke tests 守门

## 命令
- Welfare：
  - `adb logcat -c`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity`
  - `adb shell input tap 540 2136`
  - `adb logcat -d | Select-String "WelfarePage|WebViewComponent|WelfarePerformanceMonitor"`
- RN Host：
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route profile`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route settings`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route aipage`
  - `adb logcat -d | Select-String "ReactNativePage|ThemeManager|SettingsViewModel"`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`

## 预期
- Welfare 应能打开可见的 WebView，并留下加载生命周期日志。
- RN Host 路由应继续通过 `ReactNativePage` 挂载，不改变 route / payload 语义。
- Phase 6 不要求三者全部 benchmark 化，但每个区域都必须有可追溯证据。

## 实际结果
### Welfare / WebView
- `InitializeWelfarePageUseCase` 在 `21:41:08.538` 启动。
- Welfare 初始化在当前样本中出现了两次可见耗时：
  - `313 ms`
  - `555 ms`
- `WelfarePerformanceMonitor` 记录到页面加载完成样本：
  - `414 ms`
  - `743 ms`
- `WebViewComponent` 在 `21:41:09.496` 开始加载 `https://cn.bing.com/`。
- `WebViewComponent` 在 `21:41:09.762` 完成。
- `WelfarePerformanceMonitor` 记录到 WebView load completion 为 `266 ms`。

### RN Host
- `profile`
  - route jump：`21:44:37.526`
  - RN context ready：`21:44:39.606`
  - 冷启动宿主页 attach 样本约 `2.08 s`
- `settings`
  - route jump：`21:44:49.698`
  - RN context ready：`21:44:52.666`
  - settings load success：`21:44:52.695`
  - 冷启动宿主页 attach 样本约 `3.00 s`
- `aipage`
  - route jump：`21:45:00.638`
  - RN context ready：`21:45:04.404`
  - 冷启动宿主页 attach 样本约 `3.77 s`

### Bridge / 兼容
- Bridge contract tests 仍是权威兼容守门。
- RN settings smoke 与 Phase 4/5 宿主页文档继续提供：
  - route 语义
  - `ThemeChanged`
  - host 页面兼容性
  的支撑证据。

## 证据
- `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-21.txt`
- `docs/refactor/evidence/rn-host-performance-logcat-2026-03-21.txt`
- `docs/refactor/phase-5/host-compat-validation-2026-03-21.md`
- `docs/refactor/phase-4/host-risk-run-profile-2026-03-20.md`
- `docs/refactor/phase-4/host-risk-run-settings-2026-03-20.md`
- `docs/refactor/phase-4/host-risk-run-author-ai-2026-03-20.md`
- `__tests__/bridge/NativeBridgeEventContracts.test.ts`
- `__tests__/smoke/SettingsPage.smoke.test.tsx`

## 结论
- `通过，但带历史兼容证据`

## 残余风险
- `ThemeChanged` 的 Phase 6 运行时证据强度仍弱于静态 contract + 历史宿主页证据的组合。
- Welfare 当前仍是日志样本取证，不是独立 macrobenchmark。
