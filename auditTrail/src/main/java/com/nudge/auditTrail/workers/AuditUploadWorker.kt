package com.nudge.auditTrail.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.ConnectionQuality
import com.facebook.network.connectionclass.DeviceBandwidthSampler
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
) : CoroutineWorker(appContext, workerParams) {
    private val TAG = AuditUploadWorker::class.java.simpleName
    private var batchLimit = BATCH_DEFAULT_LIMIT
    private var retryCount = RETRY_DEFAULT_COUNT
    private var connectionQuality = ConnectionQuality.UNKNOWN

    override suspend fun doWork(): Result {
        var mPendingEventList = listOf<Events>()
//        val selectedSyncType = inputData.getInt(WORKER_ARG_SYNC_TYPE, SyncType.SYNC_ALL.ordinal)
//        return try {
            connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
//            batchLimit =
//                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.SYNC_BATCH_SIZE.name)
//                    .toIntOrNull().value(BATCH_DEFAULT_LIMIT)
//            retryCount =
//                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.SYNC_RETRY_COUNT.name)
//                    .toIntOrNull().value(RETRY_DEFAULT_COUNT)
//            isBlobImageUploadEnable =
//                syncManagerUseCase.fetchAppConfigFromCacheOrDbUsecase.invoke(AppConfigKeysEnum.BLOB_IMAGE_UPLOAD_ENABLED.name)
//                    .toBoolean()

            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  retryCount: $retryCount"
            )
            if (runAttemptCount > 0) {
                batchLimit = getBatchSize(connectionQuality).batchSize
            }

            CoreLogger.d(
                applicationContext,
                TAG,
                "doWork Started: batchLimit: $batchLimit  runAttemptCount: $runAttemptCount"
            )

//            var totalPendingEventCount =
//                syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(syncType = selectedSyncType)
//            CoreLogger.d(
//                applicationContext,
//                TAG,
//                "doWork: totalPendingEventCount: $totalPendingEventCount"
//            )
//            syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncStartedAnalyticEvent(
//                selectedSyncType,
//                CommonEventParams(batchLimit, retryCount, connectionQuality.name),
//                totalPendingEventCount
//            )
            DeviceBandwidthSampler.getInstance().startSampling()

            /**
             * Reset retry count to 0 if producer failed
             * */
//            syncManagerUseCase.addUpdateEventUseCase.resetFailedEventStatusForProducerFailed()
//
//            if (totalPendingEventCount > 0) {
//
//                if (selectedSyncType == SyncType.SYNC_ALL.ordinal || selectedSyncType == SyncType.SYNC_ONLY_DATA.ordinal)
//                    syncDataEvent(selectedSyncType)
//
////                if (selectedSyncType == SyncType.SYNC_ALL.ordinal || selectedSyncType == SyncType.SYNC_ONLY_IMAGES.ordinal)
////                    syncImageEvents(selectedSyncType)
//
//            }


//            CoreLogger.d(
//                applicationContext,
//                TAG,
//                "doWork: success totalPendingEventCount: $totalPendingEventCount"
//            )
//
//            syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncSuccessEvent(selectedSyncType)

           return Result.success(
                workDataOf(
                    WorkerKeys.SUCCESS_MSG to "Success: All Producer Completed and Count 0"
                )
            )
        }


//        catch (ex: SocketTimeoutException) {
////            handleException(ex, mPendingEventList, selectedSyncType)
//        } catch (ex: UnknownHostException) {
//            handleException(ex, mPendingEventList, selectedSyncType)
//        } catch (ex: ApiException) {
//            handleException(ex, mPendingEventList, selectedSyncType)
//        } catch (ex: Exception) {
//            handleException(ex, mPendingEventList, selectedSyncType)
//        } finally {
//            DeviceBandwidthSampler.getInstance().stopSampling()
//        }
    }


//    private suspend fun syncDataEvent(selectedSyncType: Int) {
//        val syncType = SyncType.SYNC_ONLY_DATA.ordinal
//        var totalPendingDataEventCount =
//            syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(syncType)
//
//        while (totalPendingDataEventCount > 0) {
//            try {
//                val mPendingEventList =
//                    syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventFromDb(
//                        batchLimit = batchLimit,
//                        retryCount = retryCount,
//                        syncType = syncType
//                    )
//
//                if (mPendingEventList.isEmpty()) {
//                    syncManagerUseCase.syncAnalyticsEventUseCase.sendSyncProducerSuccessEvent(
//                        syncType
//                    )
//                    break
//                }
//
//                CoreLogger.d(
//                    tag = TAG,
//                    msg = "syncDataEvent: pendingEvents List: ${mPendingEventList.json()}"
//                )
//
//                val eventListAfterPayloadCheck =
//                    getEventListAccordingToPayloadSize(mPendingEventList, connectionQuality)
//                val apiResponse =
//                    syncManagerUseCase.syncAPIUseCase.syncProducerEventToServer(
//                        eventListAfterPayloadCheck
//                    )
//                totalPendingDataEventCount =
//                    handleAPIResponse(
//                        apiResponse,
//                        totalPendingDataEventCount,
//                        syncType,
//                        eventListAfterPayloadCheck
//                    )
//
//                batchLimit =
//                    getBatchSize(ConnectionClassManager.getInstance().currentBandwidthQuality).batchSize
//
//            } catch (ex: SocketTimeoutException) {
//                val exception = TimeoutException(ex.message.value())
//                CoreLogger.e(
//                    tag = TAG,
//                    msg = "syncDataEvent: TimeoutException -> ${exception.message}",
//                    ex = ex,
//                    stackTrace = true
//                )
//                throw exception
//            } catch (ex: UnknownHostException) {
//                val exception = HostNotFoundException(ex.message.value())
//                CoreLogger.e(
//                    tag = TAG,
//                    msg = "syncDataEvent: HostNotFoundException -> ${exception.message}",
//                    ex = ex,
//                    stackTrace = true
//                )
//                throw exception
//            } catch (ex: ApiException) {
//                CoreLogger.e(
//                    tag = TAG,
//                    msg = "syncDataEvent: ApiException -> ${ex.message}",
//                    ex = ex,
//                    stackTrace = true
//                )
//                throw ex
//            } catch (ex: Exception) {
//                CoreLogger.e(
//                    tag = TAG,
//                    msg = "syncDataEvent: Exception -> ${ex.message}",
//                    ex = ex,
//                    stackTrace = true
//                )
//                throw ex
//            }
//
//        }
//
//        syncManagerUseCase.syncAPIUseCase.fetchConsumerEventStatus { success: Boolean, message: String, requestIds: Int, ex: Throwable? ->
//            syncManagerUseCase.syncAnalyticsEventUseCase.sendConsumerEvents(
//                selectedSyncType,
//                CommonEventParams(batchLimit, retryCount, connectionQuality.name),
//                success,
//                message,
//                requestIds,
//                ex
//            )
//        }
//
//    }


//    private fun getEventListAccordingToPayloadSize(
//        dataEventList: List<Events>,
//        connectionQuality: ConnectionQuality
//    ): List<Events> {
//        var eventPayloadSize = dataEventList.json().getSizeInLong() / 1000
//
//        CoreLogger.d(
//            applicationContext,
//            TAG,
//            "doWork: Event Payload size: ${dataEventList.json().getSizeInLong()}"
//        )
//        var eventListAccordingToPayload: List<Events> = dataEventList
//        while (eventPayloadSize > getBatchSize(connectionQuality).maxPayloadSizeInkb && eventListAccordingToPayload.size > 1) {
//            eventListAccordingToPayload =
//                eventListAccordingToPayload.subList(0, (eventListAccordingToPayload.size / 2))
//            eventPayloadSize = eventListAccordingToPayload.json().getSizeInLong() / 1000
//            CoreLogger.d(
//                applicationContext,
//                TAG,
//                "doWork: Event Payload size in loop: ${
//                    eventListAccordingToPayload.json().getSizeInLong()
//                }"
//            )
//        }
//        return eventListAccordingToPayload
//    }


//    private suspend fun SyncUploadWorker.handleAPIResponse(
//        apiResponse: ApiResponseModel<List<SyncEventResponse>>,
//        totalPendingEventCount: Int,
//        selectedSyncType: Int,
//        mPendingEventList: List<Events>
//    ): Int {
//        var mTotalPendingEventCount = totalPendingEventCount
//        if (apiResponse.status == SUCCESS) {
//            apiResponse.data?.let { eventList ->
//                if (eventList.isNotEmpty()) {
//                    processEventList(eventList)
//                    mTotalPendingEventCount =
//                        syncManagerUseCase.fetchEventsFromDBUseCase.getPendingEventCount(
//                            syncType = selectedSyncType
//                        )
//                    CoreLogger.d(
//                        applicationContext,
//                        TAG,
//                        "doWork: After totalPendingEventCount: $mTotalPendingEventCount"
//                    )
//                } else {
//                    handleAPIResponseFailure(
//                        mPendingEventList,
//                        EMPTY_EVENT_LIST_FAILURE,
//                        selectedSyncType = selectedSyncType
//                    )
//                    throw EmptyResponse()
//                }
//            } ?: withContext(Dispatchers.IO) {
//                handleAPIResponseFailure(
//                    mPendingEventList,
//                    NULL_RESPONSE_FAILURE,
//                    apiResponse.message,
//                    selectedSyncType = selectedSyncType
//                )
//                throw NullResponse()
//            }
//        } else {
//            handleAPIResponseFailure(
//                mPendingEventList,
//                FAILED_RESPONSE_FAILURE,
//                apiResponse.message,
//                selectedSyncType
//            )
//            throw ApiException.HttpError(
//                statusCode = apiResponse.status,
//                message = apiResponse.message
//            )
//        }
//        DeviceBandwidthSampler.getInstance().stopSampling()
//        return mTotalPendingEventCount
//    }

