package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.toDate
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
        var activityPending = 0
        missionEntityDao.updateMissionStatus(
            missionId,
            8,
            activities.size
        )
        var taskSize = 0
        activities.forEach { activity ->
            activityPending += activity.pendingDidi
            taskSize += activity.activityTaskSize
        }
        missionEntityDao.updateMissionStatus(
            missionId,
            taskSize - activityPending,
            activityPending
        )
    }

    override suspend fun updateMissionStatus(missionId: Int, status: SectionStatus) {
        if (status == SectionStatus.COMPLETED) {
            missionEntityDao.markMissionCompleted(
                missionId = missionId,
                status = status.name,
                actualCompletedDate = System.currentTimeMillis().toDate().toString()
            )
        } else {
            missionEntityDao.markMissionInProgress(
                missionId = missionId,
                status = status.name,
                actualStartDate = System.currentTimeMillis().toDate().toString()
            )
        }
    }
}