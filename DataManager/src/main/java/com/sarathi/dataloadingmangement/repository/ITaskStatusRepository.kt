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
    suspend fun reCheckActivityStatus(missionId:Int): List<ActivityEntity>
    suspend fun reCheckMissionStatus(): List<MissionEntity>

    suspend fun markInProgressActivitiesStatus(missionId: Int, activityIds: List<Int>)

}