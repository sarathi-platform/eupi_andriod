package com.nudge.auditTrail

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.WorkManager
import com.nudge.auditTrail.workers.AuditUploadWorker
import com.nudge.auditTrail.workers.WorkerKeys.AUDIT_TRAIL_WORKER_TAG
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AuditTrailSyncEventUseCase @Inject constructor(@ApplicationContext private val context: Context) {


    fun syncAuditEvents() {
        val workManagerBuilder = AuditTrailWorkManagerBuilder.Companion.Builder(context)
            .setWorkerClass(AuditUploadWorker::class.java)
            .setWorkerTag(AUDIT_TRAIL_WORKER_TAG)
            .setBatchCount(10)
            .setRepeatInterval(30) // Repeat every 30 minutes
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "com.nudge.AuditTrailWorker",
            ExistingPeriodicWorkPolicy.UPDATE, workManagerBuilder.build()
        )


    }
}