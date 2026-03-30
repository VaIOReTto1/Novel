[简体中文](./README.md) | [English](./README.en.md)

# Novel

`Novel` 是一个 `React Native + Android Compose/Kotlin` 的混合架构小说阅读应用仓库。

当前仓库的技术与重构状态以代码和 `docs/refactor/**` 为准；本 README 只提供当前工程事实、运行方式和导航入口，不再维持过时的阶段宣传页口径。

## 当前状态

- 当前 package version：`1.0.2`
- 当前 refactor control-plane：`Stage 5 = validated`
- 当前 Android 模块图：`app + core-* + feature-* + macrobenchmark`
- 当前仓库形态：
  - `src/**` 承载主要 React Native 业务页面、store、bridge、theme 与 app init
  - `android/**` 承载 Android Compose 页面、bridge/runtime、core/feature 模块与 benchmark
  - `__tests__/**` 承载 Jest 合同与 smoke 覆盖

当前权威入口：

- [重构控制面板](./docs/refactor/README.md)
- [Stage 4 关闭总结](./docs/refactor/stage-4-closeout-summary.md)
- [Stage 5 关闭总结](./docs/refactor/stage-5-closeout-summary.md)
- [当前焦点](./docs/harness/current-focus.md)
- [稳定架构地图](./ARCHITECTURE.md)

## 当前技术栈

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

- Kotlin toolchain 以 `2.0.21` 为主
- Jetpack Compose
- Hilt
- Room
- Paging3
- DataStore
- OkHttp + Retrofit
- Macrobenchmark / Baseline Profile

### 当前工程特征

- Android 侧已经拆分为稳定的 `core-*` / `feature-*` 模块
- RN 与 Native 仍通过现有 bridge / host 机制协作
- 已有本地：
  - `RefactorFeatureFlags`
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
  - `verification-metadata.xml`
- 当前仍是已知缺口：
  - `android/gradle/libs.versions.toml` 缺失
  - `org.gradle.configuration-cache=false`
  - 无统一 Crash / ANR / 灰度平台

## 仓库布局

### React Native

- `App.tsx`：RN app root
- `index.js`：RN 入口与页面注册
- `src/page/**`：RN 页面与页面级状态
- `src/utils/bridge/**`：JS bridge 封装
- `src/utils/theme/**`：主题与样式状态

### Android

- `android/app`：Application、Activity、route wrapper、RN module adapter、host 默认实现
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

## 快速开始

### 环境要求

- Node.js `>= 18`
- Java `>= 17`
- Android Studio / Android SDK

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 常用命令

```bash
# 启动 Metro
npm run start

# 运行 Android
npm run android

# Jest
npm test -- --runInBand

# ESLint
npm run lint

# Harness 文档检查
npm run harness:check

# 刷新 harness snapshot
npm run harness:refresh
```

### Android 常用验证

```bash
cd android

./gradlew.bat app:testDebugUnitTest
./gradlew.bat app:lintDebug
./gradlew.bat app:compileDebugAndroidTestKotlin
./gradlew.bat :macrobenchmark:assemble
```

### Release 产物

```bash
npm run build:android
npm run build:android:bundle
```

## 当前架构说明

- Android 稳定 feature root 已迁入 `feature-home/search/login/book/reader/rn-host/welfare`
- `app` 保持 thin-app 组合入口，不再承载稳定 feature root state
- RN 页面注册仍通过 `AppRegistry.registerComponent(...)`
- JS bridge 稳定入口在：
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`

如果需要当前阶段和治理状态，不要依赖本 README 的历史描述，直接看：

- [docs/refactor/README.md](./docs/refactor/README.md)
- [docs/refactor/tracking/phase-7-8-validation-board.md](./docs/refactor/tracking/phase-7-8-validation-board.md)
- [docs/refactor/tracking/phase-9-11-validation-board.md](./docs/refactor/tracking/phase-9-11-validation-board.md)

## 当前验证与治理入口

- [验证命令参考](./docs/harness/references/verification.md)
- [重构路线图](./docs/refactor/master-roadmap.md)
- [Stage 5 计划](./docs/refactor/stage-5-phase-9-11-plan.md)
- [模块 owner 矩阵](./docs/refactor/phase-5/module-owner-matrix-2026-03-27.md)
- [API surface checklist](./docs/refactor/phase-5/api-surface-review-checklist.md)
- [回滚索引](./docs/refactor/tracking/rollback-index.md)

## 文档说明

- 旧版 README 中的大量版本亮点、阶段规划和能力承诺，已经不再能完整代表当前仓库。
- 当前对外权威状态以：
  - [docs/refactor/README.md](./docs/refactor/README.md)
  - [docs/refactor/stage-4-closeout-summary.md](./docs/refactor/stage-4-closeout-summary.md)
  - [docs/refactor/stage-5-closeout-summary.md](./docs/refactor/stage-5-closeout-summary.md)
  为准。
- 历史版本叙事保留在 [CHANGELOG.md](./CHANGELOG.md) 中。

## 贡献

如果你要在当前仓库继续推进重构或功能开发，先看：

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)
4. [docs/refactor/README.md](./docs/refactor/README.md)

并遵守：

- 原子提交
- 中文 commit message
- 先更新 `docs/refactor/**`，再更新 `docs/harness/**`

## 许可证

本项目基于 [MIT License](./LICENSE)。
