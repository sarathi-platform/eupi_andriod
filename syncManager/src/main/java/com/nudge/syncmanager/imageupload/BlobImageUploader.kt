package com.nudge.syncmanager.imageupload


import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.StorageException
import com.nudge.core.decodeBase64ToPlainText
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject


class BlobImageUploader @Inject constructor() : ImageUploader {
    val blobImageUploadTag = "BLOB Image Upload"

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
        var exceptionToThrow: Exception? = null
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
            blobImageUploadTag,
            "Image uploaded successfully ${blob.storageUri.primaryUri.toString()}"
        )
            onUploadImageResponse(blob.storageUri.primaryUri.toString(), false)
        } catch (storageException: StorageException) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext().applicationContext,
                blobImageUploadTag,
                "StorageException: ${storageException.message}",
                ex = storageException
            )
            exceptionToThrow = storageException
        } catch (fileNotEx: FileNotFoundException) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext().applicationContext,
                blobImageUploadTag,
                "FileNotFoundException: ${fileNotEx.message}",
                ex = fileNotEx
            )
            exceptionToThrow = fileNotEx
        } catch (ioException: IOException) {
            CoreLogger.e(
                CoreAppDetails.getApplicationContext().applicationContext,
                blobImageUploadTag,
                "IOException: ${ioException.message}",
                ex = ioException
            )
            exceptionToThrow = ioException
        }
        exceptionToThrow?.let { throw it }

    }


}