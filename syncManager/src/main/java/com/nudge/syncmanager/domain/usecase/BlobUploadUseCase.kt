package com.nudge.syncmanager.domain.usecase

import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.domain.repository.SyncBlobRepository

class BlobUploadUseCase(
    private val syncBlobRepository: SyncBlobRepository
) {
    suspend fun uploadImageOnBlob(
        filePath: String,
        fileName: String,
        postSelectionContainerName: String,
        selectionContainerName: String,
        blobConnectionUrl: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "BlobUploadUseCase",
            "uploadImageOnBlob : FilePath: $filePath :: FileName: $fileName"
        )
        syncBlobRepository.uploadImageOnBlob(
            filePath,
            fileName,
            postSelectionContainerName = postSelectionContainerName,
            selectionContainerName = selectionContainerName,
            blobConnectionUrl = blobConnectionUrl
        ) { message, isExceptionOccur ->
            onUploadImageResponse(message, isExceptionOccur)
        }
    }

    suspend fun updateImageBlobStatus(
        blobUrl: String,
        isBlobUploaded: Boolean,
        imageStatusId: String,
        errorMessage: String,
        status: String,
        eventId: String,
        requestId: String
    ) {
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "BlobUploadUseCase",
            "updateImageBlobStatus : "
        )
        syncBlobRepository.updateBlobStatus(
            isBlobUploaded = isBlobUploaded,
            imageStatusId = imageStatusId,
            blobUrl = blobUrl,
            errorMessage = errorMessage,
            status = status,
            eventId = eventId,
            requestId = requestId
        )
    }
    fun isSyncImageBlobUploadEnable(): Boolean {
        return syncBlobRepository.isSyncImageBlobUploadEnable()
    }
}