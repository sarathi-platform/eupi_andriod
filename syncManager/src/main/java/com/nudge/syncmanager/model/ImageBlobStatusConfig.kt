package com.nudge.syncmanager.model

data class ImageBlobStatusConfig(
    val blobUrl: String,
    val isBlobUploaded: Boolean,
    val imageStatusId: String,
    val errorMessage: String,
    val status: String,
    val eventId: String,
    val requestId: String
)
