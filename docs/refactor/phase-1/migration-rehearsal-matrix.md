# Phase 1 - 迁移演练矩阵

## 目标
- 为 `V1-06` 提供可执行、可复现、可审计的迁移演练方案。
- 覆盖当前 Android 发布路径中最关键的三类持久化对象：
  - Room 数据库 `kxq.db`
  - 用户偏好 `Novel_user_defaults.xml`
  - 加密凭据 `Novel_keystore_prefs`

## 适用范围
- Android 本地升级路径验证
- 调试版与 release-like 包升级前演练
- 数据完整性、异常恢复、兼容性与回滚影响评估

## 当前持久化资产
| 资产 | 当前位置/来源 | 当前实现 | 关键风险 |
| --- | --- | --- | --- |
| Room 数据库 | `/data/user/0/com.novel/databases/kxq.db` | `NovelDatabase(version = 4)` | 发布路径此前默认 destructive migration |
| 用户偏好 | `/data/user/0/com.novel/shared_prefs/Novel_user_defaults.xml` | `SharedPrefsUserDefaults` | 后续若迁移到 DataStore，需验证兼容和回灌 |
| 加密凭据 | `/data/user/0/com.novel/shared_prefs/Novel_keystore_prefs.xml` | `EncryptedNovelKeyChain` | 设备密钥失效、升级异常和读取失败恢复 |

## 演练前提
- 当前代码基线：
  - `NovelDatabase.exportSchema = true`
  - `room.schemaLocation` 已配置
  - `schemas/com.novel.utils.dao.NovelDatabase/4.json` 已生成
- 演练环境：
  - 真机优先
  - 允许调试包 + release-like 包组合验证
  - 所有演练必须记录设备、系统、包版本、Git 提交号

## 演练样本来源

### 数据库样本
- 空数据库
- 有用户数据的数据库
- 有首页缓存数据的数据库
- 有阅读进度和历史数据的数据库

### SharedPreferences 样本
- 默认空偏好
- 含主题、亮度、字号、翻页效果的偏好集
- 含阅读历史、阅读进度 JSON 的偏好集
- 含异常值或缺字段样本

### KeyChain 样本
- 空 Token
- 仅 Access Token
- Access + Refresh Token
- 密钥不可用/读取异常模拟样本

## 演练矩阵

| 编号 | 类别 | 场景 | 输入样本 | 执行方式 | 预期结果 | 阻断条件 |
| --- | --- | --- | --- | --- | --- | --- |
| M1 | Room | 空数据库升级到当前版本 | 空 `kxq.db` | 安装旧包后升级到当前包 | 应正常创建或升级，无崩溃 | 启动崩溃、表结构缺失 |
| M2 | Room | 含用户数据数据库升级 | 用户表已有数据 | 升级后进入首页/登录路径 | 用户数据可读，DAO 查询正常 | 数据丢失、字段无法解析 |
| M3 | Room | 含首页缓存数据库升级 | `home_books/home_banners/home_categories` 有数据 | 升级后进入首页 | 首页缓存可读，不触发异常清库 | 缓存读取失败、首页崩溃 |
| M4 | Room | 旧 schema 升级到当前 schema | 使用旧 schema 导出的数据库副本 | 升级当前包并启动 | 需走显式迁移或明确失败提示 | 无提示清库、启动崩溃 |
| M5 | Room | 升级中断恢复 | 安装升级到一半中断/杀进程 | 重启应用继续进入 | 应可恢复到可启动状态 | 数据库损坏、应用无法启动 |
| M6 | SharedPrefs | 保留主题与阅读偏好 | 含 `fontSize/backgroundColor/pageFlipEffect` | 升级后进入阅读器/设置 | 偏好值延续 | 默认值覆盖、读取异常 |
| M7 | SharedPrefs | 保留阅读历史 | 含 `reading_history`、`reading_progress_data` | 升级后进入书架/阅读器 | 历史可解析，最近阅读正常恢复 | JSON 解析失败、历史丢失 |
| M8 | SharedPrefs | 异常值容错 | 注入缺字段/非法值 | 启动后进入设置/阅读器 | 能回退默认值，不崩溃 | 因非法值导致崩溃 |
| M9 | KeyChain | 保留 Access Token | 有 Access Token | 升级后触发需要登录态页面 | Token 仍可读取 | Token 丢失、读取异常 |
| M10 | KeyChain | 保留 Access + Refresh Token | 完整 Token 对 | 升级后触发网络请求 | 鉴权链路保持可用 | 鉴权失败、清空 Token |
| M11 | KeyChain | 加密读取失败容错 | 模拟 MasterKey 或加密读取失败 | 启动后进入需要登录态页面 | 应有错误日志与降级处理，不应闪退 | 应用崩溃 |
| M12 | 综合 | 三类资产同时存在并升级 | 数据库 + 偏好 + Token 全量样本 | 升级后走首页、书架、阅读器、设置 | 核心主路径不崩溃，数据基本完整 | 任一主路径不可用 |

## 每个场景的执行记录模板
- 日期：
- Git 提交：
- 设备/系统：
- 旧版本包：
- 新版本包：
- 输入样本：
- 操作步骤：
- 实际结果：
- 截图/日志路径：
- 是否通过：

## 推荐执行顺序
1. `M1 -> M2 -> M3`
2. `M6 -> M7 -> M8`
3. `M9 -> M10 -> M11`
4. 最后执行 `M12` 综合样本

## 通过标准
- 主路径不崩溃
- 不出现无提示清库
- 关键偏好值与凭据不应无故丢失
- 日志中不存在未处理异常
- 若发生降级，必须有明确日志和用户可恢复路径

## 当前结论
- 当前代码已经具备开始迁移演练的最小前提：schema 导出开启、debug 与 release-like 构建可用。
- 下一步应优先准备样本输入和具体执行记录，再补 `V1-06` 的首轮演练证据。
