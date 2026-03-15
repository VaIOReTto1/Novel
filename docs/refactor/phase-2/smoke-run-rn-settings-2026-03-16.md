# Smoke Run - rn - settings

- Scenario: Settings RN render smoke
- Command: `npm test -- --runInBand --runTestsByPath __tests__/smoke/SettingsPage.smoke.test.tsx`
- Route / Page: `SettingsPage`
- Device / API: `Jest / react-test-renderer`
- Network: `N/A`
- Build Variant: `test`
- Expected: 设置页在 mock NativeModules / stores 环境下可稳定渲染，并出现核心 section 文案
- Actual: Jest 运行通过，`SettingsPage smoke` 1/1 passing
- Evidence Files:
  - `__tests__/smoke/SettingsPage.smoke.test.tsx`
  - `jest.setup.js`
- Result: `green`

## Notes
- 该 smoke 只验证设置页核心结构和初始化链路，不承载原生功能回归。
- 原生桥接字段和协议回归由 `V2-04` 的 Bridge contract tests 单独覆盖。
