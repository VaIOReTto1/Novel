# 决策日志

| 日期 | 阶段 | 类型 | 决策 | 原因 | 影响 | 后续动作 |
| --- | --- | --- | --- | --- | --- | --- |
| 2026-03-28 | CI gate recovery | quality | `android-detekt-observe` 改用 committed `android/app/detekt-baseline.xml` 冻结历史 finding，`android-smoke` 保持 observe | `app:detekt` 当前仍有 `1481` weighted issues，emulator smoke 路径仍需继续观察 | detekt job 可以真实通过且不放松规则，smoke job 继续保留 artifact 与 flake 可观测性 | 后续按月收缩 baseline，并在 smoke emulator 连续稳定后再评估升级为 blocking |
| 2026-03-26 | Phase 5 reopen | architecture | 将 `home/search/login/book/reader/rn-host` 根状态机全部收入各自 feature 模块，`app` 只保留 Android 强制入口与 host wrapper | 继续把 feature 根留在 `app` 会让 reopen 无法真正 closeout | `app` 不再直接承载 feature ViewModel 根 | 进入 thin-app sweep 与文档重建 |
| 2026-03-26 | Phase 5 reopen | architecture | `ReaderViewModel` 迁移采用 `ReaderPaginationGateway / ReaderSettingsGateway / ReaderHistoryGateway` + app 默认实现，而不是一次性迁完整 service/usecase 根 | Reader 依赖树过深，一次性迁移风险高 | 行为语义保持不变，同时 `feature-reader` 获得根状态机所有权 | 后续如需继续深挖，再逐步下沉 service/usecase |
| 2026-03-26 | Phase 5 reopen | host | 在 `ReactNativePage / NavigationBridgeModule / SettingsBridgeModule` 引入统一 `HostGatewayEntryPoint` 取宿主能力 | 需要把宿主 adapter 从直接 `new Default*` 压到统一入口 | 宿主各包装边界更清晰 | host adapter 默认实现仍留在 `app` |
| 2026-03-26 | Phase 5 reopen | verification | 最终验证采用 `in-process + non-incremental` Gradle 参数执行 | 本机 Kotlin/KSP 增量缓存存在抖动，但源码本身可稳定通过 | 最终矩阵结果可复现，可作为 closeout 证据 | 后续如环境恢复，可回到默认增量验证 |
| 2026-03-26 | Phase 5 reopen | closeout | `2026-03-21` closeout 降级为历史 checkpoint，当前权威事实以 `2026-03-26 reopen closeout` 为准 | reopen 后旧 closeout 已不能代表当前模块边界 | README、验证看板、closeout 文档统一口径 | 历史矩阵与 host-compat 文件仅保留追溯用途 |
