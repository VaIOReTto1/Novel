# Phase 5 进入条件清单

## 状态
- 当前状态：`planned`
- 前置阶段：`Phase 4 = validated`

## 进入目标
- 在不改变现有 UI/业务语义的前提下，启动真正的 Gradle 模块化准备
- 先拆稳定基础层，再拆 feature，Reader 继续后置

## 已满足前置条件
- `V4-01 ~ V4-08` 已达到 `green`
- `BridgeFacade` 与宿主页链路已稳定
- `ESLint error = 0`
- `detekt weighted issues = 1901`
- `app:testDebugUnitTest` 与 `npm test -- --runInBand` 当前通过

## Phase 5 必须承接的 carried debt
### 明确延期到 Phase 5 的 mock / fallback
- `android/app/src/main/java/com/novel/page/search/usecase/GetCategoryFiltersUseCase.kt`
  - 服务端分类失败时退回整套硬编码分类
- `src/page/comment/ReviewDetailPage/api/reviewDetailApi.ts`
  - 评论详情页主数据仍由 mock API 驱动
- `src/page/ScrollBox/BecomeWriterPage/**`
  - 作者中心与作品区仍深度依赖 mock
- `src/page/BookshelfPage/pages/Bookshelf/store/bookshelfStore.ts`
- `src/page/BookshelfPage/pages/Watchlist/store/watchlistStore.ts`
- `src/page/BookshelfPage/pages/Community/store/communityStore.ts`
- `src/page/ScrollBox/ViewedUsersPage/store/viewedUsersStore.ts`
- `src/page/ScrollBox/MyReservationPage/store/myReservationStore.ts`
- `src/page/ScrollBox/MemberCenterPage/store/memberCenterStore.ts`
- `src/page/ScrollBox/RecommendBookPage/store/recommendBookStore.ts`
- `src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore.ts`
- `src/page/ScrollBox/MessagePage/store/messageStore.ts`

## Phase 5 不允许偷带的范围扩张
- 不允许把 `Phase 5` 伪装成“顺手大清所有 RN 页面 mock + 顺手改语义”
- 不允许在模块化首轮就提前做 Reader 最终大拆
- 不允许改 route / Bridge event / payload / RN 组件名

## 推荐模块化顺序
1. `core-network / core-storage / core-bridge-contract`
2. `feature-home / feature-search / feature-welfare`
3. `feature-profile / feature-settings / feature-author-ai`
4. `feature-bookshelf` 与仍带 mock 的 RN heavy pages
5. `reader` 最后进入真正模块化准备

## 进入前必须再次确认
- carried debt 已在对应 phase 文档、看板和决策日志中留痕
- `profile-host / RN Host` 风险证据仍可回溯
- `Phase 4` closeout 文档已固定，不再改写口径

## 进入结论
- 当前允许状态：`可以开始 Phase 5 计划与拆分准备，但不允许跳过本清单直接进入 reader 最终大拆`
