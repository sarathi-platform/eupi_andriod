package com.sarathi.dataloadingmangement.domain.use_case

import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity
import com.sarathi.dataloadingmangement.repository.IMATStatusEventRepository
import com.sarathi.dataloadingmangement.repository.ITaskStatusRepository
import javax.inject.Inject

class UpdateMissionActivityTaskStatusUseCase @Inject constructor(private val repository: ITaskStatusRepository, private val iMATrepository: IMATStatusEventRepository,) {
    suspend fun markTaskCompleted(taskId: Int) {
        return repository.markCompleteTaskStatus(taskId = taskId)
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

    suspend fun markActivitiesInProgress(missionId: Int, activityIds: List<Int>) {
        repository.markInProgressActivitiesStatus(missionId = missionId, activityIds = activityIds)

    }

    suspend fun markMissionInProgress(missionId: Int) {
        repository.markInProgressMissionStatus(missionId)
    }

    suspend fun markMissionCompleted(missionId: Int) {
        repository.markCompleteMissionStatus(missionId)
    }

    suspend fun reCheckActivityStatus(missionId: Int,programId:Int): List<ActivityEntity> {
        return repository.reCheckActivityStatus(missionId =missionId,programId=programId )
    }

    suspend fun reCheckMissionStatus(): List<MissionEntity> {
        return repository.reCheckMissionStatus()
    }



}