package com.sarathi.contentmodule.content_downloder.domain.repository

import com.sarathi.contentmodule.ui.theme.repository.BaseRepository
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.entities.Content
import javax.inject.Inject

class ContentDownloaderRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao
) : BaseRepository(),
    IContentDownloader {
    override suspend fun getContentDataFromDb(): List<Content> = contentDao.getContentData()

}