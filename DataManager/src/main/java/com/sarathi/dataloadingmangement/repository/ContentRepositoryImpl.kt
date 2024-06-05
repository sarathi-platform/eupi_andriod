package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mat.response.ContentResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    val apiInterface: DataLoadingApiService,
    val contentDao: ContentDao,
) : IContentRepository {
    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>> {
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

}