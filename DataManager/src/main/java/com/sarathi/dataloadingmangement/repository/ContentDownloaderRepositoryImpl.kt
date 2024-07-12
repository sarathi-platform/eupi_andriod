package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.DIDI_IMAGE_ATTRIBUTE
import com.sarathi.dataloadingmangement.data.dao.AttributeValueReferenceDao
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.SubjectEntityDao
import com.sarathi.dataloadingmangement.data.entities.Content
import javax.inject.Inject

class ContentDownloaderRepositoryImpl @Inject constructor(
    private val contentDao: ContentDao,
    private val contentConfigDao: ContentConfigDao,
    private val attributeValueReferenceDao: AttributeValueReferenceDao,
    private val subjectEntityDao: SubjectEntityDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    IContentDownloader {
    override suspend fun getContentDataFromDb(): List<Content> = contentDao.getContentData()
    override suspend fun getSpecificContentDataFromDb(contentKeys: List<String>): List<Content> {
        return contentDao.getContentData(contentKeys, coreSharedPrefs.getUniqueUserIdentifier())
    }

    override suspend fun getContentValue(contentKey: String): String {
        return contentDao.getContentValue(
            contentKey,
            coreSharedPrefs.getAppLanguage(),
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getLimitedContentData(
        limit: Int,
        contentKeys: List<String>
    ): List<Content> {
        return contentDao.getLimitedData(
            limit,
            contentKeys,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getContentCount(contentKeys: List<String>): Int {
        return contentDao.getContentCount(contentKeys, coreSharedPrefs.getUniqueUserIdentifier())
    }

    override fun getContentKeyFromContentConfig(
        referenceID: Int,
        referenceType: Int
    ): List<String> {
        return contentConfigDao.getAllContentKey(
            referenceID = referenceID,
            referenceType = referenceType,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            languageCode = coreSharedPrefs.getAppLanguage()

        )
    }

    override suspend fun getDidiImagesUrl(): List<String> {
        return attributeValueReferenceDao.getAttributeValue(
            DIDI_IMAGE_ATTRIBUTE,
            coreSharedPrefs.getUniqueUserIdentifier()
        )
    }

    override suspend fun getDidiImageUrlForSmallGroup(): List<String> {
        return subjectEntityDao.getDidiImageUrlForSmallGroup(coreSharedPrefs.getUniqueUserIdentifier())
    }

}