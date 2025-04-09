package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.usecase

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository.MissionSummaryScreenRepository
import javax.inject.Inject

class GetMissionActivitiesFromDBUseCase @Inject constructor(private val repository: MissionSummaryScreenRepository) {
    suspend operator fun invoke(missionId: Int): List<MissionActivityEntity>? {
        return repository.getMissionActivitiesFromDB(missionId)
    }

    suspend fun getMission(missionId: Int): MissionEntity? {
        return repository.getMission(missionId)
    }
}