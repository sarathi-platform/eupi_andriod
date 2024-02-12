package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepository
import javax.inject.Inject

class UpdateMisisonState @Inject constructor(private val repository: MissionSummaryScreenRepository) {

    suspend fun updateMissionStatus(missionId: Int, activities: List<MissionActivityEntity>) {
        return repository.getMissionActivitiesStatusFromDB(missionId, activities)
    }

}