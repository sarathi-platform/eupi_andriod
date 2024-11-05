package com.nudge.syncmanager.domain.usecase

import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.domain.repository.SyncBlobRepository
import com.nudge.syncmanager.model.BlobUploadConfig
import com.nudge.syncmanager.model.ImageBlobStatusConfig

class BlobUploadUseCase(
    private val syncBlobRepository: SyncBlobRepository
) {
    suspend fun uploadImageOnBlob(
        blobUploadConfig: BlobUploadConfig,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "BlobUploadUseCase",
            "uploadImageOnBlob : FilePath: ${blobUploadConfig.filePath} :: FileName: ${blobUploadConfig.fileName}"
        )
        syncBlobRepository.uploadImageOnBlob(
            blobUploadConfig = blobUploadConfig
        ) { message, isExceptionOccur ->
            onUploadImageResponse(message, isExceptionOccur)
        }
    }

    suspend fun updateImageBlobStatus(
        imageBlobStatusConfig: ImageBlobStatusConfig
    ) {
        CoreLogger.d(
            context = CoreAppDetails.getApplicationContext().applicationContext,
            "BlobUploadUseCase",
            "updateImageBlobStatus : "
        )
        syncBlobRepository.updateBlobStatus(
            imageBlobStatusConfig = imageBlobStatusConfig
        )
    }
    fun isSyncImageBlobUploadEnable(): Boolean {
        return syncBlobRepository.isSyncImageBlobUploadEnable()
    }
}