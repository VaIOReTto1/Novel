# Phase 9 Energy And Background Governance

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 9`
- 当前结论：`已固定 Reader / Welfare / 后台任务的能耗与后台治理口径`

## 当前锚点
| Surface | Current Anchor | Current Fact |
| --- | --- | --- |
| Reader 持续阅读 | `ReadingBehaviorAnalyzer` | 已基于网络、电量、充电状态、可用存储空间做预取判断与优先级建议 |
| Reader 章节预取 | `ReadingBehaviorAnalyzer.generatePrefetchRecommendation()` | 低电量、无网络、低存储会直接禁用预取 |
| 低内存与后台治理 | `MemoryPressureManager` | 已监听 `onTrimMemory`、`onLowMemory`、`onConfigurationChanged`，并支持生命周期感知监控 |
| Welfare 活动页驻留 | Phase 6 Welfare / WebView evidence | 已有首开、复开、回退复用路径与恢复日志，但尚无独立耗电影响量化 |
| 后台协程 / 轮询 | `MemoryPressureManager` + 各 feature 现有 runtime 路径 | 当前已有局部治理入口，但未形成统一“后台任务目录” |

## 当前判定
- 当前仓库已经具备“按设备状态抑制 Reader 预取”的运行时入口，不是完全裸跑。
- 当前仓库尚未形成：
  - Reader 持续阅读耗电样本矩阵
  - Welfare 驻留耗电样本矩阵
  - 全局后台协程 / 轮询目录
- 因此 Phase 9 在本项关闭的是“治理口径与现有锚点”，不是“能耗专项已经量化完成”。

## 允许降级
- 低电量时直接关闭预取
- 无网络时不做后台拉取
- 低存储时停止额外缓存写入

## 禁止伪恢复
- 不能在低电量或无网络下仍强制预取，然后把失败伪装成正常路径
- 不能用“后台一直轮询直到成功”来掩盖连续性问题

## 后续深化入口
- 若后续要量化 Reader / Welfare / 后台任务对电量和热量的真实收益，应在本治理口径上追加样本矩阵，而不是重写规则。
