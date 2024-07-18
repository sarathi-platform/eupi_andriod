package com.sarathi.dataloadingmangement.repository

import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import com.sarathi.dataloadingmangement.data.entities.MissionEntity

interface ITaskStatusRepository {

    fun markCompleteTaskStatus(taskId: Int)
    fun markInProgressTaskStatus(taskId: Int)
    fun markNotAvailableTaskStatus(taskId: Int)
    fun getTaskStatus(userId: String, taskId: Int, subjectId: Int): String?
    fun markCompleteActivityStatus(activityId: Int, missionId: Int)
    fun markInProgressActivityStatus(activityId: Int, missionId: Int)
    fun markCompleteMissionStatus(missionId: Int)
    fun markInProgressMissionStatus(missionId: Int)
    suspend fun reCheckActivityStatus() : List<ActivityEntity>
    suspend fun reCheckMissionStatus() : List<MissionEntity>

}