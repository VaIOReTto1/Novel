# Phase 3 - 基础设施收口

## 目标
- 把当前“多套基础设施并存”收成“单一主系统 + 明确兼容层”。
- 为后续边界收口和超大类拆分提供统一的网络、存储、错误与异步通路。

## 范围
- 网络主通路统一
- 协程模型统一
- 存储通路统一
- 错误模型统一
- 日志与观测统一
- 第二阶段静态债基线建立与第一轮收敛

## 非目标
- 不做正式 Gradle 模块拆分
- 不做 Reader 核心算法重写
- 不做 Bridge 协议语义变更
- 不做全量 DataStore 一次性迁移
- 不做 repo 级静态债一次性清零

## 进入条件
- 第一阶段已 `validated`
- 第一阶段 blocking 回归命令持续可执行
- 第二阶段文档与验证看板已创建
- 第二阶段静态债基线文档已准备记录

## 任务拆解
| 编号 | 任务 | 预期输出 | 对应检验 |
| --- | --- | --- | --- |
| P3.1 | 建立第二阶段文档、验证看板、静态债基线文档 | Stage 2 文档骨架与 baseline 文档 | V3-05 |
| P3.2 | 记录静态债基线 | `RN lint` / `detekt` 当前基线 | V3-05 |
| P3.3 | 输出旧网络壳真实调用矩阵 | 高风险生产路径与次级兼容路径矩阵 | V3-01 |
| P3.4 | 建立 `NetworkFacade + LegacyApiServiceAdapter` | 新旧网络主通路/兼容层结构 | V3-01 |
| P3.5 | 迁移 Home / Search / Bridge 高风险路径到主网络通路 | 高风险路径唯一主栈成立 | V3-01 |
| P3.6 | 建立 `StorageFacade` | `NovelUserDefaults`、SharedPreferences 兼容层统一入口 | V3-03 |
| P3.7 | 做 `DataStore` 试点迁移 | 低风险 key 迁移样本与兼容读取验证 | V3-03 |
| P3.8 | 引入 `AppError + DataResult<T>` | 第一批高风险边界错误模型统一 | V3-04 |
| P3.9 | 清理 `runBlocking / 匿名全局 scope` | 第二阶段触达范围内异步模型统一 | V3-02 |
| P3.10 | 建立 rollback / kill switch | 网络主栈、BridgeFacade、存储抽象切换开关 | V3-06 |

## 交付物
- 网络壳迁移矩阵
- `NetworkFacade` 设计与兼容适配层
- `StorageFacade` 设计与迁移策略
- `AppError` / `DataResult<T>` 统一模型
- 第二阶段静态债基线文档
- rollback / kill switch 设计

## 硬阈值
- 第二阶段触达范围内新增：
  - 旧 `ApiService / RetrofitClient` 生产直连调用数 = `0`
  - SharedPreferences 业务直连新增数 = `0`
  - `runBlocking / 匿名全局 scope` 新增数 = `0`
- 第一批高风险路径统一进入 `AppError`
- touched files：
  - ESLint error = `0`
  - detekt issue = `0`

## 风险与回滚
- 风险：
  - 旧网络壳与新主栈并存期兼容问题
  - 存储抽象替换引发数据读取差异
  - 跨层异步模型变更引发线程时序问题
  - 静态债收敛过程中误伤业务代码
- 回滚：
  - 所有主线按单主题原子提交
  - `NetworkFacade`、`StorageFacade`、`BridgeFacade` 必须保留兼容切换能力
  - 若核心 smoke / contract 回归，立即回退到上一稳定原子提交

## 检验计划
- `V3-01` 高风险生产路径主网络通路唯一化成立
- `V3-02` 第二阶段触达范围内协程模型统一
- `V3-03` `StorageFacade` 成立，SharedPreferences 仅保留兼容层
- `V3-04` `AppError` 第一批统一落地
- `V3-05` 第二阶段静态债基线建立完成
- `V3-06` rollback / kill switch 可执行
- `V3-07` Phase 4 进入条件明确

## 退出条件
- 全部 `V3-*` 为 `green`
- 高风险生产路径主通路唯一化成立
- 第二阶段基础抽象与兼容层稳定
- Phase 4 进入条件已客观化

## 负责人
- Owner：当前重构实施者
- Reviewer：模块代码评审者
- Validator：阶段门禁批准者
- 当前状态：`in_progress`
