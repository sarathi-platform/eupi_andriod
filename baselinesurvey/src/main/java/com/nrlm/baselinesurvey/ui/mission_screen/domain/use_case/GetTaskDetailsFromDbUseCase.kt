package com.nrlm.baselinesurvey.ui.mission_screen.domain.use_case

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.ui.mission_screen.domain.repository.MissionScreenRepository
import javax.inject.Inject

class GetTaskDetailsFromDbUseCase @Inject constructor(private val repository: MissionScreenRepository) {

    fun getTotalTaskCountForMission(missionId: Int): Int {
        return repository.getTotalTaskCountForMission(missionId)
    }

    fun getPendingTaskCountForMission(missionId: Int): LiveData<Int> {
        return repository.getPendingTaskCountLiveForMission(missionId)
    }

}
