# RN Naming Directory State Model Guide

## 目标
- 为 Stage 6 之后的 RN 维护提供统一的命名、目录与状态模型规则。

## 目录规则
- 页面入口继续保留在各自页面目录中。
- 页面层只负责展示与最薄交互接线。
- 页面域编排统一下沉到 `domain/*PageModel.ts`。
- store / hooks / components / types / styles 继续留在页面域内部，不再跨域散落。

## 命名规则
- page model 文件统一使用 `*PageModel.ts`。
- bootstrap helper 统一使用 `bootstrap*Page`。
- 页面动作编排 helper 统一使用 `create*PageHandlers`。
- smoke 测试继续使用 `*.smoke.test.tsx`。
- domain 测试继续使用 `*PageModel.test.ts` 或领域名 helper test。

## 状态模型规则
- 页面层本地状态仅保留 UI 临时态。
- 业务态优先留在 store。
- domain helper 只做 orchestration，不直接持有 UI 树。
- 原生导航、桥接、registry 和 runtime 继续通过既有 wrapper 层进入。

## 当前适用范围
- `Profile / Settings`
- `Bookshelf / History / Watchlist / Community`
- `Comment / ReviewDetail / WriteReview`
- `Writer / AIWriteAssistant / BookManage / WritePage`
- `ScrollBox` 其余长页
