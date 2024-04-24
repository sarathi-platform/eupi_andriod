package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
import javax.inject.Inject

class GetMissionListFromDbUseCase @Inject constructor(private val repository: MissionScreenRepository) {
    suspend operator fun invoke(): List<MissionEntity>? {
        return repository.getMissionsFromDB()
    }

    fun getPendingActivityCountForMissionLive(missionId: Int): LiveData<Int> {
        return repository.getPendingActivityCountForMissionLive(missionId)
    }

    fun getTotalActivityCountForMission(missionId: Int): Int {
        return repository.getTotalActivityCountForMission(missionId)
    }

}