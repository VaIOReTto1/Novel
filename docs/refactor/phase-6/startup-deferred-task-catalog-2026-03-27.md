# Startup Deferred Task Catalog - 2026-03-27

## 状态
- 生效日期：`2026-03-27`
- 关联阶段：`Phase 6 / Startup backlog 深化`
- 当前结论：`首帧后任务已从粗粒度布尔开关升级为正式任务清单`

## 目的
- 把原先仅有 `network/settings` 两个布尔开关的首帧后初始化策略，升级成有：
  - 任务 ID
  - 优先级
  - 触发条件
  - 预期收益
  的正式任务清单。
- 为后续 startup evidence 和 budget 文档提供统一口径。

## 当前任务清单
| Task ID | Priority | Trigger | Expected Benefit | 当前代码入口 |
| --- | --- | --- | --- | --- |
| `NETWORK` | `HIGH` | `after_first_frame` | `将网络服务初始化移出首帧前关键路径` | `StartupDeferredInitializationCoordinator.kt`, `MainApplication.kt` |
| `SETTINGS` | `MEDIUM` | `after_first_frame` | `将设置服务初始化移出首帧前关键路径` | `StartupDeferredInitializationCoordinator.kt`, `MainApplication.kt` |

## 当前执行规则
- 首帧未完成时，不允许调度任何 deferred task。
- 同一进程生命周期内只调度一次 deferred task plan。
- 编排器按任务清单顺序执行，而不是继续散落在多个布尔分支中。

## 当前意义
- 这份 catalog 不是说 Startup 已经做完了。
- 它解决的是：后续讨论 startup deferred work 时，终于有一份稳定、可回链到代码的任务清单，而不是只有“network / settings 两组”的模糊表述。

## 主要引用
- `android/app/src/main/java/com/novel/StartupDeferredInitializationCoordinator.kt`
- `android/app/src/main/java/com/novel/MainApplicationStartupOrchestrator.kt`
- `android/app/src/test/java/com/novel/StartupDeferredInitializationCoordinatorTest.kt`
- `android/app/src/test/java/com/novel/MainApplicationStartupOrchestratorTest.kt`
