# Phase 10 - 无障碍、合规、供应链与双端协作治理

## 目标
- 把现有半成品入口收成统一治理宿主，而不是继续散落在页面实现、日志和历史 phase 文档里。

## 范围
- 无障碍：Compose semantics、TalkBack、点击区域、对比度、字体缩放、Reader 大字号极限验证
- 合规与政策：权限申请说明、隐私策略同步、敏感日志脱敏、WebView 来源合规
- 供应链安全：Gradle / plugin / npm 审计、wrapper 校验、lockfile 一致性
- RN / Native 协作：Bridge schema、RN 组件名集中注册、兼容窗口机制

## 非目标
- 不引入新的线上平台或第三方 SaaS
- 不借治理名义重开模块化或运行时大改
- 不直接改既有对外 Bridge / route 契约

## 当前仓库入口基线
- 无障碍已有局部入口：
  - `WelfareAccessibilityHelper`
  - 登录标题语义
  - 首页分类按钮 `contentDescription`
- 合规/日志已有局部入口：
  - `CoreLogger`
  - `TimberLogger`
  - WebView 来源与无障碍辅助代码
- 供应链已有局部入口：
  - `android/gradle/verification-metadata.xml`
  - `package.json`
  - `yarn.lock`
  - `gradle-wrapper`
- 双端协作已有局部入口：
  - `bridge-schema-compat-governance-2026-03-27.md`
  - `index.js`
  - `AppRegistry.registerComponent(...)`

## 协作编制
### Leader Mode
- `single leader / four helpers`

### Base Helper Count
- `4`

### Agent Roster
- `AccessibilityAuditAgent`
- `CompliancePolicyAgent`
- `SupplyChainAuditAgent`
- `BridgeRegistryAgent`

## 任务拆解
| ID | Task | Expected Outcome |
| --- | --- | --- |
| P10.1 | 固定无障碍审计矩阵 | 页面与检查项可追溯 |
| P10.2 | 固定合规与敏感日志治理口径 | 权限 / 隐私 / WebView / 日志边界明确 |
| P10.3 | 固定供应链审计 playbook | Gradle / npm / wrapper / lockfile 检查入口明确 |
| P10.4 | 固定 Bridge schema manifest 与 RN 组件注册表 | 双端协作边界和兼容窗口可追溯 |
| P10.5 | 输出 Phase 10 closeout 宿主 | 治理入口稳定 |

## 交付物
- `accessibility-audit-matrix-2026-03-30.md`
- `bridge-schema-manifest-2026-03-30.md`
- `rn-component-registry-2026-03-30.md`
- `compliance-and-sensitive-log-governance-2026-03-30.md`
- `supply-chain-audit-playbook-2026-03-30.md`
- `phase-10-closeout-assessment.md`

## 当前状态
- `validated（Phase 10 closeout 生效于 2026-03-30）`
