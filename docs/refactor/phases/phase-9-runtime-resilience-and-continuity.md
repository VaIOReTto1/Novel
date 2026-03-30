# Phase 9 - 运行可靠性与业务连续性

## 目标
- 把蓝图中的“电量与热量、稳定性、业务连续性”从愿景清单升级为统一场景矩阵与恢复契约。
- 明确哪些路径已经有恢复入口，哪些仍只是 backlog。

## 范围
- Reader 持续阅读、章节预取、电量与后台任务治理
- 进程重建、配置变化、内存回收恢复、RN context 丢失恢复
- 设置导入导出、历史恢复、Token 过期/刷新、弱网 / 离线体验

## 非目标
- 不引入统一线上监控平台
- 不重开 `Phase 6` 的性能专项
- 不改既有 route / Bridge payload / RN `componentName`

## 当前仓库入口基线
- 设置导入导出已存在：
  - `ExportUserDataUseCase`
  - `ImportUserDataUseCase`
- 配置变化与状态恢复已有局部入口：
  - `rememberPageCurlState`
  - `MemoryPressureManager`
  - `HomePage` 生命周期恢复检查
  - Welfare WebView 恢复日志样本
- 弱网 / 离线与 fallback 已有散落入口：
  - `NetworkCacheManager`
  - `CachedBookRepository`
  - `HomeRepository`
  - `UserRepository`
- Token 读取与用户态连续性已有入口，但“过期/刷新策略”未形成阶段宿主

## 协作编制
### Leader Mode
- `single leader / four helpers`

### Base Helper Count
- `4`

### Agent Roster
- `RuntimeResilienceAgent`
- `LifecycleRecoveryAgent`
- `WeakNetworkContinuityAgent`
- `ContinuityEvidenceAgent`

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P9.1 | 固定运行恢复场景矩阵 | Reader / Welfare / RN Host / 登录与用户态恢复路径可追溯 |
| P9.2 | 固定电量与后台任务治理口径 | 持续阅读、预取、后台轮询的风险边界明确 |
| P9.3 | 固定弱网 / 离线 / Token 连续性口径 | 可降级、禁止伪恢复、恢复失败语义明确 |
| P9.4 | 固定导入导出 / 历史恢复入口 | 用户连续性入口与最小验证清晰 |
| P9.5 | 输出 Phase 9 closeout 宿主 | 下一阶段不再重复定义恢复契约 |

## 交付物
- `runtime-resilience-matrix-2026-03-30.md`
- `energy-and-background-governance-2026-03-30.md`
- `weak-network-offline-token-continuity-2026-03-30.md`
- `import-export-history-recovery-2026-03-30.md`
- Phase 9 closeout 评估

## 当前状态
- `validated（Phase 9 closeout 生效于 2026-03-30）`
