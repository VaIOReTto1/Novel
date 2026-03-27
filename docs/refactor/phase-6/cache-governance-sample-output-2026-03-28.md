# Cache Governance Sample Output - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 目标：为 `CacheGovernanceReportGenerator` 补一份可回链的样例输出与 recommendation 解读。

## 样例来源
- 生成器：
  - `android/app/src/main/java/com/novel/utils/network/cache/CacheGovernanceReportGenerator.kt`
- 校验样例：
  - `android/app/src/test/java/com/novel/utils/network/cache/CacheGovernanceReportGeneratorTest.kt`

## 样例快照
### 平衡样例
- `current_cache_size_bytes = 15`
- `current_entry_count = 2`
- `cleanup_runs = 2`
- `cleanup_reduction_ratio = 0.22`
- `average_bytes_cleaned_per_run = 306.00`
- recommendation:
  - `none`

### 风险样例
- `cleanup_runs = 4`
- `space_cleaned = 0`
- `entryCountBefore = 18`
- `entryCountAfter = 6`
- `cleanup_reduction_ratio = 0.66`
- recommendations:
  - `risk cleanup-frequency-high`
  - `warning cleanup-entry-drop-large`
  - `warning cleanup-space-release-low`

## recommendation 解读
| recommendation | 含义 | 当前用途 |
| --- | --- | --- |
| `cleanup-frequency-high` | cleanup 次数过高 | 作为 cache pressure 异常入口 |
| `cleanup-entry-drop-large` | 单次清理移除了过多条目 | 作为缓存稳定性风险提示 |
| `cleanup-space-release-low` | cleanup 发生但没有明显释放空间 | 作为无效清理或统计异常提示 |

## 当前结论
- 缓存治理现在已经有了“输出长什么样”和“warning 怎么解读”的固定样例。
- 当前仍未完成的是 IO / 内存 / 电量收益量化，不是治理输出缺失。
