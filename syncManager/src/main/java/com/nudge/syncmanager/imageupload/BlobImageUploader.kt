package com.nudge.syncmanager.imageupload


import com.microsoft.azure.storage.CloudStorageAccount
import com.nudge.core.model.CoreAppDetails
import com.nudge.core.utils.CoreLogger
import javax.inject.Inject

class BlobImageUploader @Inject constructor() : ImageUploader {

    val containerName = "uat/eupi-documents"

    override suspend fun uploadImage(filePath: String, fileName: String): String {
        return uploadImageInBlobStorage(filePath, fileName)
    }

    private fun uploadImageInBlobStorage(photoPath: String, fileName: String): String {
        val storageConnectionString =
            "DefaultEndpointsProtocol=https;AccountName=nonprodnudgestorage;AccountKey=fa9RdKxroa4DtvAnosimlz4nmeVLSrEEZGq4Kav7YIUT4RunMF692CguZwC2V2dEP67t+xM4EVeM+AStWlCLEA==;EndpointSuffix=core.windows.net"
        val account = CloudStorageAccount
            .parse(storageConnectionString)

        val blobClient = account.createCloudBlobClient()

        val container = blobClient.getContainerReference(
            containerName
        )

        val blob = container
            .getBlockBlobReference(fileName)
        blob.uploadFromFile(photoPath)
        CoreLogger.d(
            CoreAppDetails.getApplicationContext().applicationContext,
            "BLOB Image Upload",
            "Image uploaded successfully ${blob.storageUri.primaryUri.path}"
        )
        return blob.storageUri.primaryUri.path


    }


}