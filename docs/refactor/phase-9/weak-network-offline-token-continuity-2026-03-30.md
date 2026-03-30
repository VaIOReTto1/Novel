# Phase 9 Weak Network Offline Token Continuity

## 状态
- 记录日期：`2026-03-30`
- 关联阶段：`Stage 5 / Phase 9`
- 当前结论：`已固定弱网 / 离线 / Token 连续性的当前语义边界`

## 当前锚点
| Domain | Current Anchor | Current Fact |
| --- | --- | --- |
| 弱网 / 离线缓存兜底 | `NetworkCacheManager` | 已存在 `CACHE_ONLY`、`CACHE_FIRST`、`NETWORK_FIRST`、`SMART_FALLBACK` 四类策略 |
| 业务缓存兜底 | `CachedBookRepository`, `HomeRepository`, `UserRepository` | 已存在缓存优先、过期缓存兜底和离线可用入口 |
| 用户态 Token 读取 | `TokenProvider`, `AuthService`, `NovelKeyChain` | Token 读取与保存入口已存在 |
| 登录态镜像 | `AuthService.saveAuthInfo()` | 已写入 `TOKEN_EXPIRES_AT`、`IS_LOGGED_IN`、`USER_ID` 并同步 `SettingsDataStorePilot` 镜像 |
| Token 过期 / 刷新 | `AuthService` | 当前只明确了过期时间写入，没有独立 refresh 策略宿主 |

## 当前判定
- 当前仓库已经具备“弱网 / 离线时允许缓存降级”的实现入口。
- 当前仓库还没有把“Token 过期 / 刷新 / 恢复失败后的用户提示”升格为单独治理对象。
- 因此本项关闭的是：当前连续性语义已经明确、禁止伪恢复边界已经明确。

## 允许降级
- 可返回缓存数据或过期缓存，并明确这是 fallback 路径
- 可在 Token 无法恢复时回落到未登录态

## 禁止伪恢复
- 不能用 mock 用户态来伪装登录恢复成功
- 不能把网络失败静默吞掉并假装请求成功
- 不能把 Token 丢失当作正常已登录状态继续传播到 RN

## 当前缺口
- 缺独立 refresh token 策略宿主
- 缺统一弱网 / 离线 / Token 失效的用户提示目录
- 缺 Stage 5 级别的弱网 / 离线样本矩阵
