# Phase 6 Performance Budget Summary

| Area | Metric | Current | Budget / Target | Status | Evidence |
| --- | --- | --- | --- | --- | --- |
| Startup | `timeToInitialDisplayMs` median | `654.4 ms` (`startupNoCompilation`) | `<= 700 ms` on `DN2101` debug no-compilation | `green` | `startup-benchmark-run-2026-03-21.md` |
| Startup | `timeToInitialDisplayMs` median | `663.8 ms` (`startup`) | `<= 700 ms` on the default green suite | `green` | `startup-benchmark-run-2026-03-21.md` |
| Home Scroll | `frameDurationCpuMs P95` | `20.9 ms` | `<= 24 ms` | `green` | `scroll-benchmark-run-2026-03-21.md` |
| Home Scroll | `frameOverrunMs P95` | `7.4 ms` | `<= 10 ms` | `green` | `scroll-benchmark-run-2026-03-21.md` |
| Search Result | route jump -> success log | `~945 ms` | `<= 1200 ms` | `green` | `search-performance-baseline-2026-03-21.md` |
| Reader Init | route jump -> first ReaderPage load log | `~266 ms` | `<= 500 ms` | `green` | `reader-performance-baseline-2026-03-21.md` |
| Reader Flip | direct flip sample | `missing in current run` | `capture a direct FlipPageUseCase sample before future hard perf gating` | `documented gap` | `reader-performance-baseline-2026-03-21.md` |
| Reader Settings | direct settings update sample | `missing in current run` | `capture a direct UpdateSettingsUseCase sample before future hard perf gating` | `documented gap` | `reader-performance-baseline-2026-03-21.md` |
| Welfare Init | `InitializeWelfarePageUseCase` | `313-555 ms` | `<= 600 ms` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| Welfare WebView | WebView load completion | `266 ms` | `<= 400 ms` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host Profile | route jump -> RN context ready | `~2.08 s` | `<= 2.5 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host Settings | route jump -> settings load success | `~3.00 s` | `<= 3.5 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| RN Host AI | route jump -> RN context ready | `~3.77 s` | `<= 4.0 s` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
| Baseline Profile | connected generation | `blocked on device compile` | `must either generate or remain reproducibly blocked with remediation path` | `accepted blocker` | `baseline-profile-run-2026-03-21.md` |
| Device Compile | `cmd package compile` on `com.novel` and `com.android.settings` | `both fail with the same shell error` | `re-verify on a second device before promoting compiled-mode perf gates` | `accepted blocker` | `device-compile-blocker-2026-03-21.md` |
| Bridge Contract | runtime semantics | `green via smoke + contract + host rerun evidence` | `no route / payload drift` | `green` | `webview-bridge-performance-baseline-2026-03-21.md` |
