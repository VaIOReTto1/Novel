# Stage 3 Performance Baseline - 2026-03-21

## Summary
- Phase: `Phase 6`
- Status: `kickoff baseline`
- Goal: establish the first executable performance baseline package on top of the validated Phase 5 module graph.

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

## Startup Baseline
### Command
- `adb shell logcat -c`
- `adb shell am start -S -n com.novel/.ComposeMainActivity`
- `adb logcat -d | Select-String StartupPerformanceMonitor`

### Actual
- `StartupPerformanceMonitor` log samples captured:
  - `ThemeManager 初始化耗时: 5ms`
  - `SoLoader 初始化耗时: 28ms`
  - `Application onCreate 完成，耗时: 36ms`
  - `RetrofitClient 初始化耗时: 0ms`
  - `SettingsUtils 初始化耗时: 0ms`

### Evidence
- `temp/phase6-startup-logcat.txt`
- `android/app/src/main/java/com/novel/utils/performance/StartupPerformanceMonitor.kt`

## Macrobenchmark Startup Baseline
### Intended Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark"`

### Actual
- Command reached the macrobenchmark build pipeline, but did not produce a trustworthy startup baseline.
- Current blocker:
  - Kotlin/Gradle release compilation cache instability inside Stage 3 modularized modules during benchmark variant preparation.

### Evidence / Failure Shape
- Release/benchmark preparation hit cache-close / missing-artifact instability under module release compilation.
- This is currently treated as a Phase 6 kickoff blocker for `V6-02`, not as a Phase 5 regression.

## Scroll Baseline
### Intended Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ScrollPerformanceBenchmark"`

### Actual
- Command reached the benchmark setup, but no stable scroll benchmark report was produced yet.
- Current blocker is the same release compilation cache instability seen in startup benchmark preparation.

## Baseline Profile State
### Intended Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.BaselineProfileGenerator"`

### Actual
- Baseline profile generation is not yet stable in the current environment.
- Two concrete blockers were observed:
  - Kotlin incremental cache cleanup instability in release/benchmark preparation
  - `:app:createBundleReleaseJsAndAssets` failed to produce `index.android.bundle.hbc`

### Existing Baseline Assets
- `android/macrobenchmark/src/main/java/com/novel/macrobenchmark/BaselineProfileGenerator.kt`
- `android/macrobenchmark/src/main/java/com/novel/macrobenchmark/ExampleStartupBenchmark.kt`

## Current Gaps
- Reader performance is still measured on the `app` boundary.
- Welfare / WebView / Bridge still rely on mixed automated + manual evidence.
- `core-network` deeper modularization remains carried debt from Phase 5, but it is not a blocker for starting Phase 6.
- Macrobenchmark connected runs are currently blocked by release/benchmark preparation instability and need a dedicated stabilization pass before `V6-02` can go green.

## Immediate Next Actions
1. Stabilize `:macrobenchmark:connectedBenchmarkAndroidTest` for startup and scroll suites.
2. Capture the first trustworthy benchmark output files and link them from this baseline doc.
3. Add a Reader-specific Phase 6 baseline document once benchmark and startup collection are stable.
