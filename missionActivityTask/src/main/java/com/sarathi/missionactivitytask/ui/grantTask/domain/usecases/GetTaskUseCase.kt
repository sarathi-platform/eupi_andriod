package com.sarathi.missionactivitytask.ui.grantTask.domain.usecases

import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.missionactivitytask.ui.grantTask.domain.repository.GetTaskRepositoryImpl
import javax.inject.Inject


class GetTaskUseCase @Inject constructor(private val taskRepositoryImpl: GetTaskRepositoryImpl) {

    suspend fun getActiveTasks(missionId: Int, activityId: Int): List<TaskUiModel> =
        taskRepositoryImpl.getActiveTask(missionId, activityId)

    suspend fun getSubjectAttributes(taskId: Int) = taskRepositoryImpl.getTaskAttributes(taskId)
}