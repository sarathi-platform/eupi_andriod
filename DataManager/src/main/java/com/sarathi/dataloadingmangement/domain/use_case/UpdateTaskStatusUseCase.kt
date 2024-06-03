package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.repository.ITaskStatusRepository
import javax.inject.Inject

class UpdateTaskStatusUseCase @Inject constructor(private val repository: ITaskStatusRepository) {
    suspend fun markTaskCompleted(subjectId: Int, taskId: Int) {
        return repository.markCompleteTaskStatus(subjectId = subjectId, taskId = taskId)
    }

    suspend fun markTaskInProgress(subjectId: Int, taskId: Int) {
        return repository.markInProgressTaskStatus(subjectId = subjectId, taskId = taskId)
    }
}