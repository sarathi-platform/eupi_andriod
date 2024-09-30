package com.patsurvey.nudge.activities.backup.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.sarathi.dataloadingmangement.data.entities.ActivityEntity

interface ReopenActivityEventHelperRepository {

    suspend fun getActivitiesForMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<ActivityEntity>

    suspend fun getActivitiesForBaselineMission(
        missionId: Int,
        activityIds: List<Int>
    ): List<MissionActivityEntity>

}