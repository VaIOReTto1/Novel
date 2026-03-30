# Production Mock And Fallback Backlog

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 11`
- 当前结论：`已固定剩余生产 mock 与 fallback backlog 的当前宿主`

## 当前 backlog 分层
### P0 - 页面主数据仍依赖 mock
- `src/page/comment/ReviewDetailPage/api/reviewDetailApi.ts`
- `src/page/BookshelfPage/pages/Bookshelf/store/bookshelfStore.ts`
- `src/page/BookshelfPage/pages/Watchlist/store/watchlistStore.ts`
- `src/page/BookshelfPage/pages/Community/store/communityStore.ts`
- `src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore.ts`
- `src/page/ScrollBox/MessagePage/store/messageStore.ts`

### P1 - fallback 仍会影响正式语义
- `android/app/src/main/java/com/novel/page/search/repository/SearchRankingRepository.kt`
- `android/app/src/main/java/com/novel/page/read/usecase/LoadBookReviewsUseCase.kt`
- `android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt`
- `android/app/src/main/java/com/novel/utils/network/repository/CachedBookRepository.kt`

### P2 - 需要真实数据源或明确业务策略后才能退场
- `src/page/ScrollBox/BecomeWriterPage/**`
- `src/page/ScrollBox/MemberCenterPage/store/memberCenterStore.ts`
- `src/page/ScrollBox/RecommendBookPage/store/recommendBookStore.ts`
- `src/page/ScrollBox/MyReservationPage/store/myReservationStore.ts`

## 当前结论
- 当前 backlog 已不再是“未知散点”，而是有明确层级和入口。
- `Phase 11` 关闭前不要求这些页面全部接入真实数据源；要求的是：
  - backlog 清单清晰
  - fallback / fail-closed 语义明确
  - 新的生产 mock 不再继续增加

## 主要引用
- `docs/refactor/phase-4/mock-inventory-report.md`
- `docs/refactor/phase-4/production-mock-exit-governance-2026-03-27.md`
