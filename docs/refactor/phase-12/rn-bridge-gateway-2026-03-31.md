# RN Bridge Gateway

## 状态
- 记录日期：`2026-03-31`
- 关联阶段：`Stage 6 / Phase 12`
- 当前结论：`已固定 RN bridge gateway 宿主`

## 当前主入口
- `src/utils/bridge/NavigationBridge.ts`
- `src/utils/bridge/UserBridge.ts`

## 当前问题
- 主包装层已存在
- 但页面和 store 里仍有散落的 `NativeModules` 直接调用

## 收口目标
- `NavigationBridge` / `UserBridge` 成为 RN -> Native 的默认唯一入口
- 页面与 store 不再直接碰 `NativeModules`
