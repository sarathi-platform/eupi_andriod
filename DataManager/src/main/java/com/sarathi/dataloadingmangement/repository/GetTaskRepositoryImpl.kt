package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.util.constants.SurveyStatusEnum
import javax.inject.Inject


class GetTaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val coreSharedPrefs: CoreSharedPrefs,
    private val activityDao: ActivityDao
) : ITaskRepository {
    override suspend fun getActiveTask(missionId: Int, activityId: Int): List<TaskUiModel> {
        return taskDao.getActiveTask(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId
        )
    }

    override suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes> {
        return subjectAttributeDao.getSubjectAttributes(taskId)
    }

    override suspend fun getTask(taskId: Int): ActivityTaskEntity {
        return taskDao.getTaskById(coreSharedPrefs.getUniqueUserIdentifier(), taskId)
    }

    override suspend fun isAllActivityCompleted(missionId: Int, activityId: Int): Boolean {
        return taskDao.countTasksByStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId,
            statuses = listOf(SurveyStatusEnum.NOT_STARTED.name, SurveyStatusEnum.INPROGRESS.name)
        ) == 0
    }

    override suspend fun updateActivityStatus(missionId: Int, activityId: Int, status: String) {
        activityDao.updateActivityStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId,
            status = status
        )
    }

    override suspend fun updateTaskStatus(
        taskId: Int,
        status: String
    ) {
        taskDao.updateTaskStatus(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            status = status
        )
    }
}
