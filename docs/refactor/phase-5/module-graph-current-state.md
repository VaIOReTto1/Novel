# Phase 5 模块图当前事实

## 口径
- 生效日期：`2026-03-26`
- 状态：`validated`
- 说明：本文件描述 reopen 收口完成后的当前事实，不再沿用 `2026-03-21` checkpoint 的旧口径。

## 模块职责
- `:app`
  - `MainApplication`
  - `ComposeMainActivity`
  - `MainPage / NavigationUtil / ReaderPage / ReactNativePage`
  - `NavigationPackage / NavigationBridgeModule / SettingsBridgeModule / ReactNativeBridge`
  - 默认 host gateway / default service implementation / Hilt binding
- `:core-common`
  - `CoreLogger`
  - 通用 MVI / domain / concurrency / adapter
- `:core-ui`
  - 共享主题、尺寸与通用 Compose 组件
- `:core-bridge`
  - `BridgeStateAdapter`
  - `DefaultNavigationBridgeFacade`
  - `NavigationBridgeNetworkGateway`
  - `BridgeCoroutineScopes`
- `:core-bridge-contract`
  - delegate / contract / helper
- `:core-storage`
  - 共享存储抽象与兼容层
- `:core-network`
  - 共享网络契约与执行器适配
- `:feature-home`
  - `HomeViewModel`
  - `HomeMvi / HomeStateAdapter / HomeStateProjector`
  - `HomeFeedGateway / HomeRnSyncGateway`
- `:feature-search`
  - `SearchViewModel / SearchResultViewModel`
  - 搜索 MVI、状态适配与搜索 gateway
- `:feature-login`
  - `LoginViewModel`
  - 登录 MVI、状态适配与登录 gateway
- `:feature-book`
  - `BookDetailViewModel`
  - 书详情 MVI、状态适配与书详情 gateway
- `:feature-reader`
  - `ReaderViewModel`
  - `ReaderIntent / ReaderState / ReaderEffect / ReaderReducer / ReaderStateAdapter`
  - `ReaderSettingsCoordinator / ReaderSettingsRefreshCoordinator / ReaderHistoryCoordinator / ReaderMappingHelper`
  - `ReaderPaginationGateway / ReaderSettingsGateway / ReaderHistoryGateway`
- `:feature-rn-host`
  - `BridgeViewModel`
  - `SettingsViewModel`
  - `ReactNativePageContent`
  - `HostNavigationGateway / ReactRootViewCacheGateway / ReactContextWarmupGateway / ReactRootViewRegistryGateway`
- `:feature-welfare`
  - welfare 主状态层、页面内容、初始化 usecase 与 WebView 适配

## 依赖方向
- `:app -> :core-*`
- `:app -> :feature-home`
- `:app -> :feature-search`
- `:app -> :feature-login`
- `:app -> :feature-book`
- `:app -> :feature-reader`
- `:app -> :feature-rn-host`
- `:app -> :feature-welfare`
- 当前没有新增 Gradle 循环依赖。

## app 薄壳边界
- 允许留在 `app` 的内容：
  - Android 强制入口
  - route/page wrapper
  - RN module adapter
  - host gateway 默认实现
  - service 默认实现
- 不再允许留在 `app` 的内容：
  - `home/search/login/book/reader` feature ViewModel 根
  - `BridgeViewModel / SettingsViewModel`
  - 首页、搜索、登录、书详情、阅读器的稳定状态机与可复用 feature 逻辑

## 当前高风险根收口结果
- `MainApplication`
  - 保留为 Application 入口与 registry/orchestrator/reporter 转发
- `NavigationPackage`
  - 保留 RN native module 注册适配
- `NavigationBridgeModule / SettingsBridgeModule`
  - 保留 RN adapter、Promise 映射、delegate/facade 调用
- `ReactNativePage / ReactNativeBridge`
  - 通过 host gateway 工作，不再直接碰 `MainApplication` 或 `ViewModelProvider`

## 剩余允许直连
- `MainApplication.getInstance()`
  - 仅允许出现在 app-host 默认实现中
- `ViewModelProvider(...)`
  - 仅允许出现在 `HostBridgeViewModelGateway`
- `NavViewModel.navController`
  - 仅允许出现在 `NavigationUtil`、`iosSwipeBack` 等导航 wrapper / host adapter 中
