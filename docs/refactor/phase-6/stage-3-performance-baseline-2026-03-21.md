# Stage 3 性能基线总入口 - 2026-03-21

## 摘要
- 所属阶段：`Phase 6`
- 当前状态：`已验证的基线包`
- 目标：把 `Phase 6` kickoff 阶段的零散样本收成一套可追溯的 Stage 3 性能基线包。

## 环境
- 设备：`192.168.8.130:5555`
- 机型 / 系统：`DN2101 / Android 13`
- 构建变体：`debug`
- 当前模块边界输入：
  - `core-common`
  - `core-storage`
  - `core-network`
  - `core-bridge-contract`
  - `feature-home`
  - `feature-search`
  - `feature-welfare`
  - `feature-rn-host`
  - `app` 仍作为 composition root

## 基线包内容
- 启动 benchmark：
  - `docs/refactor/phase-6/startup-benchmark-run-2026-03-21.md`
- 首页滚动 benchmark：
  - `docs/refactor/phase-6/scroll-benchmark-run-2026-03-21.md`
- baseline profile 运行记录：
  - `docs/refactor/phase-6/baseline-profile-run-2026-03-21.md`
- 设备 compile 阻塞项：
  - `docs/refactor/phase-6/device-compile-blocker-2026-03-21.md`
- 搜索基线：
  - `docs/refactor/phase-6/search-performance-baseline-2026-03-21.md`
- Reader 基线：
  - `docs/refactor/phase-6/reader-performance-baseline-2026-03-21.md`
- Welfare / WebView / Bridge 基线：
  - `docs/refactor/phase-6/webview-bridge-performance-baseline-2026-03-21.md`
- 预算摘要：
  - `docs/refactor/phase-6/performance-budget-summary.md`

## 启动基线
- 应用侧启动样本可追溯到：
  - `docs/refactor/evidence/phase6-startup-logcat-2026-03-21.txt`
- 当前稳定 benchmark 基线已经收敛为 no-compilation 主线：
  - `startupNoCompilation median = 654.4 ms`
  - `startup median = 663.8 ms`

## 滚动基线
- 当前稳定滚动 benchmark 基线为：
  - `frameDurationCpuMs P95 = 20.9 ms`
  - `frameOverrunMs P95 = 7.4 ms`

## Baseline Profile 状态
- 当前状态：`阻塞但具备补救路径`
- 原因：
  - `BaselineProfileGenerator` 仍然卡在设备侧 compile 步骤
  - benchmark 外部直接执行 shell compile，也在：
    - `com.novel`
    - `com.android.settings`
    两个包上得到同样错误
- 结论：
  - 当前将其视为 `DN2101` 环境阻塞项，而不是 `Phase 5` 或应用运行时回归

## Search / Reader / WebView / Host
- Search 已形成正式日志样本基线。
- Reader 已形成正式 init 基线，并显式写出 flip / settings 样本缺口。
- Welfare / WebView / RN Host / Bridge 已形成聚合专项文档。

## 当前缺口
- Reader 的 flip 与 settings update 仍缺可信的直接数值样本。
- baseline profile generation 仍需在第二设备或可用 emulator 上复验。

## 结论
- 这套基线包足以关闭 `V6-01`。
- 同时也足以按既定规则关闭 `V6-02`：
  - 成功生成 profile，或
  - 形成可复现环境阻塞项，并附带补救路径
