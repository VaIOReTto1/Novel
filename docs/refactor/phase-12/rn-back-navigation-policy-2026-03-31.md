# RN Back Navigation Policy

## 状态
- 记录日期：`2026-03-31`
- 关联阶段：`Stage 6 / Phase 12`
- 当前结论：`已固定 RN back navigation policy 宿主`

## 当前问题
- 多个页面直接使用 `BackHandler.addEventListener(...)`
- 返回行为既有直接 `NativeModules.NavigationBridge` 调用，也有包装层调用

## 收口目标
- `BackHandler` 只允许出现在约定 runtime / navigation policy 层
- 页面返回语义统一经 RN bridge runtime 进入
