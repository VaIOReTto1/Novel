# Phase 4 Wave Tracker

## 当前状态
- 当前阶段：`Phase 4`
- 当前状态：`planned`
- 当前激活波次：`Wave 1`
- 自治模式：`enabled`
- 默认编制：`1 Leader + 4 helpers`
- 当前建议下一原子主题：`Wave 1 / 包边界图与迁移映射初稿`

## Wave 总表
| Wave | Status | Goal | Primary Owners | Primary Locks | Exit Evidence |
| --- | --- | --- | --- | --- | --- |
| Wave 1 | `planned` | 建立边界骨架、拆分地图、BridgeFacade 外围映射 | `BridgeFacadeSplitAgent`, `FeatureBoundarySplitAgent` | `LOCK-BRIDGE-FACADE`, `LOCK-HOME-SEARCH-SPLIT` | 包边界图、职责切片图、delegate 映射表 |
| Wave 2 | `planned` | 收口 Bridge 与宿主页边界 | `BridgeFacadeSplitAgent`, `HostRiskQualityAgent` | `LOCK-BRIDGE-FACADE`, `LOCK-HOST-QUALITY` | BridgeFacade 接口、兼容映射、host 风险验证清单 |
| Wave 3 | `planned` | 拆 Home/Search/Cache 超大类 | `FeatureBoundarySplitAgent`, `CacheReaderLightAgent` | `LOCK-HOME-SEARCH-SPLIT`, `LOCK-CACHE-READER-LIGHT` | 拆分前后职责对照、定向测试、静态债结果 |
| Wave 4 | `planned` | Reader 轻触减重、mock 清理、阶段收尾 | `CacheReaderLightAgent`, `HostRiskQualityAgent`, `LeaderAgent` | `LOCK-CACHE-READER-LIGHT`, `LOCK-HOST-QUALITY`, `LOCK-REFRACTOR-DOCS` | mock 清单、closeout 文档、Phase 5 进入条件 |

## Wave Summary Rules
- 每次波次切换都必须：
  - 更新本文件
  - 更新 `decision-log.md`
  - 同步 `phase-3-4-validation-board.md`
- 每个波次至少要记录：
  - 当前状态
  - 目标
  - 已完成原子主题
  - 当前 blocker
  - 下一步

## 当前未关闭风险
- `profile-host / RN Host` 仍未形成正式验证证据
- `NavigationBridgeModule`、`HomeViewModel`、`SearchRepository`、`NetworkCacheManager` 尚未拆分
- 静态债第二轮收敛尚未开始

## 下一步
- 先输出 Wave 1 的：
  - 包边界图
  - 迁移映射表
  - 四个超大类的职责切片图
