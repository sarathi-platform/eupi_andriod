package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.ActivityTaskEntity
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.dataloadingmangement.repository.GetTaskRepositoryImpl
import javax.inject.Inject

class GetTaskUseCase @Inject constructor(private val taskRepositoryImpl: GetTaskRepositoryImpl) {

    suspend fun getActiveTasks(missionId: Int, activityId: Int): List<TaskUiModel> =
        taskRepositoryImpl.getActiveTask(missionId, activityId)

    suspend fun getSubjectAttributes(taskId: Int) = taskRepositoryImpl.getTaskAttributes(taskId)

    suspend fun getTask(taskId: Int): ActivityTaskEntity {
        return taskRepositoryImpl.getTask(taskId)
    }
}