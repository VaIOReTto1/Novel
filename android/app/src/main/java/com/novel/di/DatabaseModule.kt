package com.novel.di

import android.content.Context
import com.novel.utils.TimberLogger
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.novel.page.login.dao.UserDao
import com.novel.page.home.dao.HomeDao
import com.novel.utils.dao.NovelDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * 数据库依赖注入模块
 *
 * 负责配置和提供小说应用的本地数据库相关依赖：
 * - Room 数据库实例的创建和配置
 * - 各功能模块 DAO 对象的提供
 * - 数据库迁移策略的设置
 * - 性能优化配置（WAL 模式、全文搜索、索引优化）
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val TAG = "DatabaseModule"
    private const val DATABASE_NAME = "kxq.db"

    /**
     * 提供 Room 数据库实例
     * @param ctx 应用上下文
     * @return NovelDatabase 单例
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): NovelDatabase {
        TimberLogger.d(TAG, "创建 Room 数据库实例: $DATABASE_NAME")

        return Room.databaseBuilder(ctx, NovelDatabase::class.java, DATABASE_NAME)
            // 启用 WAL 模式，提升并发写入性能
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            // 开发阶段使用破坏性迁移（版本更新时清空重建）
            .fallbackToDestructiveMigration()
            // 使用 IO 线程执行查询与事务
            .setQueryExecutor(Dispatchers.IO.asExecutor())
            .setTransactionExecutor(Dispatchers.IO.asExecutor())
            .addCallback(object : RoomDatabase.Callback() {
                /**
                 * 首次创建数据库时执行：索引与全文搜索表创建
                 */
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    TimberLogger.d(TAG, "数据库首次创建，建立索引与全文搜索表")
                    createPerformanceIndexes(db)
                    createFTS4Tables(db)
                }

                /**
                 * 每次打开数据库时执行：性能相关 PRAGMA 配置
                 */
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    TimberLogger.d(TAG, "数据库打开，应用性能优化 PRAGMA")
                    // 对可能返回结果的 PRAGMA 使用 query 并关闭 Cursor
                    db.query("PRAGMA synchronous=NORMAL", emptyArray<Any?>()).close()
                    db.query("PRAGMA cache_size=-10000", emptyArray<Any?>()).close()
                    db.query("PRAGMA mmap_size=268435456", emptyArray<Any?>()).close()
                    db.query("PRAGMA temp_store=MEMORY", emptyArray<Any?>()).close()
                }
            })
            .build()
    }

    /**
     * 创建性能优化索引，表名需与 @Entity(tableName) 一致
     */
    private fun createPerformanceIndexes(db: SupportSQLiteDatabase) {
        TimberLogger.d(TAG, "创建性能优化索引")
        try {
            // HomeBook 实体对应表名 home_books
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS idx_home_book_category_type_sort
                ON home_books(category, type, sortOrder)
                """.trimIndent()
            )
            // 用户表 users
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS idx_user_last_update_time
                ON users(lastUpdateTime DESC)
                """.trimIndent()
            )
            // 书单横幅表 home_banners
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS idx_banner_position
                ON home_banners(position ASC)
                """.trimIndent()
            )
            // 分类表 home_categories
            db.execSQL(
                """
                CREATE INDEX IF NOT EXISTS idx_category_order
                ON home_categories(sortOrder ASC)
                """.trimIndent()
            )
            TimberLogger.d(TAG, "性能优化索引创建完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "创建性能优化索引失败", e)
        }
    }

    /**
     * 使用 FTS4 建立全文搜索表与触发器
     */
    private fun createFTS4Tables(db: SupportSQLiteDatabase) {
        TimberLogger.d(TAG, "创建 FTS4 全文搜索表与触发器")
        try {
            // 对 home_books 表建立 FTS4 虚拟表
            db.execSQL(
                """
                CREATE VIRTUAL TABLE IF NOT EXISTS book_fts
                USING fts4(
                    book_id UNINDEXED,
                    title,
                    author,
                    description,
                    tags,
                    content='home_books',
                    content_rowid='id'
                )
                """.trimIndent()
            )
            // 插入触发器
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS book_fts_insert
                AFTER INSERT ON home_books
                BEGIN
                  INSERT INTO book_fts(book_id, title, author, description, tags)
                  VALUES (new.id, new.title, new.author, new.description, new.tags);
                END;
                """.trimIndent()
            )
            // 更新触发器
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS book_fts_update
                AFTER UPDATE ON home_books
                BEGIN
                  UPDATE book_fts SET
                    title = new.title,
                    author = new.author,
                    description = new.description,
                    tags = new.tags
                  WHERE book_id = new.id;
                END;
                """.trimIndent()
            )
            // 删除触发器
            db.execSQL(
                """
                CREATE TRIGGER IF NOT EXISTS book_fts_delete
                AFTER DELETE ON home_books
                BEGIN
                  DELETE FROM book_fts WHERE book_id = old.id;
                END;
                """.trimIndent()
            )
            TimberLogger.d(TAG, "FTS4 全文搜索表创建完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "创建 FTS4 全文搜索表失败", e)
        }
    }

    /**
     * 提供 UserDao 实例
     */
    @Provides
    fun provideUserDao(db: NovelDatabase): UserDao {
        TimberLogger.d(TAG, "提供 UserDao 实例")
        return db.userDao()
    }

    /**
     * 提供 HomeDao 实例
     */
    @Provides
    fun provideHomeDao(db: NovelDatabase): HomeDao {
        TimberLogger.d(TAG, "提供 HomeDao 实例")
        return db.homeDao()
    }
}
