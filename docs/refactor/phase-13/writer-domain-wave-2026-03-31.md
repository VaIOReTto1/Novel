# Phase 13 Wave 5 - Writer Domain

## 范围
- `src/page/Writer/AIWriteAssistant/**`
- `src/page/Writer/BookManage/**`
- `src/page/Writer/WritePage/**`

## 本轮落地
- `AIWriteAssistant` 已建立 `aiWriteAssistantPageModel`：
  - rehydrate bootstrap
  - idea selector toggle / close / select
  - input focus scroll
  - back navigation
- `BookManagePage` 已建立 `bookManagePageModel`：
  - 页面 bootstrap
  - 返回
  - 继续草稿 / 创建章节 / 打开书籍管理动作
- `WritePage` 已先建立 `writePageModel`：
  - selection menu 事件分发
  - replace / append selected text
  - 参数弹窗 hint 路由

## 新增文件
- `src/page/Writer/AIWriteAssistant/domain/aiWriteAssistantPageModel.ts`
- `src/page/Writer/BookManage/domain/bookManagePageModel.ts`
- `src/page/Writer/WritePage/domain/writePageModel.ts`

## 新增测试
- `__tests__/domains/aiWriteAssistantPageModel.test.ts`
- `__tests__/domains/bookManagePageModel.test.ts`
- `__tests__/domains/writePageModel.test.ts`

## 验证
- `npm test -- --runInBand __tests__/domains/aiWriteAssistantPageModel.test.ts __tests__/domains/bookManagePageModel.test.ts __tests__/domains/writePageModel.test.ts`

## 当前判断
- `AIWriteAssistant` 与 `BookManagePage` 已进入 page-domain 委派模式。
- `WritePage` 当前先完成了 helper 和测试护栏，后续仍可继续把页面中的 selection / modal orchestration 更完整地下沉到 domain model。
