# Novel 超大型 Android 重构执行蓝图 v2

## 摘要
- 本次重构按“超大型项目治理”标准设计，核心目标不是追求短期代码漂亮，而是在 **不影响现有 UI、不影响既有功能、不影响业务节奏** 的前提下，把项目逐步改造成一个可长期演进、可安全发布、可稳定扩展、可量化优化的 Android 混合架构工程。
- 总体原则固定为四条：
  1. **先护栏，后重构**：没有测试、监控、回滚、基线，就不进入大规模结构调整。
  2. **先收口，后拆分**：先统一规范和边界，再做模块化，否则只会把混乱复制到更多模块。
  3. **先稳定核心链路，后做高级优化**：优先处理启动、阅读器、导航、Bridge、网络、发布安全等主路径。
  4. **全程可逆、渐进替换**：所有核心变更都必须支持适配层、双轨期、灰度验证和回滚。

## 全局治理原则
- 重构期间冻结以下用户可感知行为：
  - 首页主布局、书详情主布局、阅读器交互模式、登录流程、搜索主流程、福利页入口、RN 页面跳转语义。
- 重构期间冻结以下协议：
  - Compose route 语义不变。
  - RN Bridge 事件名、核心 payload 字段名不变。
  - 版本号策略不变，只优化实现方式。
- 所有大改都必须满足：
  - 有前后基线数据。
  - 有自动化验证。
  - 有回滚路径。
  - 有 ADR/设计文档。
  - 有迁移适配期。
- 默认采用“主干持续集成 + 小步提交 + 分阶段合并”，不做长期超级分支。

## 现状复盘与核心问题
- 项目优点：
  - 混合架构方向清晰，Android 原生承担高性能与复杂交互页面，RN 承担大量业务页，路线本身合理。
  - 已接入 Hilt、MVI、Room、Paging3、Baseline Profile、Macrobenchmark、性能监控、缓存、加密存储，说明已有较强工程意识。
  - 阅读器、首页、Bridge、缓存、网络优化已经形成一定基础，不是从零开始。
- 当前主要问题：
  - **架构收口不足**：Hilt 网络栈与手写 `RetrofitClient/ApiService` 并存，主通路不唯一。
  - **复杂度过高**：`ReaderViewModel`、`NavigationBridgeModule`、`NetworkCacheManager`、`HomeViewModel` 等超大类已超过安全维护范围。
  - **发布不生产化**：release 仍接近调试态，未开启完整 shrink/minify，签名和安全策略不够正式。
  - **安全与合规不足**：全局 cleartext、硬编码 `http://`、权限过宽、证书锁定未真正配置。
  - **数据可靠性不足**：Room 使用 destructive migration，schema 不导出；生产路径混入 mock/fallback 数据。
  - **测试护栏薄弱**：几乎没有 JVM 单元测试，CI 基本缺失，androidTest 有但覆盖面与稳定性不够。
  - **包体积与资源浪费明显**：字体资源异常大，资源压缩与拆分策略未系统化。
  - **文档领先实现**：README 中部分“已完成能力”与真实工程成熟度并不完全一致。
- 额外补充的潜在问题点：
  - 无障碍与大字体适配未见系统化验证。
  - 电量、热量、后台任务、WebView 生命周期风险未纳入统一指标。
  - Bridge 契约无版本化管理，RN/Native 双端协作成本会越来越高。
  - 缺少 feature flag、灰度发布、线上快速止损机制。
  - 依赖治理缺少 catalog/BOM/漏洞扫描/变更审计。
  - 缺少 crash、ANR、卡顿、网络质量、包体积、启动速度的持续回归体系。

## 目标架构定稿
- 目标分层固定为：
  - `UI(Compose/RN Host)`
  - `ViewModel(MVI/State Adapter)`
  - `UseCase`
  - `Repository`
  - `DataSource(Network/DB/Prefs/File/WebView)`
  - `Core(Common/Bridge/Navigation/Observability/Security)`
- 目标原则固定为：
  - UI 不直接操作 Service/Api/Storage。
  - ViewModel 不直接拼网络请求和缓存策略。
  - Repository 不直接耦合页面状态。
  - Bridge 不直接承载大段业务逻辑。
- 需要新增并逐步收敛到的稳定接口：
  - `NovelApi`
  - `AppNavigator`
  - `BridgeFacade`
  - `UserSettingsStore`
  - `ReaderRepository`
  - `SearchRepository`
  - `HomeRepository`
  - `SecureConfigProvider`
  - `AppError`
  - `DataResult<T>`
- 需要版本化管理的内部协议：
  - Route 常量
  - Bridge event 名称
  - Bundle/Payload schema
  - WebView allowed domains
  - Feature flags
  - Performance metrics 名称

## 分阶段详细计划

### Phase 0 基线建立与重构禁区定义，1-2 周
- 目标：
  - 让项目先“可测量、可比对、可止损”。
- 主要动作：
  - 建立主路径清单：首页启动、首页滚动、书详情进入阅读、章节切换、阅读设置、登录、搜索、福利页加载、RN 页面首开与复开。
  - 建立基线指标：
    - 冷启动时间
    - 首帧时间
    - 首页帧时间
    - 阅读器翻页卡顿
    - RN 页面首开耗时
    - AAB/APK 体积
    - 内存峰值
    - Crash/ANR
    - 网络错误率
  - 建立视觉基线：
    - 关键页面截图基线
    - 关键流程录屏基线
  - 建立契约清单：
    - 所有 route
    - 所有 RN 组件名
    - 所有 Bridge 事件与 Promise 返回字段
    - 关键本地存储 key
  - 输出文档：
    - 架构现状图
    - 数据流图
    - 风险图谱
    - 重构禁区列表
- 退出条件：
  - 所有核心链路都有基线。
  - 所有 route / bridge 语义已盘点。
  - 团队对“哪些不能动、哪些可逐步动”达成一致。

### Phase 1 发布链路、安全与合规优先治理，1-2 周
- 目标：
  - 先把工程从“能跑”提升到“可安全发布”。
- 主要动作：
  - 构建体系整理：
    - 明确 `debug/release/benchmark` 的职责。
    - 统一 Kotlin/Compose/Hilt 插件版本来源。
    - 整理 Gradle 参数，避免重复/冲突配置。
  - Release 生产化：
    - 正式 signing 配置。
    - 启用 R8/minify。
    - 启用 `shrinkResources`。
    - 调整 Proguard/R8 keep 规则。
    - AAB 作为默认产物。
  - 安全治理：
    - 去除 `READ_PRIVILEGED_PHONE_STATE`。
    - 最小化电话权限申请。
    - 移除全局 cleartext，改为 debug 或白名单域名策略。
    - 所有硬编码 `http://`、IP、域名迁入配置层。
    - 真正配置 release 证书锁定。
  - WebView 安全：
    - 白名单域名。
    - SSL 错误处理策略。
    - scheme 跳转拦截。
    - 外部浏览器兜底规范。
  - 数据安全：
    - Room 开启 `exportSchema`。
    - 停止默认 destructive migration。
    - 为当前版本设计正式 migration 路径。
  - 权限合规：
    - 审视手机号读取场景是否真有必要。
    - 隐私政策、权限说明、运行时请求时机统一。
- 退出条件：
  - release 可独立构建并具备生产配置。
  - 关键权限与网络安全策略收口完成。
  - 数据库迁移策略不再依赖清库。

### Phase 2 质量护栏与持续集成，1-2 周
- 目标：
  - 重构前先建立“自动拦截回归”的墙。
- 主要动作：
  - 测试分层定稿：
    - `src/test` 放 JVM 单测。
    - `androidTest` 放集成/UI/benchmark。
    - RN 侧保留现有 Jest 体系。
  - Android 先补的单测对象：
    - Reducer
    - UseCase
    - Repository
    - Data mapper
    - 权限与配置判断逻辑
  - 自动化回归：
    - 首页 smoke
    - 登录 smoke
    - 阅读器 smoke
    - 搜索 smoke
    - RN 设置页 smoke
  - CI 最低门槛：
    - `assemble`
    - `detekt`
    - Android JVM tests
    - RN lint/test
    - androidTest smoke
    - macrobenchmark smoke
    - size diff
  - 引入质量门禁：
    - PR 不允许绕过构建失败
    - 指标退化需显式批准
- 退出条件：
  - 核心逻辑拥有单测基础。
  - 每次合并都有最小自动验证。
  - 重构不再完全依赖人工回归。

### Phase 3 基础设施收口，3-4 周
- 目标：
  - 先把“多套系统并存”收成“一套主系统”。
- 主要动作：
  - 网络层统一：
    - Hilt 注入 Retrofit/OkHttp 作为唯一主栈。
    - `ApiService/RetrofitClient` 降级为适配层。
    - 统一超时、重试、日志、认证、缓存、错误模型。
  - 协程模型统一：
    - 清理 `runBlocking` 和匿名全局 scope。
    - 所有跨层异步都依赖注入 `DispatcherProvider`。
  - 存储层统一：
    - `NovelUserDefaults` 迁往 `DataStore` 抽象。
    - SharedPreferences 做兼容迁移。
    - KeyChain 保留但补恢复策略。
  - 错误模型统一：
    - 网络、缓存、Bridge、权限、存储、WebView 统一进入 `AppError`。
  - 日志与监控统一：
    - 所有敏感信息做脱敏。
    - 统一 trace id / request id 能力。
- 退出条件：
  - 项目存在唯一主数据通路。
  - 旧网络壳已不再新增调用。
  - 跨层异步与错误处理策略一致。

### Phase 4 边界收口与超大类拆分，4-6 周
- 目标：
  - 把“功能还在，但复杂度失控”的状态扭回来。
- 主要动作：
  - 先在单 `app` 模块内做逻辑模块化，不立刻 Gradle 拆模块。
  - Package 目标结构：
    - `core/common`
    - `core/network`
    - `core/storage`
    - `core/security`
    - `core/navigation`
    - `core/bridge`
    - `core/observability`
    - `feature/home`
    - `feature/book`
    - `feature/reader`
    - `feature/search`
    - `feature/login`
    - `feature/welfare`
    - `feature/profile-host`
  - 拆分优先顺序固定：
    1. `NavigationBridgeModule`
    2. `HomeViewModel`
    3. `SearchRepository`
    4. `NetworkCacheManager`
    5. `ReaderViewModel`
  - Bridge 重构：
    - 拆成导航、设置、历史、作者、AI、用户等子 bridge。
    - 统一经 `BridgeFacade` 出口。
    - 增加 schema 校验与兼容字段策略。
  - 生产路径去 mock：
    - 书架、关注、评论、历史、榜单等功能去除生产 mock 数据。
    - mock 只保留 debug/fake data source。
- 退出条件：
  - 超大类被拆成可维护子系统。
  - Native/RN 边界更清晰。
  - 主要业务逻辑不再散落在 UI/Bridge/Service 中。

### Phase 5 真正的模块化演进，3-5 周
- 目标：
  - 在边界稳定后，再做 Gradle 模块拆分，降低编译、依赖、认知复杂度。
- 默认拆分顺序：
  1. `core-common`
  2. `core-network`
  3. `core-storage`
  4. `core-ui`
  5. `core-bridge`
  6. `feature-home`
  7. `feature-search`
  8. `feature-login`
  9. `feature-welfare`
  10. `feature-reader`
- 原则：
  - 阅读器最后拆，因为最复杂、最敏感。
  - 每次只拆 1-2 个模块。
  - 先移动代码，不改行为。
  - 依赖方向必须单向，不允许 feature 互相依赖。
- 同步动作：
  - 引入 module owner。
  - 引入 API surface 审查。
  - 引入构建时间统计。
- 退出条件：
  - core/feature 边界形成。
  - 编译依赖方向稳定。
  - 团队可以并行开发而不频繁互相踩踏。

### Phase 6 性能专项治理，2-4 周
- 目标：
  - 在结构稳定基础上做真实收益优化，而不是前期“边改边猜”。
- 主要动作：
  - 启动性能：
    - 压缩 Application 冷启动任务。
    - RN 上下文预热策略可配置化。
    - 清理首帧前非必要初始化。
  - Compose 性能：
    - 稳定 state 与非稳定 state 拆开。
    - 减少全量状态驱动大树重组。
    - 优化 animation / image / pager / lazy 列表热点。
  - 阅读器专项：
    - 分页、翻页、章节预取、进度保存、评论加载、背景/亮度/字号设置分别压测。
    - 让 Reader 的分页、缓存、设置、历史、评论各有独立服务边界。
  - RN Host 性能：
    - ReactRootView 缓存生命周期规范。
    - Bridge 批量调用/线程切换规范。
    - 首开与复开分开优化。
  - 数据库与缓存性能：
    - 校验索引收益。
    - 复盘 FTS4 是否仍最佳。
    - 观察缓存清理对 IO、内存和电量影响。
- 退出条件：
  - 启动、滚动、翻页、首开等核心指标全部优于基线。
  - 优化项都有数据证明，不靠主观感受。

### Phase 7 包体积、依赖与构建效率治理，2-3 周
- 目标：
  - 把“资源和依赖膨胀”变成可管理资产。
- 主要动作：
  - 字体治理：
    - 默认做字体子集化和裁剪。
    - 通过截图基线验证视觉一致。
    - 未经验证不替换字族。
  - 资源治理：
    - 清理未使用资源。
    - 优化图标、mipmap、drawable 重复资源。
    - 校验 WebP/矢量资源策略。
  - 依赖治理：
    - 引入 Version Catalog/BOM。
    - 统一 Compose/AndroidX/OkHttp/Retrofit/Hilt/Room 版本。
    - 移除重复、过时、alpha、未使用依赖。
  - 构建效率：
    - build cache
    - 并行构建
    - configuration cache 兼容性推进
    - KSP/KAPT 负担评估
  - 发布产物治理：
    - AAB 默认
    - ABI 策略按渠道决定
    - 输出 size report
- 退出条件：
  - AAB 体积明显下降。
  - 构建耗时更稳定。
  - 依赖清单可维护、可审计。

### Phase 8 可观测性、线上治理与长期维护，持续阶段
- 目标：
  - 让重构成果能长期保持，而不是半年后重新失控。
- 主要动作：
  - 观测体系：
    - Crash
    - ANR
    - 启动
    - 卡顿
    - 网络成功率
    - WebView 错误
    - Bridge 异常
    - 权限拒绝率
    - 缓存命中率
  - 线上策略：
    - feature flags
    - canary 发布
    - 分阶段 rollout
    - 快速回滚
  - 变更治理：
    - ADR 制度
    - 大型接口变更审查
    - schema 变更审查
    - route/bridge 兼容性审查
  - 团队治理：
    - 模块 owner
    - Code review checklist
    - 性能预算
    - 安全基线
- 退出条件：
  - 项目进入“可持续维护状态”。
  - 后续新增功能不会再次破坏边界和工程质量。

## 补充的遗漏优化点清单
- 无障碍：
  - Compose semantics
  - TalkBack
  - 可点击区域
  - 对比度
  - 字体缩放
  - 阅读器大字号极限验证
- 电量与热量：
  - 阅读器持续阅读场景
  - 章节预取对电量影响
  - WebView 活动页驻留耗电
  - 后台协程与轮询治理
- 稳定性：
  - 进程重建
  - 配置变化
  - 内存回收后恢复
  - RN context 丢失恢复
- 业务连续性：
  - 用户设置导入导出
  - 历史记录恢复
  - Token 过期与刷新
  - 网络弱网/离线体验
- 合规与政策：
  - 权限申请说明
  - 隐私策略同步
  - 敏感日志脱敏
  - WebView 内容来源合规
- 供应链安全：
  - Gradle/plugin 依赖审计
  - npm 依赖审计
  - wrapper 校验
  - lockfile 一致性
- RN/Native 协作成本优化：
  - Bridge schema 文档化
  - RN 组件名集中管理
  - 双端兼容窗口机制
- 数据质量：
  - 生产路径彻底去 mock
  - fallback 策略可观测
  - 异常空态统一
- 可维护性：
  - 统一命名规范
  - 统一目录规范
  - 统一状态模型
  - 统一错误文案与用户提示

## 对外/对内接口与类型调整
- 需要新增的稳定内部类型：
  - `AppError`
  - `DataResult<T>`
  - `BridgeCommand`
  - `BridgeEvent`
  - `RouteSpec`
  - `SecureEndpointConfig`
  - `FeatureFlag`
  - `PerformanceMetric`
- 需要集中管理的内部常量：
  - 路由
  - RN 组件名
  - Bundle keys
  - SharedPreferences/DataStore keys
  - WebView 域名白名单
  - 网络 endpoint 配置
- 需要逐步废弃的实现方式：
  - callback 风格主数据流
  - 生产路径 mock 数据补齐
  - 无约束匿名协程作用域
  - Bridge 内直写业务逻辑
  - 破坏性数据库迁移默认策略

## 测试与验收计划
- 单元测试：
  - Reducer 状态收敛
  - UseCase 业务分支
  - Repository 缓存/降级/错误处理
  - 配置与权限判断
  - 数据转换器
- 集成测试：
  - 网络失败 + 缓存兜底
  - Token 读取与认证
  - 数据库迁移
  - Bridge 调用往返
  - WebView 生命周期
- UI/端到端测试：
  - 首页
  - 搜索
  - 登录
  - 书详情
  - 阅读器
  - 福利页
  - 设置页
  - RN 宿主页
- 性能测试：
  - 冷启动
  - 首页滚动
  - 阅读器翻页
  - RN 页面首开
  - AAB 体积
  - 内存峰值
- 发布前验收：
  - 截图 diff 无异常
  - 主流程录屏对照通过
  - benchmark 不退化
  - Crash/ANR 基线不恶化

## 阶段门禁与回滚策略
- 每个阶段进入前必须有：
  - 基线
  - 任务边界
  - 风险评估
  - 回滚点
- 每个阶段完成后必须通过：
  - 自动化测试
  - 关键路径人工回归
  - 性能/体积对比
  - 文档更新
- 回滚策略固定为：
  - 关键协议不一次性改名
  - 使用适配层保留旧调用入口
  - 新旧实现可并存一段时间
  - 必要时通过 feature flag 回退到旧路径

## 默认实施顺序与资源假设
- 默认建议顺序：
  1. Phase 0
  2. Phase 1
  3. Phase 2
  4. Phase 3
  5. Phase 4
  6. Phase 5
  7. Phase 6
  8. Phase 7
  9. Phase 8
- 默认总周期：
  - `14-24` 周，取决于人力与业务插入频率。
- 默认人力模型：
  - Android 主力 1-2 人
  - RN 配合 1 人
  - 测试/产品按阶段参与验收
- 默认不在本轮做的事：
  - 全面 RN 改写为 Compose
  - KMP
  - Dynamic Feature
  - 大规模 UI 改版
- 默认第一批最值得立即开始的工作：
  - Phase 0 + Phase 1 + Phase 2
  - 因为这是后续所有重构的安全前提。
