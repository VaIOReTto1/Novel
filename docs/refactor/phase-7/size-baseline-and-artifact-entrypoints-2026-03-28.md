# Phase 7 Size Baseline And Artifact Entrypoints

## 状态
- 记录日期：`2026-03-28`
- 关联阶段：`Stage 4 / Phase 7`
- 当前结论：`已固定首批 size baseline 与 artifact diff 入口`

## 目的
- 把 `Phase 7` 的包体积治理从“感觉很大”升级为可追溯的 release 产物基线。
- 固定后续做 shrink / diff 时必须使用的产物路径、采样命令与对比对象。

## 采样命令
- 工作目录：`d:/program/Novel/android`
- 产物采样：
  - `./gradlew.bat app:assembleRelease app:bundleRelease --no-daemon --console=plain`
- 产物盘点参考：
  - `android/app/build/outputs/**`
  - `android/app/build/generated/assets/createBundleReleaseJsAndAssets/**`
  - `android/app/build/intermediates/assets/release/mergeReleaseAssets/**`

## 当前基线
- 原始证据：
  - `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-28.json`
- 当前关键产物：

| Item | Path | Size |
| --- | --- | --- |
| Release APK | `android/app/build/outputs/apk/release/app-release.apk` | `98.93 MiB` |
| Release AAB | `android/app/build/outputs/bundle/release/app-release.aab` | `72.60 MiB` |
| Release JS bundle | `android/app/build/generated/assets/createBundleReleaseJsAndAssets/index.android.bundle` | `2.33 MiB` |
| Merged icon fonts total | `android/app/build/intermediates/assets/release/mergeReleaseAssets/fonts/*.ttf` | `3.71 MiB` |
| Merged icon fonts count | `android/app/build/intermediates/assets/release/mergeReleaseAssets/fonts` | `19` files |

## 当前高占比样本
| Asset | Size |
| --- | --- |
| `MaterialCommunityIcons.ttf` | `1.09 MiB` |
| `Ionicons.ttf` | `0.42 MiB` |
| `FontAwesome6_Solid.ttf` | `0.40 MiB` |
| `MaterialIcons.ttf` | `0.34 MiB` |
| `Fontisto.ttf` | `0.30 MiB` |

## 当前解释
- AAB 已比 APK 小约 `26.33 MiB`，说明收缩、拆分与 bundle 分发已经提供了第一层收益。
- `react-native-vector-icons` 进入 release merged assets 后的字体总量约 `3.71 MiB`，已经明显大于当前 JS bundle 的 `2.33 MiB`。
- 当前最大单体字体是 `MaterialCommunityIcons.ttf`，单文件就超过 `1 MiB`，应优先作为后续 subset / audit 样本。
- `mapping.txt` 等输出文件体积较大，但不属于终端用户下载产物，不应与 APK / AAB 直接混算。

## 固定 diff 入口
1. 任何包体积治理都必须同时记录：
   - `app-release.apk`
   - `app-release.aab`
   - `index.android.bundle`
   - `mergeReleaseAssets/fonts/*.ttf`
2. 若后续引入新的资源治理策略，还必须补记录：
   - 新旧产物名
   - 新旧体积
   - 对比日期
   - 变更原因
3. 若只记录 APK 或只记录 AAB，视为不完整的 size diff。

## 后续动作
- 先做字体与 JS/native assets 的低风险清点，再进入第一轮 shrink。
- 后续若要关闭 `V7-01` 之后的体积治理项，至少需要新增一轮“变更前/变更后”对比样本。

## 主要引用
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
- `docs/refactor/evidence/phase7-release-artifact-inventory-2026-03-28.json`
