package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel

interface ITaskRepository {
    suspend fun getActiveTask(missionId: Int, activityId: Int): List<TaskUiModel>
    suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes>
    suspend fun getTask(taskId: Int): ActivityTaskEntity
    suspend fun isAllActivityCompleted(missionId: Int, activityId: Int): Boolean
    suspend fun updateActivityStatus(missionId: Int, activityId: Int, status: String)

    suspend fun updateTaskStatus(
        taskId: Int,
        status: String
    )

}