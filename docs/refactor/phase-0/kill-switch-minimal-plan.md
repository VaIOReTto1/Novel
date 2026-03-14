# Phase 0 - Kill Switch 最小方案

## 1. 目标
- 为后续 Phase 1-3 的结构改造提供最小可回滚能力。
- 避免在网络栈、Bridge、WebView、安全策略等改动中只能依赖整包回滚。

## 2. 最小开关范围
| 开关 | 目标 | 默认 |
| --- | --- | --- |
| 网络主通路开关 | 在旧网络栈与新网络栈之间切换 | 旧路径 |
| Bridge 路径开关 | 在旧 Bridge 行为与收口后的适配层之间切换 | 旧路径 |
| WebView 安全策略开关 | 白名单/SSL/外链严格策略的灰度控制 | 兼容模式 |
| 预热/预加载策略开关 | RN 预热、WebView 预热、缓存预加载的风险隔离 | 关闭或现状兼容 |

## 3. 推荐形态

### 本地开关
- 用于开发与回归：
  - `BuildConfig`
  - `gradle.properties`
  - debug menu

### 远程开关
- 用于线上灰度与止损：
  - 远程配置中心或轻量配置下发
  - 支持默认值与兜底值

### 只读配置提供器
- 建议统一抽象为 `FeatureFlagProvider` / `SecureConfigProvider`
- 上层只读，不允许页面层直接散落读取多个配置源

## 4. 使用原则
- 只有架构切换、协议切换、安全策略切换等高风险主题才允许引入 kill switch。
- kill switch 不是长期双轨借口，必须在阶段关闭前清理或收敛。
- 每个开关必须定义：
  - 名称
  - 作用范围
  - 默认值
  - 回滚场景
  - 失效时间

## 5. Phase 1-3 推荐开关
| 阶段 | 开关 | 用途 |
| --- | --- | --- |
| Phase 1 | release 安全策略开关 | WebView/网络严格策略的灰度验证 |
| Phase 1 | endpoint 配置来源开关 | 从硬编码迁移到配置提供器时的过渡 |
| Phase 2 | 测试数据来源开关 | online/fake fixture 的切换 |
| Phase 3 | 网络主栈切换开关 | `ApiService` 与统一 Retrofit 主栈过渡 |
| Phase 3 | Bridge 适配层开关 | 新旧 BridgeFacade 路径切换 |

## 6. 当前结论
- 当前仓库还没有统一的 feature flag / kill switch 提供层。
- 若后续不先建立最小方案，任何高风险重构都只能依赖 Git 回滚或整包回退，风险过高。
- 本文可作为 `V0-05` 的一部分证据使用。
