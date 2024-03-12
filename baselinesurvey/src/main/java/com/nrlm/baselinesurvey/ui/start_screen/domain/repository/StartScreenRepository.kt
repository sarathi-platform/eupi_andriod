package com.nrlm.baselinesurvey.ui.start_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.DidiIntoEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity

interface StartScreenRepository {

    suspend fun getSurveyeeDetails(didiId: Int): SurveyeeEntity
    suspend fun getDidiInfoDetails(didiId: Int): DidiIntoEntity

    suspend fun saveImageLocalPathForSurveyee(surveyeeEntity: SurveyeeEntity, finalPathWithCoordinates: String)

    suspend fun getDidiInfoObjectLive(didiId: Int): LiveData<List<DidiIntoEntity>>
    fun getStateId(): Int

}