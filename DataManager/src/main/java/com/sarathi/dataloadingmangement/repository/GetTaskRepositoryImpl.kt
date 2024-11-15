package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.utils.SubjectStatus
import com.sarathi.dataloadingmangement.data.dao.ActivityDao
import com.sarathi.dataloadingmangement.data.dao.FormDao
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
    private val formDao: FormDao,
    private val activityDao: ActivityDao
) : ITaskRepository {
    override suspend fun getActiveTask(missionId: Int, activityId: Int): List<TaskUiModel> {
        val taskList = taskDao.getActiveTask(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId
        )
        val resultTaskList = ArrayList<TaskUiModel>()
        taskList.forEachIndexed { index, taskUiModelV1 ->
            val formData = formDao.getFormDataForTask(
                userId = coreSharedPrefs.getUniqueUserIdentifier(),
                taskId = taskUiModelV1.taskId
            )
            if (formData.isNullOrEmpty()) {
                resultTaskList.add(
                    TaskUiModel(
                        taskId = taskUiModelV1.taskId,
                        subjectId = taskUiModelV1.subjectId,
                        status = taskUiModelV1.status,
                        isTaskSecondaryStatusEnable = false,
                        isNotAvailableButton = true,
                        isActiveStatus = taskUiModelV1.isActive
                            ?: SubjectStatus.SUBJECT_ACTIVE.ordinal
                    )
                )
            } else {
                val isTaskSecondaryStatusEnable = formData.filter { !it.isFormGenerated }.isEmpty()
                resultTaskList.add(
                    TaskUiModel(
                        taskId = taskUiModelV1.taskId,
                        subjectId = taskUiModelV1.subjectId,
                        status = taskUiModelV1.status,
                        isTaskSecondaryStatusEnable = isTaskSecondaryStatusEnable,
                        isNotAvailableButton = isTaskSecondaryStatusEnable,
                        isActiveStatus = taskUiModelV1.isActive
                            ?: SubjectStatus.SUBJECT_ACTIVE.ordinal
                    )
                )

            }
        }
        return resultTaskList
    }

    override suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes> {
        return subjectAttributeDao.getSubjectAttributes(
            coreSharedPrefs.getUniqueUserIdentifier(),
            taskId
        )
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
