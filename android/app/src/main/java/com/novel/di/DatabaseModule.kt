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
 * - Room数据库实例的创建和配置
 * - 各功能模块DAO对象的提供
 * - 数据库迁移策略的设置
 * - 性能优化配置（WAL模式、FTS5、索引优化）
 * 
 * 所有数据库相关组件都采用单例模式，确保数据一致性
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val TAG = "DatabaseModule"
    private const val DATABASE_NAME = "kxq.db"

    /**
     * 提供Room数据库实例
     * 
     * 优化配置：
     * - 数据库名称：kxq.db
     * - WAL模式：启用Write-Ahead Logging提升并发性能
     * - 查询执行器：使用IO调度器优化I/O性能
     * - 迁移策略：破坏性迁移（适用于开发阶段）
     * - 实例模式：应用级单例
     * - FTS5支持：全文搜索功能
     * 
     * @param ctx 应用上下文
     * @return NovelDatabase实例
     */
    @Provides 
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): NovelDatabase {
        TimberLogger.d(TAG, "创建Room数据库实例: $DATABASE_NAME")
        
        return Room.databaseBuilder(ctx, NovelDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration(true)  // 开发阶段使用破坏性迁移
            .setQueryExecutor(Dispatchers.IO.asExecutor()) // 使用IO线程池执行查询
            .setTransactionExecutor(Dispatchers.IO.asExecutor()) // 使用IO线程池执行事务
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    TimberLogger.d(TAG, "数据库创建完成，开始性能优化配置...")
                    
                    // 启用WAL模式提升并发性能
                    db.execSQL("PRAGMA journal_mode=WAL")
                    TimberLogger.d(TAG, "启用WAL模式")
                    
                    // 设置同步模式为NORMAL，平衡性能和安全性
                    db.execSQL("PRAGMA synchronous=NORMAL")
                    TimberLogger.d(TAG, "设置同步模式为NORMAL")
                    
                    // 增加缓存大小到10MB
                    db.execSQL("PRAGMA cache_size=-10000")
                    TimberLogger.d(TAG, "设置缓存大小为10MB")
                    
                    // 启用内存映射I/O
                    db.execSQL("PRAGMA mmap_size=268435456") // 256MB
                    TimberLogger.d(TAG, "启用内存映射I/O")
                    
                    // 设置临时存储为内存
                    db.execSQL("PRAGMA temp_store=memory")
                    TimberLogger.d(TAG, "设置临时存储为内存")
                    
                    // 创建性能监控相关的索引
                    createPerformanceIndexes(db)
                    
                    // 创建FTS5全文搜索表
                    createFTS5Tables(db)
                }
                
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    TimberLogger.d(TAG, "数据库打开，验证性能配置...")
                }
            })
            .build()
    }

    /**
     * 创建性能优化索引
     */
    private fun createPerformanceIndexes(db: SupportSQLiteDatabase) {
        TimberLogger.d(TAG, "创建性能优化索引...")
        
        try {
            // 为HomeBookEntity创建复合索引
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_home_book_category_rank 
                ON HomeBookEntity(category, ranking_type, rank_position)
            """)
            
            // 为用户查询优化创建索引
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_user_login_time 
                ON UserEntity(login_time DESC)
            """)
            
            // 为横幅显示优化创建索引
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_banner_position 
                ON HomeBannerEntity(position ASC)
            """)
            
            // 为分类查询优化创建索引
            db.execSQL("""
                CREATE INDEX IF NOT EXISTS idx_category_order 
                ON HomeCategoryEntity(category_order ASC)
            """)
            
            TimberLogger.d(TAG, "性能优化索引创建完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "创建性能优化索引失败", e)
        }
    }

    /**
     * 创建FTS5全文搜索表
     */
    private fun createFTS5Tables(db: SupportSQLiteDatabase) {
        TimberLogger.d(TAG, "创建FTS5全文搜索表...")
        
        try {
            // 创建书籍全文搜索表
            db.execSQL("""
                CREATE VIRTUAL TABLE IF NOT EXISTS book_fts 
                USING fts5(
                    book_id UNINDEXED,
                    title,
                    author,
                    description,
                    tags,
                    content='HomeBookEntity',
                    content_rowid='id'
                )
            """)
            
            // 创建FTS搜索触发器 - 插入
            db.execSQL("""
                CREATE TRIGGER IF NOT EXISTS book_fts_insert 
                AFTER INSERT ON HomeBookEntity 
                BEGIN
                    INSERT INTO book_fts(book_id, title, author, description, tags)
                    VALUES (NEW.id, NEW.title, NEW.author, NEW.description, NEW.tags);
                END
            """)
            
            // 创建FTS搜索触发器 - 更新
            db.execSQL("""
                CREATE TRIGGER IF NOT EXISTS book_fts_update 
                AFTER UPDATE ON HomeBookEntity 
                BEGIN
                    UPDATE book_fts SET
                        title = NEW.title,
                        author = NEW.author,
                        description = NEW.description,
                        tags = NEW.tags
                    WHERE book_id = NEW.id;
                END
            """)
            
            // 创建FTS搜索触发器 - 删除
            db.execSQL("""
                CREATE TRIGGER IF NOT EXISTS book_fts_delete 
                AFTER DELETE ON HomeBookEntity 
                BEGIN
                    DELETE FROM book_fts WHERE book_id = OLD.id;
                END
            """)
            
            TimberLogger.d(TAG, "FTS5全文搜索表创建完成")
        } catch (e: Exception) {
            TimberLogger.e(TAG, "创建FTS5全文搜索表失败", e)
        }
    }

    /**
     * 提供用户数据访问对象
     * 
     * @param db 数据库实例
     * @return UserDao - 用户相关数据操作接口
     */
    @Provides
    fun provideUserDao(db: NovelDatabase): UserDao {
        TimberLogger.d(TAG, "提供UserDao实例")
        return db.userDao()
    }

    /**
     * 提供首页数据访问对象
     * 
     * @param db 数据库实例  
     * @return HomeDao - 首页相关数据操作接口
     */
    @Provides
    fun provideHomeDao(db: NovelDatabase): HomeDao {
        TimberLogger.d(TAG, "提供HomeDao实例")
        return db.homeDao()
    }
}
