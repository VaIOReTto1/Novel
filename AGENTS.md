# Novel Agent Guide

## Role Of This File
- This file is a directory, not an encyclopedia.
- Read the next linked document only when you need that extra context.
- Do not treat this file as the final source of truth for refactor status.

## First Read Order
1. [ARCHITECTURE.md](./ARCHITECTURE.md)
2. [docs/harness/README.md](./docs/harness/README.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)
4. If the task touches refactor stages, [docs/refactor/README.md](./docs/refactor/README.md)
5. If the task touches evidence, rollback, or closeout, follow the linked refactor docs from that control panel

## Source Of Truth Priority
1. Executable repo truth
   - `package.json`
   - `android/settings.gradle`
   - `android/app/build.gradle`
   - `.github/workflows/quality-gates.yml`
   - current source files and tests
2. Refactor authority
   - `docs/refactor/README.md`
   - linked validation boards
   - linked closeout docs
   - `docs/refactor/tracking/decision-log.md`
   - `docs/refactor/tracking/rollback-index.md`
3. Harness memory layer
   - `docs/harness/current-focus.md`
   - `docs/harness/session-log.md`
   - `docs/harness/generated/workspace-snapshot.md`
4. Secondary docs that may lag
   - root `README.md`
   - `.trae/rules/project_rules.md`

## Repo Shape
- React Native app code lives in `src/**`
- Android code lives in `android/app`, `android/core-*`, `android/feature-*`, and `android/macrobenchmark`
- Jest tests live in `__tests__/**`
- Refactor control plane lives in `docs/refactor/**`
- Harness control plane lives in `docs/harness/**`

## Task Routing
- Architecture or boundary questions:
  - [ARCHITECTURE.md](./ARCHITECTURE.md)
  - `android/settings.gradle`
  - current code
- "What is happening now?" questions:
  - [docs/harness/current-focus.md](./docs/harness/current-focus.md)
- Stage status, closeout, rollback, decision history:
  - [docs/refactor/README.md](./docs/refactor/README.md)
- Android module graph:
  - `android/settings.gradle`
  - `docs/refactor/phase-5/module-graph-current-state.md`
- RN bridge contracts:
  - `src/utils/bridge/**`
  - `android/app/src/main/java/com/novel/rn/**`
  - `__tests__/bridge/**`
- Verification command lookup:
  - `docs/harness/references/verification.md`
- Active execution plans:
  - `docs/harness/exec-plans/active/index.md`

## Update Rules
- If refactor stage status changes, update `docs/refactor/**` first.
- Then update `docs/harness/current-focus.md`.
- Then append `docs/harness/session-log.md`.
- Then regenerate `docs/harness/generated/workspace-snapshot.md`.
- After each atomic change, commit immediately with a Chinese commit message.
- If the same confusion appears twice, upgrade it into docs or a check script.
- Generated files under `docs/harness/generated/**` are script-owned.

## Generated Artifacts
- `docs/harness/generated/workspace-snapshot.md`
  - owner: `npm run harness:refresh`
  - purpose: machine-readable current state snapshot
  - rule: do not edit by hand

## Quick Commands
- `npm run harness:refresh`
- `npm run harness:check`
- `npm test -- --runInBand`
- `android/gradlew.bat app:testDebugUnitTest`
- `android/gradlew.bat app:lintDebug`
- `android/gradlew.bat app:compileDebugAndroidTestKotlin`
- `android/gradlew.bat :macrobenchmark:assemble`

## Current High-Level Reality
- This repo is a hybrid React Native + Android Compose app.
- The current refactor authority says `Phase 5 = in_progress`, `Stage 3 = in_progress`, and `Phase 6 = validated`.
- `app` is intended to stay thin; stable feature roots live in `feature-*`.
- The current default major line is still the `Phase 5` reopen deepening path, not `Phase 7`.

## Known Drift Traps
- Root `README.md` can lag behind code and refactor docs.
- `.trae/rules/project_rules.md` is a shim, not a fact source.
- Historical `docs/refactor` checkpoint files are not always current authority.
- Generated snapshot freshness depends on the current `HEAD`.

## Working Style
- Prefer executable truth over prose when they disagree.
- Prefer `docs/refactor/README.md` over phase drafts when status conflicts.
- Keep this file short; push durable detail into linked docs.
- Add checks for repeatable structure problems instead of re-reviewing them manually.
