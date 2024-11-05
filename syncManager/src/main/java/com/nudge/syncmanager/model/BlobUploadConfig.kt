package com.nudge.syncmanager.model

data class BlobUploadConfig(
    val filePath: String,
    val fileName: String,
    val postSelectionContainerName: String,
    val selectionContainerName: String,
    val blobConnectionUrl: String
)
