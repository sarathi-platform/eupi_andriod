package com.sarathi.dataloadingmangement.repository

interface ITaskStatusRepository {

    fun markCompleteTaskStatus(subjectId: Int, taskId: Int)
    fun markInProgressTaskStatus(subjectId: Int, taskId: Int)
    fun getTaskStatus(userId: String, taskId: Int, subjectId: Int): String?
}