package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.use_case

import com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository.SurveyeeListScreenRepository
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import javax.inject.Inject

class UpdateActivityStatusUseCase @Inject constructor(private val repository: SurveyeeListScreenRepository) {

    suspend operator fun invoke(missionId: Int, activityId: Int, status: SectionStatus) {
        repository.updateActivityStatus(
            missionId = missionId,
            activityId = activityId,
            status = status
        )
    }

}