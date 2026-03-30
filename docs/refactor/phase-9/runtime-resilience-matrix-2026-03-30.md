# Runtime Resilience Matrix

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 9`
- 当前结论：`已固定 Phase 9 的宿主矩阵骨架`

## 场景矩阵
| Domain | Scenario | Current Anchor | Current Fact | Planned Validation |
| --- | --- | --- | --- | --- |
| Reader | 配置变化恢复 | `rememberPageCurlState` | 翻页状态已通过 `rememberSaveable` 保持 | JVM / device sample |
| Reader | 历史与进度恢复 | `ProgressService`, `ReadingProgressRepository` | 已存在阅读进度恢复入口，但未形成 Stage 5 宿主矩阵 | JVM / device sample |
| Welfare / WebView | WebView 状态恢复 | Phase 6 WebView evidence | 已有“savedState / restore”日志样本 | device evidence |
| RN Host | RN context 丢失恢复 | `feature-rn-host` host runtime / existing host evidence | 当前有 host runtime 证据，但无统一恢复契约宿主 | smoke / device sample |
| Login / User | 设置导入导出 | `ExportUserDataUseCase`, `ImportUserDataUseCase` | 功能入口已存在 | JVM validation + manual path |
| Login / User | Token 读取与连续性 | `TokenProvider`, login/home usecases | Token 读取入口已存在，刷新治理未单独宿主化 | JVM / weak-network matrix |
| Shared Runtime | 低内存与配置变化 | `MemoryPressureManager` | 已监听 `onTrimMemory`, `onLowMemory`, `onConfigurationChanged` | JVM / device sample |
| Data / Cache | 弱网 / 离线 fallback | `NetworkCacheManager`, `CachedBookRepository`, `HomeRepository` | 离线 / fallback 路径已分散存在 | weak-network / offline matrix |

## 判定规则
- 允许降级：有明确空态、错误态、缓存态或重新初始化路径。
- 禁止伪恢复：用 mock、静默重置或隐藏错误来伪装恢复成功。
- 若当前只存在局部入口但无统一宿主，Phase 9 视其为“有 anchor、未关账”。

## 当前结论
- 当前矩阵已经足够支撑 `V9-01`，因为主要运行恢复域和锚点已经集中到单一宿主中。
