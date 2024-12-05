package com.sarathi.dataloadingmangement.repository

import com.nudge.core.model.ApiResponseModel
import com.sarathi.dataloadingmangement.data.entities.ActivityConfigEntity
import com.sarathi.dataloadingmangement.model.survey.response.SectionStatusResponseModel

interface ISectionStatusRepository {

    suspend fun fetchSectionStatusFromNetwork(
        activityConfigEntity: ActivityConfigEntity
    ): ApiResponseModel<List<SectionStatusResponseModel>>

    suspend fun saveSectionStatusIntoDb(
        sectionStatus: List<SectionStatusResponseModel>,
        missionId: Int
    )

    suspend fun getSurveyIdForMission(missionId: Int): List<Int>

    suspend fun getActivityConfigForMission(missionId: Int): List<ActivityConfigEntity>?

}