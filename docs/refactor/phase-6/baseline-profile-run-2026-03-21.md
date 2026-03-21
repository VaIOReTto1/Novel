# Baseline Profile Run - 2026-03-21

## Scenario
- Baseline profile generation probe on the connected `DN2101` device
- Target case:
  - `BaselineProfileGenerator.generate`

## Command
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.BaselineProfileGenerator"`

## Device / Build
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build Variant: `debug`
- Host App for target profile: `com.novel`

## Expected
- Either generate a reproducible baseline profile artifact, or produce a reproducible blocker with a clear remediation path.

## Actual
- Command failed on the connected device.
- Failure shape:
  - `java.lang.IllegalStateException: Failed to compile (out=Error: Failed to cpmpile !)`
  - stack top remained at `CompilationMode.cmdPackageCompile(...)`
- The same failure shape matches the direct shell compile blocker reproduced outside the benchmark harness.

## Evidence
- `docs/refactor/evidence/baseline-profile-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/baseline-profile-benchmark-report-2026-03-21.html`
- `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- `docs/refactor/evidence/device-compile-blocker-2026-03-21.txt`

## Result
- `blocked-with-remediation`

## Residual Risk
- `DN2101` cannot currently be trusted as a baseline-profile generation device.
- Future compiled-mode benchmarking must be retried on a second device or emulator with a functioning `cmd package compile` path.
