package com.nudge.syncmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.getBatchSize
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.SocketTimeoutException

@HiltWorker
class SyncUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    val syncApiRepository: SyncApiRepository
) : CoroutineWorker(appContext, workerParams) {
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

                    val pendingEvents = syncApiRepository.getPendingEventFromDb()
 // Call Before BatchStart
                    DeviceBandwidthSampler.getInstance().startSampling()
                val apiResponse = syncApiRepository.syncEventToServer(pendingEvents)
                Log.d("UploadWorker", apiResponse.data?.first()?.result ?: "")

                    totalPendingEventCount =  syncApiRepository.getPendingEventCount()
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


}

