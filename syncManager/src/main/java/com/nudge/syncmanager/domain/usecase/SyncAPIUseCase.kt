package com.nudge.syncmanager.domain.usecase

import com.nudge.core.BLANK_STRING
import com.nudge.core.database.entities.Events
import com.nudge.core.json
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.request.toImageEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.domain.repository.SyncApiRepository
import com.nudge.syncmanager.domain.repository.SyncRepository
import com.nudge.syncmanager.utils.SUCCESS
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SyncAPIUseCase(
    private val repository: SyncRepository,
    private val syncAPiRepository: SyncApiRepository
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return syncAPiRepository.syncProducerEventToServer(eventRequest)
    }

    suspend fun syncImageProducerEventToServer(
        events: Events,
        blobUrl: String
    ): ApiResponseModel<List<SyncEventResponse>> {
        val imageEventRequest = events.toImageEventRequest(blobUrl = blobUrl)
        return syncAPiRepository.syncProducerEventToServer(listOf(imageEventRequest))
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

    suspend fun fetchConsumerEventStatus() {
        val requestIdList = repository.fetchAllRequestEventForConsumerStatus().map { it.requestId }
        val eventConsumerRequest = EventConsumerRequest(
            requestId = requestIdList,
            mobile = BLANK_STRING,
            endDate = BLANK_STRING,
            startDate = BLANK_STRING
        )
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "SyncAPIUseCase",
            "fetchConsumerStatus Consumer Request: ${eventConsumerRequest.json()}"
        )

        val consumerAPIResponse = syncAPiRepository.fetchConsumerEventStatus(eventConsumerRequest)
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "SyncAPIUseCase",
            "fetchConsumerStatus Consumer Response: ${consumerAPIResponse.json()}"
        )
        if (consumerAPIResponse.status == SUCCESS) {
            consumerAPIResponse.data?.let {
                if (it.isNotEmpty()) {
                    repository.updateEventConsumerStatus(eventList = it)
                }
            }
        }

    }

    fun getSyncBatchSize() = syncAPiRepository.getSyncBatchSize()
    fun getSyncRetryCount() = syncAPiRepository.getSyncRetryCount()
}