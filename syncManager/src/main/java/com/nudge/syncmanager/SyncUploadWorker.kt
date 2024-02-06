package com.nudge.syncmanager

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.enums.NetworkSpeed
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.IOException
import java.net.SocketTimeoutException

@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val syncApiRepository: SyncApiRepository
) :

    CoroutineWorker(appContext, workerParams) {
    var batchLimit = 10;
    override suspend fun doWork(): Result {
        Log.d(
            "WorkManager",
            "batchCountL " + workerParams.inputData.getInt("batchCount", 0).toString()
        )
        return if (runAttemptCount < 5) { // runAttemptCount starts from 0
            try {

                var connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality

                if(runAttemptCount>0) {
                    batchLimit = getBatchSize(connectionQuality)
                }
                var totalPendingEventCount = syncApiRepository.getPendingEventCount()
                if (totalPendingEventCount > 0) {
                    val pendingEvents = syncApiRepository.getPendingEventFromDb()
 // Call Before BatchStart
                    DeviceBandwidthSampler.getInstance().startSampling()
                    syncApiRepository.syncEventInNetwork(pendingEvents)
//                    while (totalPendingEventCount>0) {
//                        val pendingEvent = syncApiRepository.getPendingEventFromDb()
//                        totalPendingEventCount =  syncApiRepository.getPendingEventCount()
//
//                        pendingEvent.forEach {
//                            Log.d("WorkManager", it.toString())
//                        }
//
//                    totalPendingEventCount--;
//                    }
                    DeviceBandwidthSampler.getInstance().stopSampling()
                    Result.success()

                } else {

                }
                throw SocketTimeoutException()

                // do long running work
            } catch (ex: SocketTimeoutException) {
                DeviceBandwidthSampler.getInstance().stopSampling()

                Result.retry()
            }
        } else {
            Result.failure()
        }
    }

    fun getBatchSize(connectionQuality: ConnectionQuality): Int {
        return when (connectionQuality) {
            ConnectionQuality.EXCELLENT -> return 20
            ConnectionQuality.GOOD -> return 15
            ConnectionQuality.MODERATE -> return 10
            ConnectionQuality.POOR -> 5
            ConnectionQuality.UNKNOWN -> -1
        }
    }

}

