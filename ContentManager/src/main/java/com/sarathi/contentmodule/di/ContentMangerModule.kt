package com.sarathi.contentmodule.di

import com.sarathi.contentmodule.content_downloder.domain.repository.ContentDownloaderRepositoryImpl
import com.sarathi.contentmodule.content_downloder.domain.repository.IContentDownloader
import com.sarathi.contentmodule.content_downloder.domain.usecase.ContentDownloaderUseCase
import com.sarathi.contentmodule.content_downloder.domain.usecase.ContentUseCase
import com.sarathi.contentmodule.download_manager.DownloaderManager
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ContentMangerModule {
    @Provides
    @Singleton
    fun provideContentDownloaderRepositoryImpl(
        contentDao: ContentDao,
    ): IContentDownloader {
        return ContentDownloaderRepositoryImpl(
            contentDao
        )
    }

    @Provides
    @Singleton
    fun provideContentUseCase(
        repository: ContentDownloaderRepositoryImpl,
        downloaderManager: DownloaderManager,
    ): ContentUseCase {
        return ContentUseCase(
            contentDownloaderUseCase = ContentDownloaderUseCase(repository, downloaderManager),
        )
    }
}