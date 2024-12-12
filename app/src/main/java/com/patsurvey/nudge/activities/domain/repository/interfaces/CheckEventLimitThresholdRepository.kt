package com.patsurvey.nudge.activities.domain.repository.interfaces

import com.nudge.core.enums.SyncAlertType

interface CheckEventLimitThresholdRepository {

    fun getEventLimitThreshold(thresholdType: String): Int
    fun setEventLimitThreshold(thresholdType: String, thresholdLimit: Int)

    fun checkEventLimitStatus(eventCount: Int): SyncAlertType
}