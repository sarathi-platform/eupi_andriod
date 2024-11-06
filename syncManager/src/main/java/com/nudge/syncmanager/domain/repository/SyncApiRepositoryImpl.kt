package com.nudge.syncmanager.domain.repository

import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.preference.CorePrefRepo
import com.nudge.syncmanager.network.SyncApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SyncApiRepositoryImpl(
    val apiService: SyncApiService,
    val eventStatusDao: EventStatusDao,
    val corePrefRepo: CorePrefRepo,
    val imageStatusDao: ImageStatusDao
) : SyncApiRepository {

    override suspend fun syncProducerEventToServer(
        eventRequest: List<EventRequest>
    ): ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncEvent(eventRequest)
    }

    override suspend fun syncImageWithEventToServer(
        imageList: List<MultipartBody.Part>,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncImageWithEvent(imageFileList = imageList, imagePayload = imagePayload)
    }

    override suspend fun fetchConsumerEventStatus(
        eventConsumerRequest: EventConsumerRequest
    ): ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncConsumerStatusApi(eventConsumerRequest)
    }

    override fun getSyncBatchSize(): Int {
        return corePrefRepo.getSyncBatchSize()
    }

    override fun getSyncRetryCount(): Int {
        return corePrefRepo.getSyncRetryCount()
    }
}