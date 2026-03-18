# Phase 4 - 边界收口与超大类拆分

## 目标
- 把“功能还在，但复杂度失控”的状态收回到可维护区间。
- 在单 `app` 模块内先完成逻辑模块化和超大类减重准备。

## 范围
- 包内逻辑模块化
- Bridge 收口到 `BridgeFacade`
- 指定超大类拆分
- 生产路径去 mock
- `profile-host / RN Host` 风险验证补齐
- 第二阶段静态债第二轮收敛

## 非目标
- 不做正式 Gradle 模块拆分
- 不做 Reader 最终大拆
- 不做 route / Bridge payload / RN 组件名变更
- 不做产品语义改版

## 进入条件
- `Phase 3` 已 `validated`
- `NetworkFacade`、`StorageFacade`、`AppError`、rollback / kill switch 已成立
- 静态债基线已存在

## 协作编制
### Leader Mode
- `single leader / multi-helper`

### Base Helper Count
- `4`

### Scale-Up Triggers
- `NavigationBridgeModule` 与 `HomeViewModel` 可独立拆分且互不抢锁。
- `RN Host / profile-host` 风险验证可以独立运行，不与业务拆分类主题共享写锁。

### Scale-Down Triggers
- 当前只做文档、看板、关闭评审或证据归档。
- 当前只推进单一超大类拆分，不涉及 Host 风险验证。

### Agent Roster
- `BridgeFacadeSplitAgent`
- `FeatureBoundarySplitAgent`
- `CacheReaderLightAgent`
- `HostRiskQualityAgent`

### Lock Strategy
- `LOCK-BRIDGE-FACADE`
- `LOCK-HOME-SEARCH-SPLIT`
- `LOCK-CACHE-READER-LIGHT`
- `LOCK-HOST-QUALITY`

### Retry Window
- `0-15 min` helper 自检、自修一次
- `15-30 min` Leader 收窄范围后二次派发

### Escalation Window
- `30-45 min` 进入 `hard escalation`
- 立即升级条件：
  - UI 语义变化
  - route / Bridge event / payload 语义变化
  - Reader 核心翻页或分页行为风险
  - 无法给出 rollback command
  - touched files 新增 lint / detekt 红项

### Leader-only Actions
- 更新阶段状态
- 更新验证看板
- 更新决策日志
- 维护 README 与回滚索引
- 执行 Git commit / revert

## 任务拆解
| 编号 | 任务 | 预期输出 | 对应检验 |
| --- | --- | --- | --- |
| P4.1 | 建立目标包结构与迁移映射 | 逻辑模块化边界图与迁移表 | V4-01 |
| P4.2 | 建立 `BridgeFacade` | Bridge 统一出口 | V4-03 |
| P4.3 | 拆分 `NavigationBridgeModule` | facade + delegates 结构 | V4-02 |
| P4.4 | 拆分 `HomeViewModel` | 协调/状态/加载/导航职责拆分 | V4-02 |
| P4.5 | 拆分 `SearchRepository` | Search 相关数据职责拆分 | V4-02 |
| P4.6 | 拆分 `NetworkCacheManager` | 缓存职责边界拆分 | V4-02 |
| P4.7 | Reader 轻触减重 | helper、mapping、settings/history 边界抽离 | V4-02 |
| P4.8 | 清理第二阶段触达范围内生产 mock | 正式路径去 mock | V4-04 |
| P4.9 | 补齐 `profile-host / RN Host` 专项验证 | 宿主页挂载、白屏风险、Bridge 初始化时序证据 | V4-05 |
| P4.10 | 第二阶段静态债第二轮收敛 | touched files 清零与 repo 级债务下降 | V4-06 |
| P4.11 | 输出第二阶段关闭总结 | 第二阶段 closeout 文档 | V4-07 |

## 交付物
- 包结构迁移映射
- `BridgeFacade` 与 delegate 设计
- 指定超大类拆分结果
- 生产 mock 清理清单
- `profile-host / RN Host` 风险验证材料
- 第二阶段关闭总结

## 硬阈值
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 必须完成拆分
- Bridge 新增直连调用点（绕过 `BridgeFacade`）= `0`
- 第二阶段触达范围内生产 mock 保留数 = `0`
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`

## 风险与回滚
- 风险：
  - `profile-host / RN Host` 挂载稳定性
  - Bridge 初始化时序
  - Reader 减重触碰阅读核心行为
  - 生产 mock 清理引发真实空态或弱网分支暴露
- 回滚：
  - facade、delegate、helper 拆分必须保留兼容入口
  - 若 `RN Host / profile-host` 风险扩大，允许暂停 Phase 4，仅保留 Phase 3 成果
  - 若 Reader 轻触减重引发翻页/分页核心回归，立即回退该组原子提交

## 检验计划
- `V4-01` 包边界骨架稳定
- `V4-02` 指定超大类拆分完成
- `V4-03` `BridgeFacade` 成立且旧协议兼容
- `V4-04` 第二阶段触达范围内生产 mock 清理完成
- `V4-05` `profile-host / RN Host` 风险验证补齐
- `V4-06` 第二阶段静态债目标达标
- `V4-07` 第二阶段关闭总结完成
- `V4-08` Phase 5 进入条件明确

## 退出条件
- 全部 `V4-*` 为 `green`
- 指定超大类拆分完成并通过回归
- `profile-host / RN Host` 不再只有口头高风险说明
- 第二阶段可进入真正模块化准备阶段

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`planned`
