package com.nrlm.baselinesurvey.ui.mission_summary_screen.domain.repository

import androidx.lifecycle.LiveData
import com.nrlm.baselinesurvey.data.prefs.PrefRepo
import com.nrlm.baselinesurvey.database.dao.ActivityTaskDao
import com.nrlm.baselinesurvey.database.dao.MissionActivityDao
import com.nrlm.baselinesurvey.database.dao.MissionEntityDao
import com.nrlm.baselinesurvey.database.dao.SurveyeeEntityDao
import com.nrlm.baselinesurvey.database.entity.MissionActivityEntity
import com.nrlm.baselinesurvey.database.entity.MissionEntity
import com.nrlm.baselinesurvey.model.datamodel.ActivityForSubjectDto
import com.nrlm.baselinesurvey.utils.states.SectionStatus
import com.nudge.core.toDate
import javax.inject.Inject

class MissionSummaryScreenRepositoryImpl @Inject constructor(
    private val missionActivityDao: MissionActivityDao,
    private val taskDao: ActivityTaskDao,
    private val surveyeeEntityDao: SurveyeeEntityDao,
    private val missionEntityDao: MissionEntityDao,
    private val prefRepo: PrefRepo
) : MissionSummaryScreenRepository {
    override suspend fun getMissionActivitiesFromDB(missionId: Int): List<MissionActivityEntity>? {
        return missionActivityDao.getActivities(userId = getBaseLineUserId(), missionId)
    }

    override suspend fun getMissionActivitiesStatusFromDB(
        missionId: Int,
        activities: List<MissionActivityEntity>
    ) {
        var activityPending = 0
        missionEntityDao.updateMissionStatus(
            userId = getBaseLineUserId(),
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
            userId = getBaseLineUserId(),
            missionId,
            taskSize - activityPending,
            activityPending
        )
    }

    override suspend fun updateMissionStatus(missionId: Int, status: SectionStatus) {
        if (status == SectionStatus.COMPLETED) {
            missionEntityDao.markMissionCompleted(
                userId = getBaseLineUserId(),
                missionId = missionId,
                status = status.name,
                actualCompletedDate = System.currentTimeMillis().toDate().toString()
            )
        } else {
            missionEntityDao.markMissionInProgress(
                userId = getBaseLineUserId(),
                missionId = missionId,
                status = status.name,
                actualStartDate = System.currentTimeMillis().toDate().toString()
            )
        }
    }

    override fun getPendingTaskCountLive(activityId: Int): LiveData<Int> {
        return taskDao.getPendingTaskCountLive(userId = getBaseLineUserId(), activityId)
    }

    override fun isActivityCompleted(missionId: Int, activityId: Int): Boolean {
        return missionActivityDao.isActivityCompleted(
            userId = getBaseLineUserId(),
            missionId,
            activityId
        ).status != SectionStatus.COMPLETED.name
    }

    override fun getActivityFromSubjectId(subjectId: Int): ActivityForSubjectDto {
        return missionActivityDao.getActivityFromSubjectId(userId = getBaseLineUserId(), subjectId)
    }

    override fun getBaseLineUserId(): String {
        return prefRepo.getBaseLineUserId()
    }

    override suspend fun getMission(missionId: Int): MissionEntity {
        return missionEntityDao.getMission(missionId)
    }
}