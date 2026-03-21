# 启动 Benchmark 运行记录 - 2026-03-21

## 场景
- 在 `Phase 6` 已验证的 benchmark 套件上建立稳定的启动基线。
- 本次覆盖的用例：
  - `ExampleStartupBenchmark.startup`
  - `ExampleStartupBenchmark.startupNoCompilation`

## 命令
- `android/gradlew.bat :macrobenchmark:connectedBenchmarkAndroidTest "-Pandroid.testInstrumentationRunnerArguments.class=com.novel.macrobenchmark.ExampleStartupBenchmark"`

## 设备 / 构建
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建变体：`debug`
- 目标应用：`com.novel`

## 预期
- 默认启动基线在真机上保持绿色可重复。
- 编译型启动探针不再阻塞默认绿色套件。

## 实际结果
- 命令在连接设备上执行成功。
- `startup` 的 `timeToInitialDisplayMs`：
  - 最小值：`647.4 ms`
  - 中位数：`663.8 ms`
  - 最大值：`721.3 ms`
- `startupNoCompilation` 的 `timeToInitialDisplayMs`：
  - 最小值：`647.4 ms`
  - 中位数：`654.4 ms`
  - 最大值：`734.7 ms`
- 启动日志样本仍可追溯到应用侧初始化拆解：
  - `ThemeManager = 5 ms`
  - `SoLoader = 24 ms`
  - `Application onCreate = 31 ms`

## 证据
- `docs/refactor/evidence/startup-benchmark-testlog-2026-03-21.txt`
- `docs/refactor/evidence/startup-benchmark-report-2026-03-21.html`
- `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`

## 结论
- `通过`

## 残余风险
- 当前启动基线有意限制在 `CompilationMode.None()`。
- 编译型启动路径已被拆到 probe 套件，原因不是仓库回归，而是 `DN2101` 的设备侧 `cmd package compile` 仍不可靠。
