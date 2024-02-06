package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.SUCCESS
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchSurveyFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(): Boolean {
        try {
            //TODO Run a loop on language id later
            val surveyRequestBodyModel =
                SurveyRequestBodyModel(languageId = 2, surveyName = "BASELINE", stateId = 4)
            val surveyApiResponse = repository.fetchSurveyFromNetwork(surveyRequestBodyModel)
            if (surveyApiResponse.status.equals(SUCCESS, true)) {
                surveyApiResponse.data?.let { surveyApiResponse ->
                    for (survey in surveyApiResponse) {
                        repository.saveSurveyToDb(survey, languageId = 2)
                    }
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
