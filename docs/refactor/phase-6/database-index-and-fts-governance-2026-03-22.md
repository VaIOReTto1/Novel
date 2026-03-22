# 数据库索引与 FTS4 治理报告

## 摘要
- 日期：`2026-03-22`
- 目标：为 `NovelDatabase` 建立第一版数据库治理报告入口，固定当前索引、FTS4 表/触发器与关键查询计划探针。
- 当前口径：
  - 已开始治理
  - 尚未完成“索引收益复盘 / FTS4 是否仍最佳”的最终结论

## 工具入口
- 运行时代码入口：
  - [DatabaseGovernanceReportGenerator.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/DatabaseGovernanceReportGenerator.kt)
- 关键输入：
  - [DatabaseModule.kt](/d:/program/Novel/android/app/src/main/java/com/novel/di/DatabaseModule.kt)
  - [NovelDatabase.kt](/d:/program/Novel/android/app/src/main/java/com/novel/utils/dao/NovelDatabase.kt)
  - [HomeDao.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/home/dao/HomeDao.kt)
  - [UserDao.kt](/d:/program/Novel/android/app/src/main/java/com/novel/page/login/dao/UserDao.kt)
  - [4.json](/d:/program/Novel/android/app/schemas/com.novel.utils.dao.NovelDatabase/4.json)

## 当前索引清单
| 表 | 索引名 | 来源 | 列 |
| --- | --- | --- | --- |
| `home_books` | `idx_home_book_category_type_sort` | `DatabaseModule` 显式索引 | `category, type, sortOrder` |
| `users` | `idx_user_last_update_time` | `DatabaseModule` 显式索引 | `lastUpdateTime DESC` |
| `home_banners` | `idx_banner_position` | `DatabaseModule` 显式索引 | `position ASC` |
| `home_categories` | `idx_category_order` | `DatabaseModule` 显式索引 | `sortOrder ASC` |

## FTS4 表与触发器状态
- 当前 `DatabaseModule` 在 `onCreate` 时会建立：
  - `book_fts`
- 当前已定义的同步触发器：
  - `book_fts_insert`
  - `book_fts_update`
  - `book_fts_delete`
- 当前绑定关系：
  - FTS4 内容源为 `home_books`
  - `content_rowid = id`

## 关键查询计划探针
- 当前工具默认固定三条 `EXPLAIN QUERY PLAN` 探针：
  - `home_books_by_type`
    - `SELECT * FROM home_books WHERE type = ? ORDER BY sortOrder ASC, updateTime DESC`
  - `home_categories_sorted`
    - `SELECT * FROM home_categories ORDER BY sortOrder ASC`
  - `user_by_uid`
    - `SELECT * FROM users WHERE uid = ?`
- 当前结论：
  - 探针入口已内置到报告生成器
  - 后续可直接对运行时数据库输出真实 `detail` 结果

## 当前判断
- 已完成：
  - 数据库治理报告生成器落地
  - 显式索引 / FTS4 / 触发器 / 关键查询探针固定成统一入口
- 未完成：
  - 索引收益的定量复盘
  - `FTS4` 与其他方案的横向比较
  - 真机/本地运行时 `EXPLAIN QUERY PLAN` 结果归档

## 残余风险
- 当前显式索引来自 `DatabaseModule` 的 `onCreate` 路径，若未来表结构调整，需同步校验索引与 FTS4 触发器的兼容性。
- 当前报告更多是“治理入口 + 结构基线”，不是最终性能收益证明。
