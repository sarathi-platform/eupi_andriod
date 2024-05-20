package com.nudge.syncmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.SocketTimeoutException

@HiltWorker
class FirebaseUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncApiRepository: SyncApiRepository,
    private val firebaseRepository: FirebaseRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return if (runAttemptCount < 5) { // runAttemptCount starts from 0
            try {
                var totalPendingEventCount = syncApiRepository.getPendingEventCount()
                while (totalPendingEventCount > 0) {
                    val pendingEvents = syncApiRepository.getPendingEventFromDb()
                    firebaseRepository.addEventsToFirebase(pendingEvents)
                    totalPendingEventCount = syncApiRepository.getPendingEventCount()
                }
                // do long running work
            } catch (ex: SocketTimeoutException) {
                Result.retry()
            }
            Result.success()
        } else {
            Result.failure()
        }
    }

}