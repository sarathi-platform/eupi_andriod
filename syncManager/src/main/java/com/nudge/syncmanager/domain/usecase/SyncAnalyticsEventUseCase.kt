package com.nudge.syncmanager.domain.usecase

import com.nudge.core.analytics.AnalyticsManager
import com.nudge.core.analytics.mixpanel.AnalyticsEvents
import com.nudge.core.analytics.mixpanel.AnalyticsEventsParam
import com.nudge.core.analytics.mixpanel.CommonEventParams
import com.nudge.core.database.entities.Events
import com.nudge.core.utils.CoreLogger
import com.nudge.core.utils.SyncType
import com.nudge.core.value
import javax.inject.Inject

class SyncAnalyticsEventUseCase @Inject constructor(
    private val analyticsManager: AnalyticsManager
) {

    fun sendSyncSuccessEvent(selectedSyncType: Int) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_SUCCESS.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                )
            )
        )
    }

    fun sendSyncProducerSuccessEvent(selectedSyncType: Int) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_PRODUCER_SUCCESS.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                )
            )
        )
    }

    fun sendSyncApiFailureEvent(
        selectedSyncType: Int,
        failureType: String,
        failureMessage: String,
        commonEventParams: CommonEventParams,
        pendingEventList: List<Events>
    ) {
        val syncType = SyncType.getSyncTypeFromInt(selectedSyncType)
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_API_FAILURE.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to syncType,
                AnalyticsEventsParam.SYNC_BATCH_SIZE.eventParam to commonEventParams.batchLimit,
                AnalyticsEventsParam.RETRY_COUNT.eventParam to commonEventParams.retryCount,
                AnalyticsEventsParam.CONNECTION_QUALITY.eventParam to commonEventParams.connectionQuality,
                AnalyticsEventsParam.API_FAILURE_TYPE.eventParam to failureType,
                AnalyticsEventsParam.API_FAILURE_ERROR_MESSAGE.eventParam to failureMessage,
                AnalyticsEventsParam.FAILED_EVENT_ID_LIST.name to pendingEventList.map { it.id }
                    .toString()
            )
        )
    }

    fun sendSyncStartedAnalyticEvent(
        selectedSyncType: Int,
        commonEventParams: CommonEventParams,
        totalPendingEventCount: Int
    ) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_STARTED.eventName, mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                ),
                AnalyticsEventsParam.SYNC_BATCH_SIZE.eventParam to commonEventParams.batchLimit,
                AnalyticsEventsParam.RETRY_COUNT.eventParam to commonEventParams.retryCount,
                AnalyticsEventsParam.CONNECTION_QUALITY.eventParam to commonEventParams.connectionQuality,
                AnalyticsEventsParam.TOTAL_PENDING_EVENT_COUNT.eventParam to totalPendingEventCount
            )
        )
    }

    fun sendSyncFailureDueToExceptionAnalyticsEvent(
        ex: Exception,
        selectedSyncType: Int,
        commonEventParams: CommonEventParams,
        mPendingEventList: List<Events>
    ) {
        val stackTrace = CoreLogger.getStackTraceForLogs(ex = ex)
        val syncType = SyncType.getSyncTypeFromInt(selectedSyncType)

        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_FAILED_DUE_TO_EXCEPTION.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to syncType,
                AnalyticsEventsParam.SYNC_BATCH_SIZE.eventParam to commonEventParams.batchLimit,
                AnalyticsEventsParam.RETRY_COUNT.eventParam to commonEventParams.retryCount,
                AnalyticsEventsParam.CONNECTION_QUALITY.eventParam to commonEventParams.connectionQuality,
                AnalyticsEventsParam.EXCEPTION_MESSAGE.name to ex.message.value(),
                AnalyticsEventsParam.STACK_TRACE.name to stackTrace,
                AnalyticsEventsParam.FAILED_EVENT_ID_LIST.name to mPendingEventList.map { it.id }
                    .toString()
            )
        )
    }

    fun sendConsumerEvents(
        selectedSyncType: Int,
        commonEventParams: CommonEventParams,
        success: Boolean,
        message: String,
        requestIdCount: Int,
        ex: Throwable?
    ) {
        if (success)
            sendSyncConsumerSuccessEvent(
                selectedSyncType,
                requestIdCount
            )
        else if (ex == null) {
            sendSyncConsumerApiFailEvent(
                selectedSyncType,
                message,
                commonEventParams,
                requestIdCount
            )
        } else {
            sendSyncConsumerFailureDueToExceptionAnalyticsEvent(
                ex,
                selectedSyncType,
                commonEventParams,
                requestIdCount
            )
        }
    }

    fun sendSyncConsumerSuccessEvent(selectedSyncType: Int, successRequestIdsCount: Int) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_CONSUMER_SUCCESS.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                ),
                AnalyticsEventsParam.CONSUMER_SUCCESS_REQUEST_IDS_COUNT.eventParam to successRequestIdsCount
            )
        )
    }

    fun sendSyncConsumerApiFailEvent(
        selectedSyncType: Int,
        message: String,
        commonEventParams: CommonEventParams,
        successRequestIdsCount: Int
    ) {
        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_CONSUMER_FAILED.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to SyncType.getSyncTypeFromInt(
                    selectedSyncType
                ),
                AnalyticsEventsParam.CONSUMER_FAIL_MESSAGE.eventParam to message,
                AnalyticsEventsParam.SYNC_BATCH_SIZE.eventParam to commonEventParams.batchLimit,
                AnalyticsEventsParam.RETRY_COUNT.eventParam to commonEventParams.retryCount,
                AnalyticsEventsParam.CONNECTION_QUALITY.eventParam to commonEventParams.connectionQuality,
                AnalyticsEventsParam.CONSUMER_FAIL_REQUEST_IDS_COUNT.eventParam to successRequestIdsCount
            )
        )
    }

    fun sendSyncConsumerFailureDueToExceptionAnalyticsEvent(
        ex: Throwable?,
        selectedSyncType: Int,
        commonEventParams: CommonEventParams,
        failedRequestIdsCount: Int
    ) {
        val stackTrace = CoreLogger.getStackTraceForLogs(ex = ex)
        val syncType = SyncType.getSyncTypeFromInt(selectedSyncType)

        analyticsManager.trackEvent(
            AnalyticsEvents.SYNC_CONSUMER_FAILED_DUE_TO_EXCEPTION.eventName,
            mapOf(
                AnalyticsEventsParam.SYNC_TYPE.eventParam to syncType,
                AnalyticsEventsParam.SYNC_BATCH_SIZE.eventParam to commonEventParams.batchLimit,
                AnalyticsEventsParam.RETRY_COUNT.eventParam to commonEventParams.retryCount,
                AnalyticsEventsParam.CONNECTION_QUALITY.eventParam to commonEventParams.connectionQuality,
                AnalyticsEventsParam.EXCEPTION_MESSAGE.name to ex?.message.value(),
                AnalyticsEventsParam.STACK_TRACE.name to stackTrace,
                AnalyticsEventsParam.CONSUMER_FAIL_REQUEST_IDS_COUNT.name to failedRequestIdsCount
            )
        )
    }

}