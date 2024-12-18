package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.survey.response.ContentList
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.ContentResponse

interface IContentRepository {

    suspend fun fetchContentsFromServer(contentMangerRequest: List<ContentRequest>): ApiResponseModel<List<ContentResponse>>
    suspend fun saveContentToDB(contents: List<Content>)
    suspend fun deleteContentFromDB()
    suspend fun getContentData(): List<Content>
    suspend fun getAllContentRequest(): List<ContentRequest>
    suspend fun getSelectedAppLanguage(): String
    suspend fun getContentList(
        contentKey: String,
        languageCode: String
    ): ContentList?
}