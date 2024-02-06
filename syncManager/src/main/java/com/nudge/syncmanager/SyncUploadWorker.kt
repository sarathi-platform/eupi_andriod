package com.nudge.syncmanager

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
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
    override suspend fun doWork(): Result {
        Log.d("WorkManager","batchCountL "+workerParams.inputData.getInt("batchCount",0).toString())
        return if (runAttemptCount < 5) { // runAttemptCount starts from 0
            try {

                var totalPendingEventCount = syncApiRepository.getPendingEventCount()
                if (totalPendingEventCount > 0) {
                    val pendingEvents = syncApiRepository.getPendingEventFromDb()
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

                    Result.success()

                } else {

                }
                throw  SocketTimeoutException()

                // do long running work
            } catch (ex: SocketTimeoutException) {
                Result.retry()
            }
        } else {
            Result.failure()
        }
    }


}

