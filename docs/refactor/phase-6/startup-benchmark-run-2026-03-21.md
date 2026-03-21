# Startup Benchmark Run - 2026-03-21

## Scenario
- Stable startup baseline on the validated `Phase 6` benchmark suite
- Target cases:
  - `ExampleStartupBenchmark.startup`
  - `ExampleStartupBenchmark.startupNoCompilation`

## Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark"`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`
- Host App: `com.novel`

## Expected
- The daily startup baseline must stay green on the connected device.
- Compiled-mode startup probes must no longer be part of the default green suite.

## Actual
- Command passed on the connected device.
- `startup` recorded `timeToInitialDisplayMs`:
  - min `647.4 ms`
  - median `663.8 ms`
  - max `721.3 ms`
- `startupNoCompilation` recorded `timeToInitialDisplayMs`:
  - min `647.4 ms`
  - median `654.4 ms`
  - max `734.7 ms`
- The startup log sample still captured application-side initialization markers:
  - `ThemeManager = 5 ms`
  - `SoLoader = 24 ms`
  - `Application onCreate = 31 ms`

## Evidence
- `docs/refactor/evidence/startup-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/startup-benchmark-report-2026-03-21.html`
- `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`

## Result
- `pass`

## Residual Risk
- This baseline is intentionally limited to `CompilationMode.None()`.
- Compiled startup modes were moved to the probe suite because `DN2101` cannot reliably execute `cmd package compile`.
