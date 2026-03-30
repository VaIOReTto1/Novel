# RN Event Hub

## 状态
- 记录日期：`2026-03-31`
- 关联阶段：`Stage 6 / Phase 12`
- 当前结论：`已固定 RN event hub 宿主`

## 当前入口
- `src/utils/nativeEventListener.ts`
- `src/utils/theme/themeStore.ts`
- `WritePage.tsx` 中局部 `DeviceEventEmitter` 监听
- `TimedSwitchPage.tsx` 中局部 `DeviceEventEmitter` 监听

## 当前问题
- `DeviceEventEmitter` 监听与派发职责目前分散在 runtime、theme 和页面实现中

## 收口目标
- event listener 注册、派发、清理路径可追溯
- 页面不再随手创建新的 native event hub
