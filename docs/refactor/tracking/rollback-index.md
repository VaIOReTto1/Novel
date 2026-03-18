# Rollback Index

## 目标
- 固定 `Phase 4+` 每个原子主题的一键回滚入口。
- 避免后续出现“改动可解释但不可快速回退”的情况。

## 使用规则
- 仅 `LeaderAgent` 可写。
- 每个原子主题在进入提交阶段前，必须先分配 `Rollback ID`。
- 若无法给出 `One-Click Command`，该主题不得进入提交阶段。

## 字段定义
| Rollback ID | Phase | Wave | Atomic Theme | Owner Agent | Commit SHA | One-Click Command | Precheck | Postcheck | Status | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## 当前记录
| Rollback ID | Phase | Wave | Atomic Theme | Owner Agent | Commit SHA | One-Click Command | Precheck | Postcheck | Status | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
