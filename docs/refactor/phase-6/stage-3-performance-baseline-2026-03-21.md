# Stage 3 Performance Baseline - 2026-03-21

## Summary
- Phase: `Phase 6`
- Status: `validated baseline package`
- Goal: turn the `Phase 6` kickoff snapshot into a traceable Stage 3 performance baseline package.

## Environment
- Device: `192.168.8.130:5555`
- Model / OS: `DN2101 / Android 13`
- Build Variant: `debug`
- Current module graph input:
  - `core-common`
  - `core-storage`
  - `core-network`
  - `core-bridge-contract`
  - `feature-home`
  - `feature-search`
  - `feature-welfare`
  - `feature-rn-host`
  - `app` remains composition root

## Baseline Package
- Startup benchmark:
  - `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- Scroll benchmark:
  - `docs/refactor/phase-6/scroll-benchmark-run-2026-03-21.md`
- Baseline profile run:
  - `docs/refactor/phase-6/baseline-profile-run-2026-03-21.md`
- Direct device compile blocker:
  - `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- Search baseline:
  - `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- Reader baseline:
  - `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- Welfare / WebView / Bridge baseline:
  - `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- Budget summary:
  - `docs/refactor/phase-6/performance-budget-summary.md`

## Startup Baseline
- App-side startup sample remains traceable in:
  - `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`
- Stable benchmark baseline is now no-compilation only:
  - `startupNoCompilation median = 654.4 ms`
  - `startup median = 663.8 ms`

## Scroll Baseline
- Stable scroll benchmark baseline is now no-compilation only:
  - `frameDurationCpuMs P95 = 20.9 ms`
  - `frameOverrunMs P95 = 7.4 ms`

## Baseline Profile State
- Current state: `blocked-with-remediation`
- Reason:
  - `BaselineProfileGenerator` still fails at the device-side compile step
  - direct shell reproduction shows the same error on:
    - `com.novel`
    - `com.android.settings`
- Conclusion:
  - this is treated as a `DN2101` environment blocker, not as a `Phase 5` or app-runtime regression

## Reader / Search / WebView / Host
- Search now has a formal log-sample baseline.
- Reader now has a formal init baseline with documented flip/settings gaps.
- Welfare / WebView / RN Host / Bridge now have a single aggregated baseline document.

## Current Gaps
- Reader flip and settings update still lack a trustworthy direct numeric sample.
- Baseline profile generation still needs a second device or emulator with a functioning `cmd package compile` path.

## Verdict
- This baseline package is sufficient to close `V6-01`.
- It is also sufficient to close `V6-02` under the accepted rule:
  - generated profile, or
  - reproducible environment blocker with remediation path
