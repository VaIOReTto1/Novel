package com.novel.page.read.utils

/**
 * 阅读器模块日志标签管理
 * 
 * 统一管理阅读器相关组件的日志标签，便于调试和日志过滤
 */
object ReaderLogTags {
    
    // ViewModel层
    const val READER_VIEW_MODEL = "ReaderViewModel"
    const val READER_PERFORMANCE_PROBE = "ReaderPerfProbe"
    
    // Service层
    const val CHAPTER_SERVICE = "ChapterService"
    const val PAGINATION_SERVICE = "PaginationService"
    const val PROGRESS_SERVICE = "ProgressService"
    const val SETTINGS_SERVICE = "SettingsService"
    
    // UseCase层
    const val INIT_READER_USE_CASE = "InitReaderUseCase"
    const val FLIP_PAGE_USE_CASE = "FlipPageUseCase"
    const val SWITCH_CHAPTER_USE_CASE = "SwitchChapterUseCase"
    const val SEEK_PROGRESS_USE_CASE = "SeekProgressUseCase"
    const val UPDATE_SETTINGS_USE_CASE = "UpdateSettingsUseCase"
    const val SAVE_PROGRESS_USE_CASE = "SaveProgressUseCase"
    const val PAGINATE_CHAPTER_USE_CASE = "PaginateChapterUseCase"
    const val PRELOAD_CHAPTERS_USE_CASE = "PreloadChaptersUseCase"
    const val SPLIT_CONTENT_USE_CASE = "SplitContentUseCase"
    const val BUILD_VIRTUAL_PAGES_USE_CASE = "BuildVirtualPagesUseCase"
    const val OBSERVE_PAGINATION_PROGRESS_USE_CASE = "ObservePaginationProgressUseCase"
    
    // Repository层
    const val READING_PROGRESS_REPOSITORY = "ReadingProgressRepo"
    const val BOOK_CACHE_MANAGER = "BookCacheManager"
    
    // Utils层
    const val PAGE_SPLITTER = "PageSplitter"
} 
