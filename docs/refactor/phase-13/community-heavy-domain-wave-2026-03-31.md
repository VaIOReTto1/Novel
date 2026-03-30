# Phase 13 Wave 6 - Community / Remaining Heavy Pages

## 范围
- `src/page/BookshelfPage/pages/Community/**`
- `src/page/ScrollBox/RecommendBookPage/**`
- `src/page/ScrollBox/MemberCenterPage/**`
- `src/page/ScrollBox/BecomeWriterPage/**`

## 本轮落地
- `Community` 已建立 `communityPageModel`，并将 `useCommunity` 的 bootstrap、分页、重试与筛选切换动作委派到纯 helper。
- `RecommendBookPage` 已建立 `recommendBookPageModel`：
  - 页面 bootstrap
  - 返回
  - 任务 tab 切换
  - 查看全部 / 服务 / 任务点击
  - 提现 alert 路由
- `MemberCenterPage` 已建立 `memberCenterPageModel`：
  - 页面 bootstrap
  - 返回
  - 卡片切换
  - 套餐选择 / 购买
  - 隐私 / 条款 / task card 路由
- `BecomeWriterPage` 已建立 `becomeWriterPageModel`：
  - 页面 bootstrap
  - 作者作品恢复与 fallback
  - 返回
  - data / author / activity tab 切换
  - AI / more / work / create chapter 路由

## 新增文件
- `src/page/BookshelfPage/pages/Community/domain/communityPageModel.ts`
- `src/page/ScrollBox/RecommendBookPage/domain/recommendBookPageModel.ts`
- `src/page/ScrollBox/MemberCenterPage/domain/memberCenterPageModel.ts`
- `src/page/ScrollBox/BecomeWriterPage/domain/becomeWriterPageModel.ts`

## 新增测试
- `__tests__/domains/communityPageModel.test.ts`
- `__tests__/domains/recommendBookPageModel.test.ts`
- `__tests__/domains/memberCenterPageModel.test.ts`
- `__tests__/domains/becomeWriterPageModel.test.ts`

## 验证
- `npm test -- --runInBand __tests__/domains/communityPageModel.test.ts __tests__/community/communityHandlers.test.ts __tests__/domains/recommendBookPageModel.test.ts __tests__/domains/memberCenterPageModel.test.ts __tests__/domains/becomeWriterPageModel.test.ts`

## 当前判断
- `Community` 这块已经不再只是 action handlers，bootstrap / paging / retry / filters 也开始进入 page model 边界。
- `RecommendBook / MemberCenter / BecomeWriter` 这组三个 remaining heavy pages 已进入和前面波次一致的 page-model 委派模式。
- `Phase 13` 剩余重点已继续收缩到：
  - `WritePage deeper extraction`
  - 仍未按 page model 治理的个别长页 / host-heavy 页
