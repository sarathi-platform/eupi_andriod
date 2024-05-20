package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus

interface MissionSummaryScreenRepository {
    suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>?
    suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>
    )

    suspend fun updateMissionStatus(missionId: Int, status: SectionStatus)
    fun getPendingTaskCountLive(activityId: Int): LiveData<Int>

    fun isActivityCompleted(missionId: Int, activityId: Int): Boolean

    fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto?

    suspend fun getMission(missionId: Int): MissionEntity
    fun getBaseLineUserId(): String
}