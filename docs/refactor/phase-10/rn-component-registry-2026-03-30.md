# RN Component Registry

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 10`
- 当前结论：`已固定 RN component registry 宿主`

## Root
- `Novel`

## Registered Components
- `AIWriteAssistantComponent`
- `BecomeWriterPageComponent`
- `BookManagePageComponent`
- `BookshelfPageComponent`
- `CategoryPageComponent`
- `CommentPageComponent`
- `FeedbackHelpMainPageComponent`
- `HelpSupportPageComponent`
- `HistoryPageComponent`
- `MemberCenterPageComponent`
- `MessagePageComponent`
- `MyReservationPageComponent`
- `PrivacyPolicyPageComponent`
- `QuestionDetailPageComponent`
- `QuestionListPageComponent`
- `RecommendBookPageComponent`
- `ReviewDetailPageComponent`
- `SettingsPageComponent`
- `TimedSwitchPageComponent`
- `ViewedUsersPageComponent`
- `WritePageComponent`
- `WriteReviewPageComponent`

## 默认 Owner
- 当前未单独指派组件 owner 时，默认仍是：`当前重构实施者`

## 使用规则
- 新增 `AppRegistry.registerComponent` 时，必须同步更新本注册表。
- 若某个 `componentName` 与 route / bridge 跳转绑定，变更时必须同步更新 schema manifest。
