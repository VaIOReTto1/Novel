# WebView / Bridge Performance Baseline - 2026-03-21

## Scenario
- Welfare WebView cold open from the main tab
- RN Host cold opens through:
  - `profile`
  - `settings`
  - `aipage`
- Bridge compatibility remains guarded by existing contract tests and RN smoke tests

## Command
- Welfare:
  - `adb logcat -c`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity`
  - `adb shell input tap 540 2136`
  - `adb logcat -d | Select-String "WelfarePage|WebViewComponent|WelfarePerformanceMonitor"`
- RN Host:
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route profile`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route settings`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route aipage`
  - `adb logcat -d | Select-String "ReactNativePage|ThemeManager|SettingsViewModel"`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`

## Expected
- Welfare must reach a visible WebView and record load lifecycle logs.
- RN Host routes must still mount `ReactNativePage` without changing route/payload semantics.
- Phase 6 does not require fully benchmarked host/webview runs, but each area must have traceable evidence.

## Actual
### Welfare / WebView
- `InitializeWelfarePageUseCase` started at `21:41:08.538`.
- Welfare init completed in two observed samples:
  - `313 ms`
  - `555 ms`
- `WelfarePerformanceMonitor` recorded page-load completion samples:
  - `414 ms`
  - `743 ms`
- `WebViewComponent` started loading `https://cn.bing.com/` at `21:41:09.496`.
- `WebViewComponent` finished at `21:41:09.762`.
- `WelfarePerformanceMonitor` recorded WebView load completion at `266 ms`.

### RN Host
- `profile`
  - route jump at `21:44:37.526`
  - RN context ready at `21:44:39.606`
  - cold host attach sample: about `2.08 s`
- `settings`
  - route jump at `21:44:49.698`
  - RN context ready at `21:44:52.666`
  - settings load success at `21:44:52.695`
  - cold host attach sample: about `3.00 s`
- `aipage`
  - route jump at `21:45:00.638`
  - RN context ready at `21:45:04.404`
  - cold host attach sample: about `3.77 s`

### Bridge / Compatibility
- Existing bridge contract tests remain the authoritative compatibility guard.
- Existing RN settings smoke and Phase 4/5 host-risk docs remain valid supporting evidence for:
  - route semantics
  - `ThemeChanged`
  - host-page compatibility

## Evidence
- `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-21.txt`
- `docs/refactor/evidence/rn-host-performance-logcat-2026-03-21.txt`
- `docs/refactor/phase-5/host-compat-validation-2026-03-21.md`
- `docs/refactor/phase-4/host-risk-run-profile-2026-03-20.md`
- `docs/refactor/phase-4/host-risk-run-settings-2026-03-20.md`
- `docs/refactor/phase-4/host-risk-run-author-ai-2026-03-20.md`
- `__tests__/bridge/NativeBridgeEventContracts.test.ts`
- `__tests__/smoke/SettingsPage.smoke.test.tsx`

## Result
- `pass-with-carried-host-evidence`

## Residual Risk
- Phase 6 runtime evidence for `ThemeChanged` is still weaker than the static contract + historical host evidence combination.
- Welfare performance sampling is log-based and tab-entry based; it is not yet a dedicated macrobenchmark.
