package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.DataLoadingScreenRepository
import com.nrlm.baselinesurvey.utils.BaselineLogger

class FetchSurveyAnswerFromNetworkUseCase(private val repository: DataLoadingScreenRepository) {

    suspend operator fun invoke() {

        try {
            repository.getSurveyAnswers()

        } catch (ex: Exception) {
            BaselineLogger.e("FetchCastesFromNetworkUseCase", "invoke", ex)
        }
    }

}