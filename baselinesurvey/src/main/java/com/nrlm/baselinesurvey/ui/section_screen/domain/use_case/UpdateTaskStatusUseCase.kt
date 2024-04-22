package com.nrlm.baselinesurvey.ui.section_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.ui.section_screen.domain.repository.SectionListScreenRepository
import com.nrlm.baselinesurvey.utils.states.SectionStatus

class UpdateTaskStatusUseCase(private val repository: SectionListScreenRepository) {

    suspend operator fun invoke(didiId: Int, surveyState: SectionStatus) {
        repository.updateTaskStatus(didiId = didiId, surveyState = surveyState)
    }

    suspend fun getTaskForSubjectId(subjectId: Int): ActivityTaskEntity? {
        return repository.getTaskForSubjectId(subjectId)
    }

    suspend fun updateTaskStatus(
        taskId: Int,
        activityId: Int,
        missionId: Int,
        status: String
    ) {
        repository.updateTaskStatus(taskId, activityId, missionId, status)
    }
}