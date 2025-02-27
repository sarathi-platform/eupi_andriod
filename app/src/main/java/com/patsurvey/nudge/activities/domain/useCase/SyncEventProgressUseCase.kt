package com.patsurvey.nudge.activities.domain.useCase

import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.database.entities.Events
import com.nudge.core.getEventCountsByStatus
import com.nudge.core.isDataEvent
import com.nudge.core.isImageEvent
import com.nudge.core.syncStatusParamMap
import com.patsurvey.nudge.activities.domain.repository.interfaces.SyncEventProgressRepository
import javax.inject.Inject

class SyncEventProgressUseCase @Inject constructor(
    private val syncEventProgressRepository: SyncEventProgressRepository
) {

    suspend operator fun invoke() {

        val eventsList = syncEventProgressRepository.getAllEventsForUser()

        if (eventsList.isEmpty()) {
            return
        }

        val dataEvents = eventsList.filter { isDataEvent(it) }
        val imageEvents = eventsList.filter { isImageEvent(it) }

        val dataEventParamMap = getDataEventParamMap(dataEvents)

        val imageEventParamMap = getImageEventParamMap(imageEvents)

        syncEventProgressRepository.sendAnalyticsEventForSyncProgress(
            dataEventParamMap,
            imageEventParamMap
        )

        val deletedEventCount = syncEventProgressRepository.deleteSyncedEventForUser()

        syncEventProgressRepository.sendAnalyticsEventForDeletedEventCount(deletedEventCount)
    }

    private fun getImageEventParamMap(imageEvents: List<Events>): MutableMap<String, Int> {
        val imageEventParamMap = mutableMapOf<String, Int>()

        if (imageEvents.isNotEmpty()) {

            val imageEventStatusCountMap = imageEvents.getEventCountsByStatus()

            imageEventStatusCountMap.forEach { (status, count) ->
                syncStatusParamMap[status]?.let { imageEventParamMap[it] = count }
            }
        }

        imageEventParamMap[AnalyticsEventsParam.TOTAL_EVENT_COUNT.eventParam] = imageEvents.size

        return imageEventParamMap
    }

    private fun getDataEventParamMap(dataEvents: List<Events>): MutableMap<String, Int> {
        val dataEventParamMap = mutableMapOf<String, Int>()

        if (dataEvents.isNotEmpty()) {

            val dataEventStatusCountMap = dataEvents.getEventCountsByStatus()

            dataEventStatusCountMap.forEach { (status, count) ->
                syncStatusParamMap[status]?.let { dataEventParamMap[it] = count }
            }
        }

        dataEventParamMap[AnalyticsEventsParam.TOTAL_EVENT_COUNT.eventParam] = dataEvents.size

        return dataEventParamMap
    }

}