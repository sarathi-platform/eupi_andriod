package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity

interface MissionSummaryScreenRepository {
    suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>?
    suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>
    )
}