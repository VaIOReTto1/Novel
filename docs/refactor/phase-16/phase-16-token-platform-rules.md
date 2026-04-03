# Phase 16 Token Platform Rules

## 目标
- 为 Stage 7 的语义 Token 提供统一的 `dark / a11y / RTL` 落地规则，作为 Figma、RN、Android 三端对齐的最小权威宿主。

## Dark Mode Rules
- 单一主题源仍以 `design-system/source/novel-design.tokens.json` 为准。
- 所有颜色语义必须以 `light / dark` 成对出现，不允许仅提供单侧色值。
- `resolveNovelDesignTheme` 与 `themeStore` 负责运行态切换，不允许页面组件自行推断深浅主题并硬编码替代色值。
- Figma Variables 对齐时，页级 frame 与组件级 token 需保持 `canvas / surface / elevated / text / border / brand / status / interaction / reader` 语义分层。

## Accessibility Rules
- 基础门禁沿用 [Phase 10 accessibility audit matrix](../phase-10/accessibility-audit-matrix-2026-03-30.md)。
- Stage 7 页面换肤默认要求：
  - 关键点击目标最小 `44x44`
  - 关键交互元素必须具备可读 label 或语义描述
  - 字体缩放下主要信息不可被截断
  - 颜色对比不得依赖单一品牌色表达状态
- RN 侧以现有 `design-system`、`smoke`、`settings` 相关测试为最小验证基线；Android 侧以 shared gate 与既有 accessibility 宿主为最小验证基线。

## RTL Rules
- 当前 Stage 7 不新增单独 RTL token 分支；默认沿用语义 spacing / radius / alignment token，并通过布局方向适配实现镜像。
- 导航、top bar、tabs、action row、sheet/dialog 需遵守：
  - leading / trailing 语义优先于 left / right
  - icon + text 组合在 RTL 下允许整体镜像，但品牌 logo 和非对称插画不强制镜像
  - 组件映射与 Figma 标注需记录是否需要 RTL mirror
- 若某组件必须排除镜像，需在 Figma 标注或 closeout 证据中显式说明。

## Mapping Rules
- Figma：Variables / frame annotations / component mapping
- RN：`novelDesignTokens.ts`、`NovelDesignUI`、页面样式层
- Android：`novel_design_tokens.xml`、`NovelDesignTokens.kt`、Compose 页面与共享基元
- 任何平台例外必须写入对应宿主文档，不允许只留在页面局部实现。

## 验证入口
- `npm run novel-design:tokens`
- `npm run novel-design:tokens:check`
- `npm test -- --runInBand __tests__/design-system/NovelDesignUI.test.ts __tests__/design-system/resolveNovelDesignTheme.test.ts`
- `npm test -- --runInBand __tests__/bridge/NativeBridgeEventContracts.test.ts __tests__/smoke/SettingsPage.smoke.test.tsx`
- Android shared gate：`app:testDebugUnitTest app:lintDebug app:compileDebugAndroidTestKotlin :macrobenchmark:assemble`
