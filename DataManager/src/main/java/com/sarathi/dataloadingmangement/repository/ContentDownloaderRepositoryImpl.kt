package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.entities.Content
import javax.inject.Inject

class ContentDownloaderRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    IContentDownloader {
    override suspend fun getContentDataFromDb(): List<Content> = contentDao.getContentData()
    override suspend fun getContentValue(contentKey: String): String {
        return contentDao.getContentValue(contentKey, coreSharedPrefs.getAppLanguage())
    }

    override suspend fun getLimitedContentData(limit: Int): List<Content> {
        return contentDao.getLimitedData(limit)
    }

    override suspend fun getContentCount(): Int {
        return contentDao.getContentCount()
    }

}