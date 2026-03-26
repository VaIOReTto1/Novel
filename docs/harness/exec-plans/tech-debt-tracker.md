# Harness Tech Debt Tracker

## Current Tracked Debt

| Item | Status | Why It Matters | Next Move |
| --- | --- | --- | --- |
| `Phase 7` 入口口径漂移 | open | `Phase 7` 文档仍保留旧表述，影响新会话判断下一步 | 统一 `README`、`Stage 3` summary 与 `Phase 7` phase doc |
| 根 `README.md` 与代码版本漂移 | open | 技术版本和成熟度描述晚于当前代码事实 | 后续单独做 README 事实追平 |
| `.trae` 旧规则漂移 | open | 旧本地规则曾混入愿景和过时约束 | 保持 shim 化，必要时继续瘦身 |

## V2 Runtime Legibility Backlog
- per-worktree app boot orchestration
- CDP or Playwright-driven app/runtime inspection
- dedicated observability and doc-gardening flows
- automated drift PRs for docs and generated artifacts
