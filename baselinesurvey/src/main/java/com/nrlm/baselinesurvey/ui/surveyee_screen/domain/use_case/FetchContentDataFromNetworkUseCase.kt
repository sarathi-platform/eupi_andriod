package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.model.mappers.ContentEntityMapper
import com.nrlm.baselinesurvey.model.request.ContentMangerRequest
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchContentDataFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {
    suspend operator fun invoke(): Boolean {
        try {
            //TODO Run a loop on language id later
            val contentKeys =
                repository.getContentKeyFromDB().filter { it -> it != "" && it != "question" }
            val languageId = repository.getSelectedLanguageId()
            val contentEntities = mutableListOf<ContentEntity>()
            val contentMangerRequest = ContentMangerRequest(languageId, contentKeys)
            val contentResponse = repository.fetchContentsFromServer(contentMangerRequest)
            if (contentResponse.status.equals(
                    SUCCESS_CODE,
                    true
                ) || contentResponse.status.equals("SUCCESS", true)
            ) {
                contentResponse.data?.let { content ->
                    repository.deleteContentFromDB()
                    for (content in contentResponse.data) {
                        if (content != null) {
                            contentEntities.add(ContentEntityMapper.getContentEntity(content))
                        }
                    }
                    repository.saveContentsToDB(contentEntities)
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (ex: Exception) {
            BaselineLogger.e("FetchSurveyFromNetworkUseCase", "invoke", ex)
            return false
        }
    }
}