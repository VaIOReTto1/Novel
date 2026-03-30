# Bridge Schema Manifest

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 10`
- 当前结论：`已固定 Bridge schema manifest 宿主`

## 当前稳定契约面
| Surface | Current Host | Source Of Truth |
| --- | --- | --- |
| Native module names | `NavigationBridge`, `UserBridge` | `bridge-schema-compat-governance-2026-03-27.md`, `src/utils/bridge/**` |
| Promise payload / error mapping | `BridgePromiseErrorMapper`, `AppError` | `android/core-bridge/**`, `android/core-common/**` |
| Event names | `ThemeChanged`, `WritePageSelectionMenuAction` | `bridge-schema-compat-governance-2026-03-27.md`, `__tests__/bridge/**` |
| route / componentName compatibility | `NavigationUtil`, RN component registry | `bridge-schema-compat-governance-2026-03-27.md`, `index.js` |

## 兼容窗口规则
- 默认不改既有方法名、事件名、route 名和 `componentName`。
- 如确需变更，必须：
  - 先登记旧值 / 新值
  - 指定双端兼容窗口
  - 明确何时删旧值
  - 补 contract tests
