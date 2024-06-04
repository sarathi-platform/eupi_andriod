package com.sarathi.dataloadingmangement.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.nudge.core.toTimeDateString
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.model.SurveyStatusEnum
import javax.inject.Inject

class TaskStatusRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,

    val coreSharedPrefs: CoreSharedPrefs
) :
    ITaskStatusRepository {
    override fun markCompleteTaskStatus(subjectId: Int, taskId: Int) {
        taskDao.markTaskCompleted(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            actualCompletedDate = System.currentTimeMillis().toTimeDateString(),
            status = SurveyStatusEnum.COMPLETED.name,
            subjectId = subjectId
        )
    }


    override fun markInProgressTaskStatus(subjectId: Int, taskId: Int) {
        taskDao.markTaskInProgress(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            taskId = taskId,
            actualStartDate = System.currentTimeMillis().toTimeDateString(),
            status = SurveyStatusEnum.INPROGRESS.name,
            subjectId = subjectId
        )
    }


}