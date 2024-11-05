package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.survey.response.SectionStatusResponseModel

interface ISectionStatusRepository {

    suspend fun fetchSectionStatusFromNetwork(
        missionId: Int,
        surveyId: Int
    ): ApiResponseModel<List<SectionStatusResponseModel>>

    suspend fun saveSectionStatusIntoDb(
        sectionStatus: List<SectionStatusResponseModel>,
        missionId: Int
    )

    suspend fun getSurveyIdForMission(missionId: Int): List<Int>

}