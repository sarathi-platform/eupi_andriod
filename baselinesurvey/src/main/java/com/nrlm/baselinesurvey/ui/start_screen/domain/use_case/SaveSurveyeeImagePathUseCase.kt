package com.nrlm.baselinesurvey.ui.start_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.start_screen.domain.repository.StartScreenRepository

class SaveSurveyeeImagePathUseCase(
    private val repository: StartScreenRepository
) {

    suspend fun invoke(surveyeeEntity: SurveyeeEntity, finalPathWithCoordinates: String) {
        repository.saveImageLocalPathForSurveyee(surveyeeEntity, finalPathWithCoordinates)
    }

}
