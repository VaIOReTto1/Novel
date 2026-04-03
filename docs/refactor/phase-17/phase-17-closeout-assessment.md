# Phase 17 Closeout Assessment

## 当前结论
- `Phase 17 = in_progress`
- 当前状态：`ready_for_closeout_except_review`
- 生效日期：`2026-04-04`
- 所属阶段：`Stage 7`

## 本轮收口内容
- 资产治理宿主已齐：
  - `icon-manifest.json`
  - `media-manifest.json`
  - `illustration-manifest.json`
  - `copyright-ledger.json`
- RN 资产消费基元已齐：
  - `NovelDesignIcon.tsx`
  - `PlaceholderImage.tsx`
  - `PexelsCreditOverlay.tsx`
- Showcase 基建已齐：
  - RN `src/design-system/showcase/NovelDesignShowcase.tsx`
  - web `src/web/webEntryConfig.ts`
  - Android `NovelDesignShowcaseScreen.kt` / `NovelDesignShowcaseModel.kt`
  - Android route `novel_design_showcase`

## 关键验证
- `npm run novel-design:assets`
- `npm run novel-design:assets:check`
- `npm test -- --runInBand __tests__/harness/androidNovelDesignPages.test.js`
- `npm test -- --runInBand __tests__/web/webEntryConfig.test.ts`
- `npm test -- --runInBand __tests__/design-system/NovelDesignShowcase.test.tsx`
- `cd android && ..\\android\\gradlew.bat app:compileDebugAndroidTestKotlin --no-daemon "-Dkotlin.compiler.execution.strategy=in-process" "-Pkotlin.incremental=false" "-Pkapt.incremental.apt=false"`
- [Stage 7 Showcase Runbook](./showcase-runbook.md)

## 当前 blocker
- 当前已具备可运行 showcase 入口，但 web 侧仍是 showcase 页面而非独立 Storybook 站点。

## 关闭条件
- 保持 `copyright-ledger.json` 与实际展示/消费入口同步，不再回退到空账本基线。
- 将 `V17-03` 的“可运行展示基建”维持在可操作 runbook 状态，并在后续需要时再升级为独立 Storybook 宿主。
- 切换 `V17-01`、`V17-02`、`V17-03` 到 `validated`。
