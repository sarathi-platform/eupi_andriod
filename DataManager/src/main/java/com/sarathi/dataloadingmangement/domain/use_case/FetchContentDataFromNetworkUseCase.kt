package com.sarathi.dataloadingmangement.domain.use_case

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sarathi.dataloadingmangement.R
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.model.mapper.ContentMapper
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import dagger.hilt.android.qualifiers.ApplicationContext


class FetchContentDataFromNetworkUseCase(
    private val repository: IDataLoadingScreenRepository,
    @ApplicationContext private val mContext: Application,
) {
    suspend fun invoke(): Boolean {
        try {
//            val languageId = "en"
            val contentEntities = mutableListOf<Content>()
//            val contentMangerRequest = ContentRequest(languageId, listOf())
//            val contentResponse = repository.fetchContentsFromServer(contentMangerRequest)
//            if (contentResponse.status.equals(
//                    "200",
//                    true
//                ) || contentResponse.status.equals("SUCCESS", true)
//            ) {
//                contentResponse.data?.let { content ->
//                    repository.deleteContentFromDB()
//                   for (content in contentResponse.data) {
//                        contentEntities.add(ContentMapper.getContent(content))
//                   }
//                    repository.saveContentToDB(contentEntities)
//                    return true
//                }
//                return false
//            } else {
//               return false
//            }false
            repository.deleteContentFromDB()
            val contentResponse = getContentData(mContext)
            contentResponse.forEach { respone ->
                contentEntities.add(ContentMapper.getContent(respone))
            }
            repository.saveContentToDB(contentEntities)
            return true
        } catch (ex: Exception) {
            throw ex
        }
        return false
    }

    suspend fun getContentDataFromDB(): List<Content> {
        return repository.getContentData()
    }
}

fun getContentData(context: Application): List<ContentResponse> {
    var contentResponse: List<ContentResponse>
    context.resources.openRawResource(R.raw.content).use {
        val listType = object : TypeToken<List<ContentResponse>>() {}.type
        contentResponse =
            Gson().fromJson(it.reader(), listType)
    }
    return contentResponse
}