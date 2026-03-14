# Phase 0 - 风险图谱与重构禁区清单

## 1. 风险分级标准
- `blocker`：不解决则不得进入下一阶段。
- `high`：可继续推进局部工作，但不得在未登记偏差的情况下关闭阶段。
- `medium`：需要在后续阶段列入明确任务。
- `low`：记录并持续观察。

## 2. 风险图谱

### Blocker
| ID | 风险 | 说明 | 影响范围 | 当前证据 |
| --- | --- | --- | --- | --- |
| R-B01 | Release 安全与发布路径不可信 | 当前 release 路径仍需系统化收口，Manifest/网络安全/签名链路未正式生产化 | 发布、安全、回滚 | `android/app/build.gradle`, `android/app/src/main/AndroidManifest.xml`, `android/app/src/main/res/xml/network_security_config.xml` |
| R-B02 | 数据库发布路径不安全 | `fallbackToDestructiveMigration()` 与 `exportSchema=false` 不适合正式发布 | 升级、数据可靠性 | `android/app/src/main/java/com/novel/di/DatabaseModule.kt`, `android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt` |

### High
| ID | 风险 | 说明 | 影响范围 | 当前证据 |
| --- | --- | --- | --- | --- |
| R-H01 | 网络栈双轨并存 | Hilt 网络栈与手写 `RetrofitClient/ApiService` 并存，后续重构极易失控 | 网络、认证、缓存、错误处理 | `android/app/src/main/java/com/novel/di/NetworkModule.kt`, `android/app/src/main/java/com/novel/utils/network/ApiService.kt` |
| R-H02 | Bridge 职责过大 | `NavigationBridgeModule` 暴露能力过多，混合 Promise/Callback/Event 模式 | Native/RN 协议、回归、性能 | `android/app/src/main/java/com/novel/rn/bridge/NavigationBridgeModule.kt` |
| R-H03 | JVM 单测为 0 | 当前 `android/app/src/test` 不存在，难以对核心逻辑建立稳定护栏 | 测试、回归、重构速度 | `android/app/src/test` 缺失 |
| R-H04 | 质量门禁缺失 | 当前 `.github/workflows` 仅有 `label.yml`，不具备阻断能力 | CI、发布、团队协作 | `.github/workflows/label.yml` |
| R-H05 | 生产路径混入 mock/fallback 数据 | 评论、历史、搜索等链路存在 mock 补齐或降级数据 | 功能真实性、测试稳定性 | `android/app/src/main/java/com/novel/page/read/usecase/LoadBookReviewsUseCase.kt`, `android/app/src/main/java/com/novel/page/read/service/HistoryService.kt`, `android/app/src/main/java/com/novel/page/search/repository/SearchRepository.kt` |
| R-H06 | 超大类膨胀 | 阅读器、Bridge、缓存与首页相关类体量过大 | 可维护性、修改风险 | `ReaderViewModel.kt`, `NavigationBridgeModule.kt`, `NetworkCacheManager.kt`, `HomeViewModel.kt` |

### Medium
| ID | 风险 | 说明 | 影响范围 | 当前证据 |
| --- | --- | --- | --- | --- |
| R-M01 | 字体资源体积异常大 | 字体资源约 `76.04 MB`，后续包体积治理压力大 | 包体积、安装体积、资源治理 | `android/app/src/main/res/font` |
| R-M02 | 权限申请过宽 | `READ_PHONE_STATE`、`READ_PHONE_NUMBERS`、`READ_PRIVILEGED_PHONE_STATE` 同时存在 | 合规、审核、用户感知 | `android/app/src/main/AndroidManifest.xml` |
| R-M03 | 性能基线未完成动态采样 | 当前只有静态基线，动态性能指标尚待设备补采 | 性能验证、Phase 门禁 | `docs/refactor/phase-0/baseline-snapshot.md` |
| R-M04 | 构建配置分散 | 版本、插件、Gradle 属性、版本脚本分散 | 构建维护、升级成本 | `android/build.gradle`, `android/app/build.gradle`, `android/gradle.properties`, `android/versioning.gradle`, `scripts/version-sync.js` |

### Low
| ID | 风险 | 说明 | 影响范围 | 当前证据 |
| --- | --- | --- | --- | --- |
| R-L01 | README 与真实成熟度存在偏差 | 文档可能领先实现 | 文档可信度 | `README.md` |
| R-L02 | 文档责任人未定 | 当前 Owner/Reviewer/Validator 仍待指定 | 执行节奏、阶段关闭 | `docs/refactor/phases/*.md` |

## 3. 重构禁区清单

### 禁止直接修改的对象
- 不允许在未建立 Bridge contract tests 前直接修改 Bridge event 名或 Promise 返回字段。
- 不允许在未建立 route 资产清单和回归用例前直接修改 Compose route 语义。
- 不允许在未完成 Phase 0 基线前修改核心页面 UI 语义并宣称“无影响”。
- 不允许在未建立 fixture/fake data 方案前删除生产路径 mock/fallback 数据。
- 不允许在未完成 Phase 1 发布治理前推进大规模结构重构。
- 不允许在未完成 Phase 2 质量门禁前推进超大类拆分和模块化。

### 需要先有证据再改动的对象
- 阅读器：
  - 必须先有翻页、切章、进度恢复、设置恢复的回归证据。
- 福利 WebView：
  - 必须先有域名白名单、SSL 策略、外链策略和错误态验证。
- Release 构建：
  - 必须先有签名、混淆、资源收缩、产物报告和回滚路径。
- 数据库与本地存储：
  - 必须先有 migration 策略、演练矩阵和中断恢复方案。

## 4. 当前结论
- `R-B01` 和 `R-B02` 属于 Phase 1 的强制 blocker。
- `R-H01` 到 `R-H06` 属于后续 Phase 1-3 的持续重点风险。
- 当前已形成可执行的禁区清单，可支撑 `V0-04` 进入验证。
