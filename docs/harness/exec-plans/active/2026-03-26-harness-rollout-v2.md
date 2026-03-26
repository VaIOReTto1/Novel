# Harness Rollout V2

## Goal
- Build a repo-local control layer so a new session can understand the project, current refactor state, and next default workstream in minutes.

## Workstreams
- Root control plane
  - `AGENTS.md`
  - `ARCHITECTURE.md`
  - `.trae/rules/project_rules.md` shim
- Harness knowledge store
  - `docs/harness/current-focus.md`
  - `docs/harness/session-log.md`
  - `docs/harness/references/verification.md`
  - `docs/harness/exec-plans/**`
  - `docs/harness/quality-score.md`
- Generated artifacts and enforcement
  - `scripts/harness-refresh.js`
  - `scripts/harness-check.js`
  - `docs/harness/generated/workspace-snapshot.md`
  - CI `harness-docs` gate

## Done Criteria
- `npm run harness:refresh` regenerates the snapshot from repo facts
- `npm run harness:check` validates required structure and links
- `AGENTS.md`, `ARCHITECTURE.md`, and `current-focus.md` alone are enough for a fast human or agent handoff
