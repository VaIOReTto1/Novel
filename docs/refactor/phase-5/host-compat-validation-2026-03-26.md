# Phase 5 Host-Compat Validation（2026-03-26）

## 口径
- 类型：`reopen closeout`
- 状态：`validated`

## 宿主兼容结论
- `NavigationBridgeModule`、`SettingsBridgeModule`、`ReactNativePage` 已切到 host gateway / entry point 取能力。
- `MainApplication.getInstance()` 只保留在 app-host 默认实现中。
- `ViewModelProvider(...)` 只保留在 `HostBridgeViewModelGateway`。
- `BridgeViewModel / SettingsViewModel` 已迁入 `feature-rn-host`。
- `ReactNativeBridge` 继续通过 `ReactContextWarmupGateway` 取上下文，不再直接碰宿主根实现细节。

## 验证证据
- `android/gradlew.bat :feature-rn-host:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"`
- `android/gradlew.bat :app:compileDebugKotlin --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"`
- `android/gradlew.bat :app:testDebugUnitTest --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkapt.incremental.apt=false" "-Pkotlin.incremental=false"`
- `npm test -- --runInBand __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`

## 允许保留的宿主直连
- `NavigationUtil` 和 `iosSwipeBack` 仍可碰 `NavViewModel.navController`，因为它们属于导航 wrapper。
- `DefaultReactRootViewRegistryGateway / DefaultReactRootViewCacheGateway / DefaultReactContextWarmupGateway` 仍可碰 `MainApplication.getInstance()`，因为它们属于 app-host 默认实现。
