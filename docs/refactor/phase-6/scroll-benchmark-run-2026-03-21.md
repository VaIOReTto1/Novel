# 滚动 Benchmark 运行记录 - 2026-03-21

## 场景
- 在 `Phase 6` 已验证的 benchmark 套件上建立首页滚动基线。
- 本次覆盖的用例：
  - `ScrollPerformanceBenchmark.scrollingPerformance`

## 命令
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ScrollPerformanceBenchmark"`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`
- 目标应用：`com.novel`

## 预期
- 默认首页滚动基线在真机上保持绿色可重复。
- 编译型滚动探针不再阻塞默认绿色套件。

## 实际结果
- 命令在连接设备上执行成功。
- `ScrollPerformanceBenchmark.scrollingPerformance` 记录到：
  - `frameCount`：最小 `657`，中位数 `672`，最大 `1023`
  - `frameDurationCpuMs`：`P50 13.3`，`P90 18.5`，`P95 20.9`，`P99 27.7`
  - `frameOverrunMs`：`P50 -1.2`，`P90 4.4`，`P95 7.4`，`P99 16.0`

## 证据
- `docs/refactor/evidence/scroll-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/scroll-benchmark-report-2026-03-21.html`

## 结论
- `通过`

## 残余风险
- 当前滚动基线只证明了 no-compilation 路径。
- `ScrollCompilationProbeBenchmark` 仍是后续 profile / compiled-mode 滚动实验的正确入口。
