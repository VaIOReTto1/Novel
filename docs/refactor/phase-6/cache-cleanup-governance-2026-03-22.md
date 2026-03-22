# 缓存清理治理报告

## 摘要
- 日期：`2026-03-22`
- 目标：为 `NetworkCacheManager` 建立第一版缓存治理报告入口，固定当前缓存体积、条目量、cleanup 次数/原因/耗时与前后条目量变化。
- 当前口径：
  - 已开始治理
  - 尚未完成 IO / 内存 / 电量收益复盘

## 工具入口
- 运行时代码入口：
  - [CacheGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt)
  - [NetworkCacheManager.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/NetworkCacheManager.kt)
- 关键统计来源：
  - [CacheStatsReporter.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheStatsReporter.kt)
  - [CacheCleanupCoordinator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/network/cache/CacheCleanupCoordinator.kt)

## 当前治理字段
- 当前缓存体积：来自 `network_cache` 目录文件总大小
- 当前缓存条目量：来自 `network_cache` 目录有效文件数
- cleanup 次数：`cleanupRuns`
- cleanup 原因：`cleanupReason`
- cleanup 耗时：`lastCleanupDurationMs`
- cleanup 前后条目量：`entryCountBefore -> entryCountAfter`
- 累计清理条目与累计释放空间：
  - `totalCleaned`
  - `spaceCleaned`

## 当前判断
- 已完成：
  - cleanup 统计模型扩展为“次数 + 原因 + 耗时 + 前后条目量”
  - `NetworkCacheManager` 已具备缓存治理报告输出入口
  - 缓存治理报告生成器已落地
- 未完成：
  - cleanup 前后 IO / 内存 / 电量收益的定量复盘
  - 不同 cleanup 策略之间的收益对比
  - 真机长时间运行样本归档

## 残余风险
- 当前报告能反映缓存目录与 cleanup 统计，但还不能直接证明最终用户态收益。
- `entryCountBefore / entryCountAfter` 当前是 cleanup 时点统计，后续若引入更细粒度 cache 层级，还需要重新校准口径。
