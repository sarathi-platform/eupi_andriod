package com.nudge.auditTrail.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.auditTrail.domain.usecase.AuditTrailNetworkUseCase
import com.nudge.core.BATCH_DEFAULT_LIMIT
import com.nudge.core.RETRY_DEFAULT_COUNT
import com.nudge.core.database.entities.Events
import com.nudge.core.getBatchSize
import com.nudge.core.utils.CoreLogger
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AuditUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private  val auditTrailNetworkUseCase:AuditTrailNetworkUseCase
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = AuditUploadWorker::class.java.simpleName
    private var batchLimit = BATCH_DEFAULT_LIMIT
    private var retryCount = RETRY_DEFAULT_COUNT
    private var connectionQuality = ConnectionQuality.UNKNOWN
    override suspend fun doWork(): Result {
        var mPendingEventList = listOf<Events>()
        connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
        CoreLogger.d(
            applicationContext,
            TAG,
            "doWork Started: batchLimit: $batchLimit  retryCount: $retryCount"
        )
        if (runAttemptCount > 0) {
            batchLimit = getBatchSize(connectionQuality).batchSize
        }
        val auditEventList = auditTrailNetworkUseCase.getAuditTrailEventFromDb()
        try {
            auditTrailNetworkUseCase.auditTrailEventToServer(auditEventList)

        } catch (exception: Exception) {
            CoreLogger.e(
                applicationContext,
                TAG,
                "exception", exception
            )
        }

        CoreLogger.d(
            applicationContext,
            TAG,
            "doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount"
        )
        DeviceBandwidthSampler.getInstance().startSampling()
        return Result.success(
            workDataOf(
                WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed and Count 0"
            )
        )
    }

//    private suspend fun auditTrailDataEvent() {
//        val auditTrailEventList =
//            auditTrailNetworkUseCase.getAuditTrailEventFromDb()
//        auditTrailNetworkUseCase.auditTrailEventToServer(
//            auditTrailEventList
//        )
//    }
}

