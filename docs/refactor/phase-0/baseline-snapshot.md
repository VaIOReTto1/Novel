# Phase 0 - 当前基线快照

## 说明
- 本快照记录当前仓库在 `git rev-parse --short HEAD = ad509dd` 时可立即复现的静态基线。
- 这里的“基线”分为两类：
  - 仓库内静态基线：可直接通过代码和目录统计复现
  - 设备实测基线：需要真机/模拟器和固定测量协议
- 当前文件先落第一类，第二类保留待补采项，避免假精确。

## 1. 仓库内静态基线
| 指标 | 当前值 | 说明 |
| --- | --- | --- |
| Android 原生主源码文件数 | `280` | `android/app/src/main/java` 下 Kotlin/Java 文件数量 |
| RN `src` 文件数 | `322` | `src` 目录文件数量 |
| Android `androidTest` 文件数 | `15` | 当前以 UI/集成为主 |
| Android `src/test` 文件数 | `0` | JVM 单测基础设施尚未建立 |
| 显式注册的 RN `*Component` 文件数 | `22` | `src/page/**/**Component.tsx` |
| `AppRegistry.registerComponent` 数量 | `23` | 含根组件 `Novel` |
| Compose route 数量 | `29` | 来自 `NavigationUtil.kt` |
| GitHub workflow 数量 | `1` | 当前仅存在 `label.yml` |
| Android 字体资源总大小 | `76.04 MB` | `android/app/src/main/res/font` |
| Hermes 开关 | `true` | 当前 `android/gradle.properties` |
| RN New Architecture 开关 | `false` | 当前 `android/gradle.properties` |
| Room `exportSchema` | `false` | 当前数据库 schema 未导出 |
| Room destructive migration | `enabled` | 当前默认使用 `fallbackToDestructiveMigration()` |

## 2. 已确认的结构性风险基线
| 项目 | 当前观察 |
| --- | --- |
| Release/安全 | Manifest 与网络安全配置仍需 Phase 1 收口 |
| 测试护栏 | JVM 单测为 `0`，CI workflow 仅 `1` 个且非质量门禁 |
| 包体积 | 字体资源体积异常大，属于后续 Phase 7 的重点对象 |
| Bridge 复杂度 | Bridge 暴露能力多且混杂，后续需收口 |
| 数据库发布可靠性 | Schema 未导出且默认 destructive migration，不符合正式发布要求 |

## 3. 待补采的设备实测基线
已完成的动态采样记录见：
- `docs/refactor/phase-0/dynamic-baseline-run-2026-03-14.md`

| 指标 | 当前状态 | 备注 |
| --- | --- | --- |
| 冷启动 | 已完成首轮采样 | 已在 `DN2101 / Android 13` 上完成 5 次冷启动采样 |
| 首帧 | 待补采 | 需统一构建类型和设备 |
| 首页滚动 | 已完成首轮粗采样 | 已在首页执行 3 次固定滑动并记录 `gfxinfo` |
| 阅读器翻页 | 已完成首轮正文采样 | 已通过第二章 debug 路由完成正文页翻页、`gfxinfo` 与内存采样 |
| RN 页面首开 | 待补采 | 需选定代表页执行 3 次采样 |
| 构建时长 | 已完成首轮采样 | 本地 `app:assembleDebug` 首轮耗时已记录 |
| 内存峰值 | 已完成首轮快照 | 已记录首页启动后的 `TOTAL PSS` / `TOTAL RSS` |

## 4. 当前结论
- Phase 0 已具备第一轮仓库内可复现的静态基线。
- 已补充第一轮真机动态证据，但真正决定后续性能退化判断的完整指标集仍需继续补采。
- 在动态指标完成前，`V0-03` 只能保持 `in_progress`，不能直接关闭。
