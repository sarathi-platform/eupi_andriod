package com.nudge.syncmanager.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.RESPONSE_DATA_IS_NULL_EXCEPTION
import com.nudge.core.RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION
import com.nudge.core.RESPONSE_STATUS_FAILED_EXCEPTION
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.entities.Events
import com.nudge.core.getBatchSize
import com.nudge.core.json
import com.nudge.core.model.response.EventResult
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.SyncApiRepository
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.syncmanager.utils.SUCCESS
import com.nudge.core.utils.SyncType
import com.nudge.syncmanager.utils.WORKER_ARG_SYNC_TYPE
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date

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
        var mPendingEventList = listOf<Events>()

        if (runAttemptCount >= 5) {
            return Result.failure(workDataOf(
                WorkerKeys.ERROR_MSG to  "Failed: Producer Failed with Attempt Count"
            ))
        }
        val selectedSyncType= inputData.getInt(WORKER_ARG_SYNC_TYPE, SyncType.SYNC_ALL.ordinal)

        return try {
            val connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
            DeviceBandwidthSampler.getInstance().startSampling()

            if (runAttemptCount > 0) {
                batchLimit = getBatchSize(connectionQuality)
            }

            CoreLogger.d(applicationContext, TAG, "doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount")

            var totalPendingEventCount = syncApiRepository.getPendingEventCount(syncType = selectedSyncType)
            CoreLogger.d(applicationContext, TAG, "doWork: totalPendingEventCount: $totalPendingEventCount")

            while (totalPendingEventCount > 0) {
                mPendingEventList = syncApiRepository.getPendingEventFromDb(batchLimit, retryCount)

                if (mPendingEventList.isEmpty()) {
                    return Result.success(
                        workDataOf(
                            WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed"
                        )
                    )
                }

                CoreLogger.d(applicationContext, TAG, "doWork: pendingEvents List: ${mPendingEventList.json()}")
                val apiResponse = syncApiRepository.syncProducerEventToServer(mPendingEventList)

                if (apiResponse.status == SUCCESS) {
                    apiResponse.data?.let { eventList ->
                        if (eventList.isNotEmpty()) {
                            processEventList(eventList)
                            totalPendingEventCount = syncApiRepository.getPendingEventCount(
                                syncType = selectedSyncType
                            )
                            CoreLogger.d(applicationContext, TAG, "doWork: After totalPendingEventCount: $totalPendingEventCount")
                        } else {
                            handleEmptyEventListResponse(mPendingEventList)
                        }
                    } ?: run {
                        handleNullApiResponse(mPendingEventList)
                    }
                } else {
                    handleFailedApiResponse(mPendingEventList)
                }
            }

            fetchConsumerStatus(syncApiRepository, syncApiRepository.getLoggedInMobileNumber())
            CoreLogger.d(applicationContext, TAG, "doWork: success totalPendingEventCount: $totalPendingEventCount")
            Result.success(workDataOf(
                WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed and Count 0"
            ))
        } catch (ex: Exception) {
            handleException(ex, mPendingEventList)
        } finally {
            DeviceBandwidthSampler.getInstance().stopSampling()
        }
    }

    private fun processEventList(eventList: List<SyncEventResponse>) {
        val eventSuccessList = eventList.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }
        val eventFailedList = eventList.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }

        if (eventSuccessList.isNotEmpty()) {
            CoreLogger.d(applicationContext, TAG, "doWork: eventSuccessList List: ${eventSuccessList.json()}")
            syncApiRepository.updateSuccessEventStatus(eventSuccessList)
        }

        if (eventFailedList.isNotEmpty()) {
            CoreLogger.d(applicationContext, TAG, "doWork: eventFailedList List: ${eventFailedList.json()}")
            syncApiRepository.updateFailedEventStatus(eventFailedList)
        }
    }

    private fun handleEmptyEventListResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Producer Response list Empty error")
        syncApiRepository.updateFailedEventStatus(createEventResponseList(mPendingEventList, RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION))
    }

    private fun handleNullApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API response Null")
        syncApiRepository.updateFailedEventStatus(createEventResponseList(mPendingEventList, RESPONSE_DATA_IS_NULL_EXCEPTION))
    }

    private fun handleFailedApiResponse(mPendingEventList: List<Events>) {
        CoreLogger.d(applicationContext, TAG, "doWork: Getting API Failed")
        syncApiRepository.updateFailedEventStatus(createEventResponseList(mPendingEventList, RESPONSE_STATUS_FAILED_EXCEPTION))
    }

    private fun handleException(ex: Exception, mPendingEventList: List<Events>): Result {
        CoreLogger.e(applicationContext, TAG, "doWork: Exception: ${ex.message} :: ${mPendingEventList.json()}", ex, true)

        return if (runAttemptCount < 3) {
             Result.retry()
        } else {
            if (mPendingEventList.isNotEmpty()) {
                syncApiRepository.updateFailedEventStatus(createEventResponseList(mPendingEventList, ex.message ?: SOMETHING_WENT_WRONG))
            }
             Result.failure(workDataOf(
                 WorkerKeys.ERROR_MSG to "Failed: Producer Failed with Exception: ${ex.message}"
             ))
        }
    }


}

fun createEventResponseList(eventList:List<Events>,errorMessage:String): List<SyncEventResponse> {
    val failedEventList= arrayListOf<SyncEventResponse>()
    eventList.forEach {
        failedEventList.add(
            SyncEventResponse(
                clientId =it.id,
                status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                type = it.id,
                mobileNumber = it.mobile_number,
                errorMessage = errorMessage,
                eventName = it.name,
                eventResult = EventResult(
                    eventId = BLANK_STRING,
                    status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                    message = errorMessage
                ),
                requestId = BLANK_STRING
            )
        )
    }
    return failedEventList
}

@SuppressLint("SimpleDateFormat")
suspend fun fetchConsumerStatus(syncApiRepository:SyncApiRepository, mobileNumber:String){
    val date= SimpleDateFormat("yyyy-MM-dd").format(Date())
    val eventConsumerRequest = EventConsumerRequest(
        requestId = BLANK_STRING,
        mobile = mobileNumber,
        endDate = date,
        startDate = date
    )
    val consumerAPIResponse= syncApiRepository.fetchConsumerEventStatus(eventConsumerRequest)
    if(consumerAPIResponse.status == SUCCESS){
            consumerAPIResponse.data?.let {
                if(it.isNotEmpty()){
                    syncApiRepository.updateEventConsumerStatus(it)
                }
            }
    }
}

