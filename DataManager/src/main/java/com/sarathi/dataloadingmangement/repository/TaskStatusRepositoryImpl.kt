package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toDate
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.MissionDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
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

    override suspend fun reCheckActivityStatus(): List<ActivityEntity> {
        val updatedActivities = mutableListOf<ActivityEntity>()
        missionDao.getActiveMissions(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
        ).forEach { missionEntity ->
            activityDao
                .getActiveActivities(
                    missionId = missionEntity.missionId,
                    userId = coreSharedPrefs.getUniqueUserIdentifier()
                )
                .forEach { activity ->
                    val totalTaskActivityCount = taskDao.getTaskCountForActivity(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        activityId = activity.activityId,
                        missionId = missionEntity.missionId
                    )

                    if (totalTaskActivityCount > 0) {
                        val pendingTaskCount = taskDao.countTasksByStatus(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            activityId = activity.activityId,
                            missionId = missionEntity.missionId,
                            statuses = listOf(
                                SurveyStatusEnum.NOT_STARTED.name,
                                SurveyStatusEnum.INPROGRESS.name
                            )
                        )
                        val pendingActivityCount = activityDao
                            .countActivityByStatus(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                missionId = missionEntity.missionId,
                                statuses = listOf(
                                    SurveyStatusEnum.NOT_STARTED.name,
                                    SurveyStatusEnum.INPROGRESS.name
                                )
                            )
                        if (pendingTaskCount > 0 && pendingActivityCount > 0) {
                            activityDao.updateActivityStatus(
                                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                                missionId = missionEntity.missionId,
                                activityId = activity.activityId,
                                status = SurveyStatusEnum.INPROGRESS.name
                            )
                            if (activity.status != SurveyStatusEnum.INPROGRESS.name) {

                                updatedActivities.add(
                                    activity.copy(status = SurveyStatusEnum.INPROGRESS.name)
                                )
                            }
                        }
                    } else {
                        activityDao.updateActivityStatus(
                            userId = coreSharedPrefs.getUniqueUserIdentifier(),
                            activityId = activity.activityId,
                            missionId = missionEntity.missionId,
                            status = SurveyStatusEnum.COMPLETED.name
                        )
                        if (activity.status != SurveyStatusEnum.COMPLETED.name) {
                            updatedActivities.add(
                                activity.copy(status = SurveyStatusEnum.COMPLETED.name)
                            )
                        }
                    }
                }
        }
        return updatedActivities
    }

    override suspend fun reCheckMissionStatus(): List<MissionEntity> {
        val updatedMission = mutableListOf<MissionEntity>()
        missionDao.getActiveMissions(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
        ).forEach { missionEntity ->
            val totalActivityCount = activityDao
                .getAllActivityCount(
                    coreSharedPrefs.getUniqueUserIdentifier(),
                    missionId = missionEntity.missionId
                )

            if (totalActivityCount > 0) {
                val pendingActivityCount = activityDao
                    .countActivityByStatus(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        missionId = missionEntity.missionId,
                        statuses = listOf(
                            SurveyStatusEnum.NOT_STARTED.name,
                            SurveyStatusEnum.INPROGRESS.name
                        )
                    )
                val pendingMissionCount = missionDao
                    .countMissionByStatus(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        missionId = missionEntity.missionId,
                        statuses = listOf(
                            SurveyStatusEnum.NOT_STARTED.name,
                            SurveyStatusEnum.INPROGRESS.name
                        )
                    )
                if (pendingActivityCount > 0 && pendingMissionCount > 0) {
                    missionDao.updateMissionStatus(
                        userId = coreSharedPrefs.getUniqueUserIdentifier(),
                        missionId = missionEntity.missionId,
                        status = SurveyStatusEnum.INPROGRESS.name
                    )
                    if (missionEntity.status != SurveyStatusEnum.INPROGRESS.name) {
                        updatedMission.add(
                            missionEntity.copy(status = SurveyStatusEnum.INPROGRESS.name)
                        )
                    }
                }
            } else {
                missionDao.updateMissionStatus(
                    userId = coreSharedPrefs.getUniqueUserIdentifier(),
                    missionId = missionEntity.missionId,
                    status = SurveyStatusEnum.COMPLETED.name
                )
                if (missionEntity.status != SurveyStatusEnum.COMPLETED.name) {
                    updatedMission.add(
                        missionEntity.copy(status = SurveyStatusEnum.COMPLETED.name)
                    )
                }
            }
        }
    return updatedMission
    }


}