package com.sarathi.dataloadingmangement.domain

import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.ContentDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.Activity
import com.sarathi.dataloadingmangement.data.entities.Content
import com.sarathi.dataloadingmangement.data.entities.Mission
import com.sarathi.dataloadingmangement.data.entities.Task
import com.sarathi.dataloadingmangement.model.MissionActivityModel
import com.sarathi.dataloadingmangement.model.MissionTaskModel
import com.sarathi.dataloadingmangement.network.DataLoadingApiService
import com.sarathi.dataloadingmangement.network.request.ContentRequest
import com.sarathi.dataloadingmangement.network.request.MissionRequest
import com.sarathi.dataloadingmangement.network.response.ApiResponseModel
import com.sarathi.dataloadingmangement.network.response.ContentResponse
import com.sarathi.dataloadingmangement.network.response.MissionResponseModel
import com.sarathi.dataloadingmangement.repository.IDataLoadingScreenRepository
import javax.inject.Inject

class DataLoadingScreenRepositoryImpl @Inject constructor(
    private val apiInterface: DataLoadingApiService,
    private val missionDao: MissionDao,
    private val activityDao: ActivityDao,
    private val taskDao: TaskDao,
    private val contentDao: ContentDao
) : IDataLoadingScreenRepository {
    override suspend fun fetchMissionDataFromServer(
        languageCode: String,
        missionName: String
    ): ApiResponseModel<List<MissionResponseModel>> {
        val missionRequest = MissionRequest(languageCode, missionName)
        return apiInterface.fetchMissionData(missionRequest)
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
                    Mission.getMissionEntity(
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
            val activityCount = activityDao.getActivityCount(
                userId = "99",
                missionActivityModel.activityId
            )
            if (activityCount == 0) {
                activityDao.insertMissionActivity(
                    Activity.getMissionActivityEntity(
                        "99",
                        missionId,
                        missionActivityModel.tasks.size,
                        missionActivityModel
                    )
                )
            } else {
                activityDao.updateActivityActiveStatus(
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
        taskDao.softDeleteActivityTask("99", activityId, missionId)
        activities.forEach { task ->
            val taskCount =
                taskDao.getTaskByIdCount(
                    userId = "99",
                    taskId = task.id ?: 0
                )
            if (taskCount == 0) {
                taskDao.insertActivityTask(
                    Task.getActivityTaskEntity(
                        userId = "99",
                        missionId = missionId,
                        activityId = activityId,
                        activityName = activityName,
                        task = task,
                    )
                )
            } else {
                taskDao.updateActiveTaskStatus(1, task.id ?: 0, "99")
            }

        }
    }

    override suspend fun fetchContentsFromServer(contentMangerRequest: ContentRequest): ApiResponseModel<List<ContentResponse>> {
        return apiInterface.fetchContentData(contentMangerRequest)
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