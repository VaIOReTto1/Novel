package com.novel.page.read.di

import com.novel.page.read.service.ChapterService
import com.novel.page.read.service.PaginationService
import com.novel.page.read.service.ProgressService
import com.novel.page.read.service.SettingsService
import com.novel.page.read.service.common.DispatcherProvider
import com.novel.page.read.service.common.ServiceLogger
import com.novel.page.read.usecase.BuildVirtualPagesUseCase
import com.novel.page.read.usecase.FlipPageUseCase
import com.novel.page.read.usecase.InitReaderUseCase
import com.novel.page.read.usecase.LoadBookReviewsUseCase
import com.novel.page.read.usecase.ObservePaginationProgressUseCase
import com.novel.page.read.usecase.PaginateChapterUseCase
import com.novel.page.read.usecase.PreloadChaptersUseCase
import com.novel.page.read.usecase.SaveProgressUseCase
import com.novel.page.read.usecase.SeekProgressUseCase
import com.novel.page.read.usecase.SplitContentUseCase
import com.novel.page.read.usecase.SwitchChapterUseCase
import com.novel.page.read.usecase.UpdateSettingsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ReaderModule {



    @Provides
    @ViewModelScoped
    fun provideSplitContentUseCase(
        paginationService: PaginationService,
        chapterService: ChapterService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): SplitContentUseCase {
        return SplitContentUseCase(paginationService, chapterService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideBuildVirtualPagesUseCase(
        chapterService: ChapterService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): BuildVirtualPagesUseCase {
        return BuildVirtualPagesUseCase(chapterService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideInitReaderUseCase(
        settingsService: SettingsService,
        chapterService: ChapterService,
        progressService: ProgressService,
        paginateChapterUseCase: PaginateChapterUseCase,
        preloadChaptersUseCase: PreloadChaptersUseCase,
        splitContentUseCase: SplitContentUseCase,
        paginationService: PaginationService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): InitReaderUseCase {
        return InitReaderUseCase(
            settingsService,
            chapterService,
            progressService,
            paginateChapterUseCase,
            preloadChaptersUseCase,
            splitContentUseCase,
            paginationService,
            dispatchers,
            logger
        )
    }

    @Provides
    @ViewModelScoped
    fun provideFlipPageUseCase(
        switchChapterUseCase: SwitchChapterUseCase,
        preloadChaptersUseCase: PreloadChaptersUseCase,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): FlipPageUseCase {
        return FlipPageUseCase(switchChapterUseCase, preloadChaptersUseCase, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideSwitchChapterUseCase(
        chapterService: ChapterService,
        paginateChapterUseCase: PaginateChapterUseCase,
        preloadChaptersUseCase: PreloadChaptersUseCase,
        saveProgressUseCase: SaveProgressUseCase,
        splitContentUseCase: SplitContentUseCase,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): SwitchChapterUseCase {
        return SwitchChapterUseCase(
            chapterService,
            paginateChapterUseCase,
            preloadChaptersUseCase,
            saveProgressUseCase,
            splitContentUseCase,
            dispatchers,
            logger
        )
    }

    @Provides
    @ViewModelScoped
    fun provideSeekProgressUseCase(
        paginationService: PaginationService,
        chapterService: ChapterService,
        paginateChapterUseCase: PaginateChapterUseCase,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): SeekProgressUseCase {
        return SeekProgressUseCase(paginationService, chapterService, paginateChapterUseCase, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveProgressUseCase(
        progressService: ProgressService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): SaveProgressUseCase {
        return SaveProgressUseCase(progressService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateSettingsUseCase(
        settingsService: SettingsService,
        paginateChapterUseCase: PaginateChapterUseCase,
        splitContentUseCase: SplitContentUseCase,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): UpdateSettingsUseCase {
        return UpdateSettingsUseCase(settingsService, paginateChapterUseCase, splitContentUseCase, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun providePaginateChapterUseCase(
        paginationService: PaginationService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): PaginateChapterUseCase {
        return PaginateChapterUseCase(paginationService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun providePreloadChaptersUseCase(
        chapterService: ChapterService,
        paginationService: PaginationService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): PreloadChaptersUseCase {
        return PreloadChaptersUseCase(chapterService, paginationService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideObservePaginationProgressUseCase(
        paginationService: PaginationService,
        dispatchers: DispatcherProvider,
        logger: ServiceLogger
    ): ObservePaginationProgressUseCase {
        return ObservePaginationProgressUseCase(paginationService, dispatchers, logger)
    }

    @Provides
    @ViewModelScoped
    fun provideLoadBookReviewsUseCase(
        bookService: com.novel.utils.network.api.front.BookService,
        dispatchers: com.novel.page.read.service.common.DispatcherProvider,
        logger: com.novel.page.read.service.common.ServiceLogger
    ): LoadBookReviewsUseCase {
        return LoadBookReviewsUseCase(bookService, dispatchers, logger)
    }
}
