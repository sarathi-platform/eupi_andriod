package com.nudge.syncmanager.domain.repository

import com.nudge.core.database.dao.EventStatusDao
import com.nudge.core.database.dao.ImageStatusDao
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.preference.CorePrefRepo
import com.nudge.core.utils.CoreLogger
import com.nudge.syncmanager.network.SyncApiService

class SyncBlobRepositoryImpl(
    val apiService: SyncApiService,
    val eventStatusDao: EventStatusDao,
    val corePrefRepo: CorePrefRepo,
    val imageStatusDao: ImageStatusDao
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
        onUploadImageResponse("Upload Success", false)
//        imageUploader.uploadImage(
//            filePath = filePath,
//            fileName = fileName,
//            onUploadImageResponse = { message, isExceptionOccur ->
//
//            })
    }

    override suspend fun updateBlobStatus(
        blobUrl: String,
        isBlobUploaded: Boolean,
        imageStatusId: String,
        errorMessage: String
    ) {
        imageStatusDao.updateBlobUrl(
            blobUrl = blobUrl,
            imageStatusId = imageStatusId,
            isBlobUploaded = isBlobUploaded,
            mobileNumber = corePrefRepo.getMobileNo(),
            errorMessage = errorMessage
        )
    }
}