# Phase 4 Mock Inventory Report

## 记录信息
- 生成日期：`2026-03-20`
- 阶段：`Phase 4 / Wave 4 / W4-M01`
- 目标：识别 `Phase 4` 触达范围内的生产 mock、测试兜底和假数据注入点，并为 `W4-M02` 提供清理顺序

## 2026-03-21 关闭结论
- 关闭口径：`触达范围收口`
- 已完成清理：
  - `SearchRankingRepository.loadRanking`
  - `SearchResultViewModel.defaultCategories`
  - `HomeCompositeUseCase.loadInitialData` 中硬编码单项 `推荐` fallback
  - `src/page/ScrollBox/HistoryPage/store/historyStore.ts#loadMoreHistory`
  - `src/page/BookshelfPage/pages/History/store/historyStore.ts#loadMoreHistory`
  - `src/utils/bridge/UserBridge.ts` native-missing mock user fallback
  - `src/page/comment/CommentPage/store/commentStore.ts` 空数据/错误时的随机 mock fallback
- 延期规则：
  - 页面主数据源本身仍为 mock 的 RN heavy pages，不在 `Phase 4` 中强行替换
  - 所有延期项已转入 `docs/refactor/phase-4/phase-5-entry-checklist.md`

## 低风险优先清理
| Priority | Path | Symbol / Behavior | Current Behavior | Cleanup Risk |
| --- | --- | --- | --- | --- |
| P1 | `android/app/src/main/java/com/novel/page/search/repository/SearchRankingRepository.kt` | `loadRanking` | 榜单不足 20 条时补 `测试小说N / 热门短剧N / 新书推荐N` 等假条目 | low |
| P1 | `android/app/src/main/java/com/novel/page/search/viewmodel/SearchResultViewModel.kt` | `defaultCategories` | 视图层重复提供硬编码分类兜底 | low |
| P2 | `android/app/src/main/java/com/novel/page/home/usecase/HomeCompositeUseCase.kt` | `loadInitialData` fallback | 分类筛选加载失败时回落到单个 `推荐` 项 | low |
| P2 | `src/page/ScrollBox/HistoryPage/store/historyStore.ts` | `loadMoreHistory` | 首屏真实，翻页追加 `generateMockHistoryItems` | medium |
| P2 | `src/page/BookshelfPage/pages/History/store/historyStore.ts` | `loadMoreHistory` | 首屏真实，翻页追加 `generateMockHistoryItems` | medium |
| P2 | `src/utils/bridge/UserBridge.ts` | mock user fallback | Native bridge 缺失时返回 `mock_uid / mock_token` 与伪用户态 | medium |
| P2 | `src/page/comment/CommentPage/store/commentStore.ts` | comment mock fallback | 空数据、异常和随机路径回落 mock comment | medium |

## 高风险暂缓项
| Priority | Path | Symbol / Behavior | Current Behavior | 暂缓原因 |
| --- | --- | --- | --- | --- |
| H1 | `src/page/comment/ReviewDetailPage/api/reviewDetailApi.ts` | `mockReviewDetail / mockComments` | 评论详情整页由 mock API 驱动 | 清理前必须接真实接口或接受空态 |
| H1 | `android/app/src/main/java/com/novel/page/search/usecase/GetCategoryFiltersUseCase.kt` | `getDefaultCategories` | 搜索分类接口失败时退回整套硬编码分类 | 当前承担可用性兜底，后端稳定性未确认 |
| H1 | `src/page/ScrollBox/BecomeWriterPage/**` | `generateMock*` / `fetchAuthorWorks` fallback | 作者中心和作品区深度依赖 mock | 清理会直接暴露大面积空态 |
| H1 | `src/page/BookshelfPage/pages/Bookshelf/store/bookshelfStore.ts` | `mockBookshelfItems / mockRecommendations` | 书架与推荐主链路是本地 mock | 清理前需真实数据源 |
| H1 | `src/page/BookshelfPage/pages/Watchlist/store/watchlistStore.ts` | `mockWatchlistItems / mockRecommendations` | 追剧列表主链路是本地 mock | 清理前需真实数据源 |
| H1 | `src/page/BookshelfPage/pages/Community/store/communityStore.ts` | `mockCommunityPosts / mockCategories / mockCommunityCircles` | 社区页面主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/ViewedUsersPage/store/viewedUsersStore.ts` | `generateMockUserInfo / generateMockRecommendUsers` | 推荐用户页主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/MyReservationPage/store/myReservationStore.ts` | `generateMockUserInfo / generateMockNewReservations` | 预约页主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/MemberCenterPage/store/memberCenterStore.ts` | `generateMock* / vipBenefitsMock` | 会员中心主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/RecommendBookPage/store/recommendBookStore.ts` | `generateMock*` | 投稿推荐页主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore.ts` | `mockConsultCategories / mockFrequentQuestions / mockQuestionDetails` | 帮助中心主体由 mock 供给 | 清理前需真实数据源 |
| H1 | `src/page/ScrollBox/MessagePage/store/messageStore.ts` | `generateMockMessages` | 消息中心列表全链路由 mock 驱动 | 清理前需真实数据源 |

## 当前不列入清理
- `android/app/src/main/java/com/novel/utils/network/cache/**`
  - 当前只见真实缓存、过期缓存和网络兜底，没有额外注入伪业务数据。
- `android/app/src/main/java/com/novel/page/read/service/HistoryService.kt`
  - 当前存在 mock title/author/progress fallback，但 `W4-R02` 约束为“先不改 fallback 语义”，因此暂只记录，不在本轮首批清理。

## W4-M02 建议顺序
1. 清理 `SearchRankingRepository` 的榜单补假条目。
2. 清理 `SearchResultViewModel` 的重复硬编码分类兜底。
3. 将两处历史页的 `loadMoreHistory` mock 追加改为真实“无更多数据/空态”分支。
4. 评估 `UserBridge` 与 comment mock fallback 是否可 fail-closed；若会影响登录/评论主语义，则升级。
5. 对高风险暂缓项只保留 inventory，不在本轮直接动。

## 风险声明
- `W4-M02` 只能处理低风险候选；高风险项若进入实施，必须视为重大决策升级。
- `V4-05` 已补齐正向验证证据，后续 carried debt 不得重新引入 Host 风险。
- 所有 mock 清理原子主题必须保留单独 rollback 入口，并在 touched files 维持 detekt / ESLint 零新增问题。
