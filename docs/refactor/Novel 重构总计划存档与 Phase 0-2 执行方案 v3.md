# Novel 重构总计划存档与 Phase 0-2 执行方案 v3

## 摘要
- 先落地两类文档：`总重构计划` 和 `Phase 0-2 详细执行/检验计划`，统一存放到 `docs/refactor/`。
- 继续采用严格门禁：`Phase 0 validated -> Phase 1 -> validated -> Phase 2 -> validated`，阶段间不允许核心改动重叠。
- 第一阶段计划已补齐之前 review 中的缺口：硬阈值、回滚/kill switch、稳定测试数据、供应链与签名治理、设备矩阵、迁移演练矩阵、责任角色、Android 专项门禁。
- 后续真正开始实施时，按“单主题、可回退、可验证”的原子化提交推进；每次提交后立即用中文 commit message 提交 Git。

## 文档落盘方案
- 新建目录：`docs/refactor/`
- 新建文件：`docs/refactor/README.md`
  - 作为总入口，只放当前阶段、阶段状态、最近结论、下一步、文档索引。
- 新建文件：`docs/refactor/master-roadmap.md`
  - 存放完整总重构计划，内容包括：全局目标、重构原则、阶段总览、长期不做项、全局风险、阶段依赖、原子提交策略。
- 新建文件：`docs/refactor/phases/phase-0-foundation.md`
  - 存放 Phase 0 完整执行计划、交付物、风险、验证项、达标阈值。
- 新建文件：`docs/refactor/phases/phase-1-release-security.md`
  - 存放 Phase 1 完整执行计划、交付物、风险、验证项、达标阈值。
- 新建文件：`docs/refactor/phases/phase-2-quality-gates.md`
  - 存放 Phase 2 完整执行计划、交付物、风险、验证项、达标阈值。
- 新建文件：`docs/refactor/tracking/phase-0-2-validation-board.md`
  - 统一检验看板，所有验证项按 ID 记录 `Expected / Evidence / Actual / Status / Analysis / Validator / Date`。
- 新建文件：`docs/refactor/tracking/decision-log.md`
  - 记录偏差、延期、遗留项、是否允许进入下一阶段、原因和负责人。
- 新建文件：`docs/refactor/tracking/atomic-commit-guide.md`
  - 记录原子改动规则、提交粒度和中文 commit 规范，作为后续实施约束。

## 文档固定格式
- 每个阶段文档固定区块：
  - `目标`
  - `范围`
  - `非目标`
  - `进入条件`
  - `任务拆解`
  - `交付物`
  - `硬阈值`
  - `风险与回滚`
  - `检验计划`
  - `退出条件`
  - `负责人`
- 任务编号固定：
  - `P0.x`
  - `P1.x`
  - `P2.x`
- 验证编号固定：
  - `V0-xx`
  - `V1-xx`
  - `V2-xx`
- 状态枚举固定：
  - `planned`
  - `in_progress`
  - `blocked`
  - `ready_for_validation`
  - `validated`
  - `not_met`
  - `deferred`

## 总重构计划内容要求
- 总计划文档必须包含：
  - 当前项目混合架构现状总结
  - 主要问题分层：架构、性能、依赖、包体积、安全、维护、测试、发布、可观测性、合规
  - 全局目标：不影响 UI、不影响功能、不影响业务节奏
  - 阶段总览：Phase 0 到长期阶段
  - 全局原则：先护栏后重构、先收口后拆分、先稳定核心链路后做高级优化、全程可逆
  - 全局补充优化点：
    - 无障碍
    - 电量/热量
    - 进程重建
    - 低内存恢复
    - WebView 合规
    - 供应链安全
    - feature flag / kill switch
    - 桥接协议版本化
    - 可观测性
    - 团队治理
- 总计划必须明确：
  - 本轮不做 KMP
  - 不做全面 RN->Compose 重写
  - 不做大 UI 改版
  - Reader 模块最后拆

## Phase 0 详细计划
### 目标
- 建立可测量、可比较、可回滚的基线与控制面板，为 Phase 1 和 Phase 2 提供唯一判断依据。

### 范围
- 核心用户路径矩阵
- Route / Bridge / Storage / Config / Schema 资产盘点
- 构建、体积、性能、测试现状基线
- 设备矩阵与测量协议
- 风险图谱与禁区清单
- 稳定测试数据方案设计
- Feature flag / kill switch 最小方案设计

### 进入条件
- 当前仓库可正常读取
- Android 构建配置已可分析
- `docs/refactor/` 文档骨架已创建

### 任务拆解
- `P0.1` 输出核心路径矩阵
  - 首页、书详情、阅读器、登录、搜索、福利、设置、分类、书架、我的、作者、AI 页面
- `P0.2` 输出资产清单
  - Compose routes
  - RN 组件名
  - Bridge 事件名
  - Promise 返回字段
  - 本地存储 key
  - 数据库实体、version、迁移现状
  - release/debug/benchmark 配置
- `P0.3` 输出设备矩阵和测量协议
  - 低端、中端、高端机各 1 台
  - Android 版本至少覆盖 2 个主版本
  - 网络环境：正常网、弱网、离线
  - 测量轮次：冷启动 5 次、滚动 3 次、阅读器翻页 3 次
- `P0.4` 输出性能与体积基线
  - 冷启动
  - 首帧
  - 首页滚动
  - 阅读器翻页
  - RN 首开
  - AAB/APK 体积
  - 构建时长
  - 内存峰值
- `P0.5` 输出测试缺口与 CI 缺口
- `P0.6` 输出重构禁区清单
  - 不允许直接改 route 语义
  - 不允许直接改 Bridge payload 字段
  - 不允许先改 UI 再补验证
- `P0.7` 输出稳定测试数据方案
  - fake repository
  - fixture 数据
  - 固定 benchmark 输入
  - 可复现 smoke 数据
- `P0.8` 输出 feature flag / kill switch 最小方案
  - 网络栈切换
  - Bridge 路径切换
  - WebView 安全策略开关
  - 预热策略开关

### 交付物
- 核心路径矩阵
- 资产清单
- 设备矩阵
- 测量协议
- 性能/体积基线表
- 风险与禁区清单
- 测试数据方案
- Kill switch 方案

### 硬阈值
- 所有核心路径必须覆盖率 `100%`
- 资产清单核心对象覆盖率 `100%`
- 至少完成一轮可复现基线采集
- 每项基线必须记录设备、系统、网络、构建类型、时间

### 检验计划
- `V0-01` 核心路径矩阵完整
- `V0-02` route/bridge/storage/schema/config 清单有代码定位
- `V0-03` 基线数据可复现且有测量协议
- `V0-04` 风险按 blocker/high/medium/low 分级
- `V0-05` 测试数据方案与 kill switch 方案已定稿
- `V0-06` Phase 1 进入条件明确且可客观判断

### 退出条件
- 全部 `V0-*` 至少为 `green`
- 不存在 blocker 级未知项
- 若存在 high 级遗留，必须写入 `decision-log.md`

## Phase 1 详细计划
### 目标
- 让工程达到“可正式发布、可安全审查、可进入结构重构”的状态。

### 范围
- 构建体系收口
- Release 生产化
- 网络安全与 endpoint 配置治理
- 权限最小化
- Room 迁移治理
- WebView 安全策略
- 签名、环境注入、依赖验证、供应链审计
- Benchmark 构建链路修正

### 进入条件
- Phase 0 已 `validated`
- 设备矩阵、测量协议、资产清单、禁区清单已存在
- Kill switch 最小方案已被接受

### 任务拆解
- `P1.1` 统一 Gradle/Kotlin/Compose/Hilt 版本来源与插件声明
- `P1.2` 明确 `debug/release/benchmark` 职责并输出构建矩阵
- `P1.3` 建立正式 release signing 流程与文档
  - 本地开发
  - CI
  - 密钥托管
  - 环境变量约定
- `P1.4` 启用 `minify`、`shrinkResources`、产物报告
- `P1.5` 收口 endpoint 配置
  - 不允许 release 内硬编码 `http://`
  - 不允许业务代码直写 host/ip
- `P1.6` 收紧权限
  - 去除不合规权限
  - 建立权限矩阵和申请说明
- `P1.7` 数据库治理
  - 开启 `exportSchema`
  - 停止默认 destructive migration
  - 输出 migration 路线
- `P1.8` 迁移演练矩阵
  - 旧数据库升级
  - SharedPreferences 升级
  - KeyChain/token 状态升级
  - 升级中断恢复
- `P1.9` 福利 WebView 安全治理
  - 域名白名单
  - SSL 错误策略
  - 外链策略
  - mixed content 策略
- `P1.10` 供应链与依赖验证
  - Gradle dependency verification
  - lockfile 检查
  - npm audit/依赖风险登记
- `P1.11` 修正 benchmark 构建
  - 固定 `uiautomator` 版本
  - 确认运行在 release-like 目标

### 交付物
- 构建矩阵
- Release 发布策略
- Signing/环境配置说明
- 权限矩阵
- Endpoint 配置规范
- Database migration 策略
- 迁移演练矩阵
- WebView 安全策略
- 依赖验证与供应链检查说明
- Benchmark 环境说明

### 硬阈值
- Release 构建可独立成功率 `100%`
- Release 路径硬编码 `http://` 数量必须降为 `0`
- Release manifest 中不应存在不合规权限
- Room 发布路径不允许默认 destructive migration
- WebView 策略必须覆盖 SSL/HTTP/外链三类风险
- Dependency verification 与 lockfile 校验必须启用

### 检验计划
- `V1-01` release 构建成功并生成正式产物报告
- `V1-02` release 路径无硬编码 `http://`
- `V1-03` release manifest 权限矩阵通过审查
- `V1-04` `cleartext` 未对 release 全局开放
- `V1-05` schema 导出开启且 migration 策略完备
- `V1-06` 迁移演练矩阵至少完成一轮
- `V1-07` WebView 安全策略被代码和文档双重覆盖
- `V1-08` signing、环境注入、依赖验证均可追溯
- `V1-09` benchmark 目标、依赖版本、执行环境明确

### 退出条件
- 全部 `V1-*` 为 `green`
- 无 blocker 级发布与安全风险
- 若迁移演练存在已知残缺，不允许关闭 Phase 1

## Phase 2 详细计划
### 目标
- 建立真实可阻断的质量门禁，为后续架构收口和模块拆分提供护栏。

### 范围
- 测试分层
- JVM 单测
- Smoke 套件
- Bridge contract tests
- 固定 fixtures / fake data source
- CI 工作流
- PR 质量门禁
- Benchmark/size diff 报告
- Android Lint / 安全扫描 / manifest 检查

### 进入条件
- Phase 1 已 `validated`
- Release 和 benchmark 基线可信
- 测试数据方案已定稿

### 任务拆解
- `P2.1` 建立 `src/test` 正式源集
- `P2.2` 建立首批 JVM 单测
  - reducer
  - usecase
  - repository
  - config/security mapper
- `P2.3` 建立固定 fixture 与 fake data source
- `P2.4` 建立 Bridge contract tests
  - event name
  - payload schema
  - 关键 promise 字段
- `P2.5` 建立 smoke 套件
  - 首页
  - 登录
  - 搜索
  - 阅读器
  - 设置
- `P2.6` 建立 CI 工作流
  - assemble
  - detekt
  - Android Lint
  - JVM tests
  - RN lint/test
  - android smoke
  - benchmark smoke
  - size diff
  - manifest/security scan
- `P2.7` 建立 flake 处理规则
  - benchmark flake
  - ui smoke flake
  - 重试策略
  - 阻断策略
- `P2.8` 建立 PR 质量门禁与责任分工
  - Owner
  - Reviewer
  - Validator

### 交付物
- 测试分层文档
- Fixture / fake data 方案
- JVM 测试清单
- Smoke 清单
- Bridge contract 清单
- CI 工作流说明
- 门禁说明
- Flake 处理规范

### 硬阈值
- 首批 JVM 单测通过率 `100%`
- smoke 通过率 `100%`
- PR 核心门禁阻断能力 `100%`
- benchmark 回退允许阈值：
  - 冷启动不得回退超过 `5%`
  - 首页滚动帧时间不得回退超过 `5%`
  - 阅读器翻页指标不得回退超过 `5%`
- 包体积阈值：
  - Phase 2 期间 AAB 不得无解释增长超过 `2%`
- 测试 flake rate 上限：
  - smoke `<= 2%`
  - benchmark `<= 5%`

### 检验计划
- `V2-01` `src/test` 已启用并纳入构建
- `V2-02` 首批 reducer/usecase/repository JVM tests 可稳定运行
- `V2-03` fixtures/fake data 可复现
- `V2-04` Bridge contract tests 覆盖关键协议
- `V2-05` smoke 套件覆盖首页/登录/搜索/阅读器/设置
- `V2-06` CI 包含 assemble、detekt、lint、JVM tests、RN lint/test
- `V2-07` PR 有阻断型门禁，不再只有 label workflow
- `V2-08` size diff、benchmark diff、截图/录屏证据归档格式固定
- `V2-09` flake 规则可执行且有责任人

### 退出条件
- 全部 `V2-*` 为 `green`
- 后续结构重构所需核心门禁全部在线
- 若 smoke 或 contract tests 仍不稳定，Phase 2 不关闭

## 检验看板规则
- 看板每条记录字段固定：
  - `ID`
  - `Phase`
  - `Item`
  - `Expected`
  - `Evidence`
  - `Actual`
  - `Status`
  - `Result Analysis`
  - `Owner`
  - `Validator`
  - `Validated On`
- `Result Analysis` 颜色定义：
  - `green`
  - `yellow`
  - `red`
- 阶段状态规则：
  - 任意 `red` -> 阶段只能是 `blocked` 或 `not_met`
  - 存在 `yellow` 且无 `red` -> 阶段只能是 `ready_for_validation` 或 `deferred`
  - 全部关键项 `green` -> 阶段可标记 `validated`

## 原子提交策略
- 每次提交只做一类变化：
  - 只建文档
  - 只建目录和索引
  - 只补一类基线文档
  - 只补一类构建治理
  - 只补一类测试设施
- 一个 commit 不混入：
  - 文档结构 + 构建逻辑 + 测试设施 + 业务改动
- 每次原子提交后必须：
  - 更新对应文档状态
  - 记录对应验证项
  - 立即 Git commit
- 中文 commit message 规范：
  - `docs: 新增重构总计划与阶段文档`
  - `docs: 补充 Phase 0 基线与验证看板`
  - `build: 收口 Android release 构建配置`
  - `security: 收紧网络与权限策略`
  - `test: 建立 Android JVM 单测基础设施`

## 责任角色
- 每个阶段文档必须写明：
  - `Owner`
  - `Reviewer`
  - `Validator`
- 默认责任划分：
  - Owner：实施者
  - Reviewer：方案与代码评审者
  - Validator：阶段关闭批准者
- 若角色未定，阶段状态不得进入 `ready_for_validation`

## 执行顺序
1. 写入 `docs/refactor/` 总入口与总计划
2. 写入 `Phase 0/1/2` 三个阶段文档
3. 写入统一检验看板与决策日志
4. 写入原子提交规范
5. 真正开始 Phase 0
6. Phase 0 验证通过后再进入 Phase 1
7. Phase 1 验证通过后再进入 Phase 2

## 假设
- 当前仍处于计划阶段，本轮只产出可直接实施的最终方案，不执行文件写入或代码改动。
- 离开计划阶段后，按照以上顺序实际创建文件并开始 Phase 0，且每个原子改动后立即提交中文 Git commit。
