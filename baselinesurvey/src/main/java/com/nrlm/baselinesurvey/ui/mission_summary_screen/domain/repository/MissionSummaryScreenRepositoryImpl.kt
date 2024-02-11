package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import javax.inject.Inject

class MissionSummaryScreenRepositoryImpl @Inject constructor(
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao,
    private val surveyeeEntityDao: SurveyeeEntityDao
) : MissionSummaryScreenRepository {
    override suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>? {
        return missionActivityDao.getActivities(missionId)
    }

    override suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>?
    ): List<MissionActivityEntity> {
        activities?.forEach { activity ->
            var isStatusUpdate = false
            var pendingInc = 0
            if (taskDao.isTaskExist(missionId, activity.activityName)) {
                taskDao.getActivityTask(missionId, activity.activityName).forEach { task ->
                    if (surveyeeEntityDao.isDidiExist(task.didiId)) {
                        var didi = surveyeeEntityDao.getDidi(didiId = task.didiId)
                        isStatusUpdate = didi.surveyStatus == 2
                        if (!isStatusUpdate) {
                            ++pendingInc
                        }
                    }
                }
            }
            missionActivityDao.updateActivityStatus(
                missionId,
                activity.activityName,
                if (isStatusUpdate) 2 else 0,
                pendingInc
            )
        }
        return activities ?: listOf()
    }
}