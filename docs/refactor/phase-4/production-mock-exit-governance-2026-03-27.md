# Phase 4 Production Mock Exit Governance

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 4 carried debt 收口`
- 当前结论：`已从 inventory 升级为退出治理工件`

## 目的
- 把 `mock-inventory-report.md` 中“有哪些生产 mock”升级为“哪些已关闭、哪些延期、延期项如何退出”的治理口径。
- 明确后续不允许把剩余 mock debt 继续模糊地写成“后面再看”。
- 为 `gap-analysis` 提供一份能直接回答“剩余 mock 在哪里、为什么还在、怎么退场”的正式入口。

## 当前关闭口径
- `Phase 4` 已按“触达范围收口”完成低风险生产 mock 清理。
- 已关闭的条目继续以 `docs/refactor/phase-4/mock-inventory-report.md` 与 `docs/refactor/phase-4/phase-4-closeout-assessment.md` 为准。
- 本文档只治理仍保留的 carried debt，不把已关闭项重新写回 backlog。

## 剩余 carried debt 一览
| 域 | 路径 | 当前保留行为 | 当前 Owner 面 | 退出前提 | 当前归属 |
| --- | --- | --- | --- | --- | --- |
| Search filters | `android/app/src/main/java/com/novel/page/search/usecase/GetCategoryFiltersUseCase.kt` | 接口失败时退回整套硬编码分类 | `feature-search / data-source governance` | 后端稳定性确认，或接受 fail-closed / 空态契约 | `Phase 3-5 carried debt` |
| Review detail | `src/page/comment/ReviewDetailPage/api/reviewDetailApi.ts` | 评论详情主数据仍由 mock API 驱动 | `RN comment / review detail` | 真实接口接入或明确空态文案与错误态 | `Phase 4 deep mock debt` |
| Author center | `src/page/ScrollBox/BecomeWriterPage/**` | 作者中心与作品区深度依赖 mock | `RN author / AI flow` | 真实数据源、空态契约、写作入口兼容回归 | `Phase 5+` |
| Bookshelf | `src/page/BookshelfPage/pages/Bookshelf/store/bookshelfStore.ts` | 书架主体列表与推荐仍由 mock 驱动 | `RN bookshelf` | 真实列表源与空书架态 | `Phase 5+` |
| Watchlist | `src/page/BookshelfPage/pages/Watchlist/store/watchlistStore.ts` | 追剧列表主体仍由 mock 驱动 | `RN bookshelf` | 真实列表源与空态 | `Phase 5+` |
| Community | `src/page/BookshelfPage/pages/Community/store/communityStore.ts` | 社区主体由 mock 帖子 / 分类供给 | `RN community` | 社区真实接口或明确降级页面 | `Phase 5+` |
| Viewed users | `src/page/ScrollBox/ViewedUsersPage/store/viewedUsersStore.ts` | 推荐用户页主体由 mock 驱动 | `RN social` | 真实推荐源或 fail-closed 方案 | `Phase 5+` |
| Reservation | `src/page/ScrollBox/MyReservationPage/store/myReservationStore.ts` | 预约页主体由 mock 驱动 | `RN reservation` | 真实预约数据源或空态 | `Phase 5+` |
| Member center | `src/page/ScrollBox/MemberCenterPage/store/memberCenterStore.ts` | 会员中心主体由 mock 驱动 | `RN member center` | 真实权益数据或显式静态页策略 | `Phase 5+` |
| Recommend book | `src/page/ScrollBox/RecommendBookPage/store/recommendBookStore.ts` | 投稿推荐页主体由 mock 驱动 | `RN author / recommend` | 真实推荐流或空态 | `Phase 5+` |
| Help center | `src/page/ScrollBox/FeedbackHelpPage/store/feedbackHelpStore.ts` | 帮助中心主体由 mock 驱动 | `RN help center` | 真实 FAQ / 工单源 | `Phase 5+` |
| Message center | `src/page/ScrollBox/MessagePage/store/messageStore.ts` | 消息列表主体由 mock 驱动 | `RN message` | 真实消息流或空态 | `Phase 5+` |

## 退出规则
### 一律禁止
- 在生产路径新增未登记的业务 mock / 假数据分支。
- 用 mock 填洞来伪装真实空态、鉴权失败、接口失败。
- 未补 contract / smoke / 状态说明时直接删掉 mock，导致页面语义失真。

### 允许的退出方式
1. 接入真实数据源。
2. 在用户可接受的前提下改成显式空态 / 错误态。
3. 拆成 debug-only fake data source，并从生产路径移除。

### 每次退出必须补的证据
1. 代码定位与变更说明。
2. 该页的真实空态 / 错误态 / fallback 语义说明。
3. 至少一条 Jest / JVM / smoke 样本。
4. 若影响 route / host path，还要补对应 device 样本或历史证据回链。

## 优先级规则
- `P0`：当前仍会影响主功能真假性的页面主数据 mock。
- `P1`：真实接口已存在，只差替换或空态契约确认。
- `P2`：需要新接口、数据重建或产品语义确认的重页。

## 当前治理判断
- `Search filters` 属于 `P1`，因为当前更多是可用性兜底策略，而不是整页 mock 主数据源。
- `Review detail` 与 `Bookshelf / Community / Message / MemberCenter` 等主体页属于 `P0-P1`，因为它们仍会直接影响“页面是否真实”。
- `Author center` 等写作链路属于 `P2`，因为退出 mock 前需要更完整的业务语义确认。

## 与 Phase 5 的关系
- 这份文档不要求 `Phase 5` 把所有 RN heavy pages 一次性清零。
- `Phase 5` 只需要确保：
  - 剩余 mock 有清单、有 owner 面、有退出前提
  - 模块化或宿主治理不会重新引入新的生产 mock
- 真正的大规模 mock 退场，仍应按数据源、页面群或业务域拆成后续专项。

## 关闭结论
- 自 `2026-03-27` 起，`生产路径去 mock` 不再只有 inventory，也有明确的退出治理规则。
- `gap-analysis` 后续可把这条从“只有部分实现”提升为“治理工件已补齐，但业务 debt 仍在”。

## 主要引用
- `docs/refactor/phase-4/mock-inventory-report.md`
- `docs/refactor/phase-4/phase-4-closeout-assessment.md`
- `docs/refactor/phase-4/phase-5-entry-checklist.md`
- `docs/refactor/tracking/phase-3-4-validation-board.md`
