package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain

import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository.IDataLoadingScreenRepository
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.sarathi.missionactivitytask.data.dao.ActivityTaskDao
import com.sarathi.missionactivitytask.data.dao.MissionActivityDao
import com.sarathi.missionactivitytask.data.dao.MissionDao
import com.sarathi.missionactivitytask.data.entities.ActivityTaskEntity
import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.data.entities.MissionEntity
import com.sarathi.missionactivitytask.models.response.MissionActivityModel
import com.sarathi.missionactivitytask.models.response.MissionResponseModel
import com.sarathi.missionactivitytask.models.response.MissionTaskModel
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    val apiInterface: ApiService,
    val missionDao: MissionDao,
    val missionActivityDao: MissionActivityDao,
    val activityTaskDao: ActivityTaskDao
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
                userId = "",
                missionId = mission.missionId
            )
            if (missionCount == 0) {
                missionDao.insertMission(
                    MissionEntity.getMissionEntity(
                        userId = "",
                        activityTaskSize = mission.activities.size,
                        mission = mission
                    )
                )
            } else {
                missionDao.updateMissionActiveStatus(mission.missionId, "")
            }
        }

    }

    override suspend fun saveMissionsActivityToDB(
        activities: List<MissionActivityModel>,
        missionId: Int
    ) {
        activities.forEach { missionActivityModel ->
            val activityCount = missionActivityDao.getActivityCount(
                userId = "",
                missionActivityModel.activityId
            )
            if (activityCount == 0) {
                missionActivityDao.insertMissionActivity(
                    MissionActivityEntity.getMissionActivityEntity(
                        "",
                        missionId,
                        missionActivityModel.tasks.size,
                        missionActivityModel
                    )
                )
            } else {
                missionActivityDao.updateActivityActiveStatus(
                    missionId,
                    "",
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
        activityTaskDao.softDeleteActivityTask("", activityId, missionId)
        activities.forEach { task ->
            val taskCount =
                activityTaskDao.getTaskByIdCount(
                    userId = "",
                    taskId = task.id ?: 0
                )
            if (taskCount == 0) {
                activityTaskDao.insertActivityTask(
                    ActivityTaskEntity.getActivityTaskEntity(
                        userId = "",
                        missionId = missionId,
                        activityId = activityId,
                        activityName = activityName,
                        task = task,
                    )
                )
            } else {
                activityTaskDao.updateActiveTaskStatus(1, task.id ?: 0, "")
            }

        }
    }
}