# Startup Compilation Probe Blocker - 2026-03-28

## 状态
- 日期：`2026-03-28`
- 关联阶段：`Phase 6 / Startup compiled-mode probe`
- 当前结论：`compiled-mode benchmark 仍被环境阻塞`

## 背景
- 当前默认 Startup benchmark 已稳定在 `CompilationMode.None()` 路径。
- 为了判断 compiled-mode blocker 是否仍然存在，本轮再次执行了：
  - `StartupCompilationProbeBenchmark`

## 本轮新事实
- 这次不再卡在仓库内的重复类打包问题。
- `SearchRankingItem` 的重复定义已修复，release APK 能正常产出。
- 当前实际阻塞点变成：
  - `connectedBenchmarkAndroidTest` 在把 `app-release.apk` 安装到 `DN2101` 时，
  - 设备切成 `offline`
  - UTP / ddmlib 提前结束测试

## 阻塞定位
| 层面 | 当前判断 |
| --- | --- |
| 仓库 release 打包 | `已通过到 release APK 产物阶段` |
| benchmark 安装阶段 | `被无线 adb / device offline 阻塞` |
| compiled-mode benchmark 本身 | `尚未真正开始执行` |

## 当前意义
- 这条 blocker 现在应表述为：
  - `compiled-mode benchmark install path unstable on DN2101 over wireless adb`
- 不应再继续沿用“重复类打包失败”或更早的模糊口径。

## 证据
- `docs/refactor/evidence/startup-compilation-probe-blocker-2026-03-28.txt`

## 后续动作
- 若继续深挖 compiled-mode：
  1. 优先换更稳定的设备连接方式或第二设备
  2. 再重试 `StartupCompilationProbeBenchmark`
  3. 成功后才讨论 compiled-mode 数据本身
