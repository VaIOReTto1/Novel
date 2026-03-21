# Reader Performance Baseline - 2026-03-21

## Scenario
- Cold-start into Reader via debug route
- Route: `reader/1334318497132552192?chapterId=1334318500051787776`
- Actions attempted:
  - open reader
  - single horizontal swipe to trigger a page flip sample

## Command
- `adb logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route "reader/1334318497132552192?chapterId=1334318500051787776"`
- `adb shell input swipe 900 1200 180 1200 300`
- `adb logcat -d | Select-String <Reader patterns>`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`

## Expected
- Reader route must open without request failure.
- The baseline doc must at least capture:
  - reader init route
  - page-flip action
  - settings change action
  - current gaps if a direct sample is missing

## Actual
- `NavigationSetup` jumped to the reader route at `21:36:08.897`.
- `ReaderPage` logged parameter change and `开始加载书籍和章节内容` at `21:36:09.163`.
- Reader state snapshot logged immediately:
  - background `#FFF5F5DC`
  - text `#FF2E2E2E`
  - font size `16sp`
  - page-flip effect `PAGECURL`
- Reader history save logged at `21:36:10.384`, proving the init path reached a stable page payload.
- Background pagination then continued with `ReaderViewModel` progress updates through the sampled window.
- The current swipe attempt did not yield a trustworthy `FlipPageUseCase` log sample.
- The current run also did not produce a trustworthy `UpdateSettingsUseCase` log sample.

## Evidence
- `docs/refactor/evidence/reader-performance-logcat-2026-03-21.txt`
- `docs/refactor/tracking/decision-log.md`

## Result
- `pass-with-documented-gaps`

## Residual Risk
- Reader init is now evidenced, but direct numeric samples for:
  - flip action
  - settings update action
  remain missing in the current run.
- If Phase 7 or later needs hard regression gates on those two actions, a dedicated automation path or a debug-only probe will still be needed.
