[简体中文](./README.md) | [English](./README.en.md)

# Novel

`Novel` 是一个 `React Native + Android Compose/Kotlin` 混合架构的小说阅读应用仓库。

本 README 以当前代码与重构控制面为准，聚焦：

- 当前工程状态
- 仓库结构
- 真实可用的运行与验证命令
- 当前重构阶段和权威文档入口

## 当前工程状态

- 当前 package version：`1.0.2`
- 当前 Android 模块图：`app + core-* + feature-* + macrobenchmark`
- 当前 refactor control-plane：
  - `Stage 4 = validated`
  - `Stage 5 = validated`
- 当前没有新的 active refactor stage 正在执行；后续结构性工作应进入长期维护 / reopen / 新阶段规划流程，而不是绕开控制面直接推进

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

### 当前已知工程事实

- Android 已稳定拆分为 `core-*` 与 `feature-*` 模块
- RN 与 Native 当前仍通过 bridge / host 机制协作
- 已有本地治理入口：
  - `RefactorFeatureFlags`
  - `StartupPerformanceMonitor`
  - `WelfarePerformanceMonitor`
  - `android/gradle/verification-metadata.xml`
- 当前已知缺口：
  - `android/gradle/libs.versions.toml` 缺失
  - `org.gradle.configuration-cache=false`
  - 无统一 Crash / ANR / 灰度平台

## 仓库结构

### React Native

- `App.tsx`：RN app root
- `index.js`：RN 入口与页面注册
- `src/page/**`：RN 页面、组件、store、hooks
- `src/utils/bridge/**`：JS bridge 封装
- `src/utils/theme/**`：主题与样式状态
- `src/utils/appInit.ts`：RN 初始化与主题/用户态预加载

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

### 测试与文档

- `__tests__/**`：Jest bridge contract 与 smoke 覆盖
- `docs/refactor/**`：阶段计划、closeout、validation board、decision log、rollback index
- `docs/harness/**`：agent 入口、当前焦点、session log、generated snapshot

## 当前主要能力

### Android Native / Compose

- 首页、搜索、登录、书籍详情、阅读器、福利页已由 Android 侧稳定模块承接
- 当前具备：
  - MVI / reducer / state adapter
  - Room / DataStore / 本地缓存
  - Reader 状态与历史/进度恢复入口
  - Welfare WebView host 与性能监控入口

### React Native

- `src/**` 仍承载大量业务页，包括：
  - Profile / Bookshelf / History / Message
  - Settings / Privacy / Help Support
  - BecomeWriter / RecommendBook / MemberCenter
  - Comment / ReviewDetail / WriteReview
  - WritePage / AIWriteAssistant / BookManage
- 当前 RN 侧已具备：
  - Zustand store 体系
  - Theme store 与 Native 主题同步
  - NavigationBridge / UserBridge contract
  - 多页面 `AppRegistry.registerComponent(...)` 注册机制

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

# 运行 iOS（如环境支持）
npm run ios

# Jest
npm test -- --runInBand

# ESLint
npm run lint

# harness 检查
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

- Android 稳定 feature root 已迁入：
  - `feature-home`
  - `feature-search`
  - `feature-login`
  - `feature-book`
  - `feature-reader`
  - `feature-rn-host`
  - `feature-welfare`
- `app` 当前保持 thin-app 组合入口
- RN 页面注册仍通过 `AppRegistry.registerComponent(...)`
- 稳定 JS bridge 入口在：
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`

如果你要了解当前阶段和治理状态，不要依赖旧版 README 中的愿景路线描述，直接看：

- [docs/refactor/README.md](./docs/refactor/README.md)
- [docs/refactor/tracking/phase-7-8-validation-board.md](./docs/refactor/tracking/phase-7-8-validation-board.md)
- [docs/refactor/tracking/phase-9-11-validation-board.md](./docs/refactor/tracking/phase-9-11-validation-board.md)

## 当前验证与治理入口

- [验证命令参考](./docs/harness/references/verification.md)
- [重构总路线图](./docs/refactor/master-roadmap.md)
- [Stage 5 计划](./docs/refactor/stage-5-phase-9-11-plan.md)
- [模块 owner 矩阵](./docs/refactor/phase-5/module-owner-matrix-2026-03-27.md)
- [API surface checklist](./docs/refactor/phase-5/api-surface-review-checklist.md)
- [回滚索引](./docs/refactor/tracking/rollback-index.md)

## 当前重构状态

- `Stage 4` 已关闭：
  - `Phase 7`：包体积、依赖、构建效率治理
  - `Phase 8`：observability、rollback、ADR 治理宿主
- `Stage 5` 已关闭：
  - `Phase 9`：运行恢复与业务连续性
  - `Phase 10`：无障碍、合规、供应链与双端协作
  - `Phase 11`：数据质量与可维护性

后续默认进入长期维护 / reopen / 新阶段规划流程，而不是继续沿用旧 README 中的“阶段 1-4 即将执行”叙事。

## 文档说明

- 旧版 README 中大量功能亮点、阶段路线和技术承诺，已经不再完整代表当前仓库。
- 当前对外权威状态以：
  - [docs/refactor/README.md](./docs/refactor/README.md)
  - [docs/refactor/stage-4-closeout-summary.md](./docs/refactor/stage-4-closeout-summary.md)
  - [docs/refactor/stage-5-closeout-summary.md](./docs/refactor/stage-5-closeout-summary.md)
  为准。
- 历史版本叙事保留在 [CHANGELOG.md](./CHANGELOG.md) 中。

## 贡献

如果你要继续推进功能或重构，请先看：

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)
4. [docs/refactor/README.md](./docs/refactor/README.md)

仓库仍遵守：

- 原子提交
- 中文 commit message
- 先更新 `docs/refactor/**`，再更新 `docs/harness/**`

## 许可证

本项目基于 [MIT License](./LICENSE)。
