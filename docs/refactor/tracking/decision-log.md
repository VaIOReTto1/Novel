# 偏差与决策日志

## 使用规则
- 所有偏离原计划的调整都必须登记。
- 所有允许带遗留项进入下一阶段的决定都必须登记。
- 所有 blocker 与 high 风险处置方案都必须登记。

## 字段模板
| 日期 | 阶段 | 类型 | 决策/偏差 | 原因 | 影响 | Owner | Reviewer | Validator | 后续动作 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |

## 当前记录
| 日期 | 阶段 | 类型 | 决策/偏差 | 原因 | 影响 | Owner | Reviewer | Validator | 后续动作 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 2026-03-14 | Phase 0 | 初始化 | 建立 `docs/refactor/` 重构文档体系并采用严格门禁 | 避免超大型项目在上下文切换中丢失阶段边界和验证依据 | 后续阶段必须同步更新控制面板与验证看板 | 待指定 | 待指定 | 待指定 | 开始落地 Phase 0 基线与资产盘点 |
| 2026-03-14 | Phase 0 | 风险确认 | RN 首开基线当前受元素级自动化识别能力不足阻塞 | `ReactNativeJNI` 与 `uiautomator dump` 无法稳定给出根宿主页元素证据 | RN 根宿主页白屏保留为高风险遗留，但不阻塞 Phase 0 关闭 | 待指定 | 待指定 | 待指定 | 后续以 release-like 或专项调试方式补采 |
| 2026-03-15 | Phase 0 | 决策 | 采用“书架”作为 RN 宿主页代表首开样本 | 书架与分类同属 RN 页面，且书架在冷启动后首次进入时具备稳定正向渲染截图和日志证据 | `V0-03` 获得可替代的 RN 正向样本，`profile` 白屏降为高风险遗留 | 待指定 | 待指定 | 待指定 | Phase 0 关闭评审时明确带入下一阶段的条件 |
| 2026-03-15 | 阶段切换 | 决策 | 按项目推进指令将 Phase 0 视为已通过并启动 Phase 1 | Phase 0 已形成完整关闭评审材料与绿色验证项 | 控制面板和执行重心切换到 `Phase 1 in_progress` | 待指定 | 待指定 | 待指定 | 开始执行发布、安全与合规治理 |
| 2026-03-15 | Phase 1 | 进展 | 启动权限最小化收口 | 清理 Manifest 中普通应用不应保留的权限，并对旧版电话权限范围做限制 | `V1-03` 进入 `in_progress` | 待指定 | 待指定 | 待指定 | 后续补权限矩阵说明 |
| 2026-03-15 | Phase 1 | 进展 | 启动 endpoint 配置收口脚手架 | 后端 API 和图片 host 散落在 Kotlin 代码中，不利于 release 安全治理 | `V1-02` 进入 `in_progress`，地址统一收口到 `BuildConfig` | 待指定 | 待指定 | 待指定 | 后续补环境注入和 release/debug 分流 |
| 2026-03-15 | Phase 1 | 进展 | 启动 cleartext 策略拆分 | 主资源此前对所有构建类型全局放开 cleartext，不符合发布路径目标 | `V1-04` 进入 `in_progress`，主资源默认关闭 cleartext，debug 单独放开 | 待指定 | 待指定 | 待指定 | 后续对白名单域名继续精细化 |
| 2026-03-15 | Phase 1 | 进展 | 启动 Room 发布路径治理第一步 | 当前数据库 `exportSchema=false` 且 destructive migration 默认开启 | `V1-05` 进入 `in_progress`，schema 导出开启，destructive migration 仅保留给 debug | 待指定 | 待指定 | 待指定 | 后续补迁移策略与演练矩阵 |
| 2026-03-15 | Phase 1 | 进展 | 启动福利页 WebView 安全收口 | 福利页默认 URL 为硬编码 `http`，外链与错误处理存在未完成分支 | `V1-07` 进入 `in_progress`，白名单、外链浏览器打开、默认 URL 配置化开始落地 | 待指定 | 待指定 | 待指定 | 真机验证后继续补异常分支 |
| 2026-03-15 | Phase 1 | 进展 | 将福利页默认测试站点切换为 Bing | 原福利测试地址不可用，影响真机验证 | `V1-07` 的运行验证基准页切换到 `https://www.bing.com/` | 待指定 | 待指定 | 待指定 | 补真机截图与跳转证据 |
| 2026-03-15 | Phase 1 | 进展 | 已完成 Bing 福利页真机运行取证 | 真机日志显示福利页跳转到 `https://cn.bing.com/` 且 `OnPageFinished` 成功触发 | `V1-07` 获得首轮正向运行证据 | 待指定 | 待指定 | 待指定 | 后续可继续补外链与异常路径 |
| 2026-03-15 | Phase 1 | 进展 | 启动 release 生产化脚手架 | 当前 release 仍以接近调试态配置为主，未真正打开压缩与收缩 | `V1-01` 进入 `in_progress` | 待指定 | 待指定 | 待指定 | 继续用 release 构建暴露真实发布风险 |
| 2026-03-15 | Phase 1 | 进展 | release 产物已首次成功构建 | 经过 R8 缺类规则补齐和 network security lint 修正后，`assembleRelease` 成功通过 | `V1-01` 具备正向验证证据，产物与 mapping 已生成 | 待指定 | 待指定 | 待指定 | 后续继续细化签名、资源收缩收益和发布矩阵 |
| 2026-03-15 | Phase 1 | 进展 | 启动 release 签名配置脚手架 | release 仍沿用 debug 签名，缺少正式签名参数读取结构 | `V1-08` 进入 `in_progress`，支持从 Gradle 属性和环境变量读取签名参数 | 待指定 | 待指定 | 待指定 | 后续补签名说明与 CI 注入规范 |
| 2026-03-15 | Phase 1 | 进展 | 已生成 Gradle 依赖校验元数据 | 供应链治理需要可追溯的依赖校验基础 | `V1-08` 从“只有签名脚手架”推进到“签名脚手架 + Gradle 校验” | 待指定 | 待指定 | 待指定 | 后续继续补 CI 注入约定与 npm 风险登记 |
| 2026-03-15 | Phase 1 | 进展 | 补齐迁移演练矩阵和首轮实操样本 | 当前数据库、SharedPreferences、KeyChain 的发布路径已开始收口，但缺少统一演练矩阵会导致升级风险不可系统评估 | `V1-06` 已具备矩阵和首轮“重装保留数据”实操证据 | 待指定 | 待指定 | 待指定 | 后续继续补 token 样本与旧 schema 升级演练 |
| 2026-03-15 | Phase 1 | 进展 | 启动并验证 benchmark 环境 | macrobenchmark 模块存在 `uiautomator:<latest>` 这类不稳定依赖声明，且缺少统一环境说明 | `V1-09` 已完成版本固定、环境说明和 `:macrobenchmark:assemble` 构建验证 | 待指定 | 待指定 | 待指定 | 后续在真实设备上补 `connectedCheck` 或 Baseline Profile 生成证据 |
| 2026-03-15 | Phase 1 | 决策 | Phase 1 进入关闭评审准备状态 | 当前 `V1-01 ~ V1-09` 已全部具备绿色证据 | 控制面板、阶段文档与关闭评审文档进入 `ready_for_validation` | 待指定 | 待指定 | 待指定 | 下一步完成 Phase 1 关闭验证并启动 Phase 2 |
| 2026-03-15 | 阶段切换 | 决策 | 按项目推进指令将 Phase 1 视为已通过并启动 Phase 2 | Phase 1 已形成完整关闭评审材料与全部绿色验证项 | 控制面板、阶段文档与执行重心切换到 `Phase 2 in_progress` | 待指定 | 待指定 | 待指定 | 开始执行 JVM 单测基础设施任务 |
| 2026-03-15 | Phase 2 | 进展 | JVM 单测基础设施已落地并完成首轮执行 | `src/test`、测试依赖和首批纯逻辑测试已落地，且 `app:testDebugUnitTest` 已成功通过 | `V2-01` 与 `V2-02` 已具备待验证证据 | 待指定 | 待指定 | 待指定 | 下一步继续补 `V2-03` fixture/fake data 层 |
| 2026-03-15 | Phase 2 | 进展 | fixture/fake data 基础层已落地 | 已将首批样本放入 `src/test/resources/fixtures/`，并提供 `FixtureCatalog` 与 `FakeReaderHistorySource` 作为测试数据入口 | `V2-03` 已具备待验证证据，可继续被 reducer/usecase/repository 测试复用 | 待指定 | 待指定 | 待指定 | 下一步推进 Bridge contract tests 或第二批 JVM 测试 |
| 2026-03-15 | Phase 2 | 进展 | Bridge contract tests 已落地 | 为了在不改动业务逻辑和 UI 的前提下拦截 Native/RN 协议回归，先补齐 UserBridge/NavigationBridge Promise 合同以及 ThemeChanged/WritePageSelectionMenuAction 事件合同 | `V2-04` 进入 `ready_for_validation`，后续可继续推进 smoke 或 CI 门禁 | 待指定 | 待指定 | 待指定 | 下一步优先评估 `V2-05` smoke 套件或 `V2-06` CI 工作流 |
| 2026-03-15 | Phase 2 | 阻塞 | smoke 套件基础层已落地但真机执行被 ADB 环境阻塞 | 已新增首页/登录/搜索/阅读器 Android smoke 与设置 RN smoke，且 RN smoke 与 `compileDebugAndroidTestKotlin` 已通过；但 `connectedDebugAndroidTest` 执行时 `adb devices` 返回空列表 | `V2-05` 目前只能维持 `blocked/yellow`，待当前 shell 识别到设备后补真机证据即可继续关闭 | 待指定 | 待指定 | 待指定 | 重新连接 ADB 设备后重跑 `connectedDebugAndroidTest`，补齐录屏/截图与通过结果 |
| 2026-03-16 | Phase 2 | 进展 | Android/RN smoke 套件已补齐真机与本地证据 | 当前 shell 已重新识别到设备 `192.168.8.130:5555`，随后完成 `connectedDebugAndroidTest` 真机执行；同时再次确认 `SettingsPage.smoke.test.tsx` 通过 | `V2-05` 进入 `ready_for_validation`，Phase 2 的核心路径 smoke 已形成可重复执行证据链 | 待指定 | 待指定 | 待指定 | 后续将 smoke job 在 CI emulator 上稳定化后再考虑升级为 blocking |
| 2026-03-16 | Phase 2 | 进展 | Phase 2 已进入关闭评审准备态 | 核心质量资产已到位：JVM tests、fixture、Bridge contract、smoke、workflow、证据归档、flake 规则、PR 门禁矩阵均已落地并同步看板 | 控制面板状态切换为 `ready_for_validation`，后续将以 Phase 3 前的关闭评审结论作为正式阶段切换依据 | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 进入 Phase 2 关闭评审与后续结构重构准备 |
| 2026-03-15 | Phase 2 | 阻塞 | CI 本地基线已部分跑通，但仍有 lint/detekt 历史债务 | 已补齐 Jest AsyncStorage/reanimated/native UI mocks，`npm test -- --runInBand` 转绿；同时修复 `StateAdapter` 与 `NetworkMonitor` 使 `lintDebug` 通过。Detekt 已从配置阻塞恢复到真实扫描阶段，但当前仍报 `2260 weighted issues`；`npm run lint` 仍暴露大量现存 RN lint debt | `V2-06` 当前可先承接通过的 build/test/lint 子命令，完整阻断型 CI 仍需继续收口 RN lint 与 detekt 代码债务 | 待指定 | 待指定 | 待指定 | 下一步优先决定 detekt/RN lint 采用全量清债还是增量门禁，再落 workflow 阻断策略 |
| 2026-03-15 | Phase 2 | 进展 | 首版 quality workflow 已落地 | 已新增 `.github/workflows/quality-gates.yml`，将本地已验证通过的命令接入 blocking job，同时把 `android-smoke`、`rn-lint`、`app:detekt` 以 observe job 形式上线 | 项目已不再只有 label workflow，`V2-06` 从“无质量工作流”推进到“有阻断 + 有观测”的过渡态 | 待指定 | 待指定 | 待指定 | 后续在 CI 实际运行结果基础上，逐步把 observe job 升级为 blocking |
| 2026-03-15 | Phase 2 | 进展 | 证据归档标准已固定 | 已新增 benchmark diff、size diff、smoke 证据的统一命名、字段、目录和 markdown 模板标准，避免后续出现截图/录屏不可追溯 | `V2-08` 进入 `ready_for_validation`，后续所有 Phase 2 证据需按统一格式归档 | 待指定 | 待指定 | 待指定 | 后续新增 benchmark/size/smoke 证据时强制套用该标准 |
| 2026-03-15 | Phase 2 | 进展 | flake 处理规则已形成初稿 | 已固定 flake 定义、重试上限、阻断策略、升级规则、隔离条件与角色模板，避免后续 CI 通过无限重跑掩盖真实问题 | `V2-09` 已进入规则可执行阶段，但仍需在 CI 实跑后补具体责任人和实际案例 | 待指定 | 待指定 | 待指定 | 后续在 CI 首轮运行结果出来后补充 flake 实例与责任人 |
| 2026-03-16 | Phase 2 | 决策 | 采用“blocking + observe”增量门禁策略关闭 Phase 2 | 当前已有一组本地稳定通过的 build/test/lint 命令，可直接用于阻断 PR；同时 `rn-lint` 与 `detekt` 仍存在大规模历史债，若强行全量阻断会直接压垮当前节奏 | `V2-06` 与 `V2-07` 进入 `ready_for_validation/green`，并把历史债相关 job 保持在线观测，后续逐步升级为 blocking | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 进入 Phase 2 关闭评审，并将下一阶段的清债/结构重构放到后续 roadmap |
| 2026-03-16 | Stage 2 Planning | 决策 | 第二阶段范围固定为 `Phase 3 + Phase 4` | 第一阶段已完成基线、发布安全与质量门禁，下一步最适合先做基础设施收口与边界收口，不直接进入真正 Gradle 模块化 | 第二阶段正式计划、Phase 3/4 详细文档、验证看板与静态债基线文档已落盘到 `docs/refactor/`，供后续实施直接使用 | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 待第一阶段正式切换为 `validated` 后启动第二阶段 |
| 2026-03-16 | 阶段切换 | 决策 | 第一阶段（Phase 0-2）正式关闭为 `validated` | `V0-*`、`V1-*`、`V2-*` 均已形成绿色证据链，且 `Phase 2 closeout assessment` 已给出建议通过结论 | 允许正式进入第二阶段，控制面板与阶段状态切换到 `Phase 3 in_progress` | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 开始执行 `P3.1 / P3.2` 之后的基础设施收口主线 |
| 2026-03-16 | Phase 3 | 进展 | 旧网络壳真实调用矩阵已落盘 | 已对 `ApiService / RetrofitClient` 的调用分布做主路径分级，并明确高风险生产路径、次级兼容路径与初始化依赖路径的迁移顺序 | `V3-01` 从 `planned` 推进到 `in_progress`，后续可直接进入 `NetworkFacade + LegacyApiServiceAdapter` 收口实施 | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 下一步开始建立 `NetworkFacade` 与旧壳兼容适配层 |
| 2026-03-16 | Phase 3 | 进展 | `NetworkFacade + LegacyApiServiceAdapter` 骨架已落地 | 已新增统一网络抽象、旧 `ApiService` 兼容适配层与配套 JVM 单测，且 `app:testDebugUnitTest --tests com.novel.core.network.LegacyApiServiceAdapterTest` 与 `app:testDebugUnitTest` 均已通过 | `P3.4` 已具备继续接入高风险生产路径的最小实现基础 | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 下一步开始迁移 Home / Search / Bridge 高风险调用到主网络抽象 |
| 2026-03-16 | Phase 3 | 进展 | 首个高风险 Bridge 调用已迁入 `NetworkFacade` 主通路 | `NavigationBridgeModule.getHomeBooksHighPriority()` 属于高风险 Bridge 生产路径，适合作为 `P3.5` 的最小切口；为保证本地 JVM 单测稳定运行，网关 JSON 解析采用 `Gson JsonParser` 而不是 Android `org.json` | `V3-01` 获得首个真实迁移样本，`NavigationBridgeNetworkGatewayTest` 与 `app:testDebugUnitTest` 已通过；当前主通路唯一化仍未完成，后续需继续迁移 Home / Search / 其他 Bridge 路径 | 当前重构实施者 | 模块代码评审者 | 阶段门禁批准者 | 下一步扩展 `NetworkFacade` 到 Search / Home 其余高风险调用，并继续保持原子化提交 |
