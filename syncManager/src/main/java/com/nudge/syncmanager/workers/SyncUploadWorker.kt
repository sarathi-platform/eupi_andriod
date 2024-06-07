package com.nudge.syncmanager.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
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
import com.nudge.syncmanager.utils.WORKER_RESULT
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
        return if (runAttemptCount < 5) {
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
                    mPendingEventList = syncApiRepository.getPendingEventFromDb(
                        batchLimit = batchLimit,
                        retryCount = retryCount
                    )
                    if(mPendingEventList.isNotEmpty()) {
                        CoreLogger.d(applicationContext,TAG,"doWork: pendingEvents List: ${mPendingEventList.json()}")
                        val apiResponse = syncApiRepository.syncProducerEventToServer(mPendingEventList)

                        if (apiResponse.status.equals(SUCCESS)) {
                            apiResponse.data?.let { eventList ->
                                if (eventList.isNotEmpty()) {
                                    val eventSuccessList =
                                        eventList.filter { it.status == EventSyncStatus.PRODUCER_SUCCESS.eventSyncStatus }
                                    val eventFailedList =
                                        eventList.filter { it.status == EventSyncStatus.PRODUCER_FAILED.eventSyncStatus }

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
                                }else {
                                    if (mPendingEventList.isNotEmpty()) syncApiRepository.updateFailedEventStatus(
                                        createEventResponseList(
                                            mPendingEventList,
                                            RESPONSE_DATA_LIST_IS_EMPTY_EXCEPTION
                                        )
                                    )
                                }

                            } ?: syncApiRepository.updateFailedEventStatus(
                                createEventResponseList(
                                    mPendingEventList,
                                    RESPONSE_DATA_IS_NULL_EXCEPTION
                                )
                            )
                        } else {
                            DeviceBandwidthSampler.getInstance().stopSampling()
                            if (mPendingEventList.isNotEmpty()) syncApiRepository.updateFailedEventStatus(
                                createEventResponseList(
                                    mPendingEventList,
                                    RESPONSE_STATUS_FAILED_EXCEPTION
                                )
                            )
                        }
                    }else {
                        val outputData = Data.Builder().putString(WORKER_RESULT,"Success : All Producer Completed").build()
                        Result.success(outputData)
                    }
                }
                fetchConsumerStatus(syncApiRepository = syncApiRepository, mobileNumber =syncApiRepository.getLoggedInMobileNumber() )
                CoreLogger.d(applicationContext,TAG,"doWork: success totalPendingEventCount: $totalPendingEventCount")
                Result.success(Data.Builder().putString(WORKER_RESULT,"Success : All Producer Completed and Count 0").build())
                // do long running work
            } catch (ex: Exception) {
                CoreLogger.e(applicationContext,TAG,"doWork :Exception: ${ex.message} :: ${mPendingEventList.json()}",ex,true)
                DeviceBandwidthSampler.getInstance().stopSampling()
                if(runAttemptCount<3)
                 Result.retry()
                else {
                    if(mPendingEventList.isNotEmpty()){
                        syncApiRepository.updateFailedEventStatus(createEventResponseList(mPendingEventList,ex.message ?: SOMETHING_WENT_WRONG))
                    }
                    Result.failure(Data.Builder().putString(WORKER_RESULT,"Failed : Producer Failed with Exception: ${ex.message}").build())
                }
            }
        } else {
            Result.failure(Data.Builder().putString(WORKER_RESULT,"Failed : Producer Failed with Attempt Count").build())
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
    val eventConsumerRequest= EventConsumerRequest(requestId = BLANK_STRING, mobile = mobileNumber, endDate = date, startDate = date)
    val consumerAPIResponse= syncApiRepository.fetchConsumerEventStatus(eventConsumerRequest)
    if(consumerAPIResponse.status == SUCCESS){
            consumerAPIResponse.data?.let {
                if(it.isNotEmpty()){
                    syncApiRepository.updateEventConsumerStatus(it)
                }
            }
    }
}

