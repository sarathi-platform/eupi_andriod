package com.nudge.syncmanager.domain.repository


import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.dao.RequestStatusDao
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.RequestStatusEntity
import com.nudge.core.datamodel.ImageEventDetailsModel
import com.nudge.core.preference.CorePrefRepo
import com.nudge.syncmanager.network.SyncApiService
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    val apiService: SyncApiService,
    val eventDao: EventsDao,
    val eventStatusDao: EventStatusDao,
    val corePrefRepo: CorePrefRepo,
    val imageStatusDao: ImageStatusDao,
    val requestStatusDao: RequestStatusDao
) : SyncRepository {
    val pendingEventStatusList = listOf(
        EventSyncStatus.OPEN.eventSyncStatus,
        EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus,
        EventSyncStatus.PRODUCER_FAILED.eventSyncStatus
    )

    override suspend fun fetchAllImageEventDetails(eventIds: List<String>): List<ImageEventDetailsModel> {
        return eventDao.fetchAllImageEventsWithImageDetails(
            mobileNumber = corePrefRepo.getMobileNo(),
            eventIds = eventIds
        )
    }

    override suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events> {
        return eventDao.getAllPendingEventList(
            pendingEventStatusList,
            batchLimit = batchLimit,
            retryCount = retryCount,
            mobileNumber = corePrefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    override suspend fun getPendingEventCount(syncType: Int): Int {
        return eventDao.getSyncPendingEventCount(
            pendingEventStatusList,
            mobileNumber = corePrefRepo.getMobileNo(),
            syncType = syncType
        )
    }

    override suspend fun fetchAllRequestEventForConsumerStatus(): List<RequestStatusEntity> {
        return requestStatusDao.getAllRequestEventForConsumerStatus(
            corePrefRepo.getMobileNo(),
            listOf(
                EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus,
                EventSyncStatus.CONSUMER_FAILED.eventSyncStatus
            )
        )
    }



    override suspend fun getEventListForConsumer(): List<String> {
        return eventDao.getAllEventsForConsumerStatus(
            corePrefRepo.getMobileNo(),
            EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus
        )
    }


}