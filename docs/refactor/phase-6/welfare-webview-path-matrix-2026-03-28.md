# Welfare / WebView Path Matrix - 2026-03-28

## 状态
- 生效日期：`2026-03-28`
- 设备：`192.168.8.130:5555 / DN2101 / Android 13`
- 目标：把 Welfare / WebView 的首开、复开、回退复用路径固定成统一矩阵。

## 证据来源
- `docs/refactor/evidence/welfare-webview-performance-logcat-2026-03-27.txt`
- `docs/refactor/evidence/welfare-webview-path-matrix-logcat-2026-03-28.txt`

## 路径矩阵
| Path | Evidence Date | Key Signals | Current Conclusion |
| --- | --- | --- | --- |
| `FIRST_OPEN` | `2026-03-28` | `InitializeWelfarePageUseCase=352ms`, `pageLoadComplete=258ms`, `TTI=136ms`, `webViewLoad=170ms`, `FCP=496ms` | 首开路径稳定 |
| `FIRST_OPEN` | `2026-03-27` | `InitializeWelfarePageUseCase=248ms`, `pageLoadComplete=110ms`, `TTI=347ms`, `webViewLoad=375ms`, `FCP=696ms` | 首开路径可追溯 |
| `REOPEN_FROM_HOME` | `2026-03-27` | `savedState=true`, `WebView状态已恢复`, `页面加载完成=1ms`, `TTI=236ms`, `webViewLoad=113ms`, `FCP=704ms` | 复开恢复路径成立 |
| `BACK_REUSE` | `2026-03-27` | `传入URL与当前URL相同，无需重新加载`, `savedState restore` | 回退复用路径成立 |

## 当前判断
- Welfare / WebView 现在已经不只是“有日志样本”，而是有明确路径矩阵：
  - `FIRST_OPEN`
  - `REOPEN_FROM_HOME`
  - `BACK_REUSE`
- 当前还没完成的是：
  - 专项 benchmark 化
  - cache / cookie / 预加载收益量化

## 当前结论
- “路径级矩阵”这一层已经补齐。
- 后续剩余 backlog 应聚焦更深量化，而不是继续补首条路径样本。
