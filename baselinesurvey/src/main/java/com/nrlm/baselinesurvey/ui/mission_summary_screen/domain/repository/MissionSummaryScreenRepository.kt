package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.utils.states.SectionStatus

interface MissionSummaryScreenRepository {
    suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>?
    suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>
    )

    suspend fun updateMissionStatus(missionId: Int, status: SectionStatus)
}