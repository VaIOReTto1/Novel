# Phase 9 Import Export History Recovery

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 9`
- 当前结论：`已固定导入导出、阅读历史与恢复入口的当前边界`

## 当前锚点
| Surface | Current Anchor | Current Fact |
| --- | --- | --- |
| 用户设置导出 | `ExportUserDataUseCase` | 当前导出的是主题与自定义设置，不是完整用户态快照 |
| 用户设置导入 | `ImportUserDataUseCase` | 当前导入的是主题与自定义设置，支持验证文件与覆盖控制 |
| 阅读历史保存 | `HistoryService`, `ReaderHistoryCoordinator`, `ReaderHistoryGateway` | 已存在保存、读取、删除、清空历史入口 |
| 阅读进度恢复 | `ProgressService`, `ReadingProgressRepository` | 已存在进度持久化和恢复日志入口 |
| Reader 配置变化恢复 | `rememberPageCurlState`, `PageCurlConfig` | 已使用 `rememberSaveable` 保持局部状态 |

## 当前判定
- 当前仓库已经具备“设置导入导出”和“阅读历史 / 进度恢复”的最小实现，不再是完全空白。
- 当前导入导出能力明确只覆盖设置域，不覆盖完整 Token / 历史 / 缓存全量迁移。
- 因此 Phase 9 在这里关闭的是“用户连续性入口与边界已经明确”，而不是“全量连续性资产都已统一导出导入”。

## 允许降级
- 仅导出设置，不伪装成整机恢复
- 历史或进度恢复失败时回落到空态或重新初始化

## 禁止伪恢复
- 不能把“只导出设置”的能力宣称为“完整用户数据备份”
- 不能在历史恢复失败时静默补假数据

## 当前缺口
- 历史与设置仍不是统一备份包
- Token / 登录态不在导出导入范围内
- 缺一份 Stage 5 级别的“恢复后用户可见行为”样本矩阵
