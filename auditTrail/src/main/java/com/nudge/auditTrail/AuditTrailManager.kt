//package com.nudge.auditTrail
//
//import android.content.Context
//import androidx.work.BackoffPolicy
//import androidx.work.Constraints
//import androidx.work.Data
//import androidx.work.ExistingPeriodicWorkPolicy
//import androidx.work.NetworkType
//import androidx.work.PeriodicWorkRequest
//import androidx.work.PeriodicWorkRequestBuilder
//import com.nudge.auditTrail.workers.AuditUploadWorker
//import com.nudge.core.enums.NetworkSpeed
//import java.util.concurrent.TimeUnit
//
//class AuditTrailManager (
//    val periodicTime: Int?,
//    val batchCount: Int?,
//
//)
//class Builder {
//    private var periodicTime: Int? = null
//    private var batchCount: Int? = null
//
//
//    fun setMake(make: String) = apply { this.periodicTime = periodicTime }
//    fun setModel(model: String) = apply { this.batchCount = model }
//
//    fun build(): AuditTrailManager {
//        return AuditTrailManager(periodicTime,batchCount)
//    }
//     suspend fun auditEventEvent(
//        context: Context,
//        networkSpeed: NetworkSpeed,
//        auditTrailType: Int
//    ) {
//
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .setRequiresBatteryNotLow(true)
//            .build()
//        val data = Data.Builder()
//        data.putInt(WORKER_ARG_BATCH_COUNT, getBatchSize(networkSpeed))
//        data.putInt(WORKER_ARG_SYNC_TYPE, auditTrailType)
//        val uploadWorkRequest: PeriodicWorkRequest =
//            PeriodicWorkRequestBuilder<AuditUploadWorker>(
//                repeatInterval = periodicTime,
//                repeatIntervalTimeUnit = TimeUnit.MINUTES
//            )
//                .setConstraints(
//                    constraints
//                )
//                .setBackoffCriteria(
//                    backoffPolicy = BackoffPolicy.LINEAR,
//                    backoffDelay = 90000,
//                    TimeUnit.MILLISECONDS
//                ).setInputData(data.build())
//                .build()
//
//        workManager.enqueueUniquePeriodicWork(
//            SYNC_UNIQUE_NAME,
//            ExistingPeriodicWorkPolicy.UPDATE, uploadWorkRequest
//        )
//    }
//
//
//}
//
//
//// Usage:
//fun main() {
//    val car = AuditTrailManager.Builder()
//        .setMake("Toyota")
//        .setModel("Corolla")
//        .setYear(2023)
//        .setColor("Red")
//        .setEngineCapacity(1.8)
//        .build()
//
//}