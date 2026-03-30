# Phase 8 ADR Reviewer Owner Governance

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 8`
- 当前结论：`已固定当前仓库的 ADR / reviewer / owner 治理入口`

## 当前治理入口
### Owner
- `docs/refactor/phase-5/module-owner-matrix-2026-03-27.md`
- 负责说明：
  - 模块 owner 面
  - 主职责
  - 重点评审项
  - 最小验证

### API Surface / Reviewer
- `docs/refactor/phase-5/api-surface-review-checklist.md`
- 负责说明：
  - 哪些变更必须升级评审
  - 哪些变更属于硬停止项
  - 最小交付要求

### ADR / 决策留痕
- `docs/refactor/tracking/decision-log.md`
- 负责说明：
  - 为什么做
  - 影响什么
  - 后续动作是什么

### 验证状态
- `docs/refactor/tracking/*validation-board.md`
- 负责说明：
  - 当前项是否 green
  - 关闭依据是什么

### 回退与执行
- `docs/refactor/tracking/rollback-index.md`
- `docs/refactor/tracking/atomic-commit-guide.md`

## 当前规则
1. 变更跨 `app / core-* / feature-*` 边界时，先查 owner matrix。
2. 变更 route / bridge payload / componentName / schema 时，先查 API surface checklist。
3. 如果一个判断会影响控制面口径，必须写入 decision log。
4. 如果一个主题不能给出 rollback 命令，就不能进入提交阶段。

## reviewer 升级条件
- `:app` 重新变胖
- bridge / route / payload 漂移
- network 主通路语义变化
- host runtime / root view cache / theme sync 语义变化
- Reader 再次越过“轻触式优化”边界

## 当前结论
- 当前仓库已经具备 owner / reviewer / ADR / rollback 四件套入口。
- 当前仍缺的是平台自动化，不是治理宿主文档本身。
- 因此 Phase 8 的关闭标准应该是“制度落盘并可执行”，而不是“必须先有 CODEOWNERS 平台自动分发”。

## 后续维护规则
- 同一类评审意见重复两次以上，升级成 checklist 或检查脚本。
- 任何新的跨模块公开接口，都必须同时补 owner 面和最小验证。
- 若后续引入平台级自动 reviewer / CODEOWNERS，更新本文件而不是新开第二套规则。

## 主要引用
- `docs/refactor/phase-5/module-owner-matrix-2026-03-27.md`
- `docs/refactor/phase-5/api-surface-review-checklist.md`
- `docs/refactor/tracking/decision-log.md`
- `docs/refactor/tracking/rollback-index.md`
- `docs/refactor/tracking/atomic-commit-guide.md`
