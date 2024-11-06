package com.nudge.syncmanager.domain.repository

import com.nudge.syncmanager.model.BlobUploadConfig
import com.nudge.syncmanager.model.ImageBlobStatusConfig

interface SyncBlobRepository {

    suspend fun uploadImageOnBlob(
        blobUploadConfig: BlobUploadConfig,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    )

    suspend fun updateBlobStatus(
        imageBlobStatusConfig: ImageBlobStatusConfig
    )
    fun isSyncImageBlobUploadEnable(): Boolean


}