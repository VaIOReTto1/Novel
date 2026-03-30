[简体中文](./README.md) | [English](./README.en.md)

# Novel

`Novel` is a hybrid `React Native + Android Compose/Kotlin` novel-reading app repository.

This README is now aligned to current repository truth. For the authoritative refactor/control-plane state, use `docs/refactor/**` and current source/config files rather than older project marketing copy.

## Current Status

- Current package version: `1.0.2`
- Current refactor control-plane: `Stage 5 = validated`
- Current Android module graph: `app + core-* + feature-* + macrobenchmark`
- Current repository shape:
  - `src/**` contains the main React Native pages, stores, bridges, theme utilities, and app init
  - `android/**` contains Android Compose pages, bridge/runtime code, modularized core/feature code, and benchmarks
  - `__tests__/**` contains Jest contract and smoke coverage

Authoritative entry points:

- [Refactor control panel](./docs/refactor/README.md)
- [Stage 4 closeout summary](./docs/refactor/stage-4-closeout-summary.md)
- [Stage 5 closeout summary](./docs/refactor/stage-5-closeout-summary.md)
- [Current focus](./docs/harness/current-focus.md)
- [Architecture map](./ARCHITECTURE.md)

## Current Tech Stack

### React Native / Web

- `react-native 0.79.2`
- `react 19.0.0`
- `react-dom 19.0.0`
- `typescript 5.0.4`
- `zustand 5.0.5`
- `react-navigation 7`
- `react-native-reanimated 3`
- `react-native-svg`
- `react-native-vector-icons`

### Android

- Kotlin toolchain centered on `2.0.21`
- Jetpack Compose
- Hilt
- Room
- Paging3
- DataStore
- OkHttp + Retrofit
- Macrobenchmark / Baseline Profile

### Current Engineering Traits

- Android has been split into stable `core-*` and `feature-*` modules
- RN and Native still cooperate through the current bridge / host runtime
- The repo already has:
  - `RefactorFeatureFlags`
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
  - `verification-metadata.xml`
- Known current gaps:
  - missing `android/gradle/libs.versions.toml`
  - `org.gradle.configuration-cache=false`
  - no unified Crash / ANR / rollout platform

## Repository Layout

### React Native

- `App.tsx`: RN app root
- `index.js`: RN entry and page registration
- `src/page/**`: RN pages and page-level state
- `src/utils/bridge/**`: JS bridge wrappers
- `src/utils/theme/**`: theme and style state

### Android

- `android/app`: Application, Activity, route wrappers, RN module adapters, host defaults
- `android/core-common`
- `android/core-ui`
- `android/core-bridge`
- `android/core-bridge-contract`
- `android/core-storage`
- `android/core-network`
- `android/feature-home`
- `android/feature-book`
- `android/feature-login`
- `android/feature-search`
- `android/feature-reader`
- `android/feature-rn-host`
- `android/feature-welfare`
- `android/macrobenchmark`

## Quick Start

### Requirements

- Node.js `>= 18`
- Java `>= 17`
- Android Studio / Android SDK

### Install Dependencies

```bash
npm install
# or
yarn install
```

### Common Commands

```bash
# Start Metro
npm run start

# Run Android
npm run android

# Jest
npm test -- --runInBand

# ESLint
npm run lint

# Harness docs check
npm run harness:check

# Refresh harness snapshot
npm run harness:refresh
```

### Common Android Verification

```bash
cd android

./gradlew.bat app:testDebugUnitTest
./gradlew.bat app:lintDebug
./gradlew.bat app:compileDebugAndroidTestKotlin
./gradlew.bat :macrobenchmark:assemble
```

### Release Artifacts

```bash
npm run build:android
npm run build:android:bundle
```

## Current Architecture Notes

- Stable Android feature roots now live in `feature-home/search/login/book/reader/rn-host/welfare`
- `app` remains a thin composition root instead of owning stable feature root state
- RN page registration still goes through `AppRegistry.registerComponent(...)`
- Stable JS bridge entry points are:
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`

For current phase/governance status, do not rely on old roadmap text in earlier README revisions. Use:

- [docs/refactor/README.md](./docs/refactor/README.md)
- [docs/refactor/tracking/phase-7-8-validation-board.md](./docs/refactor/tracking/phase-7-8-validation-board.md)
- [docs/refactor/tracking/phase-9-11-validation-board.md](./docs/refactor/tracking/phase-9-11-validation-board.md)

## Verification And Governance Entry Points

- [Verification reference](./docs/harness/references/verification.md)
- [Master roadmap](./docs/refactor/master-roadmap.md)
- [Stage 5 plan](./docs/refactor/stage-5-phase-9-11-plan.md)
- [Module owner matrix](./docs/refactor/phase-5/module-owner-matrix-2026-03-27.md)
- [API surface checklist](./docs/refactor/phase-5/api-surface-review-checklist.md)
- [Rollback index](./docs/refactor/tracking/rollback-index.md)

## Documentation Notes

- Older README sections that advertised broad architecture phases and capability claims no longer fully match the current repository.
- The current external source of truth is:
  - [docs/refactor/README.md](./docs/refactor/README.md)
  - [docs/refactor/stage-4-closeout-summary.md](./docs/refactor/stage-4-closeout-summary.md)
  - [docs/refactor/stage-5-closeout-summary.md](./docs/refactor/stage-5-closeout-summary.md)
- Historical release storytelling remains in [CHANGELOG.en.md](./CHANGELOG.en.md).

## Contributing

If you want to continue feature or refactor work in this repository, start with:

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)
4. [docs/refactor/README.md](./docs/refactor/README.md)

Repository rules still include:

- atomic commits
- Chinese commit messages
- update `docs/refactor/**` first, then `docs/harness/**`

## License

This project is licensed under the [MIT License](./LICENSE).
