package com.nudge.syncmanager.domain.usecase

import com.facebook.network.connectionclass.ConnectionClassManager
import com.facebook.network.connectionclass.DeviceBandwidthSampler
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.database.entities.Events
import com.nudge.core.getBatchSize
import com.nudge.core.json
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.utils.CoreLogger
import com.nudge.core.value
import com.nudge.syncmanager.domain.repository.SyncAddUpdateEventRepository
import com.nudge.syncmanager.domain.repository.SyncApiRepository
import com.nudge.syncmanager.domain.repository.SyncRepository
import com.nudge.syncmanager.utils.SUCCESS
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SyncAPIUseCase(
    private val repository: SyncRepository,
    private val syncAPiRepository: SyncApiRepository,
    private val syncAddUpdateEventRepository: SyncAddUpdateEventRepository
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return syncAPiRepository.syncProducerEventToServer(eventRequest)
    }

    suspend fun syncImageWithEventToServer(
        imageList: List<MultipartBody.Part>,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncEventResponse>> {
        return syncAPiRepository.syncImageWithEventToServer(
            imageList = imageList,
            imagePayload = imagePayload
        )
    }

    suspend fun fetchConsumerEventStatus(
        response: (
            success: Boolean, message: String,
            requestIdCount: Int,
            ex: Throwable?
        ) -> Unit
    ) {
        val requestIdList = repository.getEventListForConsumer()
        val connectionQuality = ConnectionClassManager.getInstance().currentBandwidthQuality
        DeviceBandwidthSampler.getInstance().startSampling()
        val chunkedRequestIDs =
            requestIdList.chunked(getBatchSize(connectionQuality).maxClientIdsForStatus)
        chunkedRequestIDs.forEach {
            try {
                val eventConsumerRequest = eventConsumerRequest(it)

                val consumerAPIResponse =
                    syncAPiRepository.fetchConsumerEventStatus(eventConsumerRequest)
                CoreLogger.d(
                    context = CoreAppDetails.getApplicationContext().applicationContext,
                    "SyncAPIUseCase",
                    "fetchConsumerStatus Consumer Response: ${consumerAPIResponse.json()}"
                )
                if (consumerAPIResponse.status == SUCCESS) {
                    consumerAPIResponse.data?.let {
                        if (it.isNotEmpty()) {
                            syncAddUpdateEventRepository.updateEventConsumerStatus(eventList = it)
                            if (it.all { it.status == EventSyncStatus.CONSUMER_SUCCESS.eventSyncStatus }) {
                                response(true, consumerAPIResponse.message, it.size, null)
                            } else if (it.any { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }) {
                                it.filter { it.status == EventSyncStatus.CONSUMER_FAILED.eventSyncStatus }
                                    .forEach { syncEventResponse ->
                                        response(
                                            false,
                                            syncEventResponse.errorMessage,
                                            it.size,
                                            null
                                        )
                                    }
                            }
                        }
                    }
                } else {
                    response(false, consumerAPIResponse.message, it.size, null)
                }
            } catch (exception: Exception) {
                CoreLogger.d(
                    context = CoreAppDetails.getApplicationContext().applicationContext,
                    "SyncAPIUseCase",
                    "fetchConsumerStatus Consumer Exception: ${exception}"
                )
                response(false, exception.message.value(), it.size, exception)
            } finally {
                DeviceBandwidthSampler.getInstance().stopSampling()

            }
        }
    }

    private fun eventConsumerRequest(it: List<String>): EventConsumerRequest {
        val eventConsumerRequest = EventConsumerRequest(
            requestId = listOf(),
            mobile = BLANK_STRING,
            endDate = BLANK_STRING,
            startDate = BLANK_STRING,
            clientIds = it
        )
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "SyncAPIUseCase",
            "fetchConsumerStatus Consumer Request: ${eventConsumerRequest.json()}"
        )
        return eventConsumerRequest
    }
}