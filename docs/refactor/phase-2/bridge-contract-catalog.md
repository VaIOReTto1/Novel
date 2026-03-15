# Phase 2 Bridge Contract Catalog

## 目标
- 为 `V2-04` 提供一组稳定、低侵入的 Bridge contract tests。
- 在不修改 UI 和业务行为的前提下，拦截 Native/RN 协议层的高风险回归。

## 本轮覆盖范围
- `UserBridge`
  - Promise 返回字段: `uid`, `token`, `nickname`, `photo`, `sex`, `balance`, `coins`
  - RN fallback 合同: 无 Native bridge 时仍返回稳定 mock 结构
- `NavigationBridge`
  - 关键 Promise 合同: `getReadingHistory`, `getAuthorStatus`, `getAuthorBooks`
  - 参数合同: `aiExpand`, `aiCondense`, `aiContinue` 的数值取整
  - 兼容合同: `navigateToBecomeWriterWithFlag` 缺失时回退到旧接口
- DeviceEventEmitter 事件合同
  - `ThemeChanged`
    - 必需字段: `colorScheme`, `currentThemeMode`, `followSystem`
  - `WritePageSelectionMenuAction`
    - 必需字段: `action`, `start`, `end`
    - 可选字段: `selectedText`

## 证据位置
- Jest tests
  - `__tests__/bridge/UserBridge.contract.test.ts`
  - `__tests__/bridge/NavigationBridge.contract.test.ts`
  - `__tests__/bridge/NativeBridgeEventContracts.test.ts`
- Contract fixtures
  - `__tests__/fixtures/bridge/user-bridge-current-user-data.json`
  - `__tests__/fixtures/bridge/navigation-reading-history.json`
  - `__tests__/fixtures/bridge/theme-changed-event.json`
  - `__tests__/fixtures/bridge/write-page-selection-menu-action.json`
- Native sources under validation
  - `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt`
  - `android/app/src/main/java/com/novel/rn/bridge/UserBridgeModule.kt`
  - `android/app/src/main/java/com/novel/ui/theme/ThemeManager.kt`
- RN consumers under validation
  - `src/utils/bridge/NavigationBridge.ts`
  - `src/utils/bridge/UserBridge.ts`
  - `src/utils/theme/themeStore.ts`
  - `src/page/Writer/WritePage/WritePage.tsx`
  - `src/page/SettingsPage/TimeSwitchPage/TimedSwitchPage.tsx`

## 验证命令
```bash
npm test -- --runInBand --runTestsByPath __tests__/bridge/UserBridge.contract.test.ts __tests__/bridge/NavigationBridge.contract.test.ts __tests__/bridge/NativeBridgeEventContracts.test.ts
```

## 后续扩展建议
- 若后续新增 RN module，先补 contract fixture，再补 bridge tests，最后再接入 smoke/CI。
- 若 Promise payload 发生字段升级，必须同步更新:
  - Native producer
  - RN wrapper
  - fixture
  - contract test
  - `phase-0-2-validation-board.md`
