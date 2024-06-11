package com.sarathi.dataloadingmangement.repository

interface ITaskStatusRepository {

    fun markCompleteTaskStatus(subjectId: Int, taskId: Int)
    fun markInProgressTaskStatus(subjectId: Int, taskId: Int)
    fun getTaskStatus(userId: String, taskId: Int, subjectId: Int): String?
    fun markCompleteActivityStatus(activityId: Int, missionId: Int)
    fun markInProgressActivityStatus(activityId: Int, missionId: Int)
    fun markCompleteMissionStatus(missionId: Int)
    fun markInProgressMissionStatus(missionId: Int)


}