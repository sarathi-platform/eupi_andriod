package com.nudge.syncmanager.imageupload


import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.StorageException
import com.nudge.core.decodeBase64ToPlainText
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import java.io.IOException
import javax.inject.Inject


class BlobImageUploader @Inject constructor() : ImageUploader {
    val TAG = "BLOB Image Upload"

    override suspend fun uploadImage(
        filePath: String,
        fileName: String,
        containerName: String,
        blobConnectionUrl: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {
        return uploadImageInBlobStorage(
            filePath,
            fileName,
            containerName = containerName,
            blobConnectionUrl = blobConnectionUrl,
            onUploadImageResponse = { message, isException ->
                onUploadImageResponse(message, isException)
            })
    }

    private suspend fun uploadImageInBlobStorage(
        photoPath: String,
        fileName: String,
        containerName: String,
        blobConnectionUrl: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    ) {
        try {


            val account = CloudStorageAccount
                .parse(decodeBase64ToPlainText(blobConnectionUrl))

        val blobClient = account.createCloudBlobClient()

        val container = blobClient.getContainerReference(
            containerName
        )

        val blob = container
            .getBlockBlobReference(fileName)
        blob.uploadFromFile(photoPath)
        CoreLogger.d(
            CoreAppDetails.getApplicationContext().applicationContext,
            TAG,
            "Image uploaded successfully ${blob.storageUri.primaryUri.toString()}"
        )
            onUploadImageResponse(blob.storageUri.primaryUri.toString(), false)
        } catch (se: StorageException) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext().applicationContext,
                TAG,
                "StorageException: ${se.message}",
                ex = se
            )
            se.message?.let { onUploadImageResponse(it, true) }
        } catch (ioe: IOException) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext().applicationContext,
                TAG,
                "IOException: ${ioe.message}",
                ex = ioe
            )
            ioe.message?.let { onUploadImageResponse(it, true) }
        }

    }


}