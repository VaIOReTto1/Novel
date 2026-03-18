# Subagent Dispatch Log

## 目标
- 为 `Phase 4+` 的长期自主推进提供 append-only 派发记录。
- 确保每次 helper 派发都可追溯到：
  - 波次
  - 原子主题
  - 锁
  - 证据
  - 回滚入口

## 使用规则
- 仅 `LeaderAgent` 可写。
- 每次 helper 派发、重派发、升级派发都必须新增一条记录。
- 不允许修改历史记录，只允许追加。

## 字段定义
| Run ID | Date | Phase | Wave | Task ID | Atomic Theme | Agent | Model | Lock ID | Outcome | Evidence | Rollback ID | Escalated | Next Action |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## 当前记录
| Run ID | Date | Phase | Wave | Task ID | Atomic Theme | Agent | Model | Lock ID | Outcome | Evidence | Rollback ID | Escalated | Next Action |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
