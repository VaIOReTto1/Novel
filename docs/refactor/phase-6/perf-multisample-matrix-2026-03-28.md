# Phase 6 Multi-Sample Matrix - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 目标：把 Startup / Search / Reader 从“单条样本”推进到“多次采样矩阵”。

## 原始证据
- `docs/refactor/evidence/perf-multisample-2026-03-28.txt`

## Startup
| Run | Displayed | First Frame | Fully Loaded |
| --- | --- | --- | --- |
| `1` | `1.736s` | `949ms` | `1065ms` |
| `2` | `1.705s` | `961ms` | `1071ms` |
| `3` | `1.748s` | `974ms` | `1079ms` |

### 当前判断
- `first_frame`
  - min: `949ms`
  - median: `961ms`
  - max: `974ms`
- `fully_loaded`
  - min: `1065ms`
  - median: `1071ms`
  - max: `1079ms`
- 当前结果说明：
  - 首帧后 gate-driven deferred task 已稳定
  - 首帧时间仍偏高，但波动区间已比较收敛

## Search
### `query=我` + `debug_search_page_size=5`
| Run | INITIAL_ENTRY | LOAD_MORE page=2 | LOAD_MORE page=3 |
| --- | --- | --- | --- |
| `1` | `94ms` | `50ms` | `165ms` |
| `2` | `180ms` | `92ms` | `76ms` |
| `3` | `171ms` | `90ms` | `75ms` |

### 当前判断
- `INITIAL_ENTRY`
  - min: `94ms`
  - median: `171ms`
  - max: `180ms`
- `LOAD_MORE page=2`
  - min: `50ms`
  - median: `90ms`
  - max: `92ms`
- `LOAD_MORE page=3`
  - min: `75ms`
  - median: `76ms`
  - max: `165ms`
- 当前结果说明：
  - `LOAD_MORE` 已不再只是单条真机样本
  - 当前已形成最小可重复分页矩阵

## Reader
### `debug_reader_auto_flip=next`
| Run | settings_update | init | flip |
| --- | --- | --- | --- |
| `1` | `5ms` | `384ms` | `49ms` |
| `2` | `4ms` | `399ms` | `39ms` |
| `3` | `4ms` | `381ms` | `41ms` |

### 当前判断
- `settings_update`
  - min: `4ms`
  - median: `4ms`
  - max: `5ms`
- `init`
  - min: `381ms`
  - median: `384ms`
  - max: `399ms`
- `flip`
  - min: `39ms`
  - median: `41ms`
  - max: `49ms`
- 当前结果说明：
  - `flip` 已从“首条样本”推进到三次可重复样本
  - 目前仍缺的是更系统的 benchmark / 压测，而不是动作级多次采样

## 当前结论
- Search / Reader 的“多次采样矩阵”这一层已经补到 repo 内。
- Startup 也已有多次样本，可支撑后续 first-frame 收敛讨论。
