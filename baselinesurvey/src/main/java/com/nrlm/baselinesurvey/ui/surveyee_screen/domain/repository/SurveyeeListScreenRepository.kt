package com.nrlm.baselinesurvey.ui.surveyee_screen.domain.repository

import com.nrlm.baselinesurvey.database.entity.ActivityTaskEntity
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.SurveyeeEntity
import com.nrlm.baselinesurvey.utils.states.SurveyeeCardState

interface SurveyeeListScreenRepository {

    suspend fun getSurveyeeList(missionId: Int, activityName: String): List<SurveyeeEntity>

    suspend fun getSurveyeeListFromNetwork(): Boolean

    suspend fun moveSurveyeesToThisWeek(didiIdList: Set<Int>, moveDidisToNextWeek: Boolean)

    suspend fun moveSurveyeeToThisWeek(didiId: Int, moveDidisToNextWeek: Boolean)
    suspend fun getActivityTasks(missionId: Int, activityName: String): List<ActivityTaskEntity>
    suspend fun getMissionActivitiesStatusFromDB(
        activityId: Int,
        surveyeeCardState: List<SurveyeeCardState>
    )

    suspend fun getMissionActivitiesAllTaskStatusFromDB(
        activityId: Int,
        isAllTask: Boolean
    )

    suspend fun getActivitiyStatusFromDB(
        activityId: Int,
    ): MissionActivityEntity

}