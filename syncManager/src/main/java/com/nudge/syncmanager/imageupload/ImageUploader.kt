package com.nudge.syncmanager.imageupload

interface ImageUploader {
    suspend fun uploadImage(
        filePath: String,
        fileName: String,
        containerName: String,
        onUploadImageResponse: suspend (String, Boolean) -> Unit
    )

}