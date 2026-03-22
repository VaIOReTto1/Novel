# Phase 7 - 包体积、依赖与构建效率治理

## 目标
- 在 `Stage 3 validated` 的模块图基础上，建立稳定的：
  - artifact / size baseline
  - Gradle 与 npm 依赖治理入口
  - 构建效率与产物差异治理入口
- 把包体积、依赖和构建速度从“临时优化”升级为长期门禁对象。

## 范围
- APK / AAB / native libs / fonts / assets 体积盘点
- Gradle / npm 依赖冗余、重复与升级治理
- build graph、task hot path、增量构建与 clean build 对比
- artifact diff、dependency diff、build-time baseline

## 非目标
- 不重开 `Phase 6` 的性能专项
- 不借机继续扩大 `Phase 5` 的模块化范围
- 不改 UI 语义或业务功能语义

## 进入条件
- `Phase 6 = validated`
- `Stage 3 = validated`
- `Phase 6` 中的已接受阻塞项与残余风险已留痕，不再混入 `Phase 7` 主线

## 不承接的 Phase 6 性能债
- 以下内容不属于 `Phase 7` 主线，应留在后续性能专项待办池：
  - Reader flip / settings 动作级治理与直接样本补齐
  - 搜索结果页 benchmark 化与分页 / 筛选热点治理
  - Welfare / WebView 更深层专项与重复上报治理
  - RN Host 生命周期、warm / cold path、Bridge 批量调用与线程切换进一步优化
  - 数据库索引收益、`FTS4` 与缓存清理收益复盘
- 统一参考：
  - `docs/refactor/phase-6/phase-6-optimization-opportunity-catalog.md`
  - `docs/refactor/phase-6/database-index-and-fts-governance-2026-03-22.md`
  - `docs/refactor/phase-6/cache-cleanup-governance-2026-03-22.md`

## 协作编制
### Leader Mode
- `single leader / three helpers`

### Base Helper Count
- `3`

### Scale-Up Triggers
- 当 size / dependency / build 三条线都具备独立取证脚手架时，可短期扩容一名 helper。

### Scale-Down Triggers
- 当只剩文档收尾或单线清债时，回到最小编制。

### Agent Roster
- `SizeBudgetAgent`
  - 包体积、资源盘点、artifact diff
- `DependencyGraphAgent`
  - Gradle / npm 依赖治理
- `BuildEfficiencyAgent`
  - build hot path、task baseline、增量构建效率

### Lock Strategy
- `LOCK-SIZE-BUDGET`
- `LOCK-DEPENDENCY-GRAPH`
- `LOCK-BUILD-EFFICIENCY`

### Retry Window
- baseline / diff / build profiling：`2` 次同环境重跑
- 若两次后仍无法得到稳定结果，必须登记残余风险

### Escalation Window
- 任一优化要求改变业务功能、UI 语义、route 或 bridge payload
- 任一优化要求把 `Phase 7` 升级成新的架构重构阶段

### Leader-only Actions
- 串行执行 Gradle / npm 验证
- 统一 artifact diff / dependency diff / build-time 口径
- 同步看板、README、决策日志与关闭总结

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P7.1 | 固定 size baseline 与 artifact diff 入口 | 当前 APK/AAB/资源构成可追溯 |
| P7.2 | 固定 Gradle / npm dependency baseline | 依赖冗余与治理顺序明确 |
| P7.3 | 执行第一轮 size shrink | 低风险体积收益落地 |
| P7.4 | 执行第一轮 build efficiency 治理 | clean / incremental baseline 成立 |
| P7.5 | 输出 Stage 4 后续门禁与关闭总结入口 | 下一阶段入口清晰 |

## 交付物
- size baseline 文档
- dependency inventory / diff 文档
- build efficiency baseline 文档
- Phase 7 关闭总结文档

## 当前状态
- `planned`
