package com.nrlm.baselinesurvey.ui.common_components.common_domain.commo_repository

import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState

interface SurveyStateRepository {

    suspend fun updateSurveyState(didiId: Int, surveyState: SurveyState)
    suspend fun saveDidiInfo(didiIntoEntity: DidiIntoEntity)


}