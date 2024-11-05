package com.sarathi.dataloadingmangement.repository

import com.nudge.core.BLANK_STRING
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityConfigDao
import com.sarathi.dataloadingmangement.data.dao.SectionStatusEntityDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.SectionStatusEntity
import com.sarathi.dataloadingmangement.model.survey.request.SectionStatusRequest
import com.sarathi.dataloadingmangement.model.survey.response.SectionStatusResponseModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import javax.inject.Inject


class SectionStatusRepositoryImpl @Inject constructor(
    private val sectionStatusDao: SectionStatusEntityDao,
    private val dataLoadingApiService: DataLoadingApiService,
    private val activityConfigDao: ActivityConfigDao,
    private val taskDao: TaskDao,
    val coreSharedPrefs: CoreSharedPrefs
) : ISectionStatusRepository {
    override suspend fun fetchSectionStatusFromNetwork(
        missionId: Int,
        surveyId: Int
    ): ApiResponseModel<List<SectionStatusResponseModel>> {

        return dataLoadingApiService.getSectionStatus(
            SectionStatusRequest(
                sectionId = 0,
                surveyId = surveyId,
                userId = coreSharedPrefs.getUserId().toIntOrNull() ?: 0
            )
        )

    }

    override suspend fun saveSectionStatusIntoDb(
        sectionStatus: List<SectionStatusResponseModel>,
        missionId: Int
    ) {
        sectionStatus.forEach {

            sectionStatusDao.addSectionStatus(
                SectionStatusEntity.geSectionStatusEntity(
                    missionId = missionId,
                    sectionStatus = it,
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    taskId = taskDao.getTaskIdFromLocalTaskId(
                        localTaskId = it.localTaskId ?: BLANK_STRING,
                        userId = coreSharedPrefs.getUniqueUserIdentifier()
                    )
                )
            )
        }

    }

    override suspend fun getSurveyIdForMission(missionId: Int): List<Int> {
        return activityConfigDao.getSurveyIds(
            missionId,
            userId = coreSharedPrefs.getUniqueUserIdentifier()
        )
    }


}
