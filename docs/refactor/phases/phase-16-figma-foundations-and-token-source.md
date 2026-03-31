# Phase 16 - Figma 基础系统与 Token 真源

## 目标
- 在 Figma 中建立唯一设计真源，并导出跨端可消费的语义 Token。

## 范围
- `01-基础规范`
- `02-组件规范`
- `03-页面-亮色`
- `04-页面-暗色`
- `05-标注与交付`
- `06-QA 与对账`

## Token 结构
- `color.*`
- `space.*`
- `radius.*`
- `elevation.*`
- `motion.duration.*`
- `motion.curve.*`
- `typography.*`

## 导出链路
- `Figma Variables -> versioned JSON -> Style Dictionary -> LESS / RN / Android`

## 当前产物入口
- [stage7.tokens.json](../../../design-system/source/stage7.tokens.json)
- [style-dictionary.tokens.json](../../../design-system/generated/style-dictionary.tokens.json)
- [tokens.less](../../../design-system/generated/tokens.less)
- [stage7Tokens.ts](../../../src/design-system/tokens/stage7Tokens.ts)
- [stage7_tokens.xml](../../../android/core-ui/src/main/res/values/stage7_tokens.xml)
- [Stage7Tokens.kt](../../../android/core-ui/src/main/java/com/novel/ui/theme/Stage7Tokens.kt)

## 当前状态
- `in_progress`
