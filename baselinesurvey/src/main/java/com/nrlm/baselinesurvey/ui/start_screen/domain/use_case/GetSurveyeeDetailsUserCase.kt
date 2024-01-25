package com.nrlm.baselinesurvey.ui.start_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.ui.start_screen.domain.repository.StartScreenRepository

class GetSurveyeeDetailsUserCase (private val repository: StartScreenRepository) {

    suspend fun invoke(didiId: Int): SurveyeeEntity {
        return repository.getSurveyeeDetails(didiId)
    }

}
