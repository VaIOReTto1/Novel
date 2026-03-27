# Welfare / WebView Multi-Sample Matrix - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 目标：把 Welfare / WebView 从“路径矩阵”继续推进到“多次采样矩阵”。

## 原始证据
- `docs/refactor/evidence/welfare-webview-multisample-2026-03-28.txt`

## 首开样本
| Run | InitializeWelfarePageUseCase | pageLoadComplete | TTI | WebView load | FCP |
| --- | --- | --- | --- | --- | --- |
| `1` | `401ms` | `231ms` | `190ms` | `223ms` | `577ms` |
| `2` | `388ms` | `224ms` | `179ms` | `241ms` | `515ms` |
| `3` | `394ms` | `213ms` | `184ms` | `208ms` | `570ms` |

## 复开样本
| Run | reopen pageLoadComplete |
| --- | --- |
| `1` | `1ms` |
| `2` | `0ms` |
| `3` | `1ms` |

## 当前判断
- `InitializeWelfarePageUseCase`
  - min: `388ms`
  - median: `394ms`
  - max: `401ms`
- `pageLoadComplete`
  - min: `213ms`
  - median: `224ms`
  - max: `231ms`
- `TTI`
  - min: `179ms`
  - median: `184ms`
  - max: `190ms`
- `WebView load`
  - min: `208ms`
  - median: `223ms`
  - max: `241ms`
- `FCP`
  - min: `515ms`
  - median: `570ms`
  - max: `577ms`

## 当前结论
- Welfare / WebView 现在已经有：
  - 路径矩阵
  - 多次采样矩阵
- 剩余大项已经更集中到：
  - cache / cookie / 预加载收益量化
  - 更系统的专项 benchmark
