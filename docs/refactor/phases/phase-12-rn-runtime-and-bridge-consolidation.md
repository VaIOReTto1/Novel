# Phase 12 - RN 运行时与桥接收口

## 目标
- 把 RN 侧的启动初始化、bridge 包装、native event、页面注册和返回策略收成统一入口层。

## 范围
- `App.tsx`
- `index.js`
- `src/utils/appInit.ts`
- `src/utils/runtime/**`
- `src/utils/bridge/**`
- `src/utils/nativeEventListener.ts`
- `src/utils/theme/themeStore.ts`
- 页面与 store 中原本直接触达 `NativeModules` / `DeviceEventEmitter` / `BackHandler` 的入口

## 非目标
- 不进行页面域目录重排
- 不改 `route` / bridge payload / `componentName`
- 不引入新的跨端框架

## 已完成事项
| ID | Task | 实际结果 |
| --- | --- | --- |
| P12.1 | 固定 RN runtime coordinator 宿主 | 已建立 `runtimeCoordinator / preload / pageStateCache / componentRegistry` |
| P12.2 | 固定 bridge gateway 宿主 | 已补 `SettingsBridge`，并扩展 `NavigationBridge` 统一入口 |
| P12.3 | 固定 event hub 宿主 | 已建立 `eventHub`，并让 `nativeEventListener` / `themeStore` 委派到统一监听层 |
| P12.4 | 固定 back navigation policy | 已建立 `backNavigation`，页面层不再直接使用 `BackHandler` |
| P12.5 | 输出 Phase 12 closeout 宿主 | 已形成 closeout assessment 与结构护栏测试 |

## 关键交付物
- `docs/refactor/phase-12/rn-runtime-coordinator-2026-03-31.md`
- `docs/refactor/phase-12/rn-bridge-gateway-2026-03-31.md`
- `docs/refactor/phase-12/rn-event-hub-2026-03-31.md`
- `docs/refactor/phase-12/rn-back-navigation-policy-2026-03-31.md`
- `docs/refactor/phase-12/phase-12-closeout-assessment.md`

## 关闭判断
- `src/**` 中的 `NativeModules` 仅允许留在 `src/utils/bridge/**`
- `src/**` 中的 `DeviceEventEmitter` 仅允许留在 `src/utils/runtime/eventHub.ts`
- `src/**` 中的 `BackHandler` 仅允许留在 `src/utils/runtime/backNavigation.ts`
- 运行时、bridge 与设置页 smoke 护栏可执行

## 证据与验证
- `__tests__/runtime/backNavigation.test.ts`
- `__tests__/runtime/eventHub.test.ts`
- `__tests__/runtime/runtimeCoordinator.test.ts`
- `__tests__/runtime/rawPrimitivesBoundary.test.ts`
- `__tests__/bridge/NavigationBridge.contract.test.ts`
- `__tests__/bridge/UserBridge.contract.test.ts`
- `__tests__/smoke/SettingsPage.smoke.test.tsx`

## 当前状态
- `validated（Phase 12 closeout 生效于 2026-03-31）`
