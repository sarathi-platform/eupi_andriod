package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState

interface SurveyeeListScreenRepository {

    suspend fun getSurveyeeList(missionId: Int, activityId: Int): List<SurveyeeEntity>

    suspend fun getSurveyeeListFromNetwork(): Boolean

    suspend fun moveSurveyeesToThisWeek(didiIdList: Set<Int>, moveDidisToNextWeek: Boolean)

    suspend fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean)
    suspend fun getActivityTasks(missionId: Int, activityId: Int): List<ActivityTaskEntity>
    suspend fun getMissionActivitiesStatusFromDB(
        activityId: Int,
        surveyeeCardState: List<SurveyeeCardState>
    )

    suspend fun updateActivityAllTaskStatus(
        activityId: Int,
        isAllTask: Boolean
    )

    suspend fun updateActivityStatus(
        missionId: Int,
        activityId: Int,
        status: SectionStatus
    )

    suspend fun getActivitiyStatusFromDB(
        activityId: Int,
    ): MissionActivityEntity

    fun getBaseLineUserId(): String

}