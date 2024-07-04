package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject

class TaskStatusRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val missionDao: MissionDao,
    private val activityDao: ActivityDao,

    val coreSharedPrefs: CoreSharedPrefs
) :
    ITaskStatusRepository {
    override fun markCompleteTaskStatus(taskId: Int) {
        taskDao.markTaskCompleted(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            actualCompletedDate = System.currentTimeMillis().toDate().toString(),
            status = SurveyStatusEnum.COMPLETED.name,
        )
    }


    override fun markInProgressTaskStatus(taskId: Int) {
        taskDao.markTaskInProgress(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            actualStartDate = System.currentTimeMillis().toDate().toString(),
            status = SurveyStatusEnum.INPROGRESS.name,
        )
    }

    override fun markNotAvailableTaskStatus(taskId: Int) {
        taskDao.updateTaskStatus(
            taskId = taskId,
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            status = SurveyStatusEnum.NOT_AVAILABLE.name
        )
    }

    override fun getTaskStatus(userId: String, taskId: Int, subjectId: Int): String? {
        return taskDao.getTaskStatus(userId = userId, taskId = taskId, subjectId = subjectId)
    }

    override fun markCompleteActivityStatus(activityId: Int, missionId: Int) {
        activityDao.markActivityCompleted(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId,
            actualEndDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override fun markInProgressActivityStatus(activityId: Int, missionId: Int) {
        activityDao.markActivityInProgress(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            activityId = activityId,
            missionId = missionId,
            actualStartDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override fun markCompleteMissionStatus(missionId: Int) {
        missionDao.markMissionCompleted(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            actualEndDate = System.currentTimeMillis().toDate().toString()
        )
    }

    override fun markInProgressMissionStatus(missionId: Int) {
        missionDao.markMissionInProgress(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            actualStartDate = System.currentTimeMillis().toDate().toString()
        )
    }


}