# Phase 1 - 发布、安全与合规治理

## 目标
- 让工程达到“可正式发布、可安全审查、可进入结构重构”的状态。
- 为后续架构收口提供可信的 release、权限、网络和数据库基线。

## 范围
- 构建体系收口。
- Release 生产化。
- 网络安全与 endpoint 配置治理。
- 权限最小化。
- Room 迁移治理。
- WebView 安全策略。
- 签名、环境注入、依赖验证、供应链审计。
- Benchmark 构建链路修正。

## 非目标
- 不在本阶段推进网络层主通路统一。
- 不在本阶段推进大规模功能重构。
- 不在本阶段修改阅读器、首页等业务交互语义。
- 不在本阶段进行模块化拆分。

## 进入条件
- `Phase 0 = validated`。
- 设备矩阵、测量协议、资产清单、禁区清单已存在。
- Kill switch 最小方案已被接受。
- Release 路径关键风险已明确。

## 任务拆解
| 编号 | 任务 | 预期输出 | 对应检验 |
| --- | --- | --- | --- |
| P1.1 | 统一 Gradle/Kotlin/Compose/Hilt 版本来源与插件声明 | 构建配置收口清单 | V1-01 |
| P1.2 | 明确 `debug/release/benchmark` 职责并输出构建矩阵 | Build variant matrix | V1-01 |
| P1.3 | 建立正式 release signing 流程与文档 | 本地、CI、密钥托管、环境变量说明 | V1-08 |
| P1.4 | 启用 `minify`、`shrinkResources`、产物报告 | Release 产物与体积报告 | V1-01 |
| P1.5 | 收口 endpoint 配置 | Release 不含硬编码 `http://`、不在业务代码直写 host/ip | V1-02 |
| P1.6 | 收紧权限 | Manifest 权限矩阵与申请说明 | V1-03 |
| P1.7 | 数据库治理 | `exportSchema`、migration 路线、移除默认 destructive migration | V1-05 |
| P1.8 | 迁移演练矩阵 | 数据库、SharedPreferences、KeyChain/token 升级与中断恢复演练计划 | V1-06 |
| P1.9 | 福利 WebView 安全治理 | 域名白名单、SSL、外链、mixed content 策略 | V1-07 |
| P1.10 | 供应链与依赖验证 | Gradle dependency verification、lockfile 检查、npm audit 风险登记 | V1-08 |
| P1.11 | 修正 benchmark 构建 | 固定 `uiautomator` 版本，确认运行在 release-like 目标 | V1-09 |

## 交付物
- 构建矩阵。
- Release 发布策略。
- Signing/环境配置说明。
- 权限矩阵。
- Endpoint 配置规范。
- Database migration 策略。
- 迁移演练矩阵。
- WebView 安全策略。
- 依赖验证与供应链检查说明。
- Benchmark 环境说明。

## 硬阈值
- Release 构建可独立成功率必须达到 `100%`。
- Release 路径硬编码 `http://` 数量必须降为 `0`。
- Release manifest 中不得存在不合规权限。
- Room 发布路径不得默认 destructive migration。
- WebView 策略必须覆盖 SSL/HTTP/外链三类风险。
- Dependency verification 与 lockfile 校验必须启用。

## 风险与回滚
- 风险：
  - Release 生产化过程可能引入混淆、资源收缩和签名链路问题。
  - endpoint 收口可能影响现有环境兼容性。
  - Room 迁移策略变更可能影响升级稳定性。
  - WebView 安全策略可能改变第三方内容访问行为。
- 回滚：
  - 每个子主题独立原子提交。
  - 保留可切换配置和 kill switch，避免一次性硬切。
  - 若 release 产物不稳定，立即回退到上一个构建配置提交。

## 检验计划
| ID | 检验项 | 预期结果 |
| --- | --- | --- |
| V1-01 | release 构建成功并生成正式产物报告 | 构建与产物可复现 |
| V1-02 | release 路径无硬编码 `http://` | endpoint 配置规范落地 |
| V1-03 | release manifest 权限矩阵通过审查 | 权限最小化完成 |
| V1-04 | `cleartext` 未对 release 全局开放 | 网络安全策略生效 |
| V1-05 | schema 导出开启且 migration 策略完备 | 数据库发布路径可信 |
| V1-06 | 迁移演练矩阵至少完成一轮 | 升级风险可评估 |
| V1-07 | WebView 安全策略被代码和文档双重覆盖 | 域名、SSL、外链、mixed content 规则明确 |
| V1-08 | signing、环境注入、依赖验证均可追溯 | 发布与供应链基础完备 |
| V1-09 | benchmark 目标、依赖版本、执行环境明确 | 性能基线可信 |

## 退出条件
- 全部 `V1-*` 为 `green`。
- 无 blocker 级发布与安全风险。
- 若迁移演练存在已知残缺，不允许关闭 `Phase 1`。

## 负责人
- Owner：待指定
- Reviewer：待指定
- Validator：待指定
- 当前状态：`validated`
