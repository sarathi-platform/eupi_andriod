package com.nudge.syncmanager

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import com.nudge.syncmanager.utils.SYNC_UNIQUE_NAME
import com.nudge.syncmanager.utils.WORKER_ARG_BATCH_COUNT
import com.nudge.syncmanager.utils.WORKER_ARG_SYNC_TYPE
import com.nudge.syncmanager.workers.SyncUploadWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventObserverInterfaceImpl @Inject constructor(
    val eventsDao: EventsDao,
    private val eventDependencyDao: EventDependencyDao,
    val eventStatusDao: EventStatusDao,
    private val workManager: WorkManager
) : EventObserverInterface {

    override fun <T> onEventCallback(event: T) {
        //Needs to implement after sync integration
    }

    override suspend fun addEvent(event: Events) {
        eventsDao.insert(event)
        eventStatusDao.insert(EventStatusEntity(
            clientId = event.id,
            errorMessage = BLANK_STRING,
            status = EventSyncStatus.OPEN.eventSyncStatus,
            mobileNumber = event.mobile_number,
            createdBy = event.createdBy,
            eventStatusId = 0
         )
        )
    }

    override suspend fun addEvents(events: List<Events>) {
        eventsDao.insertAll(events)
    }

    override suspend fun addEventDependency(eventDependency: EventDependencyEntity) {
        eventDependencyDao.insert(eventDependency)
    }

    override suspend fun addEventDependencies(eventDependencies: List<EventDependencyEntity>) {
        eventDependencyDao.insertAll(eventDependencies)
    }

    override suspend fun getEvent(): List<Events> {
        return eventsDao.getAllEvent()
    }


    override suspend fun syncPendingEvent(
        context: Context,
        networkSpeed: NetworkSpeed,
        syncType: Int
    ) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val data = Data.Builder()
        data.putInt(WORKER_ARG_BATCH_COUNT, getBatchSize(networkSpeed))
        data.putInt(WORKER_ARG_SYNC_TYPE, syncType)
        val uploadWorkRequest: PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncUploadWorker>(
                repeatInterval = 15,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
                .setConstraints(
                    constraints
                )
                .setBackoffCriteria(
                    backoffPolicy = BackoffPolicy.LINEAR,
                    backoffDelay = 90000,
                    TimeUnit.MILLISECONDS
                ).setInputData(data.build())
                .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_UNIQUE_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, uploadWorkRequest
        )
    }

    private fun getBatchSize(networkSpeed: NetworkSpeed): Int {
        return when (networkSpeed) {
            NetworkSpeed.EXCELLENT -> 20
            NetworkSpeed.GOOD -> 15
            NetworkSpeed.MODERATE -> 10
            NetworkSpeed.POOR -> 5
            NetworkSpeed.UNKNOWN -> 3
        }
    }

}