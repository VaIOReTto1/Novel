# Baseline Profile 运行记录 - 2026-03-21

## 场景
- 在连接的 `DN2101` 设备上执行 baseline profile 生成探针。
- 本次覆盖的用例：
  - `BaselineProfileGenerator.generate`

## 命令
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.BaselineProfileGenerator"`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`
- 目标 profile 应用：`com.novel`

## 预期
- 要么生成可追溯的 baseline profile 产物，
- 要么产生可复现的 blocker，并带出后续 remediation path。

## 实际结果
- 命令在连接设备上失败。
- 失败形态稳定为：
  - `java.lang.IllegalStateException: Failed to compile (out=Error: Failed to cpmpile !)`
  - 栈顶落在 `CompilationMode.cmdPackageCompile(...)`
- 同样的失败形态与 benchmark 外部直接执行的 shell compile blocker 一致。

## 证据
- `docs/refactor/evidence/baseline-profile-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/baseline-profile-benchmark-report-2026-03-21.html`
- `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- `docs/refactor/evidence/device-compile-blocker-2026-03-21.txt`

## 结论
- `阻塞但可解释`

## 残余风险
- `DN2101` 当前不能作为 baseline profile 生成设备。
- 后续若要把 compiled-mode startup/profile 升级成硬门禁，需要在第二台设备或可用 emulator 上复验。
