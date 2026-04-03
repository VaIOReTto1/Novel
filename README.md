[简体中文](./README.md) | [English](./README.en.md)

# Novel

`Novel` 是一个 `React Native + Android Compose/Kotlin` 混合架构的小说阅读应用仓库。

当前仓库以代码事实与 `docs/refactor/**` 控制面为准。若和历史 README、旧阶段文档冲突，应优先相信：

1. 代码与构建文件
2. [docs/refactor/README.md](./docs/refactor/README.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)

## 当前状态

- 当前 package version：`1.0.2`
- 当前 Android 模块图：`app + core-* + feature-* + macrobenchmark`
- 当前 refactor 主线：`Stage 7 = Phase 15-18`
- 当前阶段状态：
  - `Stage 7 = in_progress`
  - `Phase 15 = in_progress`
  - `Phase 16 = in_progress`
  - `Phase 17 = in_progress`
  - `Phase 18 = in_progress`

当前 Stage 7 重点是视觉系统、Token 真源、资产治理、展示基建与页面换肤收尾。

## 权威入口

- [重构控制面板](./docs/refactor/README.md)
- [Stage 7 计划](./docs/refactor/stage-7-phase-15-18-plan.md)
- [Phase 15-18 验证看板](./docs/refactor/tracking/phase-15-18-validation-board.md)
- [当前焦点](./docs/harness/current-focus.md)
- [架构地图](./ARCHITECTURE.md)
- [Agent 入口](./AGENTS.md)

## 仓库结构

### React Native

- `App.tsx`：RN app root
- `index.js`：RN 入口与页面注册
- `src/page/**`：RN 页面、组件、store、hooks
- `src/utils/bridge/**`：JS bridge 封装
- `src/utils/runtime/**`：runtime 收口层
- `src/design-system/**`：Stage 7 视觉系统、Token、媒体与展示基建

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

- `__tests__/**`：Jest、bridge、runtime、design-system、smoke、harness
- `docs/refactor/**`：阶段计划、validation board、closeout、decision log、rollback index
- `docs/harness/**`：current focus、session log、verification reference、generated snapshot

## 常用命令

### JavaScript / RN

```bash
npm install
npm run start
npm run android
npm test -- --runInBand
npm run lint
```

### Harness / Stage 7

```bash
npm run harness:refresh
npm run harness:check
npm run novel-design:audit
npm run novel-design:audit:check
npm run novel-design:tokens
npm run novel-design:tokens:check
npm run novel-design:assets
npm run novel-design:assets:check
```

### Android

```bash
cd android
./gradlew.bat app:testDebugUnitTest
./gradlew.bat app:lintDebug
./gradlew.bat app:compileDebugAndroidTestKotlin
./gradlew.bat :macrobenchmark:assemble
```

## 当前已知事实

- Android 稳定 feature root 已拆到 `feature-*`
- `app` 维持 thin-app 入口
- RN / Native 通过 bridge / host 协作
- 当前已建立 Stage 7 的：
  - 审计脚本与 machine-readable inventory
  - Token 真源与导出链路
  - 资产治理 manifest / ledger
  - RN / Android showcase 基建
  - 页面换肤回归、smoke、design-system 测试基线

## 已知缺口

- `android/gradle/libs.versions.toml` 仍缺失
- 当前仓库仍无统一 Crash / ANR / 灰度平台
- Stage 7 closeout 仍待补齐 Figma frame map、标注稿、组件映射与最终签核证据

## 协作约定

如需继续推进功能或重构，请先读：

1. [AGENTS.md](./AGENTS.md)
2. [ARCHITECTURE.md](./ARCHITECTURE.md)
3. [docs/harness/current-focus.md](./docs/harness/current-focus.md)
4. [docs/refactor/README.md](./docs/refactor/README.md)

仓库当前遵守：

- 原子提交
- 中文 commit message
- 若阶段状态变化，先改 `docs/refactor/**`，再改 `docs/harness/**`

## 许可

本项目基于 [MIT License](./LICENSE)。
