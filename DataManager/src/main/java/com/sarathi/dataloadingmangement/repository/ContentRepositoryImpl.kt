package com.sarathi.dataloadingmangement.repository

import com.nudge.core.DEFAULT_LANGUAGE_CODE
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ContentConfigDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.UiConfigDao
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val contentDao: ContentDao,
    val contentConfigDao: ContentConfigDao,
    val uiConfigDao: UiConfigDao,
    val coreSharedPrefs: CoreSharedPrefs
) : IContentRepository {
    override suspend fun fetchContentsFromServer(contentMangerRequest: List<ContentRequest>): ApiResponseModel<List<ContentResponse>> {
        return apiInterface.fetchContentData(contentMangerRequest)
    }

    override suspend fun saveContentToDB(contents: List<Content>) {
        contentDao.insertContent(contents)
    }

    override suspend fun deleteContentFromDB() {
        contentDao.deleteContent()
    }

    override suspend fun getContentData(): List<Content> {
        return contentDao.getContentData()
    }

    override suspend fun getAllContentRequest(): List<ContentRequest> {
        val contentRequests = ArrayList<ContentRequest>()
        contentConfigDao.getAllContentKey(
            coreSharedPrefs.getUniqueUserIdentifier(),
        ).forEach {
            contentRequests.add(ContentRequest(languageCode = it.languageCode, contentKey = it.key))
        }

        uiConfigDao.getAllIconsKey().forEach {
            contentRequests.add(
                ContentRequest(
                    languageCode = DEFAULT_LANGUAGE_CODE,
                    contentKey = it
                )
            )

        }
        return contentRequests
    }



    override suspend fun getSelectedAppLanguage(): String {
        return coreSharedPrefs.getAppLanguage()
    }

}