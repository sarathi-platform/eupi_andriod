package com.nudge.auditTrail

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import com.nudge.auditTrail.workers.AuditUploadWorker
import java.util.concurrent.TimeUnit

class AuditTrailWorkManagerBuilder private constructor(
    private val context: Context,
    private val workerClass: Class<out ListenableWorker>,
    private val workerTag: String,
    private val batchCount: Int,
    private val repeatInterval: Long, // In minutes
    private val constraints: Constraints
) {

    fun build(): PeriodicWorkRequest {
        val inputData = Data.Builder()
            .putInt("BATCH_COUNT", batchCount)
            .build()

        return PeriodicWorkRequestBuilder<AuditUploadWorker>(repeatInterval, TimeUnit.MINUTES)
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(workerTag)
            .build()
    }

    companion object {
        class Builder(private val context: Context) {
            private lateinit var workerClass: Class<out ListenableWorker>
            private var workerTag: String = "default_worker_tag"
            private var batchCount: Int = 1
            private var repeatInterval: Long = 15L // Default 15 minutes
            private var constraints: Constraints = Constraints.Builder().build()

            fun setWorkerClass(workerClass: Class<out ListenableWorker>) = apply {
                this.workerClass = workerClass
            }

            fun setWorkerTag(workerTag: String) = apply {
                this.workerTag = workerTag
            }

            fun setBatchCount(batchCount: Int) = apply {
                this.batchCount = batchCount
            }

            fun setRepeatInterval(interval: Long) = apply {
                this.repeatInterval = interval
            }

            fun setConstraints(constraints: Constraints) = apply {
                this.constraints = constraints
            }

            fun build(): AuditTrailWorkManagerBuilder {
                if (!::workerClass.isInitialized) {
                    throw IllegalStateException("Worker class must be set")
                }

                return AuditTrailWorkManagerBuilder(
                    context,
                    workerClass,
                    workerTag,
                    batchCount,
                    repeatInterval,
                    constraints
                )
            }
        }
    }
}
