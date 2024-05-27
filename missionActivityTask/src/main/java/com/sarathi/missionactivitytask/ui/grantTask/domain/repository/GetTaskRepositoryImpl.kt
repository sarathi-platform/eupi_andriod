package com.sarathi.missionactivitytask.ui.grantTask.domain.repository

import com.nudge.core.preference.CoreSharedPrefs
import com.sarathi.dataloadingmangement.data.dao.SubjectAttributeDao
import com.sarathi.dataloadingmangement.data.dao.TaskDao
import com.sarathi.dataloadingmangement.model.uiModel.SubjectAttributes
import com.sarathi.dataloadingmangement.model.uiModel.TaskUiModel
import com.sarathi.missionactivitytask.domain.repository.BaseRepository
import javax.inject.Inject


class GetTaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val subjectAttributeDao: SubjectAttributeDao,
    private val coreSharedPrefs: CoreSharedPrefs
) :
    BaseRepository(),
    ITaskRepository {
    override suspend fun getActiveTask(missionId: Int, activityId: Int): List<TaskUiModel> {
        return taskDao.getActiveTask(
            userId = coreSharedPrefs.getUniqueUserIdentifier(),
            missionId = missionId,
            activityId = activityId
        )
    }

    override suspend fun getTaskAttributes(taskId: Int): List<SubjectAttributes> {
        return subjectAttributeDao.getSubjectAttributes(taskId)
    }


}
