package com.patsurvey.nudge.activities.domain.repository.impls

import com.nudge.core.THRESHOLD_TYPE_HARD
import com.nudge.core.THRESHOLD_TYPE_SOFT
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.enums.SyncAlertType
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.domain.repository.interfaces.CheckEventLimitThresholdRepository
import javax.inject.Inject

class CheckEventLimitThresholdRepositoryImpl @Inject constructor(
    private val coreSharedPrefs: CoreSharedPrefs,
    private val eventsDao: EventsDao,
    private val eventStatusDao: EventStatusDao
) : CheckEventLimitThresholdRepository {
    override fun getEventLimitThreshold(thresholdType: String): Int {
        return if (thresholdType == THRESHOLD_TYPE_SOFT) coreSharedPrefs.getSoftEventLimitThreshold() else coreSharedPrefs.getHardEventLimitThreshold()
    }

    override fun setEventLimitThreshold(thresholdType: String, thresholdLimit: Int) {
        if (thresholdType == THRESHOLD_TYPE_SOFT)
            coreSharedPrefs.setSoftEventLimitThreshold(thresholdLimit)
        else
            coreSharedPrefs.setHardEventLimitThreshold(thresholdLimit)
    }

    override fun checkEventLimitStatus(eventCount: Int): SyncAlertType {
        if (eventCount == 0)
            return SyncAlertType.NO_ALERT

        val softThresholdLimit = getEventLimitThreshold(THRESHOLD_TYPE_SOFT)
        val hardThresholdLimit = getEventLimitThreshold(THRESHOLD_TYPE_HARD)

        if (eventCount in softThresholdLimit until hardThresholdLimit)
            return SyncAlertType.SOFT_ALERT

        if (eventCount >= hardThresholdLimit)
            return SyncAlertType.HARD_ALERT

        return SyncAlertType.NO_ALERT
    }


}