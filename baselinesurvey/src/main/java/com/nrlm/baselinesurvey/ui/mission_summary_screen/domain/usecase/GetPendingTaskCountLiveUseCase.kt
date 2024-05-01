package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepository
import javax.inject.Inject

class GetPendingTaskCountLiveUseCase @Inject constructor(private val repository: MissionSummaryScreenRepository) {

    operator fun invoke(activityId: Int): LiveData<Int> {
        return repository.getPendingTaskCountLive(activityId)
    }

    fun isActivityComplete(missionId: Int, activityId: Int): Boolean {
        return repository.isActivityCompleted(missionId, activityId)
    }

    fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto? {
        return repository.getActivityFromSubjectId(subjectId)
    }

}
