# RN Runtime Coordinator

## 状态
- 记录日期：`2026-03-31`
- 关联阶段：`Stage 6 / Phase 12`
- 当前结论：`已固定 RN runtime coordinator 宿主`

## 当前职责混合点
- `App.tsx`
- `index.js`
- `src/utils/appInit.ts`

当前混合职责包括：
- app init
- 页面状态缓存
- 主题同步
- 用户预加载
- 设置预加载
- RN 页面组件导入注册

## 收口目标
- 启动初始化
- 主题初始化与同步
- 用户/设置预加载
- 页面状态缓存
- 页面注册入口

## 当前判定
- 这些职责已经存在，但缺单一 runtime coordinator 宿主。
