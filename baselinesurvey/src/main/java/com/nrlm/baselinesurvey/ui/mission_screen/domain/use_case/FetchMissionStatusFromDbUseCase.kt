package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
import javax.inject.Inject

class FetchMissionStatusFromDbUseCase @Inject constructor(private val repository: MissionScreenRepository) {
    suspend fun getMissionsStatusFromDB(missions: List<MissionEntity>): List<MissionEntity>? {
        return repository.getMissionsStatusFromDB(missions)
    }

}