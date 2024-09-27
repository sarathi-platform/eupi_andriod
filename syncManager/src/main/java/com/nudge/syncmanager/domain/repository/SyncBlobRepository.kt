package com.nudge.syncmanager.domain.repository

interface SyncBlobRepository {

    suspend fun uploadImageOnBlob(
        filePath: String,
        fileName: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    )

    suspend fun updateBlobStatus(
        blobUrl: String,
        isBlobUploaded: Boolean,
        imageStatusId: String,
        errorMessage: String,
        status: String,
        eventId: String,
        requestId: String
    )
    fun isSyncImageBlobUploadEnable(): Boolean


}