# Phase 0 - 稳定测试数据方案

## 1. 目标
- 为 `Phase 2` 的 JVM 单测、Bridge contract tests、Smoke 与 benchmark 提供稳定、可复现、可共享的数据来源。
- 逐步减少对生产路径 mock/fallback 的依赖，避免“功能能跑但测试不可重复”。

## 2. 设计原则
- fixture 数据优先于在线实时数据。
- fake data source 优先于直接 mock 大量 UI 状态。
- 测试数据必须可版本化、可复用、可追溯。
- benchmark 输入必须固定，避免混入随机性。

## 3. 推荐分层

### Fixture 层
- 位置建议：
  - Android：`android/app/src/testFixtures/` 或 `android/app/src/test/resources/`
  - RN：`__fixtures__/` 或 `src/test/fixtures/`
- 内容建议：
  - 首页推荐、榜单、分类、搜索结果样本
  - 书详情、评论、章节内容样本
  - 用户信息、设置、历史记录样本
  - Bridge payload 样本

### Fake Data Source 层
- 建议为以下对象提供 fake 实现：
  - `HomeRepository`
  - `SearchRepository`
  - `ReaderRepository`
  - `UserRepository`
  - `SettingsStore`
  - `BridgeFacade` 或对应桥接协议适配层
- 目的：
  - 让 UseCase/Reducer/JVM tests 能直接依赖固定数据。
  - 让 Smoke 与 benchmark 不必依赖不稳定后端。

### Contract Fixture 层
- 用于 Bridge contract tests：
  - 事件名固定样本
  - Promise 返回字段样本
  - Native -> RN 主题同步样本
  - 阅读、设置、作者、AI 等关键协议样本

## 4. Phase 2 的最小数据集
| 场景 | 推荐数据 |
| --- | --- |
| 首页 Smoke | 固定推荐列表、榜单列表、分类列表 |
| 登录 Smoke | 固定验证码状态、固定登录成功/失败样本 |
| 搜索 Smoke | 固定搜索词、固定结果页、固定榜单页 |
| 阅读器 Smoke | 固定书籍、固定章节内容、固定阅读设置 |
| 设置 Smoke | 固定主题配置、固定缓存大小结果、固定定时切换状态 |
| Bridge Contract | 固定事件名、固定 Promise 返回 schema |

## 5. Benchmark 输入要求
- 基准测试不得依赖在线动态返回数据。
- 首页 benchmark 至少准备：
  - 固定“推荐”页面可见内容
  - 固定榜单切换数据
- 阅读器 benchmark 至少准备：
  - 固定书籍内容
  - 固定章节页数
  - 固定翻页模式
- 福利页 benchmark 若需要纳入：
  - 需准备稳定可控的 URL 或本地测试页

## 6. 当前仓库现状与约束
- 当前生产代码中存在 mock/fallback 数据，但这些并不适合直接作为自动化测试数据基准。
- 当前 Android `src/test` 尚未建立，fixture/fake data 建议先在文档中定稿，Phase 2 再正式落地代码结构。

## 7. 当前结论
- Phase 2 前必须先补齐 fixture 与 fake data 设计，否则单测和 Smoke 将高度依赖真实网络与随机状态。
- 本文可作为 `V0-05` 的一部分证据使用。
