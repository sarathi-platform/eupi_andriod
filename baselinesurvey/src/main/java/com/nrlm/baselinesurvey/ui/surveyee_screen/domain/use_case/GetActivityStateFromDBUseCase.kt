package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState
import javax.inject.Inject

class GetActivityStateFromDBUseCase @Inject constructor(private val repository: SurveyeeListScreenRepository) {
    suspend fun getActivitiesStatus(
        activityId: Int,
        surveyeeCardState: List<SurveyeeCardState>
    ) {
        return repository.getMissionActivitiesStatusFromDB(activityId, surveyeeCardState)
    }

    suspend fun getActivitiesAllTaskStatus(
        activityId: Int,
        isAllTask: Boolean
    ) {
        return repository.getMissionActivitiesAllTaskStatusFromDB(activityId, isAllTask)
    }

    suspend fun getActivity(activityId: Int): MissionActivityEntity {
        return repository.getActivitiyStatusFromDB(activityId)
    }


}