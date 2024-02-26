package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import android.util.Log
import com.nrlm.baselinesurvey.SUCCESS_CODE
import com.nrlm.baselinesurvey.model.request.SurveyRequestBodyModel
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchSurveyFromNetworkUseCase(
    private val repository: DataLoadingScreenRepository
) {
    suspend operator fun invoke(surveyRequestBodyModel: SurveyRequestBodyModel): Boolean {
        try {
            //TODO Run a loop on language id later
            val surveyApiResponse = repository.fetchSurveyFromNetwork(surveyRequestBodyModel)
            if (surveyApiResponse.status.equals(SUCCESS_CODE, true)) {
                surveyApiResponse.data?.let { surveyApiResponse ->
//                    for (survey in surveyApiResponse) {
                    Log.d("invoke", "surveyApiResponse.sections.find -> ${surveyApiResponse.sections.find { it.sectionId == 8 }} \n" +
                            "\n" +
                            "\n")
                    repository.saveSurveyToDb(surveyApiResponse, languageId = 2)
//                    }
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
