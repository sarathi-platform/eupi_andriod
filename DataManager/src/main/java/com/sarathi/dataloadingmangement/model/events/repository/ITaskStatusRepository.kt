package com.sarathi.dataloadingmangement.model.events.repository

interface ITaskStatusRepository {

    fun markCompleteTaskStatus(subjectId: Int, taskId: Int)
    fun markInProgressTaskStatus(subjectId: Int, taskId: Int)
}