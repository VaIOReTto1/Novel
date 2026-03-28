# Harness Tech Debt Tracker

## Current Tracked Debt

| Item | Status | Why It Matters | Next Move |
| --- | --- | --- | --- |
| 根 `README.md` 与代码版本漂移 | open | 技术版本和成熟度描述晚于当前代码事实 | 后续单独做 README 事实追平 |
| `.trae` 旧规则漂移 | open | 旧本地规则曾混入愿景和过时约束 | 保持 shim 化，必要时继续瘦身 |

## V2 Runtime Legibility Backlog
- per-worktree app boot orchestration
- CDP or Playwright-driven app/runtime inspection
- dedicated observability and doc-gardening flows
- automated drift PRs for docs and generated artifacts
