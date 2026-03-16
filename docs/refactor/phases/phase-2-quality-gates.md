# Phase 2 - 质量门禁与自动化护栏

## 目标
- 建立真实可阻断的质量门禁。
- 为后续架构收口、超大类拆分和模块化提供自动化护栏。

## 范围
- 测试分层。
- JVM 单测。
- Smoke 套件。
- Bridge contract tests。
- 固定 fixtures / fake data source。
- CI 工作流。
- PR 质量门禁。
- Benchmark/size diff 报告。
- Android Lint / 安全扫描 / manifest 检查。

## 非目标
- 不在本阶段推进大量业务逻辑重写。
- 不在本阶段重构所有历史测试代码。
- 不在本阶段处理所有性能优化，仅建立门禁和基线。

## 进入条件
- `Phase 1 = validated`。
- Release 和 benchmark 基线可信。
- 测试数据方案已定稿。
- 关键资产清单和禁区清单已可引用。

## 任务拆解
| 编号 | 任务 | 预期输出 | 对应检验 |
| --- | --- | --- | --- |
| P2.1 | 建立 `src/test` 正式源集 | Android JVM test 基础设施 | V2-01 |
| P2.2 | 建立首批 JVM 单测 | reducer、usecase、repository、config/security mapper 清单与样例 | V2-02 |
| P2.3 | 建立固定 fixture 与 fake data source | 可复现测试数据方案 | V2-03 |
| P2.4 | 建立 Bridge contract tests | event、payload、Promise 字段校验 | V2-04 |
| P2.5 | 建立 smoke 套件 | 首页、登录、搜索、阅读器、设置 smoke | V2-05 |
| P2.6 | 建立 CI 工作流 | assemble、detekt、lint、JVM tests、RN lint/test、android smoke、benchmark smoke、size diff、manifest/security scan | V2-06 |
| P2.7 | 建立 flake 处理规则 | benchmark/UI smoke flake 识别、重试、阻断规则 | V2-09 |
| P2.8 | 建立 PR 质量门禁与责任分工 | Owner、Reviewer、Validator、阻断规则 | V2-07 |
| P2.9 | 固化归档规范 | size diff、benchmark diff、截图/录屏证据模板 | V2-08 |

## 交付物
- 测试分层文档。
- Fixture / fake data 方案。
- JVM 测试清单。
- Smoke 清单。
- Bridge contract 清单。
- CI 工作流说明。
- 门禁说明。
- Flake 处理规范。

## 硬阈值
- 首批 JVM 单测通过率必须达到 `100%`。
- Smoke 通过率必须达到 `100%`。
- PR 核心门禁阻断能力必须达到 `100%`。
- Benchmark 回退允许阈值：
  - 冷启动不得回退超过 `5%`
  - 首页滚动帧时间不得回退超过 `5%`
  - 阅读器翻页指标不得回退超过 `5%`
- 包体积阈值：
  - Phase 2 期间 AAB 不得无解释增长超过 `2%`
- 测试 flake rate 上限：
  - smoke `<= 2%`
  - benchmark `<= 5%`

## 风险与回滚
- 风险：
  - 历史代码测试可注入性不足，导致单测推进不均衡。
  - Smoke 用例在设备、网络和数据环境下不稳定。
  - CI 引入后可能增加短期构建时长和失败率。
- 回滚：
  - 优先以新增测试基础设施、文档、CI 工作流为原子提交。
  - 若某类门禁不稳定，可先标记为观察态，但不得虚假标绿。
  - 若 CI 工作流阻断过强导致开发停滞，必须在 `decision-log.md` 明确记录临时放宽原因与恢复时间。

## 检验计划
| ID | 检验项 | 预期结果 |
| --- | --- | --- |
| V2-01 | `src/test` 已启用并纳入构建 | JVM test 基础设施完成 |
| V2-02 | 首批 reducer/usecase/repository JVM tests 可稳定运行 | 核心逻辑具备基本单测覆盖 |
| V2-03 | fixtures/fake data 可复现 | 测试数据稳定且可共享 |
| V2-04 | Bridge contract tests 覆盖关键协议 | Native/RN 协议更改可被发现 |
| V2-05 | smoke 套件覆盖首页/登录/搜索/阅读器/设置 | 核心回归路径具备自动验证 |
| V2-06 | CI 包含 assemble、detekt、lint、JVM tests、RN lint/test | 最小自动化门禁在线 |
| V2-07 | PR 有阻断型门禁，不再只有 label workflow | 不合格提交无法直接通过 |
| V2-08 | size diff、benchmark diff、截图/录屏证据归档格式固定 | 验证证据统一可追溯 |
| V2-09 | flake 规则可执行且有责任人 | 门禁具备长期维护能力 |

## 退出条件
- 全部 `V2-*` 为 `green`。
- 后续结构重构所需核心门禁全部在线。
- 若 smoke 或 contract tests 仍不稳定，不允许关闭 `Phase 2`。

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`ready_for_validation`
