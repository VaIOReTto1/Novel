# Phase 7 First Size Shrink - Vector Icon Font Prune

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 4 / Phase 7`
- 当前结论：`已完成第一轮低风险 size shrink`

## 变更内容
- 在 `android/app/build.gradle` 中显式限制 `react-native-vector-icons` 的字体复制范围：
  - `MaterialIcons.ttf`
  - `Feather.ttf`
- 不再让 release 构建默认复制整套 `19` 个 icon fonts。

## 变更依据
- 当前仓库对 `react-native-vector-icons` 的直接使用只发现：
  - `MaterialIcons`
  - `Feather`
- 当前代码扫描未发现其它 icon family 的直接 import。
- 字体复制逻辑来自 `node_modules/react-native-vector-icons/fonts.gradle`，支持通过 `project.vectoricons.iconFontNames` 限定复制名单。

## 验证命令
- 代码扫描：
  - `rg -n "react-native-vector-icons/" src __tests__ android`
- release 构建：
  - `./gradlew.bat clean app:assembleRelease app:bundleRelease --no-daemon --console=plain`

## 结果对比
- 原始证据：
  - `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-28.json`
- 变更后证据：
  - `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-30.json`
- diff：
  - `docs/refactor/evidence/phase7-size-shrink-diff-2026-03-30.json`

| Item | Before | After | Diff |
| --- | --- | --- | --- |
| `app-release.apk` | `98.93 MiB` | `97.23 MiB` | `-1.70 MiB` |
| `app-release.aab` | `72.60 MiB` | `70.90 MiB` | `-1.70 MiB` |
| `index.android.bundle` | `2.33 MiB` | `2.33 MiB` | `0` |
| merged icon fonts total | `3.71 MiB` | `0.39 MiB` | `-3.32 MiB` |
| merged icon font count | `19` | `2` | `-17` |

## 当前解释
- 这次 shrink 没有触碰 JS bundle、业务逻辑、route、bridge payload 或 release 行为语义。
- 实际收益主要来自移除未使用的 icon font 资产，而不是 JS 逻辑裁剪。
- 字体总量下降幅度明显大于最终 APK / AAB 的下降幅度，说明压缩和打包流程对最终产物存在进一步折叠，但收益已经真实进入最终产物。

## 风险与限制
- 这轮 shrink 的安全前提是当前仓库只直接使用 `MaterialIcons` 与 `Feather`。
- 后续如果新增其它 icon family，必须同步更新 `project.ext.vectoricons.iconFontNames`，否则 release 包会缺字体资产。
- 当前没有引入自动校验“新增 family 时强制补名单”的脚本，后续可以视需要补一层 guard。

## 主要引用
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
- `docs/refactor/evidence/phase7-size-shrink-diff-2026-03-30.json`
