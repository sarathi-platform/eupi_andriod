package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.repository.ITaskStatusRepository
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(private val repository: ITaskStatusRepository) {
    suspend fun markTaskCompleted(subjectId: Int, taskId: Int) {
        return repository.markCompleteTaskStatus(subjectId = subjectId, taskId = taskId)
    }

    suspend fun markTaskInProgress(taskId: Int) {
        return repository.markInProgressTaskStatus(taskId = taskId)
    }

    suspend fun markTaskNotAvailable(taskId: Int) {
        return repository.markNotAvailableTaskStatus(taskId = taskId)
    }


    suspend fun getTaskStatus(userId: String, taskId: Int, subjectId: Int): String? {
        return repository.getTaskStatus(userId = userId, taskId = taskId, subjectId = subjectId)
    }


    suspend fun markActivityCompleted(missionId: Int, activityId: Int) {
        repository.markCompleteActivityStatus(activityId = activityId, missionId = missionId)

    }

    suspend fun markActivityInProgress(missionId: Int, activityId: Int) {
        repository.markInProgressActivityStatus(activityId = activityId, missionId = missionId)

    }

    suspend fun markMissionInProgress(missionId: Int) {
        repository.markInProgressMissionStatus(missionId)
    }

    suspend fun markMissionCompleted(missionId: Int) {
        repository.markCompleteMissionStatus(missionId)
    }


}