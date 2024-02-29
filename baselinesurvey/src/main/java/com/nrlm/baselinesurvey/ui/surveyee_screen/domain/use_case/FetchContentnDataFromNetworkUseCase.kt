package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.database.entity.ContentEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository

class FetchContentnDataFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {
    suspend operator fun invoke(): Boolean {
        try {
            //TODO Run a loop on language id later
            var contentEntities = mutableListOf<ContentEntity>()
            val contentResponse = repository.fetchContentsFromServer()
            if (contentResponse.status.equals(SUCCESS_CODE, true)) {
                contentResponse.data?.let { content ->
                    repository.deleteContentFromDB()
                    for (content in contentResponse.data) {
                        contentEntities.add(ContentEntity.getContentEntity(content))
                    }
                    repository.saveContentsToDB(contentEntities)
                    return true
                }
                return false
            } else {
                return false
            }
        } catch (ex: Exception) {
            //BaselineLogger.e("FetchSurveyFromNetworkUseCase", "invoke", ex)
            return false
        }
    }
}