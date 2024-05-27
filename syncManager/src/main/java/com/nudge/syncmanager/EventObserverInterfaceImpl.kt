package com.nudge.syncmanager

import android.content.Context
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
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
import com.nudge.syncmanager.workers.SyncUploadWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventObserverInterfaceImpl @Inject constructor(
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao,
    val eventStatusDao: EventStatusDao
) : EventObserverInterface {

    override fun <T> onEventCallback(event: T) {
        //TODO: Needs to implement after sync integration
    }

    override suspend fun addEvent(event: Events) {
        eventsDao.insert(event)
        eventStatusDao.insert(EventStatusEntity(
            clientId = event.id,
            name = event.name,
            errorMessage = BLANK_STRING,
            type = event.type,
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


    override suspend fun syncPendingEvent(context: Context, networkSpeed: NetworkSpeed) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val data = Data.Builder()
        data.putInt("batchCount", getBatchSize(networkSpeed))
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<SyncUploadWorker>().setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    200,
                    TimeUnit.MILLISECONDS
                ).setInputData(data.build())
                .build()
       val workManager=WorkManager.getInstance(context)

        val workerInfo=  workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id)
        Log.d("TAG", "syncPendingEvent workerInfo: ${workerInfo.value}")
        workManager
            .enqueue(uploadWorkRequest)



    }

    fun getBatchSize(networkSpeed: NetworkSpeed): Int {
        return when (networkSpeed) {
            NetworkSpeed.EXCELLENT -> return 20
            NetworkSpeed.GOOD -> return 15
            NetworkSpeed.MODERATE -> return 10
            NetworkSpeed.POOR -> 5
            NetworkSpeed.UNKNOWN -> 3
        }
    }

}