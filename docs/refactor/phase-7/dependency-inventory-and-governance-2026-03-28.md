# Phase 7 Dependency Inventory And Governance

## 状态
- 记录日期：`2026-03-28`
- 关联阶段：`Stage 4 / Phase 7`
- 当前结论：`已固定首批 Gradle / npm dependency inventory 与治理顺序`

## 目的
- 把 `Phase 7` 的依赖治理从“版本分散”升级为可追溯的 inventory。
- 固定后续 catalog / BOM / dependency diff 的入口，避免 shrink 与 build efficiency 先行后再回头找依赖来源。

## 当前证据
- `docs/refactor/evidence/phase7-npm-top-level-2026-03-28.json`
- `docs/refactor/evidence/phase7-app-release-runtime-classpath-2026-03-28.txt`
- `android/build.gradle`
- `android/app/build.gradle`
- `android/gradle.properties`
- `android/gradle/verification-metadata.xml`

## 当前 inventory 摘要
### npm
- `package.json` 当前声明：
  - `dependencies = 19`
  - `devDependencies = 25`
  - 总声明 top-level package = `44`
- 当前 top-level 声明中：
  - exact version = `13`
  - ranged version（`^` / `~`） = `31`
- 这意味着 npm 侧当前仍以浮动版本为主，后续 diff 时必须同时记录：
  - 声明版本
  - 实际解析版本

### Gradle
- 当前不存在 `android/gradle/libs.versions.toml`，version catalog 尚未建立。
- 当前也没有统一 BOM / catalog 作为唯一版本入口，版本仍分散在多个文件中。
- `android/gradle/verification-metadata.xml` 已存在，说明依赖校验有入口，但尚未和 catalog / inventory 连成统一治理链路。

## 当前显式漂移
| Area | Current Fact | Why It Matters |
| --- | --- | --- |
| Kotlin plugin | `android/build.gradle` 中 `kotlinVersion = 2.0.21`，但 `classpath(\"org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25\")` | 根插件版本与 ext 常量不一致，后续 catalog 化前要先收唯一来源 |
| Kotlin runtime | `releaseRuntimeClasspath` 中 `org.jetbrains.kotlin:kotlin-stdlib:2.0.21 -> 2.1.21` | 解析结果已经高于声明口径，说明当前 classpath 存在被传递升级的事实 |
| Hilt | root plugin `2.49`，app runtime/compiler `2.52` | 插件与 runtime/compiler 存在明显版本漂移 |
| Compose | root / app 使用 Kotlin Compose plugin `2.0.21`，同时 app 仍保留 `composeOptions.kotlinCompilerExtensionVersion = 1.5.14` | Compose 编译器相关配置来源分散 |
| Configuration cache | `org.gradle.configuration-cache=false` | build efficiency 治理前必须先知道是“未启用”还是“启用失败” |

## 当前 releaseRuntimeClasspath 采样信号
- `releaseRuntimeClasspath` 已落盘到：
  - `docs/refactor/evidence/phase7-app-release-runtime-classpath-2026-03-28.txt`
- 当前首个高价值信号：
  - `org.jetbrains.kotlin:kotlin-stdlib:2.0.21 -> 2.1.21`
- 这说明即使 repo 里写了版本，最终运行时依赖仍可能被传递依赖或插件链抬升。

## 固定入口
- npm inventory：
  - `npm ls --depth=0 --json`
- Gradle runtime inventory：
  - `android/gradlew.bat :app:dependencies --configuration releaseRuntimeClasspath --console=plain`
- 版本来源盘点：
  - `android/build.gradle`
  - `android/app/build.gradle`
  - `package.json`
  - `android/gradle.properties`

## 治理顺序
1. 先固定 inventory 与单一版本来源，再做 catalog / BOM。
2. 先拆 Gradle 与 npm 两条治理线，再讨论统一 diff 展示。
3. 先解释 Kotlin / Hilt / Compose 的版本漂移，再进入 build efficiency 与 shrink。

## 当前结论
- `V7-02` 所要求的“依赖冗余与治理顺序明确”已经具备第一版基线。
- 当前最优先的依赖治理目标不是立刻升级版本，而是先把版本来源收成唯一入口。

## 主要引用
- `docs/refactor/phases/phase-7-size-dependency-build-governance.md`
- `docs/refactor/tracking/phase-7-8-validation-board.md`
- `docs/refactor/evidence/phase7-npm-top-level-2026-03-28.json`
- `docs/refactor/evidence/phase7-app-release-runtime-classpath-2026-03-28.txt`
