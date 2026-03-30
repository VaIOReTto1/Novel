# Phase 13 Wave 2-4 - Bookshelf / History / Watchlist / Comment

## 范围
- `src/page/BookshelfPage/pages/History/**`
- `src/page/BookshelfPage/pages/Watchlist/**`
- `src/page/BookshelfPage/pages/Bookshelf/**`
- `src/page/comment/**`

## 本轮落地
- `HistoryPage` 已委派到 `historyPageModel`：
  - 初始化加载
  - tab 切换
  - edit toggle
  - 删除选中项
  - 加入书架
- `WatchlistPage` 已委派到 `watchlistPageModel`：
  - 初始化加载
  - edit toggle
  - 全选 / 删除
  - item select / find dramas
- `BookshelfPage` 已委派到 `bookshelfPageModel`：
  - 初始化加载 bookshelf + recommendations
  - 书籍点击 / 长按
  - recommendation / menu / filter / edit
- `CommentPage`、`ReviewDetailPage`、`WriteReviewPage` 已各自抽出 page model：
  - comment bootstrap / search / category / write review
  - review detail parse / load / reply sheet action
  - write review bootstrap / submit / can-submit

## 新增文件
- `src/page/BookshelfPage/pages/History/domain/historyPageModel.ts`
- `src/page/BookshelfPage/pages/Watchlist/domain/watchlistPageModel.ts`
- `src/page/BookshelfPage/pages/Bookshelf/domain/bookshelfPageModel.ts`
- `src/page/comment/CommentPage/domain/commentPageModel.ts`
- `src/page/comment/ReviewDetailPage/domain/reviewDetailPageModel.ts`
- `src/page/comment/WriteReviewPage/domain/writeReviewPageModel.ts`

## 新增测试
- `__tests__/domains/bookshelfHistoryPageModel.test.ts`
- `__tests__/domains/watchlistPageModel.test.ts`
- `__tests__/domains/bookshelfPageModel.test.ts`
- `__tests__/domains/commentPageModel.test.ts`
- `__tests__/domains/reviewDetailPageModel.test.ts`
- `__tests__/domains/writeReviewPageModel.test.ts`

## 验证
- `npm test -- --runInBand __tests__/domains/bookshelfPageModel.test.ts __tests__/domains/bookshelfHistoryPageModel.test.ts __tests__/domains/watchlistPageModel.test.ts __tests__/domains/commentPageModel.test.ts __tests__/domains/reviewDetailPageModel.test.ts __tests__/domains/writeReviewPageModel.test.ts __tests__/stores/historyStores.mock-closure.test.ts __tests__/stores/commentStore.mock-closure.test.ts`

## 当前判断
- `Bookshelf / History / Watchlist / Comment` 这组页面域已经开始形成统一的 “page -> domain model -> store/hook” 结构。
- `CommunityPage` 仍主要通过 `useCommunity + communityHandlers` 收口，后续可继续评估是否还需要单独 page model。
- `Writer / AIWriteAssistant / BookManage` 仍是下一批重点域。
