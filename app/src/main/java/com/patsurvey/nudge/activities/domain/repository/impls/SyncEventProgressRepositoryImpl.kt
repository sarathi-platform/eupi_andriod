package com.patsurvey.nudge.activities.domain.repository.impls

import com.nudge.core.TWO_WEEK_DURATION_RANGE
import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.Events
import com.nudge.core.getDayPriorCurrentTimeMillis
import com.nudge.core.preference.CoreSharedPrefs
import com.patsurvey.nudge.activities.domain.repository.interfaces.SyncEventProgressRepository
import javax.inject.Inject

class SyncEventProgressRepositoryImpl @Inject constructor(
    private val prefRepo: CoreSharedPrefs,
    private val eventsDao: EventsDao,
    private val eventStatusDao: EventStatusDao,
    private val analyticsManager: AnalyticsManager
) : SyncEventProgressRepository {

    override suspend fun getAllEventsForUser(): List<Events> {
        return eventsDao.getAllEventsForUser(prefRepo.getMobileNo())
    }

    override suspend fun sendAnalyticsEventForSyncProgress(
        dataEventParamMap: Map<String, Int>,
        imageEventParamMap: Map<String, Int>
    ) {
        if (dataEventParamMap.isNotEmpty())
            analyticsManager.trackEvent(
                AnalyticsEvents.DATA_SYNC_EVENT_PROGRESS.eventName,
                dataEventParamMap
            )

        if (imageEventParamMap.isNotEmpty())
            analyticsManager.trackEvent(
                AnalyticsEvents.IMAGE_SYNC_EVENT_PROGRESS.eventName,
                imageEventParamMap
            )
    }

    override suspend fun deleteSyncedEventForUser(): Int {
        val deletedEventIds = eventsDao.deleteOlderEventsAndReturnEventId(
            prefRepo.getMobileNo(),
            getDayPriorCurrentTimeMillis(TWO_WEEK_DURATION_RANGE)
        )

        if (deletedEventIds.isNotEmpty()) {
            eventStatusDao.deleteEventStatusEntity(
                mobileNumber = prefRepo.getMobileNo(),
                deletedEventIds
            )
        }

        return deletedEventIds.size
    }

    override suspend fun sendAnalyticsEventForDeletedEventCount(deletedEventCount: Int) {
        analyticsManager.trackEvent(
            AnalyticsEvents.OLD_DELETE_EVENT_COUNT.eventName,
            mapOf(AnalyticsEventsParam.TOTAL_DELETED_EVENT_COUNT.eventParam to deletedEventCount)
        )
    }

}