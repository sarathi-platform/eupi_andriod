package com.patsurvey.nudge.activities.backup.domain.use_case

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.patsurvey.nudge.activities.backup.domain.repository.ReopenActivityEventHelperRepository
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity
import javax.inject.Inject

class ReopenActivityEventHelperUseCase @Inject constructor(
    private val reopenActivityEventHelperRepository: ReopenActivityEventHelperRepository
) {

    suspend fun getActivitiesForMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<ActivityEntity> {
        return reopenActivityEventHelperRepository.getActivitiesForMission(
            missionId = missionId,
            activityIds = activityIds
        )
    }

    suspend fun getActivitiesForBaselineMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<MissionActivityEntity> {
        return reopenActivityEventHelperRepository.getActivitiesForBaselineMission(
            missionId = missionId,
            activityIds = activityIds
        )
    }

}