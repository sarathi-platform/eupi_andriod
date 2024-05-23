package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.data.dao.ActivityTaskDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.MissionActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.MissionActivityEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.model.ApiResponseModel
import com.sarathi.dataloadingmangement.model.ContentResponse
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import com.sarathi.dataloadingmangement.response.MissionActivityModel
import com.sarathi.dataloadingmangement.response.MissionResponseModel
import com.sarathi.dataloadingmangement.response.MissionTaskModel
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    private val apiInterface: DataLoadingApiService,
    private val missionDao: MissionDao,
    private val missionActivityDao: MissionActivityDao,
    private val activityTaskDao: ActivityTaskDao,
    private val contentDao: ContentDao
) : IDataLoadingScreenRepository {
    override suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponseModel>> {
        val missionRequest = MissionRequest(languageCode, missionName)
        return apiInterface.getBaseLineMission(missionRequest)
    }

    override suspend fun saveMissionToDB(missions: List<MissionResponseModel>) {
        missionDao.softDeleteMission("")
        missions.forEach { mission ->
            val missionCount = missionDao.getMissionCount(
                userId = "99",
                missionId = mission.missionId
            )
            if (missionCount == 0) {
                missionDao.insertMission(
                    MissionEntity.getMissionEntity(
                        userId = "99",
                        activityTaskSize = mission.activities.size,
                        mission = mission
                    )
                )
            } else {
                missionDao.updateMissionActiveStatus(mission.missionId, "99")
            }
        }

    }

    override suspend fun saveMissionsActivityToDB(
        activities: List<MissionActivityModel>,
        missionId: Int
    ) {
        activities.forEach { missionActivityModel ->
            val activityCount = missionActivityDao.getActivityCount(
                userId = "99",
                missionActivityModel.activityId
            )
            if (activityCount == 0) {
                missionActivityDao.insertMissionActivity(
                    MissionActivityEntity.getMissionActivityEntity(
                        "99",
                        missionId,
                        missionActivityModel.tasks.size,
                        missionActivityModel
                    )
                )
            } else {
                missionActivityDao.updateActivityActiveStatus(
                    missionId,
                    "99",
                    1,
                    missionActivityModel.activityId
                )
            }
        }
    }

    override fun saveMissionsActivityTaskToDB(
        missionId: Int,
        activityId: Int,
        activityName: String,
        activities: List<MissionTaskModel>
    ) {
        activityTaskDao.softDeleteActivityTask("99", activityId, missionId)
        activities.forEach { task ->
            val taskCount =
                activityTaskDao.getTaskByIdCount(
                    userId = "99",
                    taskId = task.id ?: 0
                )
            if (taskCount == 0) {
                activityTaskDao.insertActivityTask(
                    ActivityTaskEntity.getActivityTaskEntity(
                        userId = "99",
                        missionId = missionId,
                        activityId = activityId,
                        activityName = activityName,
                        task = task,
                    )
                )
            } else {
                activityTaskDao.updateActiveTaskStatus(1, task.id ?: 0, "99")
            }

        }
    }

    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>> {
        return apiInterface.getContent(contentMangerRequest)
    }

    override suspend fun saveContentToDB(contents: List<Content>) {
        contentDao.insertContent(contents)
    }

    override suspend fun deleteContentFromDB() {
        contentDao.deleteContent()
    }

    override suspend fun getContentData(): List<Content> {
        return contentDao.getContentData()
    }
}