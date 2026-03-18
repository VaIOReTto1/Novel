# Phase-aware GPT-5.4 Subagent 协作策略

## 摘要
- 适用范围：`docs/refactor` 体系下的所有后续阶段文档、执行计划、验证看板和关闭评审。
- 目标：把“每个 phase 的 helper 数量和分工”从临时决定，升级为固定规则 + phase 内再确认。
- 模型约束：所有 helper 与 leader 默认模型固定为 `GPT-5.4`。
- 主原则：
  - 先按 phase 规划编制，再写任务拆解。
  - 写的是 `基线人数 + 扩缩规则`，不是写死人数。
  - `docs/refactor/**` 的 tracking 主文档一律由 `LeaderAgent` 写。

## 全局角色模型
### LeaderAgent
- 唯一职责：
  - 分解任务
  - 分配锁
  - 合并 helper 输出
  - 更新 `README`、阶段文档、验证看板、决策日志
  - 执行 Git commit / revert
  - 维护回滚索引
- 禁止下放：
  - 阶段状态切换
  - tracking 文档主线写入
  - 最终签字评审结论

### Helper Agent 通用职责
- 只负责单锁范围内的原子主题。
- 必须返回固定输出包。
- 不允许直接写入：
  - `docs/refactor/README.md`
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/tracking/*validation-board.md`
  - 任意阶段状态字段

## 固定输入输出契约
### Leader -> Helper 输入包
- `Task ID`
- `Phase / Validation ID`
- `Atomic Theme`
- `Goal`
- `In-Scope Paths`
- `Out-of-Scope Paths`
- `Preconditions`
- `Acceptance Criteria`
- `Required Tests`
- `Evidence Required`
- `Rollback Type`

### Helper -> Leader 输出包
- `Task ID`
- `Agent`
- `Model`
- `Result`
- `Changed Paths`
- `Tests Run`
- `Evidence`
- `Residual Risks`
- `Rollback Command`
- `Suggested Commit Title`
- `Need Escalation`
- `Next Recommended Action`

## 长期运行工件
- `docs/refactor/phase-4/phase-4-wave-tracker.md`
  - 记录当前波次、波次摘要、下一原子主题
- `docs/refactor/tracking/subagent-dispatch-log.md`
  - 记录每次 helper 派发、重派发、升级派发
- `docs/refactor/tracking/rollback-index.md`
  - 记录每个原子主题的 `Rollback ID` 与一键回滚命令

### 工件写入规则
- `LeaderAgent` 负责写入上述三类工件。
- 每轮自主推进至少要完成一次：
  - 波次状态更新
  - 派发记录追加
  - 回滚索引补记或确认

## 每个新 Phase 文档必须包含的协作编制字段
- `Leader Mode`
- `Base Helper Count`
- `Scale-Up Triggers`
- `Scale-Down Triggers`
- `Agent Roster`
- `Lock Strategy`
- `Retry Window`
- `Escalation Window`
- `Leader-only Actions`

## Phase Staffing 总表
| Phase | Base Helper Count | 默认角色类型 | 典型扩容条件 |
| --- | --- | --- | --- |
| Phase 0 | `2` | 盘点/基线、设备/证据 | Android/RN 盘点与动态基线取证并行 |
| Phase 1 | `3` | 发布构建、安全合规、迁移验证 | 签名、WebView、迁移演练与 benchmark 并行 |
| Phase 2 | `4` | JVM/fixture、Bridge contract、smoke/CI、静态债 | RN smoke 与 Android smoke 分离，CI 与静态债并行 |
| Phase 3 | `4` | Core infra、Bridge boundary、Storage/error、Validation | 作为历史样本保留，不回填旧阶段文档 |
| Phase 4 | `4` | BridgeFacade、Feature split、Reader/cache light、Host/quality | `NavigationBridgeModule` 与 `HomeViewModel` 可独立拆分 |
| Phase 5 | `5` | Module graph、core module、feature A、feature B、build integration | 模块拆分与构建集成互不抢锁 |
| Phase 6 | `3` | Startup perf、Reader/scroll perf、benchmark/observability | WebView 与 DB 性能专项独立成线 |
| Phase 7 | `3` | Size shrink、dependency/build、artifact diff | npm 与 Gradle 依赖治理拆成两条线 |
| Phase 8+ | `3` | Observability、governance/ADR、rollout/flag | Crash/ANR 与治理建设同时推进 |

## Phase 4 当前默认编制
### Leader Mode
- `single leader / multiple helpers`

### Base Helper Count
- `4`

### Agent Roster
- `BridgeFacadeSplitAgent`
- `FeatureBoundarySplitAgent`
- `CacheReaderLightAgent`
- `HostRiskQualityAgent`

### Scale-Up Triggers
- `NavigationBridgeModule` 与 `HomeViewModel` 可独立拆分且互不抢锁。
- `RN Host / profile-host` 验证可单独执行，不与业务拆分类变更共享写锁。

### Scale-Down Triggers
- 当前只做文档、看板、closeout 或验证归档。
- 仅推进单一超大类拆分，不涉及 host 风险验证。

## 锁策略
### 全局锁
- `LOCK-REFRACTOR-DOCS`
  - 范围：`docs/refactor/**`
  - 仅 `LeaderAgent` 可写
- `LOCK-CORE-INFRA`
  - 范围：`android/app/src/main/java/com/novel/core/**`, `android/app/src/main/java/com/novel/di/**`
- `LOCK-BRIDGE-RN`
  - 范围：`android/app/src/main/java/com/novel/rn/**`
- `LOCK-FEATURE-SPLIT`
  - 范围：`android/app/src/main/java/com/novel/page/home/**`, `page/search/**`, `page/read/**`, `android/app/src/main/java/com/novel/utils/network/cache/**`
- `LOCK-QUALITY`
  - 范围：测试、CI、证据模板

### Phase 4 固定锁
- `LOCK-BRIDGE-FACADE`
- `LOCK-HOME-SEARCH-SPLIT`
- `LOCK-CACHE-READER-LIGHT`
- `LOCK-HOST-QUALITY`

### 锁规则
- helper 一次只能持有 `1` 把写锁。
- 任何需要 2 把以上写锁的任务必须先由 `LeaderAgent` 拆成串行原子任务。
- 锁冲突时，优先级按：
  - `LOCK-REFRACTOR-DOCS`
  - `LOCK-BRIDGE-*`
  - `LOCK-CORE-INFRA`
  - `LOCK-FEATURE-*`
  - `LOCK-QUALITY`

## 重试与升级窗口
### Retry Window
- `0-15 min`
  - helper 自检、自修一次
- `15-30 min`
  - Leader 收窄范围后二次派发

### Escalation Window
- `30-45 min`
  - 若仍未收敛，进入 `hard escalation`
- 立即升级条件：
  - UI 语义变化
  - route / Bridge event / payload 语义变化
  - Reader 核心翻页或分页行为风险
  - 无法给出 rollback command
  - touched files 新增 lint/detekt 红项

## Leader-only Actions
- 更新阶段状态
- 更新验证看板
- 更新决策日志
- 写 `README`
- 生成与维护 rollback index
- 执行 Git commit / revert
- 写签字评审结论

## 回滚与追溯
- 每个原子主题必须生成：
  - `Rollback ID`
  - `Commit SHA`
  - `One-Click Command`
  - `Precheck`
  - `Postcheck`
- helper 必须在输出包中提供 `Rollback Command`。
- Leader 负责把最终版本写入 `docs/refactor/tracking/rollback-index.md`。

## Phase 4 长期运行默认循环
1. 读取 `docs/refactor/phase-4/phase-4-wave-tracker.md`
2. 选择当前 wave 的单主题原子任务
3. 确认 `Lock ID`
4. 派发 helper 并追加 `subagent-dispatch-log`
5. 汇总证据、补 `Rollback ID`
6. 判断继续同 wave、切换 wave，还是升级

## 未来证据脱敏规则
- 历史文档中的设备 IP、调试环境标识可保留为历史记录。
- 从本策略生效起，后续新增文档禁止写入：
  - 设备 IP
  - 机器名
  - 个人目录
  - 真实 token
  - 真实账号
- 若必须记录设备信息，只允许使用分级标签，例如：
  - `low-end-device-01`
  - `mid-tier-device-01`
  - `high-end-device-01`
