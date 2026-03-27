# 缓存清理治理报告

## 摘要
- 首次建立日期：`2026-03-22`
- 最近更新：`2026-03-27`
- 目标：为 `NetworkCacheManager` 维持一份可持续演进的治理输出，固定：
  - 当前缓存体积
  - 当前缓存条目量
  - cleanup 次数 / 原因 / 耗时 / 前后条目变化
  - cleanup 摘要与风险提示
- 当前口径：
  - 治理入口已形成
  - 风险提示能力已形成
  - IO / 内存 / 电量收益复盘仍未完成

## 工具入口
- 运行时代码入口：
  - [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt)
  - [NetworkCacheManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt)
- 关键统计来源：
  - [CacheStatsReporter.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheStatsReporter.kt)
  - [CacheCleanupCoordinator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheCleanupCoordinator.kt)

## 当前治理输出
- 当前缓存体积
- 当前缓存条目量
- cleanup 次数、原因、耗时
- cleanup 前后条目量变化
- 累计清理条目与累计释放空间
- 摘要字段：
  - `cleanup_reduction_ratio`
  - `average_bytes_cleaned_per_run`
- 风险提示：
  - `cleanup-frequency-high`
  - `cleanup-entry-drop-large`
  - `cleanup-space-release-low`

## 当前判断
- 已完成：
  - cleanup 统计模型扩展为“次数 + 原因 + 耗时 + 前后条目量”
  - 缓存治理报告生成器落地
  - cleanup 摘要与风险提示能力落地
- 未完成：
  - cleanup 对 IO / 内存 / 电量收益的定量复盘
  - 不同 cleanup 策略之间的收益对比
  - 真机长时间运行样本归档

## 残余风险
- 当前输出更偏“治理入口 + 风险识别”，不是最终用户态收益证明。
- 后续若引入更细粒度缓存层级，还需要重新校准治理字段口径。
