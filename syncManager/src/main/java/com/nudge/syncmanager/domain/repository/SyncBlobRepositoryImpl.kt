package com.nudge.syncmanager.domain.repository

import com.nudge.core.EventSyncStatus
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.imageupload.BlobImageUploader
import com.nudge.syncmanager.network.SyncApiService

class SyncBlobRepositoryImpl(
    val apiService: SyncApiService,
    val eventStatusDao: EventStatusDao,
    val corePrefRepo: CorePrefRepo,
    val imageStatusDao: ImageStatusDao,
    val eventsDao: EventsDao,
    val blobImageUploader: BlobImageUploader
) : SyncBlobRepository {
    override suspend fun uploadImageOnBlob(
        filePath: String,
        fileName: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {

        CoreLogger.d(
            CoreAppDetails.getApplicationContext().applicationContext,
            "uploadImageOnBlob",
            "uploadImageOnBlob: FilePath: $filePath :: FileName: $fileName"
        )
        blobImageUploader.uploadImage(
            filePath = filePath,
            fileName = fileName,
            onUploadImageResponse = { message, isExceptionOccur ->
                CoreLogger.d(
                    CoreAppDetails.getApplicationContext().applicationContext,
                    "uploadImageOnBlob",
                    "uploadImageOnBlob: $message :: $isExceptionOccur"
                )
                onUploadImageResponse(message, isExceptionOccur)
            })
    }

    override suspend fun updateBlobStatus(
        blobUrl: String,
        isBlobUploaded: Boolean,
        imageStatusId: String,
        errorMessage: String,
        status: String,
        eventId: String,
        requestId: String
    ) {
        var retryCount = 0
        if (status == EventSyncStatus.BLOB_UPLOAD_FAILED.eventSyncStatus) {
            retryCount = eventsDao.fetchRetryCountForEvent(eventId) + 1
        }
        imageStatusDao.updateBlobUrl(
            blobUrl = blobUrl,
            imageStatusId = imageStatusId,
            mobileNumber = corePrefRepo.getMobileNo(),
            errorMessage = errorMessage
        )
        if (!isBlobUploaded) {
            eventsDao.updateEventStatus(
                retryCount = retryCount,
                clientId = eventId,
                errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
                modifiedDate = System.currentTimeMillis().toDate(),
                newStatus = status,
                requestId = requestId
            )
        }


        eventStatusDao.insert(
            EventStatusEntity(
                clientId = eventId,
                errorMessage = errorMessage ?: SOMETHING_WENT_WRONG,
                status = status,
                mobileNumber = corePrefRepo.getMobileNo(),
                createdBy = corePrefRepo.getUserId(),
                eventStatusId = 0
            )
        )
    }
    override fun isSyncImageBlobUploadEnable(): Boolean {
        return corePrefRepo.isSyncImageBlobUploadEnable()
    }
}