# Phase 3 旧网络壳迁移矩阵

## 目标
- 固定第二阶段开始时 `ApiService / RetrofitClient` 的真实调用分布。
- 区分高风险生产路径、次级兼容路径、初始化依赖路径。
- 为 `NetworkFacade + LegacyApiServiceAdapter` 的迁移顺序提供唯一参考。

## 遗留网络壳定义
- 旧网络壳：
  - `android/app/src/main/java/com/novel/utils/network/ApiService.kt`
  - `android/app/src/main/java/com/novel/utils/network/ApiService.kt` 中的 `RetrofitClient`
- 当前主栈候选：
  - `android/app/src/main/java/com/novel/di/NetworkModule.kt`
  - Hilt 注入的 Retrofit / OkHttp / interceptors

## 调用分布分级

### A. 高风险生产路径
这些路径直接影响主业务流，必须优先迁移。

| Subsystem | Key Paths | Current Pattern | Risk | Phase 3 Priority |
| --- | --- | --- | --- | --- |
| RN Bridge | `com/novel/rn/bridge/NavigationBridgeModule.kt` | 直接调用 `ApiService.get()` | 高 | P0 |
| Home | `com/novel/utils/network/api/front/HomeService.kt` | `ApiService.get()` | 高 | P1 |
| Search | `com/novel/utils/network/api/front/SearchService.kt` | `ApiService.get()` | 高 | P1 |
| User | `com/novel/utils/network/api/front/user/UserService.kt` | `ApiService.get/post/put/delete()` | 高 | P1 |
| Book | `com/novel/utils/network/api/front/BookService.kt` | 大量 `ApiService.get/post()` | 高 | P2 |
| Author | `com/novel/utils/network/api/author/AuthorService.kt` | `ApiService.get/post/delete()` | 高 | P2 |
| AI | `com/novel/utils/network/api/author/ai/AiService.kt` | `ApiService.postQuery()` | 高 | P2 |

### B. 次级兼容路径
这些路径仍属于生产代码，但不应先于主链路迁移。

| Subsystem | Key Paths | Current Pattern | Risk | Phase 3 Priority |
| --- | --- | --- | --- | --- |
| News | `com/novel/utils/network/api/front/NewsService.kt` | `ApiService.get()` | 中 | P3 |
| Resource | `com/novel/utils/network/api/front/resource/ResourceService.kt` | `ApiService.get/post()` | 中 | P3 |

### C. 初始化与兼容依赖路径
这些路径不一定是业务主链路，但会影响旧壳退场顺序。

| Subsystem | Key Paths | Current Pattern | Risk | Phase 3 Priority |
| --- | --- | --- | --- | --- |
| App bootstrap | `com/novel/MainApplication.kt` | `RetrofitClient.init(...)` | 高 | P4 |
| DI candidate | `com/novel/di/NetworkModule.kt` | Hilt Retrofit/OkHttp 已存在 | 机会点 | P0 |

## 关键观察
- 当前项目并不是“完全没有主栈”，而是“主栈已存在，但旧壳仍在大量生产路径中活跃”。
- `NavigationBridgeModule` 属于最优先风险点：
  - 它跨 Native / RN 边界
  - 它直接连接用户可感知主路径
  - 它和后续 `BridgeFacade` 收口存在直接耦合
- `BookService`、`UserService`、`AuthorService` 的调用面较大，属于第二批迁移对象，不适合在没有矩阵和兼容层时直接散点替换。
- 当前高风险生产路径中的 `NavigationBridgeModule`、`HomeService`、`SearchService`、`UserService`、`AiService`、`AuthorService` 与 `BookService` 文件内旧 `ApiService` 直连均已清零；旧网络壳的剩余重点已退到次级兼容路径与启动初始化路径。

## 固定迁移顺序
1. `NavigationBridgeModule`
2. `HomeService`
3. `SearchService`
4. `UserService`
5. `BookService`
6. `AuthorService`
7. `AiService`
8. `NewsService`
9. `ResourceService`
10. `MainApplication` 中的 `RetrofitClient.init` 退场

## 执行规则
- 第二阶段内禁止新增任何生产代码对旧 `ApiService / RetrofitClient` 的直接依赖。
- 所有迁移必须先经过：
  - `NetworkFacade`
  - `LegacyApiServiceAdapter`
- 旧实现退场顺序必须晚于：
  - 高风险主路径完成 smoke
  - contract tests 持续通过
  - 回滚开关可用

## 关联验证
- 关联任务：
  - `P3.3`
  - `P3.4`
  - `P3.5`
- 关联验证：
  - `V3-01`
  - `V3-06`
