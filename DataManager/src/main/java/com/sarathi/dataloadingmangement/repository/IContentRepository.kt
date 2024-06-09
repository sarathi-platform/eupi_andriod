package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.response.ContentResponse

interface IContentRepository {

    suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>>
    suspend fun saveContentToDB(contents: List<Content>)
    suspend fun deleteContentFromDB()
    suspend fun getContentData(): List<Content>
    suspend fun getAllContentKeys(): List<String>
    suspend fun getSelectedAppLanguage(): String
}