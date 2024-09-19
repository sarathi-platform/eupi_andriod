package com.nudge.syncmanager.imageupload


import com.microsoft.azure.storage.CloudStorageAccount
import javax.inject.Inject

class BlobImageUploader @Inject constructor() : ImageUploader {

    val containerName = "uat/eupi-documents"

    override suspend fun uploadImage(filePath: String, fileName: String): String {
        return uploadImageInBlobStorage(filePath, fileName)
    }

    private fun uploadImageInBlobStorage(photoPath: String, fileName: String): String {
        val storageConnectionString = ""
        val account = CloudStorageAccount
            .parse(storageConnectionString)

        val blobClient = account.createCloudBlobClient()

        val container = blobClient.getContainerReference(
            containerName
        )

        val blob = container
            .getBlockBlobReference(fileName)
        blob.uploadFromFile(photoPath)
//        CoreLogger.d(applicationContext,"Image uploaded successfully ${blob.storageUri.primaryUri.path}")
        return blob.storageUri.primaryUri.path


    }


}