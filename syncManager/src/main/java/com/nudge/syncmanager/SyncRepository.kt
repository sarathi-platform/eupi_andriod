package com.nudge.syncmanager


import android.content.Context
import com.nudge.core.BLANK_STRING
import com.nudge.core.EventSyncStatus
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.database.entities.Events
import com.nudge.core.database.entities.ImageStatusEntity
import com.nudge.core.model.ApiResponseModel
import com.nudge.core.model.request.EventConsumerRequest
import com.nudge.core.model.request.EventRequest
import com.nudge.core.model.request.toEventRequest
import com.nudge.core.model.response.SyncEventResponse
import com.nudge.core.model.response.SyncImageStatusResponse
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.network.SyncApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SyncApiRepository @Inject constructor(
    val apiService: SyncApiService,
    private val eventDao: EventsDao,
    private val eventStatusDao: EventStatusDao,
    private val prefRepo: CorePrefRepo,
    private val imageStatusDao: ImageStatusDao
) {
    suspend fun syncProducerEventToServer(events: List<Events>): ApiResponseModel<List<SyncEventResponse>> {
        val eventRequest: List<EventRequest> = events.map {
            it.toEventRequest()
        }
        return apiService.syncEvent(eventRequest)
    }

    suspend fun fetchConsumerEventStatus(eventConsumerRequest: EventConsumerRequest)
            : ApiResponseModel<List<SyncEventResponse>> {
        return apiService.syncConsumerStatusApi(eventConsumerRequest)
    }

    suspend fun getPendingEventFromDb(
        batchLimit: Int,
        retryCount: Int,
        syncType: Int
    ): List<Events> {
        return eventDao.getAllPendingEventList(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            batchLimit = batchLimit,
            retryCount = retryCount,
            mobileNumber = prefRepo.getMobileNumber(),
            syncType = syncType
        )
    }

    suspend fun syncImageToServer(
        image: MultipartBody.Part,
        imagePayload: RequestBody
    ): ApiResponseModel<List<SyncImageStatusResponse>> {
        return apiService.syncImage(imageFile = image, imagePayload = imagePayload)
    }

    suspend fun getPendingEventCount(syncType: Int): Int {
        return eventDao.getSyncPendingEventCount(
            listOf(
                EventSyncStatus.OPEN.eventSyncStatus,
                EventSyncStatus.PRODUCER_IN_PROGRESS.eventSyncStatus
            ),
            mobileNumber = prefRepo.getMobileNumber(),
            syncType = syncType
        )
    }

    suspend fun updateSuccessEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateSuccessEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = BLANK_STRING,
                        status = it.status,
                        mobileNumber = it.mobileNumber,
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
                    )
                )
            }
        } catch (ex: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateSuccessEventStatus: Exception: ${ex.message}"
            )
        }

    }

    suspend fun updateFailedEventStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateFailedEventStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage.ifEmpty { SOMETHING_WENT_WRONG },
                        status = EventSyncStatus.PRODUCER_FAILED.eventSyncStatus,
                        mobileNumber = prefRepo.getMobileNumber(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
                    )
                )
            }
        } catch (e: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    suspend fun updateEventConsumerStatus(context: Context, eventList: List<SyncEventResponse>) {
        try {
            eventDao.updateConsumerStatus(eventList)
            eventList.forEach {
                eventStatusDao.insert(
                    EventStatusEntity(
                        clientId = it.clientId,
                        errorMessage = it.errorMessage,
                        status = it.status,
                        mobileNumber = prefRepo.getMobileNumber(),
                        createdBy = prefRepo.getUserId(),
                        eventStatusId = 0
                    )
                )
            }
        } catch (e: Exception) {
            CoreLogger.d(
                context = context,
                "SyncApiRepository",
                "updateFailedEventStatus: Exception: ${e.message}"
            )
        }
    }

    fun getLoggedInMobileNumber():String = prefRepo.getMobileNumber()

    suspend fun fetchImageStatusFromEventId(eventId: String): ImageStatusEntity {
        return imageStatusDao.fetchImageStatusFromEventId(
            mobileNumber = getLoggedInMobileNumber(),
            eventId = eventId
        )
    }

    suspend fun updateImageEventStatus(
        eventId: String,
        status: String,
        errorMessage: String? = BLANK_STRING
    ) {
        imageStatusDao.updateImageEventStatus(
            status = status,
            eventId = eventId,
            errorMessage = errorMessage ?: BLANK_STRING,
            modifiedDate = System.currentTimeMillis().toDate(),
            mobileNumber = getLoggedInMobileNumber()
        )
    }
}




