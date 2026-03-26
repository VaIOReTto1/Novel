# Harness Knowledge Index

## Purpose
- This directory is the in-repo control layer for new sessions.
- It does not replace `docs/refactor/**`.
- It provides entrypoints, current-focus summaries, historical handoff notes, and generated snapshots.

## Progressive Disclosure
1. Start at [AGENTS.md](../../AGENTS.md)
2. Read [ARCHITECTURE.md](../../ARCHITECTURE.md) for the stable repo map
3. Read [current-focus.md](./current-focus.md) for what matters now
4. If the task touches refactor status, jump to [docs/refactor/README.md](../refactor/README.md)
5. If the task needs historical handoff or active plans, use the files below

## File Map
- [core-beliefs.md](./core-beliefs.md)
  - durable rules for how this harness layer is maintained
- [current-focus.md](./current-focus.md)
  - current branch, current authority, default next line, known drift
- [session-log.md](./session-log.md)
  - append-only handoff journal for major turns in repo state
- [references/verification.md](./references/verification.md)
  - common verification commands and when to use them
- [exec-plans/active/index.md](./exec-plans/active/index.md)
  - active execution plans and current main lines
- [exec-plans/completed/index.md](./exec-plans/completed/index.md)
  - completed harness-specific execution plans
- [exec-plans/tech-debt-tracker.md](./exec-plans/tech-debt-tracker.md)
  - known documentation/control-plane debt and deferred v2 work
- [quality-score.md](./quality-score.md)
  - current harness legibility scorecard
- [generated/workspace-snapshot.md](./generated/workspace-snapshot.md)
  - generated snapshot owned by `npm run harness:refresh`

## Ownership Boundaries
- Refactor stage truth stays in [docs/refactor/README.md](../refactor/README.md) and linked evidence.
- Harness docs summarize and route, but should not duplicate closeout detail.
- Generated files under `generated/` are script-owned.

## When To Update This Directory
- After any refactor status change
- After any meaningful shift in current default workstream
- After adding or removing Android modules, bridge entrypoints, or key CI commands
- When recurring confusion should become durable docs or a check script
