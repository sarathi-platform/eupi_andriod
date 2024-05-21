package com.patsurvey.nudge.activities.ui.login.dataloadingscreen.domain

import com.patsurvey.nudge.activities.ui.login.dataloadingscreen.repository.IDataLoadingScreenRepository
import com.patsurvey.nudge.model.response.ApiResponseModel
import com.patsurvey.nudge.network.interfaces.ApiService
import com.sarathi.contentmodule.model.ContentResponse
import com.sarathi.contentmodule.request.ContentRequest
import com.sarathi.missionactivitytask.data.dao.ActivityTaskDao
import com.sarathi.missionactivitytask.data.dao.ContentDao
import com.sarathi.missionactivitytask.data.dao.MissionActivityDao
import com.sarathi.missionactivitytask.data.dao.MissionDao
import com.sarathi.missionactivitytask.data.entities.ActivityTaskEntity
import com.sarathi.missionactivitytask.data.entities.Content
import com.sarathi.missionactivitytask.data.entities.MissionActivityEntity
import com.sarathi.missionactivitytask.data.entities.MissionEntity
import com.sarathi.missionactivitytask.models.response.MissionActivityModel
import com.sarathi.missionactivitytask.models.response.MissionResponseModel
import com.sarathi.missionactivitytask.models.response.MissionTaskModel
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    private val apiInterface: ApiService,
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