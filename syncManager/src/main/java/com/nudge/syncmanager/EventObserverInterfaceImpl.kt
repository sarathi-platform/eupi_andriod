package com.nudge.syncmanager

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.nudge.communicationModule.EventObserverInterface
import com.nudge.core.database.dao.EventDependencyDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.entities.EventDependencyEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.enums.NetworkSpeed
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class EventObserverInterfaceImpl @Inject constructor(
    val eventsDao: EventsDao,
    val eventDependencyDao: EventDependencyDao
) : EventObserverInterface {

    override fun <T> onEventCallback(event: T) {

    }

    override suspend fun addEvent(event: Events) {
        eventsDao.insert(event)
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

    override suspend fun syncPendingEvent(context: Context, networkSpeed: NetworkSpeed) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val data = Data.Builder()
        data.putInt("batchCount",getBatchSize(networkSpeed) )
        val uploadWorkRequest: WorkRequest =
            OneTimeWorkRequestBuilder<SyncUploadWorker>().setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10000,
                    TimeUnit.MILLISECONDS
                ).setInputData(data.build())
                .build()

        WorkManager
            .getInstance(context)
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