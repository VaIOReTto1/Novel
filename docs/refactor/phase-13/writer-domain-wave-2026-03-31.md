# Phase 13 Wave 5 - Writer Domain

## 范围
- `src/page/Writer/AIWriteAssistant/**`
- `src/page/Writer/BookManage/**`
- `src/page/Writer/WritePage/**`

## 本轮落地
- `AIWriteAssistant` 已建立并接入 `aiWriteAssistantPageModel`：
  - rehydrate bootstrap
  - idea selector toggle / close / select
  - input focus scroll
  - back navigation
- `BookManagePage` 已建立并接入 `bookManagePageModel`：
  - 页面 bootstrap
  - 返回
  - 继续草稿 / 创建章节 / 打开书籍管理动作
- `WritePage` 已建立并接入 `writePageModel`：
  - selection menu 事件分发
  - replace / append selected text
  - 参数弹窗 hint 路由
  - selection change 解析
  - param modal confirm 分发
  - focus sync
  - `WritePage.smoke` 页面渲染护栏

## 新增文件
- `src/page/Writer/AIWriteAssistant/domain/aiWriteAssistantPageModel.ts`
- `src/page/Writer/BookManage/domain/bookManagePageModel.ts`
- `src/page/Writer/WritePage/domain/writePageModel.ts`

## 新增测试
- `__tests__/domains/aiWriteAssistantPageModel.test.ts`
- `__tests__/domains/bookManagePageModel.test.ts`
- `__tests__/domains/writePageModel.test.ts`
- `__tests__/smoke/WritePage.smoke.test.tsx`

## 验证
- `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts __tests__/smoke/WritePage.smoke.test.tsx`

## 当前判断
- `AIWriteAssistant` 与 `BookManagePage` 已进入 page-domain 委派模式。
- `WritePage` 已完成当前这一轮 deeper extraction，selection / modal orchestration 已进入 page model。
- Writer 域当前剩余工作主要转向更零散的长页与后续命名/目录清理，而不是继续把核心流程留在页面层。
