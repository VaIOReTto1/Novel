# Phase 13 Wave 1 - Profile / Settings Domain

## 范围
- `ProfilePage`
- `SettingsPage`
- `settingsStore`
- 新增域层 helper 与域层测试

## 本轮落地
- `ProfilePage` 不再直接承载页面预加载细节，改为委派：
  - `bootstrapProfilePageData`
  - `bootstrapProfileUserState`
- `SettingsPage` 不再直接承载初始化与 section 构建细节，改为委派：
  - `bootstrapSettingsPage`
  - `createSettingsSections`
- `settingsStore` 去掉了模块导入即触发的初始化副作用。

## 新增文件
- `src/page/ProfilePage/domain/profileBootstrap.ts`
- `src/page/SettingsPage/settingspage/domain/settingsPageModel.ts`
- `__tests__/domains/profileBootstrap.test.ts`
- `__tests__/domains/settingsPageModel.test.ts`

## 验证
- `npm test -- --runInBand __tests__/domains/profileBootstrap.test.ts __tests__/domains/settingsPageModel.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`

## 当前判断
- 这一波已经把 `Profile + app root preload` 与 `Settings` 域的第一批 page-domain 混杂点抽成可测 helper。
- 但这还不是 `Phase 13` closeout；后续仍需继续推进：
  - `Bookshelf / History / Watchlist / Community`
  - `Comment / ReviewDetail / WriteReview`
  - `Writer / AIWriteAssistant / BookManage`
