# Scroll Benchmark Run - 2026-03-21

## Scenario
- Stable homepage scroll baseline on the validated `Phase 6` benchmark suite
- Target case:
  - `ScrollPerformanceBenchmark.scrollingPerformance`

## Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ScrollPerformanceBenchmark"`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`
- Host App: `com.novel`

## Expected
- The default scroll baseline must remain green on the connected device.
- Scroll compiled-mode probes must not block the daily benchmark suite.

## Actual
- Command passed on the connected device.
- `ScrollPerformanceBenchmark.scrollingPerformance` recorded:
  - `frameCount`: min `657`, median `672`, max `1023`
  - `frameDurationCpuMs`: `P50 13.3`, `P90 18.5`, `P95 20.9`, `P99 27.7`
  - `frameOverrunMs`: `P50 -1.2`, `P90 4.4`, `P95 7.4`, `P99 16.0`

## Evidence
- `docs/refactor/evidence/scroll-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/scroll-benchmark-report-2026-03-21.html`

## Result
- `pass`

## Residual Risk
- The current run only proves the no-compilation scroll path.
- `ScrollCompilationProbeBenchmark` remains the correct place for future profile/compiled scroll experiments.
