package com.nudge.syncmanager.domain.repository

import com.nudge.core.EventSyncStatus
import com.nudge.core.SOMETHING_WENT_WRONG
import com.nudge.core.UPCM_USER
import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.EventsDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.database.entities.EventStatusEntity
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.toDate
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.imageupload.BlobImageUploader
import com.nudge.syncmanager.model.BlobUploadConfig
import com.nudge.syncmanager.model.ImageBlobStatusConfig
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
        blobUploadConfig: BlobUploadConfig,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {

        CoreLogger.d(
            CoreAppDetails.getApplicationContext().applicationContext,
            "uploadImageOnBlob",
            "uploadImageOnBlob: FilePath: ${blobUploadConfig.filePath} :: FileName: ${blobUploadConfig.fileName}"
        )
        blobImageUploader.uploadImage(
            filePath = blobUploadConfig.filePath,
            fileName = blobUploadConfig.fileName,
            containerName = if (corePrefRepo.getUserType() == UPCM_USER) blobUploadConfig.postSelectionContainerName
            else blobUploadConfig.selectionContainerName,
            blobConnectionUrl = blobUploadConfig.blobConnectionUrl,
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
        imageBlobStatusConfig: ImageBlobStatusConfig
    ) {
        var retryCount = 0
        if (imageBlobStatusConfig.status == EventSyncStatus.BLOB_UPLOAD_FAILED.eventSyncStatus) {
            retryCount = eventsDao.fetchRetryCountForEvent(imageBlobStatusConfig.eventId) + 1
        }
        imageStatusDao.updateBlobUrl(
            blobUrl = imageBlobStatusConfig.blobUrl,
            imageStatusId = imageBlobStatusConfig.imageStatusId,
            mobileNumber = corePrefRepo.getMobileNo(),
            errorMessage = imageBlobStatusConfig.errorMessage
        )
        if (!imageBlobStatusConfig.isBlobUploaded) {
            eventsDao.updateEventStatus(
                retryCount = retryCount,
                clientId = imageBlobStatusConfig.eventId,
                errorMessage = imageBlobStatusConfig.errorMessage ?: SOMETHING_WENT_WRONG,
                modifiedDate = System.currentTimeMillis().toDate(),
                newStatus = imageBlobStatusConfig.status,
                requestId = imageBlobStatusConfig.requestId
            )
        }


        eventStatusDao.insert(
            EventStatusEntity(
                clientId = imageBlobStatusConfig.eventId,
                errorMessage = imageBlobStatusConfig.errorMessage ?: SOMETHING_WENT_WRONG,
                status = imageBlobStatusConfig.status,
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