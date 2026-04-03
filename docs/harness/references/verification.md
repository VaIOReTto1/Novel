# Verification Reference

## Core Commands

| Command | Use When | Notes |
| --- | --- | --- |
| `npm test -- --runInBand` | RN/Jest broad verification | Full RN regression sweep |
| `npm test -- --runInBand __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx` | Bridge and host touchpoints | Current low-cost targeted smoke path |
| `android/gradlew.bat app:testDebugUnitTest` | Android JVM validation | Fastest general Android unit gate |
| `android/gradlew.bat app:lintDebug` | Android lint regression checks | Shared CI gate |
| `android/gradlew.bat app:compileDebugAndroidTestKotlin` | Android test compile safety | Shared CI gate |
| `android/gradlew.bat :macrobenchmark:assemble` | Performance artifact readiness | Shared CI gate |
| `npm run stage7:closeout` | Generate Stage 7 closeout readiness report | Use while `Stage 7` is still not fully `validated` |
| `npm run stage7:closeout:check` | Verify Stage 7 closeout readiness report freshness | Run after closeout-summary / validation-board / figma-frame-map changes |
| `npm run stage7:signoff:check` | Verify Stage 7 signoff record is complete | Run after更新 `stage-7-signoff-record.md`，只有三方都完成且 `Closeout decision` 改成 `Stage 7 = validated` 才会通过 |
| `npm run stage7:finalize` | Apply final `validated` status to Stage 7 control-plane docs | Only run after `npm run stage7:signoff:check` passes; command also refreshes readiness report and workspace snapshot |
| `android/gradlew.bat :core-common:testDebugUnitTest :core-ui:testDebugUnitTest :core-bridge:testDebugUnitTest :core-bridge-contract:testDebugUnitTest :core-storage:testDebugUnitTest :core-network:testDebugUnitTest :feature-home:testDebugUnitTest :feature-search:testDebugUnitTest :feature-welfare:testDebugUnitTest :feature-rn-host:testDebugUnitTest :feature-book:testDebugUnitTest :feature-login:testDebugUnitTest :feature-reader:testDebugUnitTest :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | Reopen closeout reproduction | Non-incremental variant used for stable `2026-03-26` evidence |
| `android/gradlew.bat app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"` | Reopen closeout reproduction | Non-incremental CI-style gate |

## Usage Notes
- If the current task changes stage status, update `docs/refactor/**` first and then rerun the relevant verification.
- If the current task changes only harness docs/scripts, at minimum run:
  - `npm run harness:refresh`
  - `npm run harness:check`
  - `npm run stage7:closeout:check`
  - `git diff --exit-code -- docs/harness/generated/workspace-snapshot.md`
  - targeted harness Jest coverage
