package com.nudge.syncmanager.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.EventSyncStatus
import com.nudge.core.getBatchSize
import com.nudge.core.json
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.SyncApiRepository
import com.nudge.syncmanager.utils.SUCCESS
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val syncApiRepository: SyncApiRepository
) : CoroutineWorker(appContext, workerParams) {
    private val TAG=SyncUploadWorker::class.java.simpleName
    private var batchLimit = 5
    private val retryCount=3
    override suspend fun doWork(): Result {
        return if (runAttemptCount < 5) { // runAttemptCount starts from 0
            try {

                val connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
                 DeviceBandwidthSampler.getInstance().startSampling()

                if (runAttemptCount > 0) {
                    batchLimit = getBatchSize(connectionQuality)
                }
                CoreLogger.d(applicationContext,TAG,"doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount")
                var totalPendingEventCount = syncApiRepository.getPendingEventCount()
                CoreLogger.d(applicationContext,TAG,"doWork: totalPendingEventCount: $totalPendingEventCount")
                while (totalPendingEventCount > 0){
                    val pendingEvents = syncApiRepository.getPendingEventFromDb(
                        batchLimit = batchLimit,
                        retryCount = retryCount
                    )
                    if(pendingEvents.isNotEmpty()) {
                        CoreLogger.d(applicationContext,TAG,"doWork: pendingEvents List: ${pendingEvents.json()}")
                        val apiResponse = syncApiRepository.syncProducerEventToServer(pendingEvents)
                        if (apiResponse.status.equals(SUCCESS)) {
                            apiResponse.data?.let { eventList ->
                                if (eventList.isNotEmpty()) {
                                    val eventSuccessList =
                                        eventList.filter { it.status.equals(EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus) }
                                    val eventFailedList =
                                        eventList.filter { it.status.equals(EventSyncStatus.PRODUCER_FAILED.eventSyncStatus) }

                                    if (eventSuccessList.isNotEmpty()) {
                                        CoreLogger.d(applicationContext,TAG,"doWork: eventSuccessList List: ${eventSuccessList.json()}")
                                        syncApiRepository.updateSuccessEventStatus(eventSuccessList)
                                    }
                                    if (eventFailedList.isNotEmpty()) {
                                        CoreLogger.d(applicationContext,TAG,"doWork: eventFailedList List: ${eventFailedList.json()}")
                                        syncApiRepository.updateFailedEventStatus(eventFailedList)
                                    }
                                    totalPendingEventCount = syncApiRepository.getPendingEventCount()
                                    CoreLogger.d(applicationContext,TAG,"doWork: After totalPendingEventCount: $totalPendingEventCount")
                                    DeviceBandwidthSampler.getInstance().stopSampling()
                                }else throw NullPointerException("Response Data List Empty")
                            } ?: throw NullPointerException("Response Data Null")
                        } else {
                            DeviceBandwidthSampler.getInstance().stopSampling()
                            throw NullPointerException("Response Status is Failed")
                        }
                    }else Result.success()
                }

                Result.success()
                // do long running work
            } catch (ex: Exception) {
                CoreLogger.e(applicationContext,TAG,"doWork :Exception: ${ex.message}",ex,true)
                DeviceBandwidthSampler.getInstance().stopSampling()
                if(runAttemptCount<3)
                 Result.retry()
                else Result.failure()
            }
        } else {
            Result.failure()
        }
    }


}

