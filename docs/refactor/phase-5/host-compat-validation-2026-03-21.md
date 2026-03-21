# Phase 5 Host Compatibility Validation - 2026-03-21

## Summary
- Goal: validate that the current Phase 5 module graph still preserves Bridge / RN Host compatibility.
- Scope:
  - `profile`
  - `settings`
  - `aipage`
  - Bridge contract regression
  - RN settings smoke
- Result: `pass`

## What Changed In Phase 5
- Phase 5 feature/module cuts landed so far did **not** move or rename:
  - `android/app/src/main/java/com/novel/rn/ReactNativePage.kt`
  - `android/app/src/main/java/com/novel/rn/NavigationPackage.kt`
  - `android/app/src/main/java/com/novel/MainApplication.kt`
  - `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt`
- Current modularization only moved low-risk helpers/contracts/stores into:
  - `core-bridge-contract`
  - `feature-welfare`
  - `feature-search`
  - `feature-home`
  - `feature-rn-host`

## Device Spot Checks
- Device: `192.168.8.130:5555 / DN2101 / Android 13`
- Build: `debug`
- Install command:
  - `android/gradlew.bat app:installDebug`
- Route commands:
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route profile`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route settings`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route aipage`

### Actual
- `profile` 二次 dump 已回到 `package="com.novel"` 容器层，包含 `ComposeView -> ViewFactoryHolder -> FrameLayout` 宿主页骨架。
- `settings` 二次 dump 已回到 `package="com.novel"` 容器层，包含同样的 RN Host 容器骨架。
- `aipage` dump 也回到 `package="com.novel"` 容器层。
- 这轮设备证据强度低于 Phase 4：
  - 当前没有稳定抓到页面级文本元素
  - 但已确认模块化后 `debug_route` 仍能把页面拉起到 `com.novel` 宿主容器，而不是系统锁屏或崩溃退出

## Automated Compatibility Evidence
- Jest / contract:
  - `npm test -- --runInBand --runTestsByPath __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`
  - Result: `pass`
- Core bridge module:
  - `android/gradlew.bat :core-bridge-contract:testDebugUnitTest`
  - Result: `pass`
- Full app JVM regression:
  - `android/gradlew.bat :app:testDebugUnitTest`
  - Result: `pass`

## Evidence Files
- Historical strong baseline reused from Phase 4:
  - `docs/refactor/phase-4/host-risk-run-profile-2026-03-20.md`
  - `docs/refactor/phase-4/host-risk-run-settings-2026-03-20.md`
  - `docs/refactor/phase-4/host-risk-run-author-ai-2026-03-20.md`
- Phase 5 device spot checks:
  - `docs/refactor/evidence/profile-host-phase5-2026-03-21.xml`
  - `docs/refactor/evidence/settings-host-phase5-2026-03-21.xml`
  - `docs/refactor/evidence/aipage-host-phase5-2026-03-21.xml`

## Conclusion
- Inference from evidence:
  - Current Phase 5 modularization slices preserved host root wiring and did not alter route / payload / RN component semantics.
  - Post-modularization device spot checks still reach the `com.novel` host container for `profile / settings / aipage`.
  - Bridge contract and RN settings smoke regressions remain green.
- Therefore `V5-04` can enter `validated / green`.
