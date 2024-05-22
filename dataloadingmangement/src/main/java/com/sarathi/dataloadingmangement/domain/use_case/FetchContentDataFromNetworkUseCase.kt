package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain.use_case

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.patsurvey.nudge.R
import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository.IDataLoadingScreenRepository
import com.patsurvey.nudge.model.mapper.ContentMapper
import com.patsurvey.nudge.utils.NudgeCore
import com.sarathi.contentmodule.model.ContentResponse
import com.sarathi.missionactivitytask.data.entities.Content


class FetchContentDataFromNetworkUseCase(private val repository: IDataLoadingScreenRepository) {
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
//                    for (content in contentResponse.data) {
//                        contentEntities.add(ContentMapper.getContent(content))
//                    }
//                    repository.saveContentToDB(contentEntities)
//                    return true
//                }
//                return false
//            } else {
//                return false
//            }
            val contentResponse = getContentData()
            contentResponse.forEach {
                repository.deleteContentFromDB()
                for (content in contentResponse) {
                    contentEntities.add(ContentMapper.getContent(content))
                }
                repository.saveContentToDB(contentEntities)
                return true
            }
        } catch (ex: Exception) {
            throw ex
        }
        return false
    }

    suspend fun getContentDataFromDB(): List<Content> {
        return repository.getContentData()
    }
}

fun getContentData(): List<ContentResponse> {
    var contentResponse: List<ContentResponse>
    NudgeCore.getAppContext().resources.openRawResource(R.raw.content).use {
        val listType = object : TypeToken<List<ContentResponse>>() {}.type
        contentResponse =
            Gson().fromJson(it.reader(), listType)
    }

    return contentResponse
}