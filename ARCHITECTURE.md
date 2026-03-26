# Novel Architecture Map

## Purpose
- This document is the stable project map for new sessions.
- It explains how the repo is split, where runtime ownership lives today, and which documents are authoritative.
- It intentionally stays higher level than `docs/refactor/**`.

## What This Repo Is
- `Novel` is a hybrid mobile app:
  - React Native handles a large set of business pages under `src/**`
  - Android Compose/Kotlin handles performance-sensitive native flows under `android/**`
- The repo currently reflects a multi-stage refactor that has already moved stable Android feature roots out of `app` and into `feature-*` modules.

## Truth Hierarchy
1. Code and build configuration
2. `docs/refactor/README.md` and its linked evidence
3. `docs/harness/**`
4. Secondary docs such as root `README.md` and local tool shims

## Top-Level Layout
- `src/`
  - React Native pages, stores, bridges, theme utilities, app init
- `android/`
  - Android application module, reusable core modules, feature modules, macrobenchmark module
- `__tests__/`
  - Jest-based RN and contract coverage
- `docs/refactor/`
  - stage plans, closeout, validation boards, decision log, rollback index
- `docs/harness/`
  - agent entrypoints, current focus, session log, generated snapshot, execution-plan index
- `.github/workflows/quality-gates.yml`
  - current shared CI entrypoint

## Android Module Map
- `:app`
  - Android forced entrypoints
  - route/page wrappers
  - RN native module adapters
  - host default implementations and Hilt bindings
- `:core-common`
  - common logging, MVI primitives, result/domain helpers, concurrency helpers
- `:core-ui`
  - shared theme and reusable Compose UI building blocks
- `:core-bridge`
  - bridge facade, state adapter, coroutine scopes, bridge network gateway
- `:core-bridge-contract`
  - bridge delegates and contract helpers
- `:core-storage`
  - shared storage abstractions, DataStore and keychain-compatible layers
- `:core-network`
  - shared network facade, executors, interceptors, adapters
- `:feature-home`
  - home root state and home feature coordination
- `:feature-search`
  - search and search-result roots plus search coordination
- `:feature-login`
  - login root state and login coordination
- `:feature-book`
  - book detail root state and related gateways
- `:feature-reader`
  - reader root state, reducer, adapters, coordinators, reader gateways
- `:feature-rn-host`
  - RN host page content, bridge/settings viewmodels, host contracts
- `:feature-welfare`
  - welfare state layer, page content, WebView-facing feature logic
- `:macrobenchmark`
  - Android performance benchmarking artifacts

## Thin-App Boundary
- `app` still owns:
  - `Application`
  - `Activity`
  - route wrappers
  - RN module adapter wiring
  - host default implementations
  - Hilt glue and legacy service bindings that have not been fully extracted
- `app` should no longer own:
  - stable feature root viewmodels for `home/search/login/book/reader`
  - `BridgeViewModel`
  - `SettingsViewModel`

## React Native Runtime Model
- Entry file: `index.js`
- App root: `App.tsx`
- Common initialization: `src/utils/appInit.ts`
- Native integration points:
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`
- RN page registration pattern:
  - root app uses `AppRegistry.registerComponent(appName, () => App)`
  - page-specific components register explicit names such as `SettingsPageComponent`, `BookshelfPageComponent`, `WritePageComponent`

## Bridge And Contracts
- JS bridge surface is exposed through `src/utils/bridge/**`.
- Android implementations and adapters live under `android/app/src/main/java/com/novel/rn/**`.
- The most stable contract safety net on the RN side is `__tests__/bridge/**`.
- Refactor work should preserve payload and route semantics unless the corresponding authority docs and tests change together.

## Current Test And CI Shape
- RN/Jest
  - `npm test -- --runInBand`
  - targeted contract and smoke tests under `__tests__/bridge/**` and `__tests__/smoke/**`
- Android shared CI gates
  - `app:testDebugUnitTest`
  - `app:lintDebug`
  - `app:compileDebugAndroidTestKotlin`
  - `:macrobenchmark:assemble`
- Current CI entrypoint
  - `.github/workflows/quality-gates.yml`

## Current Strategic Context
- The refactor control plane currently states:
  - `Phase 5 = in_progress`
  - `Phase 6 = validated`
  - `Stage 3 = in_progress`
  - current control-panel wording is driven by the `Phase 5` reopen state
- The next default line is still the `Phase 5` reopen deepening path; `Phase 7` remains planned rather than active.

## Where To Go Next
- For current priorities and drift:
  - [docs/harness/current-focus.md](./docs/harness/current-focus.md)
- For authoritative refactor state:
  - [docs/refactor/README.md](./docs/refactor/README.md)
- For command lookup:
  - [docs/harness/references/verification.md](./docs/harness/references/verification.md)
