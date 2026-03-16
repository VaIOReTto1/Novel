# 第二阶段重构计划（Stage 2 = Phase 3-4）

## 摘要
- 第二阶段定义为 `Stage 2 = Phase 3 + Phase 4`。
- `Phase 3`：基础设施收口。
- `Phase 4`：边界收口与超大类拆分。
- 第二阶段不包含 `Phase 5` 的真正 Gradle 模块化，不做 Reader 最终大拆，不做大规模 UI/功能语义调整。

## 阶段定位
- 第一阶段完成的是基线、发布安全与质量门禁。
- 第二阶段的目标不是继续堆门禁，而是把当前“多套系统并存”的状态收成“单一主系统 + 明确兼容层”，并在单 `app` 模块内完成逻辑模块化准备。
- 第二阶段继续坚持：
  - 不改 UI 语义
  - 不改业务功能语义
  - 不打断业务节奏
  - 全程可逆

## 进入条件
- 第一阶段必须先从 `ready_for_validation` 正式切换到 `validated`。
- 必须同步更新：
  - `docs/refactor/README.md`
  - `docs/refactor/stage-1-phase-0-2-summary.md`
  - `docs/refactor/tracking/phase-0-2-validation-board.md`
  - `docs/refactor/tracking/decision-log.md`
- 第一阶段现有 blocking 回归命令持续可执行：
  - `npm test -- --runInBand`
  - `android/gradlew app:testDebugUnitTest`
  - `android/gradlew app:lintDebug`
  - `android/gradlew app:compileDebugAndroidTestKotlin`
  - `android/gradlew app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=...`
  - `android/gradlew :macrobenchmark:assemble`

## 文档结构
- `docs/refactor/stage-2-phase-3-4-plan.md`
- `docs/refactor/phases/phase-3-infra-consolidation.md`
- `docs/refactor/phases/phase-4-boundary-and-class-split.md`
- `docs/refactor/tracking/phase-3-4-validation-board.md`
- `docs/refactor/tracking/stage-2-static-debt-baseline.md`
- `docs/refactor/stage-2-closeout-summary.md`
- 继续复用：
  - `docs/refactor/tracking/decision-log.md`
  - `docs/refactor/README.md`

## 关键边界
### Phase 3：基础设施收口
- 网络主通路统一
- 协程模型统一
- 存储通路统一
- 错误模型统一
- 日志与观测统一
- 第二阶段静态债基线建立与第一轮收敛

### Phase 4：边界收口与超大类拆分
- 包内逻辑模块化
- Bridge 收口到 `BridgeFacade`
- 超大类拆分
- 生产路径去 mock
- `profile-host / RN Host` 风险验证补齐
- 第二阶段静态债持续收敛

### Reader 限制
- 第二阶段 Reader 只允许：
  - 接入统一网络/存储/错误模型
  - 抽离 helper、mapping、settings/history 边界
  - 做有限减重
- 第二阶段 Reader 不允许：
  - 分页算法重写
  - 翻页核心行为重写
  - Reader 最终 Gradle 模块拆分
  - Reader 对外协议语义变更

## 核心接口与结构变化
- `NetworkFacade`
- `LegacyApiServiceAdapter`
- `StorageFacade`
- `SecureTokenStore`
- `AppError`
- `DataResult<T>`
- `BridgeFacade`
- `NavigationBridgeDelegate`
- `SettingsBridgeDelegate`
- `UserBridgeDelegate`

可选第二批 delegate，仅在 Phase 4 后段纳入：
- `HistoryBridgeDelegate`
- `AuthorBridgeDelegate`
- `AiBridgeDelegate`

兼容原则固定：
- route 语义不变
- Bridge event 名不变
- payload 字段名不变
- RN 组件名不变
- 旧实现先经适配层保留，再逐步退场

## 静态债策略
- `RN lint / detekt` 作为第二阶段主线治理项纳入。
- 第二阶段目标是“显著收敛”，不是“一次性全仓清零”。
- 固定规则：
  - touched files 的 ESLint error = `0`
  - touched files 的 detekt issue = `0`
  - repo 级债务以 `stage-2-static-debt-baseline.md` 为基线计算

## 测试与回归
- 第二阶段全程必须覆盖：
  - 首页加载、榜单切换、分类推荐
  - 登录流程与登录/注册切换
  - 搜索首页、搜索结果进入
  - 阅读器初始化、翻页、设置变更、评论入口
  - 福利 WebView
  - 设置页、书架页、RN Host 页面挂载
  - Bridge contract 回归
  - SharedPreferences 兼容读取与 `DataStore` 试点迁移
  - 旧网络壳与新主栈兼容切换

## 阶段退出条件
- `Phase 3`、`Phase 4` 的全部验证项达到 `green`
- 第二阶段核心功能与 UI 语义零变化
- rollback / kill switch 有证据可执行
- 静态债达到阶段阈值
- 第二阶段文档闭环完成：
  - `docs/refactor/README.md`
  - `docs/refactor/stage-2-phase-3-4-plan.md`
  - `docs/refactor/phases/phase-3-infra-consolidation.md`
  - `docs/refactor/phases/phase-4-boundary-and-class-split.md`
  - `docs/refactor/tracking/phase-3-4-validation-board.md`
  - `docs/refactor/stage-2-closeout-summary.md`
  - `docs/refactor/tracking/decision-log.md`

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`planned`
