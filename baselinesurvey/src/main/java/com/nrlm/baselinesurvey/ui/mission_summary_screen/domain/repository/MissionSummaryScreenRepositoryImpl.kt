package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.utils.states.SurveyState
import javax.inject.Inject

class MissionSummaryScreenRepositoryImpl @Inject constructor(
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val missionEntityDao: MissionEntityDao
) : MissionSummaryScreenRepository {
    override suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>? {
        return missionActivityDao.getActivities(missionId)
    }

    override suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>
    ) {
        val missions = missionEntityDao.getMission(missionId)
        var misisonCompleteInc = 0
        var activityPending = missions.pendingActivity
        missionEntityDao.updateMissionStatus(
            missionId,
            SurveyState.INPROGRESS.ordinal,
            activities.size
        )
        activities.forEach { activity ->
            if (activity.activityStatus == SurveyState.COMPLETED.ordinal) {
                ++misisonCompleteInc
                activityPending = --missions.pendingActivity
            }
        }
        val completeInc =
            if (missions.activityTaskSize == misisonCompleteInc) SurveyState.COMPLETED.ordinal else SurveyState.INPROGRESS.ordinal
        missionEntityDao.updateMissionStatus(
            missionId,
            misisonCompleteInc,
            activities.size - misisonCompleteInc
        )
    }
}