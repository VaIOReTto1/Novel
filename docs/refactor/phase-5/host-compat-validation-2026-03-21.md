# Phase 5 宿主页兼容验证 - 2026-03-21

# 摘要
- 目标：验证当前 `Phase 5` 模块图仍然保持 Bridge / RN Host 兼容性。
- 范围：
  - `profile`
  - `settings`
  - `aipage`
  - Bridge contract regression
  - RN settings smoke
- 结果：`pass`

## Phase 5 实际变更
- Phase 5 当前已落地的 feature / module 切口 **没有** 移动或重命名以下宿主根逻辑：
  - `android/app/src/main/java/com/novel/rn/ReactNativePage.kt`
  - `android/app/src/main/java/com/novel/rn/NavigationPackage.kt`
  - `android/app/src/main/java/com/novel/MainApplication.kt`
  - `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt`
- 当前模块化只把低风险 helper / contract / store 搬到了：
  - `core-bridge-contract`
  - `feature-welfare`
  - `feature-search`
  - `feature-home`
  - `feature-rn-host`

## 设备 Spot Check
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 构建：`debug`
- 安装命令：
  - `android/gradlew.bat app:installDebug`
- 路由命令：
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route profile`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route settings`
  - `adb shell am start -S -n com.novel/.ComposeMainActivity --es debug_route aipage`

### 实际结果
- `profile` 二次 dump 已回到 `package="com.novel"` 容器层，包含 `ComposeView -> ViewFactoryHolder -> FrameLayout` 宿主页骨架。
- `settings` 二次 dump 已回到 `package="com.novel"` 容器层，包含同样的 RN Host 容器骨架。
- `aipage` dump 也回到 `package="com.novel"` 容器层。
- 这轮设备证据强度低于 Phase 4：
  - 当前没有稳定抓到页面级文本元素
  - 但已确认模块化后 `debug_route` 仍能把页面拉起到 `com.novel` 宿主容器，而不是系统锁屏或崩溃退出

## 自动化兼容证据
- Jest / contract：
  - `npm test -- --runInBand --runTestsByPath __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`
  - 结果：`pass`
- Core bridge module：
  - `android/gradlew.bat :core-bridge-contract:testDebugUnitTest`
  - 结果：`pass`
- 全量 app JVM 回归：
  - `android/gradlew.bat :app:testDebugUnitTest`
  - 结果：`pass`

## 证据文件
- 复用 Phase 4 的历史强基线：
  - `docs/refactor/phase-4/host-risk-run-profile-2026-03-20.md`
  - `docs/refactor/phase-4/host-risk-run-settings-2026-03-20.md`
  - `docs/refactor/phase-4/host-risk-run-author-ai-2026-03-20.md`
- Phase 5 设备 spot check：
  - `docs/refactor/evidence/profile-host-phase5-2026-03-21.xml`
  - `docs/refactor/evidence/settings-host-phase5-2026-03-21.xml`
  - `docs/refactor/evidence/aipage-host-phase5-2026-03-21.xml`

## 结论
- 基于证据的判断：
  - 当前 `Phase 5` 模块化切口保留了宿主根接线，没有改变 route / payload / RN 组件语义。
  - 模块化后的设备 spot check 仍能把 `profile / settings / aipage` 拉起到 `com.novel` 宿主容器。
  - Bridge contract 与 RN settings smoke 回归继续保持绿色。
- 因此 `V5-04` 可以进入 `validated / green`。
